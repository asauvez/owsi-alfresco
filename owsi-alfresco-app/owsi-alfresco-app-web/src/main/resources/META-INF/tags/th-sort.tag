<%@ attribute name="id" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="label" required="false" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="code" required="false" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="message" required="false" rtexprvalue="true" type="org.springframework.context.MessageSourceResolvable" %>
<%@ attribute name="pagination" required="true" rtexprvalue="true" type="fr.openwide.alfresco.app.web.pagination.Pagination" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<th>
	<a href="${pagination.sortUri[id]}">
		<c:choose>
			<c:when test="${label ne null}">
				<c:out value="${label}"/>
			</c:when>
			<c:when test="${code ne null}">
				<spring:message code="${code}"/>
			</c:when>
			<c:when test="${message ne null}">
				<spring:message message="${message}"/>
			</c:when>
			<c:otherwise>
				<spring:message code="${id}"/>
			</c:otherwise>
		</c:choose>
		<c:if test="${pagination.sort.column eq id}">
			<c:choose>
				<c:when test="${pagination.sort.direction eq 'ASC'}">
					<span class="glyphicon glyphicon-sort-by-attributes"></span>
				</c:when>
				<c:when test="${pagination.sort.direction eq 'DESC'}">
					<span class="glyphicon glyphicon-sort-by-attributes-alt"></span>
				</c:when>
			</c:choose>
		</c:if>
	</a>
</th>