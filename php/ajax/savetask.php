<?php
require_once( "../engine.php");

if ( isset( $_POST['TaskDesc'])) {
	$TaskName = $_POST['TaskName'];
	$TaskID = $_POST['TaskID'];
	$TaskDesc = $_POST['TaskDesc'];
	$TaskTime = $_POST['TaskTime'];

	$query = "UPDATE Tasks SET TaskName='".$TaskName."', TaskDesc='".$TaskDesc.
			"', TaskTime='".$TaskTime."' WHERE TaskID='".$TaskID."' AND UserID='".
			$_SESSION['UserID']."'";
	mysqli_query( $dbconn, $query);
}
else {
	header('HTTP/1.1 500 Internal Server Error (Missing POST Data)');
}

echo( "Q: ($query)\n");

foreach ($_POST as $key=>$value) {
$pattern = "/Offset(\d*)/";

	if ( preg_match( $pattern, $key, $matches) ) {
	$query = "UPDATE Alerts SET AlertOffset=-$value WHERE AlertID='".mysqli_real_escape_string( $dbconn, $matches[1])."'";
	
		$result = mysqli_query( $dbconn, $query);		
		echo "R: ($query)\n";
	}	
}
?>