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

<style>

button{
	margin-top:200%;
}
</style>
</head>

<body>
<%@include file="header.jsp"%>

<script>
//Adjusts the positions of each element on-load based on the size of the window.
//This should be sufficient for most screens(most people do not use double screens like we have in the lab)
function setButton(){
	if(window.innerWidth!='undefined' && window.innerWidth!=null){
		var mapHeight=document.getElementById("pompeiimap").offsetHeight;
		var windowHeight=
		
		document.getElementById("pompeiimap").style.marginLeft=newLeftMargin.toString()+"px";
		
	}
	
}

</script>

<div class="container">
	<h2>Search Pompeii by Map</h2>
	<p>Click on one or more properties within the map, then hit the "Search" button below.</p>
	<div id="moreInfo"><button id="search" class="btn btn-agp">Search Properties</button></div>
	
	<script src="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.js"></script>
	
	<div>
	<div id="newDiv"></div>
	<div id="pompeiimap" class="mapdiv"></div>
	
	</div>
	
	
</div>


<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiMap.js"/>"></script>
<script>
	window.initmap();
	
</script>
</body>
</html>