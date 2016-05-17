<sec:authorize var="isAuthenticated" access="isAuthenticated()"/>
<c:if test="${isAuthenticated}">
	<sec:authorize var="isGestionnaire" access="hasAuthority('GROUP_GF_GEST')"/>
	<sec:authorize var="isInstructeur" access="hasAuthority('GROUP_GF_INS')"/>
	<sec:authorize var="isMedecin" access="hasAuthority('GROUP_GF_MEDECINS')"/>
	<sec:authorize var="isPsychologue" access="hasAuthority('GROUP_GF_PSY')"/>
	<sec:authorize var="isTravailleurSocial" access="hasAuthority('GROUP_GF_TS')"/>
	<sec:authorize var="isObservateur" access="hasAuthority('GROUP_GF_CONSULT')"/>
	<sec:authorize var="isUtilisateur" access="${isGestionnaire or isInstructeur or isMedecin or isPsychologue or isTravailleurSocial or isObservateur}"/>
</c:if>