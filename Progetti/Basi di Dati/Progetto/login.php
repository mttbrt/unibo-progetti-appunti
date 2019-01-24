<?php
session_start();
require_once("system/class.user.php");
$user = new User();

if($user->isLogged()) {
	$user->redirect('index.php');
}

if(isset($_POST['btn-login'])) {
	$username = strip_tags($_POST['in_username']);
	$password = strip_tags($_POST['in_password']);

	if($user->loginUser($username, $password)) {
		$user->redirect('index.php');
	} else {
		$error = "Wrong username or password!";
	}
}
?>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Login</title>
	<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
	<link rel="stylesheet" href="css/style.css" type="text/css"  />
</head>
<body>

<div class="signin-form">
	<center>
		<div class="container">
			<div class="col-md-4" style="float:none;">
				<center>
					<h1 class="heading">Git-R</h1><br/>
					<img src="img/logos/logo.png" style="width:150px;" />
				</center>
				<form class="form-signin" method="post" id="login-form">
					<h2 class="form-signin-heading">Log In.</h2><br/>
		        <div id="error">
		        <?php
							if(isset($error)) {
						?>
			        <div class="alert alert-danger">
			        	<i class="glyphicon glyphicon-warning-sign"></i> &nbsp; <?php echo $error; ?>
			        </div>
		        <?php
							}
						?>
		        </div>

		        <div class="form-group">
		        	<input type="text" class="form-control" name="in_username" placeholder="Username" required/>
		        	<span id="check-e"></span>
		        </div>
		        <div class="form-group">
		        	<input type="password" class="form-control" name="in_password" placeholder="Password" />
		        </div>
		     		<br/>
		        <div class="form-group">
			        <button type="submit" name="btn-login" class="btn btn-default">
			        	<i class="glyphicon glyphicon-log-in"></i> &nbsp; Log in
			        </button>
		        </div> <br/>

		        <label>Don't have an account yet? <a href="signup.php">Sign Up!</a></label>
				</form>
			</div>
		</div>
	</center>
</div>

</body>
</html>
