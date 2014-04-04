<?php

require_once( "droidengine.php");

if ( isset( $_POST["TaskID"]) ) {
	
	$taskID = $_POST["TaskID"];
	$query = "DELETE FROM Tasks WHERE TaskID='$taskID'";
	mysqli_query( $dbconn, $query);
	
	if ( mysqli_affected_rows( $dbconn) > 0 ) {
		
		$result = array();
		$result["TaskID"] = $taskID;
		EchoSuccessResults( $result);
	}
	else {
		FailAndDie( $dberror, "Failed to delete task");
	}
}
else {
	FailAndDie( $posterror, "Insufficient post data");
}


?>
