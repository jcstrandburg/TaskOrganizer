<?php
require_once( "../engine.php");

//$query = "DELETE FROM Tasks WHERE TaskID='".mysqli_real_escape_string( $dbconn, $_POST['TaskID']).
		//"' AND UserID='".$_SESSION['UserID']."'";
//mysqli_query( $dbconn, $query);

$stmt = $dbconn->prepare( "DELETE FROM Tasks WHERE TaskID=? AND UserID=?");
$stmt->bind_param( "ss", $_POST['TaskID'], $_SESSION['UserID']);
$stmt->execute();
?>