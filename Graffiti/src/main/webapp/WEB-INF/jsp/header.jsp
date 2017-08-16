
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
					<li><a href="<%=request.getContextPath()%>/results">Inscriptions</a></li>
					<li><a
						href="<%=request.getContextPath()%>/results?drawing_category=All">Figural
							Graffiti (Drawings)</a></li>

					<li class="dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown" href="#">Interactive Maps <span
							class="expander" aria-hidden="true"></span></a>
						<ul class="dropdown-menu">
							<li><a
								href="<%=request.getContextPath()%>/searchHerculaneum">Herculaneum</a></li>
							<li><a href="<%=request.getContextPath()%>/searchPompeii">
									Pompeii</a></li>
						</ul></li>
					<li class="dropdown"><a class="dropdown-toggle"
						data-toggle="dropdown">Data <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="<%=request.getContextPath()%>/properties">Properties</a>
						</ul></li>
					<li><a href="<%=request.getContextPath()%>/featured-graffiti">Featured
							Graffiti</a></li>
					<li class="dropdown"><a href="/about" class="dropdown-toggle"
						data-toggle="dropdown">About the Project <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="about/teaching-resources/">Teaching
									Resources</a></li>
							<li><a href="/about/">Teams</a></li>
							<li><a href="about/fieldwork/">Fieldwork</a></li>
						</ul></li>
				</ul>

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

</header>
