<?php
	require_once("session.php");
	require_once("system/class.user.php");
	require_once("system/class.manager.php");
	$user = new User();
	$manager = new Manager();

	$username = $_SESSION['user_session'];
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

		<span id="username" style="display:none;"><?php echo $username; ?></span>

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

		<!-- Click on Create Project Modal -->
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
					    <input type="text" class="form-control" id="projNameTB" placeholder="Project Name" maxlength="40">
					  </div>
		      </div>
		    </div>
		  </div>
		</div>

		<!-- Click on Delete Project Modal -->
		<div id="deleteProjectModal" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Delete Project</h4>
		      </div>
		      <div class="modal-body">
		        <button id="deleteProjectBtn" type="button" class="btn btn-danger">Delete</button>
						<div class="col-xs-10">
					    <input type="text" class="form-control" id="delProjNameTB" placeholder="Project Name" maxlength="40">
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
					<div class="modal-body" style="height:auto; text-align:left;">
						<table style="width:100%;">
							<tr>
								<td>
									<div class="col-xs-10">
										<input type="text" class="form-control" id="joinNameTB" placeholder="Project Name">
									</div>
								</td>
								<td><button id="joinProjectBtn" type="button" class="btn btn-success" style="float:right;">Fork</button></td>
							</tr>
							<tr>
								<td><div class="col-xs-10" id="joinProjBranches" style="padding: 15px;"></div></td>
							</tr>
							<tr>
								<td><hr style="border: 1px solid #717171;">
									<div class="col-xs-10">
										<input type="text" class="form-control" id="joinNameBranchTB" proj_name="" placeholder="Project Name">
									</div>
								</td>
								<td><hr style="border: 1px solid #717171;">
									<button id="createBranchJoinBtn" type="button" class="btn btn-success">New Branch</button>
								</td>
							</tr>
							<tr style="height:50px;">
								<td style="text-align: left;">
									<label class="radio-inline" style="margin-left:20px;"><input type="radio" name="branchJoin" id="featureRadioJoinBtn" checked>Feature</label>
									<label class="radio-inline"><input type="radio" name="branchJoin" id="releaseRadioJoinBtn">Release</label>
									<label class="radio-inline"><input type="radio" name="branchJoin" id="hotfixRadioJoinBtn">Hotfix</label>
								</td>
							</tr>
							<tr>
								<td>
									<div class="col-xs-10">
										<input type="text" class="form-control" id="branchNameJoinTB" placeholder="Feature Name">
									</div>
									<div class="col-xs-10" style="margin-top:20px;">
										<input type="text" class="form-control" id="branchNotesJoinTB" placeholder="Notes">
									</div>
								</td>
							</tr>
						</table>
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
						<label class="radio-inline"><input type="radio" name="branch" id="featureRadioBtn" checked>Feature</label>
						<label class="radio-inline"><input type="radio" name="branch" id="releaseRadioBtn">Release</label>
						<label class="radio-inline"><input type="radio" name="branch" id="hotfixRadioBtn">Hotfix</label>
		      </div>
					<div class="modal-footer">
						<button id="createBranchBtn" type="button" class="btn btn-success">Create</button>
						<div class="col-xs-10">
							<input type="text" class="form-control" id="branchNameTB" placeholder="Feature Name">
						</div>
						<div class="col-xs-10" style="margin-top:20px;">
							<input type="text" class="form-control" id="branchNotesTB" placeholder="Notes">
						</div>
					</div>
		    </div>
		  </div>
		</div>

		<div id="deleteBranchPubModal" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Delete Branch</h4>
		      </div>
		      <div class="modal-body">
						<label class="radio-inline"><input type="radio" name="branch" id="delFeatureRadioBtn" checked>Feature</label>
						<label class="radio-inline"><input type="radio" name="branch" id="delReleaseRadioBtn">Release</label>
						<label class="radio-inline"><input type="radio" name="branch" id="delHotfixRadioBtn">Hotfix</label>
		      </div>
					<div class="modal-footer">
						<button id="deleteBranchPubBtn" type="button" class="btn btn-danger">Delete</button>
						<div class="col-xs-10">
							<input type="text" class="form-control" id="delBranchPubNameTB" placeholder="Feature Name">
						</div>
					</div>
		    </div>
		  </div>
		</div>

		<div id="deleteBranchPriModal" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Delete Branch</h4>
		      </div>
		      <div class="modal-body">
						<label class="radio-inline"><input type="radio" name="branch" id="del2FeatureRadioBtn">Feature</label>
						<label class="radio-inline"><input type="radio" name="branch" id="del2ReleaseRadioBtn">Release</label>
						<label class="radio-inline"><input type="radio" name="branch" id="del2HotfixRadioBtn">Hotfix</label>
		      </div>
					<div class="modal-footer">
						<button id="deleteBranchPriBtn" type="button" class="btn btn-danger">Delete</button>
						<div class="col-xs-10">
							<input type="text" class="form-control" id="delBranchPriNameTB" placeholder="Feature Name">
						</div>
					</div>
		    </div>
		  </div>
		</div>

		<!-- Click on Create Branch Modal -->
		<div id="confirmationModal" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title"><strong>Are you sure?</strong></h4>
		      </div>
		      <div class="modal-body">
						<h5>This action cannot be undone, you will lose all the subsequent versions.</h5>
		      </div>
		      <div class="modal-footer">
						<button type="button" class="btn btn-danger" data-dismiss="modal" style="float:left;">Cancel</button>
						<button id="confirmRestoreBtn" type="button" class="btn btn-success">Confirm</button>
		      </div>
		    </div>
		  </div>
		</div>

		<nav class="navbar navbar-inverse navbar-fixed-top">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
					<span class="sr-only">Menu</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
				</button>
				<table style="font-size:1.1em;">
					<tr>
						<td>
							<img src="img/logos/logo.png" style="height:35px; margin: auto 10px auto 15px;"/>
						</td>
						<td>
							<div class="navbar-collapse collapse">
						    <ul class="nav navbar-nav">
						      <li class="dropdown">
						        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
						  				<span class="glyphicon glyphicon-align-left"></span>&nbsp; Projects &nbsp;<span class="caret"></span>
										</a>
						        <ul class="dropdown-menu" style="width: 180px;">
											<!--  Fixed -->
											<li><a id="createProjectShow" href="#" style="color:#55c955;"><span class="glyphicon glyphicon-plus"></span>&nbsp; Create Project</a></li>
											<li><a id="deleteProjectShow" href="#" style="color:#fe3b3b;"><span class="glyphicon glyphicon-minus"></span>&nbsp; Delete Project</a></li>
											<!--  Variables -->
											<?php
												$projects = $manager->getUserProjects($username);
												foreach ($projects as $row) {
													$repository = $manager->getRepositoryByProjectName($row['name']);
													$master = $manager->getMasterById($repository[0]['id']);
													$develop = $manager->getDevelopById($master[0]['repo_id']);
													$hotfix = $manager->getHotfixById($develop[0]['master_id']);
													$release = $manager->getReleaseById($develop[0]['master_id']);
													$features = $manager->getFeaturesById($develop[0]['master_id']);
											?>
												<li class="sub-dropdown">
													<a href="#">
														<span class="glyphicon glyphicon-menu-right"></span>&nbsp; <?php echo $row["name"]; ?>
														<div class="dropdown-content projects">
															<span class="createBranchShow" dev="<?php echo $develop[0]['master_id']; ?>" href="#" style="color:#55c955;"><span class="glyphicon glyphicon-plus"></span>&nbsp; Create Branch</span>
															<span class="deleteBranchPubShow" dev="<?php echo $develop[0]['master_id']; ?>" href="#" style="color:#fe3b3b;"><span class="glyphicon glyphicon-minus"></span>&nbsp; Delete Branch</span>
															<a href="#" class="project" proj="<?php echo $row['name']; ?>" branch="Master" url="<?php echo $master[0]['url'].'.v_'.$master[0]['version'].'/'; ?>">Master - v<?php echo $master[0]['version'].".0"; ?></a>
															<a href="#" class="project" proj="<?php echo $row['name']; ?>" branch="Develop" url="<?php echo $develop[0]['url'].'.v_'.$develop[0]['version'].'/'; ?>">Develop</a>
															<?php
																if(count($hotfix) > 0)
																	echo '<a href="#" class="project" proj="'.$row['name'].'" branch="Hotfix" url="'.$hotfix[0]['url'].'.v_'.$hotfix[0]['version'].'/">Hotfix</a>';
																if(count($release) > 0)
																	echo '<a href="#" class="project" proj="'.$row['name'].'" branch="Release" url="'.$release[0]['url'].'.v_'.$release[0]['version'].'/">Release</a>';
																if(count($features) > 0)
																	foreach ($features as $feature)
																		echo '<a href="#" class="project" proj="'.$row['name'].'" branch="'.$feature['name'].'" url="'.$feature['url'].'.v_'.$feature['version'].'/">'.$feature['name'].'</a>';
															?>
														</div>
													</a>
												</li>
											<?php } ?>
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
						  				<span class="glyphicon glyphicon-wrench"></span>&nbsp; Workbench &nbsp;<span class="caret"></span>
										</a>
						        <ul class="dropdown-menu" style="width: 180px;">
											<!--  Fixed -->
											<li><a id="joinProjectShow" href="#" style="color:#dddb6d;"><span class="glyphicon glyphicon-tasks"></span>&nbsp; Join Project</a></li>
											<!--  Variables -->
											<?php
												$projects = $manager->getUserWorkingProjects($username);
												foreach ($projects as $row) {
													$repository = $manager->getRepositoryById($row['repo_id']);
													$master = $manager->getMasterById($repository[0]['id']);
													$develop = $manager->getDevelopById($master[0]['repo_id']);
													$hotfix = $manager->getUserPrivateHotfixBranches($develop[0]['master_id'], $username);
													$release = $manager->getUserPrivateReleaseBranches($develop[0]['master_id'], $username);
													$features = $manager->getUserPrivateFeatureBranches($develop[0]['master_id'], $username);
													if(count($hotfix) > 0 || count($release) > 0 || count($features) > 0) {
											?>
												<li class="sub-dropdown">
													<a href="#">
														<span class="glyphicon glyphicon-menu-right"></span>&nbsp; <?php echo $repository[0]["project_name"]; ?>
														<div class="dropdown-content workbench" style="left: 178px; top: -25px;">
															<span class="deleteBranchPriShow" dev="<?php echo $develop[0]['master_id']; ?>" href="#" style="color:#fe3b3b;"><span class="glyphicon glyphicon-minus"></span>&nbsp; Delete Branch</span>
															<?php
																if(count($hotfix) > 0)
																	echo '<a href="#" class="project" proj="'.$repository[0]["project_name"].'" branch="Hotfix" url="'.$hotfix[0]['url'].'.work/'.'">Hotfix</a>';
																if(count($release) > 0)
																	echo '<a href="#" class="project" proj="'.$repository[0]["project_name"].'" branch="Release" url="'.$release[0]['url'].'.work/'.'">Release</a>';
																if(count($features) > 0)
																	foreach ($features as $feature)
																		echo '<a href="#" class="project" proj="'.$repository[0]["project_name"].'" branch="'.$feature['feature_pub_name'].'" url="'.$feature['url'].'.work/'.'">'.$feature['feature_pub_name'].'</a>';
															?>
														</div>
													</a>
												</li>
											<?php
													}
												}
											?>
						        </ul>
						      </li>
						    </ul>
						  </div>
						</td>
						<td>
							<div class="navbar-collapse collapse">
						    <ul class="nav navbar-nav" id="restoreVersion">
						      <li class="dropdown">
						        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false" style="color: #e08d51;">
						  				<span class="glyphicon glyphicon-retweet"></span>&nbsp; Restore version &nbsp;<span class="caret"></span>
										</a>
						        <ul class="dropdown-menu" style="width: 180px;">
											<!--  Variables -->
											<li><a href="#" class="version" version="0"><span class="glyphicon glyphicon-menu-right"></span>&nbsp; Version 0</a></li>
						        </ul>
						      </li>
						    </ul>
						  </div>
						</td>
						<td>
							<div class="navbar-collapse collapse">
						    <ul class="nav navbar-nav">
						      <li class="dropdown">
						        <a href="./pushes.php" class="dropdown-toggle">
						  				<span class="glyphicon glyphicon-equalizer"></span>&nbsp; Manage pushes
						      </li>
						    </ul>
						  </div>
						</td>
					</tr>
				</table>
			</div>
			<div style="float:right;">
				<table style="font-size:1.1em;">
					<tr>
						<td>
							<div style="color:#9d9d9d; margin-right:20px;">
								<span id="section"><strong>-</strong></span> &nbsp;<span class="glyphicon glyphicon-menu-right"></span>&nbsp; <span id="project">-</span> &nbsp;<span class="glyphicon glyphicon-menu-right"></span>&nbsp; <b><span id="branch">-</span></b>
							</div>
						</td>
						<td>
							<div style="color:#9d9d9d; margin-right:20px;">
								<button id="commitBtn" type="button" proj="" branch_name="" class="btn btn-danger"><span class="glyphicon glyphicon-export"></span>&nbsp; <b>Commit</b></button>
							</div>
						</td>
						<td>
							<div style="color:#9d9d9d; margin-right:20px;">
								<button id="pushBtn" type="button" proj="" branch_name="" class="btn btn-primary"><span class="glyphicon glyphicon-circle-arrow-up"></span>&nbsp; <b>Push</b></button>
							</div>
						</td>
						<td>
							<div id="navbar" class="navbar-collapse collapse" style="margin-right:20px;">
						    <ul class="nav navbar-nav navbar-right">
						      <li class="dropdown">
						        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
						  				<span class="glyphicon glyphicon-user"></span>&nbsp; <?php echo $username; ?>&nbsp;<span class="caret"></span></a>
							        <ul class="dropdown-menu">
							          <li><a href="logout.php?logout=true" style="color:#fb4d4d;"><span class="glyphicon glyphicon-log-out"></span>&nbsp;Logout</a></li>
							        </ul>
						      </li>
						    </ul>
						  </div>
						</td>
						<td>
							<a href="log.php" style="margin-right:15px;">
								<img src="img/logos/log.png" style="height: 35px;" />
							</a>
						</td>
					</tr>
				</table>
			</div>
		</nav>

		<div style="width: 100%; height: 100%; display: table; margin-top:-20px;">
	    <div style="display: table-row;">
				<!--  File Tree Structure -->
				<div class="fileTreeContainer">
					<div id="mainTree" class="fileTreeMain"></div>
				</div>
				<!--  Editable -->
				<div class="editable">
					<table>
						<tr>
							<td> <h3 id="fileName"></h3> </td>
							<td> <button id="saveBtn" type="button" class="btn btn-success">Save</button> </td>
						</tr>
					</table>
					<textarea id="editable"></textarea>
				</div>
	    </div>
		</div>

	</body>
</html>
