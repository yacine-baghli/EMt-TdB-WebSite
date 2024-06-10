<?php
// page index
// L. Baghli 05/2019
	session_start();
	require_once("mysql_pdo.php");

//echo phpinfo();
	if( isSet($_GET['delog']) )	$_SESSION["_user"]='';

//	if (isset($_POST['_user'])) $_SESSION["$_user"]=$_POST['_user'];
	if (isset($_SESSION['_user'])) {
		 if ($_SESSION["_user"]=='')	$_user='';
			else	$_user=$_SESSION["_user"];
		}
	else $_user='';
	$user=$_user;

	if (isset($_POST['username'])) $username= $_POST["username"]; else $username="";
	if ($username == "")	if (isset($_GET['username'])) $username = $_GET["username"];
//	if ($username == "")	$username=0;

	if (isset($_POST['password'])) $password= $_POST["password"]; else $password="";
	if ($password == "")	if (isset($_GET['password'])) $password = $_GET["password"];
//	if ($password == "")	$password=0;

// verif username, password
	if ($username!="")	if ($password!="")
		{
//echo "Recherche :<br>";
	$stmt = $pdo->prepare("SELECT NAME FROM  users WHERE (NAME =? && PW = ? ) LIMIT 1");
	$stmt->execute([$username, $password]);
	$result = $stmt->fetchColumn();
	$stmt = null;
	if ($result)	{
//					echo "trouvé :";
							$_user=$result;
							$user =$_user;
							$_SESSION["_user"]=$user;
//				echo "session enregistrée: ".$_SESSION["_user"];
								}
	else $user="";
	}
//echo "passed :";

	if ($user=="")	include("index_login.php");
				else			include("index_connected.php");
?>
	</body>
</html>