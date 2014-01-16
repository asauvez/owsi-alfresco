/**
 * Dépendence : jquery.ba-bbq.js
 */
(function( $ ){
	$.extend($.fn, {
		formPushState: function(options) {
			var form = $(this);
			
			// Options par defaut
			var contextualized = false; // permet d'avoir plusieurs formulaires de ce type sur une meme page
			
			if (form.data("widget")) {
				contextWidgetName = form.data("widget");
				contextualized = true;
			}
			
			if (options) {
				if (options.contextWidgetName) {
					contextWidgetName = options.contextWidgetName;
					contextualized = true;
				}
			}
			
			var state = $.bbq.getState();
			
			if (contextualized) {
				state[contextWidgetName] = {};
			} else {
				state = {};
			}
			
			form.find("input:not([type=submit]),select,textarea").each(function() {
				var field = $(this);
				var name = field.attr("name");
				var value = field.val();
				if (value != '') {
					if (contextualized) {
						state[contextWidgetName][name] = value;
					} else {
						state[name] = value;
					}
				}
			});
			
			if (contextualized) {
				$.bbq.pushState(state);
			} else {
				$.bbq.pushState(state, 2);
			}
		},
		formBindState: function(options) {
			this.each(function() {
				var form = $(this);
				var dirty = false;
				
				form.find("input:not([type=submit]),select,textarea").on("keydown.formBindState", function() {
					dirty = true;
				});
				
				form.find("input:not([type=submit]),select,textarea").on("change.formBindState", function() {
					dirty = true;
				});
				
				form.on("submit.formBindState", function() {
					dirty = false;
				});
				
				// Options par defaut
				var contextualized = false; // permet d'avoir plusieurs formulaires de ce type sur une meme page
				
				if (form.data("widget")) {
					contextWidgetName = form.data("widget");
					contextualized = true;
				}
				
				if (options) {
					if (options.contextWidgetName) {
						contextWidgetName = options.contextWidgetName;
						contextualized = true;
					}
				}
				
				$(window).on("hashchange.formBindState", function() {
					if (contextualized) {
						var state = $.bbq.getState()[contextWidgetName];
					} else {
						var state = $.bbq.getState();
					}
					if (state) {
						var submitNeeded = false;
						form.find("input:not([type=submit]),select,textarea").each(function() {
							var field = $(this);
							var name = field.attr("name");
							var fromState = state[name]?state[name]:"";
							if (field.val() != fromState) {
								submitNeeded = true;
								if (!dirty) { // on ne remplace pas le contenu des champs si l'utilisateur a commence a les modifier
									field.val(fromState);
									field.trigger("change");
									dirty = false; // dirty doit rester false malgre qu'on a mis a jour les champs
								}
							}
						});
					}
				});
			});
			// pour chainage jquery
			return this;
		}
	});
})( jQuery );