(function( $ ){
	$(function() {
		$('.form-query-container').each(function(n, item) {
			var scope = $(item);
			
			scope.find('.form-query input[type=submit]').click(function() {
				scope.find('.form-query .pagination-currentPage').val('1');
			});
			
			scope.find('.form-query-result .sort-column')
				.css('cursor', 'pointer')
				.click(function() {
				scope.find('.form-query .pagination-currentPage').val('1');
				scope.find('.form-query .pagination-sort-column').val($(this).data('sort-column'));
				scope.find('.form-query .pagination-sort-direction').val($(this).data('sort-direction'));
				scope.find('.form-query').submit();
				return false;
			});
			
			scope.find('.pagination li a').click(function() {
				if ($(this).parents('li.pageable').length != 0) {
					scope.find('.form-query .pagination-currentPage').val($(this).data('page'));
					scope.find('.form-query').submit();
				}
				return false;
			});
		});
	});
})( jQuery );
