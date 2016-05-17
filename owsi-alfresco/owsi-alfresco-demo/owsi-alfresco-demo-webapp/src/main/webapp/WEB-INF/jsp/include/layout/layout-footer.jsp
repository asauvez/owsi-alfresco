<div class="panel panel-default layout-footer">
	<div class="panel-body">
		<div class="row">
			<div class="col-md-6">
				<%-- Contact sous la forme d'un bouton, au cas où un changement de présentation est demandé
				<button id="mdphMessage" type="button" class="btn btn-default" data-js-onclick="mailto:<mdphapp:contactMailto />;">
					<spring:message code="contact.label" var="contactLabel" />
					<span class="glyphicon glyphicon-envelope" title="${contactLabel}"></span>
					${contactLabel}
				</button>
				--%>
				<ul class="nav nav-pills">
					<li>
						<a href="mailto:<mdphapp:contactMailto />">
							<spring:message code="contact.label" var="contactLabel" />
							<i class="glyphicon glyphicon-comment" title="${contactLabel}"></i>
							${contactLabel}
						</a>
					</li>
				</ul>
			</div>
			<div class="col-md-6 text-right text-muted">
				<h6>
					<em>
						<spring:message code="application.title"/> version ${version}
					</em>
				</h6>
			</div>
		</div>
	</div>
</div>