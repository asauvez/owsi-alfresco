<%@ include file="/WEB-INF/jsp/include/includes.jsp"%>
<html>
<head>
<title><spring:message code="accueil.title" /></title>
</head>

<body>

	<div class="content">

		<div class="row">

			<div class="col-md-12">
				<!-- Head of the pannel -->
				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title clearfix">
							<i class="glyphicon glyphicon-search"></i>
							<spring:message code="search" />
						</h3>
					</div>

					<div class="panel-body">
						<div class="panel panel-default">
							<table class="table table-striped">
								<c:if test="${empty results}">
									<tr><td>(<spring:message code="empty" />)</td></tr>
								</c:if>
								<c:forEach var="result" items="${results}">
									<tr>
										<c:choose>
											<c:when test="${result.folder}">
												<td><c:url value="/folder" var="url">
														<c:param name="nodeRef" value="${result.nodeRef}" />
													</c:url>
													<i class="glyphicon glyphicon-folder-open"></i>
													<a href="${url}">  ${result.name} </a>
												</td>
													
												<td><c:if test="${result.mayDelete}">
														<c:url value="/ajax/delete" var="urlDelete">
															<c:param name="nodeRef" value="${result.nodeRef}" />
														</c:url>
														<a class="btn btn-danger pull-right ajax-link" href="${urlDelete}"
															data-confirmation-msg="Do you realy want to delete ${result.name}?">
															<span class="glyphicon glyphicon-trash"></span>
														</a>
													</c:if>
												</td>
											</c:when>
											<c:otherwise>
												<c:url value="/content/${result.name}" var="urlDownload">
													<c:param name="nodeRef" value="${result.nodeRef}" />
													<c:param name="forceDownload" value="true" />
												</c:url>
												<c:url value="/file" var="urlFile">
													<c:param name="nodeRef" value="${result.nodeRef}" />
												</c:url>
												<td>
													<i class="glyphicon glyphicon-file"></i>
													<a href="${urlFile}"> ${result.name} </a>
												</td>
												<td>
													<div class="pull-right">
														<c:url value="/content/${result.name}" var="urlShow">
															<c:param name="nodeRef" value="${result.nodeRef}" />
														</c:url>
														<a class="btn btn-info" href="${urlShow}" target="blank_">
															<span class="glyphicon glyphicon-eye-open"></span>
														</a>
														<a class="btn btn-info " href="${urlDownload}">
															<span class="glyphicon glyphicon-download-alt"></span>
														</a> 
													</div>
												</td>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:forEach>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- Scripts -->
</body>
</html>