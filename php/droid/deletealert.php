<?php

require_once( "droidengine.php");

if ( isset( $_POST["AlertID"]) ) {
	
	$alertID = $_POST["AlertID"];
	$query = "DELETE FROM Alerts WHERE AlertID='$alertID'";
	mysqli_query( $dbconn, $query);
	
	if ( mysqli_affected_rows( $dbconn) > 0 ) {
		
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