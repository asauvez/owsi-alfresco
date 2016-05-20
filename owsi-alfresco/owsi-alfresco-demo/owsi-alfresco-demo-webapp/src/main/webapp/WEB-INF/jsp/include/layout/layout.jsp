<%@ include file="/WEB-INF/jsp/include/includes.jsp"%>

<!DOCTYPE html>
<html>
	<head>
		<%@ include file="/WEB-INF/jsp/include/layout/layout-head.jsp"%>
	</head>
	<body>
		<div class="container">
		
			<%@ include file="/WEB-INF/jsp/include/layout/layout-header.jsp"%>
			
			<div id="navbar">
				<decorator:getProperty property="page.navbar"/>
			</div>
			
			<div id="breadcrumbs">
				<decorator:getProperty property="page.breadcrumb"/>
			</div>
	
			<div class="global-alerts">
				<%@ include file="/WEB-INF/jsp/include/alerts.jsp"%>
			</div>
			
	
			<div class="contenu">
				<c:if test="${isUtilisateur}">
					<div class="sidebar">
						<div class="puce">
							<decorator:getProperty property="page.sidebar.actions"/>
						</div>
					</div>
				</c:if>
	
				<div id="dialog" class="maincontent">
					<decorator:body/>
				</div>
			</div>
	
			<%@ include file="/WEB-INF/jsp/include/layout/layout-footer.jsp"%>
		</div>
	
		<div class="modal fade" id="loadingModal" tabindex="-1" role="dialog" data-keyboard="false" data-backdrop="static" data-show="false">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-body">
						<p class="text-muted"><spring:message code="loading"/></p>
						<img src="${staticUrl}/img/ajax-loader.gif"/>
					</div>
				</div>
			</div>
		</div>
	
		<script type="text/javascript">
			var APPLICATION_BASE_URL = '${baseUrl}';
			var APPLICATION_STATIC_URL = '${staticUrl}';
			var APPLICATION_MESSAGES = {
				"confirm.title": "<spring:message code="confirm.title" javaScriptEscape="true"/>",
				"confirm.cancel": "<spring:message code="confirm.cancel" javaScriptEscape="true"/>",
				"confirm.ok": "<spring:message code="confirm.ok" javaScriptEscape="true"/>",
				"pagination.page.previous": "<spring:message code="pagination.page.previous" javaScriptEscape="true"/>",
				"pagination.page.next": "<spring:message code="pagination.page.next" javaScriptEscape="true"/>",
				"typeahead.noItem": "<spring:message code="typeahead.noItem" javaScriptEscape="true"/>",
				"exception.generic.message": "<spring:message code="exception.generic.message" javaScriptEscape="true"/>",
				"exception.javascript.message": "<spring:message code="exception.javascript.message" javaScriptEscape="true"/>",
				"exception.jsp.message": "<spring:message code="exception.jsp.message" javaScriptEscape="true"/>"
			};
		</script>
		<script type="text/javascript" src="${staticUrl}/${jqueryPath}/jquery.min.js"></script>
		<%-- Envoi des formulaires en AJAX --%>
		<script type="text/javascript" src="${staticUrl}/${jqueryFormPath}/jquery.form.js"></script>
		<%-- Gestion de l'historique sous forme de hash dans l'URL --%>
		<script type="text/javascript" src="${staticUrl}/javascript/jquery/jquery.ba-bbq.js"></script>
		<%-- Placeholders sous IE --%>
		<script type="text/javascript" src="${staticUrl}/${jqueryPlaceholderPath}/jquery.placeholder.min.js"></script>
		<%-- Cookies --%>
		<owsi:webjarPath webjar="js-cookie" var="jsCookieWebjar" />
		<script type="text/javascript" src="${staticUrl}/${jsCookieWebjar}/js.cookie.js"></script>
		<%-- Bootstrap --%>
		<script type="text/javascript" src="${staticUrl}/${bootstrapPath}/js/bootstrap.min.js"></script>
		<%-- Calendrier bootstrap --%>
		<script type="text/javascript" src="${staticUrl}/${bootstrapDatepickerPath}/js/bootstrap-datepicker.js"></script>
	
		<%-- Viewer PDF.js (0.8.1334 cf. H23610) --%>
		<script type="text/javascript" src="${staticUrl}/javascript/pdf-viewer/compatibility.js"></script>
		<script type="text/javascript" src="${staticUrl}/javascript/pdf-viewer/pdf.js"></script>
		<script type="text/javascript">
			PDFJS.imageResourcesPath = './images/';
			PDFJS.workerSrc = '${staticUrl}/javascript/pdf-viewer/pdf.worker.js';
			PDFJS.cMapUrl = '${staticUrl}/javascript/pdf-viewer/cmaps/';
			PDFJS.cMapPacked = true;
		</script>
		<script type="text/javascript" src="${staticUrl}/javascript/pdf-viewer/pdf.viewer.js"></script>
		
		<%-- OWSI --%>
		<script type="text/javascript" src="${staticUrl}/javascript/owsi-alfresco/form.submit-once.js"></script>
		<script type="text/javascript" src="${staticUrl}/javascript/owsi-alfresco/form.bind-ajax-post.js"></script>
		<script type="text/javascript" src="${staticUrl}/javascript/owsi-alfresco/url-state.js"></script>
		<script type="text/javascript" src="${staticUrl}/javascript/owsi-alfresco/form.url-state.js"></script>
		<script type="text/javascript" src="${staticUrl}/javascript/owsi-alfresco/modal.loading.js"></script>
		<script type="text/javascript" src="${staticUrl}/javascript/owsi-alfresco/file-download.js"></script>
		
		<%-- Specifique application --%>
		<script type="text/javascript" src="${staticUrl}/js/application.js"></script>
		
		<decorator:getProperty property="page.scripts"/>
		
	</body>
</html>