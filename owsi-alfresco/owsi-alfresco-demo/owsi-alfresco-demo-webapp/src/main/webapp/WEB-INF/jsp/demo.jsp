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
							${folder.name}
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
							<li class ="active">${folder.name}</li>
						</ol>
						<div class="panel panel-default">
							<div class="panel-body">
								<table class="table table-striped">
									<c:forEach var="child" items="${folder.children}">
										<tr><c:choose>
											<c:when test="${child.folder}">
												<td>
													<c:url value="/folder" var="url">
														<c:param name="nodeRef" value="${child.nodeRef}" />
													</c:url>
													<a href="${url}"> ${child.name} </a>
												</td>
												<!-- We use an "empty td" to have two columns when we don't have the  button "Download" -->
												<td/>
											</c:when>
											<c:otherwise>
												<c:url value="/content/${child.name}" var="urlDownload">
													<c:param name="nodeRef" value="${child.nodeRef}" />
												</c:url>
												<c:url value="/file" var="urlFile">
													<c:param name="nodeRef" value="${child.nodeRef}" />
												</c:url>
												<td>
													<a href="${urlFile}"> ${child.name} </a>
												</td><td>
													<a class="btn btn-primary" href="${urlDownload}"><spring:message code="file.download"/></a>
												</td>
											</c:otherwise>
										</c:choose></tr>
									</c:forEach>
								</table>
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