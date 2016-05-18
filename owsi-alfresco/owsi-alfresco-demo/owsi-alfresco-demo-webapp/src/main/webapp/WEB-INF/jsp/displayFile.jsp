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
							${file.name}
						</h3>
					</div>
					<div class="panel-body">
						<div class="panel panel-default">
							<div class="panel-body">
								<div class="row">
									<div class="col-md-9">
									<p> Titre : ${file.title}</p>
									<p> Description : ${file.description}</p>
									<p> Size : ${file.fileSize}</p>
									</div>
									<div class="col-md-2">
										<c:url value="/content/${file.name}" var="urlDownload">
											<c:param name="nodeRef" value="${file.nodeRef}" />
										</c:url>
										<a class="btn btn-primary" href="${urlDownload}"><spring:message code="file.download"/></a>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

			</div>
		</div>

	</div>
</body>
</html>