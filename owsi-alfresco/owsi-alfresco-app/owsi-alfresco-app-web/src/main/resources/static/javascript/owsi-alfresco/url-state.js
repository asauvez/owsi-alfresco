/**
 * Dépendence : jquery.ba-bbq.js
 */
(function( $ ){
	$.extend($.fn, {
		bindState: function(options) {
			this.each(function() {
				var element = $(this);
				var currentState = null;
				
				// Options par defaut
				var contextualized = false; // permet d'avoir plusieurs formulaires de ce type sur une meme page
				var onUpdate = function() {};
				var url = element.data("url");
				var method = element.data("method");
				
				if (element.data("widget")) {
					contextWidgetName = element.data("widget");
					contextualized = true;
				}
				
				if (options) {
					if (options.contextWidgetName) {
						contextWidgetName = options.contextWidgetName;
						contextualized = true;
					}
					if (options.onUpdate) {
						onUpdate = options.onUpdate;
					}
				}
				
				var submitState = function(state) {
					$(window).trigger("showloading");
					$.ajax({
						url: url,
						method: method,
						dataType: "html",
						data: state
					}).done(function(html) {
						element.html($(html).find("#" + element.attr("id")).html());
						onUpdate(element);
					}).error(function(xhr) {
						alert(xhr.responseText); // TODO améliorer la gestion des erreurs
					}).always(function() {
						$(window).trigger("hideloading");
					});
					currentState = state;
				};
				
				$(window).on("hashchange.urlState", function() {
					if (contextualized) {
						var state = $.bbq.getState()[contextWidgetName];
					} else {
						var state = $.bbq.getState();
					}
					if (state) {
						var submitNeeded = false;
						if (currentState != null) {
							for (var name in currentState) {
								var currentValue = currentState[name];
								var newValue = state[name]?state[name]:"";
								if (currentValue != newValue) {
									submitNeeded = true;
								}
							}
							for (var name in state) {
								var currentValue = currentState[name]?currentState[name]:"";
								var newValue = state[name];
								if (currentValue != newValue) {
									submitNeeded = true;
								}
							}
						}
						if (currentState == null || submitNeeded) {
							submitState(state);
						}
					}
				});
			});
			// pour chainage jquery
			return this;
		}
	});
})( jQuery );