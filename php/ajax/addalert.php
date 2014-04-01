<?php
require_once( "../engine.php");

if ( isset( $_POST['TaskID'])) {

	$query = "SELECT UserID FROM Tasks WHERE TaskID='".mysqli_real_escape_string( $dbconn, $_POST['TaskID'])."'";
	$results = mysqli_query( $dbconn, $query);
	$row = mysqli_fetch_array( $results);
	
	if ( $row['UserID'] == $_SESSION['UserID'] ) {
		$query = "INSERT INTO Alerts (TaskID, AlertOffset) VALUES (".$_POST['TaskID'].", 0)";
		mysqli_query( $dbconn, $query);
		$query = "SELECT * FROM Alerts WHERE AlertID='".mysqli_insert_id( $dbconn)."'";
		$results = mysqli_query( $dbconn, $query);
		$row = mysqli_fetch_array( $results);
		echo json_encode( $row);
	}
	else {
		header('HTTP/1.1 500 Internal Server Error (Permissions)');
	}
}
?>