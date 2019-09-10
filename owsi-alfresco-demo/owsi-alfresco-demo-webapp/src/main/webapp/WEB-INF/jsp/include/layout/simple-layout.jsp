<%@ include file="/WEB-INF/jsp/include/includes.jsp"%>

<!DOCTYPE html>
<html>
	<head>
		<%@ include file="/WEB-INF/jsp/include/layout/layout-head.jsp"%>
	</head>
	<body class="<decorator:getProperty property="page.bodyCssClass" />">
		<div class="container">
		
			<nav class="navbar navbar-default">
				<div class="container-fluid">
				</div>
			</nav>
			
			<decorator:body/>
			
			<%@ include file="/WEB-INF/jsp/include/layout/layout-footer.jsp"%>
		</div>
		
		<script type="text/javascript" src="${staticUrl}/${jqueryPath}/jquery.min.js"></script>

		<decorator:getProperty property="page.scripts"/>
	</body>
</html>