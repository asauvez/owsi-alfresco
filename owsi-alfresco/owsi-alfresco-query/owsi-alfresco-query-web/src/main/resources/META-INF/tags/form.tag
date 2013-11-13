<%@ attribute name="query" required="true" rtexprvalue="true" type="fr.openwide.alfresco.query.web.search.model.AbstractFormQuery" %>
<%@ attribute name="name" required="true" rtexprvalue="true" type="java.lang.String" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="formQuery" uri="http://app.alfresco.openwide.fr/tags/formQuery" %>

<form:form modelAttribute="${name}" method="get" role="form" id="formQuery">
	<form:errors element="div" cssClass="alert alert-error" />

	<c:forEach var="fieldSet" items="${query.inputFieldBuilder.fieldSets}">
		<c:if test="${fieldSet.visible}">
			<fieldset class="${fieldSet.inRow ? 'row' : ''}">
			 	<c:if test="${not empty fieldSet.label}">
					<legend><spring:message message="${fieldSet.label}"/></legend>
			 	</c:if>
				<c:forEach var="inputField" items="${fieldSet.inputFields}">
					<c:if test="${inputField.visible}">
						<div class="${fieldSet.inRow ? inputField.rowSpan : ''}${fieldSet.inRow ? inputField.rowOffset : ''}">
							<c:set var="fieldError">
								<form:errors cssClass="help-block" path="${inputField.name}" />
							</c:set>
							<div class="form-group ${not empty fieldError ? 'has-error' : ''}">
							
								<label for="${inputField.name}" class="control-label">
									<spring:message message="${inputField.label}"/>
								</label>
								<c:choose>
									<c:when test="${inputField.view eq 'TEXT'}">
										<c:set var="placeholder">
											<c:if test="${not empty inputField.placeholder}">
												<spring:message message="${inputField.placeholder}"/>
											</c:if>
										</c:set>
										<form:input path="${inputField.name}" placeholder="${placeholder}" cssClass="form-control"/>
									</c:when>
									<c:when test="${inputField.view eq 'DATE'}">
										<form:input path="${inputField.name}" cssClass="form-control datepicker"/>
									</c:when>
									<c:when test="${inputField.view eq 'SELECT'}">
										<form:select path="${inputField.name}" cssClass="form-control">
											<c:forEach var="item" items="${inputField.items}">
												<form:option value="${item.key}">
													<spring:message message="${item.label}"/>
												</form:option>
											</c:forEach>
										</form:select>
									</c:when>
									<c:when test="${inputField.view eq 'RADIO'}">
										<c:forEach var="item" items="${inputField.items}">
											<label class="radio-inline">
												<form:radiobutton path="${inputField.name}" value="${item.key}" />
												<spring:message message="${item.label}"/>
											</label>
										</c:forEach>
									</c:when>
									<c:when test="${inputField.view eq 'CUSTOM'}">
										<c:set var="inputField" value="${inputField}" scope="request"/>
										<jsp:doBody/>
										<c:set var="inputField" value="" scope="request"/>
									</c:when>
									<c:otherwise>
										<c:out value="Unknown view '${inputField.view}' !"/>
									</c:otherwise>
								</c:choose>
								<c:out value="${fieldError}" escapeXml="false"/>											
								<c:if test="${not empty inputField.description}">
									<p class="help-block">
										<spring:message message="${inputField.description}"/>
									</p>
								</c:if>
							</div>
						</div>
					</c:if>
				</c:forEach>
			</fieldset>
		</c:if>
	</c:forEach>
	
	<input type="hidden" id="pagination-sortColumn" name="pagination.sortColumn" value="${query.pagination.sortColumn}">
	<input type="hidden" id="pagination-sortDirection" name="pagination.sortDirection" value="${query.pagination.sortDirection}">
	<input type="hidden" id="pagination-currentPage" name="pagination.currentPage" value="${query.pagination.currentPage}">
	
	<c:if test="${not empty query.inputFieldBuilder.fieldSets}">
		<div>
			<input type="submit" value="Recherche" class="btn btn-primary"/>
			<button type="reset" class="btn btn-link">Annuler</button>
		</div>
	</c:if>
</form:form>
			