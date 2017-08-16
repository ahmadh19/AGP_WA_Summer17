<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ancient Graffiti Project :: Herculaneum Map</title>
<%@ include file="/resources/common_head.txt"%>
<%@ include file="/resources/leaflet_common.txt"%>
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
		<div>
			<div id="newDiv"><button id="search" class="btn btn-agp" style="float:left;">Search Properties</button></div>
			<div id="herculaneummap" class="mapdiv"></div>
		</div>
		

	</div>
	<script type="text/javascript"
		src="<c:url value="/resources/js/herculaneumMap.js"/>"></script>
	<script>
		window.initHerculaneumMap();
	</script>
</body>
</html>