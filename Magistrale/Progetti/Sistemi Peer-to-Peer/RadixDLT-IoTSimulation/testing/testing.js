// Requirements
const fs = require('fs')
const readlines = require('n-readlines')
const radixdlt = require('radixdlt')

// Initialize the testing Universe
const Universe = radixdlt.radixUniverse
Universe.bootstrap(radixdlt.RadixUniverse.ALPHANET)

const RadixIdentityManager = radixdlt.RadixIdentityManager
const RadixTransactionBuilder = radixdlt.RadixTransactionBuilder

const csvReader = new readlines('data/input/dataset0.csv')
const identityManager = new RadixIdentityManager()

const BUS_IDS = [ '110', '226', '371', '422', '426', '484', '512', '639', '650', '889' ]
const BUS_IDENTITIES = []
const BUS_ACCOUNTS = []
const MASTER_IDENTITY = identityManager.generateSimpleIdentity()
const MASTER_ACCOUNT = MASTER_IDENTITY.account
const APPLICATION_ID = 'radixdlt-iotsimulation'

var dir


// ------------ INIT ------------

async function init() {
  // Connect master to the network
  MASTER_ACCOUNT.openNodeConnection()

  for (var i = 0; i < BUS_IDS.length; i++) {
    // Create new bus account for each bus id
    BUS_IDENTITIES.push(identityManager.generateSimpleIdentity())
    BUS_ACCOUNTS.push(BUS_IDENTITIES[i].account)
    // Connect the bus account to the network
    BUS_ACCOUNTS[i].openNodeConnection()
  }

  // Create stats folder
  dir = 'data/output/' + new Date().toISOString()
  if (!fs.existsSync(dir)) fs.mkdirSync(dir)
}

// ------------ RUN TEST ------------

async function run() {
  try {
    csvReader.next() // get rid of header line
    while (line = csvReader.next()) {
      row = line.toString('ascii').split(',')
      console.log('Waiting ' + row[0] + ' seconds for bus ' + row[1])

      await sleep(parseInt(row[0]) * 1000)
      submitAtom(row[4], row[1], row[2], row[3])
    }

    console.log('SIMULATION COMPLETED! Outputs in: ' + dir)
  } catch (error) {
    console.error(error)
  }
}

async function submitAtom(counter, busId, lat, lon) {
  const busIndex = BUS_IDS.indexOf(row[1])

  const payload = JSON.stringify({
    message: 'Coordinates bus: ' + busId,
    data: {
      latitude: lat,
      longitude: lon,
      timestampISO: new Date().toISOString()
    }
  })

  const timeStats = {
    counter: counter,
    startTime: -1,
    powTime: -1,
    endTime: -1,
    totTime: -1,
    powExecTime: -1,
    latencyTime: -1
  }

  var transactionStatus = null
  try {
    timeStats.startTime = Date.now()
    transactionStatus = RadixTransactionBuilder
                        .createPayloadAtom([BUS_ACCOUNTS[busIndex], MASTER_ACCOUNT], APPLICATION_ID, payload)
                        .signAndSubmit(BUS_IDENTITIES[busIndex])
  } catch(error) {
    console.error('ERROR: Error occured while building transaction')
  }

  const subscription = transactionStatus.subscribe({
    next: status => {
      if(status == 'GENERATING_POW') timeStats.powTime = Date.now()
    },
    complete: () => {
      timeStats.endTime = Date.now()
      timeStats.totTime = timeStats.endTime - timeStats.startTime
      timeStats.powExecTime = timeStats.endTime - timeStats.powTime
      timeStats.latencyTime = timeStats.totTime - timeStats.powExecTime

      subscription.unsubscribe()
      console.log('SUCCESS: Transaction has been stored on the ledger')
      printResults(timeStats, busId)
    },
    error: error => {
      subscription.unsubscribe()
      console.error('ERROR: Error submitting transaction')
      printResults(timeStats, busId)
    }
  })
}

function sleep(ms) {
  return new Promise(res => { setTimeout(res, ms) })
}

function printResults(obj, bus) {
  const stringifyObj =  obj.counter + ', ' +
                        obj.startTime + ', ' +
                        obj.powTime + ', ' +
                        obj.endTime + ', ' +
                        obj.totTime + ', ' +
                        obj.powExecTime + ', ' +
                        obj.latencyTime + '\n'

  fs.appendFileSync(
    dir + '/bus-' + bus + '.csv',
    stringifyObj
  )
}

// ------------ MAIN ------------

async function main() {
  await sleep(1500)
  await init()
  await sleep(1500)
  await run()
  await sleep(3000)
  process.exit()
}

main()
