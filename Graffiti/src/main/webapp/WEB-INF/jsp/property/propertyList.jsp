<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">	
<title>Ancient Graffiti Project :: Filter Results</title>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
<%@include file="../../../resources/common_head.txt"%>
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.imagemapster-1.2.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/filterSearch.js"/>"></script>


</head>
<body>
	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<div class="container">

		<div class="button_bar">
		<a href="<%=request.getContextPath() %>/allProperties/download/csv"
			id="csv">
			<button class="btn btn-agp right-align">Export as CSV</button>
		</a>
		<a href="<%=request.getContextPath() %>/allProperties/download/json"
			id="csv">
			<button class="btn btn-agp right-align">Export as JSON</button>
		</a>
		</div>
		
		<h1>Property List</h1>
		
		<table class="table table-bordered table-striped" style="margin-bottom: 30px;" cellpadding="5"
			cellspacing="5" border="1">
			<tr>
				<th>City</th>
				<th>Insula</th>
				<th>Number</th>
				<th>Name</th>
				<th>Type</th>
				<th>Property Metadata Page</th>
			</tr>
			<c:forEach var="k" begin="${1}" end="${fn:length(requestScope.properties)}">
				<c:set var="prop" value="${requestScope.properties[k-1]}"/>
				<tr>
					<td>${prop.insula.modernCity }</td>
					<td>${prop.insula.shortName}</td>
					<td>${prop.propertyNumber}</td>
					<td>${prop.propertyName}</td>
					<td>
						<c:forEach var="l" begin="${1}" end="${fn:length(prop.propertyTypes)}">
							<c:set var="pt" value="${prop.propertyTypes[l-1]}"/>
							${pt.name}
						</c:forEach>
					</td>
					<td><a href="http://${prop.uri}">${prop.uri}</a></td>
				</tr>
			</c:forEach>
		</table>
	</div>

</body>
</html>