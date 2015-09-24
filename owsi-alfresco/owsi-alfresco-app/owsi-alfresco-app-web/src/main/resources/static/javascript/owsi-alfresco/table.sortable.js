/**
 * Dépendence : jquery.ba-bbq.js
 */
(function( $ ){
	$.fn.extend({
		tableSortable: function(options) {
			
			options = $.extend({
				asc: 'ASC',
				desc: 'DESC',
				ascSortedOrder: function(column, a) {
					column.append(' <i class="glyphicon glyphicon-sort-by-attributes"></i>');
				},
				descSortedOrder: function(column, a) {
					column.append(' <i class="glyphicon glyphicon-sort-by-attributes-alt"></i>');
				},
				doDisplay: function(me, column, newState, a) {
					a.attr("href", "#" + $.param(newState));
					a.html(column.html());
					column.html(a);
				}
			}, options);
			
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
						if (sortedOrder == options.asc) {
							options.ascSortedOrder(column, a);
							widgetState["sort.direction"] = options.desc;
						} else {
							options.descSortedOrder(column, a);
							widgetState["sort.direction"] = options.asc;
						}
					} else {
						widgetState["sort.direction"] = options.asc;
					}
					options.doDisplay(me, column, newState, a);
				});
			});
			// chainage jquery
			return this;
		}
	});
})( jQuery );