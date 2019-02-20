<?php
  exec('mysql -u user < "/var/www/html/Git-R/sql/db.sql"');
  header("Location: ../login.php"); 
?>
