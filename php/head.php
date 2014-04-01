<html>
<head>
<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js'></script>
<script src="jquery.datetimepicker.js"></script>

<link rel="stylesheet" type="text/css" href="jquery.datetimepicker.css"/ >
<link rel="stylesheet" type="text/css" href="stylesheet.css"/ >

</head>
<body>

<div class=HeaderWrapper>
<h1>Task Organizer / Alert Manager</h1>

<?php
if ( LoggedIn() )
{
	echo "Logged in as: ".$_SESSION['UserName'];
	echo " | <a href=logout.php>Log Out</a><br>";
}
?>

</div>
<hr>