<?php
require_once( "../engine.php");

$query = "DELETE FROM Tasks WHERE TaskID='".mysqli_real_escape_string( $dbconn, $_POST['TaskID']).
		"' AND UserID='".$_SESSION['UserID']."'";
mysqli_query( $dbconn, $query);
?>