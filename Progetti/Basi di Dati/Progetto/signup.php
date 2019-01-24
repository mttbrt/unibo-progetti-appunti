<?php
session_start();
require_once('system/class.user.php');
require_once('system/class.utils.php');
$user = new User();
$utils = new Utils();

if($user->isLogged()) {
	$user->redirect('index.php');
}

if(isset($_POST['btn-signup'])) {
	$username = strip_tags($_POST['in_username']);
	$password = strip_tags($_POST['in_password']);

	if($username == "")	{
		$error[] = "Please, provide a username!";
	} else if($password == "")	{
		$error[] = "Please, provide a password!";
	} else if(strlen($password) < 6){
		$error[] = "Password must be at least 6 characters";
	} else {
		try {
			$row = $user->getUserData($username);
			if($row['username'] == $username) {
				$error[] = "Sorry username already taken!";
			} else {
				if($user->registerUser($username, $password)) {
					// Setup user environment
					$utils->setupUserEnvironment($username);
					$user->redirect('signup.php?joined');
				}
			}
		} catch(PDOException $e) {
			echo $e->getMessage();
		}
	}
}
?>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Signup</title>
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
				<form method="post" class="form-signin">
		  		<h2 class="form-signin-heading">Sign up.</h2><br/>
		      	<?php
							if(isset($error)) {
					 			foreach($error as $error) {
						?>
							<div class="alert alert-danger">
								<i class="glyphicon glyphicon-warning-sign"></i> &nbsp; <?php echo $error; ?>
							</div>
		        <?php
								}
							} else if(isset($_GET['joined'])) {
						?>
							<div class="alert alert-info">
							    <i class="glyphicon glyphicon-log-in"></i> &nbsp; Successfully registered <a href='login.php'>login</a> here
							</div>
						<?php
							}
						?>
		        <div class="form-group">
		        	<input type="text" class="form-control" name="in_username" placeholder="Username" value="<?php if(isset($error)){echo $uname;}?>" />
		        </div>
		        <div class="form-group">
		        	<input type="password" class="form-control" name="in_password" placeholder="Password" />
		        </div>
		        <div class="clearfix"></div><br/>
		        <div class="form-group">
		        	<button type="submit" class="btn btn-primary" name="btn-signup">
		        		<i class="glyphicon glyphicon-open-file"></i>&nbsp;Sign up
		          </button>
		        </div> <br/>
		        <label>Have an account? <a href="login.php">Log in!</a></label>
		    </form>
			</div>
		</div>
	</center>
</div>

</body>
</html>
