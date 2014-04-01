<?php
	//this is a bit hackish, fix it later or something
	extract( $_POST);

	echo "<div class=Alert id=Alert".$AlertID.">";
	$offset = intval( $AlertOffset);
	if ( $offset % 1440 == 0 ) {
		$interval = 1440;
	}
	else if ( $offset % 60 == 0 ) {
		$interval = 60;
	}
	else {
		$interval = 1;
	}

	echo "<input type=hidden class=AlertID name='".$AlertID."' id=".$AlertID.">";
	echo "Alert ".$AlertID.": [<a href='javascript: DeleteAlert( $AlertID);' id=Delete".$AlertID.">Delete</a>] <input type=text id=Offset".$AlertID." value='".(-$AlertOffset/$interval)."'></input>";	

	echo "<select id=Interval".$AlertID.">";
	if ( $interval == 1 )
		echo "<option value=1 selected>Minutes</option>";
	else
		echo "<option value=1>Minutes</option>";
	if ( $interval == 60 )				
		echo "<option value=60 selected>Hours</option>";
	else
		echo "<option value=60>Hours</option>";
	if ( $interval == 1440 )			
		echo "<option value=1440 selected>Days</option>";
	else
		echo "<option value=1440>Days</option>";
	echo "</select> Before<br>";
	echo "</div>";
?>