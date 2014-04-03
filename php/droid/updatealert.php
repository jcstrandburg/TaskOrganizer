<?php

require_once( "droidengine.php");

if ( isset( $_POST["AlertID"]) ) {
	
	$alertID = $_POST["AlertID"];
	$alertOffset = $_POST["AlertOffset"];
	
	$query = "UPDATE Alerts SET AlertOffset='$alertOffset' WHERE AlertID='$alertID'";
	
	mysqli_query( $dbconn, $query);
	
	if ( mysqli_affected_rows( $dbconn) > 0 ) {
		
		$result = array();
		$result["AlertID"] = $taskID;
		EchoSuccessResults( $result);
	}
	else {
		FailAndDie( $dberror, "Failed to update alert");
	}
}
else {
	FailAndDie( $posterror, "No alert id provided");
}



?>