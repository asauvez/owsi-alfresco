(function( $ ){
	$.extend($.fn, {
		loadingModal: function() {
			var lastShowTrigger = -1;
			var lastHideTrigger = -1;
			this.each(function() {
				var loadingModal = $(this);
				$(window).on("showloading", function() {
					lastShowTrigger = (new Date()).getTime();
					loadingModal.modal('show');
				});
				$(window).on("hideloading", function() {
					lastHideTrigger = (new Date()).getTime();
					// Le timeout est important pour eviter les bugs d'affichage dans le cas ou les evnements sont
					// declenches de maniere rapprochee
					setTimeout(function() {
						if (lastHideTrigger > lastShowTrigger && (new Date()).getTime() - lastShowTrigger > 800) {
							loadingModal.modal('hide');
							lastHideTrigger = -1;
						}
					}, 800);
				});
			});
			// chainage jquery
			return this;
		}
	});
})( jQuery );