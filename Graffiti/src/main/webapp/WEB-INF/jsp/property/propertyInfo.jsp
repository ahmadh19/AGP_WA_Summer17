
<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Ancient Graffiti Project :: Property Info</title>

<%@include file="/resources/common_head.txt"%>
<!-- this is the stuff for leaflet map -->
<link rel="stylesheet"
	href="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.css" />
<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiPropertyData.js"/>"></script>
<script src="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.js"></script>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css" />

<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.imagemapster-1.2.js" />"></script>
<link rel="stylesheet" type="text/css"
	href="<%=request.getContextPath()%>/resources/css/details.css" />
<script src="<c:url value="/resources/js/pompeiiMap.js"/>"></script>
<script type="text/javascript"> 
function start() {
	$('img').mapster({
		areas: [
		        
			<c:forEach var="locKey" items="${requestScope.findLocationKeys}">
			{
				key: '${locKey}',
				fillColor: '0000FF',
				staticState: true
			},
			</c:forEach>
		], 
		isSelectable: false,
		mapKey: 'data-key',
		clickNavigate: false,
	}); 
}

function generateMap(name) {
	xmlHttp = new XMLHttpRequest();
	path = "<%=request.getContextPath()%>/";
	xmlHttp.open("GET", path +
			"map?clickedRegion="+name+"&city="+name, false); 
	xmlHttp.send(null);
	document.getElementById("cityMap").innerHTML = xmlHttp.responseText;
	//document.getElementById("pompeiimap").innerHTML = xmlHttp.responseText;
	start();
}
</script>
</head>
<body>

	
	<!-- this script is also for leaflet -->
	<script src="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.js"></script>

	<%@include file="/WEB-INF/jsp/header.jsp"%>
	<c:set var="i" value="${requestScope.inscription}" />
	<div class="container">
		<h1>Property</h1>

		<div class="button_bar">
			<a
				href="<%=request.getContextPath() %>/property/${prop.insula.city.name}/${prop.insula.shortName}/${prop.propertyNumber }/json"
				id="json"><button class="btn btn-agp right-align">Export
					JSON Data</button></a>
		</div>

		<table class="property-table table-striped">
			<tr>
				<th class="propertyLabel">City:</th>
				<td><a
					href="http://pleiades.stoa.org/places/${prop.insula.city.pleiadesId}">${prop.insula.modernCity}</a></td>
			</tr>
			<tr>
				<th class="propertyLabel">Insula:</th>
				<td>${prop.insula.fullName}</td>
			</tr>
			<tr>
				<th class="propertyLabel">Property Number:</th>
				<td>${prop.propertyNumber}</td>
			</tr>
			<tr>
				<th class="propertyLabel">Property Name<br />(in English):
				</th>
				<td>${prop.propertyName}</td>
			</tr>
			<c:if test="${not empty prop.italianPropertyName}">
				<tr>
					<th class="propertyLabel">Property Name<br />(in Italian):
					</th>
					<td>${prop.italianPropertyName}</td>
				</tr>
			</c:if>

			<c:if test="${not empty prop.pleiadesId}">
				<tr>
					<th class="propertyLabel">Pleiades ID:</th>
					<td>${prop.pleiadesId }</td>
				</tr>
			</c:if>
			<tr>
				<th class="propertyLabel">Property Type:</th>
				<td>${prop.propertyTypesAsString}</td>
			</tr>
			<c:if test="{not empty prop.commentary}">
				<tr>
					<th class="propertyLabel">Commentary:</th>
					<td>${prop.commentary }</td>
				</tr>
			</c:if>class="propertyMetadataMap"
			<c:if test="${prop.numberOfGraffiti > 0}">
				<tr>
					<th class="propertyLabel">Archaeological Context:</th>
					<td>
						<a href="<%=request.getContextPath()%>/results?property=${ prop.id}">
						Graffiti in this property</a>
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="propertyLabel">Links:</th>
				<td>
					<c:if test="${prop.insula.modernCity=='Pompeii'}">
						<a href="${prop.pompeiiinPicturesURL}">Pompeii in Pictures</a>
						<!--<br/><a href="${prop.plodURL}">P-LOD Linked Open Data</a>-->
					</c:if>
					<c:if test="${prop.insula.modernCity=='Herculaneum'}">
						<a href="http://donovanimages.co.nz/proxima-veritati/Herculaneum/">
						Herculaneum Panoramas</a>
						<br/><a href="http://www.pompeiisites.org/Sezione.jsp?titolo=Visita%20agli%20scavi&idSezione=94">
						Parco Archaeologico</a>
					</c:if>
				</td>
			</tr>
		</table>
		<div id="map">
			<div id="cityMap" class="propertyMetadataMap"></div>
			<div id="pompeiimap" class="propertyMetadataMap"></div>
		</div>
		
	</div>
	
	<!-- 
	<script type="text/javascript"
		src="<c:url value="/resources/js/pompeiiMap.js"/>">
	</script>
	<script>
		window.initmap();
	</script>
	 -->

	<script type="text/javascript">
	if("${prop.insula.city.name}"=="Herculaneum"){
		generateMap("${prop.insula.city.name}");
	}
	else if("${prop.insula.city.name}"=="Pompeii"){
		window.initmap(true,false,false,false,"${prop.id}");
		
	}

	

	
	</script>
	
</body>
</html>