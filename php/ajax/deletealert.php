<?php
require_once( "../engine.php");

$query = "DELETE Alerts FROM Alerts NATURAL JOIN Tasks WHERE AlertID='".$_POST['AlertID'].
		"' AND UserID='".$_SESSION['UserID']."'";
echo $query;
mysqli_query( $dbconn, $query);
?>