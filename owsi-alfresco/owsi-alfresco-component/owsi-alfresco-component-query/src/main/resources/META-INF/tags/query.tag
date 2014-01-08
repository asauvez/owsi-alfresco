<%@ attribute name="query" required="true" rtexprvalue="true" type="fr.openwide.alfresco.component.query.search.model.AbstractFormQuery" %>
<%@ attribute name="name" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="result" required="true" rtexprvalue="true" type="fr.openwide.alfresco.component.query.form.result.FormQueryResult" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="formQuery" uri="http://app.alfresco.openwide.fr/tags/formQuery" %>

<div class="form-query-container">
	<c:choose>
		<c:when test="${not empty query.inputFieldBuilder.fieldSets}">
			<div class="pull-left well">
				<formQuery:form name="${name}" query="${query}"/>
			</div>
		</c:when>
		<c:otherwise>
			<formQuery:form name="${name}" query="${query}"/>
		</c:otherwise>
	</c:choose>
	<div class="pull-left">
		<formQuery:pagination pagination="${result.pagination}"/>
		<formQuery:result result="${result}">
			<jsp:doBody/>
		</formQuery:result>
		<formQuery:pagination pagination="${result.pagination}"/>
	</div>
</div>