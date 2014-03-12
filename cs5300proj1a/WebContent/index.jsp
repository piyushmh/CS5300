<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<div>

<br/><br/>
<b> Server Message :</b><p id="servermessage"></p><br/>
<b>Cookie Expiration Time : </b><p id="cookieexptime"></p><br/>
<b>Cookie VersionNumber : </b><p id="cookieversionnumber"></p><br/>

<br/><br/>
<button type="button" id ="replace">Replace</button><input type="text" name="message" id="servermessageinput"/>
<br/>
<button type="button" id="refresh">Refresh</button>
<br/>
<button type="button" id="logout">Logout</button>
<br/>
</div>

<script src="js/jquery-2.1.0.min.js"></script>
<script src="js/jquery-ui-1.10.4.custom.min.js"></script>
<script type="text/javascript">

$(function(){
	$.ajax({
		url : "SessionManager",
		type : "GET",
		datatype : "text",
		data : {
			param : "pageload"
		},
		success : function( data, textstatus, jqXHR){
			
			var response = data.split("|");
			$("#servermessage").text(response[0]);
	    	$("#cookieexptime").text(response[1]);
			$("#cookieversionnumber").text(response[2]);
		},
		error : function( jqXHR, textstatus, errorThrown){
			alert(errorThrown);
		}
	});
});

$('#refresh').click(function(){
	$.ajax({
		url : "SessionManager",
		type : "GET",
		datatype : "text",
		data : {
			param : "refresh"
		},
		success : function( data, textstatus, jqXHR){
			
			var response = data.split("|");
			$("#servermessage").text(response[0]);
	    	$("#cookieexptime").text(response[1]);
			$("#cookieversionnumber").text(response[2]);
		},
		error : function( jqXHR, textstatus, errorThrown){
			alert(errorThrown);
		}
	});
});

$('#replace').click(function(){
	console.log("Here");
	var newmessage = $('#servermessageinput').val().trim();
	if( newmessage.length == 0 ){
		alert("Message text is empty");
	}else{
		$.ajax({
			url : "SessionManager",
			type : "GET",
			datatype : "text",
			data : {
				param : "replace",
				message : newmessage
			},
			success : function( data, textstatus, jqXHR){
				
				var response = data.split("|");
				$("#servermessage").text(response[0]);
		    	$("#cookieexptime").text(response[1]);
				$("#cookieversionnumber").text(response[2]);
			},
			error : function( jqXHR, textstatus, errorThrown){
				alert(errorThrown);
			}
		});	
	}
	
});

$('#logout').click(function(){
	$.ajax({
		url : "SessionManager",
		type : "GET",
		datatype : "text",
		data : {
			param : "logout"
		},
		success : function( data, textstatus, jqXHR){
			alert("You have been logged out!");	
			$("#servermessage").text("");
			$("#cookieexptime").text("");
			$("#cookieversionnumber").text("");
		},
		error : function( jqXHR, textstatus, errorThrown){
			alert(errorThrown);
		}
	});
});


</script>
</body>
</html>