<?php

require_once( "droidengine.php");

foreach($_POST as $key => $value) {
  echo "$key => $value <br>\n";
}

echo "Authenticated with user id $userID<br>\n";



?>
