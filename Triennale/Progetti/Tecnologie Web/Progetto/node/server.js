const PORT = 8000;
const API_KEY = 'AIzaSyBYpObQA5SQHL1cEiVBIn7xJPd-URfQtsA';
const ABS_POPULARITY = __dirname + '/DB/abs-popularity.json';
const REL_POPULARITY = __dirname + '/DB/rel-popularity.json';

const express = require('express')
const bodyParser = require('body-parser')
const request = require('request')
const path = require('path')
const fileSystem = require('fs')
const apiConnection = require('./api-connection')

const app = express()

app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: true }))
app.use('/css', express.static(path.join(__dirname, '..', 'css/')))
app.use('/img', express.static(path.join(__dirname, '..', 'img/')))
app.use('/js', express.static(path.join(__dirname, '..', 'js/')))
app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

app.get('/*', function(req, res, next) {
  if(req.path == '/')
    res.sendFile(path.join(__dirname, '..', 'index.html'))
  else if(req.path == '/404')
    res.sendFile(path.join(__dirname, '..', '404.html'))
  else if(req.path ==  '/globpop')
    getGlobPop(req.query.id, function(output) {
      res.json(output)
    })
  else
    res.redirect('/404');
})

app.post('/selected', function (req, res, next) {
  var videoId = req.body.selectedId, absPop = {}

  if(videoId != undefined && videoId !== "") {
    absPop = JSON.parse(fileSystem.readFileSync(ABS_POPULARITY, 'utf8'))
    if(absPop[videoId] != undefined)
      absPop[videoId].lastSelected = new Date().toUTCString()
    else
      absPop[videoId] = {"views": 0, "lastWatched": '', "lastSelected": new Date().toUTCString()}

    fileSystem.writeFileSync(ABS_POPULARITY, JSON.stringify(absPop))
  }
})

app.post('/watched', function (req, res, next) {
  var newVideoId = req.body.watchedId, oldVideoId = req.body.fromId, absPop = {}, relPop = {}

  if(newVideoId != undefined && newVideoId !== "") {
    // Absolute
    absPop = JSON.parse(fileSystem.readFileSync(ABS_POPULARITY, 'utf8'))
    if(absPop[newVideoId] != undefined) {
      absPop[newVideoId].lastWatched = new Date().toUTCString()
      ++absPop[newVideoId].views
    } else {
      var watchedTime = new Date()
      var selectedTime = new Date()
      selectedTime.setSeconds(watchedTime.getSeconds() - 10)
      absPop[newVideoId] = {"views": 1, "lastWatched": watchedTime.toUTCString(), "lastSelected": selectedTime.toUTCString()}
    }

    fileSystem.writeFileSync(ABS_POPULARITY, JSON.stringify(absPop))

    if(oldVideoId != undefined && oldVideoId !== "" && newVideoId !== oldVideoId) { // Relative
      relPop = JSON.parse(fileSystem.readFileSync(REL_POPULARITY, 'utf8'))
      if(relPop[oldVideoId] != undefined && relPop[oldVideoId].length > 0) {
        var edited = false
        for (var i = 0; i < relPop[oldVideoId].length; i++)
          if(relPop[oldVideoId][i].id === newVideoId) {
            ++relPop[oldVideoId][i].counter
            edited = true
            break
          }
        if(!edited)
          relPop[oldVideoId].push({"id": newVideoId, "counter": 1})
      } else
        relPop[oldVideoId] = [{"id": newVideoId, "counter": 1}]

      fileSystem.writeFileSync(REL_POPULARITY, JSON.stringify(relPop))
    }
  }
})

