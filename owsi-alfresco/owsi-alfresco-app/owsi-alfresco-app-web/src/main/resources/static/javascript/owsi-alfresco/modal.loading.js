(function( $ ){
	$.fn.extend({
		loadingModal: function(options) {
			
			options = $.extend({
				hideTimeout: 800
			}, options);
			
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
					if (options.hideTimeout > 0) {
						setTimeout(function() {
							if (lastHideTrigger > lastShowTrigger && (new Date()).getTime() - lastShowTrigger > options.hideTimeout) {
								loadingModal.modal('hide');
								lastHideTrigger = -1;
							}
						}, options.hideTimeout);
					} else {
						loadingModal.modal('hide');
						lastHideTrigger = -1;
					}
				});
			});
			// chainage jquery
			return this;
		}
	});
})( jQuery );
