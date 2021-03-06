<?php

function GetDBConnection()
{
$dbname = "strandbu_events";
$dbuser = "strandbu_taskorg";
$dbpass = "~%nsiyV0.GmU";

	$conn = mysqli_connect( localhost, $dbuser, $dbpass, $dbname) or die( "Could not create db connection");
	
	return $conn;
}

$dbconn = GetDBConnection();

$query = "SELECT T.TaskName, T.TaskID, T.TaskTime, T.TaskDesc, (SELECT COUNT( A.AlertID) FROM Alerts A WHERE A.TaskID = T.TaskID) AS AlertCount FROM Tasks T";
$result = mysqli_query( $dbconn, $query);
$rows = array();

$rows["success"] = true;
$rows["results"] = array();

while ( $r = mysqli_fetch_assoc( $result)) {

	$alerts = array();
	$query2 = "SELECT A.AlertID, A.AlertOffset FROM Alerts A WHERE A.TaskID='".$r["TaskID"]."'";
	//echo $query2 . "<br>";
	$result2 = mysqli_query( $dbconn, $query2);
	while ( $q = mysqli_fetch_assoc( $result2) ) {
		$alerts[] = $q;
	}
	
	$r["Alerts"] = $alerts;
	$rows["results"][] = $r;
	$rows[] = $r;
}

//echo "{ \"Tasks\": ";
echo json_encode( $rows);
//echo "}";
?>