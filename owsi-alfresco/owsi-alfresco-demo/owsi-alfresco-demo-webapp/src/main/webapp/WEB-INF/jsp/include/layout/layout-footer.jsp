<div class="panel panel-default">
	<div class="panel-body">
		<div class="row">
			<div class="col-md-6">
				<spring:message code="contact.mail" var="mailContact"/>
				<a href="mailto: ${mailContact}">
					<spring:message code="contact.label" var="contactLabel" />
					<i class="glyphicon glyphicon-comment" title="${contactLabel}"></i>
					${contactLabel}
				</a>
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