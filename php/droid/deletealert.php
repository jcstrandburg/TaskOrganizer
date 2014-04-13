<?php

require_once( "droidengine.php");

if ( isset( $_POST["AlertID"]) ) {
	
	$alertID = $_POST["AlertID"];
	$stmt = $dbconn->prepare( "DELETE FROM Alerts WHERE AlertID=?");
	$stmt->bind_param( "s", $alertID);
	if ( $stmt->execute()) {
		$result = array();
		$result["AlertID"] = $alertID;
		EchoSuccessResults( $result);
	}
	else {
		FailAndDie( $dberror, "Failed to delete alert");	
	}
}
else {
	FailAndDie( $posterror, "No alert id provided");
}


?>
