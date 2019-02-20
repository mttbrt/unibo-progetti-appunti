<?php
  require_once("class.utils.php");
  require_once("class.manager.php");
  require_once("class.comparison.php");
  $op = $_POST['op'];
  $utils = new Utils();
  $manager = new Manager();

  if(isset($op)) {
    switch ($op) {
      case 0: // Build File Tree
        $path = urldecode($_POST['path']);
        if(isset($path))
          echo $utils->fileTreeBuilder($path);
        break;
      case 1: // New File
        $path = $_POST['path'];
        if(isset($path))
          echo $utils->createNewFile($path);
        break;
      case 2: // New Folder
        $path = $_POST['path'];
        if(isset($path))
          echo $utils->createNewFolder($path);
        break;
      case 3: // Rename File
        $old = $_POST['old_name'];
        $new = $_POST['new_name'];
        if(isset($old) && isset($new))
          echo $utils->renameFileorFolder($old, $new);
        break;
      case 4: // Delete File or Folder
        $path = $_POST['path'];
        if(isset($path))
          echo $utils->deleteFileOrFolder($path);
        break;
      case 5: // Commit work
        $project_name = $_POST['project_name'];
        $branch = $_POST['branch'];
        $name = $_POST['name'];
        $username = $_POST['username'];
        if($branch == 1)
          echo $manager->commitFeatureWork($project_name, $name, $username);
        else if($branch == 2)
          echo $manager->commitReleaseWork($project_name, $username);
        else if($branch == 3)
          echo $manager->commitHotfixWork($project_name, $username);
        break;
      case 6: // Save file content
        $path = $_POST['path'];
        $content = $_POST['content'];
        if(isset($path))
          echo $utils->saveFile($path, $content);
        break;
      case 7: // Create new project
        $username = $_POST['username'];
        $name = $_POST['name'];
        echo $manager->createProject($username, $name);
        break;
      case 8: // Get all projects
        echo $manager->getAllProjects();
        break;
      case 9: // Get project branches
        $project_name = $_POST['project_name'];
        echo $manager->getProjectBranches($project_name);
        break;
      case 10: // Add branch
        $develop_id = $_POST['develop_id'];
        $branch = $_POST['branch'];
        $name = $_POST['name'];
        $notes = $_POST['notes'];
        $username = $_POST['username'];
        if($branch == 1)
          echo $manager->createFeatureBranch($develop_id, $name, $notes, $username);
        else if($branch == 2)
          echo $manager->createReleaseBranch($develop_id, $notes, $username);
        else if($branch == 3)
          echo $manager->createHotfixBranch($develop_id, $notes, $username);
        break;
      case 11: // Join branch
        $project_name = $_POST['project_name'];
        $username = $_POST['username'];
        $branch = $_POST['branch'];
        $name = $_POST['name'];
        if($branch == 1)
          echo $manager->joinFeatureBranch($project_name, $name, $username);
        else if($branch == 2)
          echo $manager->joinReleaseBranch($project_name, $username);
        else if($branch == 3)
          echo $manager->joinHotfixBranch($project_name, $username);
        break;
      case 12: // Add branch to another user project
        $project_name = $_POST['project_name'];
        $branch = $_POST['branch'];
        $name = $_POST['name'];
        $notes = $_POST['notes'];
        $username = $_POST['username'];
        $repo = $manager->getRepositoryByProjectName($project_name);
        $develop_id = $repo[0]['id'];
        if($branch == 1)
          echo $manager->createFeatureBranch($develop_id, $name, $notes, $username);
        else if($branch == 2)
          echo $manager->createReleaseBranch($develop_id, $notes, $username);
        else if($branch == 3)
          echo $manager->createHotfixBranch($develop_id, $notes, $username);
        break;
      case 13: // Restore version
        $project_name = $_POST['project_name'];
        $branch = $_POST['branch'];
        $name = $_POST['name'];
        $version = $_POST['version'];
        $public = $_POST['public'];
        $username = $_POST['username'];
        $repo = $manager->getRepositoryByProjectName($project_name);
        $repo_id = $repo[0]['id'];
        if($branch == 1)
          echo $manager->restoreFeatureBranchVersion($repo_id, $name, $version, $public, $username);
        else if($branch == 2)
          echo $manager->restoreReleaseBranchVersion($repo_id, $version, $public, $username);
        else if($branch == 3)
          echo $manager->restoreHotfixBranchVersion($repo_id, $version, $public, $username);
        else if($branch == 4)
          echo $manager->restoreDevelopBranchVersion($repo_id, $version);
        else if($branch == 5)
          echo $manager->restoreMasterBranchVersion($repo_id, $version);
        break;
      case 14: // Push/Merge
        $project_name = $_POST['project_name'];
        $branch = $_POST['branch'];
        $name = $_POST['name'];
        $public = $_POST['public'];
        $username = $_POST['username'];
        $repo = $manager->getRepositoryByProjectName($project_name);
        $repo_id = $repo[0]['id'];
        $proj = $manager->getProjectByName($project_name);
        $creator = $proj['creator'];
        if($branch == 1)
          echo $manager->pushFeatureBranch($repo_id, $name, $public, $username, $creator);
        else if($branch == 2)
          echo $manager->pushReleaseBranch($repo_id, $public, $username, $creator);
        else if($branch == 3)
          echo $manager->pushHotfixBranch($repo_id, $public, $username, $creator);
        else if($branch == 4)
          echo $manager->pushDevelopBranch($repo_id, $username);
        break;
      case 15: // Get pushes
        $project_name = $_POST['project_name'];
        $name = $_POST['name'];
        $repo = $manager->getRepositoryByProjectName($project_name);
        $repo_id = $repo[0]['id'];
        echo $manager->getPushesNum($repo_id, $name);
        break;
      case 16: // Get selected push folder
        $project_name = $_POST['project_name'];
        $name = $_POST['name'];
        $push_num = $_POST['push_num'];
        $repo = $manager->getRepositoryByProjectName($project_name);
        $repo_id = $repo[0]['id'];
        echo $manager->getPushFolder($repo_id, $name, $push_num);
        break;
      case 17: // Approve push
        $project_name = $_POST['project_name'];
        $name = $_POST['name'];
        $push = $_POST['push'];
        $repo = $manager->getRepositoryByProjectName($project_name);
        $repo_id = $repo[0]['id'];
        echo $manager->approvePush($repo_id, $name, $push);
        break;
      case 18: // Refuse push
        $project_name = $_POST['project_name'];
        $name = $_POST['name'];
        $push = $_POST['push'];
        $repo = $manager->getRepositoryByProjectName($project_name);
        $repo_id = $repo[0]['id'];
        echo $manager->refusePush($repo_id, $name, $push);
        break;
      case 19: // Delete project
        $project_name = $_POST['project_name'];
        $username = $_POST['username'];
        echo $manager->deleteProject($username, $project_name);
        break;
      case 20: // Delete public branch
        $develop_id = $_POST['develop_id'];
        $branch = $_POST['branch'];
        $name = $_POST['name'];
        $username = $_POST['username'];
        echo $manager->deleteBranchPub($develop_id, $branch, $name, $username);
        break;
      case 21: // Delete private branch
        $develop_id = $_POST['develop_id'];
        $branch = $_POST['branch'];
        $name = $_POST['name'];
        $username = $_POST['username'];
        echo $manager->deleteBranchPri($develop_id, $branch, $name, $username);
        break;
      case 22: // Get File Contents
        $path = $_POST['path'];
        if(isset($path))
          echo $utils->getFileContent($path);
        break;
      case 23: // Get project version
        $project_name = $_POST['project_name'];
        $branch = $_POST['branch'];
        $name = $_POST['name'];
        $public = $_POST['public'];
        $username = $_POST['username'];
        $repo = $manager->getRepositoryByProjectName($project_name);
        $repo_id = $repo[0]['id'];
        if($branch == 1)
          echo $manager->getFeatureBranchVersion($repo_id, $name, $public, $username);
        else if($branch == 2)
          echo $manager->getReleaseBranchVersion($repo_id, $public, $username);
        else if($branch == 3)
          echo $manager->getHotfixBranchVersion($repo_id, $public, $username);
        else if($branch == 4)
          echo $manager->getDevelopBranchVersion($repo_id);
        else if($branch == 5)
          echo $manager->getMasterBranchVersion($repo_id);
        break;
      case 24: // Comprarison
        $project_name = $_POST['project_name'];
        $name = $_POST['name'];
        $path = $_POST['path'];
        $repo = $manager->getRepositoryByProjectName($project_name);
        $repo_id = $repo[0]['id'];
        echo $manager->compareFiles($repo_id, $name, $path);
        break;
    }
  }
?>
