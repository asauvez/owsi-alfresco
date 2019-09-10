<%@ include file="/WEB-INF/jsp/include/includes.jsp"%>

<c:forEach var="alert" items="${alerts.items}">
	<div class="alert alert-${'error' eq alert.type ? 'danger' : alert.type}">
		<a class="close" href="#" data-dismiss="alert">&times;</a>
		<p>
			<strong><spring:message code="${alert.message}" arguments="${alert.args}"/></strong>
			<c:if test="${alert.details}">
				<spring:message code="${alert.details}"/>
			</c:if>
		</p>
	</div>
</c:forEach>
