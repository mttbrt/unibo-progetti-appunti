$(document).ready(function() {
  var selectedFile = "./";
  var username = $('#username').text();
  var ROOT_PATH = '';
  var develop_id = "";
  var clickedProject = "";
  var clickedBranch = "";
  var clickedPublic = "";
  var clickedVersion = "";
  var clickedPush = "";

  $("#mainContent").bind('resize', function(e) {
     alert("bitch");
  });

  /* Ajax requests */

  // op 1 - create new file
  $('#newFileBtn').click(function() {
    var path = selectedFile;
    if(isFile(selectedFile)) {
      var path = "", splitted = selectedFile.split("/");
      for (var i = 0; i < splitted.length - 1; i++)
        path += splitted[i] + "/";
    }
    path += $("#nameTB").val();

    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"path": path, "op": 1},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
    $('#rightClickModal').modal('hide');
    $("#nameTB").val("");
  });

  // op 2 - create new folder
  $('#newFolderBtn').click(function() {
    var path = selectedFile;
    if(isFile(selectedFile)) {
      var path = "", splitted = selectedFile.split("/");
      for (var i = 0; i < splitted.length - 1; i++)
        path += splitted[i] + "/";
    }
    path += $("#nameTB").val();

    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"path": path, "op": 2},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
    $('#rightClickModal').modal('hide');
    $("#nameTB").val("");
  });

  // op 3 - rename file or folder
  $('#renameBtn').click(function() {
    var fileA = "", splitted = selectedFile.split("/");
    if(isFile(selectedFile)) {
      for (var i = 0; i < splitted.length - 1; i++)
        fileA += splitted[i] + "/";
      var fileB = fileA + $("#nameTB").val();
      var fileA = selectedFile;
    } else {
      fileA += splitted[0];
      for (var i = 1; i < splitted.length - 2; i++)
        fileA += "/" + splitted[i];
      var fileB = fileA + "/" + $("#nameTB").val();
      fileA += "/" + splitted[splitted.length - 2];
    }

    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"old_name": fileA, "new_name": fileB, "op": 3},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
    $('#rightClickModal').modal('hide');
    $("#nameTB").val("");
  });

  // op 4 - delete file or folder
  $('#deleteBtn').click(function() {
    var path = selectedFile;
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"path": path, "op": 4},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
    $('#rightClickModal').modal('hide');
    $("#nameTB").val("");
  });

  // op 5 - commit work
  $('#commitBtn').click(function() {
    var project_name = $('#commitBtn').attr('proj');
    var name = $('#commitBtn').attr('branch_name');
    if(project_name !== "" && name !== "") {
      var branch;
      if(name === "Release")
        branch = 2;
      else if(name === "Hotfix")
        branch = 3;
      else
        branch = 1;
      $.ajax({
        url: 'system/controller.php',
        type: "POST",
        data: {"op": 5, "branch": branch, "project_name": project_name, "name": name, "username": username},
        success: function(response) {
          if(response === "ok") {
            buildFileTree(ROOT_PATH);
            displaySuccessMessage();
          } else
            displayErrorMessage();
        }
      });
    }
  });

  // op 6 - save file content
  $('#saveBtn').click(function() {
    var path = selectedFile;
    var content = $("#editable").val();
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 6, "path": path, "content": content},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
  });

  // op 7 - create new project
  $('#createProjectBtn').click(function() {
    var projName = $('#projNameTB').val();
    projName = projName.toLowerCase(); // Lower case
    projName = projName.replace(/\s/g, ''); // No spaces
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 7, "username": username, "name": projName},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
    $('#createProjectModal').modal('hide');
    $("#projNameTB").val("");
  });

  // op 8/9 - get all projects / get project branches
  $('#joinProjectShow').click(function() {
    var projects = [];
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 8},
      success: function(response) {
        var results = jQuery.parseJSON(response);
        if(typeof results === "object") {
          results.forEach(function(row) {
            projects.push({"label": row["name"]});
          });
        }
      }
    });
    $('#joinNameTB').autocomplete({ source: projects, select: function (event, ui) {
      $.ajax({
        url: 'system/controller.php',
        type: "POST",
        data: {"op": 9, "project_name": ui.item.label},
        success: function(response) {
          var results = jQuery.parseJSON(response);
          var htmlCode = "";
          if(typeof results === "object")
            results.forEach(function(row) {
              htmlCode += '<label class="checkbox-inline"><input class="joinBranches" proj_name="' + ui.item.label + '" type="checkbox" value="' + row + '" checked>' + row + '</label>';
            });
          $('#joinProjBranches').html(htmlCode);
        }
      });
    }});
    $('#joinNameBranchTB').autocomplete({ source: projects, select: function (event, ui) {
      $('#joinNameBranchTB').attr('proj_name', ui.item.label);
    }});
    $('#joinProjectModal').modal('show');
  });

  // op 10 - add new branch
  $('#createBranchBtn').click(function() {
    var branch = 0;
    var name = $("#branchNameTB").val();
    name = name.toLowerCase(); // Lower case
    name = name.replace(/\s/g, ''); // No spaces
    var notes = $("#branchNotesTB").val();
    if($("#featureRadioBtn").is(":checked") && $("#branchNameTB").val() !== "")
      branch = 1;
    else if($("#releaseRadioBtn").is(":checked"))
      branch = 2;
    else if($("#hotfixRadioBtn").is(":checked"))
      branch = 3;
    else
      displayErrorMessage();
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 10, "develop_id": develop_id, "branch": branch, "name": name, "notes": notes, "username": username},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
    $("#createBranchModal").modal('hide');
    $("#branchNameTB").val("");
    $("#branchNotesTB").val("");
  });

  // op 11 - join branch
  $('#joinProjectBtn').click(function() {
    $('.joinBranches:checkbox:checked').each(function(index) {
      var name = $(this).val();  // Branch name
      var proj_name = $(this).attr('proj_name'); // Project name
      var branch;
      if(name === "Release")
        branch = 2;
      else if(name === "Hotfix")
        branch = 3;
      else
        branch = 1;
      $.ajax({
        url: 'system/controller.php',
        type: "POST",
        data: {"op": 11, "project_name": proj_name, "branch": branch, "name": name, "username": username},
        success: function(response) {
          if(response === "ok") {
            buildFileTree(ROOT_PATH);
            displaySuccessMessage();
          } else
            displayErrorMessage();
        }
      });
    });
    $("#joinProjectModal").modal('hide');
  });

  // op 12 - add branch to project
  $('#createBranchJoinBtn').click(function() {
    var name = $("#branchNameJoinTB").val();
    name = name.toLowerCase(); // Lower case
    name = name.replace(/\s/g, ''); // No spaces
    var notes = $("#branchNotesJoinTB").val();
    var project_name = $("#joinNameBranchTB").attr('proj_name');
    var branch;
    if($("#featureRadioJoinBtn").is(":checked") && $("#branchNameJoinTB").val() !== "")
      branch = 1;
    else if($("#releaseRadioJoinBtn").is(":checked"))
      branch = 2;
    else if($("#hotfixRadioJoinBtn").is(":checked"))
      branch = 3;
    else
      displayErrorMessage();
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 12, "project_name": project_name, "branch": branch, "name": name, "notes": notes, "username": username},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
    $("#joinProjectModal").modal('hide');
    $("#branchNameJoinTB").val("");
    $("#branchNotesJoinTB").val("");
  });

  // op 13 - restore old version
  $('#confirmRestoreBtn').click(function() {
    var branch;
    if(clickedBranch === "Master")
      branch = 5;
    else if(clickedBranch === "Develop")
      branch = 4;
    else if(clickedBranch === "Hotfix")
      branch = 3;
    else if(clickedBranch === "Release")
      branch = 2;
    else
      branch = 1;
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 13, "project_name": clickedProject, "branch": branch, "name": clickedBranch, "version": clickedVersion, "public": clickedPublic, "username": username},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
    $('#confirmationModal').modal('hide');
  });

  // op 14 - push or merge branch
  $('#pushBtn').click(function() {
    var project_name = $('#pushBtn').attr('proj');
    var name = $('#pushBtn').attr('branch_name');
    if(project_name !== "" && name !== "") {
      var branch;
      if(name === "Develop")
        branch = 4;
      else if(name === "Hotfix")
        branch = 3;
      else if(name === "Release")
        branch = 2;
      else
        branch = 1;
      $.ajax({
        url: 'system/controller.php',
        type: "POST",
        data: {"op": 14, "branch": branch, "project_name": project_name, "name": name, "public": clickedPublic, "username": username},
        success: function(response) {
          if(response === "ok") {
            buildFileTree(ROOT_PATH);
            displaySuccessMessage();
          } else
            displayErrorMessage();
        }
      });
    }
  });

  // op 15 - pushes number
  $('.projectPush').click(function() {
    $("#mainTree").html("");
    $("#fileComparison").html("");
    clickedProject = $(this).attr('proj');
    clickedBranch = $(this).attr('branch');
    $('#project').text(clickedProject);
    $('#branch').text(clickedBranch);
    var pushesNum = -1;
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      async: false,
      data: {"op": 15, "project_name": clickedProject, "name": clickedBranch},
      success: function(response) {
        pushesNum = response;
      }
    });
    var html = "";
    if(pushesNum == -1)
      if(clickedBranch === "Master" || clickedBranch === "Develop")
        html += '<li class="sub-dropdown">&nbsp; No merges availables.</li>';
      else
        html += '<li class="sub-dropdown">&nbsp; No pushes availables.</li>';
    else
      for (var i = 0; i <= pushesNum; i++)
        if(clickedBranch === "Master" || clickedBranch === "Develop")
          html += '<li class="sub-dropdown"><a class="push" val="' + i + '" href="#"><span class="glyphicon glyphicon-menu-right"></span>&nbsp; Merge ' + i + '</a></li>';
        else
          html += '<li class="sub-dropdown"><a class="push" val="' + i + '" href="#"><span class="glyphicon glyphicon-menu-right"></span>&nbsp; Push ' + i + '</a></li>';
    $(".pushes").html(html);
  });

  // op 16 - show pushes in folder
  $(document).on('click', ".push", function() {
    clickedPush = $(this).attr('val');
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 16, "project_name": clickedProject, "name": clickedBranch, "push_num": clickedPush},
      success: function(response) {
        ROOT_PATH = '.' + response;
        buildFileTreeComparison(ROOT_PATH);
      }
    });
    $("#approvePush").show();
    $("#refusePush").show();
  });

  // op 17 - approve push
  $('#approvePush').click(function() {
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 17, "project_name": clickedProject, "name": clickedBranch, "push": clickedPush},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
  });

  // op 18 - refuse push
  $('#refusePush').click(function() {
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 18, "project_name": clickedProject, "name": clickedBranch, "push": clickedPush},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
  });

  // op 19 - delete project
  $('#deleteProjectBtn').click(function() {
    var projName = $('#delProjNameTB').val();
    projName = projName.toLowerCase(); // Lower case
    projName = projName.replace(/\s/g, ''); // No spaces
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 19, "project_name": projName, "username": username},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
    $('#deleteProjectModal').modal('hide');
    $("#delProjNameTB").val("");
  });

  // op 20 - delete public branch
  $('#deleteBranchPubBtn').click(function() {
    var branch = 0;
    var name = $("#delBranchPubNameTB").val();
    name = name.toLowerCase(); // Lower case
    name = name.replace(/\s/g, ''); // No spaces
    if($("#delFeatureRadioBtn").is(":checked") && $("#delBranchPubNameTB").val() !== "")
      branch = 1;
    else if($("#delReleaseRadioBtn").is(":checked"))
      branch = 2;
    else if($("#delHotfixRadioBtn").is(":checked"))
      branch = 3;
    else
      displayErrorMessage();
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 20, "develop_id": develop_id, "branch": branch, "name": name, "username": username},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
    $("#deleteBranchPubModal").modal('hide');
    $("#delBranchPubNameTB").val("");
  });

  // op 21 - delete private branch
  $('#deleteBranchPriBtn').click(function() {
    var branch = 0;
    var name = $("#delBranchPriNameTB").val();
    name = name.toLowerCase(); // Lower case
    name = name.replace(/\s/g, ''); // No spaces
    if($("#del2FeatureRadioBtn").is(":checked") && $("#delBranchPriNameTB").val() !== "")
      branch = 1;
    else if($("#del2ReleaseRadioBtn").is(":checked"))
      branch = 2;
    else if($("#del2HotfixRadioBtn").is(":checked"))
      branch = 3;
    else
      displayErrorMessage();
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 21, "develop_id": develop_id, "branch": branch, "name": name, "username": username},
      success: function(response) {
        if(response === "ok") {
          buildFileTree(ROOT_PATH);
          displaySuccessMessage();
        } else
          displayErrorMessage();
      }
    });
    $("#deleteBranchPriModal").modal('hide');
    $("#delBranchPriNameTB").val("");
  });


  // Right click on file/folder
  $('#mainTree').mousedown(function(event) {
    if(event.button == 2 && event.target.rel != undefined) {
      $("#deleteBtn").show();
      $("#renameBtn").show();
      selectedFile = event.target.rel;
      if(isFile(selectedFile))
        $('#rightClickModal h4').html("Actions on file");
      else
        $('#rightClickModal h4').html("Actions on folder");
      $('#rightClickModal p').html("Selected: <b>" + event.target.rel + "</b>");
      $('#rightClickModal').modal('show');
    }
  });

  // Right click on container
  $('.fileTreeContainer').mousedown(function(event) {
    if(event.button == 2 && event.target.rel == undefined && ROOT_PATH != '') {
      $("#deleteBtn").hide();
      $("#renameBtn").hide();
      selectedFile = ROOT_PATH;
      if(isFile(selectedFile))
        $('#rightClickModal h4').html("Actions on file");
      else
        $('#rightClickModal h4').html("Actions on folder");
      $('#rightClickModal p').html("Selected: <b>" + ROOT_PATH + "</b>");
      $('#rightClickModal').modal('show');
    }
  });

  // Show create project modal
  $('#createProjectShow').click(function() {
    $('#createProjectModal').modal('show');
  });

  // Show delete project modal
  $('#deleteProjectShow').click(function() {
    $('#deleteProjectModal').modal('show');
  });

  // Show create branch modal
  $('.createBranchShow').click(function() {
    $('#createBranchModal').modal('show');
    develop_id = $(this).attr('dev');
  });

  // Show delete public branch modal
  $('.deleteBranchPubShow').click(function() {
    $('#deleteBranchPubModal').modal('show');
    develop_id = $(this).attr('dev');
  });

  // Show delete private branch modal
  $('.deleteBranchPriShow').click(function() {
    $('#deleteBranchPriModal').modal('show');
    develop_id = $(this).attr('dev');
  });

  // Show confirmation restore version modal
  $(document).on('click', ".version", function() {
    $('#confirmationModal').modal('show');
    clickedVersion = $(this).attr("version");
  });

  // Click on project
  $('.project').click(function() {
    $("#fileName").html("");
    $("#saveBtn").hide();
    $("#editable").val("");

    clickedProject = $(this).attr('proj');
    clickedBranch = $(this).attr('branch');

    $('#project').text(clickedProject);
    $('#branch').text(clickedBranch);
    ROOT_PATH = "." + $(this).attr('url');
    buildFileTree(ROOT_PATH);
  });

  // Click on workbench project
  $('.workbench .project').click(function() {
    clickedPublic = false;
    // Set url to perform the commit
    $("#commitBtn").attr('proj', $(this).attr('proj'));
    $("#commitBtn").attr('branch_name', $(this).attr('branch'));
    $("#commitBtn").show();
    $("#pushBtn").attr('proj', $(this).attr('proj'));
    $("#pushBtn").attr('branch_name', $(this).attr('branch'));
    $("#restoreVersion").show();
    $("#section").html("<strong>Workbench</strong>");
    $("#pushBtn b").html("Push");
    setupVersions(clickedPublic);
  });

  // Click on public project
  $('.projects .project').click(function() {
    clickedPublic = true;
    // Reset url to perform the commit
    $("#commitBtn").attr('proj', '');
    $("#commitBtn").attr('branch_name', '');
    $("#commitBtn").hide();
    $("#pushBtn").attr('proj', $(this).attr('proj'));
    $("#pushBtn").attr('branch_name', $(this).attr('branch'));
    $("#restoreVersion").show();
    $("#section").html("<strong>Projects</strong>");
    $("#pushBtn b").html("Merge");
    setupVersions(clickedPublic);
  });

  // Tips for join project
  $('#joinNameTB').tooltip({'trigger':'focus', 'title': 'Find a project!'});
  $('#joinNameBranchTB').tooltip({'trigger':'focus', 'title': 'Find a project!'});

  // Reset join project fields
  $('#joinProjectModal').on('hidden.bs.modal', function (e) {
    $('#joinNameBranchTB').val("");
    $('#branchNameJoinTB').val("");
    $('#branchNotesJoinTB').val("");
    $('#joinNameTB').val("");
    $('#joinProjBranches').html("");
  });

  // Radio buttons owner
  $('#featureRadioBtn').prop('checked', true);

  $('#featureRadioBtn').click(function() {
    $('#branchNameTB').prop('disabled', false);
  });

  $('#releaseRadioBtn').click(function() {
    $('#branchNameTB').prop('disabled', true);
  });

  $('#hotfixRadioBtn').click(function() {
    $('#branchNameTB').prop('disabled', true);
  });

  // Radio buttons deletion public branch
  $('#delFeatureRadioBtn').prop('checked', true);

  $('#delFeatureRadioBtn').click(function() {
    $('#delBranchPubNameTB').prop('disabled', false);
  });

  $('#delReleaseRadioBtn').click(function() {
    $('#delBranchPubNameTB').prop('disabled', true);
  });

  $('#delHotfixRadioBtn').click(function() {
    $('#delBranchPubNameTB').prop('disabled', true);
  });

  // Radio buttons deletion private branch
  $('#del2FeatureRadioBtn').prop('checked', true);

  $('#del2FeatureRadioBtn').click(function() {
    $('#delBranchPriNameTB').prop('disabled', false);
  });

  $('#del2ReleaseRadioBtn').click(function() {
    $('#delBranchPriNameTB').prop('disabled', true);
  });

  $('#del2HotfixRadioBtn').click(function() {
    $('#delBranchPriNameTB').prop('disabled', true);
  });

  // Radio buttons join branch
  $('#featureRadioJoinBtn').prop('checked', true);

  $('#featureRadioJoinBtn').click(function() {
    $('#branchNameJoinTB').prop('disabled', false);
  });

  $('#releaseRadioJoinBtn').click(function() {
    $('#branchNameJoinTB').prop('disabled', true);
  });

  $('#hotfixRadioJoinBtn').click(function() {
    $('#branchNameJoinTB').prop('disabled', true);
  });

  // op 22 - get file contents
  function buildFileTree(root_path) {
    $('#mainTree').fileTree({root: root_path}, function(path) {
      selectedFile = path;
      // Check for the various File API support.
      if (window.File && window.FileReader && window.FileList && window.Blob) {
        $.ajax({
          url: 'system/controller.php',
          type: "POST",
          data: {"op": 22, "path": path},
          success: function(file_content) {
            var splitted = path.split('/');
            $("#fileName").html("<strong>" + splitted[splitted.length-1] + "</strong>");
            $("#saveBtn").show();
            $("#editable").val(file_content);
          }
        });
      } else {
        alert('The File APIs are not fully supported in this browser, please use Firefox.');
      }
    });
  }

  // op 23 - get project version
  function setupVersions() {
    var branch;
    if(clickedBranch === "Master")
      branch = 5;
    else if(clickedBranch === "Develop")
      branch = 4;
    else if(clickedBranch === "Hotfix")
      branch = 3;
    else if(clickedBranch === "Release")
      branch = 2;
    else
      branch = 1;
    $.ajax({
      url: 'system/controller.php',
      type: "POST",
      data: {"op": 23, "project_name": clickedProject, "branch": branch, "name": clickedBranch, "public": clickedPublic, "username": username},
      success: function(response) {
        var data = "";
        for (var i = 0; i <= response; i++)
          data += '<li><a href="#" class="version" version="' + i + '"><span class="glyphicon glyphicon-menu-right"></span>&nbsp; Version ' + i + '</a></li>';
        $('#restoreVersion li ul').html(data);
      }
    });
  }

  // op 24 - file comparison
  function buildFileTreeComparison(root_path) {
    $('#mainTree').fileTree({root: root_path}, function(path) {
      selectedFile = path;
      // Check for the various File API support.
      if (window.File && window.FileReader && window.FileList && window.Blob) {
        $.ajax({
          url: 'system/controller.php',
          type: "POST",
          data: {"op": 24, "project_name": clickedProject, "name": clickedBranch, "path": path},
          success: function(response) {
            $("#fileComparison").html(response);
          }
        });
      } else {
        alert('The File APIs are not fully supported in this browser, please use Firefox.');
      }
    });
  }

  // Show positive operation result
  function displaySuccessMessage() {
    $("#success-message").fadeTo(500, 200).fadeOut(500, function(){
      $("#success-message").alert('close');
    });
    setTimeout(function(){
      location.reload();
    }, 800);
  }

  // Show negative operation result
  function displayErrorMessage() {
    $("#error-message").fadeTo(500, 200).fadeOut(500, function(){
      $("#error-message").alert('close');
    });
    setTimeout(function(){
      // location.reload();
    }, 800);
  }

  // Check if path is file or folder
  function isFile(path) {
    var splitted = path.split("/");
    if(splitted[splitted.length-1]) // File
      return true;
    else // Folder
      return false;
  }

});
