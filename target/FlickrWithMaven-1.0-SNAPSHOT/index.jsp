<!DOCTYPE html>
<html>
<head>
<!-- jcss stylesheet -->
<link rel="stylesheet" href="index.css">
<title>Flickr Form</title>
<!-- javascript for google maps GPS picker -->
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDPqoG2DlFp770EgTOUMxBjeY5lWAXaVaM&sensor=false" type="text/javascript"></script>
<script type="text/javascript">
	var initialized = 0;
    function load() 
    {
        var mapDiv = document.getElementById("map");
        var latlng = new google.maps.LatLng(50.08, 14.42);
        var mapOptions = 
        {
            zoom: 10,
            center:latlng,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
        };
        var map = new google.maps.Map(mapDiv, mapOptions);
        var marker;
        initialized = 1;
        
        google.maps.event.addListener(map, "rightclick", function(event) {
        	placeMarker(event.latLng);
            var lat = event.latLng.lat();
            var lng = event.latLng.lng();
            document.getElementById("latid").value = lat;
            document.getElementById("lngid").value = lng;
        });
        function placeMarker(location) {
        		if(marker)
        		{
        			marker.setPosition(location);
        		} else
        		{
                    marker = new google.maps.Marker({
                    position: location, 
                    map: map});        				
        		}
        }
    }
</script>

<!-- jquery for posting form data to java servlet "flickerForm" -->
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script>
    $(document).on("submit", "#flickerForm", function(event) {
    	var $form = $(this);
        $.post($form.attr("action"), $form.serialize(), function(response) {   // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response text...
            $("#answers").html(response);       // Locate HTML DOM element with ID "somediv" and set its text content with the response text.
        });
        <!-- event.preventDefault(); -->
    });
</script>

<!-- jquery for hide / show button -->
<script>
jQuery(document).ready(function(){
    jQuery('#hideshow').on('click', function(event) {
        jQuery('#map').toggle('show');
        if (initialized == 0) {
        	setTimeout( function() {load();}, 400);
        }
        var center = map.getCenter();
        google.maps.event.trigger(map, 'resize');
        map.setCenter(center); 
    });
});
</script>

<!-- jquery for calendar -->
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<script>
$( function() {
  $( "#dateid" ).datepicker({
	    showOn: 'button',
	    buttonText: 'Calendar',
	    dateFormat: 'dd.mm.yy'
	});
} );
</script>

</head>
<body> <!-- loads map when the page is loaded -->
<div id="container" style="width:100%;"> <!-- used for diving the page vertically -->                                  
	<!-- <div id="left" style="float:left; width:50%;">   -->                              
		<form id="flickerForm" name="flickerForm" method="post" action="./PhotoSearch">
			<table>
			  <tr>
			    <td></td>
			    <td>Query:</td> <!-- TODO prepinani tag/text -->
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
		
	    <div id="map" style="width:500px;height:500px;display:none"></div>
	    
	<!-- </div> -->
	<!-- <div id="right" style="float:right; width:50%;"> -->
		<div id="answers"></div> 
	<!-- </div> -->
</div>
</body>
</html>