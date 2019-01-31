var player;
var videoId = location.hash.replace('#', '');
var prevVideoId = localStorage.getItem('user-settings') != null ? jQuery.parseJSON(localStorage.getItem('user-settings')).currentId : '';
var videoTitle = '';

var watched = false; // true se il video supera i 10 secondi
var count = 0; // conto i secondi effettivamente visti dall'utente
var callTimer; // richiamo la funzione updateTime.

var firstVideo = true; // With this variable the user can exit and not begin a loop when comes back to the first video
var enableWikiInfo = true; // With this variable the wiki info (origin, members, genres) are shown just the first click for each video

// Creo un oggetto <iframe> (e player YouTube) dopo che il codice dall'API è stato scaricato
function onYouTubeIframeAPIReady() {
  player = new YT.Player('video-player', {
    events: {
      'onReady': onPlayerReady,
      'onStateChange': onPlayerStateChange,
      'onError': onPlayerError
    }
  });
}

// Funzione chiamata quando il video player è in stato ready
function onPlayerReady(event) {
   startWatchingYTVideo(videoId);
}

// Funzione chiamata quando lo stato del video player cambia
function onPlayerStateChange(event) {
   if(event.data == YT.PlayerState.ENDED) {
     console.log("0 : Video ended");
     clearInterval(callTimer);
   } else if(event.data == YT.PlayerState.PLAYING) {
     console.log("1 : Video playing");
     if (!watched)
       callTimer = setInterval(updateTime, 1000);
   } else if(event.data == YT.PlayerState.PAUSED) {
     console.log("2 : Video paused");
     clearInterval(callTimer);
   } else if(event.data == YT.PlayerState.BUFFERING) {
     console.log("3 : Video buffering");
     clearInterval(callTimer);
   } else if(event.data == YT.PlayerState.CUED) {
     console.log("4 : Video cued");
     clearInterval(callTimer);
   }
}

// Funzione chiamata quando il video player incontra degli errori
function onPlayerError(event) {
   if(event.data == 2)
      console.log("2 : The request contains an invalid parameter value");
   else if(event.data == 5)
      console.log("5 : The requested content cannot be played in an HTML5");
   else if(event.data == 100)
      console.log("100 : The video requested was not found");
   else if(event.data == 101)
      console.log("101 : The owner of the requested video does not allow it to be played in embedded players");
   else if(event.data == 150)
      console.log("150 : The owner of the requested video does not allow it to be played in embedded players");
}

function startWatchingYTVideo(inputVideoId) {
   videoId = inputVideoId;

   if(videoId != '') {
      // Update current video Id
      var userSettings = jQuery.parseJSON(localStorage.getItem('user-settings'));
      userSettings.currentId = videoId;
      localStorage.setItem('user-settings', JSON.stringify(userSettings));

      // Play the video
      player.loadVideoById(videoId, 0, "large");

      watched = false;
      count = 0;

      enableWikiInfo = true;
      getYoutubeInfo();

      // Update active tab content (if the content depends on the video played)
      var tabIndex = $("#recommender-section ul li.active").index();
      if(tabIndex == 0) // Starter tab
        getStarterList();
      else if(tabIndex == 1) // Random tab
        getRandomList();
      else if(tabIndex == 3) // Related tab
        getRelatedList();
      else if(tabIndex == 4) // Recent tab
        getRecentList();
      else if(tabIndex == 5) // FVitali tab
        getVitaliList();
      else if(tabIndex == 6) { // Popularity tab
        getAbsoluteLocalPopList();
        getRelativeLocalPopList();
        getAbsoluteGlobalPopList();
        getRelativeGlobalPopList();
      } else if(tabIndex == 7) { // Similarity tab
        getArtistSimilarityList();
        getGenreSimilarityList();
      }
   } else
     getStarterList();
}

/* SERVER */

function videoWatched(newVideoId, oldVideoId) {
  $.ajax({
    url: '/watched',
    type: "POST",
    data: {"watchedId": newVideoId, "fromId": oldVideoId}
  });
}

function videoSelected(newVideoId) {
  $.ajax({
    url: '/selected',
    type: "POST",
    data: {"selectedId": newVideoId}
  });
}

function getYoutubeInfo() {
   $.ajax({ // Video info
     url: '/youtube',
     type: "POST",
     data: {"code": "info", "videoId": videoId},
     success: function(response) { showYTInfo(jQuery.parseJSON(response)); },
     error: function(response) {
        $("#info").html("Sorry, no information found.");
        $("#video").html("Sorry, no information found.");
     }
   });

   $.ajax({ // Video comments
     url: '/youtube',
     type: "POST",
     data: {"code": "comments", "videoId": videoId},
     success: function(response) { showYTComments(jQuery.parseJSON(response)); },
     error: function(response) { $("#comments div").html("Sorry, no comment available for this video."); }
   });
}

