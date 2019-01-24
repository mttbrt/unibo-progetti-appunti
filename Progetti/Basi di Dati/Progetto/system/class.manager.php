<?php
	require_once('class.db.php');
	require_once('class.utils.php');
	require_once('class.comparison.php');

	class Manager {
		private $conn;
		private $utils;

		public function __construct() {
			$database = new Database();
			$this->utils = new Utils();
			$this->conn = $database->dbConnection();
	  }

		public function getUserProjects($username) {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM project WHERE creator=:username");
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				return $stmt->fetchAll(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getUserWorkingProjects($username) {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM access WHERE username=:username");
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				return $stmt->fetchAll(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getRepositoryByProjectName($name) {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM repository WHERE project_name=:name");
				$stmt->bindparam(":name", $name);
				$stmt->execute();
				return $stmt->fetchAll(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getRepositoryById($id) {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM repository WHERE id=:id");
				$stmt->bindparam(":id", $id);
				$stmt->execute();
				return $stmt->fetchAll(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getMasterById($id) {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM master WHERE repo_id=:id");
				$stmt->bindparam(":id", $id);
				$stmt->execute();
				return $stmt->fetchAll(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getDevelopById($id) {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM develop WHERE master_id=:id");
				$stmt->bindparam(":id", $id);
				$stmt->execute();
				return $stmt->fetchAll(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getFeaturesById($id) {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM feature_pub WHERE develop_id=:id");
				$stmt->bindparam(":id", $id);
				$stmt->execute();
				return $stmt->fetchAll(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getHotfixById($id) {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM hotfix_pub WHERE develop_id=:id");
				$stmt->bindparam(":id", $id);
				$stmt->execute();
				return $stmt->fetchAll(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getReleaseById($id) {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM release_pub WHERE develop_id=:id");
				$stmt->bindparam(":id", $id);
				$stmt->execute();
				return $stmt->fetchAll(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getUserPrivateFeatureBranches($feature_pub_id, $username) {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM feature_pri WHERE feature_pub_id=:feature_pub_id AND username=:username");
				$stmt->bindparam(":feature_pub_id", $feature_pub_id);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				return $stmt->fetchAll(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getUserPrivateHotfixBranches($hotfix_pub_id, $username) {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM hotfix_pri WHERE hotfix_pub_id=:hotfix_pub_id AND username=:username");
				$stmt->bindparam(":hotfix_pub_id", $hotfix_pub_id);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				return $stmt->fetchAll(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getUserPrivateReleaseBranches($release_pub_id, $username) {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM release_pri WHERE release_pub_id=:release_pub_id AND username=:username");
				$stmt->bindparam(":release_pub_id", $release_pub_id);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				return $stmt->fetchAll(PDO::FETCH_ASSOC);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getProjectBranches($project_name) {
			try {
				$branches = array();

				$stmt = $this->conn->prepare("CALL getProjectBranches(:proj_name, @release, @hotfix)");
				$stmt->bindparam(":proj_name", $project_name);
				$stmt->execute();
				$release = $this->conn->query("SELECT @release AS release_res")->fetch(PDO::FETCH_ASSOC);
				$hotfix = $this->conn->query("SELECT @hotfix AS hotfix_res")->fetch(PDO::FETCH_ASSOC);

				if($release['release_res'] == 1)
					array_push($branches, "Release");
				if($hotfix['hotfix_res'] == 1)
					array_push($branches, "Hotfix");

				$stmt = $this->conn->prepare("SELECT name FROM features_names");
				$stmt->execute();
				$res = $stmt->fetchAll(PDO::FETCH_ASSOC);

				foreach ($res as $feat)
					array_push($branches, $feat['name']);

				return json_encode($branches);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function getFeatureBranchVersion($repo_id, $name, $public, $username) {
			$stmt = $this->conn->prepare("CALL getFeatureBranchVersion(:repo_id, :name, :public, :username, @version)");
			$stmt->bindparam(":repo_id", $repo_id);
			$stmt->bindparam(":name", $name);
			$stmt->bindparam(":public", filter_var($public, FILTER_VALIDATE_BOOLEAN)); // convert string to boolean
			$stmt->bindparam(":username", $username);
			$stmt->execute();
			$row = $this->conn->query("SELECT @version AS version")->fetch(PDO::FETCH_ASSOC);
      return $row['version'];
		}

		public function getReleaseBranchVersion($repo_id, $public, $username) {
			$stmt = $this->conn->prepare("CALL getReleaseBranchVersion(:repo_id, :public, :username, @version)");
			$stmt->bindparam(":repo_id", $repo_id);
			$stmt->bindparam(":public", filter_var($public, FILTER_VALIDATE_BOOLEAN)); // convert string to boolean
			$stmt->bindparam(":username", $username);
			$stmt->execute();
			$row = $this->conn->query("SELECT @version AS version")->fetch(PDO::FETCH_ASSOC);
      return $row['version'];
		}

		public function getHotfixBranchVersion($repo_id, $public, $username) {
			$stmt = $this->conn->prepare("CALL getHotfixBranchVersion(:repo_id, :public, :username, @version)");
			$stmt->bindparam(":repo_id", $repo_id);
			$stmt->bindparam(":public", filter_var($public, FILTER_VALIDATE_BOOLEAN)); // convert string to boolean
			$stmt->bindparam(":username", $username);
			$stmt->execute();
			$row = $this->conn->query("SELECT @version AS version")->fetch(PDO::FETCH_ASSOC);
      return $row['version'];
		}

		public function getDevelopBranchVersion($repo_id) {
			$stmt = $this->conn->prepare("SELECT version FROM develop WHERE master_id=:repo_id");
			$stmt->bindparam(":repo_id", $repo_id);
			$stmt->execute();
			$result = $stmt->fetch(PDO::FETCH_ASSOC);
			return $result['version'];
		}

		public function getMasterBranchVersion($repo_id) {
			$stmt = $this->conn->prepare("SELECT version FROM master WHERE repo_id=:repo_id");
			$stmt->bindparam(":repo_id", $repo_id);
			$stmt->execute();
			$result = $stmt->fetch(PDO::FETCH_ASSOC);
			return $result['version'];
		}

		public function getPushesNum($repo_id, $name) {
			if($name == "Master") {
				$stmt = $this->conn->prepare("SELECT url FROM master WHERE repo_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);
				$dst_url = $result['url'].".pushes/";
				return $this->utils->getLastPushInQueue('.'.$dst_url);
			} else if($name == "Develop") {
				$stmt = $this->conn->prepare("SELECT url FROM develop WHERE master_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);
				$dst_url = $result['url'].".pushes/";
				return $this->utils->getLastPushInQueue('.'.$dst_url);
			} else if($name == "Release") {
				$stmt = $this->conn->prepare("SELECT url FROM release_pub WHERE develop_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);
				$dst_url = $result['url'].".pushes/";
				return $this->utils->getLastPushInQueue('.'.$dst_url);
			} else if($name == "Hotfix") {
				$stmt = $this->conn->prepare("SELECT url FROM hotfix_pub WHERE develop_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);
				$dst_url = $result['url'].".pushes/";
				return $this->utils->getLastPushInQueue('.'.$dst_url);
			} else {
				$stmt = $this->conn->prepare("SELECT url FROM feature_pub WHERE develop_id=:repo_id AND name=:name");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->bindparam(":name", $name);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);
				$dst_url = $result['url'].".pushes/";
				return $this->utils->getLastPushInQueue('.'.$dst_url);
			}
		}

		public function getPushFolder($repo_id, $name, $push_num) {
			$folder = "";
			if($name == "Master") {
				$stmt = $this->conn->prepare("SELECT * FROM master WHERE repo_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);
				$folder = $result['url'].".pushes/.p_".$push_num."/";
			} else if($name == "Develop") {
				$stmt = $this->conn->prepare("SELECT * FROM develop WHERE master_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);
				$folder = $result['url'].".pushes/.p_".$push_num."/";
			} else if($name == "Release") {
				$stmt = $this->conn->prepare("SELECT * FROM release_pub WHERE develop_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);
				$folder = $result['url'].".pushes/.p_".$push_num."/";
			} else if($name == "Hotfix") {
				$stmt = $this->conn->prepare("SELECT * FROM hotfix_pub WHERE develop_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);
				$folder = $result['url'].".pushes/.p_".$push_num."/";
			} else {
				$stmt = $this->conn->prepare("SELECT * FROM feature_pub WHERE develop_id=:repo_id AND name=:name");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->bindparam(":name", $name);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);
				$folder = $result['url'].".pushes/.p_".$push_num."/";
			}
			return $folder;
		}

		public function getProjectByName($name) {
			$stmt = $this->conn->prepare("SELECT * FROM project WHERE name=:name");
			$stmt->bindparam(":name", $name);
			$stmt->execute();
			return $stmt->fetch(PDO::FETCH_ASSOC);
		}

		public function getLogs() {
			$stmt = $this->conn->prepare("SELECT * FROM log ORDER BY id DESC");
			$stmt->execute();
			return $stmt->fetchAll(PDO::FETCH_ASSOC);
		}

		public function getAllProjects() {
			try {
				$stmt = $this->conn->prepare("SELECT * FROM project");
				$stmt->execute();
				$results = $stmt->fetchAll(PDO::FETCH_ASSOC);
				return json_encode($results);
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function createProject($username, $name) {
			try {
				$stmt = $this->conn->prepare("CALL createProject(:username, :name, :time, @url)");
				$stmt->bindparam(":username", $username);
				$stmt->bindparam(":name", $name);
				$stmt->bindparam(":time", round(microtime(true)*1000));
				$stmt->execute();
				$row = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
	      if(isset($row['url']) && $row['url'] != "") {
					$this->utils->setupProject('.'.$row['url']);
					return "ok";
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
    }

		public function deleteProject($username, $project_name) {
			try {
				$stmt = $this->conn->prepare("CALL deleteProject(:username, :proj_name, @url)");
				$stmt->bindparam(":username", $username);
				$stmt->bindparam(":proj_name", $project_name);
				$stmt->execute();
				$row = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
	      if(isset($row['url']) && $row['url'] != "") {
					$this->utils->deleteFileOrFolder(".".$row['url']);
					return "ok";
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
    }

		public function deleteBranchPub($develop_id, $branch, $name, $username) {
			try {
				$stmt = $this->conn->prepare("CALL deleteBranchPub(:develop_id, :branch, :name, :username, @url)");
				$stmt->bindparam(":develop_id", $develop_id);
				$stmt->bindparam(":branch", $branch);
				$stmt->bindparam(":name", $name);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				$row = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
	      if(isset($row['url']) && $row['url'] != "") {
					$this->utils->deleteFileOrFolder(".".$row['url']);
					return "ok";
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
    }

		public function deleteBranchPri($develop_id, $branch, $name, $username) {
			try {
				$stmt = $this->conn->prepare("CALL deleteBranchPri(:develop_id, :branch, :name, :username, @url)");
				$stmt->bindparam(":develop_id", $develop_id);
				$stmt->bindparam(":branch", $branch);
				$stmt->bindparam(":name", $name);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				$row = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
				if(isset($row['url']) && $row['url'] != "") {
					$this->utils->deleteFileOrFolder(".".$row['url']);
					return "ok";
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
    }

		public function createFeatureBranch($develop_id, $name, $notes, $username) {
			try {
				$stmt = $this->conn->prepare("CALL createFeatureBranch(:develop_id, :name, :notes, :username, @devurl, @setupurl, @project_name)");
				$stmt->bindparam(":develop_id", $develop_id);
				$stmt->bindparam(":name", $name);
				$stmt->bindparam(":notes", $notes);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				$dev = $this->conn->query("SELECT @devurl AS devurl")->fetch(PDO::FETCH_ASSOC);
				$url = $this->conn->query("SELECT @setupurl AS url")->fetch(PDO::FETCH_ASSOC);
				$proj_name = $this->conn->query("SELECT @project_name AS proj_name")->fetch(PDO::FETCH_ASSOC);
				if(isset($url['url']) && $url['url'] != "" && isset($dev['devurl']) && $dev['devurl'] != "" && isset($proj_name['proj_name']) && $proj_name['proj_name'] != "") {
					$this->utils->setupBranch('.'.$dev['devurl'], '.'.$url['url']); // TODO il $src è l'url del develop
					return $this->joinFeatureBranch($proj_name['proj_name'], $name, $username);
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function createReleaseBranch($develop_id, $notes, $username) {
			try {
				$stmt = $this->conn->prepare("CALL createReleaseBranch(:develop_id, :notes, :username, @devurl, @setupurl, @project_name)");
				$stmt->bindparam(":develop_id", $develop_id);
				$stmt->bindparam(":notes", $notes);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				$dev = $this->conn->query("SELECT @devurl AS devurl")->fetch(PDO::FETCH_ASSOC);
				$url = $this->conn->query("SELECT @setupurl AS url")->fetch(PDO::FETCH_ASSOC);
				$proj_name = $this->conn->query("SELECT @project_name AS proj_name")->fetch(PDO::FETCH_ASSOC);
				if(isset($url['url']) && $url['url'] != "" && isset($dev['devurl']) && $dev['devurl'] != "" && isset($proj_name['proj_name']) && $proj_name['proj_name'] != "") {
					$this->utils->setupBranch('.'.$dev['devurl'], '.'.$url['url']);
					return $this->joinReleaseBranch($proj_name['proj_name'], $username);
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function createHotfixBranch($develop_id, $notes, $username) {
			try {
				$stmt = $this->conn->prepare("CALL createHotfixBranch(:develop_id, :notes, :username, @masterurl, @setupurl, @project_name)");
				$stmt->bindparam(":develop_id", $develop_id);
				$stmt->bindparam(":notes", $notes);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				$mas = $this->conn->query("SELECT @masterurl AS masterurl")->fetch(PDO::FETCH_ASSOC);
				$url = $this->conn->query("SELECT @setupurl AS url")->fetch(PDO::FETCH_ASSOC);
				$proj_name = $this->conn->query("SELECT @project_name AS proj_name")->fetch(PDO::FETCH_ASSOC);
				if(isset($url['url']) && $url['url'] != "" && isset($mas['masterurl']) && $mas['masterurl'] != "" && isset($proj_name['proj_name']) && $proj_name['proj_name'] != "") {
					$this->utils->setupBranch('.'.$mas['masterurl'], '.'.$url['url']);
					return $this->joinHotfixBranch($proj_name['proj_name'], $username);
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function joinFeatureBranch($project_name, $name, $username) {
			try {
				$stmt = $this->conn->prepare("CALL joinFeatureBranch(:proj_name, :name, :username, @base_url, @join_url)");
				$stmt->bindparam(":proj_name", $project_name);
				$stmt->bindparam(":name", $name);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				$base_url = $this->conn->query("SELECT @base_url AS base_url")->fetch(PDO::FETCH_ASSOC);
				$join_url = $this->conn->query("SELECT @join_url AS join_url")->fetch(PDO::FETCH_ASSOC);
				if(isset($base_url['base_url']) && $base_url['base_url'] != "" && isset($join_url['join_url']) && $join_url['join_url'] != "") {
					$this->utils->joinBranch('.'.$join_url['join_url'], '.'.$base_url['base_url']);
					return "ok";
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function joinReleaseBranch($project_name, $username) {
			try {
				$stmt = $this->conn->prepare("CALL joinReleaseBranch(:proj_name, :username, @base_url, @join_url)");
				$stmt->bindparam(":proj_name", $project_name);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				$base_url = $this->conn->query("SELECT @base_url AS base_url")->fetch(PDO::FETCH_ASSOC);
				$join_url = $this->conn->query("SELECT @join_url AS join_url")->fetch(PDO::FETCH_ASSOC);
				if(isset($base_url['base_url']) && $base_url['base_url'] != "" && isset($join_url['join_url']) && $join_url['join_url'] != "") {
					$this->utils->joinBranch('.'.$join_url['join_url'], '.'.$base_url['base_url']);
					return "ok";
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function joinHotfixBranch($project_name, $username) {
			try {
				$stmt = $this->conn->prepare("CALL joinHotfixBranch(:proj_name, :username, @base_url, @join_url)");
				$stmt->bindparam(":proj_name", $project_name);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				$base_url = $this->conn->query("SELECT @base_url AS base_url")->fetch(PDO::FETCH_ASSOC);
				$join_url = $this->conn->query("SELECT @join_url AS join_url")->fetch(PDO::FETCH_ASSOC);
				if(isset($base_url['base_url']) && $base_url['base_url'] != "" && isset($join_url['join_url']) && $join_url['join_url'] != "") {
					$this->utils->joinBranch('.'.$join_url['join_url'], '.'.$base_url['base_url']);
					return "ok";
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function commitFeatureWork($project_name, $name, $username) {
			try {
				$stmt = $this->conn->prepare("CALL commitFeatureWork(:project_name, :name, :username, @url, @new_ver)");
				$stmt->bindparam(":project_name", $project_name);
				$stmt->bindparam(":name", $name);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				$url = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
				$ver = $this->conn->query("SELECT @new_ver AS new_ver")->fetch(PDO::FETCH_ASSOC);
				if(isset($url['url']) && $url['url'] != "" && isset($ver['new_ver']) && $ver['new_ver'] != "") {
					$this->utils->commitWork(".".$url['url'], $ver['new_ver']);
					return "ok";
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function commitReleaseWork($project_name, $username) {
			try {
				$stmt = $this->conn->prepare("CALL commitReleaseWork(:project_name, :username, @url, @new_ver)");
				$stmt->bindparam(":project_name", $project_name);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				$url = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
				$ver = $this->conn->query("SELECT @new_ver AS new_ver")->fetch(PDO::FETCH_ASSOC);
				if(isset($url['url']) && $url['url'] != "" && isset($ver['new_ver']) && $ver['new_ver'] != "") {
					$this->utils->commitWork(".".$url['url'], $ver['new_ver']);
					return "ok";
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function commitHotfixWork($project_name, $username) {
			try {
				$stmt = $this->conn->prepare("CALL commitHotfixWork(:project_name, :username, @url, @new_ver)");
				$stmt->bindparam(":project_name", $project_name);
				$stmt->bindparam(":username", $username);
				$stmt->execute();
				$url = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
				$ver = $this->conn->query("SELECT @new_ver AS new_ver")->fetch(PDO::FETCH_ASSOC);
				if(isset($url['url']) && $url['url'] != "" && isset($ver['new_ver']) && $ver['new_ver'] != "") {
					$this->utils->commitWork(".".$url['url'], $ver['new_ver']);
					return "ok";
				}
				return "no";
			} catch(PDOException $e) {
				echo $e->getMessage();
			}
		}

		public function restoreFeatureBranchVersion($repo_id, $name, $version, $public, $username) {
			$stmt = $this->conn->prepare("CALL restoreFeatureBranchVersion(:repo_id, :name, :version, :public, :username, @url)");
			$stmt->bindparam(":repo_id", $repo_id);
			$stmt->bindparam(":name", $name);
			$stmt->bindparam(":version", $version);
			$stmt->bindparam(":public", filter_var($public, FILTER_VALIDATE_BOOLEAN)); // convert string to boolean
			$stmt->bindparam(":username", $username);
			$stmt->execute();
			$row = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
      if(isset($row['url']) && $row['url'] != "") {
				$this->utils->restoreVersion('.'.$row['url'], $version, $public);
				return "ok";
			}
			return "no";
		}

		public function restoreReleaseBranchVersion($repo_id, $version, $public, $username) {
			$stmt = $this->conn->prepare("CALL restoreReleaseBranchVersion(:repo_id, :version, :public, :username, @url)");
			$stmt->bindparam(":repo_id", $repo_id);
			$stmt->bindparam(":version", $version);
			$stmt->bindparam(":public", filter_var($public, FILTER_VALIDATE_BOOLEAN)); // convert string to boolean
			$stmt->bindparam(":username", $username);
			$stmt->execute();
			$row = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
      if(isset($row['url']) && $row['url'] != "") {
				$this->utils->restoreVersion('.'.$row['url'], $version, $public);
				return "ok";
			}
			return "no";
		}

		public function restoreHotfixBranchVersion($repo_id, $version, $public, $username) {
			$stmt = $this->conn->prepare("CALL restoreHotfixBranchVersion(:repo_id, :version, :public, :username, @url)");
			$stmt->bindparam(":repo_id", $repo_id);
			$stmt->bindparam(":version", $version);
			$stmt->bindparam(":public", filter_var($public, FILTER_VALIDATE_BOOLEAN)); // convert string to boolean
			$stmt->bindparam(":username", $username);
			$stmt->execute();
			$row = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
      if(isset($row['url']) && $row['url'] != "") {
				$this->utils->restoreVersion('.'.$row['url'], $version, $public);
				return "ok";
			}
			return "no";
		}

		public function restoreDevelopBranchVersion($repo_id, $version) {
			$stmt = $this->conn->prepare("CALL restoreDevelopBranchVersion(:repo_id, :version, @url)");
			$stmt->bindparam(":repo_id", $repo_id);
			$stmt->bindparam(":version", $version);
			$stmt->execute();
			$row = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
      if(isset($row['url']) && $row['url'] != "") {
				$this->utils->restoreVersion('.'.$row['url'], $version, true);
				return "ok";
			}
			return "no";
		}

		public function restoreMasterBranchVersion($repo_id, $version) {
			$stmt = $this->conn->prepare("CALL restoreMasterBranchVersion(:repo_id, :version, @url)");
			$stmt->bindparam(":repo_id", $repo_id);
			$stmt->bindparam(":version", $version);
			$stmt->execute();
			$row = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
      if(isset($row['url']) && $row['url'] != "") {
				$this->utils->restoreVersion('.'.$row['url'], $version, true);
				return "ok";
			}
			return "no";
		}

		public function pushFeatureBranch($repo_id, $name, $public, $username, $creator) {
			if($public === "true") { // Merge to develop branch
				$stmt = $this->conn->prepare("SELECT * FROM feature_pub WHERE develop_id=:repo_id AND name=:name");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->bindparam(":name", $name);
				if($stmt->execute()) {
					$result1 = $stmt->fetch(PDO::FETCH_ASSOC);
					$stmt1 = $this->conn->prepare("SELECT * FROM develop WHERE master_id=:repo_id");
					$stmt1->bindparam(":repo_id", $repo_id);
					if($stmt1->execute()) {
						$result2 = $stmt1->fetch(PDO::FETCH_ASSOC);
						$this->addLogRow($username, "Merge", $name." -> Develop");
						if($username == $creator) {
							$new_version = $result2['version'] + 1;
							$stmt2 = $this->conn->prepare("UPDATE develop SET version=:version WHERE master_id=:repo_id"); // Increase version
							$stmt2->bindparam(":version", $new_version);
							$stmt2->bindparam(":repo_id", $repo_id);
							if($stmt2->execute()) {
								$this->utils->performPush('.'.$result1['url'], $result1['version'], '.'.$result2['url'], $new_version);
								return "ok";
							}
						} else {
							$this->utils->pendingPush('.'.$result1['url'], $result1['version'], '.'.$result2['url']);
							return "ok";
						}
					}
				}
				return "no";
			} else { // Push to public branch
				$stmt = $this->conn->prepare("SELECT * FROM feature_pri WHERE feature_pub_id=:repo_id AND feature_pub_name=:name AND username=:username");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->bindparam(":name", $name);
				$stmt->bindparam(":username", $username);
				if($stmt->execute()) {
					$result1 = $stmt->fetch(PDO::FETCH_ASSOC);
					$stmt1 = $this->conn->prepare("SELECT * FROM feature_pub WHERE develop_id=:repo_id AND name=:name");
					$stmt1->bindparam(":repo_id", $repo_id);
					$stmt1->bindparam(":name", $name);
					if($stmt1->execute()) {
						$result2 = $stmt1->fetch(PDO::FETCH_ASSOC);
						$this->addLogRow($username, "Push", "[pri] ".$name." -> [pub] ".$name);
						if($username == $creator) {
							$new_version = $result2['version'] + 1;
							$stmt2 = $this->conn->prepare("UPDATE feature_pub SET version=:version WHERE develop_id=:repo_id AND name=:name"); // Increase version
							$stmt2->bindparam(":version", $new_version);
							$stmt2->bindparam(":repo_id", $repo_id);
							$stmt2->bindparam(":name", $name);
							if($stmt2->execute()) {
								$this->utils->performPush('.'.$result1['url'], $result1['version'], '.'.$result2['url'], $new_version);
								return "ok";
							}
						} else {
							$this->utils->pendingPush('.'.$result1['url'], $result1['version'], '.'.$result2['url']);
							return "ok";
						}
					}
				}
				return "no";
			}
		}

		public function pushReleaseBranch($repo_id, $public, $username, $creator) {
			if($public === "true") { // Merge
				$stmt = $this->conn->prepare("SELECT * FROM release_pub WHERE develop_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				if($stmt->execute()) {
					$result1 = $stmt->fetch(PDO::FETCH_ASSOC);
					$stmt1 = $this->conn->prepare("SELECT * FROM develop WHERE master_id=:repo_id");
					$stmt1->bindparam(":repo_id", $repo_id);
					$stmt2 = $this->conn->prepare("SELECT * FROM master WHERE repo_id=:repo_id");
					$stmt2->bindparam(":repo_id", $repo_id);
					if($stmt1->execute() && $stmt2->execute()) {
						$result2 = $stmt1->fetch(PDO::FETCH_ASSOC);
						$result3 = $stmt2->fetch(PDO::FETCH_ASSOC);
						$this->addLogRow($username, "Merge", "Release -> Develop");
						$this->addLogRow($username, "Merge", "Release -> Master");
						if($username == $creator) {
							$new_version2 = $result2['version'] + 1;
							$new_version3 = $result3['version'] + 1;
							$stmt2 = $this->conn->prepare("UPDATE develop SET version=:version WHERE master_id=:repo_id"); // Increase version
							$stmt2->bindparam(":version", $new_version2);
							$stmt2->bindparam(":repo_id", $repo_id);

							$stmt3 = $this->conn->prepare("UPDATE master SET version=:version WHERE repo_id=:repo_id"); // Increase version
							$stmt3->bindparam(":version", $new_version3);
							$stmt3->bindparam(":repo_id", $repo_id);
							if($stmt2->execute() && $stmt3->execute()) {
								$this->utils->performPush('.'.$result1['url'], $result1['version'], '.'.$result2['url'], $new_version2);
								$this->utils->performPush('.'.$result1['url'], $result1['version'], '.'.$result3['url'], $new_version3);
								return "ok";
							}
						} else {
							$this->utils->pendingPush('.'.$result1['url'], $result1['version'], '.'.$result2['url']); // Merge to develop branch
							$this->utils->pendingPush('.'.$result1['url'], $result1['version'], '.'.$result3['url']); // Merge to master branch
							return "ok";
						}
					}
				}
				return "no";
			} else { // Push to public branch
				$stmt = $this->conn->prepare("SELECT * FROM release_pri WHERE release_pub_id=:repo_id AND username=:username");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->bindparam(":username", $username);
				if($stmt->execute()) {
					$result1 = $stmt->fetch(PDO::FETCH_ASSOC);
					$stmt1 = $this->conn->prepare("SELECT * FROM release_pub WHERE develop_id=:repo_id");
					$stmt1->bindparam(":repo_id", $repo_id);
					if($stmt1->execute()) {
						$result2 = $stmt1->fetch(PDO::FETCH_ASSOC);
						$this->addLogRow($username, "Push", "[pri] Release -> [pub] Release");
						if($username == $creator) {
							$new_version = $result2['version'] + 1;
							$stmt2 = $this->conn->prepare("UPDATE release_pub SET version=:version WHERE develop_id=:repo_id"); // Increase version
							$stmt2->bindparam(":version", $new_version);
							$stmt2->bindparam(":repo_id", $repo_id);
							if($stmt2->execute()) {
								$this->utils->performPush('.'.$result1['url'], $result1['version'], '.'.$result2['url'], $new_version);
								return "ok";
							}
						} else {
							$this->utils->pendingPush('.'.$result1['url'], $result1['version'], '.'.$result2['url']);
							return "ok";
						}
					}
				}
				return "no";
			}
		}

		public function pushHotfixBranch($repo_id, $public, $username, $creator) {
			if($public === "true") { // Merge
				$stmt = $this->conn->prepare("SELECT * FROM hotfix_pub WHERE develop_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				if($stmt->execute()) {
					$result1 = $stmt->fetch(PDO::FETCH_ASSOC);
					$stmt1 = $this->conn->prepare("SELECT * FROM develop WHERE master_id=:repo_id");
					$stmt1->bindparam(":repo_id", $repo_id);
					$stmt2 = $this->conn->prepare("SELECT * FROM master WHERE repo_id=:repo_id");
					$stmt2->bindparam(":repo_id", $repo_id);
					if($stmt1->execute() && $stmt2->execute()) {
						$result2 = $stmt1->fetch(PDO::FETCH_ASSOC);
						$result3 = $stmt2->fetch(PDO::FETCH_ASSOC);
						$this->addLogRow($username, "Merge", "Hotfix -> Develop");
						$this->addLogRow($username, "Merge", "Hotfix -> Master");
						if($username == $creator) {
							$new_version2 = $result2['version'] + 1;
							$new_version3 = $result3['version'] + 1;
							$stmt2 = $this->conn->prepare("UPDATE develop SET version=:version WHERE master_id=:repo_id"); // Increase version
							$stmt2->bindparam(":version", $new_version2);
							$stmt2->bindparam(":repo_id", $repo_id);

							$stmt3 = $this->conn->prepare("UPDATE master SET version=:version WHERE repo_id=:repo_id"); // Increase version
							$stmt3->bindparam(":version", $new_version3);
							$stmt3->bindparam(":repo_id", $repo_id);
							if($stmt2->execute() && $stmt3->execute()) {
								$this->utils->performPush('.'.$result1['url'], $result1['version'], '.'.$result2['url'], $new_version2);
								$this->utils->performPush('.'.$result1['url'], $result1['version'], '.'.$result3['url'], $new_version3);
								return "ok";
							}
						} else {
							$this->utils->pendingPush('.'.$result1['url'], $result1['version'], '.'.$result2['url']); // Merge to develop branch
							$this->utils->pendingPush('.'.$result1['url'], $result1['version'], '.'.$result3['url']); // Merge to master branch
							return "ok";
						}
					}
				}
				return "no";
			} else { // Push to public branch
				$stmt = $this->conn->prepare("SELECT * FROM hotfix_pri WHERE hotfix_pub_id=:repo_id AND username=:username");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->bindparam(":username", $username);
				if($stmt->execute()) {
					$result1 = $stmt->fetch(PDO::FETCH_ASSOC);
					$stmt1 = $this->conn->prepare("SELECT * FROM hotfix_pub WHERE develop_id=:repo_id");
					$stmt1->bindparam(":repo_id", $repo_id);
					if($stmt1->execute()) {
						$result2 = $stmt1->fetch(PDO::FETCH_ASSOC);
						$this->addLogRow($username, "Push", "[pri] Hotfix -> [pub] Hotfix");
						if($username == $creator) {
							$new_version = $result2['version'] + 1;
							$stmt2 = $this->conn->prepare("UPDATE hotfix_pub SET version=:version WHERE develop_id=:repo_id"); // Increase version
							$stmt2->bindparam(":version", $new_version);
							$stmt2->bindparam(":repo_id", $repo_id);
							if($stmt2->execute()) {
								$this->utils->performPush('.'.$result1['url'], $result1['version'], '.'.$result2['url'], $new_version);
								return "ok";
							}
						} else {
							$this->utils->pendingPush('.'.$result1['url'], $result1['version'], '.'.$result2['url']);
							return "ok";
						}
					}
				}
				return "no";
			}
		}

		public function pushDevelopBranch($repo_id, $username) {
			$stmt = $this->conn->prepare("SELECT * FROM develop WHERE master_id=:repo_id");
			$stmt->bindparam(":repo_id", $repo_id);
			if($stmt->execute()) {
				$result1 = $stmt->fetch(PDO::FETCH_ASSOC);
				$stmt1 = $this->conn->prepare("SELECT * FROM master WHERE repo_id=:repo_id");
				$stmt1->bindparam(":repo_id", $repo_id);
				if($stmt1->execute()) {
					$result2 = $stmt1->fetch(PDO::FETCH_ASSOC);
					$this->addLogRow($username, "Merge", "Develop -> Master");
					$new_version = $result2['version'] + 1;
					$stmt2 = $this->conn->prepare("UPDATE master SET version=:version WHERE repo_id=:repo_id"); // Increase version
					$stmt2->bindparam(":version", $new_version);
					$stmt2->bindparam(":repo_id", $repo_id);
					if($stmt2->execute()) {
						$this->utils->performPush('.'.$result1['url'], $result1['version'], '.'.$result2['url'], $new_version);
						return "ok";
					}
				}
			}
			return "no";
		}

		public function compareFiles($repo_id, $name, $path) {
			$base_path = "";
			if($name == "Master") {
				$stmt = $this->conn->prepare("SELECT * FROM master WHERE repo_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);

				$splitted = explode("/", $path);
				$index = array_search('.pushes', $splitted);
				$final_path = "";
				for ($i = $index + 2; $i < count($splitted); $i++)
					$final_path .= "/".$splitted[$i];

				$base_path = $result['url'].".v_".$result['version'].$final_path;
			} else if($name == "Develop") {
				$stmt = $this->conn->prepare("SELECT * FROM develop WHERE master_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);

				$splitted = explode("/", $path);
				$index = array_search('.pushes', $splitted);
				$final_path = "";
				for ($i = $index + 2; $i < count($splitted); $i++)
					$final_path .= "/".$splitted[$i];

				$base_path = $result['url'].".v_".$result['version'].$final_path;
			} else if($name == "Release") {
				$stmt = $this->conn->prepare("SELECT * FROM release_pub WHERE develop_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);

				$splitted = explode("/", $path);
				$index = array_search('.pushes', $splitted);
				$final_path = "";
				for ($i = $index + 2; $i < count($splitted); $i++)
					$final_path .= "/".$splitted[$i];

				$base_path = $result['url'].".v_".$result['version'].$final_path;
			} else if($name == "Hotfix") {
				$stmt = $this->conn->prepare("SELECT * FROM hotfix_pub WHERE develop_id=:repo_id");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);

				$splitted = explode("/", $path);
				$index = array_search('.pushes', $splitted);
				$final_path = "";
				for ($i = $index + 2; $i < count($splitted); $i++)
					$final_path .= "/".$splitted[$i];

				$base_path = $result['url'].".v_".$result['version'].$final_path;
			} else {
				$stmt = $this->conn->prepare("SELECT * FROM feature_pub WHERE develop_id=:repo_id AND name=:name");
				$stmt->bindparam(":repo_id", $repo_id);
				$stmt->bindparam(":name", $name);
				$stmt->execute();
				$result = $stmt->fetch(PDO::FETCH_ASSOC);

				$splitted = explode("/", $path);
				$index = array_search('.pushes', $splitted);
				$final_path = "";
				for ($i = $index + 2; $i < count($splitted); $i++)
					$final_path .= "/".$splitted[$i];

				$base_path = $result['url'].".v_".$result['version'].$final_path;
			}
			return Comparison::toTable(Comparison::compareFiles(".".$base_path, $path));
		}

		public function approvePush($repo_id, $name, $push) {
			$stmt = $this->conn->prepare("CALL approvePush(:repo_id, :name, @url, @version)");
			$stmt->bindparam(":repo_id", $repo_id);
			$stmt->bindparam(":name", $name);
			$stmt->execute();
			$url = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
			$version = $this->conn->query("SELECT @version AS version")->fetch(PDO::FETCH_ASSOC);
			if(isset($url['url']) && $url['url'] != "" && isset($version['version']) && $version['version'] != "") {
				$this->utils->pushInNewVersion('.'.$url['url'], $version['version'], $push);
				return "ok";
			}
			return "no";
		}

		public function refusePush($repo_id, $name, $push) {
			$stmt = $this->conn->prepare("CALL refusePush(:repo_id, :name, @url)");
			$stmt->bindparam(":repo_id", $repo_id);
			$stmt->bindparam(":name", $name);
			$stmt->execute();
			$url = $this->conn->query("SELECT @url AS url")->fetch(PDO::FETCH_ASSOC);
			if(isset($url['url']) && $url['url'] != "") {
				$this->utils->removePush('.'.$url['url'], $push);
				return "ok";
			}
			return "no";
		}

		private function addLogRow($username, $action, $element) {
			$stmt = $this->conn->prepare("CALL addLogRow(:username, :action, :element)");
			$stmt->bindparam(":username", $username);
			$stmt->bindparam(":action", $action);
			$stmt->bindparam(":element", $element);
			if($stmt->execute())
				return true;
			else
				return false;
		}

		private function grantAccessToRepo($username, $repo_id) {
			$stmt = $this->conn->prepare("CALL grantAccessToRepo(:username, :repo_id)");
			$stmt->bindparam(":username", $username);
			$stmt->bindparam(":repo_id", $repo_id);
			return $stmt->execute();
		}

	}
?>
