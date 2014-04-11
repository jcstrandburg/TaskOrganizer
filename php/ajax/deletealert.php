<?php
require_once( "../engine.php");

$stmt = $dbconn->prepare( "DELETE Alerts FROM Alerts NATURAL JOIN Tasks WHERE AlertID=? AND UserID=?");
$stmt->bind_param( "ss", $_POST['AlertID'], $_SESSION['UserID']);
$stmt->execute();
?>