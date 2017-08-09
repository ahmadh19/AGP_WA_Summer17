<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="edu.wlu.graffiti.bean.Inscription"%>
<%@ page import="edu.wlu.graffiti.bean.AGPInfo"%>
<%@ page import="edu.wlu.graffiti.bean.DrawingTag"%>
<%@ page import="edu.wlu.graffiti.bean.Theme"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn' %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Update a Graffito</title>
<%@include file="/resources/common_head.txt"%>
<style type="text/css">
.attribute {
	text-align: right;
}

#agp_info {
	width: 700px;
}

#edr_info {
	-moz-border-radius: 15px;
	border-radius: 15px;
	border: 1px solid gray;
	padding: 10px;
	width: 310px;
	float: right;
}

input[name*="image"] {
	display: inline;
}


/* Had to override a bunch of random classes from boostrap to get the page to prorperly align and float, We have no idea why */

.btn-group-vertical > .btn-group::after, .btn-toolbar::after, .clearfix::after, .container-fluid::after, .container::after, .dl-horizontal dd::after, .form-horizontal .form-group::after, .modal-footer::after, .modal-header::after, .nav::after, .navbar-collapse::after, .navbar-header::after, .navbar::after, .pager::after, .panel-body::after, .row::after
{   clear:left; }

.modal {
    display: none; /* Hidden by default */
    position: fixed; /* Stay in place */
    z-index: 1; /* Sit on top */
    left: 0;
    top: 60px;
    width: 100%; /* Full width */
    height: 100%; /* Full height */
    overflow: auto; /* Enable scroll if needed */
    background-color: rgb(0,0,0); /* Fallback color */
    background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
}

.modal-content {
    background-color: #fefefe;
    margin: 15% auto; /* 15% from the top and centered */
    padding: 20px;
    border: 1px solid #888;
    width: 80%; /* Could be more or less, depending on screen size */
}

.close {
    color: #aaa;
    float: right;
    font-size: 28px;
    font-weight: bold;
}

.close:hover,
.close:focus {
    color: black;
    text-decoration: none;
    cursor: pointer;
} 

.modal .modal-content .btn-agp {
	margin: 10px;
}

</style>
<script type="text/javascript">
	$(document).ready(function() {
		$("#figural").click(function() {
			$("#drawing_info").toggle();
		});
	});
	
	$(document).ready(function() {
		$("#themed").click(function() {
			$("#themes_info").toggle();
		});
	});
	
	$(document).ready(function() {
		$("#gh_trans").click(function() {
			$("#ghCommentary").toggle();
		});
	});
	
</script>

<script type="text/javascript">
	$(document).ready(function (){
		$('#agp_info_form').submit(function(){ 
			var checked = $('#gh_fig').is(':checked');
			if(checked == true) {
				var text = $('#gh_preferred_image').val();
				if(text=='' || text=='null') {
					$('#parent').find("span").remove();
					$('#parent').prepend('<p><span class="alert alert-danger">Preferred Image is required if a figural graffito.</span></p>');
					$(window).scrollTop(0);
					return false;
				} 
			}
		});
	});

</script>


