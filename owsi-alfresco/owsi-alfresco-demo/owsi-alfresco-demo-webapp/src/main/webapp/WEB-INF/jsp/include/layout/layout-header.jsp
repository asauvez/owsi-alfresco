<nav class="navbar navbar-default">
	<div class="container-fluid">
	
		<div class="navbar-header">
			<c:url value="/" var="url"/>
			<a class="navbar-brand" href="${url}"><spring:message code="demo.title"/></a>
		</div>

		<ul class="nav navbar-nav navbar-right">
			<li>
				<form class="navbar-form navbar-left" role="search">
					<div class="form-group">
						<input type="text" class="form-control" placeholder="Search">
					</div>
					<button type="submit" class="btn btn-default">Submit</button>
				</form>
			<li>
			<li>
				<c:url value="/security/logout" var="url" /> 
				<a class="navbar-toggle collapsed" role="button" style="padding: 7%;" href="${url}"><spring:message code="logout.title" /></a>
			</li>
		</ul>
	</div>
</nav>