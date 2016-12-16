/**
 * javascript for pasting image description into the top div
 */

$(function() {
        	"use strict";
        	$(document).on('click', '#item', function(event) {
                event.preventDefault();
                event.stopPropagation();
        		var dataTarget = $(this).data('target'),
                    dataContent = $(this).data('content'),
                    target = $(dataTarget);
        		if (target.length > 0) {
                    target.html(dataContent);
        		}
        	});
        });