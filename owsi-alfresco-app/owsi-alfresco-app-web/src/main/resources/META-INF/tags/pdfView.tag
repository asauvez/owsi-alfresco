<%@ taglib prefix="c"                   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring"              uri="http://www.springframework.org/tags" %>
<%@ attribute name="title"      required="false" type="String" %>
<%@ attribute name="url"        required="true" type="String" %>

<script type="text/javascript">
	var PDF_VIEWER_MESSAGES = {
		'find_not_found': '<spring:message code="pdf-viewer.find_not_found" javaScriptEscape="true" />',
		'find_reached_top': '<spring:message code="pdf-viewer.find_reached_top" javaScriptEscape="true" />',
		'find_reached_bottom': '<spring:message code="pdf-viewer.find_reached_bottom" javaScriptEscape="true" />',
		'password_label': '<spring:message code="pdf-viewer.password_label" javaScriptEscape="true" />',
		'password_invalid': '<spring:message code="pdf-viewer.password_invalid" javaScriptEscape="true" />',
		'loading_error': '<spring:message code="pdf-viewer.loading_error" javaScriptEscape="true" />',
		'invalid_file_error': '<spring:message code="pdf-viewer.invalid_file_error" javaScriptEscape="true" />',
		'missing_file_error': '<spring:message code="pdf-viewer.missing_file_error" javaScriptEscape="true" />',
		'error_message': '<spring:message code="pdf-viewer.error_message" javaScriptEscape="true" />',
		'error_stack': '<spring:message code="pdf-viewer.error_stack" javaScriptEscape="true" />',
		'error_file': '<spring:message code="pdf-viewer.error_file" javaScriptEscape="true" />',
		'error_line': '<spring:message code="pdf-viewer.error_line" javaScriptEscape="true" />',
		'page_of': '<spring:message code="pdf-viewer.page_of" javaScriptEscape="true" />',
		'printing_not_supported': '<spring:message code="pdf-viewer.printing_not_supported" javaScriptEscape="true" />',
		'printing_not_ready': '<spring:message code="pdf-viewer.printing_not_ready" javaScriptEscape="true" />',
		'rendering_error': '<spring:message code="pdf-viewer.rendering_error" javaScriptEscape="true" />',
		'thumb_page_title': '<spring:message code="pdf-viewer.thumb_page_title" javaScriptEscape="true" />',
		'thumb_page_canvas': '<spring:message code="pdf-viewer.thumb_page_canvas" javaScriptEscape="true" />'
	};
</script>
		