app.post('/youtube', function (req, res, next) {
   if(req.body.code != undefined && req.body.code == "info") {
      apiConnection.getVideoInfoByVideoId(req.body.videoId, function(results) {
         res.json(results)
      })
   } else if(req.body.code != undefined && req.body.code == "comments") {
      apiConnection.getVideoCommentsByVideoId(req.body.videoId, function(results) {
         res.json(results)
      })
   } else if(req.body.code != undefined && req.body.code == "random") {
      apiConnection.getRandomVideoList(30, function(results) {
         res.json(results)
      })
   } else if(req.body.code != undefined && req.body.code == "starter") {
      apiConnection.getStarterList(function(results) {
         res.json(results)
      })
   } else if(req.body.code != undefined && req.body.code == "search") {
      query = ''
      if(req.body.type == "standard")
         query = req.body.query;
      else if(req.body.type == "tag")
         query = '%23%23%22' + req.body.query + '%22'

      apiConnection.getResultsFromQuery(30, query, function(results) {
         res.json(results)
      })
   } else if(req.body.code != undefined && req.body.code == "related") {
      apiConnection.getRelatedVideoList(30, req.body.videoId, function(results) {
         res.json(results)
      });
   } else if(req.body.code != undefined && req.body.code == "fvitali") {
      apiConnection.getVitaliVideoList(30, req.body.videoId, function(results) {
         res.json(results)
      });
   } else if(req.body.code != undefined && req.body.code == "abs-loc") {
     var absPop = JSON.parse(fileSystem.readFileSync(ABS_POPULARITY, 'utf8'))
     var sorted = Object.keys(absPop).sort(function(a, b) { return absPop[b].views - absPop[a].views }).slice(0, 30)
     apiConnection.getAbsoluteLocalVideoList(sorted, function(results) {
        res.json(results)
     });
   } else if(req.body.code != undefined && req.body.code == "rel-loc" && req.body.videoId != undefined && req.body.videoId !== "") {
     var obj = JSON.parse(fileSystem.readFileSync(REL_POPULARITY, 'utf8'))
     var relPop = obj[req.body.videoId]
     if(relPop != undefined) {
       var sorted = relPop.sort(function(a, b) { return b.counter - a.counter }).slice(0, 30)
       apiConnection.getRelativeLocalVideoList(sorted, function(results) {
          res.json(results)
       });
     } else
       res.json("[]")
   } else if(req.body.code != undefined && req.body.code == "abs-glo" && req.body.videoId != undefined && req.body.videoId !== "") {
     var absPop = JSON.parse(fileSystem.readFileSync(ABS_POPULARITY, 'utf8'))
     apiConnection.getAbsoluteGlobalVideoList(req.body.videoId, absPop, function(results) {
        res.json(results)
     });
   } else if(req.body.code != undefined && req.body.code == "rel-glo" && req.body.videoId != undefined && req.body.videoId !== "") {
     var obj = JSON.parse(fileSystem.readFileSync(REL_POPULARITY, 'utf8'))
     var absPop = JSON.parse(fileSystem.readFileSync(ABS_POPULARITY, 'utf8'))
     var relPop = obj[req.body.videoId], temp = []
     for(i in relPop) // Get how many views got the videos in my DB
       temp.push({
         "id": relPop[i].id,
         "content": absPop[relPop[i].id]
       })
     apiConnection.getRelativeGlobalVideoList(req.body.videoId, temp, function(results) {
        res.json(results)
     });
   } else if(req.body.code != undefined && req.body.code == "sim-art") {
      apiConnection.getArtistSimilarity(30, req.body.wikiName, function(results) {
         res.json(results)
      });
   } else if(req.body.code != undefined && req.body.code == "sim-gen") {
      apiConnection.getGenreSimilarity(30, req.body.wikiName, function(results) {
         res.json(results)
      });
   }
})

app.post('/wikipedia', function (req, res, next) {
   if(req.body.code != undefined && req.body.code == "info") {
      apiConnection.getWikiInfo(req.body.keyphrase, function(results) {
         res.json(results)
      })
   }
})

