<%@ include file="/WEB-INF/jsp/include/includes.jsp"%>
<html>
	<head>
		<title><spring:message code="accueil.title"/></title>
	</head>
	
	<body>
		<%@ include file="/WEB-INF/jsp/include/profile-vars.jsp"%>
		
		<%-- <content tag="navbar">
			<mdph:navbar active="accueil" canLogout="${canLogout}" />
		</content>
		
		<content tag="breadcrumb">
			<mdph:breadcrumb entries="menu.accueil" />
		</content>
		
		<content tag="title">
			<spring:message code="accueil.title"/>
		</content>
		 --%>
		<div class="content">
			
			<div class="row">
			
				<div class="col-md-8">
					
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title"><i class="glyphicon glyphicon-dashboard"></i> <spring:message code="accueil.mes-taches" /></h3>
						</div>
						<div class="panel-body">
							<c:choose>
								<c:when test="${1 eq 0 }">
<%-- 									<div class="alert alert-info">
										<spring:message code="accueil.mes-taches.message.aucune-tache" />
									</div> --%>
								</c:when>
								<c:otherwise>
									<c:forEach var="tache" items="${taches}">
									<div class="panel panel-default">
										<div class="panel-body">
											<div class="row">
												<div class="col-md-2">
													<div class="text-muted">
														<h5>
															<i class="glyphicon glyphicon-calendar"></i>
															<%-- <mdph:dateFormater date="${tache.created}"/> --%>
														</h5>
													</div>
													<div>
														<spring:url value="/beneficiaire/${tache.numeroSolis}" var="dossierUrl" />
														<c:set var="boutonTooltip">
															<spring:message code="button.ouvrir-dossier" arguments="${tache.numeroSolis}" />
														</c:set>
														<a href="${dossierUrl}" class="btn btn-primary btn-xs display-tooltip" role="button" title="${boutonTooltip}">
															<c:if test="${not empty tache.individu.nomNaissance}">
																<c:set var="nomDeNaissance" value=" (${tache.individu.nomNaissance})" />
															</c:if>
															${tache.numeroSolis}<br/>
															${tache.individu.nom}${nomDeNaissance}<br/>
															${tache.individu.prenom}
														</a>
													</div>
												</div>
												<div class="col-md-9">
													<blockquote>
														${tache.contenu}
													</blockquote>
												</div><!-- 
												<div class="col-md-1">
													<button type="button" class="close display-tooltip" title="<spring:message code="button.supprimer-tache" />" aria-hidden="true" data-toggle="modal" data-target="#tache${tache.idJavascript}DeleteModal">&times;</button>
													<%-- <%@ include file="/WEB-INF/jsp/include/modals/tache/tache-delete.jsp"%> --%>
												</div>-->
											</div>
										</div>
									</div>
									</c:forEach>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
	
				</div>
			</div>
		
		</div>
		<!-- Scripts -->
	</body>
</html>