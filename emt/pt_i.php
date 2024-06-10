<?php

// insert pt record
// Yacine Baghli 05/2024
require_once("mysql_pdo.php");

if (isset($_POST["ref_releve"])) $ref_releve = $_POST["ref_releve"]; else $ref_releve = "";
if ($ref_releve == "") if (isset($_GET["ref_releve"])) $ref_releve = $_GET["ref_releve"];
if ($ref_releve == "") $ref_releve = 0;

if (isset($_POST["longitude"])) $longitude = $_POST{"longitude"}; else $longitude = "";
if ($longitude == "") if (isset($_GET["longitude"])) $longitude = $_GET{"longitude"};
if ($longitude == "") $longitude = 0;

if (isset($_POST["latitude"])) $latitude = $_POST{"latitude"}; else $latitude = "";
if ($latitude == "") if (isset($_GET["latitude"])) $latitude = $_GET{"latitude"};
if ($latitude == "") $latitude = 0;

if (isset($_POST["altitude"])) $altitude = $_POST{"altitude"}; else $altitude = "";
if ($altitude == "") if (isset($_GET["altitude"])) $altitude = $_GET{"altitude"};
if ($altitude == "") $altitude = 0;

if (isset($_POST["vitessegps"])) $vitessegps = $_POST{"vitessegps"}; else $vitessegps = "";
if ($vitessegps == "") if (isset($_GET["vitessegps"])) $vitessegps = $_GET{"vitessegps"};
if ($vitessegps == "") $vitessegps = 0;

if (isset($_POST["directiongps"])) $directiongps = $_POST{"directiongps"}; else $directiongps = "";
if ($directiongps == "") if (isset($_GET["directiongps"])) $directiongps = $_GET{"directiongps"};
if ($directiongps == "") $directiongps = 0;

if (isset($_POST["nbrsatgps"])) $nbrsatgps = $_POST{"nbrsatgps"}; else $nbrsatgps = "";
if ($nbrsatgps == "") if (isset($_GET["nbrsatgps"])) $nbrsatgps = $_GET{"nbrsatgps"};
if ($nbrsatgps == "") $nbrsatgps = 0;

if (isset($_POST["dthgps"])) $dthgps = $_POST{"dthgps"}; else $dthgps = "";
if ($dthgps == "") if (isset($_GET["dthgps"])) $dthgps = $_GET{"dthgps"};
if ($dthgps == "") $dthgps = "0000-00-00";;

if (isset($_POST["vitessemoy"])) $vitessemoy = $_POST{"vitessemoy"}; else $vitessemoy = "";
if ($vitessemoy == "") if (isset($_GET["vitessemoy"])) $vitessemoy = $_GET{"vitessemoy"};
if ($vitessemoy == "") $vitessemoy = 0;

if (isset($_POST["rpm"])) $rpm = $_POST{"rpm"}; else $rpm = "";
if ($rpm == "") if (isset($_GET["rpm"])) $rpm = $_GET{"rpm"};
if ($rpm == "") $rpm = 0;

if (isset($_POST["laps"])) $laps = $_POST{"laps"}; else $laps = "";
if ($laps == "") if (isset($_GET["laps"])) $laps = $_GET{"laps"};
if ($laps == "") $laps = 0;

if (isset($_POST["verbose"])) $verbose = $_POST{"verbose"}; else $verbose = "";
if ($verbose == "") if (isset($_GET["verbose"])) $verbose = $_GET{"verbose"};
if ($verbose == "") $verbose = 0;

if (isset($_POST["energie"])) $energie = $_POST["energie"]; else $energie = "";
if ($energie == "") {if (isset($_GET["energie"])) {$energie = $_GET["energie"];}}
if ($energie == "") $energie = 0;

if (isset($_POST["temps"])) $temps = $_POST["temps"]; else $temps = "";
if ($temps == "") {if (isset($_GET["temps"])) {$temps = $_GET["temps"];}}
if ($temps == "") $temps = "00";

if (isset($_POST["distance"])) $distance = $_POST["distance"]; else $distance = "";
if ($distance == "") {if (isset($_GET["distance"])) {$distance = $_GET["distance"];}}
if ($distance == "") $distance = 0;


if (isset($_POST["intensite"])) $intensite = $_POST["intensite"]; else $intensite = "";
if ($intensite == "") {if (isset($_GET["intensite"])) {$intensite = $_GET["intensite"];}}
if ($intensite == "") $intensite = 0;


if (isset($_POST["tension"])) $tension = $_POST["tension"]; else $tension = "";
if ($tension == "") {if (isset($_GET["tension"])) {$tension = $_GET["tension"];}}
if ($tension == "") $tension = 0;

date_default_timezone_set("Europe/Paris");
$dth = date("Y-m-d H:i:s");

if ($verbose) {
    echo '<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />';
    echo 'date:' . $dth . '<br>';

    $SS = "INSERT INTO points(REF_RELEVE,TEMPS,VITESSEGPS,VITESSEMOY,INTENSITE,TENSION,ENERGIE,LATITUDE,LONGITUDE,ALTITUDE,DISTANCE,LAPS,DIRECTIONGPS,NBRSATGPS,DTHGPS) VALUES "
        . "(" . $ref_releve . ",'" . $temps . "'," . $vitessegps . "," . $vitessemoy . "," . $intensite . "," . $tension . "," . $energie . "," . $latitude . "," .
        $longitude . "," . $altitude . "," . $distance . "," . $laps . "," . $directiongps . "," . $nbrsatgps .  ",'" . $dthgps . "');";
    echo "SS= " . $SS . "<br>";
}

$stmt = $pdo->prepare(" INSERT INTO points(REF_RELEVE,TEMPS,VITESSEGPS,VITESSEMOY,INTENSITE,TENSION,ENERGIE,LATITUDE,LONGITUDE,ALTITUDE,DISTANCE,LAPS,DIRECTIONGPS,NBRSATGPS,DTHGPS) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
$stmt->execute([$ref_releve, $temps, $vitessegps, $vitessemoy, $intensite, $tension, $energie, $latitude, $longitude, $altitude, $distance, $laps, $directiongps, $nbrsatgps, $dthgps]);
$stmt = null;