<?php
	require_once('class.db.php');

	class User {
		private $conn;

		public function __construct() {
			$database = new Database();
			$this->conn = $database->dbConnection();
	  }

		public function registerUser($username, $password) {
			try {
				$new_password = password_hash($password, PASSWORD_DEFAULT);

				$stmt = $this->conn->prepare("INSERT INTO user(username, password) VALUES(:username, :password)");
				$stmt->bindparam(":username", $username);
				$stmt->bindparam(":password", $new_password);

				$stmt->execute();
				return $stmt;
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function loginUser($username, $password) {
			try {
				$stmt = $this->conn->prepare("SELECT username, password FROM user WHERE username=:username");
				$stmt->execute(array(':username'=>$username));
				$userRow = $stmt->fetch(PDO::FETCH_ASSOC);

				if($stmt->rowCount() == 1) {
					if(password_verify($password, $userRow['password'])) {
						$_SESSION['user_session'] = $userRow['username'];
						return true;
					} else {
						return false;
					}
				}
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getUserData($username) {
			try {
			  $stmt = $this->conn->prepare("SELECT username FROM user WHERE username=:username");
			  $stmt->execute(array(':username'=>$username));
			  return $stmt->fetch(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
			  echo $e->getMessage();
			}
		}

		public function isLogged() {
			if(isset($_SESSION['user_session'])) {
				return true;
			} else {
				return false;
			}
		}

		public function redirect($url) {
			header("Location: $url");
		}

		public function logoutUser() {
			session_destroy();
			unset($_SESSION['user_session']);
			return true;
		}
	}
?>
