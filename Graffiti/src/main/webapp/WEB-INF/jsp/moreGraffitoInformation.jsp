 <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>More Graffito Information</title>
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.imagemapster-1.2.js" />"></script>
<%@ include file="/resources/common_head.txt" %>
<link rel="stylesheet" href="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.css" />
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/main.css" />
<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiPropertyData.js"/>"></script>


	
</head>

<body onload="updatePage();">
<%@include file="header.jsp"%>



<div id="pompeiimap" class="findspotMap"></div>


<script src="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.js"></script>


<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiMap.js"/>"></script>

<h4>Findspot on map:</h4>
<script>
	window.initmap(true,false,true,false);
	
	document.write(document.getElementById("imgLink").href = hrefs[ind-1]+" imgLink");
	document.write(document.getElementById("imgSrc").src = srcs[ind-1]+" imgSrc");
</script>


</body>
</html>