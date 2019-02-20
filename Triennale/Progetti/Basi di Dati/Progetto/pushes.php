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
						        <a href="./index.php" class="dropdown-toggle">
						  				<span class="glyphicon glyphicon-home"></span>&nbsp; Home
						      </li>
						    </ul>
						  </div>
						</td>
						<td>
							<div class="navbar-collapse collapse">
						    <ul class="nav navbar-nav">
						      <li class="dropdown">
						        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
						  				<span class="glyphicon glyphicon-align-left"></span>&nbsp; Projects &nbsp;<span class="caret"></span>
										</a>
						        <ul class="dropdown-menu" style="width: 180px;">
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
															<span href="#" style="color:#55c955;">Branches</span>
															<a href="#" class="projectPush" proj="<?php echo $row['name']; ?>" branch="Master" url="<?php echo $master[0]['url'].'.v_'.$master[0]['version'].'/'; ?>">Master</a>
															<a href="#" class="projectPush" proj="<?php echo $row['name']; ?>" branch="Develop" url="<?php echo $develop[0]['url'].'.v_'.$develop[0]['version'].'/'; ?>">Develop</a>
															<?php
																if(count($hotfix) > 0)
																	echo '<a href="#" class="projectPush" proj="'.$row['name'].'" branch="Hotfix" url="'.$hotfix[0]['url'].'.v_'.$hotfix[0]['version'].'/">Hotfix</a>';
																if(count($release) > 0)
																	echo '<a href="#" class="projectPush" proj="'.$row['name'].'" branch="Release" url="'.$release[0]['url'].'.v_'.$release[0]['version'].'/">Release</a>';
																if(count($features) > 0)
																	foreach ($features as $feature)
																		echo '<a href="#" class="projectPush" proj="'.$row['name'].'" branch="'.$feature['name'].'" url="'.$feature['url'].'.v_'.$feature['version'].'/">'.$feature['name'].'</a>';
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
						  				<span class="glyphicon glyphicon-flag"></span>&nbsp; Overview &nbsp;<span class="caret"></span>
										</a>
						        <ul class="dropdown-menu pushes" style="width: 180px;"></ul>
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
							<button id="refusePush" type="button" class="btn btn-danger" style="margin-right:20px; display:none;">Refuse</button>
						</td>
						<td>
							<button id="approvePush" type="button" class="btn btn-success" style="margin-right:20px; display:none;">Approve</button>
						</td>
						<td>
							<div style="color:#9d9d9d; margin-right:20px;">
								<span><strong>Projects</strong></span> &nbsp;<span class="glyphicon glyphicon-menu-right"></span>&nbsp; <span id="project">-</span> &nbsp;<span class="glyphicon glyphicon-menu-right"></span>&nbsp; <b><span id="branch">-</span></b>
							</div>
						</td>
						<td>
							<div id="navbar" class="navbar-collapse collapse" style="margin-right:50px;">
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
				<div id="fileComparison"></div>
	    </div>
		</div>

	</body>
</html>
