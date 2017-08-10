<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="num" value="${fn:length(requestScope.resultsLyst)}" />
<%@ page import="java.util.*"%>

<%
	List<String> newLocationKeys = (List<String>) request.getAttribute("findLocationKeys");
%>

<div id="mapkeys" style="display: none;"><%=newLocationKeys%></div>
<div style="width: 480px">
<p class="alert alert-info">

<c:out value="${num} results found ${searchQueryDesc}" /></p>
<a href="<%=request.getContextPath()%>/filtered-results/json"
	id="bulkJson">
	<button class="btn btn-agp btn-sm">Export as JSON</button>
</a> <a href="<%=request.getContextPath()%>/filtered-results/xml"
	id="bulkEpidocs">
	<button class="btn btn-agp btn-sm">Export as EpiDoc</button>
</a> <a href="<%=request.getContextPath()%>/filtered-results/csv"
	id="bulkEpidocs">
	<button class="btn btn-agp btn-sm">Export as CSV</button>
</a>
<button id="print" class="btn btn-agp btn-sm"
	onclick="printResults();">Print</button>
<div style="float: right">
Sort By
<form method="POST" name="sortForm">
<select name="sortParam" id="sortParam">
	<option value="relevance">Relevance</option>
	<option value="findspot">Findspot</option>
	<option value="cil">CIL #</option>
</select>
</form>
</div>
</div>
<c:if test="${num == 0}">
	<br />
	<c:out value="Try broadening your search" />
</c:if>

<a href="#top">
	<button class="btn btn-agp scroll_top">Return To Top</button>
</a>

<%@ include file="resultsList.jsp"%>
