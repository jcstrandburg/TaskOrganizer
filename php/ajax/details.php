<?php
require_once( "../engine.php");

if ( isset( $_GET['TaskID'])) {
	$id = $_GET['TaskID'];

	$stmt = $dbconn->prepare( "SELECT TaskID, TaskName, TaskDesc, TaskTime FROM Tasks WHERE TaskID=? AND UserID=?");
	$stmt->bind_param( "ss", $id, $_SESSION['UserID']);
	$stmt->execute();
	$stmt->bind_result( $TaskID, $TaskName, $TaskDesc, $TaskTime);

	if ( $stmt->fetch()){
		$stmt->close();

		echo "Task name: <input id='taskname".$TaskID."' type='text' value='".$TaskName."'><br>";
		echo "Task time: <input class='datetimepicker' id='datetimepicker".$TaskID."' type='text' value='".FormatDateTime( $TaskTime)."'><br>";
		echo "Description:<br><textarea id=Desc$id>".$TaskDesc."</textarea><br>";
		echo "<input type=hidden id=Time$TaskID value='".$TaskTime."'></input>";

		$query = "SELECT AlertID, AlertOffset FROM Alerts NATURAL JOIN Tasks WHERE TaskID=?";
		$stmt = $dbconn->prepare( $query);
		$stmt->bind_param( "s", $id);
		$stmt->bind_result( $AlertID, $AlertOffset);
		$stmt->execute();
		
		echo "<h3>Alerts:</h3>";
		
		while ( $stmt->fetch()) {
			include( "viewalert.php");		
		}
		
		echo "<input type=hidden class='Task".$TaskID."Alert' Value=".$row['AlertID'].">";
		echo "<input type=button id=Add$TaskID value='Add Alert'></input>";
		echo "<input type=button id=Save$TaskID value='Save Changes'></input>";
		echo "<input type=button id=Revert$TaskID value='Revert Changes'></input>";
	}
	else {
		header('HTTP/1.1 500 Internal Server Error (Invalid ID)');
	}
}
else {
	header('HTTP/1.1 500 Internal Server Error (Missing GET Data)');
}


?>