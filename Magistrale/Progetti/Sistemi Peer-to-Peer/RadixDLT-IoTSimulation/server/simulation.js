const fs = require('fs')
const crypto = require('crypto')
const radixdlt = require('radixdlt')
const readlines = require('n-readlines')
const request = require('request')

const radixUniverse = radixdlt.radixUniverse
const RadixUniverse = radixdlt.RadixUniverse
const RadixIdentityManager = radixdlt.RadixIdentityManager
const RadixTransactionBuilder = radixdlt.RadixTransactionBuilder
const RadixAccount = radixdlt.RadixAccount
const RadixIdentity = radixdlt.RadixIdentity
const RadixAddress = radixdlt.RadixAddress
const identityManager = new RadixIdentityManager()
const csvReader = new readlines('data/dataset.csv')

const APPLICATION_ID = 'methk'
const BUS_IDS = [ '110', '226', '371', '422', '426', '484', '512', '639', '650', '889' ]
// const BUS_IDS = [ '226', '371', '422', '426', '484', '512', '639', '650' ]
const BUS_IDENTITIES = [], BUS_ACCOUNTS = []
var MASTER_ACCOUNT

var busKeys
var clientIdentity

function sleep(ms) {
  return new Promise(res => { setTimeout(res, ms) })
}

async function createBusesIdentities() {
  MASTER_ACCOUNT = RadixAccount.fromAddress('JHvbGGm3hxUaQ733ZVeqYDKhAhSEF3fZJXm3MQNWDea1ie7sVem')
  await MASTER_ACCOUNT.openNodeConnection()

  for (var i = 0; i < BUS_IDS.length; i++) {
    // Create new bus account for each bus id
    BUS_IDENTITIES.push(identityManager.generateSimpleIdentity())
    BUS_ACCOUNTS.push(BUS_IDENTITIES[i].account)
    // Connect the bus account to the network
    BUS_ACCOUNTS[i].openNodeConnection()

    console.log("Bus " + BUS_IDS[i] + " with address: " + BUS_IDENTITIES[i].address.getAddress());
  }

  busKeys = JSON.parse(fs.readFileSync('bus_keys.json', 'utf8'));
}

async function runSimulation() {
  try {
    csvReader.next() // get rid of header line

    while (line = csvReader.next()) {
      row = line.toString('ascii').split(',')
      console.log('Waiting ' + row[0] + ' seconds for bus ' + row[1])

      await sleep(parseInt(row[0]) * 1000)
      updateBusPosition(row[1], row[2], row[3])
    }

    console.log('SIMULATION COMPLETED!')
  } catch (error) {
    console.error(error)
  }
}

async function updateBusPosition(busId, lat, lng) {
  const busIndex = BUS_IDS.indexOf(busId)

  busKey = busKeys[busId]
  const encData = encrypt(JSON.stringify({
    latitude: lat,
    longitude: lng,
    timestampISO: new Date().toISOString()
  }), busKey["key"], busKey["iv"])

  const message = JSON.stringify({
    message: 'Bus ' + busId,
    data: encData
  })

  var transactionStatus = null
  try {
    transactionStatus = RadixTransactionBuilder
                        .createRadixMessageAtom(BUS_ACCOUNTS[busIndex], MASTER_ACCOUNT, message)
                        .signAndSubmit(BUS_IDENTITIES[busIndex])
  } catch(error) {
    console.error('ERROR: Error occured while building transaction')
  }

  const subscription = transactionStatus.subscribe({
    complete: () => {
      subscription.unsubscribe()
      console.log('SUCCESS: new bus position has been stored on the ledger')
    },
    error: error => {
      subscription.unsubscribe()
      console.error('ERROR: an error occured submitting transaction')
      console.log(error);
    }
  })
}

function encrypt(text, key, iv) {
  var cipher = crypto.createCipheriv('aes-256-cbc', Buffer.from(key, 'hex'), Buffer.from(iv, 'hex'));
  var encrypted = cipher.update(text);
  encrypted = Buffer.concat([encrypted, cipher.final()]);

  return encrypted.toString('hex');
}

async function main() {
  radixUniverse.bootstrap(RadixUniverse.LOCALHOST_SINGLENODE)

  await createBusesIdentities()

  await sleep(2000)
  await runSimulation()
}
main()
