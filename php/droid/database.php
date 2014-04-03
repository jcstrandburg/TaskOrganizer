<?php

function GetDBConnection()
{
$dbname = "strandbu_events";
$dbuser = "strandbu_taskorg";
$dbpass = "~%nsiyV0.GmU";

	$conn = mysqli_connect( localhost, $dbuser, $dbpass, $dbname) or die( "Could not create db connection");
	
	return $conn;
}

?>