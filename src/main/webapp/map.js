/**
 * Uses GoogleMaps API for showing the clickable map for GPS coordinates
 * also includes the code for hide / show map button
 */

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