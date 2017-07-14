 <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ancient Graffiti Project :: Search Results</title>
<script type="text/javascript"
	src="<c:url value="/resources/js/jquery.imagemapster-1.2.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/resources/js/filterSearch.js"/>"></script>
<%@ include file="/resources/common_head.txt" %>
<link rel="stylesheet" href="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.css" />
<link rel="stylesheet" href="<%=request.getContextPath() %>/resources/css/main.css" />
<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiPropertyData.js"/>"></script>
	
<%@ page import= "java.util.*" %>

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

var locationKeys; 

function setLocationKeys(){
	<%
	List<String> locationKeys=(List<String>)request.getAttribute("findLocationKeys");
	if(locationKeys==null){
		locationKeys=new ArrayList();
	}
	System.out.println("Location keys reset!");
	%>
	locationKeys = "<%=locationKeys%>";
}

//setTimeout(function(){ map.invalidateSize()}, 1000);
//function generatePompeii(name) {
		//xmlHttp = new XMLHttpRequest();
		//xmlHttp.open("GET",
			//"map?clickedRegion="+name+"&city="+name, false); 
		
		//document.getElementById("pompeiimap").innerHTML = xmlHttp.responseText;
		//start();
//		window.initmap();
//}



function generateHerculaneum(name) {
	xmlHttp = new XMLHttpRequest();
	xmlHttp.open("GET",
			"map?clickedRegion="+name+"&second=yes"+"&city="+name, false); 
	xmlHttp.send(null);
	document.getElementById("herculaneumCityMap").innerHTML = xmlHttp.responseText;
	start();
}

function selectImg(ind, k, shortId, longId){
	if (ind == 0){
		document.getElementById("imgLink"+k).href = "http://www.edr-edr.it/edr_programmi/view_img.php?id_nr=" + longId;
		document.getElementById("imgSrc"+k).src = "http://www.edr-edr.it/foto_epigrafi/thumbnails/" + shortId + "/th_" + longId +".jpg";
	}
	else {
		document.getElementById("imgLink"+k).href = "http://www.edr-edr.it/edr_programmi/view_img.php?id_nr=" + longId + "-" + ind;
		document.getElementById("imgSrc"+k).src = "http://www.edr-edr.it/foto_epigrafi/thumbnails/" + shortId + "/th_" + longId + "-" + ind + ".jpg";	
	}
}

function selectImg2(ind, k, page, thumbnail){
	document.getElementById("imgLink"+k).href = page;
	document.getElementById("imgSrc"+k).src = thumbnail;
}

function checkAlreadyClicked(ids){
	idList = ids.split(";");
	for (var i = 0; i < idList.length-1; i++){
		$("#"+idList[i]).click();
	}
}

function checkboxesAfterBack() {
	contentsUrl = window.location.href;
	var params = contentsUrl.split("?")[1].split("&");
		
	var dict = {
			"drawing_category" : "dc",
			"property" : "p",
			"property_type" : "pt",
			"insula" : "i",
			"city" : "c",
			"writing_style" : "ws",
			"language" : "l"
	};
	
	var cities = {
			"Herculaneum" : 0,
			"Pompeii" : 1
	};
	
	var writingStyle = {
		"Graffito/incised" : 1,
		"charcoal" : 2,
		"other" : 3
	};

	
	var languages = {
		"Latin" : 1,
		"Greek" : 2,
		"Latin/Greek" : 3,
		"other" : 4
	};
	
	
	for (var i in params){
		if (params[i] != "query_all=false"){
			var param = params[i];
			var term = param.split("=");
			var type = term[0];
			var value = term[1];
			if (type == "drawing_category" && value == "All") {
				value = 0;
			}
			if (type in dict) {
				var typeToken = dict[type];
				// convert the human-readable description into IDs for checkboxes
				if (typeToken == "ws") {
					value = writingStyle[value];
				} else if (typeToken == "c") {
					value = cities[value];
				} else if (typeToken == "l") {
					value = languages[value];
				} else if (typeToken == "dc" && value == 0) {
					// do nothing if All is selected
					var locationKeys = "<%=locationKeys%>";		} else {
				value = value.replace("_", " ");
				}
				var id = typeToken+value;
				//type = type.replace("_", " ");
				//alert(id);import java.util.List;
				$("#"+id).click();
			} else if (type == "content") {
				addSearchTerm("Content", value, value);
			} else if (type == "global") {
				addSearchTerm("Global", value, value);
			}
		}
	}
}


function updatePage(){
	checkboxesAfterBack();
	
	<c:if test="${not empty sessionScope.returnFromEDR}">
	document.getElementById("${sessionScope.returnFromEDR}").scrollIntoView();
	<c:set var="returnFromEDR" value="" scope="session" />
	</c:if>
	
}

</script>	

<style>
th {
	vertical-align: top;
	width: 185px;
}

.main-table {
	max-width: 475px;
	min-width: 375px;
}

hr.main-table {
	margin-left: 0px;
}

.scroll_top {
	float: right;
	position: fixed;
	color: white;
	cursor: pointer;
	align: right;
	display: inline;
Graffito

	
	margin: 225px 0px 0px 560px;
}

.map-override1 {
	position: static
}

.btn-agp {
	margin-bottom: 10px;
}

ul#searchTerms li {
    display:inline-block;
    margin: 0 0 7px 0;
}
</style>
	
</head>

<body onload="updatePage();">
<%@include file="header.jsp"%>

<div id="contain" class="container" style="margin-bottom: 50px;">
		<%@include file="sidebarSearchMenu.jsp"%>
		<!--  SideBar Map  -->
		
		<div id="pompeiimap" class="searchResultsPompeii"></div>
		<div id="herculaneumCityMap" class="searchResultsHerculaneum"></div>
		
		

		<div style="margin-left: 200px;">
			<div style="width: 475px; padding-bottom: 10px;">
				<ul id="searchTerms" style="width: 525px; margin-left: -40px;"></ul>
			</div>
			<div id="search-results"> 
				<%@include file="filter.jsp"%>
			</div>
		</div>
	</div>


<script src="https://npmcdn.com/leaflet@1.0.0-rc.2/dist/leaflet.js"></script>


<script type="text/javascript"
	src="<c:url value="/resources/js/pompeiiMap.js"/>"></script>
	
<script type="text/javascript">
	generateHerculaneum("Herculaneum");
</script>

<script>
	setLocationKeys();
    
	window.initmap(true,false,false,false,0,locationKeys);
</script>
</body>



</html>