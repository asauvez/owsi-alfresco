<%@ attribute name="result" required="true" rtexprvalue="true" type="fr.openwide.alfresco.component.query.form.result.FormQueryResult" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="alfapp" uri="http://app.alfresco.openwide.fr/tags/util" %>

<c:if test="${not empty result}">
	<table class="table table-striped table-bordered table-hover">
		<thead>
			<c:forEach var="column" items="${result.columns}">
				<c:if test="${column.visible}">
					<c:choose>
						<c:when test="${not empty column.itemComparator}">
							<alfapp:thSort id="${column.id}" message="${column.label}" pagination="${result.pagination}"/>
						</c:when>
						<c:otherwise>
							<th><spring:message message="${column.label}"/></th>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
		</thead>
		<tbody>
			<c:forEach var="row" items="${result.iterator}">
				<tr>
					<c:forEach var="cell" items="${row.iterator}">
						<c:set var="column" value="${cell.column}"/>
						<c:set var="value" value="${cell.value}"/>
						<c:if test="${column.visible}">
							<td class="${column.align}">
								<c:if test="${not empty value}">
									<c:choose>
										<c:when test="${column.view eq 'PLAIN'}">
											<c:out value="${value}"/>
										</c:when>
										<c:when test="${column.view eq 'HTML'}">
											<c:out value="${value}" escapeXml="false"/>
										</c:when>
										<c:when test="${column.view eq 'ICON'}">
											<c:set var="title"><spring:message message="${value.label}"/></c:set>
											<span class="${value.cssClass}" title="${title}"></span>
											<span class="sr-only"><c:out value="${title}" escapeXml="false"/></span>
										</c:when>
										<c:when test="${column.view eq 'NUMBER'}">
											<fmt:formatNumber value="${value}"/>
										</c:when>
										<c:when test="${column.view eq 'DATE'}">
											<fmt:formatDate value="${value}"/>
										</c:when>
										<c:when test="${column.view eq 'CHECKBOX'}">
											<input type="checkbox" name="${column.id}" value="${value}"/>
										</c:when>
										<c:when test="${column.view eq 'BUTTON'}">
											<c:set var="button" value="${value}"/>
											<c:if test="${button.visible}"> 
												<div class="btn-group">
													<c:set var="cssClass">
														<c:out value="${button.primary ? 'btn-primary' : 'btn-default'}"/>
														<c:if test="${not button.enabled}">disabled</c:if>
														<c:if test="${not empty button.dropDown}">dropdown-toggle</c:if>
														<c:out value="btn ${button.cssClass}"/>
													</c:set>
													<a href="${button.href}" data-ref="${button.currentValue}" role="button"
														data-toggle="${not empty button.dropDown ? 'dropdown' : ''}"
													 	class="${cssClass}">
														<c:if test="${not empty button.iconClass}">
															<i class="icon-user ${button.iconClass}"></i>&#160;
														</c:if>
														<spring:message message="${button.label}"/>
														<c:if test="${not empty button.dropDown}">
															&#160;<span class="caret"></span>
														</c:if>
													</a>
													<c:if test="${not empty button.dropDown}">
														<ul class="dropdown-menu">
															<c:forEach var="subButton" items="${button.dropDown}">
																<c:choose>
																	<c:when test="${empty subButton}">
																		<li class="divider"></li>
																	</c:when>
																	<c:when test="${subButton.visible}">
																		<li>
																			<c:set var="cssClass">
																				<c:if test="${not subButton.enabled}">disabled</c:if>
																				<c:out value="${subButton.primary ? 'btn-primary' : 'btn-default'}"/>
																				<c:out value="${subButton.cssClass}"/>
																			</c:set>
																			<a href="${subButton.href}" data-ref="${button.currentValue}" role="button"
																			 	class="${cssClass}">
																				<c:if test="${not empty subButton.iconClass}">
																					<i class="icon-user ${subButton.iconClass}"></i>&#160;
																				</c:if>
																				<spring:message message="${subButton.label}"/>
																			</a>
																		</li>
																	</c:when>
																</c:choose>
															</c:forEach>
														</ul>
													</c:if>
												</div>
											</c:if>
										</c:when>
										<c:when test="${column.view eq 'EMAIL'}">
											<c:set var="valueEscape"><c:out value="${value}"/></c:set>
											<a href="mailto:${valueEscape}"><c:out value="${value}"/></a>
										</c:when>
										<c:when test="${column.view eq 'EXTERNAL_LINK'}">
											<c:set var="valueEscape"><c:out value="${value}"/></c:set>
											<a href="${valueEscape}" target="_blank"><c:out value="${value}"/></a>
										</c:when>
										<c:when test="${column.view eq 'CUSTOM'}">
											<c:set var="cell" value="${cell}" scope="request"/>
											<c:set var="value" value="${value}" scope="request"/>
											<jsp:doBody/>
											<c:set var="value" value="" scope="request"/>
											<c:set var="cell" value="" scope="request"/>
										</c:when>
										<c:otherwise>
											<c:out value="Unknown view '${column.view}' !"/>
										</c:otherwise>
									</c:choose>
								</c:if>
							</td>
						</c:if>
					</c:forEach>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>
