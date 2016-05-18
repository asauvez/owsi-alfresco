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
						<ol class="breadcrumb">
							<c:forEach var="parent" items="${filAriane}">
								<c:url value="/folder" var="url">
									<c:param name="nodeRef" value="${parent.nodeRef}" />
								</c:url>
								
								<li><a href="${url}"> ${parent.name} </a></li>
							</c:forEach>
							<li class ="active">${file.name}</li>
						</ol>
						<div class="panel panel-default">
							<div class="panel-body">
								<div class="row">
									<table class="table table-striped">
										<tr>
											<td>Titre :</td><td>${file.title}</td>
										</tr>
										<tr>
											<td>Description :</td><td>${file.description}</td>
										</tr>
										<tr>
											<td>Size :</td><td>${file.fileSize} Octets</td>
										</tr>
										<tr>
											<td>Mime type :</td><td>${file.mime}</td>
										</tr>
										
									</table>
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
</body>
</html>