function getWikiInfo(keyphrase) {
   $("#wiki").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
   $.ajax({ // Video wiki
     url: '/wikipedia',
     type: "POST",
     data: {"code": "info", "keyphrase": keyphrase},
     success: function(response) { showWikiInfo(jQuery.parseJSON(response)); }
   });
}

function getStarterList() {
   $("#starter").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
   var userSettings = localStorage.getItem('user-settings');
   if(userSettings != null) {
      var obj = jQuery.parseJSON(userSettings);
      showStartingList(obj.startingList);
      if(firstVideo) {
        location.hash = '#' + obj.currentId;
        firstVideo = false;
      }
   } else {
      $.ajax({ // Video list
        url: '/youtube',
        type: "POST",
        data: {"code": "starter"},
        success: function(response) {
           var list = jQuery.parseJSON(response);
           var obj = {
              'currentId': list[0].id,
              'startingList': list,
              'recentList': []
           };
           localStorage.setItem('user-settings', JSON.stringify(obj));
           showStartingList(list);
           if(firstVideo) {
             location.hash = '#' + list[0].id;
             firstVideo = false;
           }
        },
        error: function(response) { $("#starter").html("Sorry, nothing found on YouTube."); }
      });
   }
}

function getRandomList() {
   $("#random").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
   $.ajax({ // Video list
     url: '/youtube',
     type: "POST",
     data: {"code": "random"},
     success: function(response) { showRandomVideos(jQuery.parseJSON(response)); },
     error: function(response) { $("#random").html("Sorry, nothing found on YouTube."); }
   });
}

function getSearchList(query, type) {
   $("#search").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
   $.ajax({ // Video list
     url: '/youtube',
     type: "POST",
     data: {"code": "search", "query": query, "type": type},
     success: function(response) { showSearchResults(jQuery.parseJSON(response)); },
     error: function(response) { $("#search").html("Sorry, nothing found on YouTube."); }
   });
}

function getRelatedList() {
  $("#related").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
  $.ajax({ // Video list
     url: '/youtube',
     type: "POST",
     data: {"code": "related", "videoId": videoId},
     success: function(response) { showRelatedVideos(jQuery.parseJSON(response)); },
     error: function(response) { $("#related").html("Sorry, nothing found on YouTube."); }
   });
}

function getRecentList() {
   $("#recent").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
   var userSettings = localStorage.getItem('user-settings');
   if(userSettings != null) {
     var obj = jQuery.parseJSON(userSettings);
     showRecentList(obj.recentList);
   } else
     showRecentList([]);
}

function getVitaliList() {
  $("#fvitali").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
  $.ajax({ // Video list
     url: '/youtube',
     type: "POST",
     data: {"code": "fvitali", "videoId": videoId},
     success: function(response) { showVitaliVideos(jQuery.parseJSON(response)); },
     error: function(response) { $("#fvitali").html("Sorry, nothing found on fvitali."); }
   });
}

function getAbsoluteLocalPopList() {
  $("#popularity-abs-loc").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
  $.ajax({ // Video list
     url: '/youtube',
     type: "POST",
     data: {"code": "abs-loc"},
     success: function(response) { showAbsoluteLocalPopVideos(jQuery.parseJSON(response)); },
     error: function(response) { $("#popularity-abs-loc").html("Sorry, nothing found on our server."); }
   });
}

function getRelativeLocalPopList() {
  $("#popularity-rel-loc").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
  $.ajax({ // Video list
     url: '/youtube',
     type: "POST",
     data: {"code": "rel-loc", "videoId": videoId},
     success: function(response) { showRelativeLocalPopVideos(jQuery.parseJSON(response)); },
     error: function(response) { $("#popularity-rel-loc").html("Sorry, nothing found on our server."); }
   });
}

function getAbsoluteGlobalPopList() {
  $("#popularity-abs-glo").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
  $.ajax({ // Video list
     url: '/youtube',
     type: "POST",
     data: {"code": "abs-glo", "videoId": videoId},
     success: function(response) { showAbsoluteGlobalPopVideos(jQuery.parseJSON(response)); },
     error: function(response) { $("#popularity-abs-glo").html("Sorry, nothing found on our server."); }
   });
}

function getRelativeGlobalPopList() {
  $("#popularity-rel-glo").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
  $.ajax({ // Video list
     url: '/youtube',
     type: "POST",
     data: {"code": "rel-glo", "videoId": videoId},
     success: function(response) { showRelativeGlobalPopVideos(jQuery.parseJSON(response)); },
     error: function(response) { $("#popularity-rel-glo").html("Sorry, nothing found on our server."); }
   });
}

