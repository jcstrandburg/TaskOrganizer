<?php

require_once( "droidengine.php");

if ( isset( $_POST["AlertID"]) ) {
	
	$alertID = $_POST["AlertID"];
	$alertOffset = $_POST["AlertOffset"];
	
	$stmt = $dbconn->prepare( "UPDATE Alerts SET AlertOffset=? WHERE AlertID=?");
	$stmt->bind_param( "ss", $alertOffset, $alertID);
	
	if ( $stmt->execute()) {
		
		$result = array();
		$result["AlertID"] = $alertID;
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
