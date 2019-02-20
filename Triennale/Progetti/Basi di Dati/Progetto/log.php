<?php
  require_once("system/class.manager.php");
  $manager = new Manager();

  $logs = $manager->getLogs();
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

	<body onload="setInterval(function() {window.location.reload();}, 8000);">
    <center>
      <div class="container">
        <h1>LOG</h1>
        <table class="table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Time</th>
              <th>User</th>
              <th>Action</th>
              <th>Element</th>
            </tr>
          </thead>
          <tbody>
            <?php
              foreach ($logs as $log) {
                if($log['action'] == "Create Project" || $log['action'] == "Create Branch" || $log['action'] == "New User")
                  echo '<tr class="success">';
                else if($log['action'] == "Delete Project" || $log['action'] == "Delete Branch" || $log['action'] == "Delete User")
                  echo '<tr class="danger">';
                else if($log['action'] == "Push" || $log['action'] == "Merge")
                  echo '<tr class="info">';
                else if($log['action'] == "Commit")
                  echo '<tr class="warning">';
                else if($log['action'] == "Fork Branch")
                  echo '<tr class="active">';
                else
                  echo '<tr>';

                echo "
                    <td>".$log['date']."</td>
                    <td>".$log['time']."</td>
                    <td>".$log['username']."</td>
                    <td>".$log['action']."</td>
                    <td>".$log['element']."</td>
                  </tr>
                ";
              }
            ?>
          </tbody>
        </table>
      </div>
    </center>
	</body>
</html>
