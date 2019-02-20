$(document).ready(function(){

  setVideoPlayerRatio();

  $("#closebtn").click(function(){
    closeSidenav();
  });

  $("#navicon").click(function(){
    openSidenav();
  });

  $("#sidenav a").click(function(){
     var element = $(this).attr('section');
     $('html, body').animate({ scrollTop: $(element).offset().top - 80 }, 'slow');
     closeSidenav();
  });

  $("#search-btn").click(function(){
    // Get search query content
    var query = $("#search-modal input").val();
    getSearchList(query, "standard");

    // Close modal
    $("#search-modal").modal('hide');
    $("#search-modal input").val("");

    // Activate search tab
    $('.nav-tabs a[href="#search"]').tab('show');

    // Scroll to tabs
    $("body").animate({
        scrollTop: $(".tabs").offset().top
    }, 'slow');

    $("#search").animate({
        scrollTop: 0
    }, 'slow');
  });

  $('#search-query').keypress(function(e) {
    if(e.which == 13) { // the enter key code
      $('#search-btn').click();
      return false;
    }
  });

  $("#redirect-btn").click(function(){
     window.location.replace("/");
  });

  $("#wiki-pill").click(function() {
     getWikiInfo($("#info .video-title").html());
     $('#wiki').animate({ // Scroll to the top of the div
        scrollTop: 0
     }, 'slow');
  });

  $("#random-tab").click(function() {
     getRandomList();
  });

  $("#related-tab").click(function() {
     getRelatedList();
  });

  $("#recent-tab").click(function() {
     getRecentList();
  });

  $("#fvitali-tab").click(function() {
     getVitaliList();
  });

  $("#popularity-abs-loc-tab").click(function() {
     getAbsoluteLocalPopList();
  });

  $("#popularity-rel-loc-tab").click(function() {
     getRelativeLocalPopList();
  });

  $("#popularity-abs-glo-tab").click(function() {
     getAbsoluteGlobalPopList();
  });

  $("#popularity-rel-glo-tab").click(function() {
     getRelativeGlobalPopList();
  });

  $("#similarity-artist-tab").click(function() {
     getArtistSimilarityList();
  });

  $("#similarity-genre-tab").click(function() {
     getGenreSimilarityList();
  });

  $("#info").on("click", ".tag-link", function(){
    var tag = $(this).text();
    getSearchList(tag, "tag");

    // Activate search tab
    $('.nav-tabs a[href="#search"]').tab('show');

    // Scroll to tabs
    $("body").animate({
        scrollTop: $(".tabs").offset().top
    }, 'slow');

    $("#search").animate({
        scrollTop: 0
    }, 'slow');
  });

  $('body').click(function(event){
    if(!$("#navicon i").is(event.target) && !$("#sidenav").is(event.target) && !$(".item").is(event.target))
      closeSidenav();
  });

  $(document).on("click", "#wiki a", function(e) {
     e.preventDefault();
     var keyword = this.href.replace('wiki/', '').split('/');
     getWikiInfo(keyword[keyword.length - 1]);
     $('#wiki').animate({ // Scroll to the top of the div
        scrollTop: 0
     }, 'slow');
  });

  $(document).on("click", ".add-info a", function(e) {
     e.preventDefault();
     var keyword = this.href.replace('wiki/', '').split('/');
     getWikiInfo(keyword[keyword.length - 1]);
     $('#wiki').animate({ // Scroll to the top of the div
        scrollTop: 0
     }, 'slow');
     $('.nav-pills a[href="#wiki"]').tab('show');
  });

  $(document).on("click", "#comments a", function(e) {
     e.preventDefault();
     var href = this.href;
     if(href.indexOf('www.youtube.com/watch?') > -1 && href.indexOf('&t=') > -1)
        seekToSpecificTime(href.substr(href.indexOf('&t='), href.length).replace('&t=', ''));
  });

  $(document).on("click", ".start-video", function(e) {
     videoSelected(videoId);
     $("#video .details-recomm").html("Recommendation: <b>" + $(this).parents("div").siblings(".info").find(".recomm").text() + "</b>"); // Update recommendation
     prevVideoId = videoId;
     location.hash = '#' + $(this).attr("videoId");
  });

  $(window).bind('hashchange', function(e) {
     startWatchingYTVideo(location.hash.substring(1));
  });

  $(window).on('popstate', function(e) {
    $("body").animate({ scrollTop: 0 }, 'fast');
  });

  $(window).on('resize', function(){
    setVideoPlayerRatio();
  });

});

function closeSidenav() {
  $("#sidenav").css("width", 0);
  $("#sidenav").attr("aria-expanded", "false");
  $("#main").css("margin-right", 0);
  $("#overlay").fadeOut();
}

function openSidenav() {
  $("#sidenav").css("width", "250px");
  $("#sidenav").attr("aria-expanded", "true");
  $("#main").css("margin-right", "250px");
  $("#overlay").fadeIn();
}

// Ratio 16:9
function setVideoPlayerRatio() {
  var width = $("#video-player").offsetParent().width();
  var height = width*9/16;
  $("#video-player").css("width", width);
  $("#video-player").css("height", height);
  if($(document).width() > 992)
    $('.tab-pane').css("height", height - 50);
}
