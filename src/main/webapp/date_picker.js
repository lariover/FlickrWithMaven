/**
 * Javascript based calendar for picking date
 */

$( function() {
  $( "#dateid" ).datepicker({
	    showOn: 'button',
	    buttonText: 'Calendar',
	    dateFormat: 'dd.mm.yy'
	});
} );