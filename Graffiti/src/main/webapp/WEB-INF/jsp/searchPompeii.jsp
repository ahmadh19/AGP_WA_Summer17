<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ancient Graffiti Project :: Search by Pompeii Map</title>
<%@ include file="/resources/common_head.txt"%>
<%@ include file="/resources/leaflet_common.txt"%>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />
<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiPropertyData.js"/>">
</script>
<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiWallsData.js"/>">
</script>

<script type="text/javascript"
		src="<c:url value="/resources/js/pompeiiMap.js"/>"></script>
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
		<div>
			<div id="selectionDiv"></div>
			<div id="pompeiimap" class="mapdiv"></div>
		</div>
	</div>
	<script>
		window.initPompeiiMap();
	</script>
</body>
</html>