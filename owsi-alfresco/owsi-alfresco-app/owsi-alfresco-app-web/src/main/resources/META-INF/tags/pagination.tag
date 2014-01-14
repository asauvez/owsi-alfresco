<%@ attribute name="pagination" required="true" rtexprvalue="true" type="fr.openwide.alfresco.app.web.pagination.Pagination" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${pagination.hasPagination}">
	<ul class="pagination">
		<li class="${pagination.hasPreviousPage ? 'pageable' : 'disabled'}">
			<a href="${pagination.previousPageUri}" data-page="${pagination.previousPage}">&laquo;</a>
		</li>
		<c:forEach var="pageLink" items="${pagination.pageLinks}">
			<li class="${(pageLink.number eq null) ? 'disabled' : ((pageLink.link eq null) ? 'active' : 'pageable')}">
				<a href="${(pageLink.link ne null) ? pageLink.link : pagination.currentPageUrl}" data-page="${pageLink.number}">${pageLink.label}</a>
			</li>
		</c:forEach>
		<li class="${pagination.hasNextPage ? 'pageable' : 'disabled'}">
			<a href="${pagination.nextPageUri}" data-page="${pagination.nextPage}">&raquo;</a>
		</li>
	</ul>
</c:if>
