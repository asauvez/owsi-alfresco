<%@ include file="/WEB-INF/jsp/include/includes.jsp"%>
<html>
<head>
	<title><spring:message code="login.title"/></title>
</head>
<body>
	<content tag="title">
		<spring:message code="login.title"/>
	</content>
	
	<content tag="scripts">
		<script type="text/javascript">
			$(document).ready(function() {
				$("#j_username").focus();
			});
		</script>
	</content>
	
	<content tag="bodyCssClass">login-body</content>

	<div class="panel panel-default login-panel">
		<div class="panel-body">
			<spring:url value="/j_spring_security_check" var="formURL" />

			<form method="post" action="${formURL}" class="form-horizontal">
				<div class="content">
					<h4><spring:message code="application.title" /></h4>
					
					<p class="text-muted"><spring:message code="application.welcome" /></p>
					
					<%@ include file="/WEB-INF/jsp/include/alerts.jsp" %>

					<fieldset>
						<div class="form-group">
							<label class="control-label col-sm-4" for="j_username"><spring:message code="login.username"/></label>
							<div class="controls col-sm-8"><input type="text" id="j_username" name="j_username" value="" class="form-control"/></div>
						</div>
						
						<div class="form-group">
							<label class="control-label col-sm-4" for="j_password"><spring:message code="login.password"/></label>
							<div class="controls col-sm-8"><input type="password" id="j_password" name="j_password" value="" class="form-control"/></div>
						</div>
						
						<div class="control-group text-right">
							<div class="controls">
								<button class="btn btn-primary" type="submit"><spring:message code="login.action"/></button>
							</div>
						</div>
					</fieldset>
				</div>
			</form>
		</div>
	</div>
</body>
</html>