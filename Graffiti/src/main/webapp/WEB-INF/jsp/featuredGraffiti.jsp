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
	width: 75%;
}

.portfolio-grid li {
	display: inline-block;
	margin: 25px 25px 25px 25px;
	vertical-align: top;
	width: 175px;
}

.portfolio-grid li>a, .portfolio-grid li>a img {
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

.portfolio-grid li>a img:hover {
	border-radius: 50%;
	z-index: 9999;
}

a.darken {
	display: inline-block;
	background: black;
	padding: 0;
}

a.darken span {
	display: none;
	position: absolute;
	top: 65px;
	left: 0px;
	padding: 10px;
	float: left;
	color: white;
}

a.darken img {
	display: block;
	-webkit-transition: all 0.25s linear;
	-moz-transition: all 0.25s linear;
	-ms-transition: all 0.25s linear;
	-o-transition: all 0.25s linear;
	transition: all 0.25s linear;
}

a.darken:hover img {
	opacity: 0.25;
}

/******************************************
 Animate.CSS By Dan Eden
******************************************/
.animated {
	-webkit-animation-duration: 1s;
	-moz-animation-duration: 1s;
	-o-animation-duration: 1s;
	animation-duration: 1s;
	-webkit-animation-fill-mode: both;
	-moz-animation-fill-mode: both;
	-o-animation-fill-mode: both;
	animation-fill-mode: both;
}

@
-webkit-keyframes flipInX { 0% {
	-webkit-transform: perspective(400px) rotateX(90deg);
	opacity: 0;
}

40%
{
-webkit-transform




:


 


perspective




(400
px


)
rotateX




(-10
deg


);
}
70%
{
-webkit-transform




:


 


perspective




(400
px


)
rotateX




(10
deg


);
}
100%
{
-webkit-transform




:


 


perspective




(400
px


)
rotateX




(0
deg


);
opacity




:


 


1;
}
}
@
-moz-keyframes flipInX { 0% {
	-moz-transform: perspective(400px) rotateX(90deg);
	opacity: 0;
}

40%
{
-moz-transform




:


 


perspective




(400
px


)
rotateX




(-10
deg


);
}
70%
{
-moz-transform




:


 


perspective




(400
px


)
rotateX




(10
deg


);
}
100%
{
-moz-transform




:


 


perspective




(400
px


)
rotateX




(0
deg


);
opacity




:


 


1;
}
}
@
-o-keyframes flipInX { 0% {
	-o-transform: perspective(400px) rotateX(90deg);
	opacity: 0;
}

40%
{
-o-transform




:


 


perspective




(400
px


)
rotateX




(-10
deg


);
}
70%
{
-o-transform




:


 


perspective




(400
px


)
rotateX




(10
deg


);
}
100%
{
-o-transform




:


 


perspective




(400
px


)
rotateX




(0
deg


);
opacity




:


 


1;
}
}
@
keyframes flipInX { 0% {
	transform: perspective(400px) rotateX(90deg);
	opacity: 0;
}

40%
{
transform




:


 


perspective




(400
px


)
rotateX




(-10
deg


);
}
70%
{
transform




:


 


perspective




(400
px


)
rotateX




(10
deg


);
}
100%
{
transform




:


 


perspective




(400
px


)
rotateX




(0
deg


);
opacity




:


 


1;
}
}
.flipInX {
	-webkit-backface-visibility: visible !important;
	-webkit-animation-name: flipInX;
	-moz-backface-visibility: visible !important;
	-moz-animation-name: flipInX;
	-o-backface-visibility: visible !important;
	-o-animation-name: flipInX;
	backface-visibility: visible !important;
	animation-name: flipInX;
}
</style>
<!--  Function to hide the gallery on load -->
<script>
	$(document).ready(function() {
		$("#gallery").hide();
	});
</script>

</head>
<body>

	<div id="blackOverlay" class="blackOverlay"></div>
	<%@include file="header.jsp"%>

	<div class="container">
		<div id="selectors">
			<a href="<%=request.getContextPath()%>/TranslationQuiz" id="csv">
				<button class="btn btn-agp right-align">Translation Quiz</button>
			</a> <a href="/about/teaching-resources/" id="csv">
				<button class="btn btn-agp right-align">Teaching Resources</button>
			</a>
		</div>

		<h2>Featured Graffiti</h2>

		<div id="portfolio">
			<ul class="portfolio-grid">
				<c:forEach var="theme" items="${themes}">
					<li><a
						href="<%=request.getContextPath()%>/themes/${theme.name}"
						class="darken"> <img
							src="/Graffiti/resources/images/featured_graffiti/${theme.name}.png"
							alt="Herculaneum" /> <span class="message">${theme.description}</span>
					</a></li>
				</c:forEach>
			</ul>
		</div>
	</div>

	<!-- JS for modal view, toggling, show translations-->
	<script type="text/javascript" src="resources/js/greatestHits.js"></script>

	<script type="text/javascript">
		$('.darken').hover(function() {
			$(this).find('.message').fadeIn(100);
		}, function() {
			$(this).find('.message').fadeOut(100);
		});
	</script>

</body>
</html>