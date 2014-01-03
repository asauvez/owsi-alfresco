<%@ attribute name="pagination" required="true" rtexprvalue="true" type="fr.openwide.alfresco.component.query.search.model.PaginationParams" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${pagination.hasPagination}">
	<div class="pull-right">
		<ul class="pagination">
			<li class="${pagination.hasPreviousPage ? 'pageable' : 'disabled'}"><a href="#" data-page="${pagination.previousPage}">&laquo;</a></li>
			<c:forEach var="page" items="${pagination.pages}">
				<li class="${page eq pagination.currentPage ? 'active' : 'pageable'}"><a href="#" data-page="${page}">${page}</a></li>
			</c:forEach>
			<li class="${pagination.hasNextPage ? 'pageable' : 'disabled'}"><a href="#" data-page="${pagination.nextPage}">&raquo;</a></li>
		</ul>
	</div>
</c:if>
