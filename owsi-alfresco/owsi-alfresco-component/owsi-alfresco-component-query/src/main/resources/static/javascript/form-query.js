(function( $ ){
	$(function() {
		$('.form-query-container').each(function(n, item) {
			var $container = $(item);
			$container.find('.form-query input[type=submit]').click(function() {
				$container.find('.form-query .pagination-currentPage').val('1');
			});
			
			$container.find('.form-query-result .sort-column')
				.css('cursor', 'pointer')
				.click(function() {
				$container.find('.form-query .pagination-currentPage').val('1');
				$container.find('.form-query .pagination-sort-column').val($(this).data('sort-column'));
				$container.find('.form-query .pagination-sort-direction').val($(this).data('sort-direction'));
				$container.find('.form-query').submit();
				return false;
			});
			
			$container.find('.pagination li a').click(function() {
				if ($(this).parents('li.pageable').length != 0) {
					$container.find('.form-query .pagination-currentPage').val($(this).data('page'));
					$container.find('.form-query').submit();
				}
				return false;
			});
		});
	});
})( jQuery );
