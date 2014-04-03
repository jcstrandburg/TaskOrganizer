<?php

require_once( "database.php");

function EchoSuccessResults( $results) {

	$x = array();
	$x["success"] = true;
	$x["results"] = $results;
	
	echo json_encode( $x);
}

function FailAndDie( $errorCode, $errorMessage) {

	$x = array();
	$x["success"] = false;
	$x["error-code"] = $errorCode;
	$x["error-message"] = $errorMessage;
	echo json_encode( $x);
	die();
}

$dberror = "DBERROR";
$posterror = "POSTERROR";
$dbconn = GetDBConnection();

?>