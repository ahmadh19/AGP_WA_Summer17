<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="num" value="${fn:length(requestScope.resultsLyst)}" />
<%@ page import= "java.util.*" %>

<% List<String> newLocationKeys=(List<String>)request.getAttribute("findLocationKeys");%>
<div id="mapkeys" style="display:none;"><%=newLocationKeys %></div>

<p class="alert alert-info" style="width: 475px">
	
	<c:out value="${num} results found ${searchQueryDesc}" />
	<br/>
</p>

<c:if test="${num == 0}">
	<br />
	<c:out value="Try broadening your search" />
</c:if>

<h1>Print Search Results</h1>

<%@ include file="printableResultsList.jsp"%>
