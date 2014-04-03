<?php

require_once( "droidengine.php");

$query = "INSERT INTO Tasks (TaskName, TaskDesc, UserID) VALUES ('New Task', 'task description', 1)";
mysqli_query( $dbconn, $query);

$id = mysqli_insert_id( $dbconn);
$query = "SELECT * FROM Tasks WHERE TaskID='$id'";
$results = mysqli_query( $dbconn, $query) or FailAndDie( $dberror, "failed to select inserted task");
$r = mysqli_fetch_assoc( $results);
EchoSuccessResults( $r);


?>