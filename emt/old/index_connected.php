<script type="text/javascript" src="js/jquery-1.11.2.min.js"></script>
<link rel="stylesheet" href="jqwidgets/styles/jqx.base.css" type="text/css">
<!-- <link rel="stylesheet" href="jqwidgets/styles/jqx.classic.css" type="text/css"> -->
<link rel="stylesheet" href="jqwidgets/styles/jqx.darkblue.css" type="text/css">
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css"
      integrity="sha512-xodZBNTC5n17Xt2atTPuE1HxjVMSvLVW9ocqUKLsCC5CXdbqCmblAshOMAS6/keqq/sMZMZ19scR4PsZChSR7A=="
      crossorigin=""/>
<script type="text/javascript" src="jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="jqwidgets/globalization/globalize.js"></script>
<script type="text/javascript" src="jqwidgets/jqxradiobutton.js"></script>
<script type="text/javascript" src="jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="jqwidgets/jqxmenu.js"></script>
<script type="text/javascript" src="jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="jqwidgets/jqxcalendar.js"></script>
<script type="text/javascript" src="jqwidgets/jqxdatetimeinput.js"></script>

<script type="text/javascript" src="jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="jqwidgets/jqxgrid.js"></script>
<script type="text/javascript" src="jqwidgets/jqxgrid.sort.js"></script>
<script type="text/javascript" src="jqwidgets/jqxgrid.filter.js"></script>
<script type="text/javascript" src="jqwidgets/jqxgrid.columnsresize.js"></script>
<script type="text/javascript" src="jqwidgets/jqxgrid.selection.js"></script>

<!--<script type="text/javascript" src="jqwidgets/jqx-all.js"></script>-->

<!-- Make sure you put this AFTER Leaflet's CSS -->
<script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"
        integrity="sha512-XQoYMqMTK8LvdxXYG3nZ448hOEQiglfqkJs1NOQV44cWnUrBc8PkAOcXy20w0vlaXaVUearIOBhiXZ5V3ynxwA=="
        crossorigin=""></script>

<script type="text/javascript">
<!--
var refreshIntervalId;
var Accepted= new Array;
var id_releve_default=0;
var gid_pays=0, gid_region=0, gshortpays='';
var gorigin=0, gstatus=0, gperiode=0, gtype=0, gdth=0, gall=0;
var map_Long=-1.34, map_Lat=34.87;
var mymap, relevepath, markerStart, markerEnd, myLayerGroup;
 //------------------------------------------------------------
var source_releves =
{
	datatype: "json",
	datafields: [
		{ name: 'ID' },
        { name: 'REF' },
        { name: 'REF_DTH_DEBUT' },
		{ name: 'DTH_DEBUT' },
		{ name: 'DTH_FIN' },
		{ name: 'ID_VEHICLE' },
		{ name: 'ID_USER' }
	],
	url: 'getreleves.php'
};
//------------------------------------------------------------
function getPoints(id_releve) {
    $url = 'getpoints.php?id_releve=' + id_releve+'&limit='+$('#LimitField').val();
    $("#tx_selection").html($url );
    var source = {
        datatype: "json",
        datafields: [
            {name: 'ID', type: 'number'},
            {name: 'REF_RELEVE', type: 'number'},
            {name: 'TEMPS', type: 'string'},
            {name: 'VITESSEGPS', type: 'number'},
            {name: 'VITESSEMOY', type: 'number'},
            {name: 'INTENSITE', type: 'number'},
            {name: 'TENSION', type: 'number'},
            {name: 'ENERGIE', type: 'number'},
            {name: 'LATITUDE', type: 'number'},
            {name: 'LONGITUDE', type: 'number'},
            {name: 'ALTITUDE', type: 'number'},
            {name: 'DISTANCE', type: 'number'},
            {name: 'LAPS', type: 'number'},
            {name: 'DIRECTIONGPS', type: 'number'},
            {name: 'NBRSATGPS', type: 'number'},
            {name: 'DTHGPS', type: 'string'}
        ],
        url: $url,
        sortcolumn: 'DTHGPS',
        sortdirection: 'desc'
    };
    $("#tx_fichier").html('Chargement...');
    var dataAdapter = new $.jqx.dataAdapter(source, {
        loadComplete: function () {
            var records = dataAdapter.records;
            var length = records.length;
            $("#tx_fichier").html('loadComplete');
            if (length != 0) {
                var record = records[0];
                $("#tx_fichier").html('loadComplete  record ID='+record.ID);

            }
        }
    });
    // dataAdapter.dataBind();
    $("#jqxGridPoints").jqxGrid({source: dataAdapter, Columnsheight: 15,});
    //console.log("source: "+source);

}
//------------------------------------------------------------
function getPointsGPS(id_releve) {
    $url = 'getpointsgps.php?id_releve=' + id_releve;
    $("#tx_selection").html($url );
    var source = {
        datatype: "json",
        datafields: [
            //{name: 'ID', type: 'number'},
            {name: 'LONGITUDE', type: 'number'},
            {name: 'LATITUDE', type: 'number'},
            {name: 'DIRECTIONGPS', type: 'number'},
            {name: 'VITESSEGPS', type: 'number'},
            {name: 'DTHGPS', type: 'string'}
        ],
        url: $url
    };
   // $("#tx_fichier").html('getPointsGPS');
    var dataAdapter = new $.jqx.dataAdapter(source, {
        loadComplete: function () {
            var records = dataAdapter.records;
            var length = records.length;
            //$("#tx_fichier").html('getPointsGPS loadComplete');
            if (length != 0) {
                mymap.removeLayer(myLayerGroup);
                myLayerGroup = new L.layerGroup().addTo(mymap);
                map_Long = records[0].LONGITUDE;
                map_Lat  = records[0].LATITUDE;
                path = [];
                for (let i=0; i<length; i++)
                    if (records[i].LATITUDE !=0 && records[i].LONGITUDE != 0)
                        path.push([records[i].LATITUDE, records[i].LONGITUDE]);
                relevepath = L.polyline(path, {color: 'red'}).addTo(myLayerGroup);
                mymap.fitBounds(relevepath.getBounds());

                markerStart = L.marker([records[0].LATITUDE, records[0].LONGITUDE]);
                markerEnd = L.marker([records[length-1].LATITUDE, records[length-1].LONGITUDE]);
                markerStart.bindPopup("<b>Start</b>").openPopup().addTo(myLayerGroup);
                markerEnd.bindPopup("<b>End</b>").openPopup().addTo(myLayerGroup);
                console.log('getPointsGPS loadComplete  DTH='+records[0].DTHGPS);
            }
        }
    });
    dataAdapter.dataBind();
}

