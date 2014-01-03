(function( $ ){
	$(function() {
		$('.form-query').submit(function() {
			$(this).find('.pagination-currentPage').val('1');
		});
		
		$('.form-query-result .sort-column')
			.css('cursor', 'pointer')
			.click(function() {
			$('.form-query .pagination-sortColumn').val($(this).data('sort-column'));
			$('.form-query .pagination-sortDirection').val($(this).data('sort-direction'));
			$('.form-query').submit();
			return false;
		});
		
		$('.form-query-result-container .pagination li a').click(function() {
			if ($(this).parents('li.pageable').length != 0) {
				$('.form-query .pagination-currentPage').val($(this).data('page'));
				$('.form-query').submit();
			}
			return false;
		});
	});
})( jQuery );
