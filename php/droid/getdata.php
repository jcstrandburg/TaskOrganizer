<?php

require_once( "droidengine.php");

$query = "SELECT T.TaskName, T.TaskID, T.TaskTime, T.TaskDesc, (SELECT COUNT( A.AlertID) FROM Alerts A WHERE A.TaskID = T.TaskID) AS AlertCount FROM Tasks T";
$result = mysqli_query( $dbconn, $query) or FailAndDie();
$tasks = array();

while ( $r = mysqli_fetch_assoc( $result)) {

	$alerts = array();
	
	$query2 = "SELECT A.AlertID, A.AlertOffset FROM Alerts A WHERE A.TaskID='".$r["TaskID"]."'";
	$result2 = mysqli_query( $dbconn, $query2);
	while ( $q = mysqli_fetch_assoc( $result2) ) {
		$alerts[] = $q;
	}
	
	$r["Alerts"] = $alerts;
	$tasks[] = $r;
}

EchoSuccessResults( $tasks);
?>