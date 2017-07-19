<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="edu.wlu.graffiti.bean.Theme"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>

<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<%@include file="/resources/common_head.txt"%>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}
}
</style>


</head>
<body>

	<%@include file="header.jsp"%>
	<%
		Theme theme = (Theme) request.getAttribute("theme");
	%>
	<div class="container">

		<h2><%=theme.getName()%> Graffiti</h2>

		<c:set var="inscriptions" value="${inscriptions}" />

		<div>
			<table class="table table-striped table-bordered"
				id="themedGraffiti">
				<thead>
					<tr>
						<th id="idCol">ID</th>
						<th id="textCol">Text (Latin or Greek) / English Translation</th>
						<th id="transCol">Commentary</th>
					</tr>
				</thead>
				
				<tbody>

					<c:forEach var="i" items="${inscriptions}">
						<tr>
							<td>${i.agp.edrId}<br>${i.agp.cil}</td>
							<td> <p> Text: ${i.contentWithLineBreaks} </p>
								 <p> Translation: ${i.agp.contentTranslation} </p>
							</td>
							<td>${i.agp.commentary}</td>
						</tr>
					</c:forEach>

				</tbody>
			</table>
		</div>
	</div>

</body>
</html>