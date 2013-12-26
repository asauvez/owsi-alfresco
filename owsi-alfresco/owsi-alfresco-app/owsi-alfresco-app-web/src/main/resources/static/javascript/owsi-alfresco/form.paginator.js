(function( $ ){
	$.extend($.fn, {
		paginator: function() {
			this.each(function() {
				var me = $(this);
				
				var contextualized = false;
				var contextWidgetName = "";
				var pageParam = "pageNumber";
				
				if (me.data("widget")) {
					contextWidgetName = me.data("widget");
					contextualized = true;
				}
				if (me.data("param")) {
					pageParam = me.data("param");
				}
				
				me.find("a").each(function () {
					var a = $(this);
					var regExp = new RegExp("^#([0-9]+)$"); // lien de la forme #23
					var href = a.attr("href");
					if (href.match(regExp)) {
						var page = href.substring(1); // on enleve le #
						var state = $.bbq.getState();
						if (contextualized) {
							if (!state[widgetName]) {
								state[widgetName] = {};
							}
							state[widgetName][pageParam] = page;
						} else {
							state[pageParam] = page;
						}
						var href = $.param.fragment(href, state, 2);
						a.attr("href", href);
					}
				});
			});
			// pour chainage jquery
			return this;
		}
	});
})( jQuery );