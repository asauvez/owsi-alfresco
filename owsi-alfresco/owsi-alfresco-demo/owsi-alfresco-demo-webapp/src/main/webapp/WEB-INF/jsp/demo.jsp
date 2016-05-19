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
			<div class="col-md-2">
			
				<button type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#modalAddFolder">
					<spring:message code="file.addFolder"/>
				</button>
				 <div class="modal fade" tabindex="-1" role="dialog" id="modalAddFolder">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
								<h4 class="modal-title"><spring:message code="file.addFolder"/></h4>
							</div>
							<c:url value="/add-folder" var="urlAddFolder">
								<c:param name="nodeRef" value="${folder.nodeRef}" />
							</c:url>
							<form method="post" action="${urlAddFolder}">
								<div class="modal-body">
									<div class="form-group">
										<label class="control-label"><spring:message code="file.modal.folder.name"/></label>
										<input type="text" class="form-control" name="folderName">
									</div>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="file.modal.close"/></button>
									<spring:message var="label" code="file.addFolder"/>
									<input type="submit" class="btn btn-primary" value="${label}">
								</div>
							</form>
						</div><!-- /.modal-content -->
					</div><!-- /.modal-dialog -->
				</div><!-- /.modal -->
				<br/>
				<br/>
				
				<button type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#modalAddFile" >
					<spring:message code="file.addFile"/>
				</button>
				
				<div class="modal fade" tabindex="-1" role="dialog" id="modalAddFile">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
								<h4 class="modal-title"><spring:message code="file.addFile"/></h4>
							</div>
							<c:url value="/add-file" var="urlAddFile">
								<c:param name="nodeRef" value="${folder.nodeRef}" />
							</c:url>
							<form method="post" action="${urlAddFile}" enctype="multipart/form-data">
								<div class="modal-body">
									<div class="form-group">
										<label class="control-label"><spring:message code="file.modal.folder.name"/></label>
										<input type="file" class="form-control" name="file">
									</div>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="file.modal.close"/></button>
									<spring:message var="label" code="file.addFile"/>
									<input type="submit" class="btn btn-primary" value="${label}">
								</div>
							</form>
						</div><!-- /.modal-content -->
					</div><!-- /.modal-dialog -->
				</div><!-- /.modal -->
			</div>
		</div>

	</div>
	<!-- Scripts -->
</body>
</html>