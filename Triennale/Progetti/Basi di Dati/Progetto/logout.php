<?php
require_once('session.php');
require_once('system/class.user.php');
$user = new User();

if($user->isLogged()) {
	$user->redirect('index.php');
}

if(isset($_GET['logout']) && $_GET['logout'] == "true") {
	$user->logoutUser();
	$user->redirect('login.php');
}
?>
