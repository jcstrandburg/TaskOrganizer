<?php
require_once( "../engine.php");

if ( isset( $_SESSION['UserID'])) {

	$query = "INSERT INTO Tasks (UserID, TaskName, TaskDesc, TaskTime) VALUES ('".$_SESSION['UserID']."', 'New Task', 'Description', NOW())";
	
	mysqli_query( $dbconn, $query);
	$query = "SELECT * FROM Tasks WHERE TaskID='".mysqli_insert_id( $dbconn)."'";
	$results = mysqli_query( $dbconn, $query);
	$row = mysqli_fetch_array( $results);
	echo json_encode( $row);
}
?>