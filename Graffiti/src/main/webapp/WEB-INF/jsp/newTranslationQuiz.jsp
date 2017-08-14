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
		<h2>Translation Quiz</h2>
		<c:set var="figuralHits" value="${figuralHits}" />
		<c:set var="translationHits" value="${translationHits}" />
		<%@include file="translation_greatest_hits.jsp"%>

		<!-- JS for modal view, toggling, show translations-->
		<script type="text/javascript" src="resources/js/greatestHits.js"></script></div>
</body>
</html>