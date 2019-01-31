const API_KEY = 'AIzaSyBYpObQA5SQHL1cEiVBIn7xJPd-URfQtsA'
const request = require('request')
var randomPlaylists = ["PL53244BC75ACF40D0", "PLA1D6023F6FF2684B", "PLI_TwOrHUsI8MQNW0BvBAwwHYKgyiiiDB", "PLPZgiga_0D3HWboabRTgRn5032sbL395g", "PLMC9KNkIncKvYin_USF1qoJQnIyMAfRxl", "PLed0DOpaqI_aU4TyXZDuvpFAa1VZosCT7"]
var projectsAPIs = ["http://site1825.tw.cs.unibo.it/TW/globpop"]
var totLocalVideos = 0 // The number of element in abs-popularity.json
var reqNum = 0; // Count the requests to the projects APIs
var startingListArray = [], randomArray = []
for (var i = 0; i < 120; i++)
  startingListArray.push(i)
for (var i = 0; i < 30; i++)
  randomArray.push(i)

function shuffleArray(array) { // Simple Fisher–Yates shuffle
   var i = array.length, j = 0, temp;
   while(i--) {
      j = Math.floor(Math.random() * (i+1));
      temp = array[i]; array[i] = array[j]; array[j] = temp;
   }
   return array;
}

function getVideoInfoByVideoId(videoId, callback) {
   var results = {}
   request('https://www.googleapis.com/youtube/v3/videos?id=' + videoId + '&key=' + API_KEY + '&part=snippet,contentDetails,statistics,topicDetails', function (error, response, body) {
      try {
         body = JSON.parse(body)
         if (!error && response.statusCode == 200 && body.items[0] != undefined) {
            results = {
               'thumbnail': body.items[0].snippet.thumbnails.high.url,
               'publishedAt': body.items[0].snippet.publishedAt,
               'title': body.items[0].snippet.title,
               'id': body.items[0].id,
               'channelTitle': body.items[0].snippet.channelTitle,
               'channelId': body.items[0].snippet.channelId,
               'description': body.items[0].snippet.description,
               'tags': body.items[0].snippet.tags,
               'duration': body.items[0].contentDetails.duration,
               'definition': body.items[0].contentDetails.definition,
               'dimension': body.items[0].contentDetails.dimension,
               'viewCount': body.items[0].statistics.viewCount,
               'likeCount': body.items[0].statistics.likeCount,
               'dislikeCount': body.items[0].statistics.dislikeCount
            }
         }
         callback(JSON.stringify(results))
      } catch (err) {
          console.log(err)
      }
   })
};

function getVideoCommentsByVideoId(videoId, callback) {
   var results = []
   request('https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&maxResults=10&order=relevance&videoId=' + videoId + '&key=' + API_KEY, function (error, response, body) {
      try {
         body = JSON.parse(body)
         if (!error && response.statusCode == 200) {
           for(var i = 0; i < body.items.length; i++) {
              results.push({
                 'updatedAt': body.items[i].snippet.topLevelComment.snippet.updatedAt,
                 'authorProfileImageUrl': body.items[i].snippet.topLevelComment.snippet.authorProfileImageUrl,
                 'authorDisplayName': body.items[i].snippet.topLevelComment.snippet.authorDisplayName,
                 'textDisplay': body.items[i].snippet.topLevelComment.snippet.textDisplay
              })
           }
         }
         callback(JSON.stringify(results))
      } catch (err) {
          console.log(err)
      }
   })
};

function getStarterList(callback) {
   var results = []
   request('http://site1825.tw.cs.unibo.it/video.json', function (error, response, body) {
      body = JSON.parse(body)
      if (!error && response.statusCode == 200) {
         startingListArray = shuffleArray(startingListArray);
         // Qui non viene utilizzata la ricorsione in quanto non è importante che l'ordine in cui arrivano i risultati sia lo stesso di quello previsto
         // è più imprtante la velocità di restituzione del risultato essendo la lista di partenza
         for(var i = 0; i < 30; i++) {
            getVideoInfoByVideoId(body[startingListArray[i]].videoID, function(res) {
               results.push(JSON.parse(res))
               if(results.length == 30) callback(JSON.stringify(results))
            })
         }
      }
   })
};

