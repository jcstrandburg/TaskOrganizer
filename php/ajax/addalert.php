<?php
require_once( "../engine.php");

if ( isset( $_POST['TaskID'])) {

	$stmt = $dbconn->prepare( "SELECT UserID FROM Tasks WHERE TaskID=?");
	$stmt->bind_param( "s", $_POST['TaskID']);
	$stmt->bind_result( $UserID);
	$stmt->execute();
	
	if ( $stmt->fetch() && $UserID == $_SESSION['UserID'] ) {

		$stmt->close();

		$stmt2 = $dbconn->prepare( "INSERT INTO Alerts (TaskID, AlertOffset) VALUES ( ?, 0)");
		$stmt2->bind_param( "s", $_POST['TaskID']);
		$stmt2->execute();
		$stmt2->close();
		
		$stmt3 = mysqli_prepare( $dbconn, "SELECT AlertID, AlertOffset FROM Alerts WHERE AlertID=?");
		echo $dbconn->error;
		$stmt3->bind_param( "s", mysqli_insert_id( $dbconn));
		$row = array();
		$stmt3->bind_result( $row['AlertID'], $row['AlertOffset']);
		$stmt3->execute();
		$stmt3->fetch();
		
		echo json_encode( $row);
	}
	else {
		header('HTTP/1.1 500 Internal Server Error (Permissions)');
	}
} 
else {
	header('HTTP/1.1 500 Internal Server Error (Insufficient post data)');
}
?>