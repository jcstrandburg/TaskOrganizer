<?php
require_once( "engine.php");

if ( !LoggedIn())
{
	LocalRedirect( "login.php");
}

require( "head.php");
?>

<script type='text/javascript'>
function DeleteAlert( id) {
	
	$.ajax({
		url: "ajax/deletealert.php",
		data: {"AlertID": id},
		type: "post",
		success: function( data) {
			$("#Alert"+id).remove();
		},
		error: function( data) { 
			alert( "Server error");
		},	
	});
}

function ExpandTask( id) {
var divid = "#Task"+id;

	event.preventDefault();
	$( "#"+id+".details").hide();
	
	$.ajax( {
		url:"ajax/details.php",
		type: "get",
		data: { "TaskID": id},
		success: function( data) { 
			
			$( divid).append( "<div id=ExpTask"+id+">"+data+"</div>");
			$( "#Add"+id).click( function() { AddAlert( id);});
			$( "#Save"+id).click( function() { SaveTask( id);});
			$( "#Revert"+id).click( function() { ShrinkTask( id);});
			
			$( ".datetimepicker").datetimepicker();
		},
		error: function() { alert( "it's broke"); $( "#"+id).show();},
	});
}

function AddAlert( id) {
var dat = { "TaskID": id};

	$.ajax( {
		url:"ajax/addalert.php",
		type: "post",
		data: dat,
		success: function( data) {
			var dat2 =  JSON.parse( data);
			
			$.ajax( {
				url: "ajax/viewalert.php",
				type: "post",
				data: dat2,
				success: function( data2) {
							$("#Add"+id).before( data2);
				},
				error: function( data2) {
					alert( "Server error: "+data2);
				},
			});
		},
		error: function( data) {
			alert( "Server error!");
		},
	});		
}

function ReloadTask( dat) {

}

function SaveTask( id) {
var DescID = "textarea#Desc"+id;
var dat = { "TaskName": $("#taskname"+id).val(), "TaskID": id, "TaskDesc": $("textarea#Desc"+id).val(), "TaskTime": $("#datetimepicker"+id).val() };

	$( "#ExpTask"+id+" > .Alert > .AlertID").each( function( ele, val){ 
		var offset = $("#Offset"+this.name).val();
		var interval = $("#Interval"+this.name).val();
		var total = parseInt( offset) * parseInt( interval);
		
		dat[ "Offset"+this.name] = total;
	});
	
	$.ajax( {
		url:"ajax/savetask.php",
		type: "post",
		data: dat,
		success: function( data) { 
		
			$("#Task"+id+" > h2").text( "Task: "+dat["TaskName"]);
			$("#Task"+id+" > .TimeDisplay").html( "Occurs: "+dat["TaskTime"]);
			ShrinkTask( id);
		},
		error: function() { alert( "Save failed");},
	});
}

function ShrinkTask( id) {
	$("#ExpTask"+id).remove();
	$( "#"+id+".details").show();	
}

function DeleteTask( id) {
	event.preventDefault();
	$.ajax( {
		url:"ajax/deletetask.php",
		type: "post",
		data: {"TaskID": id},
		success: function( data) {
			$('#Task'+id).remove();			
		},
		error: function( data) {
			alert( "Operation failed");
		},
	});
}

function NewTask() {

	event.preventDefault();
	$('.Tasks').append( "Beef");	
	$.ajax( {
		url:"ajax/addtask.php",
		type: "post",
		success: function( data) { 
			var postdat = JSON.parse( data);
			$.ajax( { 
				url: "ajax/viewtask.php",
				type: "post",
				data: postdat,
				success: function( data2) {
					$('#Tasks').append( "<div class=TaskContainer id=Task"+postdat["TaskID"]+">" + data2 + "</div>");
				},
				error: function( data2) {
					alert( "Operation failed");
				},
			});
		},
		error: function() { alert( "New task failed");},
	});
}

$(document).ready( function() {
	$('.details').click( function() { ExpandTask( this.id);});
	$('.deletetask').click( function() { DeleteTask( this.id);});
	$('#addtask').click( function() { NewTask(); });
});
</script>


<?php
$UserID = $_SESSION['UserID'];
$stmt = $dbconn->prepare( "SELECT TaskName, TaskID, TaskTime FROM Tasks WHERE UserID=?");
$stmt->bind_param( "s", $UserID);
$stmt->bind_result( $TaskName, $TaskID, $TaskTime );
$stmt->execute();

echo "<div id='Tasks'>";

while ( $stmt->fetch()) {

	echo "<div class=TaskContainer id=Task$TaskID>";
	include( "ajax/viewtask.php");
	echo "</div>";
}

echo "</div>";
echo "<div class='FooterWrapper'><input type=button value='Add A New Task' id='addtask'></input></div>";

require( "tail.php");
?>