//------------------------------------------------------------
$(document).ready(function () {

    mymap = L.map('map').setView([map_Lat, map_Long], 13);
    L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
        attribution: '<a href="https://polytech-nancy.univ-lorraine.fr/la-formation-a-polytech-nancy/stages-et-projets/projet-eco-motion-team/">EMT</a>',
        maxZoom: 18,
        id: 'mapbox/streets-v11',
        tileSize: 512,
        zoomOffset: -1,
        accessToken: 'pk.eyJ1IjoiYmxvdGZpIiwiYSI6ImNrdmxrejV2YzE3NjAydXRrNDFuYWZxcGUifQ.9VDpEooSt1UcT42mDuYSOg'
    }).addTo(mymap);
    myLayerGroup = L.layerGroup().addTo(mymap);

    // prepare the data of pays
    var relevesdataAdapter = new $.jqx.dataAdapter(source_releves);
    $("#ddl_releves").jqxDropDownList(
    {
        source: relevesdataAdapter,
        theme: 'classic',
        width: 220,
        height: 25,
        selectedIndex: 0,
        displayMember: 'REF_DTH_DEBUT',
        valueMember: 'ID'
    });

    $("#jqxCBAutoReload").jqxCheckBox({ width: 120, height: 15});
    $("#jqxCBAutoReload").on('change', function (event) {
        var checked = $(this).val();
        clearInterval(refreshIntervalId);
        if (checked) {
            refreshIntervalId = setInterval( function(){
                getPoints(id_releve_default);
                getPointsGPS(id_releve_default);
            }  , 3000 );
        }
        else {
            clearInterval(refreshIntervalId);
        }
    });

    $("#LimitField").jqxInput({ placeHolder: "Enter text",  height: 20, width: 50,
        source: function (query, response) {

        }
    });
    $('#LimitField').val(50);
    $('#LimitField').keypress(function (e) {
        var key = e.which;
        if(key == 13)  getPoints(id_releve_default);	// the enter key code
    });

    $("#ddl_releves").on('bindingComplete', function (event) {
        $('#ddl_releves').jqxDropDownList('selectIndex', 0 );
        getPoints(1);
        getPointsGPS(1);
        id_releve_default =1;
        refreshIntervalId = setInterval( function(){
            getPoints(id_releve_default);
        }  , 5000 );
        $("#jqxCBAutoReload").jqxCheckBox({ checked: true });
    });

    $('#ddl_releves').on('select', function (event) {
        var args = event.args;
        var selectedrelevesitem = $('#ddl_releves').jqxDropDownList('getItem', args.index);
//            $("#tx_selection").html(selectedrelevesitem.value);
        getPoints(selectedrelevesitem.value);
        getPointsGPS(selectedrelevesitem.value);
        id_releve_default = selectedrelevesitem.value;
    });

    // le resultat dans une jqxGrid, qui est sans données au départ
    $("#jqxGridPoints").jqxGrid(
        {
//				source: dataAdapter,
            width: '1000px',
            height: '300px',
            columnsresize: true,
            filterable: true,
            sortable: true,
            columns: [
                { text: 'ID', datafield: 'ID', width: 60, cellsalign: 'center' },
                { text: 'RefRelevé', datafield: 'REF_RELEVE', width: 80, cellsalign: 'center' },
                { text: 'Temps', datafield: 'TEMPS', width: 100 },
                { text: 'Vitesse (km/h)', datafield: 'VITESSEGPS', width: 100 },
                { text: 'Vitesse Moyenne (km/h)', datafield: 'VITESSEMOY', width: 140 },
                { text: 'Intensité (A)', datafield: 'INTENSITE', width: 80 },
                { text: 'Tension (V)', datafield: 'TENSION', width: 80 },
                { text: 'Energie (J)', datafield: 'ENERGIE', width: 80 },
                { text: 'Latitude', datafield: 'LATITUDE', width: 90 },
                { text: 'Longitude', datafield: 'LONGITUDE', width: 90 },
                { text: 'Altitude', datafield: 'ALTITUDE', width: 90 },
                { text: 'Distance', datafield: 'DISTANCE', width: 90 },
                { text: 'Laps', datafield: 'LAPS', width: 100 },
                { text: 'DirGPS', datafield: 'DIRECTIONGPS', width: 60 },
                { text: 'NbrSatGPS', datafield: 'NBRSATGPS', width: 80 },
                { text: 'DthGPS', datafield: 'DTHGPS', width: 150 }
            ]
        });

});	// .ready


