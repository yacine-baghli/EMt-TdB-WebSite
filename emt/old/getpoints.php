<?php
// L. Baghli 11/2021
	require_once("mysql_pdo.php");

if (isset($_POST['id_releve'])) $id_releve= $_POST["id_releve"]; else $id_releve="";
if ($id_releve == "")	if (isset($_GET['id_releve'])) $id_releve = $_GET["id_releve"];
if ($id_releve == "")	$id_releve=0;

if (isset($_POST['limit'])) $lim= $_POST["limit"]; else $lim="";
if ($lim == "")	if (isset($_GET['limit'])) $lim = $_GET["limit"];
if ($lim == "")	$lim=50;

header('Content-Type: application/json; charset=utf-8');

//echo $id_releve;
$stmt = $pdo->prepare("SELECT p.* FROM points p LEFT JOIN releves r  ON (p.REF_RELEVE=r.REF) WHERE (r.ID=?) LIMIT ?");
$stmt->execute([$id_releve, $lim]);
$arr = $stmt->fetchAll(PDO::FETCH_ASSOC);
echo json_encode($arr);
$stmt = null;

