<nav class="navbar navbar-default">
	<div class="container-fluid">
		<form class="navbar-form navbar-left" role="search">
			<div class="form-group">
				<input type="text" class="form-control" placeholder="Search">
			</div>
			<button type="submit" class="btn btn-default">Submit</button>
		</form>
		<ul class="nav navbar-nav navbar-right">
			<li>
				<c:url value="/security/logout" var="url" /> 
				<a class="navbar-toggle collapsed" role="button" href="${url}"><spring:message code="logout.title" /></a>
			</li>
		</ul>
	</div>
</nav>