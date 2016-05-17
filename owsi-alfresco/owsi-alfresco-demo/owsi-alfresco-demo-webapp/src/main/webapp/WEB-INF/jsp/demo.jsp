<%@ include file="/WEB-INF/jsp/include/includes.jsp"%>
<html>
<head>
<title><spring:message code="accueil.title" /></title>
</head>

<body>
	<%@ include file="/WEB-INF/jsp/include/profile-vars.jsp"%>

	<div class="content">

		<div class="row">

			<div class="col-md-8">

				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title">
							<i class="glyphicon glyphicon-dashboard"></i>
							<%-- <spring:message code="demo.fichiers" /> --%>
							${folderName}
						</h3>
					</div>
					<div class="panel-body">
						<div class="panel panel-default">
							<div class="panel-body">
								<div class="row">
									<c:forEach var="folder" items="${childrenFolder}">
										<h2> ${folder.name} </h2>
									</c:forEach>
									<div class="col-md-2">
										<div class="text-muted">
										</div>
									</div>
									<div class="col-md-9"></div>
								</div>
							</div>
						</div>
					</div>
				</div>

			</div>
		</div>

	</div>
	<!-- Scripts -->
</body>
</html>