function getRandomVideoList(maxResults, callback) {
  randomArray = shuffleArray(randomArray);
  var results = [], randomPlaylistId = randomPlaylists[Math.floor(Math.random() * ((randomPlaylists.length) + 1))]
  request('https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2C+id&maxResults=30&playlistId=' + randomPlaylistId + '&key=' + API_KEY, function (error, response, body) {
    try {
      body = JSON.parse(body)
      if (!error && response.statusCode == 200) {
        var totTokens = parseInt(body.pageInfo.totalResults / 30)
        var randomToken = Math.floor(Math.random() * ((totTokens) + 1))
        var currentPage = 0, addition = ""

        recursiveBrowsing();

        function recursiveBrowsing(token = "") {
          if(token != undefined && token != "")
            addition = '&pageToken=' + token;
          request('https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2C+id&maxResults=30&playlistId=' + randomPlaylistId + '&key=' + API_KEY + addition, function (error, response, body) {
            try {
              body = JSON.parse(body)
              if (!error && response.statusCode == 200) {
                if (body.nextPageToken && currentPage < parseInt(token)){
                  currentPage++;
                  recursiveBrowsing(body.nextPageToken)
                } else {
                  for(var i = 0; i < (body.items.length < maxResults ? body.items.length : maxResults); i++)
                    results.push({
                       'thumbnail': body.items[randomArray[i]].snippet.thumbnails.high.url,
                       'title': body.items[randomArray[i]].snippet.title,
                       'videoId': body.items[randomArray[i]].snippet.resourceId.videoId,
                       'channelTitle': body.items[randomArray[i]].snippet.channelTitle,
                       'position': body.items[randomArray[i]].snippet.position,
                       'publishedAt': body.items[randomArray[i]].snippet.publishedAt,
                       'description': body.items[randomArray[i]].snippet.description
                    })
                  callback(JSON.stringify(results))
                }
              }
            } catch (err) {
              console.log(err);
            }
          })
        }
      }
    } catch (err) {
      console.log(err)
    }
  })
};

function getResultsFromQuery(maxResults, query, callback) {
   var results = []
   request('https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=' + maxResults +'&q=' + query + '&key=' + API_KEY + '&type=video&videoEmbeddable=true&videoSyndicated=true', function (error, response, body) {
      try {
         body = JSON.parse(body)
         if (!error && response.statusCode == 200) {
            for(var i = 0; i < body.items.length; i++) {
               results.push({
                  'thumbnail': body.items[i].snippet.thumbnails.high.url,
                  'title': body.items[i].snippet.title,
                  'videoId': body.items[i].id.videoId,
                  'channelTitle': body.items[i].snippet.channelTitle,
                  'publishedAt': body.items[i].snippet.publishedAt,
                  'description': body.items[i].snippet.description
               })
            }
         }
         callback(JSON.stringify(results))
      } catch (err) {
          console.log(err)
      }
   })
};

function getRelatedVideoList(maxResults, videoId, callback) {
   var results = []
   request('https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=' + maxResults + '&relatedToVideoId=' + videoId + '&key=' + API_KEY + '&type=video&videoEmbeddable=true&videoSyndicated=true', function (error, response, body) {
      body = JSON.parse(body)
      if (!error && response.statusCode == 200) {
         for(var i = 0; i < body.items.length; i++) {
            results.push({
               'thumbnail': body.items[i].snippet.thumbnails.high.url,
               'title': body.items[i].snippet.title,
               'videoId': body.items[i].id.videoId,
               'channelTitle': body.items[i].snippet.channelTitle,
               'position': body.items[i].snippet.position,
               'publishedAt': body.items[i].snippet.publishedAt,
               'description': body.items[i].snippet.description
            })
         }
      }
      callback(JSON.stringify(results))
   })
};

function getVitaliVideoList(maxResults, videoId, callback){
   var results = []
   request('http://site1825.tw.cs.unibo.it/TW/globpop?id=' + videoId, function (error, response, body){
      body = JSON.parse(body)
      if (!error && response.statusCode == 200) {
        for(var i = 0; i < (body.recommended.length < maxResults ? body.recommended.length : maxResults); i++) {
          getVideoInfoByVideoId(body.recommended[i].videoID, function(res) {
            results.push(JSON.parse(res))
            if(results.length == (body.recommended.length < maxResults ? body.recommended.length : maxResults))
              callback(JSON.stringify(results))
          })
        }
      }
   })
};

