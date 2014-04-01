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
	$query = "SELECT * FROM Users WHERE UserName='$usr' AND UserPass='$pass'";
	$result = mysqli_query( $dbconn, $query);
	
	//if we get a valid result back from the db authenticate them
	if ( $row = mysqli_fetch_array( $result))
	{
		$_SESSION['UserID'] = $row['UserID'];
		$_SESSION['UserName'] = $row['UserName'];
		$_SESSION['UserAuth'] = $row['UserAuth'];		
		
		return true;
	}
	else
	{
		return false;
	}
}

function FormatDateTime( $dt) {
	return date( "Y/m/d H:i", strtotime( $dt));
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