<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ancient Graffiti Project :: Search by Pompeii Map</title>
<%@ include file="/resources/common_head.txt"%>
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.2.0/dist/leaflet.css"
   integrity="sha512-M2wvCLH6DSRazYeZRIm1JnYyh22purTM+FDB5CsyxtQJYeKq83arPe5wgbNmcFXGqiSH2XR8dT/fJISVA1r/zQ=="
   crossorigin=""/>
 <!-- Make sure you put this AFTER Leaflet's CSS -->
 <script src="https://unpkg.com/leaflet@1.2.0/dist/leaflet.js"
   integrity="sha512-lInM/apFSqyy1o6s89K4iQUKg6ppXEgsVxT35HbzUupEVRh2Eu9Wdl4tHj7dZO0s1uvplcYGmt3498TtHq+log=="
   crossorigin=""></script>

<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />
<link rel="stylesheet"
	href="<c:url value="resources/js/leaflet-compass-master/src/leaflet-compass.css"/>"/>
<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiPropertyData.js"/>">
</script>
<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiWallsPropertyData.js"/>">
</script>

<style>
button {
	margin-top: 200%;
}
</style>
</head>
<body>
	<%@include file="header.jsp"%>
	<div class="container">
		<h2>Search Pompeii by Map</h2>
		<p>Click on one or more properties within the map, then hit the
			"Search" button below.</p>
		<div id="moreInfo">
			<button id="search" class="btn btn-agp">Search Properties</button>
		</div>
		<script
			src="<c:url value="resources/js/leaflet-compass-master/src/leaflet-compass.js"/>"></script>
		<div>
			<div id="newDiv"></div>
			<div id="pompeiimap" class="mapdiv"></div>
		</div>
	</div>
	<script type="text/javascript"
		src="<c:url value="/resources/js/pompeiiMap.js"/>"></script>
	<script>
		window.initpompmap();
	</script>
</body>
</html>