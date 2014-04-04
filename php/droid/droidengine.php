<?php

require_once( "database.php");

$dberror = "DBERROR";
$posterror = "POSTERROR";
$autherror = "AUTHENTICATION";

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

function Authenticate( $usr, $pass) {	
global $dbconn;

	$pass = md5( $pass);
	$query = "SELECT * FROM Users WHERE UserName='$usr' AND UserPass='$pass'";
	$result = mysqli_query( $dbconn, $query);
	
	if ( $row = mysqli_fetch_array( $result)) { 
		return $row['UserID'];
	}
	else {
		FailAndDie( $autherror, "Failed to authenticate");
	}
}

$dbconn = GetDBConnection();
$userID = Authenticate( $_POST["UserName"], $_POST["UserPass"]);


?>