//-->
</script>

<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>
<html><!-- L BAGHLI 11/2021 -->
	<head>
        <title>EMT interface</title>
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
	</head>
	<body> 
	

<?php
	$stmt = $pdo->prepare("SELECT ID FROM users WHERE (NAME =?) LIMIT 1");
	$stmt->execute([$user]);
	$id_user = $stmt->fetchColumn();
	$stmt = null;
//	echo"debug : user :$user, ID :$id_user<br>";
//	echo"debug : date :".date("Y-m-d H:i:s")."<br>";
	$stmt = $pdo->prepare("UPDATE users SET LOGTIME = ? WHERE id = ?");
	$stmt->execute([date("Y-m-d H:i:s"), $id_user]);
	$stmt = null;
	$stmt = $pdo->prepare("INSERT INTO logs(DTH, MSG) VALUES (?, ?)");
	$stmt->execute([date("Y-m-d H:i:s"), "ID=".$id_user.", UserName=".$user]);
	$stmt = null;
?>
<div class='body'>
<center><font size="+2" color="">EMT</font></center>
<?php
	echo "<p id='delog'>";
	echo "Bienvenue <b>".$user."</b> <a href='?delog=1'> - Se déconnecter ?</a></p>";
//------------------------------------------------------	
?>
    Nombre de lignes du tableau :	<input type="text" id="LimitField"/>&nbsp;&nbsp;<div id="jqxCBAutoReload">Refresh</div>

    <div class="parent">
        Date du relevé :
        <div class="child_left_line" id="ddl_releves"></div>
        <span class="child_leftdecal_line">Sélection : </span>
        <div class="child_left_line" id="tx_selection"></div>
    </div>
<!---->
<!--    <div class="parparent_filter">-->
<!--        <div class="parent_filter">-->
<!--            Origine :-->
<!--            <div class="child_left_line" id="ddl_origin_filter"></div>-->
<!--            <span class="child_leftdecal_line">Statut : </span>-->
<!--            <div class="child_left_line" id="ddl_status_filter"></div>-->
<!--            <span class="child_leftdecal_line">Type : </span>-->
<!--            <div class="child_left_line" id="ddl_type_filter"></div>-->
<!--            <input class="child_right_line" type="button" value="Clear Filters" id="btclearfilters"/>-->
<!--        </div>-->
<!--        <div class="parent_filter">-->
<!--            Période :-->
<!--            <div class="child_leftdecal_line" id="ddl_periode_filter"></div>-->
<!--            <input class="child_leftdecal_line" id="bttoday" type="button" value="Aujourd'hui"/>-->
<!--            <div class="child_leftdecal_line" id="i_dth_filter"></div>-->
<!--        </div>-->
<!--    </div>-->

    <div class="parent" style="margin-top: 5px;">
        <div class="child_left_news">
            <strong>Points : </strong>
            <div class="child_leftdecal_line" id="tx_fichier"></div>
        </div>
    </div>

    <div id="jqxWidget" style="margin-top: 5px; font-size: 8px; font-family: Verdana; float: left;">
        <div id="jqxGridPoints" style="margin-top: 5px; font-size: 10px; font-family: Verdana; float: left;"></div>
    </div>
    <br>
    <div class="parent">
        ID :
        <div class="child_left_line" id="tx_ID"></div>
        <span class="child_leftdecal_line">IDN : </span>
        <div class="child_left_line" id="tx_IDN"></div>
    </div>

    <div id="map"></div>
      <br>
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
</div>