<?php

require_once( "droidengine.php");

if ( isset( $_POST["TaskID"]) ) {

	$taskID = $_POST["TaskID"];
	$query = "INSERT INTO Alerts (TaskID, AlertOffset) VALUES ('$taskID', '0')";
	mysqli_query( $dbconn, $query);

	$id = mysqli_insert_id( $dbconn);
	$query = "SELECT * FROM Alerts WHERE AlertID='$id'";
	$results = mysqli_query( $dbconn, $query) or FailAndDie( $dberror, "failed to select inserted task");
	$r = mysqli_fetch_assoc( $results);
	EchoSuccessResults( $r);
}
else {
	FailAndDie( $posterror, "No task id supplied");
}

?>