function getAbsoluteLocalVideoList(sortedList, callback) {
   function recursiveRequest(index = 0, resultsArray = []) {
     getVideoInfoByVideoId(sortedList[index], function(res) {
       resultsArray.push(JSON.parse(res))
       if(resultsArray.length < sortedList.length)
         recursiveRequest(++index, resultsArray)
       else
         callback(JSON.stringify(resultsArray))
     })
   }
   recursiveRequest()
};

function getRelativeLocalVideoList(sortedList, callback) {
   function recursiveRequest(index = 0, resultsArray = []) {
     getVideoInfoByVideoId(sortedList[index].id, function(res) {
       resultsArray.push(JSON.parse(res))
       if(resultsArray.length < sortedList.length)
         recursiveRequest(++index, resultsArray)
       else
         callback(JSON.stringify(resultsArray))
     })
   }
   if(sortedList.length > 0) recursiveRequest()
   else callback("[]");
};

function getAbsoluteGlobalVideoList(videoId, localVideoList, callback) {
   var allVideos = []

   for(var key in localVideoList) // get the videoId and the number of views of all videos watched by my users
     allVideos.push({
       videoId: key,
       views: localVideoList[key].views
     })
   totLocalVideos = allVideos.length

   for(var i = 0; i < projectsAPIs.length; i++) {
     request(projectsAPIs[i] + '?id=' + videoId, function (error, response, body) {
        body = JSON.parse(body)
        if (!error && response.statusCode == 200) {
          for(var i = 0; i < body.recommended.length; i++) {
            allVideos.push({
              videoId: body.recommended[i].videoID,
              views: body.recommended[i].timesWatched
            })
          }

          if(allVideos.length == (totLocalVideos + 10 * projectsAPIs.length)) {
            // Order array by views and get first 30 videos
            allVideos = allVideos.sort(function(a, b) { return b.views - a.views }).slice(0, 30)
            // Remove duplicates
            var temp = []
            for(var i = 0, len = allVideos.length; i < len; i++)
              if(allVideos[i]) temp[allVideos[i]['videoId']] = allVideos[i];
            allVideos = new Array();
            for(var key in temp)
              allVideos.push(temp[key]);

            function recursiveRequest(index = 0, resultsArray = []) { // I need to preserve the order
              getVideoInfoByVideoId(allVideos[index].videoId, function(res) {
                resultsArray.push(JSON.parse(res))
                if(resultsArray.length < allVideos.length)
                  recursiveRequest(++index, resultsArray)
                else
                  callback(JSON.stringify(resultsArray))
              })
            }
            recursiveRequest()
          }
        }
     })
   }
};

function getRelativeGlobalVideoList(videoId, localVideoList, callback) {
   var allVideos = []

   for(var i in localVideoList) // get the videoId and the number of views of all videos watched by my users
     if(localVideoList[i].id && localVideoList[i].content)
       allVideos.push({
         videoId: localVideoList[i].id,
         views: localVideoList[i].content.views
       })

   for(var i = 0; i < projectsAPIs.length; i++) {
     request(projectsAPIs[i] + '?id=' + videoId, function (error, response, body) {
        reqNum++
        body = JSON.parse(body)
        if (!error && response.statusCode == 200) {
          for(var i = 0; i < body.recommended.length; i++)
            if(body.recommended[i].prevalentReason == "RelativePopularity" && body.recommended[i].videoId !== videoId)
              allVideos.push({
                videoId: body.recommended[i].videoId,
                views: body.recommended[i].timesWatched
              })

          if(projectsAPIs.length == reqNum) {
            reqNum = 0
            if(allVideos.length > 0) {
              // Order array by views and get first 30 videos
              allVideos = allVideos.sort(function(a, b) { return b.views - a.views }).slice(0, 30)
              console.log(allVideos);
              // Remove duplicates
              var temp = []
              for(var i = 0, len = allVideos.length; i < len; i++)
                if(allVideos[i]) temp[allVideos[i]['videoId']] = allVideos[i];
              allVideos = new Array();
              for(var key in temp)
                allVideos.push(temp[key]);

              function recursiveRequest(index = 0, resultsArray = []) { // I need to preserve the order
                getVideoInfoByVideoId(allVideos[index].videoId, function(res) {
                  resultsArray.push(JSON.parse(res))
                  if(resultsArray.length < allVideos.length)
                    recursiveRequest(++index, resultsArray)
                  else
                    callback(JSON.stringify(resultsArray))
                })
              }
              recursiveRequest()
            } else
              callback(JSON.stringify([]))
          }
        }
     })
   }
};

