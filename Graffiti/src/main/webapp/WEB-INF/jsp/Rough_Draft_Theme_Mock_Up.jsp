<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<link rel="stylesheet" type="text/css" href="New-featured-graffiti.css" />
		
<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<!--    Fix responsive navbar-->
<%@include file="/resources/common_head.txt"%>
<link rel="stylesheet" type="text/css"
	href="resources/css/greatestHits.css">
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}
}

.folio-item {
	display: block;
	float: left;
	position: relative;
	-webkit-perspective: 1000px;
	perspective: 1000px;
	-webkit-perspective-origin: 50% 50%;
	perspective-origin: 50% 50%;
	-ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";
	filter: alpha(opacity = 0);
	opacity: 0;
}

.isotope-item {
	z-index: 2;
}
body {
	height: 100%;
	background-color: #f2f2f2;
}

.portfolio-grid {
	list-style: none;
	padding: 0;
	margin: 0 auto;
	text-align: center;
	width: 100%;
}

.portfolio-grid li {
	display: inline-block;
	margin: 5px 5px 5px 5px;
	vertical-align: top;
    width:212px;
}

.portfolio-grid li > a,
.portfolio-grid li > a img {
    width: 100%;
	border: none;
	outline: none;
	display: block;
	position: relative;
    transition: all 0.3s ease-in-out;
    -moz-transition: all 0.3s ease-in-out;
    -webkit-transition: all 0.3s ease-in-out;
    -o-transition: all 0.3s ease-in-out;
}

.portfolio-grid li > a img:hover {
    border-radius: 50%;
    z-index: 9999;
}
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="original">
	<!--  <h3>Graffiti from Herculaneum</h3>  -->
	<table class="table table-striped table-bordered"
		id="greatest_hits_table">
		<thead>
			<tr>
				<th id="idCol">ID</th>
				<th id="textCol">Text (Latin or Greek)</th>
				<th id="transCol">Translation</th>
				<!-- 
				<th id="imgCol">Commentary</th>
				 -->
			</tr>
		</thead>
		<!--  LOOOP  -->
		<tbody>
			<c:forEach var="i" items="${translationHits}">
				<tr>
					<td><a
						href="<%=request.getContextPath() %>/graffito/${i.agp.agpId}">${i.agp.agpId}</a>
						<c:if test="${not empty i.agp.cil }">
							<br />${i.agp.cil}
						</c:if> <c:if test="${not empty i.agp.langner }">
							<br />${i.agp.langner}
						</c:if></td>
					<td><em>${i.contentWithLineBreaks }</em></td>
					<td><c:if test="${not empty i.agp.contentTranslation }">
							<p class="trans" style="display: none;">
								<em>${i.agp.contentTranslation}</em>
							</p>
							<input type="button" class="btn btn-agp showTrans"
								value="Show Translation">
						</c:if></td>
						<!--  
					<td style="white-space: pre-wrap">${i.agp.greatestHitsInfo.commentary }</td>
						 -->
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</body>
</html>

