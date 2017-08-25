<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Ancient Graffiti Project</title>
<%@include file="../../resources/common_head.txt"%>
<style>
.fluid-img {
	margin-right: auto;
	margin-left: auto;
	max-height: 325px;
	max-width: 100%;
	width: auto;
	border: 3px solid black;
}

.footer-img {
	display: inline-block;
	margin-left: 10px;
	margin-right: 10px;
	width: 110px;
}

.leftcol {
	float: left;
	width: 50%;
	margin-bottom: 25px;
}

.rightcol {
	float: right;
	width: 50%;
	margin-bottom: 25px;
}

h3 {
	text-align: center;
}

.alert-info {
	color: black;
	background-color: beige;
	border-color: lightgray;
	background-image: none;
}

@media only screen and (max-width: 1023px) {
	[class*="col"] {
		width: 100%;
	}
}
</style>

</head>
<body>
	<%@include file="header.jsp"%>
	<div class="container">
		<%--Check for error message informing user of invalid city name or inscription id --%>
		<c:if test="${not empty requestScope.error}">
			<p style="color: red;">${requestScope.error}
			<p />
		</c:if>
		<div>
			<p class="alert alert-info">This site is an active work in progress. More updates are coming soon.</p>
		</div>
		<p style="font-size: 16px; text-align: center;">Welcome to The
			Ancient Graffiti Project, a digital resource and search engine for locating and
			studying graffiti of the early Roman empire. More than 500 ancient graffiti are now
			available here, ca. 300 from Herculaneum and another 200 from Pompeii
			(from the Lupanar, Insula I.8, and other locations). <br/>Entries for the
			Herculaneum graffiti now include photographs from our fieldwork in
			2014-2016.</p>
	</div>
	<!-- <h2 style="text-align:center;">Click on a map to search</h2> -->
	<div style="max-width: 1100px; float: center; margin: auto;">
		<div class="leftcol">
			<h3>Herculaneum</h3>
			<a href="searchHerculaneum"><img class="fluid-img"
				src="<%=request.getContextPath()%>/resources/images/Herculaneum.jpg"
				onmouseover="this.src='<%=request.getContextPath()%>/resources/images/exploreHerculaneum.jpg'"
				onmouseout="this.src='<%=request.getContextPath()%>/resources/images/Herculaneum.jpg'" /></a>
		</div>

		<div class="rightcol">
			<h3>Pompeii</h3>
			<a href="searchPompeii"><img class="fluid-img"
				src="<%=request.getContextPath()%>/resources/images/Pompeii.jpg"
				onmouseover="this.src='<%=request.getContextPath()%>/resources/images/explorePompeii.jpg'"
				onmouseout="this.src='<%=request.getContextPath()%>/resources/images/Pompeii.jpg'" /></a>
		</div>
	</div>
	<p style="text-align: center;">
		Special acknowledgements to <a
			href="http://digitalhumanities.umass.edu/pbmp/">Eric Poehler</a> for
		the geospatial data used to create the map of Pompeii.
	</p>
	<footer>
		<p style="text-align: center;">
			<br />This work is licensed under a <a rel="license"
				href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative
				Commons Attribution-NonCommercial-ShareAlike 4.0 International
				License</a>
		</p>
		<p style="text-align: center;">
			<a href="https://creativecommons.org/licenses/by-nc-sa/4.0/"> <img
				class="footer-img" style="height: 40px;"
				src="<%=request.getContextPath()%>/resources/images/cc_license.png"
				alt="CC License"></a> <a href="http://www.neh.gov/"> <img
				class="footer-img" style="height: 26px;"
				src="http://www.neh.gov/files/neh_at_logo.png" alt="NEH"></a> <a
				href="http://digitalhumanities.wlu.edu/"> <img
				class="footer-img" style="height: 40px;"
				src="http://ancientgraffiti.wlu.edu/files/2016/07/dh_at_wandl.png"
				alt="W&L Digital Humanitites"></a> <a href="https://mellon.org/">
				<img class="footer-img" style="height: 40px;"
				src="http://ancientgraffiti.wlu.edu/files/2015/06/mellon-e1467740285109.jpeg"
				alt="Mellon Foundation">
			</a> <a href="http://chs.harvard.edu/"> <img class="footer-img"
				style="height: 62px;"
				src="http://ancientgraffiti.org/about/wp-content/uploads/2017/06/CHS.png"
				alt="CHS Harvard"></a> <a
				href="https://github.com/AncientGraffitiProject/AGP"> <img
				class="footer-img" style="height: 40px;"
				src="<%=request.getContextPath()%>/resources/images/octocat.png"
				alt="Github"></a>
		</p>
	</footer>
</body>
</html>
