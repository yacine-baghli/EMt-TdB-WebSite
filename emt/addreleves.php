<?php

// insert pt record
// Yacine Baghli 05/2024
require_once("mysql_pdo.php");

if (isset($_POST['username'])) $username = $_POST["username"]; else $username = "";
if ($username == "") if (isset($_GET['username'])) $username = $_GET["username"];
//	if ($username == "")	$username=0;

if (isset($_POST['password'])) $password = $_POST["password"]; else $password = "";
if ($password == "") if (isset($_GET['password'])) $password = $_GET["password"];
//	if ($password == "")	$password=0;

if (isset($_POST["v_id"])) $v_id = $_POST["v_id"]; else $v_id = "";
if ($v_id == "") if (isset($_GET["v_id"])) $v_id = $_GET["v_id"];
if ($v_id == "") $v_id = 0;

if (isset($_POST["verbose"])) $verbose = $_POST{"verbose"}; else $verbose = "";
if ($verbose == "") if (isset($_GET["verbose"])) $verbose = $_GET{"verbose"};
if ($verbose == "") $verbose = 0;

date_default_timezone_set("Europe/Paris");

// verif username, password
if ($username != "") if ($password != "") {
//echo "Recherche :<br>";
    $stmt = $pdo->prepare("SELECT ID FROM  users WHERE (NAME =? && PW = ? ) LIMIT 1");
    $stmt->execute([$username, $password]);
    $result = $stmt->fetchColumn();
    $stmt = null;
    if ($result) {
//					echo "trouv� :";
        $id_user = $result;
        $stmt = $pdo->prepare("SELECT REF FROM releves  ORDER BY ID DESC LIMIT 1");
        $stmt->execute();
        $lastref = $stmt->fetchColumn();
        $stmt = null;
        $dth_yy = date("y");
        if (strcmp($dth_yy, substr($lastref, 0, 2)) == 0) {
//            if ($verbose) {echo 'date equal :' . $dth_yy . '<br>';
//            echo substr($lastref, 2, 3) . '<br>';}
            $nbr = substr($lastref, 2, 3) + 1;
            $newref = $dth_yy * 1000 + $nbr;
        } else  $newref = $dth_yy * 1000 + 1;
        if ($verbose) echo $newref . '<br>';
        $dth = date("Y-m-d H:i:s");

        $stmt = $pdo->prepare(" INSERT INTO releves ( REF, DTH_DEBUT, ID_VEHICULE, ID_USER) VALUES (?,?,?,?);");
        $stmt->execute([$newref, $dth, $v_id, $id_user]);
        $stmt = null;
    }
}

