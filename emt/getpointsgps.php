<?php
// Yacine Baghli 05/2024
require_once("mysql_pdo.php");

if (isset($_POST['id_releve'])) $id_releve= $_POST["id_releve"]; else $id_releve="";
if ($id_releve == "")	if (isset($_GET['id_releve'])) $id_releve = $_GET["id_releve"];
if ($id_releve == "")	$id_releve=0;

header('Content-Type: application/json; charset=utf-8');
//echo $id_releve;
$stmt = $pdo->prepare("SELECT p.ID, p.TEMPS, p.LONGITUDE, p.LATITUDE , p.ALTITUDE, p.DIRECTIONGPS, p.VITESSEGPS FROM points p LEFT JOIN releves r  ON (p.REF_RELEVE=r.REF) WHERE (r.ID=?)");
$stmt->execute([$id_releve]);
$arr = $stmt->fetchAll(PDO::FETCH_ASSOC);
echo(json_encode($arr));
$stmt = null;

