(function( $ ){
	$(function() {
		$('.form-query-container').each(function(n, item) {
			var scope = $(item);
			
			scope.find(".form-query-result").bindState({
				form: scope.find(".form-query")
			});
		});
	});
})( jQuery );