<div class="panel panel-default pdf-viewer-container" data-url="${url}">
	<c:if test="${not empty title}">
		<div class="panel-heading">
			<h4 class="panel-title"><i class="glyphicon glyphicon-eye-open"></i> <spring:message code="pdf-viewer.title" arguments="${title}" /></h4>
		</div>
	</c:if>
	<div class="panel-heading second-heading">
			<div id="mainContainer">
				<div id="secondaryToolbar" class="secondaryToolbar hidden doorHangerRight">
					<div id="secondaryToolbarButtonContainer">
						<button id="secondaryPresentationMode" class="secondaryToolbarButton presentationMode visibleLargeView display-tooltip" title="<spring:message code="pdf-viewer.presentation_mode" />" tabindex="18" data-l10n-id="presentation_mode">
							<span data-l10n-id="presentation_mode_label"><spring:message code="pdf-viewer.presentation_mode_label" /></span>
						</button>
						
						<!--
						<button id="secondaryOpenFile" class="btn btn-default secondaryToolbarButton openFile visibleLargeView display-tooltip" title="<spring:message code="pdf-viewer.open_file" />" tabindex="19" data-l10n-id="open_file">
							<span data-l10n-id="open_file_label"><spring:message code="pdf-viewer.open_file_label" /></span>
						</button>
						-->
						
						<!--
						<button id="secondaryPrint" class="secondaryToolbarButton print visibleMediumView display-tooltip" title="<spring:message code="pdf-viewer.print" />" tabindex="20" data-l10n-id="print">
							<span data-l10n-id="print_label"><spring:message code="pdf-viewer.print_label" /></span>
						</button>
						-->
						
						<a href="${url}" id="secondaryDownload" class="secondaryToolbarButton download file-download visibleMediumView display-tooltip" title="<spring:message code="pdf-viewer.download" />" tabindex="21" data-l10n-id="download">
							<span data-l10n-id="download_label"><spring:message code="pdf-viewer.download_label" /></span>
						</a>
						
						<a href="#" id="secondaryViewBookmark" class="secondaryToolbarButton bookmark visibleSmallView display-tooltip" title="<spring:message code="pdf-viewer.bookmark" />" tabindex="22" data-l10n-id="bookmark">
							<span data-l10n-id="bookmark_label"><spring:message code="pdf-viewer.bookmark_label" /></span>
						</a>
					</div>
				</div>  <!-- secondaryToolbar -->
				
				<div class="toolbar">
					<div id="toolbarContainer">
						<div id="toolbarViewer">
							<button id="sidebarShow" class="btn btn-default btn-sm display-tooltip" title="<spring:message code="pdf-viewer.show_sidebar" />" tabindex="4" data-l10n-id="toggle_sidebar">
								<span class="glyphicon glyphicon-chevron-right"></span>
							</button>
							<button id="sidebarHide" class="btn btn-default btn-sm display-tooltip" title="<spring:message code="pdf-viewer.hide_sidebar" />" tabindex="4" data-l10n-id="toggle_sidebar">
								<span class="glyphicon glyphicon-chevron-left"></span>
							</button>
							<button id="viewFind" class="btn btn-default btn-sm group hiddenSmallView display-tooltip" title="<spring:message code="pdf-viewer.findbar" />" tabindex="5" data-l10n-id="findbar">
								<span class="glyphicon glyphicon-search"></span> <span class="caret"></span>
							</button>
							<div class="btn-group">
								<button class="btn btn-default btn-sm pageUp display-tooltip" title="<spring:message code="pdf-viewer.previous" />" id="previous" tabindex="6" data-l10n-id="previous">
									<span class="glyphicon glyphicon-backward"></span>
								</button>
								<button class="btn btn-default btn-sm pageDown display-tooltip" title="<spring:message code="pdf-viewer.next" />" id="next" tabindex="7" data-l10n-id="next">
									<span class="glyphicon glyphicon-forward"></span>
								</button>
							</div>
							
								<label id="pageNumberLabel" for="pageNumber" data-l10n-id="page_label"><spring:message code="pdf-viewer.page_label" /> </label>
								<input type="number" id="pageNumber" class="pageNumber" value="1" size="4" min="1" tabindex="8" />
								<span id="numPages"></span>
							
							<div class="btn-group">
								<button id="zoomOut" class="btn btn-default btn-sm zoomOut display-tooltip" title="<spring:message code="pdf-viewer.zoom_out" />" tabindex="9" data-l10n-id="zoom_out">
									<span class="glyphicon glyphicon-zoom-out"></span>
								</button>
								<button id="zoomIn" class="btn btn-default btn-sm zoomIn display-tooltip" title="<spring:message code="pdf-viewer.zoom_in" />" tabindex="10" data-l10n-id="zoom_in">
									<span class="glyphicon glyphicon-zoom-in"></span>
								</button>
								<div class="btn-group">
									<button type="button" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown">
										<span id="scale-selected"></span>
										<span class="caret"></span>
									</button>
									<ul class="dropdown-menu pull-right">
										<li><a href="#" data-scale-select="auto"><spring:message code="pdf-viewer.zoom.auto" /></a></li>
										<li><a href="#" data-scale-select="page-actual"><spring:message code="pdf-viewer.zoom.page-actual" /></a></li>
										<li><a href="#" data-scale-select="page-fit"><spring:message code="pdf-viewer.zoom.page-fit" /></a></li>
										<li><a href="#" data-scale-select="page-width"><spring:message code="pdf-viewer.zoom.page-width" /></a></li>
										<li class="divider"></li>
										<li><a href="#" data-scale-select="0.5">50%</a></li>
										<li><a href="#" data-scale-select="0.75">75%</a></li>
										<li><a href="#" data-scale-select="1">100%</a></li>
										<li><a href="#" data-scale-select="1.25">125%</a></li>
										<li><a href="#" data-scale-select="1.5">150%</a></li>
										<li><a href="#" data-scale-select="2">200%</a></li>
									</ul>
								</div>
							</div>
							
							<div class="btn-group">
								<button id="presentationMode" class="btn btn-default btn-sm presentationMode hiddenLargeView display-tooltip" title="<spring:message code="pdf-viewer.presentation_mode" />" tabindex="12" data-l10n-id="presentation_mode">
									<span class="glyphicon glyphicon-fullscreen"></span>
								</button>
								
								<!--
								<button id="openFile" class="btn btn-default btn-sm openFile hiddenLargeView display-tooltip" title="<spring:message code="pdf-viewer.open_file" />" tabindex="13" data-l10n-id="open_file">
									<span class="glyphicon glyphicon-open"></span>
								</button>
								-->
								
								<!--
								<button id="print" class="btn btn-default btn-sm print hiddenMediumView display-tooltip" title="<spring:message code="pdf-viewer.print" />" tabindex="14" data-l10n-id="print">
									<span class="glyphicon glyphicon-print"></span>
								</button>
								-->
								
								<a href="${url}" id="download" class="btn btn-default btn-sm download file-download hiddenMediumView display-tooltip" title="<spring:message code="pdf-viewer.download" />" tabindex="15" data-l10n-id="download">
									<span class="glyphicon glyphicon-download-alt"></span>
								</a>
								<!-- <div class="toolbarButtonSpacer"></div> -->
								<a href="#" id="viewBookmark" class="btn btn-default btn-sm bookmark hiddenSmallView display-tooltip" title="<spring:message code="pdf-viewer.bookmark" />" tabindex="16" data-l10n-id="bookmark">
									<span class="glyphicon glyphicon-bookmark"></span>
								</a>
								
								<!-- Single button -->
								<div class="btn-group">
									<button type="button" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown">
										<span class="caret"></span>
									</button>
									<ul class="dropdown-menu pull-right" role="menu">
										<li><a id="firstPage" class="firstPage" tabindex="23" data-l10n-id="first_page">
											<span class="glyphicon glyphicon-fast-backward"></span> <spring:message code="pdf-viewer.first_page" />
										</a></li>
										<li><a id="lastPage" class="lastPage" tabindex="24" data-l10n-id="last_page">
											<span class="glyphicon glyphicon-fast-forward"></span> <spring:message code="pdf-viewer.last_page" />
										</a></li>
										<li class="divider"></li>
										<li><a id="pageRotateCw" tabindex="25" data-l10n-id="page_rotate_cw">
											<span class="glyphicon glyphicon-retweet"></span> <spring:message code="pdf-viewer.page_rotate_cw" />
										</a></li>
										<li><a id="pageRotateCcw" tabindex="26" data-l10n-id="page_rotate_ccw">
											<span class="glyphicon glyphicon-retweet"></span> <spring:message code="pdf-viewer.page_rotate_ccw" />
										</a></li>
									</ul>
								</div>
							</div>
						</div>  <!-- toolbarViewer -->
						<div id="loadingBar">
							<div class="progress">
								<div class="glimmer"></div>
							</div>
						</div>  <!-- loadingBar -->
					</div> <!-- toolbarContainer -->
				</div>  <!-- toolbar -->
			</div>  <!-- mainContainer -->
		
	</div>
	
	<div class="panel-body hidden" id="findbar">
		<div class="input-group input-group-sm">
			<span class="input-group-addon"><span class="glyphicon glyphicon-search"></span></span>
			<input id="findInput" tabindex="41" type="text" class="form-control" />
			<div class="input-group-btn">
				<button class="btn btn-default btn-xs findPrevious display-tooltip" title="<spring:message code="pdf-viewer.find_previous" />" id="findPrevious" tabindex="42" data-l10n-id="find_previous">
					<span class="glyphicon glyphicon-step-backward"></span>
				</button>
				<button class="btn btn-default btn-xs findNext display-tooltip" title="<spring:message code="pdf-viewer.find_next" />" id="findNext" tabindex="43" data-l10n-id="find_next">
					<span class="glyphicon glyphicon-step-forward"></span>
				</button>
			</div>
		</div>
		<div class="checkbox">
					<label for="findHighlightAll" tabindex="44" data-l10n-id="find_highlight">
						<input type="checkbox" id="findHighlightAll" />
						<spring:message code="pdf-viewer.find_highlight" />
					</label>
				</div>
				<div class="checkbox">
					<label for="findMatchCase" tabindex="45" data-l10n-id="find_match_case_label">
						<input type="checkbox" id="findMatchCase" />
						<spring:message code="pdf-viewer.find_match_case_label" />
					</label>
				</div>
		<span id="findMsg"></span>
	</div> <!-- findbar -->
	
	<div id="sidebar" style="float:left;">
		<!--
		<div class="row">
			<div class="col-md-12">
			<div class="btn-group">
				<button id="viewThumbnail display-tooltip" title="<spring:message code="pdf-viewer.thumbs" />" type="button" class="btn btn-default" tabindex="2" data-l10n-id="thumbs"><span class="glyphicon glyphicon-th-large"></span></button>
				<button id="viewOutline display-tooltip" title="<spring:message code="pdf-viewer.outline" />" type="button" class="btn btn-default" tabindex="3" data-l10n-id="outline"><span class="glyphicon glyphicon-list"></span></button>
			</div>
			</div>
		</div>
		-->
		<div id="sidebarContent" style="padding: 10px 15px;">
			<ul id="thumbnailView" class="nav nav-pills"></ul>
			<div id="outlineView" class="hidden"></div>
		</div>
	</div>
	
	<div>
		<div id="viewerContainer" tabindex="0">
			<div id="viewer"></div>
		</div>
		
		<div id="errorWrapper" hidden='true'>
			<div id="errorMessageLeft">
				<span id="errorMessage"></span>
				<button id="errorShowMore" data-l10n-id="error_more_info">
					<spring:message code="pdf-viewer.error_more_info" />
				</button>
				<button id="errorShowLess" data-l10n-id="error_less_info" hidden='true'>
					<spring:message code="pdf-viewer.error_less_info" />
				</button>
			</div>
			<div id="errorMessageRight">
				<button id="errorClose" data-l10n-id="error_close">
					<spring:message code="pdf-viewer.error_close" />
				</button>
			</div>
			<div class="clearBoth"></div>
			<textarea id="errorMoreInfo" hidden='true' readonly="readonly"></textarea>
		</div>
	
		<div id="overlayContainer" class="hidden">
			<div id="promptContainer">
				<div id="passwordContainer" class="prompt doorHanger">
					<div class="row">
						<p id="passwordText" data-l10n-id="password_label"><spring:message code="pdf-viewer.password_label" /></p>
					</div>
					<div class="row">
						<input type="password" id="password" class="toolbarField" />
					</div>
					<div class="row">
						<button id="passwordCancel" class="promptButton"><span data-l10n-id="password_cancel"><spring:message code="pdf-viewer.password_cancel" /></span></button>
						<button id="passwordSubmit" class="promptButton"><span data-l10n-id="password_ok"><spring:message code="pdf-viewer.password_ok" /></span></button>
					</div>
				</div>
			</div>
		</div> 
			
	</div> <!-- pdf-viewer-container -->
	<div id="printContainer">
		<div id="mozPrintCallback-shim" hidden>
			<style scoped>
				#mozPrintCallback-shim {
				  position: fixed;
				  top: 0;
				  left: 0;
				  height: 100%;
				  width: 100%;
				  z-index: 9999999;
				
				  display: block;
				  text-align: center;
				  background-color: rgba(0, 0, 0, 0.5);
				}
				#mozPrintCallback-shim[hidden] {
				  display: none;
				}
				@media print {
				  #mozPrintCallback-shim {
				    display: none;
				  }
				}
				
				#mozPrintCallback-shim .mozPrintCallback-dialog-box {
				  display: inline-block;
				  margin: -50px auto 0;
				  position: relative;
				  top: 45%;
				  left: 0;
				  min-width: 220px;
				  max-width: 400px;
				
				  padding: 9px;
				
				  border: 1px solid hsla(0, 0%, 0%, .5);
				  border-radius: 2px;
				  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
				
				  background-color: #474747;
				
				  color: hsl(0, 0%, 85%);
				  font-size: 16px;
				  line-height: 20px;
				}
				#mozPrintCallback-shim .progress-row {
				  clear: both;
				  padding: 1em 0;
				}
				#mozPrintCallback-shim progress {
				  width: 100%;
				}
				#mozPrintCallback-shim .relative-progress {
				  clear: both;
				  float: right;
				}
				#mozPrintCallback-shim .progress-actions {
				  clear: both;
				}
			</style>
			<div class="mozPrintCallback-dialog-box">
				<spring:message code="pdf-viewer.preparing-for-printing" />
				<div class="progress-row">
					<progress value="0" max="100"></progress>
					<span class="relative-progress">0%</span>
				</div>
				<div class="progress-actions">
					<input type="button" value="<spring:message code="button.cancel" />" class="mozPrintCallback-cancel">
				</div>
			</div>
		</div>
	</div>
	
	<menu type="context" id="viewerContextMenu">
		<menuitem id="contextFirstPage" label="<spring:message code="pdf-viewer.first_page" />" data-l10n-id="first_page"></menuitem>
		<menuitem id="contextLastPage" label="<spring:message code="pdf-viewer.last_page" />" data-l10n-id="last_page"></menuitem>
		<menuitem id="contextPageRotateCw" label="<spring:message code="pdf-viewer.page_rotate_cw" />" data-l10n-id="page_rotate_cw"></menuitem>
		<menuitem id="contextPageRotateCcw" label="<spring:message code="pdf-viewer.page_rotate_ccw" />" data-l10n-id="page_rotate_ccw"></menuitem>
	</menu>
</div>