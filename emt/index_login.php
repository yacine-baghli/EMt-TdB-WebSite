<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>
<html><!-- Yacine Baghli 05/2024 -->
	<head>
		<title>sInfo Admin interface</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<meta name="Robots" content="index, follow"/>
		<meta name="Owner" content="LB"/>
		<meta name="Language" content="FR"/>
		<meta name="Rating" content="General"/>
		<meta name="Distribution" content="Global"/>
		<meta name="Copyright" content="LB"/>
		<meta name="KeyWords" content="sInfo"/>
		<meta http-equiv="pragma" content="no-cache"/>
		<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
		<link rel="stylesheet" href="css/style.css" type="text/css" />
<body>

<script language="javascript" type="text/javascript">
<!--
function Login_event( formulaire)
{
	var formu = document.getElementById(formulaire);
	// verif username, password non vide
	if (formu.username.value!="")	if (formu.password.value!="")	formu.submit();
}
//----------------------------------------------------------------------------
function KeyDown_event( formulaire)
{
	if ((event.which && event.which == 13) || 
    (event.keyCode && event.keyCode == 13)) 
	{
	var formu = document.getElementById(formulaire);
	// verif username, password non vide (dans le main)
	if (formu.username.value!="")	if (formu.password.value!="")	formu.submit();
	}
}
//----------------------------------------------------------------------------
//-->
</script>

		<div ><!--HEADER PRINCIPAL -->
<hr> 
  
Non connecté<br>
Veuillez vous identifier :
<form id='formlogin' name='formlogin'  method='post' enctype='multipart/form-data'  action='index.php' >
	Nom : <input type='text' id='username' name='username' value='' size='20' onkeydown='KeyDown_event ("formlogin")' ><br>
	Mot de passe : <input type='password' id='password' name='password' value='' size='20'  onkeydown='KeyDown_event ("formlogin")' ><br>
	  <input id='btsubmit'  name='btsubmit' type='button' value='Entrer' onclick='Login_event ("formlogin")' />	

</form>
<?php
	if ($user!="")	echo "You are Loggued as <b>$user</b><br />"; 
	if ($user!="")	echo "<a href='?delog=1'>DELOG</a><br />"; 
?>




<hr>
Contact : 
	<script language="JavaScript">
	<!--
    var domain = "gmail.com";
    var name = "yacine.baghli";
    document.write('<a href=\"mailto:' + name + '@' + domain + '\">');
    document.write('Envoyer un email</a>');
	 -->
	</script>
<hr>
