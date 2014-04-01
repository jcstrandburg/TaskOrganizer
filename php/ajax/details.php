<?php
require_once( "../engine.php");

if ( isset( $_GET['TaskID'])) {
	$id = mysqli_real_escape_string( $dbconn, $_GET['TaskID']);

	$query = "SELECT * FROM Tasks WHERE TaskID='$id' AND UserID='".$_SESSION['UserID']."'";
	$result = mysqli_query( $dbconn, $query);
	
	if ( $row = mysqli_fetch_array( $result)) {
			
		echo "Task name: <input id='taskname".$row['TaskID']."' type='text' value='".$row['TaskName']."'><br>";
		echo "Task time: <input class='datetimepicker' id='datetimepicker".$row['TaskID']."' type='text' value='".FormatDateTime( $row['TaskTime'])."'><br>";
		echo "Description:<br><textarea id=Desc$id>".$row['TaskDesc']."</textarea><br>";
		echo "<input type=hidden id=Time$TaskID value='".$row['TaskTime']."'></input>";
		
		$query = "SELECT AlertID, TaskTime, AlertOffset, DATE_ADD( TaskTime, INTERVAL AlertOffset MINUTE) AS AlertTime FROM Alerts NATURAL JOIN Tasks WHERE TaskID='$id'";
		$result = mysqli_query( $dbconn, $query);
		
		echo "<h3>Alerts:</h3>";
		
		while ( $row = mysqli_fetch_array( $result))
		{
			extract( $row);
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