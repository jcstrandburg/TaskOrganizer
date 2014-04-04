<?php

require_once( "droidengine.php");

Authenticate( $_POST["UserName"], $_POST["UserPass"]);

foreach($_POST as $key => $value) {
  echo "$key => $value <br>\n";
}
?>