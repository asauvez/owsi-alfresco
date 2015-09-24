(function( $ ){
	$.fn.extend({
		submitOnce: function(options) {
			var options = $.extend({
				
				onSubmit: function(form) {
					form.data("submitted", true);
					form.find("button[type=submit]")
							.addClass("disabled")
							.addClass("submitting")
							.attr("disabled", "disabled");
				},
				
				afterSubmit: function(form) {
					form.data("submitted", false);
					form.find("button[type=submit]")
							.removeClass("disabled")
							.removeClass("submitting")
							.removeAttr("disabled");
				}
				
			}, options);
			
			this.each(function() {
				var form = $(this);
				form.data("submitted", false);
				form.on("submit.sumbitOnce", function(event) {
					if (form.data("submitted")) {
						event.preventDefault();
					} else {
						options.onSubmit(form);
					}
				});
				form.on("aftersubmit.sumbitOnce", function(event) {
					options.afterSubmit(form);
				});
			});
			// pour chainage jquery
			return this;
		}
	});
})( jQuery );
