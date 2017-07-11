<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>

<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<!--    Fix responsive navbar-->
<%@include file="/resources/common_head.txt"%>
<link rel="stylesheet" type="text/css"
	href="resources/css/greatestHits.css">
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}
}
</style>
<!--  Function to hide the gallery on load -->
<script>
	$(document).ready(function() {
		$("#gallery").hide();
	});
</script>

</head>
<body>
	
	<%@include file="header.jsp"%>
	<div class="container">

		<h2>Generic Theme</h2>

		<c:set var="translationHits" value="${translationHits}" />

		<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="original">
	<!--  <h3>Graffiti from Herculaneum</h3>  -->
	<table class="table table-striped table-bordered"
		id="greatest_hits_table">
		<thead>
			<tr>
				<th id="idCol">ID</th>
				<th id="textCol">Text (Latin or Greek) / English Translation</th>
				<th id="transCol">Commentary</th>
				
				<!-- 
				<th id="imgCol">Commentary</th>
				 -->
			</tr>
		</thead>
		<!--  LOOOP  -->
		<tbody>
			<c:forEach var="i" items="${translationHits}">
				<tr>
					<td><a
						href="<%=request.getContextPath() %>/graffito/${i.agp.agpId}">${i.agp.agpId}</a>
						<c:if test="${not empty i.agp.cil }">
							<br />${i.agp.cil}
						</c:if> <c:if test="${not empty i.agp.langner }">
							<br />${i.agp.langner}
						</c:if></td>
					<td><em>${i.contentWithLineBreaks }</em></td>
					<td><c:if test="${not empty i.agp.contentTranslation }">
							<p class="trans" style="display: none;">
								<em>${i.agp.contentTranslation}</em>
							</p>
						Hello world!
						</c:if></td>
						<!--  
					<td style="white-space: pre-wrap">${i.agp.greatestHitsInfo.commentary }</td>
						 -->
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

	<!-- JS for modal view, toggling, show translations-->
	<script type="text/javascript" src="resources/js/greatestHits.js"></script></div>

</body>
</html>