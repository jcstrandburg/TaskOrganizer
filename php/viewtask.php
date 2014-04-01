<html>
<head>

<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js'></script>

<script type='text/javascript'>
var index = 0;

$(document).ready( function() { 
	
	$("#Add").click( function() {
		
		$("#Add").before( "Input "+index+": <input class=intext name="+index+" type=text id="+index+"></input><br>");
		$("#Add").before( "<select id=units"+index+"><option value=1>Minutes</option><option value=60>Hours</option><option value=1440>Days</option></select><br>");
		index++;
		//$("#Add").before( "Input "+index+": <input type=text></input><br>");
		//alert( "bob");
	});
	
	$("#Notify").click( function() {
		//alert( "notify");
		$(".intext").each( function() {
		var selid = "#units"+this.name;
		var comp = Number( this.value) * Number( $(selid).val());
		
			alert( this.name + " := "+comp);
		});
	});
});
</script>

</head>
<body>

<form name=form1>
<input type=button name=Add value="Add" id="Add">
<input type=button name=Notify value="Notify" id="Notify">
</form>

</body>
</html>