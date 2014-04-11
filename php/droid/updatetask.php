<?php

require_once( "droidengine.php");

if ( isset( $_POST["TaskID"]) ) {
	
	$taskID = $_POST["TaskID"];
	$taskName = $_POST["TaskName"];
	$taskDesc = $_POST["TaskDesc"];
	$taskTime = $_POST["TaskTime"];
	
	$query = "UPDATE Tasks SET TaskName='$taskName', TaskDesc='$taskDesc', TaskTime='$taskTime' WHERE TaskID='$TaskID'";
	
	mysqli_query( $dbconn, $query);
	
	if ( mysqli_affected_rows( $dbconn) > 0 ) {
		
		$result = array();
		$result["TaskID"] = $taskID;
		EchoSuccessResults( $result);
	}
	else {
		FailAndDie( $dberror, "Failed to update task");
	}
}
else {
	FailAndDie( $posterror, "No task id provided");
}

?>