function getArtistSimilarityList() {
  $("#similarity-artist").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
  $.ajax({ // Video list
     url: '/youtube',
     type: "POST",
     data: {"code": "sim-art", "wikiName": videoTitle},
     success: function(response) { showArtistSimilarityVideos(jQuery.parseJSON(response)); },
     error: function(response) { $("#similarity-artist").html("Sorry, nothing found on DBPedia."); }
   });
}

function getGenreSimilarityList() {
  $("#similarity-genre").html("<img src='img/loading.gif' class='loading-gif'/><h3>Loading...</h3>Please wait.");
  $.ajax({ // Video list
     url: '/youtube',
     type: "POST",
     data: {"code": "sim-gen", "wikiName": videoTitle},
     success: function(response) { showGenreSimilarityVideos(jQuery.parseJSON(response)); },
     error: function(response) { $("#similarity-genre").html("Sorry, nothing found on DBPedia."); }
   });
}

/* DISPLAY */

function showYTInfo(info) {
   if(Object.keys(info).length > 0) { // The object is not empty
     var release = new Date(info.publishedAt), tags = "";
     if(info.tags != undefined)
       info.tags.forEach(function(tag) { tags += "<span class='tag-link'>" + tag + "</span> "; });

     // Update recent list with the new video
     var userSettings = jQuery.parseJSON(localStorage.getItem('user-settings'));
     for (var i = 0; i < userSettings.recentList.length; i++) // If it is already in the list then remove it
       if(userSettings.recentList[i].id === info.id) {
         userSettings.recentList.splice(i, 1);
         break;
       }
     userSettings.recentList.unshift(info); // Add in the beginning
     if(userSettings.recentList.length > 30)
       userSettings.recentList.splice(30, userSettings.recentList.length - 30);
     localStorage.setItem('user-settings', JSON.stringify(userSettings));
     getRecentList();

     getWikiInfo(info.title);

     $('#info .video-title').html(info.title);
     $('#info .video-code').html(info.id);
     $('#info .video-release-date').html(release.getUTCDate() + "/" + release.getUTCMonth() + "/" + release.getUTCFullYear() + " - " + release.getUTCHours() + ":" + release.getUTCMinutes() + ":" + release.getUTCSeconds());
     $('#info .video-channel').html(info.channelTitle);
     $('#info .video-channel').attr('channelId', info.channelId);
     $('#info .video-description').html(info.description.replace(/(\b(https?|):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig, '<a target="_blank" href="$1">$1</a>'));
     $('#info .video-tags').html(tags);

     $('#video .details-duration').html("Duration: <b>" + convertDuration(info.duration) + "</b>");
     $('#video .details-definition').html("Definition: <b>" + info.definition.toUpperCase() + "</b>");
     $('#video .details-dimension').html("Dimension: <b>" + info.dimension.toUpperCase() + "</b>");
     $('#video .details-views').html("Views: <b>" + info.viewCount + "</b>");
     $('#video .details-like').html("Likes: <b>" + info.likeCount + "</b>");
     $('#video .details-dislike').html("Dislike: <b>" + info.dislikeCount + "</b>");
   }
}

function showYTComments(comments) {
   var commentsHTML = '';
   for(var i = 0; i < comments.length; i++) {
      if(Object.keys(comments[i]).length > 0) { // The object is not empty
        var date = new Date(comments[i].updatedAt);
        commentsHTML += '<div class="single-comment">\
           <div class="col-md-2">\
              <img src="' + comments[i].authorProfileImageUrl + '"/>\
           </div>\
           <div class="col-md-10">\
              <h5><b>' + comments[i].authorDisplayName + '</b> - [' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() +
           ']</h5></div>\
           <p>' + comments[i].textDisplay +'</p>\
        </div>';
      }
   }
   $("#comments div").html(commentsHTML);
}

function showWikiInfo(info) {
   if(info.error == undefined && info.title != undefined && Object.keys(info).length > 0 && info.text != undefined) {
     var result = info.text;
     result = result.replace(new RegExp('href="/', 'g'), 'href="//www.wikipedia.org/');
     result = result.replace(new RegExp('background:*', 'g'), 'background-color:#ffffff1a;');
     result = result.replace(new RegExp('#aaa', 'g'), '#74707099');
     $('#wiki').html(result);

     if(enableWikiInfo) {
       showVideoData(info.title, result);
       enableWikiInfo = false;
     }
   } else
     $("#wiki").html("Sorry. Nothing found on Wikipedia.");
}

function showVideoData(title, result) {
  videoTitle = title;
  getArtistSimilarityList();
  getGenreSimilarityList();

  var data = {
    'title': title,
    'origin': '',
    'members': [],
    'genres': []
  };

  var origin = $('<div>').append(result).find('th:contains("Origin")').nextAll('td:first')[0];
  var members = $('<div>').append(result).find('th:contains("Members")').nextAll('td:first')[0];
  var genres = $('<div>').append(result).find('th:contains("Genres")').nextAll('td:first')[0];

  if(origin !== undefined)
    if(origin.childNodes[0].nodeName === "A")
      data.origin = '<a href="' + origin.childNodes[0].href + '">' + origin.childNodes[0].innerHTML + '</a>';
    else if(origin.childNodes[0].nodeName === "#text") {
      var temp = origin.childNodes[0].nodeValue.replace(new RegExp('\n', 'g'), '').replace(new RegExp('\t', 'g'), '').replace(new RegExp(',', 'g'), '').trim()
      if(temp !== "") data.origin = temp;
    }
  if(members !== undefined)
    for(var i = 0; i < members.childNodes.length; i++)
      if(members.childNodes[i].nodeName === "A")
        data.members.push('<a href="' + members.childNodes[i].href + '">' + members.childNodes[i].innerHTML + '</a>');
      else if(members.childNodes[0].nodeName === "#text") {
        var temp = members.childNodes[0].nodeValue.replace(new RegExp('\n', 'g'), '').replace(new RegExp('\t', 'g'), '').replace(new RegExp(',', 'g'), '').trim()
        if(temp !== "") data.members = temp;
      }
  if(genres !== undefined)
    for(var i = 0; i < genres.childNodes.length; i++)
      if(genres.childNodes[i].nodeName === "A")
        data.genres.push('<a href="' + genres.childNodes[i].href + '">' + genres.childNodes[i].innerHTML + '</a>');
      else if(genres.childNodes[0].nodeName === "#text") {
        var temp = genres.childNodes[0].nodeValue.replace(new RegExp('\n', 'g'), '').replace(new RegExp('\t', 'g'), '').replace(new RegExp(',', 'g'), '').trim()
        if(temp !== "") data.genres = temp;
      }

  var addInfo = '<h4><b>' + data.title + '</b></h4>';
  if(data.origin !== "")
    addInfo += '<h5>Origin: ' + data.origin + '</h5>';
  if(data.members.length > 0) {
    addInfo += '<h5> Members: ';
    for(var i = 0; i < data.members.length; i++)
      addInfo += data.members[i] + ', ';
    addInfo = addInfo.substring(0, addInfo.length - 2);
    addInfo += '</h5>';
  }
  if(data.genres.length > 0) {
    addInfo += '<h5> Genres: ';
    for(var i = 0; i < data.genres.length; i++)
      addInfo += data.genres[i] + ', ';
    addInfo = addInfo.substring(0, addInfo.length - 2);
    addInfo += '</h5>';
  }

  $(".add-info").html(addInfo);
}

function showStartingList(list) {
   var result = "";
   if(list.length > 0)
     for(var i = 0; i < list.length; i++) {
       if(Object.keys(list[i]).length > 0) { // The object is not empty
         var date = new Date(list[i].publishedAt);
         result += '<div class="col-md-12 video-item" role="option">\
          <div class="col-md-2 image">\
            <img src="' + list[i].thumbnail + '" width="100%" alt="Video ' + list[i].title + ' Thumbnail" class="start-video" videoId="' + list[i].id + '"/>\
          </div>\
          <div class="col-md-5 description">\
            <span class="title start-video" videoId="' + list[i].id + '">' + list[i].title + ' </span><br>\
            <span class="code start-video" videoId="' + list[i].id + '">' + list[i].id + '</span><br/>\
            <span class="racc">' + list[i].description.substr(0, 150) + '...</span>\
          </div>\
          <div class="col-md-5 info">\
            <span class="title"> Information </span> <br/><br/>\
              Channel title: <b>' + list[i].channelTitle + '</b><br/>\
              Duration: <b>' + convertDuration(list[i].duration) + '</b><br>\
              Published at: <b> ' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() + '</b><br/>\
              Recommendation: <span class="recomm"><b>This was recommended as a starting video.</b></span>\
          </div>\
         </div>';
       }
     }
   else
     result += "<div class='col-md-12'><h3>Sorry!</h3><h5>Nothing to show, we apologise for the inconvenience.</h5></div>"
   $('#starter').html(result);
}

function showRandomVideos(list) {
   var result = "";
   if(list.length > 0)
     for(var i = 0; i < list.length; i++) {
        if(Object.keys(list[i]).length > 0) { // The object is not empty
          var date = new Date(list[i].publishedAt);
          result += '<div class="col-md-12 video-item" role="option">\
           <div class="col-md-2 image">\
             <img src="' + list[i].thumbnail + '" width="100%" alt="Video ' + list[i].title + ' Thumbnail" class="start-video" videoId="' + list[i].videoId + '"/>\
           </div>\
           <div class="col-md-5 description">\
             <span class="title start-video" videoId="' + list[i].videoId + '">' + list[i].title + ' </span><br>\
             <span class="code start-video" videoId="' + list[i].videoId + '">' + list[i].videoId + '</span><br/>\
             <span class="racc">' + list[i].description.substr(0, 150) + '...</span>\
           </div>\
           <div class="col-md-5 info">\
             <span class="title"> Information </span> <br/><br/>\
               Channel title: <b>' + list[i].channelTitle + '</b><br/>\
               Position: <b>' + list[i].position + '</b><br>\
               Published at: <b> ' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() + '</b><br/>\
               Recommendation: <span class="recomm"><b>This was recommended as a random video.</b></span>\
           </div>\
          </div>';
        }
     }
   else
     result += "<div class='col-md-12'><h3>Sorry!</h3><h5>Nothing to show, we apologise for the inconvenience.</h5></div>"
   $('#random').html(result);
}

function showSearchResults(list) {
   var result = "";
   if(list.length > 0)
     for(var i = 0; i < list.length; i++) {
        if(Object.keys(list[i]).length > 0) { // The object is not empty
          var date = new Date(list[i].publishedAt);
          result += '<div class="col-md-12 video-item" role="option">\
           <div class="col-md-2 image">\
             <img src="' + list[i].thumbnail + '" width="100%" alt="Video ' + list[i].title + ' Thumbnail" class="start-video" videoId="' + list[i].videoId + '"/>\
           </div>\
           <div class="col-md-5 description">\
             <span class="title start-video" videoId="' + list[i].videoId + '">' + list[i].title + ' </span><br>\
             <span class="code start-video" videoId="' + list[i].videoId + '">' + list[i].videoId + '</span><br>\
             <span class="racc">' + list[i].description.substr(0, 150) + '...</span>\
           </div>\
           <div class="col-md-5 info">\
             <span class="title"> Information </span> <br/><br/>\
               Channel title: <b>' + list[i].channelTitle + '</b><br/>\
               Published at: <b> ' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() + '</b><br/>\
               Recommendation: <span class="recomm"><b>This was recommended as search result.</b></span>\
           </div>\
          </div>';
        }
     }
   else
     result += "<div class='col-md-12'><h3>Sorry!</h3><h5>Nothing to show, we apologise for the inconvenience.</h5></div>"
   $('#search').html(result);
}

function showRelatedVideos(list) {
   var result = "";
   if(list.length > 0)
     for(var i = 0; i < list.length; i++) {
        if(Object.keys(list[i]).length > 0) { // The object is not empty
          var date = new Date(list[i].publishedAt);
          result += '<div class="col-md-12 video-item" role="option">\
           <div class="col-md-2 image">\
             <img src="' + list[i].thumbnail + '" width="100%" alt="Video ' + list[i].title + ' Thumbnail" class="start-video" videoId="' + list[i].videoId + '"/>\
           </div>\
           <div class="col-md-5 description">\
             <span class="title start-video" videoId="' + list[i].videoId + '">' + list[i].title + ' </span><br>\
             <span class="code start-video" videoId="' + list[i].videoId + '">' + list[i].videoId + '</span><br/>\
             <span class="racc">' + list[i].description.substr(0, 150) + '...</span>\
           </div>\
           <div class="col-md-5 info">\
             <span class="title"> Information </span> <br/><br/>\
               Channel title: <b>' + list[i].channelTitle + '</b><br/>\
               Published at: <b> ' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() + '</b><br/>\
               Recommendation: <span class="recomm"><b>This was recommended as a related video.</b></span>\
           </div>\
          </div>';
        }
     }
   else
     result += "<div class='col-md-12'><h3>Sorry!</h3><h5>Nothing to show, we apologise for the inconvenience.</h5></div>"
   $('#related').html(result);
}

function showRecentList(list) {
   var result = "";
   if(list.length > 0)
     for(var i = 0; i < list.length; i++) {
       if(Object.keys(list[i]).length > 0) { // The object is not empty
         var date = new Date(list[i].publishedAt);
         result += '<div class="col-md-12 video-item" role="option">\
          <div class="col-md-2 image">\
            <img src="' + list[i].thumbnail + '" width="100%" alt="Video ' + list[i].title + ' Thumbnail" class="start-video" videoId="' + list[i].id + '"/>\
          </div>\
          <div class="col-md-5 description">\
            <span class="title start-video" videoId="' + list[i].id + '">' + list[i].title + ' </span><br>\
            <span class="code start-video" videoId="' + list[i].id + '">' + list[i].id + '</span><br/>\
            <span class="racc">' + list[i].description.substr(0, 150) + '...</span>\
          </div>\
          <div class="col-md-5 info">\
            <span class="title"> Information </span> <br/><br/>\
              Channel title: <b>' + list[i].channelTitle + '</b><br/>\
              Duration: <b>' + convertDuration(list[i].duration) + '</b><br>\
              Published at: <b> ' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() + '</b><br/>\
              Recommendation: <span class="recomm"><b>This was recommended as a recent video.</b></span>\
          </div>\
         </div>';
       }
     }
   else
     result += "<div class='col-md-12'><h3>Sorry!</h3><h5>Nothing to show, we apologise for the inconvenience.</h5></div>"
   $('#recent').html(result);
}

function showVitaliVideos(list) {
  var result = "";
  if(list.length > 0)
    for(var i = 0; i < list.length; i++) {
       if(Object.keys(list[i]).length > 0) { // The object is not empty
         var date = new Date(list[i].publishedAt);
         result += '<div class="col-md-12 video-item" role="option">\
          <div class="col-md-2 image">\
            <img src="' + list[i].thumbnail + '" width="100%" alt="Video ' + list[i].title + ' Thumbnail" class="start-video" videoId="' + list[i].id + '"/>\
          </div>\
          <div class="col-md-5 description">\
            <span class="title start-video" videoId="' + list[i].id + '">' + list[i].title + ' </span><br>\
            <span class="code start-video" videoId="' + list[i].id + '">' + list[i].id + '</span><br/>\
            <span class="racc">' + list[i].description.substr(0, 150) + '...</span>\
          </div>\
          <div class="col-md-5 info">\
            <span class="title"> Information </span> <br/><br/>\
              Channel title: <b>' + list[i].channelTitle + '</b><br/>\
              Duration: <b>' + convertDuration(list[i].duration) + '</b><br>\
              Published at: <b> ' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() + '</b><br/>\
              Recommendation: <span class="recomm"><b>This was recommended as an fvitali video.</b></span>\
          </div>\
         </div>';
       }
    }
  else
    result += "<div class='col-md-12'><h3>Sorry!</h3><h5>Nothing to show, we apologise for the inconvenience.</h5></div>"
  $('#fvitali').html(result);
}

function showAbsoluteLocalPopVideos(list) {
  var result = "";
  if(list.length > 0)
    for(var i = 0; i < list.length; i++) {
      if(Object.keys(list[i]).length > 0) { // The object is not empty
        var date = new Date(list[i].publishedAt);
        result += '<div class="col-md-12 video-item" role="option">\
         <div class="col-md-2 image">\
           <img src="' + list[i].thumbnail + '" width="100%" alt="Video ' + list[i].title + ' Thumbnail" class="start-video" videoId="' + list[i].id + '"/>\
         </div>\
         <div class="col-md-5 description">\
           <span class="title start-video" videoId="' + list[i].id + '">' + list[i].title + ' </span><br>\
           <span class="code start-video" videoId="' + list[i].id + '">' + list[i].id + '</span><br/>\
           <span class="racc">' + list[i].description.substr(0, 150) + '...</span>\
         </div>\
         <div class="col-md-5 info">\
           <span class="title"> Information </span> <br/><br/>\
             Channel title: <b>' + list[i].channelTitle + '</b><br/>\
             Duration: <b>' + convertDuration(list[i].duration) + '</b><br>\
             Published at: <b> ' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() + '</b><br/>\
             Recommendation: <span class="recomm"><b>This was recommended as an absolute local popularity video.</b></span>\
         </div>\
        </div>';
      }
    }
  else
    result += "<div class='col-md-12'><h3>Sorry!</h3><h5>Nothing to show, we apologise for the inconvenience.</h5></div>"
  $('#popularity-abs-loc').html(result);
}

function showRelativeLocalPopVideos(list) {
  var result = "";
  if(list.length > 0)
    for(var i = 0; i < list.length; i++) {
      if(Object.keys(list[i]).length > 0) { // The object is not empty
        var date = new Date(list[i].publishedAt);
        result += '<div class="col-md-12 video-item" role="option">\
         <div class="col-md-2 image">\
           <img src="' + list[i].thumbnail + '" width="100%" alt="Video ' + list[i].title + ' Thumbnail" class="start-video" videoId="' + list[i].id + '"/>\
         </div>\
         <div class="col-md-5 description">\
           <span class="title start-video" videoId="' + list[i].id + '">' + list[i].title + ' </span><br>\
           <span class="code start-video" videoId="' + list[i].id + '">' + list[i].id + '</span><br/>\
           <span class="racc">' + list[i].description.substr(0, 150) + '...</span>\
         </div>\
         <div class="col-md-5 info">\
           <span class="title"> Information </span> <br/><br/>\
             Channel title: <b>' + list[i].channelTitle + '</b><br/>\
             Duration: <b>' + convertDuration(list[i].duration) + '</b><br>\
             Published at: <b> ' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() + '</b><br/>\
             Recommendation: <span class="recomm"><b>This was recommended as a relative local popularity video.</b></span>\
         </div>\
        </div>';
      }
    }
  else
    result += "<div class='col-md-12'><h3>Sorry!</h3><h5>Nothing to show, we apologise for the inconvenience.</h5></div>"
  $('#popularity-rel-loc').html(result);
}

function showAbsoluteGlobalPopVideos(list) {
  var result = "";
  if(list.length > 0)
    for(var i = 0; i < list.length; i++) {
      if(Object.keys(list[i]).length > 0) { // The object is not empty
        var date = new Date(list[i].publishedAt);
        result += '<div class="col-md-12 video-item" role="option">\
         <div class="col-md-2 image">\
           <img src="' + list[i].thumbnail + '" width="100%" alt="Video ' + list[i].title + ' Thumbnail" class="start-video" videoId="' + list[i].id + '"/>\
         </div>\
         <div class="col-md-5 description">\
           <span class="title start-video" videoId="' + list[i].id + '">' + list[i].title + ' </span><br>\
           <span class="code start-video" videoId="' + list[i].id + '">' + list[i].id + '</span><br/>\
           <span class="racc">' + list[i].description.substr(0, 150) + '...</span>\
         </div>\
         <div class="col-md-5 info">\
           <span class="title"> Information </span> <br/><br/>\
             Channel title: <b>' + list[i].channelTitle + '</b><br/>\
             Duration: <b>' + convertDuration(list[i].duration) + '</b><br>\
             Published at: <b> ' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() + '</b><br/>\
             Recommendation: <span class="recomm"><b>This was recommended as an absolute global popularity video.</b></span>\
         </div>\
        </div>';
      }
    }
  else
    result += "<div class='col-md-12'><h3>Sorry!</h3><h5>Nothing to show, we apologise for the inconvenience.</h5></div>"
  $('#popularity-abs-glo').html(result);
}

function showRelativeGlobalPopVideos(list) {
  var result = "";
  if(list.length > 0)
    for(var i = 0; i < list.length; i++) {
      if(Object.keys(list[i]).length > 0) { // The object is not empty
        var date = new Date(list[i].publishedAt);
        result += '<div class="col-md-12 video-item" role="option">\
         <div class="col-md-2 image">\
           <img src="' + list[i].thumbnail + '" width="100%" alt="Video ' + list[i].title + ' Thumbnail" class="start-video" videoId="' + list[i].id + '"/>\
         </div>\
         <div class="col-md-5 description">\
           <span class="title start-video" videoId="' + list[i].id + '">' + list[i].title + ' </span><br>\
           <span class="code start-video" videoId="' + list[i].id + '">' + list[i].id + '</span><br/>\
           <span class="racc">' + list[i].description.substr(0, 150) + '...</span>\
         </div>\
         <div class="col-md-5 info">\
           <span class="title"> Information </span> <br/><br/>\
             Channel title: <b>' + list[i].channelTitle + '</b><br/>\
             Duration: <b>' + convertDuration(list[i].duration) + '</b><br>\
             Published at: <b> ' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() + '</b><br/>\
             Recommendation: <span class="recomm"><b>This was recommended as a relative global popularity video.</b></span>\
         </div>\
        </div>';
      }
    }
  else
    result += "<div class='col-md-12'><h3>Sorry!</h3><h5>Nothing to show, we apologise for the inconvenience.</h5></div>"
  $('#popularity-rel-glo').html(result);
}

function showArtistSimilarityVideos(list) {
  var result = "", temp = {}, start = 0;

  if(list[0] && list[0].error == 1) { // if the results come from YT and not from DBpedia
    result += '<div class="col-md-12">\
        <h3>Sorry!</h3>\
        <h5>No information found on DBpedia. Below some YouTube results about the artist.</h5>\
     </div>'
    start = 1
  }

  // Remove duplicate items
  for(var i = start, len = list.length; i < len; i++)
    if(list[i]) temp[list[i]['videoId']] = list[i];
  list = new Array();
  for(var key in temp)
    list.push(temp[key]);
  // Display video list
  for(var i = start; i < list.length; i++) {
    if(Object.keys(list[i]).length > 0 && list[i].videoId != videoId) { // The object is not empty
      var date = new Date(list[i].publishedAt);
      result += '<div class="col-md-12 video-item" role="option">\
       <div class="col-md-2 image">\
         <img src="' + list[i].thumbnail + '" width="100%" alt="Video ' + list[i].title + ' Thumbnail" class="start-video" videoId="' + list[i].videoId + '"/>\
       </div>\
       <div class="col-md-5 description">\
         <span class="title start-video" videoId="' + list[i].videoId + '">' + list[i].title + ' </span><br>\
         <span class="code start-video" videoId="' + list[i].videoId + '">' + list[i].videoId + '</span><br/>\
         <span class="racc">' + list[i].description.substr(0, 150) + '...</span>\
       </div>\
       <div class="col-md-5 info">\
         <span class="title"> Information </span> <br/><br/>\
           Channel title: <b>' + list[i].channelTitle + '</b><br/>\
           Published at: <b> ' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() + '</b><br/>\
           Recommendation: <span class="recomm"><b>This was recommended as an artist similarity video.</b></span>\
       </div>\
      </div>';
    }
  }
  $('#similarity-artist').html(result);
}

function showGenreSimilarityVideos(list) {
  var result = "", temp = {}, start = 0;

  if(list[0] && list[0].error == 1) { // if the results come from YT and not from DBpedia
    result += '<div class="col-md-12">\
        <h3>Sorry!</h3>\
        <h5>No information found on DBpedia. Below some YouTube results about the artist.</h5>\
     </div>'
    start = 1
  }

  // Remove duplicate items
  for(var i = start, len = list.length; i < len; i++)
    if(list[i]) temp[list[i]['videoId']] = list[i];
  list = new Array();
  for(var key in temp)
    list.push(temp[key]);
  // Display video list
  for(var i = start; i < list.length; i++) {
    if(Object.keys(list[i]).length > 0 && list[i].videoId != videoId) { // The object is not empty
      var date = new Date(list[i].publishedAt);
      result += '<div class="col-md-12 video-item" role="option">\
       <div class="col-md-2 image">\
         <img src="' + list[i].thumbnail + '" width="100%" alt="Video ' + list[i].title + ' Thumbnail" class="start-video" videoId="' + list[i].videoId + '"/>\
       </div>\
       <div class="col-md-5 description">\
         <span class="title start-video" videoId="' + list[i].videoId + '">' + list[i].title + ' </span><br>\
         <span class="code start-video" videoId="' + list[i].videoId + '">' + list[i].videoId + '</span><br/>\
         <span class="racc">' + list[i].description.substr(0, 150) + '...</span>\
       </div>\
       <div class="col-md-5 info">\
         <span class="title"> Information </span> <br/><br/>\
           Channel title: <b>' + list[i].channelTitle + '</b><br/>\
           Published at: <b> ' + date.getUTCDate() + '/' + date.getUTCMonth() + '/' + date.getUTCFullYear() + ' - ' + date.getUTCHours() + ':' + date.getUTCMinutes() + ':' + date.getUTCSeconds() + '</b><br/>\
           Recommendation: <span class="recomm"><b>This was recommended as a genre similarity video.</b><span>\
       </div>\
      </div>';
    }
  }
  $('#similarity-genre').html(result);
}

/* UTILS */

function convertDuration(YTduration) {
   YTduration = YTduration.substr(2); // remove first 2 chars

   if(YTduration == "0S")
      return "Live";

   var hours = 0, minutes = 0, seconds = 0;
   if(YTduration.indexOf("H") > -1) { // more than H
      hours = YTduration.split("H")[0];
      if(hours < 10) hours = '0' + hours;
      YTduration = YTduration.split("H")[1];
   }

   if(YTduration.indexOf("M") > -1) { // more than M
    minutes = YTduration.split("M")[0];
    if(minutes < 10) minutes = '0' + minutes;
    YTduration = YTduration.split("M")[1];
   }

   if(YTduration.indexOf("S") > -1) { // more than S
    seconds = YTduration.split("S")[0];
    if(seconds < 10) seconds = '0' + seconds;
    YTduration = YTduration.split("S")[1];
   }

   return hours + ":" + minutes + ":" + seconds;
}

function updateTime() {
  if(count >= 15) {
    console.log("Video reached 15 second watched");
    count = 0;
    watched = true;
    videoWatched(videoId, prevVideoId);
    clearInterval(callTimer);
  } else
    count++;
}

function seekToSpecificTime(stringTime) {
   var h = 0, m = 0, s = 0;
   if(stringTime.indexOf('h') > -1) {
      h = stringTime.split('h')[0];
      stringTime = stringTime.split('h')[1];
   }

   if(stringTime.indexOf('m') > -1) {
      m = stringTime.split('m')[0];
      stringTime = stringTime.split('m')[1];
   }

   if(stringTime.indexOf('s') > -1) {
      s = stringTime.split('s')[0];
      stringTime = stringTime.split('s')[1];
   }

   player.seekTo((parseInt(h) * 3600 + parseInt(m) * 60 + parseInt(s)), true);
}
