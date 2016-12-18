<!DOCTYPE html>
<html>
<head>
<!-- jcss stylesheet -->
<link rel="stylesheet" href="index.css">
<title>Flickr Form</title>
<!-- javascript for google maps GPS picker -->
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDPqoG2DlFp770EgTOUMxBjeY5lWAXaVaM&sensor=false" type="text/javascript"></script>
<script type="text/javascript" src="map.js"></script>

<!-- jquery for calendar -->
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script src="date_picker.js"></script>

</head>
<body>
<div id="container">                             
		<!-- input form made with usage of tables -->
		<form id="flickerForm" name="flickerForm" method="post" action="flickerForm">
			<table>
			  <tr>
			    <td></td>
			    <td>Query:</td> 
			    <td><input type="text" name="query"/></td>
			    <td><input type="radio" name="searchtype" value="tag" checked>Tag</td>
			    <td><input type="radio" name="searchtype" value="fulltext">Fulltext</td>
			  </tr>
			  <tr>
			    <td rowspan="2"><input type="checkbox" name="gpscheck"></td>
			    <td>Latitude:</td>
			    <td><input type="text" name="latitude" id="latid" autocomplete="off"></td>
			    <td rowspan="2"><input type='button' id='hideshow' value='Map'></td>
			    <td rowspan="2"><input type="range" name="GPSprio" id="GPSprioid" value="50" min="0" max="100" oninput="GPSoutputid.value = GPSprioid.value/100">
    <output name="GPSoutput" id="GPSoutputid">0.5</output></td>
			  </tr>
			  <tr>
			    <td>Longitude:</td>
			    <td><input type="text" name="longitude" id="lngid" autocomplete="off"></td>
			  </tr>
			  <tr>
			    <td><input type="checkbox" name="datecheck"></td>
			    <td>Date:</td>
			    <td colspan="2"><input type="text" name="date" id="dateid" autocomplete="off"></td>
				<td> <input type="range" name="Dateprio" id="Dateprioid" value="50" min="0" max="100" oninput="Dateoutputid.value = Dateprioid.value/100">
    <output name="Dateoutput" id="Dateoutputid">0.5</output> </td>
			  </tr>
			  <tr>
			  	<td><input type="checkbox" name="likecheck"></td>
			    <td># of likes:</td>
			    <td colspan="2"><input type="text" name="likes" id="likesid"></td>
			    <td> <input type="range" name="Likesprio" id="likesprioid" value="50" min="0" max="100" oninput="likesoutputid.value = likesprioid.value/100">
    <output name="likesoutput" id="likesoutputid">0.5</output> </td>
			  </tr>
			  <tr>
                <td></td>      
                <td># threads:</td>
                <td colspan="2"><input type="text" name="threads" id="threads"></td>
                        
              </tr>
			</table>
		    <input type="submit" value="Submit" id="submitButton"/>
		</form>
		
		<!-- div with map used for picking gps goordinates -->
	    <div id="map" style="width:500px;height:500px;display:none"></div>
</div>
</body>
</html>