function getArtistSimilarity(maxResults, wikiName, callback){
   var results = [], artist = '';

   // Steps: check if the video title is a song, if true get artist name, if false i assume it is an artist or a band. Then try to get the songs of the artist, if doesn't work try with the band, if doesn't work perform a search query on YT.

   var songQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\
   SELECT DISTINCT * {\
      {\
        SELECT ?name WHERE {\
          ?song a dbo:Song .\
          ?song rdfs:label \"" + wikiName + "\"@en .\
          ?song <http://dbpedia.org/ontology/artist> ?artist .\
          ?artist rdfs:label ?name .\
          FILTER (lang(?name) = 'en')\
        } LIMIT 1\
      } UNION {\
        SELECT ?name WHERE {\
          ?song a dbo:Single .\
          ?song rdfs:label \"" + wikiName + "\"@en .\
          ?song <http://dbpedia.org/ontology/artist> ?artist .\
          ?artist rdfs:label ?name .\
          FILTER (lang(?name) = 'en')\
        } LIMIT 1\
      } UNION {\
        SELECT ?name WHERE {\
          ?song a dbo:Song .\
          ?song rdfs:label \"" + wikiName + "\"@en .\
          ?song <http://dbpedia.org/ontology/musicalArtist> ?artist .\
          ?artist rdfs:label ?name .\
          FILTER (lang(?name) = 'en')\
        } LIMIT 1\
      } UNION {\
        SELECT ?name WHERE {\
          ?song a dbo:Single .\
          ?song rdfs:label \"" + wikiName + "\"@en .\
          ?song <http://dbpedia.org/ontology/musicalArtist> ?artist .\
          ?artist rdfs:label ?name .\
          FILTER (lang(?name) = 'en')\
        } LIMIT 1\
      } UNION {\
        SELECT ?name WHERE {\
          ?song a dbo:Album .\
          ?song rdfs:label \"" + wikiName + "\"@en .\
          ?song <http://dbpedia.org/ontology/artist> ?artist .\
          ?artist rdfs:label ?name .\
          FILTER (lang(?name) = 'en')\
        } LIMIT 1\
      } UNION {\
        SELECT ?name WHERE {\
          ?song a dbo:Album .\
          ?song rdfs:label \"" + wikiName + "\"@en .\
          ?song <http://dbpedia.org/ontology/musicalArtist> ?artist .\
          ?artist rdfs:label ?name .\
          FILTER (lang(?name) = 'en')\
        } LIMIT 1\
      }\
    }";

   request('http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=' + encodeURIComponent(songQuery) + '&output=json', function (error, response, body){
      body = JSON.parse(body)
      if (!error && response.statusCode == 200) {
        if(body.results.bindings.length > 0) // if the title was a song search info about the artist
          artist = body.results.bindings[0].name.value
        else
          artist = wikiName

        var query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\
        SELECT DISTINCT * {\
          {\
            SELECT ?title WHERE {\
              ?band a dbo:MusicalArtist .\
              ?band rdfs:label \"" + artist + "\"@en .\
              ?song <http://dbpedia.org/ontology/artist> ?band .\
              ?song a <http://dbpedia.org/ontology/Song> .\
              ?song rdfs:label ?title .\
              FILTER (lang(?title) = 'en')\
            } LIMIT 30\
          } UNION {\
            SELECT ?title WHERE {\
               ?band a dbo:Band .\
               ?band rdfs:label \"" + artist + "\"@en .\
               ?song <http://dbpedia.org/ontology/artist> ?band .\
               ?song a <http://dbpedia.org/ontology/Song> .\
               ?song rdfs:label ?title .\
               FILTER (lang(?title) = 'en')\
            } LIMIT 30\
          } UNION {\
            SELECT ?title WHERE {\
               ?band a dbo:MusicalArtist .\
               ?band rdfs:label \"" + artist + "\"@en .\
               ?song <http://dbpedia.org/ontology/artist> ?band .\
               ?song a <http://dbpedia.org/ontology/Single> .\
               ?song rdfs:label ?title .\
               FILTER (lang(?title) = 'en')\
            } LIMIT 30\
          } UNION {\
            SELECT ?title WHERE {\
               ?band a dbo:Band .\
               ?band rdfs:label \"" + artist + "\"@en .\
               ?song <http://dbpedia.org/ontology/artist> ?band .\
               ?song a <http://dbpedia.org/ontology/Single> .\
               ?song rdfs:label ?title .\
               FILTER (lang(?title) = 'en')\
            } LIMIT 30\
          } UNION {\
            SELECT ?title WHERE {\
               ?band a dbo:MusicalArtist .\
               ?band rdfs:label \"" + artist + "\"@en .\
               ?song <http://dbpedia.org/ontology/musicalArtist> ?band .\
               ?song a <http://dbpedia.org/ontology/Single> .\
               ?song rdfs:label ?title .\
               FILTER (lang(?title) = 'en')\
            } LIMIT 30\
          } UNION {\
            SELECT ?title WHERE {\
               ?band a dbo:Band .\
               ?band rdfs:label \"" + artist + "\"@en .\
               ?song <http://dbpedia.org/ontology/musicalArtist> ?band .\
               ?song a <http://dbpedia.org/ontology/Single> .\
               ?song rdfs:label ?title .\
               FILTER (lang(?title) = 'en')\
            } LIMIT 30\
          } UNION {\
            SELECT ?title WHERE {\
               ?band a dbo:MusicalArtist .\
               ?band rdfs:label \"" + artist + "\"@en .\
               ?album <http://dbpedia.org/ontology/artist> ?band .\
               ?album a <http://dbpedia.org/ontology/Album> .\
               ?album <http://dbpedia.org/property/title> ?title .\
               FILTER (isLiteral(?title))\
            } LIMIT 30\
          }\
        }"

        request('http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=' + encodeURIComponent(query) + '&output=json', function (error, response, body){
           body = JSON.parse(body)
           if (!error && response.statusCode == 200) {
             if(body.results.bindings.length > 0) {
               for(var i = 0; i < (body.results.bindings.length < maxResults ? body.results.bindings.length : maxResults); i++) {
                 getResultsFromQuery(1, body.results.bindings[i].title.value + " " + artist, function(res) {
                   results.push(JSON.parse(res)[0])
                   if(results.length == (body.results.bindings.length < maxResults ? body.results.bindings.length : maxResults))
                     callback(JSON.stringify(results))
                 })
               }
             } else {
               getResultsFromQuery(maxResults, artist, function(res) {
                 results.push({'error': 1})
                 res = JSON.parse(res)
                 for (var i = 0; i < res.length; i++)
                   results.push(res[i])
                 callback(JSON.stringify(results))
               })
             }
           }
        })
      }
   })
};

function getGenreSimilarity(maxResults, wikiName, callback){
   var results = [], artist = '';

   var songQuery = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\
   SELECT DISTINCT * {\
      {\
        SELECT ?name WHERE {\
          ?song a dbo:Song .\
          ?song rdfs:label \"" + wikiName + "\"@en .\
          ?song <http://dbpedia.org/ontology/artist> ?artist .\
          ?artist rdfs:label ?name .\
          FILTER (lang(?name) = 'en')\
        } LIMIT 1\
      } UNION {\
        SELECT ?name WHERE {\
          ?song a dbo:Single .\
          ?song rdfs:label \"" + wikiName + "\"@en .\
          ?song <http://dbpedia.org/ontology/artist> ?artist .\
          ?artist rdfs:label ?name .\
          FILTER (lang(?name) = 'en')\
        } LIMIT 1\
      } UNION {\
        SELECT ?name WHERE {\
          ?song a dbo:Song .\
          ?song rdfs:label \"" + wikiName + "\"@en .\
          ?song <http://dbpedia.org/ontology/musicalArtist> ?artist .\
          ?artist rdfs:label ?name .\
          FILTER (lang(?name) = 'en')\
        } LIMIT 1\
      } UNION {\
        SELECT ?name WHERE {\
          ?song a dbo:Single .\
          ?song rdfs:label \"" + wikiName + "\"@en .\
          ?song <http://dbpedia.org/ontology/musicalArtist> ?artist .\
          ?artist rdfs:label ?name .\
          FILTER (lang(?name) = 'en')\
        } LIMIT 1\
      } UNION {\
        SELECT ?name WHERE {\
          ?song a dbo:Album .\
          ?song rdfs:label \"" + wikiName + "\"@en .\
          ?song <http://dbpedia.org/ontology/artist> ?artist .\
          ?artist rdfs:label ?name .\
          FILTER (lang(?name) = 'en')\
        } LIMIT 1\
      } UNION {\
        SELECT ?name WHERE {\
          ?song a dbo:Album .\
          ?song rdfs:label \"" + wikiName + "\"@en .\
          ?song <http://dbpedia.org/ontology/musicalArtist> ?artist .\
          ?artist rdfs:label ?name .\
          FILTER (lang(?name) = 'en')\
        } LIMIT 1\
      }\
    }";

   request('http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=' + encodeURIComponent(songQuery) + '&output=json', function (error, response, body){
      body = JSON.parse(body)
      if (!error && response.statusCode == 200) {
        if(body.results.bindings.length > 0) // if the title was a song search info about the artist
          artist = body.results.bindings[0].name.value
        else
          artist = wikiName

        var query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\
        SELECT DISTINCT * {\
          {\
            SELECT ?bandTitle ?songTitle WHERE {\
              ?band a dbo:Band .\
              ?band rdfs:label \"" + artist + "\"@en .\
              ?relatedBand <http://dbpedia.org/ontology/associatedBand> ?band .\
              ?song <http://dbpedia.org/ontology/artist> ?relatedBand .\
              ?song a <http://dbpedia.org/ontology/Song> .\
              ?song rdfs:label ?songTitle .\
              ?relatedBand rdfs:label ?bandTitle .\
              FILTER (lang(?songTitle) = 'en' && lang(?bandTitle) = 'en')\
            } LIMIT 30 \
          } UNION {\
            SELECT ?bandTitle ?songTitle WHERE {\
              ?band a dbo:MusicalArtist .\
              ?band rdfs:label \"" + artist + "\"@en .\
              ?relatedBand <http://dbpedia.org/ontology/associatedBand> ?band .\
              ?song <http://dbpedia.org/ontology/artist> ?relatedBand .\
              ?song a <http://dbpedia.org/ontology/Song> .\
              ?song rdfs:label ?songTitle .\
              ?relatedBand rdfs:label ?bandTitle .\
              FILTER (lang(?songTitle) = 'en' && lang(?bandTitle) = 'en')\
            } LIMIT 30\
          } UNION {\
            SELECT ?bandTitle ?songTitle WHERE {\
              ?band a dbo:Band .\
              ?band rdfs:label \"" + artist + "\"@en .\
              ?relatedBand <http://dbpedia.org/ontology/associatedBand> ?band .\
              ?song <http://dbpedia.org/ontology/artist> ?relatedBand .\
              ?song a <http://dbpedia.org/ontology/Single> .\
              ?song rdfs:label ?songTitle .\
              ?relatedBand rdfs:label ?bandTitle .\
              FILTER (lang(?songTitle) = 'en' && lang(?bandTitle) = 'en')\
            } LIMIT 30\
          } UNION {\
            SELECT ?bandTitle ?songTitle WHERE {\
              ?band a dbo:MusicalArtist .\
              ?band rdfs:label \"" + artist + "\"@en .\
              ?relatedBand <http://dbpedia.org/ontology/associatedBand> ?band .\
              ?song <http://dbpedia.org/ontology/artist> ?relatedBand .\
              ?song a <http://dbpedia.org/ontology/Single> .\
              ?song rdfs:label ?songTitle .\
              ?relatedBand rdfs:label ?bandTitle .\
              FILTER (lang(?songTitle) = 'en' && lang(?bandTitle) = 'en')\
            } LIMIT 30\
          } UNION {\
            SELECT ?bandTitle ?songTitle WHERE {\
              ?band a dbo:Band .\
              ?band rdfs:label \"" + artist + "\"@en .\
              ?relatedBand <http://dbpedia.org/ontology/associatedBand> ?band .\
              ?album <http://dbpedia.org/ontology/artist> ?relatedBand .\
              ?album a <http://dbpedia.org/ontology/Album> .\
              ?album <http://dbpedia.org/property/title> ?songTitle .\
              ?relatedBand rdfs:label ?bandTitle .\
              FILTER (isLiteral(?songTitle) && lang(?bandTitle) = 'en')\
            } LIMIT 30\
          } UNION {\
            SELECT ?bandTitle ?songTitle WHERE {\
              ?band a dbo:MusicalArtist .\
              ?band rdfs:label \"" + artist + "\"@en .\
              ?relatedBand <http://dbpedia.org/ontology/associatedBand> ?band .\
              ?album <http://dbpedia.org/ontology/artist> ?relatedBand .\
              ?album a <http://dbpedia.org/ontology/Album> .\
              ?album <http://dbpedia.org/property/title> ?songTitle .\
              ?relatedBand rdfs:label ?bandTitle .\
              FILTER (isLiteral(?songTitle) && lang(?bandTitle) = 'en')\
            } LIMIT 30\
          }\
        }"

        request('http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=' + encodeURIComponent(query) + '&output=json', function (error, response, body){
           body = JSON.parse(body)
           if (!error && response.statusCode == 200) {
             if(body.results.bindings.length > 0) {
               for(var i = 0; i < (body.results.bindings.length < maxResults ? body.results.bindings.length : maxResults); i++) {
                 getResultsFromQuery(1, body.results.bindings[i].songTitle.value + " " + body.results.bindings[i].bandTitle.value, function(res) {
                   results.push(JSON.parse(res)[0])
                   if(results.length == (body.results.bindings.length < maxResults ? body.results.bindings.length : maxResults))
                     callback(JSON.stringify(results))
                 })
               }
             } else {
               getResultsFromQuery(maxResults, artist, function(res) {
                 results.push({'error': 1})
                 res = JSON.parse(res)
                 for (var i = 0; i < res.length; i++)
                   results.push(res[i])
                 callback(JSON.stringify(results))
               })
             }
           }
        })
      }
   })
};

function getWikiInfo(keyphrase, callback) {
   var results = {}
   request('https://en.wikipedia.org/w/api.php?action=query&list=search&format=json&origin=*&srsearch=' + keyphrase, function (error, response, body) {
      if(body != undefined) {
        try {
           body = JSON.parse(body)
           if (!error && response.statusCode == 200 && body.query.search[0] != undefined) {
              request('https://en.wikipedia.org/w/api.php?action=parse&format=json&section=0&page=' + body.query.search[0].title + '&prop=text&origin=*', function (error, response, body) {
                 if (!error && response.statusCode == 200) {
                    body = JSON.parse(body)
                    results = {
                      'title': body.parse.title,
                      'text': body.parse.text['*']
                    }
                 } else
                   results = { 'error': 1 }
                 callback(JSON.stringify(results))
              })
           } else
             callback(JSON.stringify({ 'error': 1 }))
        } catch (err) {
            console.log(err)
        }
      }
   })
};

module.exports = {
    getVideoInfoByVideoId: getVideoInfoByVideoId,
    getVideoCommentsByVideoId: getVideoCommentsByVideoId,
    getStarterList: getStarterList,
    getRandomVideoList: getRandomVideoList,
    getResultsFromQuery: getResultsFromQuery,
    getRelatedVideoList: getRelatedVideoList,
    getVitaliVideoList: getVitaliVideoList,
    getAbsoluteLocalVideoList: getAbsoluteLocalVideoList,
    getRelativeLocalVideoList: getRelativeLocalVideoList,
    getAbsoluteGlobalVideoList: getAbsoluteGlobalVideoList,
    getRelativeGlobalVideoList: getRelativeGlobalVideoList,
    getArtistSimilarity: getArtistSimilarity,
    getGenreSimilarity: getGenreSimilarity,
    getWikiInfo: getWikiInfo
};
