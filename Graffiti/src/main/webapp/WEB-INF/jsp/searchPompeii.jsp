 <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Pompeii Map</title>
<%@ include file="/resources/common_head.txt" %>
<link rel="stylesheet" href="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.css" />

 <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/resources/css/details.css"/>
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/main.css" />
<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiPropertyData.js"/>"></script>
</head>

<body>
<header id="top" class="navbar navbar-static-top bs-docs-nav" role="banner">
<div class="navbar navbar-inverse navbar-fixed-top">
<div class="container-fluid" style="padding: 0 25px;">
<div class="navbar-header">
<button class="navbar-toggle" type="button" data-toggle="collapse" data-target=".bs-navbar-collapse" style="width: 45px;">
</div>
<nav class="collapse navbar-collapse bs-navbar-collapse" role="navigation">
<ul id="nav" class="nav navbar-nav">
<li>
<a href="/Graffiti/">Home</a>
</li>
<li>
<a href="/Graffiti/results?query_all=true">Browse All Inscriptions</a>
</li>
<li>
<a href="/Graffiti/results?drawing_category=All">Browse Figural Graffiti (Drawings)</a>
</li>
<li>
<a href="/Graffiti/search?city=Herculaneum">Search Herculaneum</a>
</li>
<li>
<a href="/Graffiti/search?city=Pompeii">Search Pompeii</a>
</li>
<li>
<a href="/Graffiti/featured-graffiti">Featured Graffiti</a>
</li>
<li>
<a href="/Graffiti/New-featured-graffiti">New Featured Graffiti</a>
</li>
<li>
<a href="/about">About the Project</a>
</li>
</ul>
</nav>
</div>
</div>
<div id="Jumbo" class="block">
<div class="jumbotron">
<div class="container">
<h1>The Ancient Graffiti Project Search Engine</h1>
<p>A digital resource for studying the graffiti of Herculaneum and Pompeii</p>
</div>
</div>
</div>
</header>

<h1>Search Pompeii by Map</h1>
<p>Click on one or more properties within the map, then hit the "Search" button below.</p>
<script src="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.js"></script>

<div>
<div id="newDiv"></div>
<div id="pompeiimap" class="mapdiv"></div>

</div>

<div id="moreInfo"><button id="search" class="btn btn-agp">Click here to search</button></div>

<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiMap.js"/>"></script>
<script>
	window.initmap();
</script>
</body>
</html>