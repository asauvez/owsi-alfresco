<%@ include file="/WEB-INF/jsp/include/includes.jsp"%>

<spring:url value="/" var="baseUrl"/>
<alfapp:applicationVersion var="version"/>
<spring:url value="/static/${version}" var="staticUrl"/>

<alfapp:webjarPath webjar="jquery" var="jqueryPath"/>
<alfapp:webjarPath webjar="jquery-form" var="jqueryFormPath"/>
<alfapp:webjarPath webjar="jquery-placeholder" var="jqueryPlaceholderPath"/>
<alfapp:webjarPath webjar="bootstrap" var="bootstrapPath"/>
<alfapp:webjarPath webjar="bootstrap-datepicker" var="bootstrapDatepickerPath"/>
