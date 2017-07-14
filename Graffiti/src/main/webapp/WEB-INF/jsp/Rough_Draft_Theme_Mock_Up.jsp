<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html lang="en">
<head>

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
</style>
<!--  Function to hide the gallery on load -->
<script>
	$(document).ready(function() {
		$("#gallery").hide();
	});
</script>

</head>
<body>

	<%@include file="header.jsp"%>
	<div class="container">

		<h2>Generic Theme</h2>

		<c:set var="translationHits" value="${translationHits}" />

		<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
		<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

		<div id="original">
			<!--  <h3>Graffiti from Herculaneum</h3>  -->
			<table class="table table-striped table-bordered"
				id="greatest_hits_table">
				<thead>
					<tr>
						<th id="idCol">ID</th>
						<th id="textCol">Text (Latin or Greek) / English Translation</th>
						<th id="transCol">Commentary</th>

						<!-- 
				<th id="imgCol">Commentary</th>
				 -->
					</tr>
				</thead>
				<!--  LOOOP  -->
				<tbody>

					<tr>
						<td><a
							href="<%=request.getContextPath() %>/graffito/${i.agp.agpId}">${i.agp.agpId}</a>
							<c:if test="${not empty i.agp.cil }">
								<br />${i.agp.cil}
						</c:if> <c:if test="${not empty i.agp.langner }">
								<br />${i.agp.langner}
						</c:if> AGP-EDR140983 <br>CIL 04, 10697 (1)</td>
						<td>Text: Fortunatus amat Amplianda(m). Ianuarius amat
							Veneria(m). Rogamus damna (:domina) Venus ut nos in mente(m)
							habias (:habeas) quod te modo introrgamus. (:interrogamus) <br>
							<br>Translation: Fortunatus loves Amplianda. Ianuarius loves
							Veneria. We ask mistress Venus that you keep us in mind (and
							also) that which we now ask of you.
						</td>
						<td>
							<p class="trans" style="display: none;">
								<em>${i.agp.contentTranslation}</em>
							</p> This is one of the longest messages among the Herculanean
							graffiti, made more remarkable by its small size, with letters
							that are half a centimeter high. Beginning as a declaration of
							love and evolving into a divine supplication, the five line
							prayer to Venus, the goddess of love, was inscribed into the wet
							plaster of the ramp either by the workman doing the plastering or
							by someone passing by soon afterward. Plaster dries overnight,
							narrowing the window of opportunity significantly. Nevertheless,
							whoever Fortunatus and Ianuarius were, they needed a little
							divine help to win their women, Amplianda and Veneria. They were
							at least bold enough to explicitly name themselves and their
							lovers on the wall of the ramp, the only causeway to and from the
							sea from the city of Herculaneum.
						</td>
					<tr>
						<td>AGP-EDR140195<br>CIL 04, 10575</td>
						<td>Text: XI k(alendas) pane(m) factum III nonas pane(m)
							factum 〚III〛 <br> <br> Translation: Eleven days before
							the Kalends, bread was made. Three days before the Nones, bread
							was made.
						</td>
						<td>This graffito shows the Roman dating system.</td>
					</tr>
					<tr>
						<td>AGP-EDR140039 <br>CIL 04, 10567</td>
						<td>Text:〈:columna I〉 Branc broc trans nus <br>〈:columna
							II〉 Nos= ter tros men <br>〈:columna III〉 Quod quid quae quas
							<br>〈:columna IV〉 Rum quis que dem <br>〈:columna V〉 Con
							les lis muḷ <br>〈:columna VI〉 Mol ma me mae <br> <br>
							Translation:
						</td>
						<td>These are what are presumed to be grammatical exercises.
							However, many of the words have no definable meaning.</td>
					</tr>


					<!--  
					<td style="white-space: pre-wrap">${i.agp.greatestHitsInfo.commentary }</td>
						 -->


				</tbody>
			</table>
		</div>

		<!-- JS for modal view, toggling, show translations-->
		<script type="text/javascript" src="resources/js/greatestHits.js"></script>
	</div>

</body>
</html>