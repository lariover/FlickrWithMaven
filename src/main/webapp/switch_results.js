/**
 * javascript for switching between ranked and unranked results
 */

window.onload=function(){document.getElementById('hideshow').value='Show ranked results';};
        jQuery(document).ready(function(){
            jQuery('#hideshow').on('click', function(event) {
                jQuery('#norank').toggle('show');
                jQuery('#rank').toggle('show');
                if(this.value == 'Show unranked results'){
                	this.value = 'Show ranked results';} else {
                	this.value = 'Show unranked results';};            
                }
            );
        });