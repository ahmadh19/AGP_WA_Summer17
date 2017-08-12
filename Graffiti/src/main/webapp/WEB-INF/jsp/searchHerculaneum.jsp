
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ancient Graffiti Project :: Herculaneum Map</title>
<%@ include file="/resources/common_head.txt"%>
<link rel="stylesheet"
	href="https://unpkg.com/leaflet@1.1.0/dist/leaflet.css"
	integrity="sha512-wcw6ts8Anuw10Mzh9Ytw4pylW8+NAD4ch3lqm9lzAsTxg0GFeJgoAtxuCLREZSC5lUXdVyo/7yfsqFjQ4S+aKw=="
	crossorigin="" />

<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />
<script type="text/javascript"
	src="<c:url value="/resources/js/herculaneumPropertyData.js"/>"></script>
</head>

<body>
	<%@include file="header.jsp"%>
	<div class="container">
		<h2>Search Herculaneum by Map</h2>
		<p>Click on one or more properties within the map, then hit the
			"Search" button below.</p>
		<div id="moreInfo">
			<button id="search" class="btn btn-agp">Search Properties</button>
		</div>

		<script src="https://unpkg.com/leaflet@1.1.0/dist/leaflet.js"
			integrity="sha512-mNqn2Wg7tSToJhvHcqfzLMU6J4mkOImSPTxVZAdo+lcPlk+GhZmYgACEe0x35K7YzW1zJ7XyJV/TT1MrdXvMcA=="
			crossorigin=""></script>

		<div>
			<div id="newDiv"></div>
			<div id="herculaneummap" class="mapdiv"></div>
		</div>
	</div>

	<script type="text/javascript"
		src="<c:url value="/resources/js/herculaneumMap.js"/>"></script>
	<script>
		window.inithercmap();
	</script>
</body>
</html>