function getGlobPop(id, callback) {
  var videoId = id, absPop = {}, output = {}

  if(videoId != undefined) {
    absPop = JSON.parse(fileSystem.readFileSync(ABS_POPULARITY, 'utf8'))
    var output = {
       'site': 'site1829.tw.cs.unibo.it',
       'recommender': videoId,
       'lastWatched': absPop[videoId] != undefined && absPop[videoId].lastWatched !== '' ? absPop[videoId].lastWatched : 'Never watched',
       'recommended': []
    }

    // Random
    apiConnection.getRandomVideoList(1, function(results) {
      results = JSON.parse(results)
      output.recommended.push({
         'videoId': results[0] ? results[0].videoId : videoId,
         'timesWatched': results[0] && absPop[results[0].videoId] != undefined ? absPop[results[0].videoId].views : 0,
         'prevalentReason': "Random",
         'lastSelected': results[0] && absPop[results[0].videoId] != undefined && absPop[results[0].videoId].lastSelected !== '' ? absPop[results[0].videoId].lastSelected : 'Never selected'
      })

      // Search
      apiConnection.getResultsFromQuery(1, videoId, function(results) {
        results = JSON.parse(results)
        output.recommended.push({
           'videoId': results[0] ? results[0].videoId : videoId,
           'timesWatched': results[0] && absPop[results[0].videoId] != undefined ? absPop[results[0].videoId].views : 0,
           'prevalentReason': "Search",
           'lastSelected': results[0] && absPop[results[0].videoId] != undefined && absPop[results[0].videoId].lastSelected !== '' ? absPop[results[0].videoId].lastSelected : 'Never selected'
        })

        // Related
        apiConnection.getRelatedVideoList(1, videoId, function(results) {
          results = JSON.parse(results)
          output.recommended.push({
             'videoId': results[0] ? results[0].videoId : videoId,
             'timesWatched': results[0] && absPop[results[0].videoId] != undefined ? absPop[results[0].videoId].views : 0,
             'prevalentReason': "Related",
             'lastSelected': results[0] && absPop[results[0].videoId] != undefined && absPop[results[0].videoId].lastSelected !== '' ? absPop[results[0].videoId].lastSelected : 'Never selected'
          })

          var lastSelected = Object.keys(absPop).sort(function(a, b) {
            var d1 = new Date(absPop[a].lastSelected), d2 = new Date(absPop[b].lastSelected)
            return d2.getTime() - d1.getTime();
          }).slice(0, 1)

          output.recommended.push({
             'videoId': lastSelected[0] ? lastSelected[0] : videoId,
             'timesWatched': lastSelected[0] && absPop[lastSelected[0]] != undefined ? absPop[lastSelected[0]].views : 0,
             'prevalentReason': "Recent",
             'lastSelected': lastSelected[0] && absPop[lastSelected[0]] != undefined && absPop[lastSelected[0]].lastSelected !== '' ? absPop[lastSelected[0]].lastSelected : 'Never selected'
          })

          // Fvitali
          apiConnection.getVitaliVideoList(1, videoId, function(results) {
            results = JSON.parse(results)
            output.recommended.push({
               'videoId': results[0] ? results[0].id : videoId,
               'timesWatched': results[0] && absPop[results[0].id] != undefined ? absPop[results[0].id].views : 0,
               'prevalentReason': "Fvitali",
               'lastSelected': results[0] && absPop[results[0].id] != undefined && absPop[results[0].id].lastSelected !== '' ? absPop[results[0].id].lastSelected : 'Never selected'
            })

            // RelativePopularity
            var obj = JSON.parse(fileSystem.readFileSync(REL_POPULARITY, 'utf8')), relPop = obj[videoId], sorted = []
            if(relPop != undefined)
              sorted = relPop.sort(function(a, b) { return b.counter - a.counter }).slice(0, 1)

            apiConnection.getRelativeLocalVideoList(sorted, function(results) {
              results = JSON.parse(results)
              output.recommended.push({
                 'videoId': results[0] ? results[0].id : videoId,
                 'timesWatched': results[0] && absPop[results[0].id] != undefined ? absPop[results[0].id].views : 0,
                 'prevalentReason': "RelativePopularity",
                 'lastSelected': results[0] && absPop[results[0].id] != undefined && absPop[results[0].id].lastSelected !== '' ? absPop[results[0].id].lastSelected : 'Never selected'
              })

              // AbsolutePopularity
              var sorted = Object.keys(absPop).sort(function(a, b) { return absPop[b].views - absPop[a].views }).slice(0, 1)

              apiConnection.getAbsoluteLocalVideoList(sorted, function(results) {
                results = JSON.parse(results)
                output.recommended.push({
                   'videoId': results[0] ? results[0].id : videoId,
                   'timesWatched': results[0] && absPop[results[0].id] != undefined ? absPop[results[0].id].views : 0,
                   'prevalentReason': "AbsolutePopularity",
                   'lastSelected': results[0] && absPop[results[0].id] != undefined && absPop[results[0].id].lastSelected !== '' ? absPop[results[0].id].lastSelected : 'Never selected'
                })

                apiConnection.getVideoInfoByVideoId(videoId, function(results) {
                  results = JSON.parse(results)
                  apiConnection.getWikiInfo(results.title, function(results) {
                    results = JSON.parse(results)
                    apiConnection.getArtistSimilarity(3, results.title, function(results) {
                      results = JSON.parse(results)
                      var get = 0
                      if(results[0] && results[0].error == 1) get = 1
                      if(results[get] == videoId) get = 2

                      output.recommended.push({
                         'videoId': results[get] ? results[get].videoId : videoId,
                         'timesWatched': results[get] && absPop[results[get].videoId] != undefined ? absPop[results[get].videoId].views : 0,
                         'prevalentReason': "ArtistSimilarity",
                         'lastSelected': results[get] && absPop[results[get].videoId] != undefined && absPop[results[get].videoId].lastSelected !== '' ? absPop[results[get].videoId].lastSelected : 'Never selected'
                      })

                      if(output.recommended.length == 9) callback(output)
                    })
                    apiConnection.getGenreSimilarity(3, results.title, function(results) {
                      results = JSON.parse(results)
                      var get = 0
                      if(results[0] && results[0].error == 1) get = 1
                      if(results[get] == videoId) get = 2

                      output.recommended.push({
                         'videoId': results[get] ? results[get].videoId : videoId,
                         'timesWatched': results[get] && absPop[results[get].videoId] != undefined ? absPop[results[get].videoId].views : 0,
                         'prevalentReason': "GenreSimilarity",
                         'lastSelected': results[get] && absPop[results[get].videoId] != undefined && absPop[results[get].videoId].lastSelected !== '' ? absPop[results[get].videoId].lastSelected : 'Never selected'
                      })

                      if(output.recommended.length == 9) callback(output)
                    })
                  })
                })
              })
            })
          })
        })
      })
    })
  } else {
    output = {'message': 'parameter id not specified.'}
    callback(output)
  }
}

app.listen(PORT, function() {
   console.log('Listening on port ' + PORT + '...')
})
