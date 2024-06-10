<?php
// LB 05/2019

$options = [
  PDO::ATTR_EMULATE_PREPARES   => false, // turn off emulation mode for "real" prepared statements
  PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION, //turn on errors in the form of exceptions
  PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC, //make the default fetch be an associative array
//  PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8",
];

$GLOBALS["linux"] = PHP_OS == "Linux";
if ($GLOBALS["linux"])
{
	$GLOBALS["serveur"]= "localhost";
	$GLOBALS["base"]= "blofib_emt_bd";
	$GLOBALS["nom"]= "blofib_emt_db";
	$GLOBALS["passe"]= "VtywJo0hP5CRnHHr";
	$dsn = "mysql:host=embesystems.com;dbname=blofib_emt_bd;charset=utf8mb4";
	try {
	  $pdo = new PDO($dsn, "blofib_emt_db", "VtywJo0hP5CRnHHr", $options);
	} catch (Exception $e) {
	  echo($e->getMessage());
	  exit('Something weird happened!'); //something a user can understand
	}
}
else
{
	$GLOBALS["serveur"]= "localhost";
	$GLOBALS["base"]= "emt_db";
	$GLOBALS["nom"]= "root";
	$GLOBALS["passe"]= "";
	$dsn = "mysql:host=localhost;dbname=emt_db;charset=utf8mb4";
	try {
	  $pdo = new PDO($dsn, "root", "", $options);
	//	echo $dsn.'   ok';
	} catch (Exception $e) {
	  error_log($e->getMessage());
	  exit('Something very weird happened to Yacino'); //something a user can understand
	}
}

