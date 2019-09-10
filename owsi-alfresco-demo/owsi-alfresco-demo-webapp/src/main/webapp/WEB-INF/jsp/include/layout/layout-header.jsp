<nav class="navbar navbar-default">
	<div class="container-fluid">
	
		<div class="navbar-header">
			<c:url value="/" var="url"/>
			<a class="navbar-brand" href="${url}"><spring:message code="demo.title"/></a>
		</div>

		<ul class="nav navbar-nav navbar-right">
			<li>
				<c:url value="/search" var="urlSearch"/>
				<form class="navbar-form navbar-left" method="get" action="${urlSearch}">
					<div class="form-group">
						<input name="q" type="text" class="form-control"
							required="required" placeholder="Search" autofocus>
					</div>
					<spring:message var="label" code="search" />
					<input type="submit" class="btn btn-default" value="${label}">
				</form>
			<li>
			<li>
				<c:url value="/security/logout" var="url" /> 
				<a class="navbar-toggle collapsed" role="button" style="padding: 7%;" href="${url}"><spring:message code="logout.title" /></a>
			</li>
		</ul>
	</div>
</nav>