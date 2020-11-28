const fs = require('fs')
const request = require('request')
const radixdlt = require('radixdlt')
const express = require('express')
const path = require('path')
const crypto = require('crypto')

const app = express()

const radixUniverse = radixdlt.radixUniverse
const RadixUniverse = radixdlt.RadixUniverse
const RadixIdentityManager = radixdlt.RadixIdentityManager
const RadixRemoteIdentity = radixdlt.RadixRemoteIdentity
const RadixTransactionBuilder = radixdlt.RadixTransactionBuilder
const RadixAccount = radixdlt.RadixAccount
const RadixKeyStore = radixdlt.RadixKeyStore
const RadixTokenManager = radixdlt.RadixTokenManager
const identityManager = new RadixIdentityManager()

const PORT = 8000
const KEYSTORE_PATH = 'keystore_client.json'
const KEYSTORE_PASSWORD = 'radix123'
const APPLICATION_ID = 'methk'

var radixToken
var clientIdentity
var clientBalance
var clientCompleteBalance
var purchasedKeys = []

main()
async function main() {
  radixUniverse.bootstrap(RadixUniverse.LOCALHOST_SINGLENODE)
  radixToken = radixUniverse.nativeToken

  await createServerIdentity()
}

async function createServerIdentity() {
  if (fs.existsSync(KEYSTORE_PATH)) {
    // Load account
    const contents = JSON.parse(fs.readFileSync(KEYSTORE_PATH, 'utf8'));
    const address = await RadixKeyStore.decryptKey(contents, KEYSTORE_PASSWORD)

    const identity = identityManager.addSimpleIdentity(address)
    await identity.account.openNodeConnection()

    clientIdentity = identity
    console.log('Loaded identity')
  } else {
    // Create new account
    const identity = identityManager.generateSimpleIdentity()
    await identity.account.openNodeConnection()
    const contents = await RadixKeyStore.encryptKey(identity.address, KEYSTORE_PASSWORD)
    await fs.writeFile(KEYSTORE_PATH, JSON.stringify(contents), 'utf8', () => {})

    clientIdentity = identity
    console.log('Generated new identity')
  }

  console.log("Address: " + clientIdentity.address.getAddress())

  // Get money from faucet
  const faucetAccount = RadixAccount.fromAddress('JH1P8f3znbyrDj8F4RWpix7hRkgxqHjdW2fNnKpR3v6ufXnknor')
  const message = 'Send me 10 RDX please'
  RadixTransactionBuilder.createRadixMessageAtom(clientIdentity.account, faucetAccount, message).signAndSubmit(clientIdentity)

  clientIdentity.account.transferSystem.getTokenUnitsBalanceUpdates().subscribe(balance => {
    clientCompleteBalance = balance
    clientBalance = parseInt(balance[radixToken.toString()])
    console.log(balance)
  })
}

function decrypt(text, key, iv) {
  let encryptedText = Buffer.from(text, 'hex');
  let decipher = crypto.createDecipheriv('aes-256-cbc', Buffer.from(key, 'hex'), Buffer.from(iv, 'hex'));
  let decrypted = decipher.update(encryptedText);
  decrypted = Buffer.concat([decrypted, decipher.final()]);

  return decrypted.toString();
}



app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname + '/GUI/index.html'));
})

app.get('/balance', (req, res) => {
  res.json({
    success: 1,
    data: clientCompleteBalance
  })
})

app.get('/buses', (req, res) => {
  request.get({
      headers: { 'content-type': 'application/json' },
      url: 'http://localhost:3001/buses'
  },  function (error, response, body) {
        res.json(JSON.parse(body))
  })
})

app.get('/bus', (req, res) => {
  request.get({
      headers: { 'content-type': 'application/json' },
      url: 'http://localhost:3001/request-access'
  },  function (error, response, body) {
        if (!error && response.statusCode == 200) {
          const challenge = response.body
          // Construct and sign the atom
          const data = {challenge}
          const atom = RadixTransactionBuilder.createPayloadAtom(
                        clientIdentity.account,
                        [clientIdentity.account],
                        APPLICATION_ID,
                        JSON.stringify(data),
                        false)
                      .buildAtom()
          clientIdentity.signAtom(atom).then((signedAtom) => {
            request.post({
                headers: { 'content-type': 'application/json' },
                url: 'http://localhost:3001/bus',
                body: JSON.stringify({
                        busTokenUri: req.query.id,
                        atom: atom.toJSON()
                      })
            },  function (error, response, body) {
                  if (!error && response.statusCode == 200 && JSON.parse(response.body).success == 1) {
                    var resJson = JSON.parse(response.body).data
                    var busId = resJson.name.split(" ")[1]

                    var flag = true
                    for (var i = 0; i < purchasedKeys.length; i++) {
                      if(purchasedKeys[i].busId == busId)
                        flag = false
                    }
                    if(flag)
                      purchasedKeys.push({
                        busId: busId,
                        data: JSON.parse(resJson.busSecret)
                      })

                    res.json(JSON.parse(response.body))
                  } else
                    res.json(JSON.parse(response.body))
            })
          })
        } else
          res.json({
            success: 0,
            data: 'cannot contact the server.'
          })
  })
})

app.get('/subscribe', (req, res) => {
  const serverAccount = RadixAccount.fromAddress('JHvbGGm3hxUaQ733ZVeqYDKhAhSEF3fZJXm3MQNWDea1ie7sVem', true)

  if(clientBalance >= 2) {
    const transactionStatus = RadixTransactionBuilder
      .createTransferAtom(clientIdentity.account, serverAccount, radixToken, 2, req.query.id)
      .signAndSubmit(clientIdentity)

    transactionStatus.subscribe({
      complete: () => { return res.json({
        success: 1,
        data: 'successfully paid 2 RDX for bus line subscription.'
      }) },
      error: error => { return res.json({
        success: 0,
        data: 'error submitting transaction.'
      }) }
    })
  } else
    res.json({
      success: 0,
      data: 'insufficient funds.'
    })
})

app.get('/position', (req, res) => {
  request.get({
      headers: { 'content-type': 'application/json' },
      url: 'http://localhost:3001/position?id=' + req.query.id
  },  function (error, response, body) {
        if (!error && response.statusCode == 200 && JSON.parse(response.body).success == 1) {
          var obj = JSON.parse(response.body).data
          var busId = obj.message.split(" ")[1]

          for (var i = 0; i < purchasedKeys.length; i++) {
            if(purchasedKeys[i].busId == busId) {
              return res.json({
                success: 1,
                data: JSON.parse(decrypt(obj.data, purchasedKeys[i].data.key, purchasedKeys[i].data.iv))
              })
            }
          }

          res.json({
            success: 0,
            data: 'key not purchased.'
          })
        } else
          res.json({
            success: 0,
            data: 'error getting bus position.'
          })
  })
})

app.listen(PORT, () => console.log(`Client app listening on port ${PORT}!`))
