<?php

function GetDBConnection()
{
$dbname = "strandbu_events";
$dbuser = "strandbu_taskorg";
$dbpass = "~%nsiyV0.GmU";

	$conn = new mysqli( localhost, $dbuser, $dbpass, $dbname);
	if ( $conn->connect_error)
		die( $conn->connect_error);
	
	return $conn;
}

?>