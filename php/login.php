<?php
require_once( "engine.php");

if ( isset( $_POST['UserName']) && isset( $_POST['UserPass'])) 
{
	if ( Authenticate( $_POST['UserName'], $_POST['UserPass']))
	{
		LocalRedirect( "index.php");
	}
	else
	{
		$authFailed = true;	
		$UserName = $_POST['UserName'];
	}
}
else
{
	$UserName = "";
}

require( "head.php");

if ( $authFailed)
{
	echo "<font color=red>Authentication failed</font><br>";
}
?>

<form action=login.php method=post>
Login<br>
User Name: <input type=text name=UserName value="<?php echo $UserName?>"></input><br>
Password: <input type=password name=UserPass></input><br>
<input type=submit value="Login"></input>
</form>

<?php
require( "tail.php");
?>