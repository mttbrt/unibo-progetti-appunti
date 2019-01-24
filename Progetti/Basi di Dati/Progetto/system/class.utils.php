<?php
  class Utils {

    public function fileTreeBuilder($path) {
      if(file_exists($root.$path)) {
    		$files = scandir($root.$path);
    		natcasesort($files); // Sort an array using a case insensitive "natural order" algorithm
    		if(count($files) > 2) { // The 2 accounts for . and ..
    			echo "<ul class=\"fileTreeBody\" style=\"display: none;\">";
    			// List all dirs
    			foreach($files as $file) {
    				if(file_exists($root.$path.$file) && $file != '.' && $file != '..' && is_dir($root.$path.$file)) {
    					echo "<li class=\"directory collapsed\"><a href=\"#\" rel=\"".htmlentities($path.$file)."/\">".htmlentities($file)."</a></li>";
    				}
    			}
    			// List all files
    			foreach($files as $file) {
    				if(file_exists($root.$path.$file) && $file != '.' && $file != '..' && !is_dir($root.$path.$file)) {
    					$ext = preg_replace('/^.*\./', '', $file);
    					echo "<li class=\"file ext_$ext\"><a href=\"#\" rel=\"".htmlentities($path.$file)."\">".htmlentities($file)."</a></li>";
    				}
    			}
    			echo "</ul>";
    		}
    	}
    }

    public function getFileContent($path) {
      $path = $this->adaptPath($path);
      return file_get_contents($path);
    }

    public function createNewFile($path) {
      $path = $this->adaptPath($path);
      if (!file_exists($path)) {
        $response = file_put_contents($path, "");
        $this->givePermissionsToUrl($path);
        if($response !== false)
          return 'ok';
        else
          return 'no';
      }
    }

    public function createNewFolder($path) {
      $path = $this->adaptPath($path);
      if (!file_exists($path)) {
        $response = mkdir($path, 0777, true);
        $this->givePermissionsToUrl($path);
        if($response)
          return 'ok';
        else
          return 'no';
      }
    }

    public function renameFileorFolder($old, $new) {
      $old_path = $this->adaptPath($old);
      $new_path = $this->adaptPath($new);
      if (!file_exists($new_path)) {
        $response = rename($old_path, $new_path);
        if($response)
          return 'ok';
        else
          return 'no';
      }
    }

    public function deleteFileOrFolder($path) {
      $path = $this->adaptPath($path);
      $response = $this->delete($path);
      if($response)
        return 'ok';
      else
        return 'no';
    }

    public function setupWorkFolder($source, $path) {
      $path .= ".work/";
      if(!file_exists($path)) {
        $this->recursiveCopy($source, $path);
        $this->givePermissionsToUrl($path);
        return 'ok';
      } else
        return 'no';
    }

    public function saveFile($path, $content) {
      $path = $this->adaptPath($path);
      if (file_exists($path)) {
        $response = file_put_contents($path, $content);
        chmod($path, 0777);
        if($response !== false)
          return 'ok';
        else
          return 'no';
      }
    }

    public function setupUserEnvironment($username) {
      $this->createNewFolder('./Users/'.$username.'/projects');
      $this->givePermissionsToUrl('./Users/'.$username.'/projects/');
      $this->createNewFolder('./Users/'.$username.'/workbench');
      $this->givePermissionsToUrl('./Users/'.$username.'/workbench/');

      $this->givePermissionsToUrl('./Users/'.$username.'/');
    }

    public function setupProject($repo_url) {
      $this->createNewFolder($repo_url."master/.pushes/");
      $this->createNewFolder($repo_url."master/.v_0/");
      $this->createNewFile($repo_url."master/.v_0/README.md");
      $this->createNewFolder($repo_url."develop/.pushes/");
      $this->createNewFolder($repo_url."develop/.v_0/");
      $this->createNewFile($repo_url."develop/.v_0/README.md");
    }

    public function setupBranch($src, $url) {
      $this->createNewFolder($url.".v_0/");
      $this->recursiveCopy($src, $url.".v_0/");
    }

    public function joinBranch($src, $dst) {
      $this->recursiveCopy($src, $dst.".v_0/");
      $this->givePermissionsToUrl($dst.".v_0/");
      $this->recursiveCopy($dst.".v_0/", $dst.".work/");
      $this->givePermissionsToUrl($dst.".work/");
    }

    public function commitWork($url, $version) {
      $src_url = $url.".work/";
      $dst_url = $url.".v_".$version."/";
      $this->recursiveCopy($src_url, $dst_url);
      $this->givePermissionsToUrl($dst_url);
    }

    public function restoreVersion($url, $version, $public) {
      if($public === "false")
        $this->recursiveCopy($url.".v_".$version."/", $url.".work/");

      $next_version = $version + 1;
      $deletion_url = $url.".v_".$next_version."/";
      while (file_exists($deletion_url) && is_dir($deletion_url)) {
        $this->delete($deletion_url);
        $next_version++;
        $deletion_url = $url.".v_".$next_version."/";
      }
    }

    public function pendingPush($src_url, $version, $dst_url) {
      $src_url .= ".v_".$version."/";
      $dst_url .= ".pushes/";

      $this->createNewFolder($dst_url);
      $last_push = $this->getLastPushInQueue($dst_url);

      $next_push = $last_push + 1;
      $dst_url .= ".p_".$next_push."/";
      $this->recursiveCopy($src_url, $dst_url);
      $this->givePermissionsToUrl($dst_url);
    }

    public function performPush($src_url, $src_version, $dst_url, $dst_version) {
      $src_url .= ".v_".$src_version."/";
      $dst_url .= ".v_".$dst_version."/";

      $this->createNewFolder($dst_url);
      $this->recursiveCopy($src_url, $dst_url);
      $this->givePermissionsToUrl($dst_url);
    }

    public function getLastPushInQueue($dst_url) {
      $dirs = glob($dst_url.'.*', GLOB_ONLYDIR);
      $last_push = -1;
      for ($i = 0; $i < count($dirs); $i++) {
        $elements = explode("/", $dirs[$i]);
        $file = $elements[count($elements)-1];
        if($file !== "." && $file !== "..") {
          $subelem = explode("_", $file);
          $push_num = $subelem[count($subelem)-1];
          if($push_num > $last_push)
            $last_push = $push_num;
        }
      }
      return $last_push;
    }

    public function pushInNewVersion($url, $version, $push) {
      $src_url = $url.".pushes/.p_".$push."";
      $dst_url = $url.".v_".$version."/";
      $this->createNewFolder($dst_url);
      $this->givePermissionsToUrl($dst_url);
      $this->recursiveCopy($src_url, $dst_url);
      $this->delete($src_url);

      $last_push = $this->getLastPushInQueue($url.".pushes/");
      for ($i = $push + 1; $i <= $last_push; $i++) {
        $new_push = $i - 1;
        $this->renameFileorFolder($url.".pushes/.p_".$i, $url.".pushes/.p_".$new_push);
      }
    }

    public function removePush($url, $push) {
      $src_url = $url.".pushes/.p_".$push."";
      $this->delete($src_url);

      $last_push = $this->getLastPushInQueue($url.".pushes/");
      for ($i = $push + 1; $i <= $last_push; $i++) {
        $new_push = $i - 1;
        $this->renameFileorFolder($url.".pushes/.p_".$i, $url.".pushes/.p_".$new_push);
      }
    }

    private function delete($path) {
      if (is_dir($path)) {
        $files = array_diff(scandir($path), array('.', '..'));
        foreach ($files as $file)
          $this->delete(realpath($path).'/'.$file);
        return rmdir($path);
      } else if (is_file($path))
        return unlink($path);
      return false;
    }

    private function recursiveCopy($source, $destination) {
      $dir = @opendir($source);
      if (!file_exists($destination)) {
        @mkdir($destination, 0777, true);
        chmod($destination, 0777);
      }
      while(($file = readdir($dir)) !== false)
        if (($file != '.') && ($file != '..'))
          if (is_dir($source.'/'.$file))
            $this->recursiveCopy($source.'/'.$file, $destination.'/'.$file);
          else {
            copy($source.'/'.$file, $destination.'/'.$file);
            chmod($destination.'/'.$file, 0777);
          }
      closedir($dir);
    }

    private function givePermissionsToUrl($url) {
      $exploded = explode('/', $url);
      $suburl = $exploded[0]."/".$exploded[1]."/".$exploded[2]; // Start from ./Users/username
      for ($i = 3; $i < count($exploded); $i++) {
        $suburl .= "/".$exploded[$i];
        chmod($suburl, 0777);
      }
    }

    private function adaptPath($path) {
      //$path = "../".$path;
      return $path;
    }

  }
?>
