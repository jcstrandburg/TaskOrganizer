<?php
$FormatMinutes = 0;
$FormatHours = 1;
$FormatDays = 2;

function LocalRedirect( $addr) {
	header( "location: ".$addr);
	die();
}

function LoggedIn() {
	return isset( $_SESSION['UserID']);
}

function Authenticate( $usr, $pass) {	
global $dbconn;

	$pass = md5( $pass);

	$stmt = $dbconn->prepare( "SELECT UserID, UserName, Authority FROM Users WHERE UserName=? AND UserPass=?");
	$stmt->bind_param( "ss", $usr, $pass);
	$stmt->bind_result( $UserID, $UserName, $Authority);
	$stmt->execute();	
	
	//if we get a valid result back from the db authenticate them
	if ( $stmt->fetch() )
	{
		
		$_SESSION['UserID'] = $UserID;
		$_SESSION['UserName'] = $UserName;
		$_SESSION['UserAuth'] = $Authority;
		
		return true;
	}
	else
	{
		return false;
	}
}

function DeAuthenticate() {
	unset( $_SESSION['UserID']);
	unset( $_SESSION['UserName']);
	unset( $_SESSION['UserAuth']);
}

require_once( "database.php");
$dbconn = GetDBConnection();
session_start();

if ( LoggedIn()) {
global $UserID, $UserName, $UserAuth;

	$UserID = $_SESSION['UserID'];
	$UserName = $_SESSION['UserName'];
	$UserAuth = $_SESSION['UserAuth'];
}


?>