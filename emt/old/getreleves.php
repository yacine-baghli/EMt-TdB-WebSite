<?php
// L. Baghli 11/2021
	require_once("mysql_pdo.php");

if (isset($_POST['last'])) $last= $_POST["last"]; else $last="";
if ($last == "")	if (isset($_GET['last'])) $last = $_GET["last"];
if ($last == "")	$last=0;

header('Content-Type: application/json; charset=utf-8');
if ($last == "1")
	$stmt = $pdo->prepare("SELECT *, CONCAT(REF, ' ', DTH_DEBUT) AS REF_DTH_DEBUT FROM releves ORDER BY ID DESC LIMIT 1");
else
	$stmt = $pdo->prepare("SELECT *, CONCAT(REF, ' ', DTH_DEBUT) AS REF_DTH_DEBUT FROM releves");
	$stmt->execute();
	$arr = $stmt->fetchAll(PDO::FETCH_ASSOC);
	echo json_encode($arr);
	$stmt = null;

