<?php

if ( !isset( $TaskID)) {
	extract( $_POST);
}

echo "<h2>Task: $TaskName</h2>";
echo "<span class=TimeDisplay id='TimeDisplay$TaskID'>Occurs: ".FormatDateTime( $TaskTime)."</span><br>";
echo "<a href='' class='deletetask' id='$TaskID'>[Delete Task]</a> ";
echo "<a href='' class='details' id='$TaskID'>[View Details]</a>";

?>