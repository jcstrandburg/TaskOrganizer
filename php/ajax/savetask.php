<?php
require_once( "../engine.php");

if ( isset( $_POST['TaskDesc'])) {
	$TaskName = $_POST['TaskName'];
	$TaskID = $_POST['TaskID'];
	$TaskDesc = $_POST['TaskDesc'];
	$TaskTime = $_POST['TaskTime'];
	
	$stmt = $dbconn->prepare( "UPDATE Tasks SET TaskName=?, TaskDesc=?, TaskTime=? WHERE TaskID=? AND UserID=?");
	$stmt->bind_param( "sssss", $TaskName, $TaskDesc, $TaskTime, $TaskID, $_SESSION['UserID']);
	$stmt->execute();
	$stmt->close();
}
else {
	header('HTTP/1.1 500 Internal Server Error (Missing POST Data)');
}

$stmt = $dbconn->prepare( "UPDATE Alerts SET AlertOffset=? WHERE AlertID=?");
foreach ($_POST as $key=>$value) {
$pattern = "/Offset(\d*)/";

	if ( preg_match( $pattern, $key, $matches) ) {
	
		$stmt->bind_param( "ss", $value, $matches[1]);
		$stmt->execute();
	}	
}
?>