		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=9" />
		<title><decorator:title/> - <spring:message code="application.title"/></title>
	
		<%@ include file="/WEB-INF/jsp/include/common-vars.jsp"%>
	
		<link type="text/css" rel="stylesheet" href="${staticUrl}/${bootstrapPath}/css/bootstrap.min.css"/>
		<link type="text/css" rel="stylesheet" href="${staticUrl}/${bootstrapDatepickerPath}/css/bootstrap-datepicker.css"/>
	
		<!-- Style du viewer PDF.js -->
		<link type="text/css" rel="stylesheet" href="${staticUrl}/css/pdf.viewer.css" />
	
		<link type="text/css" rel="stylesheet" href="${staticUrl}/css/application.css" />
		<!--[if IE]>
			<link type="text/css" rel="stylesheet" href="${staticUrl}/css/application-ie8.css">
		<![endif]-->
		<link rel="Shortcut icon" href="${staticUrl}/img/cg71-favicon.ico" type="image/x-icon" />
		<decorator:head/>