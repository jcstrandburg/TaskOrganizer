<?php

require_once( "droidengine.php");

if ( isset( $_POST["TaskID"]) ) {

	$taskID = $_POST["TaskID"];


	$stmt = $dbconn->prepare( "INSERT INTO Alerts (TaskID, AlertOffset) Values (?, '0')");
	$stmt->bind_param( "s", $taskID);
	$stmt->execute();
	$stmt->close();

	$id = mysqli_insert_id( $dbconn);
	
	$stmt = $dbconn->prepare( "SELECT AlertID, TaskID, AlertOffset FROM Alerts WHERE AlertID=?");
	$stmt->bind_param( "s", $id);
	$stmt->execute();
	$r = array();
	$stmt->bind_result( $r["AlertID"], $r["TaskID"], $r["AlertOffset"]);
	$stmt->fetch();
	
	EchoSuccessResults( $r);
}
else {
	FailAndDie( $posterror, "No task id supplied");
}

?>
