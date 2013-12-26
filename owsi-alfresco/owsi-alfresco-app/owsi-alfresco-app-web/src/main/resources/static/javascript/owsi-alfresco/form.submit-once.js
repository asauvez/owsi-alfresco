(function( $ ){
	$.extend($.fn, {
		submitOnce: function() {
			this.each(function() {
				var form = $(this);
				form.data("submitted", false);
				form.on("submit.sumbitOnce", function(event) {
					if (form.data("submitted")) {
						event.preventDefault();
					} else {
						form.data("submitted", true);
						form.find("button[type=submit]")
								.addClass("disabled")
								.addClass("submitting")
								.attr("disabled", "disabled");
					}
				});
				form.on("aftersubmit.sumbitOnce", function(event) {
					form.data("submitted", false);
					form.find("button[type=submit]")
							.removeClass("disabled")
							.removeClass("submitting")
							.removeAttr("disabled");
				});
			});
			// pour chainage jquery
			return this;
		}
	});
})( jQuery );