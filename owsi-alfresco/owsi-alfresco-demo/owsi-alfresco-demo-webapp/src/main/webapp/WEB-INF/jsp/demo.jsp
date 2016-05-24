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
						<div class="pull-right">
							<c:if test="${folder.mayAdd}">
							
								<button type="button" class="btn btn-info "
									data-toggle="modal" data-target="#modalAddFolder">
									<spring:message code="file.addFolder" />
								</button>
								<button type="button" class="btn btn-info "
									data-toggle="modal" data-target="#modalAddFile">
									<spring:message code="file.addFile" />
								</button>
							</c:if>
							
							<c:if test="${folder.mayDelete}">
								<c:url value="/ajax/delete" var="urlDelete">
									<c:param name="nodeRef" value="${folder.nodeRef}" />
								</c:url>
								<a class="btn btn-danger ajax-link" href="${urlDelete}" 
									data-confirmation-msg="Do you realy want to delete ${folder.name}?">
									<span class="glyphicon glyphicon-trash"></span>
								</a>
							</c:if>
						</div>
						

						<h3 class="panel-title clearfix">
							<i class="glyphicon glyphicon-folder-open"></i> ${folder.name}
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
							<li class="active">${folder.name}</li>
						</ol>
						
						<div class="panel panel-default">
							<table class="table table-striped">
								<c:if test="${empty folder.children}">
									<tr><td>(<spring:message code="empty" />)</td></tr>
								</c:if>
								<c:forEach var="child" items="${folder.children}">
									<tr>
										<c:choose>
											<c:when test="${child.folder}">
												<td><c:url value="/folder" var="url">
														<c:param name="nodeRef" value="${child.nodeRef}" />
													</c:url>
													<i class="glyphicon glyphicon-folder-open"></i>
													<a href="${url}">  ${child.name} </a>
												</td>
													
												<td><c:if test="${child.mayDelete}">
														<c:url value="/ajax/delete" var="urlDelete">
															<c:param name="nodeRef" value="${child.nodeRef}" />
														</c:url>
														<a class="btn btn-danger pull-right ajax-link" href="${urlDelete}"
															data-confirmation-msg="Do you realy want to delete ${child.name}?">
															<span class="glyphicon glyphicon-trash"></span>
														</a>
													</c:if>
												</td>
											</c:when>
											<c:otherwise>
												<c:url value="/content/${child.name}" var="urlDownload">
													<c:param name="nodeRef" value="${child.nodeRef}" />
													<c:param name="forceDownload" value="true" />
												</c:url>
												<c:url value="/file" var="urlFile">
													<c:param name="nodeRef" value="${child.nodeRef}" />
												</c:url>
												<td>
													<i class="glyphicon glyphicon-file"></i>
													<a href="${urlFile}"> ${child.name} </a>
												</td>
												<td>
													<div class="pull-right">
														<c:if test="${child.mayDelete}">
															<c:url value="/ajax/delete" var="urlDelete">
																<c:param name="nodeRef" value="${child.nodeRef}" />
															</c:url>
															
															<a class="btn btn-danger ajax-link" href="${urlDelete}"
																data-confirmation-msg="Do you realy want to delete ${child.name}?">
																<span class="glyphicon glyphicon-trash"></span>
															</a>
														</c:if>
														<c:url value="/content/${child.name}" var="urlShow">
															<c:param name="nodeRef" value="${child.nodeRef}" />
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


			<!-- Modal windows -->
			<div class="modal fade" tabindex="-1" role="dialog"
				id="modalAddFolder">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">
								<spring:message code="file.addFolder" />
							</h4>
						</div>
						<c:url value="/ajax/add-folder" var="urlAddFolder">
							<c:param name="nodeRef" value="${folder.nodeRef}" />
						</c:url>
						<form class="ajax-form" method="post" action="${urlAddFolder}">
							<div class="modal-body">
								<div class="form-group">
									<label class="control-label">
										<spring:message code="file.modal.folder.name" />
									</label> 
									<input type="text" required="required"
										class="form-control" name="folderName" autofocus>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default" data-dismiss="modal">
									<spring:message code="file.modal.close" />
								</button>
								<spring:message var="label" code="file.addFolder" />
								<input type="submit" class="btn btn-primary" value="${label}">
							</div>
						</form>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.modal -->



			<div class="modal fade" tabindex="-1" role="dialog"
				id="modalAddFile">
				<div class="modal-dialog">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 class="modal-title">
								<spring:message code="file.addFile" />
							</h4>
						</div>
						<c:url value="/ajax/add-file" var="urlAddFile">
							<c:param name="nodeRef" value="${folder.nodeRef}" />
						</c:url>
						<form class="ajax-form" method="post" action="${urlAddFile}"
							enctype="multipart/form-data">
							<div class="modal-body">
								<div class="form-group">
									<label class="control-label">
										<spring:message code="file.modal.folder.name" />
									</label>
									<input type="file" required="required"
										class="form-control" name="file">
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">
									<spring:message code="file.modal.close" />
								</button>
								<spring:message var="label" code="file.addFile" />
								<input type="submit" class="btn btn-primary" value="${label}">
							</div>
						</form>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.modal -->
		</div>

	</div>
	<!-- Scripts -->
</body>
</html>