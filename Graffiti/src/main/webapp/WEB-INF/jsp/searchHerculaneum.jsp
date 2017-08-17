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
<body>
	<%@include file="header.jsp"%>
	<div class="container">
		<h2>Search Herculaneum by Map</h2>
		<p>Click on one or more properties within the map, then hit the
			"Search" button.</p>
		<div id="herculaneummap" class="mapdiv"></div>
		<div id="search_info">
			<div id="toSearch"></div>
			<button id="search" class="btn btn-agp">Search</button>
		</div>
	</div>
	<script>
		window.initHerculaneumMap();
	</script>
</body>
</html>