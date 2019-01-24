<?php
  require_once("session.php");
  require_once("system/class.user.php");
  require_once("system/class.comparison.php");
  $user = new User();
  $username = $_SESSION['user_session'];
  $userRow = $user->getUserData($username);
?>

<html>
	<head>
		<title>Git-R</title>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />

		<!--  Style -->
		<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" media="screen" />
		<link href="css/style.css" rel="stylesheet" type="text/css" media="screen" />
		<link href="css/jquery-ui.css" rel="stylesheet">

		<!--  Scripting -->
		<script src="js/jquery.min.js" type="text/javascript"></script>
		<script src="js/jquery-ui.js"></script>
		<script src="js/bootstrap.min.js" type="text/javascript"></script>
		<script src="js/file-tree-script.js" type="text/javascript"></script>
		<script src="js/main-script.js" type="text/javascript"></script>
	</head>

	<body oncontextmenu="return false;">

		<!-- Success Alert -->
		<div id="success-message">
			<div class="alert alert-success">
				<button type="button" class="close" data-dismiss="alert">x</button>
		    <strong>Success!</strong> Operation completed.
		  </div>
		</div>

		<!-- Failure Alert -->
		<div id="error-message">
			<div class="alert alert-danger">
				<button type="button" class="close" data-dismiss="alert">x</button>
		    <strong>Error!</strong> Operation not completed.
		  </div>
		</div>

		<!-- Right-Click on File or Folder Modal -->
		<div id="rightClickModal" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Actions</h4>
		      </div>
		      <div class="modal-body">
		        <p></p>
						<button id="deleteBtn" type="button" class="btn btn-danger">Delete</button>
		      </div>
		      <div class="modal-footer">
		        <button id="renameBtn" type="button" class="btn btn-warning">Rename</button>
						<button id="newFolderBtn" type="button" class="btn btn-primary">New Folder</button>
		        <button id="newFileBtn" type="button" class="btn btn-success">New File</button>
						<div class="col-xs-6">
					    <input type="text" class="form-control" id="nameTB">
					  </div>
		      </div>
		    </div>
		  </div>
		</div>

		<!-- Click on New Project Modal -->
		<div id="createProjectModal" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Create New Project</h4>
		      </div>
		      <div class="modal-body">
		        <button id="createProjectBtn" type="button" class="btn btn-success">Create</button>
						<div class="col-xs-10">
					    <input type="text" class="form-control" id="projNameTB" placeholder="Project Name">
					  </div>
		      </div>
		    </div>
		  </div>
		</div>

		<!-- Click on Join Project Modal -->
		<div id="joinProjectModal" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Join Project</h4>
		      </div>
		      <div class="modal-body">
		        <button id="joinProjectBtn" type="button" class="btn btn-success">Join</button>
						<div class="col-xs-10">
							<input type="text" class="form-control" id="joinNameTB" placeholder="Project Name">
						</div>
		      </div>
		    </div>
		  </div>
		</div>

		<!-- Click on Create Branch Modal -->
		<div id="createBranchModal" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Create Branch</h4>
		      </div>
		      <div class="modal-body">
		        <button id="createBranchBtn" type="button" class="btn btn-success">Create</button>
						<div class="col-xs-10">
							<input type="text" class="form-control" id="branchNameTB" placeholder="Project Name">
						</div>
		      </div>
		    </div>
		  </div>
		</div>

		<nav class="navbar navbar-inverse navbar-fixed-top">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
					<span class="sr-only">Menu</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
				</button>
				<table>
					<tr>
						<td>
							<img src="img/logos/logo.png" style="height:35px; margin: auto 10px auto 15px;"/>
						</td>
						<td>
							<div class="navbar-collapse collapse">
						    <ul class="nav navbar-nav">
						      <li class="dropdown">
						        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
						  				<span class="glyphicon glyphicon-align-left"></span>&nbsp; My Projects &nbsp;<span class="caret"></span>
										</a>
						        <ul class="dropdown-menu">
											<!--  Fixed -->
											<li><a id="createProjectShow" href="#" style="color:#55c955;"><span class="glyphicon glyphicon-plus"></span>&nbsp; New Project</a></li>
											<!--  Variables -->
						          <li><a href=""><span class="glyphicon glyphicon-menu-right"></span>&nbsp; Proj1</a></li>
						          <li><a href=""><span class="glyphicon glyphicon-menu-right"></span>&nbsp; Proj2</a></li>
						          <li><a href=""><span class="glyphicon glyphicon-menu-right"></span>&nbsp; Proj3</a></li>
						        </ul>
						      </li>
						    </ul>
						  </div>
						</td>
						<td>
							<div class="navbar-collapse collapse">
						    <ul class="nav navbar-nav">
						      <li class="dropdown">
						        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
						  				<span class="glyphicon glyphicon-align-left"></span>&nbsp; Joined Projects &nbsp;<span class="caret"></span>
										</a>
						        <ul class="dropdown-menu">
											<!--  Fixed -->
											<li><a id="joinProjectShow" href="#" style="color:#dddb6d;"><span class="glyphicon glyphicon-tasks"></span>&nbsp; Join Project</a></li>
											<!--  Variables -->
						          <li><a href=""><span class="glyphicon glyphicon-menu-right"></span>&nbsp; Abc</a></li>
						          <li><a href=""><span class="glyphicon glyphicon-menu-right"></span>&nbsp; 123</a></li>
						          <li><a href=""><span class="glyphicon glyphicon-menu-right"></span>&nbsp; Xyz</a></li>
						          <li><a href=""><span class="glyphicon glyphicon-menu-right"></span>&nbsp; Jil</a></li>
						        </ul>
						      </li>
						    </ul>
						  </div>
						</td>
						<td>
							<div class="navbar-collapse collapse">
						    <ul class="nav navbar-nav">
						      <li class="dropdown">
						        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
						  				<span class="glyphicon glyphicon-random"></span>&nbsp; Branches &nbsp;<span class="caret"></span>
										</a>
						        <ul class="dropdown-menu">
											<!--  Fixed -->
											<li><a id="createBranchShow" href="#" style="color:#4b8cc6;"><span class="glyphicon glyphicon-plus"></span>&nbsp; Create Branch</a></li>
											<!--  Variables -->
						          <li><a href=""><span class="glyphicon glyphicon-menu-right"></span>&nbsp; Master</a></li>
						          <li><a href=""><span class="glyphicon glyphicon-menu-right"></span>&nbsp; Develop</a></li>
						        </ul>
						      </li>
						    </ul>
						  </div>
						</td>
					</tr>
				</table>
			</div>
			<div style="float:right;">
				<table>
					<tr>
						<td>
							<div style="color:#9d9d9d; margin-right:20px;">
								Proj1 &nbsp;<span class="glyphicon glyphicon-menu-right"></span>&nbsp; <b>Master</b>
							</div>
						</td>
						<td>
							<div id="navbar" class="navbar-collapse collapse" style="margin-right:50px;">
						    <ul class="nav navbar-nav navbar-right">
						      <li class="dropdown">
						        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
						  				<span class="glyphicon glyphicon-user"></span>&nbsp; <?php echo $userRow['username']; ?>&nbsp;<span class="caret"></span></a>
							        <ul class="dropdown-menu">
							          <li><a href="logout.php?logout=true" style="color:#fb4d4d;"><span class="glyphicon glyphicon-log-out"></span>&nbsp;Logout</a></li>
							        </ul>
						      </li>
						    </ul>
						  </div>
						</td>
					</tr>
				</table>
			</div>
		</nav>

		<div style="width: 100%; height: 100%; display: table; margin-top:50px;">
	    <div style="display: table-row;">
				<!--  File Tree Structure -->
				<div class="fileTreeContainer">
					<div id="mainTree" class="fileTreeMain"></div>
				</div>
				<!-- Show differences between two files -->
				<div>
          <?php echo Comparison::toTable(Comparison::compareFiles('./File1.txt', './File2.txt')); ?>
				</div>
	    </div>
		</div>

	</body>
</html>
