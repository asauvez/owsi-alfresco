<%@ include file="/WEB-INF/jsp/include/includes.jsp"%>
<html>
<head>
<title><spring:message code="accueil.title" /></title>
</head>

<body>

	<div class="content">

		<div class="row">

			<div class="col-md-12">

				<div class="panel panel-default">
					<div class="panel-heading">
						<div class="pull-right">
							<c:url value="/content/${file.name}" var="urlDownload">
								<c:param name="nodeRef" value="${file.nodeRef}" />
								<c:param name="forceDownload" value="true" />
							</c:url>
							<c:url value="/content/${file.name}" var="urlShow">
								<c:param name="nodeRef" value="${file.nodeRef}" />
							</c:url>
							<a class="btn btn-info" href="${urlShow}" target="blank_"><span
								class="glyphicon glyphicon-eye-open"></span></a> <a
								class="btn btn-info" href="${urlDownload}"><span
								class="glyphicon glyphicon-download-alt"></span></a>
							<c:if test="${file.mayDelete}">
								<c:url value="/delete" var="urlDelete">
									<c:param name="nodeRef" value="${file.nodeRef}" />
								</c:url>
								<a class="btn btn-danger " href="${urlDelete}"
									onclick="return(confirm('Do you realy want to delete ${child.name}?'));">
									<span class="glyphicon glyphicon-trash"></span>
								</a>
							</c:if>
						</div>
						<h3 class="panel-title clearfix">
							<i class="glyphicon glyphicon-file"></i> ${file.name}
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
							<li class="active">${file.name}</li>
						</ol>
						<div class="panel panel-default">
							<table class="table">
								<tr>
									<td>Titre :</td>
									<td>${file.title}</td>
								</tr>
								<tr>
									<td>Description :</td>
									<td>${file.description}</td>
								</tr>
								<tr>
									<td>Size :</td>
									<td>${file.fileSize} Octets</td>
								</tr>
								<tr>
									<td>Mime type :</td>
									<td>${file.mime}</td>
								</tr>
							</table>
						</div>
					</div>
				</div>

			</div>

		</div>

	</div>
</body>
</html>