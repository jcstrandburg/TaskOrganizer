<?php

require_once( "droidengine.php");

$qry1 = "INSERT INTO Tasks (TaskName, TaskDesc, UserID, TaskTime) VALUES ('New Task', 'task description', ?, NOW())";

if ( !($stmt = $dbconn->prepare( $qry1)))
	FailAndDie( $dberror, $stmt->error);
if ( !($stmt->bind_param( "s", $userID))) 
	FailAndDie( $dberror, $stmt->error);
if ( !$stmt->execute() )
	FailAndDie( $dberror, $stmt->error);	
$stmt->close();

$id = mysqli_insert_id( $dbconn);

$stmt = $dbconn->prepare( "SELECT TaskID, TaskName, TaskDesc, TaskTime FROM Tasks WHERE TaskID=?");
$row = array();
$stmt->bind_param( "s", $id);
$stmt->bind_result( $row["TaskID"], $row["TaskName"], $row["TaskDesc"], $row["TaskTime"]);
$stmt->execute();
$stmt->fetch();
$stmt->close();


//$query = "SELECT * FROM Tasks WHERE TaskID='$id'";
//$results = mysqli_query( $dbconn, $query) or FailAndDie( $dberror, "failed to select inserted task");
//$r = mysqli_fetch_assoc( $results);
EchoSuccessResults( $row);


?>
