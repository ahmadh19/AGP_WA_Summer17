 <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Pompeii Map</title>
<%@ include file="/resources/common_head.txt" %>


<link rel="stylesheet" href="https://unpkg.com/leaflet@1.1.0/dist/leaflet.css"
  integrity="sha512-wcw6ts8Anuw10Mzh9Ytw4pylW8+NAD4ch3lqm9lzAsTxg0GFeJgoAtxuCLREZSC5lUXdVyo/7yfsqFjQ4S+aKw=="
  crossorigin=""/>
<!-- <link rel="stylesheet" href="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.css" /> -->

 <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/resources/css/details.css"/>
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/main.css" />
<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiPropertyData.js"/>"></script>
</head>

<body>
<p></p>
<p></p>
<p></p>

<script src="https://unpkg.com/leaflet@1.1.0/dist/leaflet.js"
  integrity="sha512-mNqn2Wg7tSToJhvHcqfzLMU6J4mkOImSPTxVZAdo+lcPlk+GhZmYgACEe0x35K7YzW1zJ7XyJV/TT1MrdXvMcA=="
  crossorigin=""></script>
<!-- <script src="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.js"></script> -->

<div>
<div id="newDiv "></div>
<div id="pompeiimap" class="mapdiv"></div>

</div>

<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiMap.js"/>"></script>
<script>
	window.initpompmap();
</script>
</body>
</html>