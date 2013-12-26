/**
 * Dépendence : jquery.ba-bbq.js
 */
(function( $ ){
	$.extend($.fn, {
		tableSortable: function() {
			this.each(function() {
				var me = $(this);
				
				// Options par defaut
				var contextualized = false; // permet d'avoir plusieurs formulaires de ce type sur une meme page
				
				if (me.data("widget")) {
					contextWidgetName = me.data("widget");
					contextualized = true;
				}
				
				var sortedBy = me.data("sort-by");
				var sortedOrder = me.data("sort-order");
				me.find("th[data-column]").each(function() {
					var column = $(this);
					var columnId = column.data("column");
					var newState = $.bbq.getState();
					var widgetState;
					if (contextualized) {
						if (! newState[contextWidgetName]) {
							newState[contextWidgetName] = {};
						}
						widgetState = newState[contextWidgetName];
					} else {
						widgetState = newState;
					}
					
					widgetState["sort.property"] = columnId;
					var a = $(document.createElement("a"));
					if (columnId == sortedBy) {
						if (sortedOrder == "asc") {
							column.append(' <i class="glyphicon glyphicon-sort-by-attributes"></i>');
							widgetState["sort.direction"] = "desc";
						} else {
							column.append(' <i class="glyphicon glyphicon-sort-by-attributes-alt"></i>');
							widgetState["sort.direction"] = "asc";
						}
					} else {
						widgetState["sort.direction"] = "asc";
					}
					a.attr("href", "#" + $.param(newState));
					a.html(column.html());
					column.html(a);
				});
			});
			// chainage jquery
			return this;
		}
	});
})( jQuery );