</head>
<body>

	<!-- Format for checking if this works:
	
	http://localhost:8080/Graffiti/updateGraffito?edrID=EDR153355
	
	-->

	<%@include file="/WEB-INF/jsp/header.jsp"%>

	<%
		Inscription inscription = (Inscription) request.getAttribute("graffito");
		List<DrawingTag> drawingTags = (List<DrawingTag>) request.getAttribute("drawingTags");
		List<Integer> drawingTagIds = (List<Integer>) request.getAttribute("drawingTagIds");
		List<Theme> themes = (List<Theme>) request.getAttribute("themes");
		List<Integer> themeIds = (List<Integer>) request.getAttribute("inscriptionThemeIds");
		List<Integer> allThemeIds = (List<Integer>) request.getAttribute("allThemeIds");

	%>

	<div class="container" id="parent">

		<c:if test="${requestScope.msp != null}">
			<p class="alert alert-success" role="alert">${requestScope.msg}</p>
		</c:if>

		<c:set var="i" value="${requestScope.graffito}" />

		<p style="text-align: right;">
			<a class="btn btn-agp"
				href="<c:url value="/graffito/AGP-${i.edrId}"/>">AGP-<%=inscription.getEdrId()%>
				Details Page
			</a> <a class="btn btn-agp"
				href="http://www.edr-edr.it/edr_programmi/res_complex_comune.php?visua=si&id_nr=<%=inscription.getEdrId()%>">EDR
				link</a>
		</p>

		<h2>
			Update AGP-<%=inscription.getEdrId()%></h2>

		<div id="edr_info">
			<h3>Data from EDR</h3>
			<table class="table table-striped">
				<tbody>
					<tr>
						<td class="prop">City</td>
						<td><%=inscription.getAncientCity()%></td>
					</tr>
					<tr>
						<td class="prop">Find Spot</td>
						<td><%=inscription.getEDRFindSpot()!=null?inscription.getEDRFindSpot():""%></td>
					</tr>
					<tr>
						<td class="prop">Writing Style</td>
						<td><%=inscription.getWritingStyle()%>
					</tr>
					<tr>
						<td class="prop">Language</td>
						<td><%=inscription.getLanguage()%>
					</tr>
					<tr>
						<td class="prop">EDR Measurements</td>
						<td><%=inscription.getMeasurements()%></td>
					</tr>
					<tr>
						<td class="prop">Bibliography</td>
						<td>${i.bibliography}</td>
					</tr>
					<tr>
						<td class="prop">Apparatus Criticus</td>
						<td>${i.apparatus}</td>
					</tr>
				</tbody>
			</table>
		</div>


		<div id="agp_info">
			<form id="agp_info_form" class="form-horizontal" method="post" action="updateGraffito">
				<input type="hidden" name="${_csrf.parameterName}"
					value="${_csrf.token}" class="form-control" />

				<div class="form-group" style="clear:left;">
					<strong class="col-sm-3 attribute">Graffito:</strong>
					<div class="col-sm-6" id="graffitoContent">${i.contentWithLineBreaks}</div>
				</div>
				
				<div class="form-group">
					<label for="epidocContent" class="col-sm-3 control-label">Content EpiDoc:</label>
					<div class="col-sm-6">
						<textarea rows=10 id="epidocContent" name="epidocContent" class="form-control">${i.agp.epidocWithLineBreaks}</textarea>
					</div>
				</div>
				
				<div id="epidocModal" class="modal">
					<div class="modal-content" id="epidocModalContent">
						<span class="close">&times;</span>
					</div>
				</div>

				<div class="form-group">
					<label for="cil" class="col-sm-3 attribute">CIL Number:</label>
					<div class="col-sm-6">
						<input type="text" name="cil" id="cil" class="form-control"
							value="${i.agp.cil}" />
					</div>
				</div>
				<div class="form-group">
					<label for="langner" class="col-sm-3 attribute">Langner Number:</label>
					<div class="col-sm-6">
					<input type="text" name="langner" id="langner" class="form-control" value="${i.agp.langner }"/>
					</div>
				</div>

				<div class="form-group">
					<label for="summary" class="col-sm-3 control-label">Summary:</label>
					<div class="col-sm-6">
						<textarea id="summary" name="summary" class="form-control">${i.agp.summary}</textarea>
					</div>
				</div>

				<div class="form-group">
					<label for="content_translation" class="col-sm-3 control-label">Graffito
						Translation:</label>
					<div class="col-sm-6">
						<textarea name="content_translation" id="content_translation"
							class="form-control">${ i.agp.contentTranslation}</textarea>
					</div>
				</div>

				<div class="form-group">
					<label for="commentary" class="col-sm-3 control-label">Commentary:</label>
					<div class="col-sm-6">
						<textarea rows=7 id="commentary" name="commentary" class="form-control">${ i.agp.commentary }</textarea>
					</div>
				</div>
				
				<hr />

				<!-- look at graffito2drawingtags -->

				<div class="form-group">
					<label for="figural" class="col-sm-3 control-label">Has
						Figural Component?</label>
					<div class="col-sm-1">
						<input type="checkbox" name="figural" id="figural"
							class="form-control"
							<%=inscription.getAgp().hasFiguralComponent() ? "checked" : ""%> />
					</div>
				</div>

				<div id="drawing_info"
					style="display:<%=inscription.getAgp().hasFiguralComponent() ? "inline" : "none"%>">
					<h3>Figural Metadata</h3>

					<div class="form-group">

						<label for="drawingCategory" class="col-sm-3 control-label">Drawing
							Category</label>

						<div class="col-sm-6">
							<c:forEach var="dc" items="${drawingTags}">
							<label>
								<input type="checkbox" name="drawingCategory" 
								value='${dc.id}' <c:if test="${drawingTagIds.contains(dc.id)}" > checked </c:if>> 
								${dc.name}
								</label>
								<br/>
						</c:forEach>
						</div>
					</div>
					<div class="form-group">
						<label for="drawing_description_latin"
							class="col-sm-3 control-label">Drawing Description in
							Latin</label>
						<div class="col-sm-6">
							<textarea name="drawing_description_latin"
								id="drawing_description_latin" class="form-control">${i.agp.figuralInfo.descriptionInLatin}</textarea>
						</div>
					</div>
					<div class="form-group">
						<label for="drawing_description_english"
							class="col-sm-3 control-label">Drawing Description
							Translation</label>
						<div class="col-sm-6">
							<textarea name="drawing_description_english"
								id="drawing_description_english" class="form-control">${i.agp.figuralInfo.descriptionInEnglish}</textarea>
						</div>
					</div>
				</div>

				<h3>Measurements</h3>

				<div class="form-group">
					<label for="floor_to_graffito_height"
						class="col-sm-3 control-label"> Height from ground </label>
					<div class="col-sm-6">
						<input type="text" name="floor_to_graffito_height"
							id="floor_to_graffito_height" class="form-control"
							value="${i.agp.heightFromGround}">
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-3 control-label" for="graffito_height">
						Graffito Height</label>
					<div class="col-sm-6">
						<input type="text" name="graffito_height" id="graffito_height"
							class="form-control" value="${i.agp.graffitoHeight}">
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-3 control-label" for="graffito_length">
						Graffito Length</label>
					<div class="col-sm-6">
						<input type="text" name="graffito_length" id="graffito_length"
							class="form-control" value="${i.agp.graffitoLength}">
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-3 control-label" for="letter_height_min">
						Minimum Letter Height</label>
					<div class="col-sm-6">
						<input type="text" name="letter_height_min" id="letter_height_min"
							class="form-control" value="${i.agp.minLetterHeight}">
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-3 control-label" for="letter_height_max">
						Maximum Letter Height</label>
					<div class="col-sm-6">
						<input type="text" name="letter_height_max" id="letter_height_max"
							class="form-control" value="${i.agp.maxLetterHeight}">
					</div>
				</div>

				<div class="form-group">
					<label class="col-sm-3 control-label" for="character_heights">
						Character Heights</label>
					<div class="col-sm-6">
						<textarea name="character_heights" id="letter_height_max"
							class="form-control">${i.agp.individualLetterHeights}</textarea>
					</div>
				</div>

				<h3>Featured Graffiti</h3>

				<div class="form-group">
					<label for="gh_fig" class="col-sm-3 control-label">Figural Graffiti?</label>
					<div class="col-sm-1">
						<input type="checkbox" name="gh_fig" id="gh_fig"
							class="form-control"
							<%=inscription.getAgp().isGreatestHitFigural() ? "checked" : ""%> />
					</div>
				</div>

				<div class="form-group">
					<label for="gh_trans" class="col-sm-3 control-label">Translation
						 Graffiti?</label>
					<div class="col-sm-1">
						<input type="checkbox" name="gh_trans" id="gh_trans"
							class="form-control"
							<%=inscription.getAgp().isGreatestHitTranslation() ? "checked" : ""%> />
					</div>
				</div>
				
				<div class="form-group" id="ghCommentary"
					style="display:<%=inscription.getAgp().isGreatestHitTranslation() || inscription.getAgp().isGreatestHitFigural() ? "inline" : "none"%>">>
					<label for="gh_commentary" class="col-sm-3 control-label">Featured Graffiti Commentary</label>
					<div class="col-sm-7">
						<textarea rows="15" name="gh_commentary" id="gh_commentary"
							class="form-control"><%=inscription.getAgp().getGreatestHitsInfo().getCommentary()%></textarea>
					</div>
				</div>
				
				<div class="form-group">
					<label for="themed" class="col-sm-3 control-label">Themed Graffiti?</label>
					<div class="col-sm-1">
						<input type="checkbox" name="themed" id="themed"
							class="form-control"
							<%=inscription.getAgp().isThemed() ? "checked" : ""%> />
					</div>
				</div>
				
				<div id="themes_info"
					style="display:<%=inscription.getAgp().isThemed() ? "inline" : "none"%>">
					<div class="form-group">
	
						<label for="themes" class="col-sm-3 control-label">Themes</label>

						<div class="col-sm-6">
						
						<c:forEach var="theme" items="${themes}">
							<label>
								<input type="checkbox" name="themes" 
								value='${theme.id}' <c:if test="${inscriptionThemeIds.contains(theme.id)}" > checked </c:if>> 
								${theme.name}
								</label>
								<br/>
						</c:forEach>
					
						</div>
					</div>
				</div>

				<div class="form-group">
					<label for="gh_preferred_image" class="col-sm-3 control-label">
						Preferred Image</label>, e.g., 123456-1
					<div class="col-sm-6">
						<input type="text" name="gh_preferred_image"
							id="gh_preferred_image" class="form-control"
							value="<%=inscription.getAgp().getGreatestHitsInfo().getPreferredImage()%>" />
					</div>
				</div>


				<div class="form-group" id="btnPanel">
					<button type="submit" class="btn btn-agp" id="updateGraffito">Update Graffito</button>
					<button type="button" name="back" onclick="history.back()"
						class="btn btn-agp">Go Back</button>
				</div>
			</form>
		</div>
	</div>

	<script type="text/javascript">
		
		// GETTING POSITIONS WILL NOT WORK BECAUSE CONTENT AND CONTENT EPIDOC HAVE DIFFERENT TEXTS!
		/*
		function getSelected() {
			if (window.getSelection) {
				return window.getSelection();
			} else if (document.getSelection) {
				return document.getSelection();
			} else {
				var selection = document.selection
						&& document.selection.createRange();
				if (selection.text) {
					return selection.text;
				}
				return false;
			}
			return false;
		}
		*/
		
		var selectionStart, selectionEnd;
		
		/*
		function getSelected() {
			var e1 = document.getElementById('epidocContent');
			var text = e1.value.substring(e1.selectionStart, e1.selectionEnd);
			return {
				txt: text,
				start: e1.selectionStart,
				end: e1.selectionEnd
			}
		}
		*/
		
		function getInputSelection(el) {
		    var start = 0, end = 0, normalizedValue, range,
		        textInputRange, len, endRange;

		    if (typeof el.selectionStart == "number" && typeof el.selectionEnd == "number") {
		        start = el.selectionStart;
		        end = el.selectionEnd;
		    } else {
		        range = document.selection.createRange();

		        if (range && range.parentElement() == el) {
		            len = el.value.length;
		            normalizedValue = el.value.replace(/\r\n/g, "\n");

		            // Create a working TextRange that lives only in the input
		            textInputRange = el.createTextRange();
		            textInputRange.moveToBookmark(range.getBookmark());

		            // Check if the start and end of the selection are at the very end
		            // of the input, since moveStart/moveEnd doesn't return what we want
		            // in those cases
		            endRange = el.createTextRange();
		            endRange.collapse(false);

		            if (textInputRange.compareEndPoints("StartToEnd", endRange) > -1) {
		                start = end = len;
		            } else {
		                start = -textInputRange.moveStart("character", -len);
		                start += normalizedValue.slice(0, start).split("\n").length - 1;

		                if (textInputRange.compareEndPoints("EndToEnd", endRange) > -1) {
		                    end = len;
		                } else {
		                    end = -textInputRange.moveEnd("character", -len);
		                    end += normalizedValue.slice(0, end).split("\n").length - 1;
		                }
		            }
		        }
		    }
			var text = el.value.substring(start, end).trim();
		    return {
		        start: start,
		        end: end,
		        txt : text
		    };
		}
		
		var modal = document.getElementById("epidocModal");
		
		var nameTypes = []; // used only if the user selects two words (with a space between them) and marks the selection as a persName
		
		function getPersNameType(text) {
			$(".modal .modal-content").html("<p>Marking '" + text + "' as a &lt;persName&gt;. Select a type for the &lt;persName&gt; tag: <p>" + 
					"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"getNameType(\'"+ text +"\', 'attested')\">attested</button>" + 
					"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"getNameType(\'"+ text +"\', 'emperor')\">emperor</button>" + 
					"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"getNameType(\'"+ text +"\', 'divine')\">divine</button>" + 
					"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"getNameType(\'"+ text +"\', 'consular')\">consular</button>" + 
					"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"getNameType(\'"+ text +"\', 'other')\">other</button>");
		}
		
		function getNameType(text, persNameType) {
			
			//if the user selects two words (with a space between them) -- i.e., firstName and lastName
			if(text.includes(" ") && text.split(" ").length == 2) {
				var temp = text.split(" ");
				$(".modal .modal-content").html("<p>Marking '" + text + "' as a &lt;persName&gt;. The type for the &lt;persName&gt; tag is: " + persNameType + ". <br/> <br/> Select a type for the &lt;name&gt; tag for \'" + temp[0] + "\': <p>" + 
						"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"getOtherNameType(\'"+ text +"\', \'"+ persNameType +"\', 'praenomen')\">praenomen</button>" + 
						"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"getOtherNameType(\'"+ text +"\', \'"+ persNameType +"\', 'gentilicium')\">gentilicium</button>" + 
						"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"getOtherNameType(\'"+ text +"\', \'"+ persNameType +"\', 'cognomen')\">cognomen</button>");
			} else {
				$(".modal .modal-content").html("<p>Marking '" + text + "' as a &lt;persName&gt;. The type for the &lt;persName&gt; tag is: " + persNameType + ". <br/> <br/> Select a type for the &lt;name&gt; tag: <p>" + 
						"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"markAsPersonName(\'"+ text +"\', \'"+ persNameType +"\', 'praenomen')\">praenomen</button>" + 
						"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"markAsPersonName(\'"+ text +"\', \'"+ persNameType +"\', 'gentilicium')\">gentilicium</button>" + 
						"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"markAsPersonName(\'"+ text +"\', \'"+ persNameType +"\', 'cognomen')\">cognomen</button>");
			}
		}
		
		/*
		* Gets the other <name> type if the user has selected a firstName and a lastName.
		* Pushes the secondNameType to the array nameTypes.
		* Note: This function calls markAsPersonName with an integer value as nameType (the result of nameTypes.push(secondNameType)).
		*/
		function getOtherNameType(text, persNameType, firstNameType) {
			nameTypes.push(firstNameType);
			var temp = text.split(" ");
			$(".modal .modal-content").html("<p>Marking '" + text + "' as a &lt;persName&gt;. The type for the &lt;persName&gt; tag is: " + persNameType + ". <br/><br/>The type for the &lt;name&gt; tag for \'" + temp[0] + "\' is: " + firstNameType + 
			".<br/> <br/> Select a type for the &lt;name&gt; tag for \'" + temp[1] + "\': <p>" + 
					"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"markAsPersonName(\'"+ text +"\', \'"+ persNameType +"\', nameTypes.push('praenomen'))\">praenomen</button>" + 
					"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"markAsPersonName(\'"+ text +"\', \'"+ persNameType +"\', nameTypes.push('gentilicium'))\">gentilicium</button>" + 
					"<button type='button' class='btn btn-agp' style='width:100px;' onclick=\"markAsPersonName(\'"+ text +"\', \'"+ persNameType +"\', nameTypes.push('cognomen'))\">cognomen</button>");
		}
		
		function markAsPersonName(text, persNameType, nameType) {
		
			var epidocMarkup;
			
			// nameType is an integer if and only if the user has selected two words to be marked as a persName
			// this is because the index position of an element is return when the element is pushed to an array
			if(Number.isInteger(nameType)) {
				var temp = text.split(" ");
				epidocMarkup = "<persName type=\"" + persNameType + "\"><name nymRef=\"\" type=\"" + nameTypes[0] + "\">" + temp[0] + "</name><name nymRef=\"\" type=\"" + nameTypes[1] + "\">" + temp[1] + "</name></persName>";
			} else {
				epidocMarkup = "<persName type=\"" + persNameType + "\"><name nymRef=\"\" type=\"" + nameType + "\">" + text + "</name></persName>";
			}
			
			var tempStart = document.getElementById("epidocContent").value.substring(0, selectionStart);
			var tempEnd = document.getElementById("epidocContent").value.substring(selectionEnd, 
					document.getElementById("epidocContent").value.length);
			document.getElementById("epidocContent").value = tempStart + epidocMarkup + tempEnd;
			modal.style.display = "none";
			document.getElementById("epidocContent").setSelectionRange(selectionStart, selectionEnd + epidocMarkup.length - text.length);
			document.getElementById("epidocContent").focus();
		}
		
		/*
		function markAsPersonName(text) {
			modal.style.display = "none";
			var epidocMarkup;
			var persNameType = window.prompt("Please enter the type attribute of <persName>: ", "attested");
	
			// check if the selected name contains two words (firstName and lastName)
			if(text.includes(" ") && text.split(" ").length == 2) {
				var temp = text.split(" ");
				var nameType1 = window.prompt("Please enter the type attribute of the first <name> tag, " + text[0] + ": ", "");
				var nameType2 = window.prompt("Please enter the type attribute of the second <name> tag, " + text[1] + ": ", "");
				epidocMarkup = "<persName type=\"" + persNameType + "\"><name type=\"" + nameType1 + "\">" + temp[0] + "</name><name type=\"" + nameType2 + "\">" + temp[1] + "</name></persName>";
			} else {
				var nameType = window.prompt("Please enter the type attribute of the <name> tag, " + text + ": ", "");
				epidocMarkup = "<persName type=\"" + persNameType + "\"><name type=\"" + nameType + "\">" + text + "</name></persName>";
			}
			
			var tempStart = document.getElementById("epidocContent").value.substring(0, selectionStart);
			var tempEnd = document.getElementById("epidocContent").value.substring(selectionEnd, 
					document.getElementById("epidocContent").value.length);
			document.getElementById("epidocContent").value = tempStart + epidocMarkup + tempEnd;
		}
		*/
		
		function markAsPlaceName(text) {
			var epidocMarkup = "<placeName>" + text + "</placeName>";
			var tempStart = document.getElementById("epidocContent").value.substring(0, selectionStart);
			var tempEnd = document.getElementById("epidocContent").value.substring(selectionEnd, 
					document.getElementById("epidocContent").value.length);
			document.getElementById("epidocContent").value = tempStart + epidocMarkup + tempEnd;
			modal.style.display = "none";
			document.getElementById("epidocContent").setSelectionRange(selectionStart, selectionEnd + epidocMarkup.length - text.length);
			document.getElementById("epidocContent").focus();
		}

		$(document).ready(function() {
			var selectionImage;
			$('#epidocContent').mouseup(function(e) {
				var selection = getInputSelection(document.getElementById("epidocContent"));
				if (!selectionImage) {
					selectionImage = $('<button>').attr({
						type : 'button',
						title : 'Add To EpiDoc',
						id : 'epidocify',
						style : 'position: absolute;'
					}).html("EpiDoc-ify").css({
						"color" : "black"
					}).hide();
					$(document.body).append(selectionImage);
				}
				$("#epidocify").click(function epidocify() {
					var selection = getInputSelection(document.getElementById("epidocContent"));
					var txt = selection.txt;
					selectionStart = selection.start;
					selectionEnd = selection.end;
					
					if(txt !='') { 
						modal.style.display = "block";
						
						//alert("<button type='button' class='btn btn-agp' onclick=markAsPersonName('"+ selection.txt +"')>Person Name</button>");
						$(".modal .modal-content").html("<p>Mark '" + selection.txt + "' as one of the following: </p>" +
							"<button type='button' class='btn btn-agp' style='width:200px;' onclick=\"getPersNameType(\'"+ selection.txt +"\')\">Person Name</button>" +
							"<button type='button' class='btn btn-agp' style='width:200px;' onclick=\"markAsPlaceName(\'"+ selection.txt +"\')\">Place Name</button>");
					}
				}).mousedown(function() {

					if (selectionImage) {
						selectionImage.fadeOut();
					}

				});

				if(selectionImage && selection.txt != '') {
					selectionImage.css({
						top : e.pageY - 30, //offsets
						left : e.pageX - 13 //offsets
					}).fadeIn();
				}
			});
		});

		// hide the EpiDoc-ify button and the modal screen if user clicks elsewhere
		window.onclick = function(event) {
			if (!event.target.matches('.epidocify') && !event.target.matches('#epidocContent')) {
				var btn = document.getElementById("epidocify");
				$(btn).fadeOut();
			}
			
			if(event.target == modal) {
				modal.style.display = "none";
			}
		}
	</script>

</body>
</html>