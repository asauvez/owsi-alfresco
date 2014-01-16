<%@ attribute name="query" required="true" rtexprvalue="true" type="fr.openwide.alfresco.component.query.search.model.AbstractFormQuery" %>
<%@ attribute name="name" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="result" required="true" rtexprvalue="true" type="fr.openwide.alfresco.component.query.form.result.FormQueryResult" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="formQuery" uri="http://app.alfresco.openwide.fr/tags/formQuery" %>
<%@ taglib prefix="alfapp"		uri="http://app.alfresco.openwide.fr/tags/util" %>

<div class="form-query-container clearfix">
	<div class="pull-left">
		<formQuery:form name="${name}" query="${query}"/>
	</div>
	
	<div id="${name}-result" class="pull-left panel panel-default form-query-result">
		<c:if test="${result ne null}">
		 	<div class="panel-heading clearfix">
				<div class="pull-right">
					<alfapp:pagination pagination="${result.pagination}"/>
				</div>
	
		 		<c:set var="queryLayout" value="result-header" scope="request"/>
		 		<jsp:doBody/>
			</div>
			 <div class="panel-body">
			 	<c:set var="queryLayout" value="result-body" scope="request"/>
				<formQuery:result result="${result}">
					<jsp:doBody/>
				</formQuery:result>
			</div>
			<div class="panel-footer clearfix">
				<div class="pull-right">
					<alfapp:pagination pagination="${result.pagination}"/>
				</div>
	
		 		<c:set var="queryLayout" value="result-footer" scope="request"/>
		 		<jsp:doBody/>
			</div>
		</c:if>
	</div>
</div>