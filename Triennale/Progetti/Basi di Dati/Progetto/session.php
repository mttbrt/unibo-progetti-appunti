<?php
	session_start();
	require_once("system/class.user.php");
	$user = new User();
	if(!$user->isLogged())
		$user->redirect('login.php');
?>
