
<style>
	.dropdown:hover .dropbtn {
		background-color: maroon;
	}
	
	.dropdown-menu a:hover {
		background-color: #ddd;
	}
	
	.dropdown:hover .dropdown-menu {
		display: block;
	}
</style>

<header class="navbar navbar-static-top bs-docs-nav" id="top"
	role="banner">

	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="container-fluid" style="padding: 0 25px;">
			<div class="navbar-header">
				<a class="navbar-brand" href="<%=request.getContextPath()%>/">AGP</a>
				<!-- THIS HAS BEEN COMMENTED OUT, BECAUSE I THOUGHT IT LOOKED BAD. IF YOU WOULD 
					 RATHER PREFER THIS LOOK, I CAN EASILY USE THIS INSTEAD OF HOW BROWSE IS 
					 CURRENT SET UP. -->
				<!-- <a class="navbar-brand" href="<%=request.getContextPath()%>/">Browse</a> -->
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".bs-navbar-collapse" style="width: 45px;">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
			</div>
			<nav class="collapse navbar-collapse bs-navbar-collapse"
				role="navigation">
				<ul class="nav navbar-nav" id="nav">
					<li class="dropdown"><a class="dropdown-toggle"
							data-toggle="dropdown" href="#">Browse <span
								class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a
									href="<%=request.getContextPath()%>/results">All Graffiti</a></li>
								<li><a href="<%=request.getContextPath()%>/results?drawing_category=All">
										Figural Graffiti</a></li>
							</ul></li>
				<!-- THIS HAS BEEN COMMENTED OUT, BECAUSE I THOUGHT IT LOOKED BAD. IF YOU WOULD 
					 RATHER PREFER THIS LOOK, I CAN EASILY USE THIS INSTEAD OF HOW BROWSE IS 
					 CURRENT SET UP. -->
					<!-- 
					<li><a href="<%=request.getContextPath()%>/results">All Graffiti</a></li>
					<li><a
						href="<%=request.getContextPath()%>/results?drawing_category=All">Figural
							Graffiti</a></li>
 					-->
					<li class="dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#">Interactive Maps <span
							class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a
								href="<%=request.getContextPath()%>/searchHerculaneum">Herculaneum</a></li>
							<li><a href="<%=request.getContextPath()%>/searchPompeii">
									Pompeii</a></li>
						</ul></li>
					
					<li><a href="<%=request.getContextPath()%>/featured-graffiti">Featured
							Graffiti</a></li>
					<li class="dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#">Resources <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="<%=request.getContextPath()%>/properties">Properties</a></li>
							<li><a href="http://ancientgraffiti.org/about/teaching-resources/">Teaching
									Resources</a></li>
							<li><a href="swagger-ui.html">API Documentation</a></li>
							<li><a href="http://ancientgraffiti.org/about/main/epigraphic-conventions"/>Epigraphic Conventions</a></li>
						</ul></li>
					<li class="dropdown"><a href="/about" class="dropdown-toggle"
						data-toggle="dropdown">About the Project <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="http://ancientgraffiti.org/about/teams/">Teams</a></li>
							<li><a href="http://ancientgraffiti.org/about/fieldwork/">Fieldwork</a></li>
							<li><a href="http://ancientgraffiti.org/about/main/funding/">Funding</a></li>
						</ul></li>
				</ul>
				<form class="navbar-form navbar-left">
					<div class="form-group" >
						<input id="globalSearch" type="text" class="form-control" placeholder="Global Search">
					</div>
					<button class="btn btn-agp" onclick="globalSearchFromHeader();">Search</button>
				</form>

				<%
					boolean sess = false;
					if (session.getAttribute("authenticated") != null) {
						sess = (Boolean) session.getAttribute("authenticated");
					}

					/* If user is authenticated, Login disappears and Logout appears. Vice versa if admin is not authenticated */
					if (sess == true) {
				%>
				<ul class="nav navbar-nav navbar-right">
					<li><a href="<%=request.getContextPath()%>/admin">Admin</a></li>
					<li class="navbar-right" id="logout" style="visibility: visible"><a
						href="logout">Logout</a></li>
				</ul>
				<%
					}
				%>
			</nav>
			<!--/.navbar-collapse -->
		</div>
	</div>
	<!-- Main jumbotron  -->
	<div class="block" id="Jumbo">
		<div class="jumbotron">
			<div class="container">
				<h1>The Ancient Graffiti Project Search Engine</h1>
				<p>A digital resource for studying the graffiti of Herculaneum
					and Pompeii</p>
			</div>
		</div>
	</div>
	
<script type="text/javascript">
	function globalSearchFromHeader() {
		var param = document.getElementById("globalSearch").value;
		if(param != "") {
			param = param.replace(" ", "_");
			var url = "<%=request.getContextPath()%>";
			url += "/results?global=" + param + "&sort_by=relevance";
			window.open(url);
		}
	}
</script>

</header>
