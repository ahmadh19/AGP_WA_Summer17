
function initmap(moreZoom=false,showHover=true,colorDensity=true,interactive=true,propertyIdToHighlight=0,propertyIdListToHighlight=[]) {
	
	//alert(propertyIdToHighlight);
	var propertyIdListToHighlight=propertyIdListToHighlight;
	var propertyIdToHighlight=propertyIdToHighlight;
	var mapboxAccessToken = 'pk.eyJ1IjoibWFydGluZXphMTgiLCJhIjoiY2lxczduaG5wMDJyc2doamY0NW53M3NnaCJ9.TeA0JhIaoNKHUUJr2HyLHQ';
	
	//I don't know if it is actually necessary to declare these parameters.
	//These allow dynamic customization of the new map based on it's page. 
	var moreZoom=moreZoom;
	var showHover=showHover;
	var colorDensity=colorDensity;
	var interactive=interactive;
	
	var southWest = L.latLng(40.746, 14.48),
	northEast = L.latLng(40.754, 14.498),
	bounds = L.latLngBounds(southWest, northEast);
	
	//var map = new L.map('pompeiimap', {
		//center: [40.750, 14.4884],
		//zoom: 16,
		//minZoom: 16,
		//maxBounds: bounds
	//}); //this just sets my access token
	var map;
	var zoomLevel;
	
	
	if(!moreZoom){
		zoomLevel=16;
	}
	else{
		zoomLevel=14.4;
	}
	
	map = new L.map('pompeiimap', {
		center: [40.750, 14.4884],
		zoom: zoomLevel,
		minZoom: zoomLevel,
		maxBounds: bounds
	}); //this just sets my access token
	
	//Sinks with mapbox(?), why do we need access tokens security?
	var mapboxUrl = 'https://api.mapbox.com/styles/v1/martineza18/ciqsdxkit0000cpmd73lxz8o5/tiles/256/{z}/{x}/{y}?access_token=' + mapboxAccessToken;
	
	//Is this the background-most layer? Default for leaflet?(why the map's back is grey?)
	var grayscale = new L.tileLayer(mapboxUrl, {id: 'mapbox.light', attribution: 'Mapbox Light'});
	
	//I see the clicked areas collection, but what about the rest of the items? Are they just obscurely stored by Leaflet or GeoJSON?
	var clickedAreas = [];
	
	map.addLayer(grayscale);
	L.geoJson(pompeiiPropertyData).addTo(map);
	
	//Sets the style of the portions of the map. Color is the outside borders. There are different colors for 
	//clicked or normal fragments. When clicked, items are stored in a collection. These collections will have the color
	//contained inside of else. 
	function style(feature) {
		
		var fillColor=getFillColor(feature.properties);
		var borderColor='white';
		if (feature.properties.clicked == true || feature.properties.Property_Id==propertyIdToHighlight || propertyIdListToHighlight.indexOf(feature.properties.Property_Id)>=0) {
			borderColor='black';
		} 
		
			return { 
	    	fillColor:fillColor,
	    	width:200,
	    	height:200, 
	        weight: 1,
	        opacity: 1,
	        color: borderColor,
	        fillOpacity: 0.7,
	    };
		L.geoJson(pompeiiPropertyData, {style: style}).addTo(map);
	}
	
	function getFillColor(propertyObject){
		//Hex darkens color as number it represents decreases
		if(colorDensity){

			if(0<=propertyObject.Number_Of_Graffiti && propertyObject.Number_Of_Graffiti<=2){
				return '#FFEDA0';
				
			}
			
			else if(2<propertyObject.Number_Of_Graffiti && propertyObject.Number_Of_Graffiti<=5){
				return '#FEB24C';
				
			}
			
			else if(5<propertyObject.Number_Of_Graffiti && propertyObject.Number_Of_Graffiti<=15){
				return '#FD8D3C';
			}
			else if(15<propertyObject.Number_Of_Graffiti && propertyObject.Number_Of_Graffiti<=30){
				return '#FC4E2A' ;
			}
			else if(30<propertyObject.Number_Of_Graffiti && propertyObject.Number_Of_Graffiti<=60){
				return '#E31A1C';
				
			}
			else if(60<propertyObject.Number_Of_Graffiti && propertyObject.Number_Of_Graffiti<=100){
				return '#BD0026';
			}
			else{
				return '#800026';
			}
			}
		//What should this be?
		//return '#800026';
		//return 'green';
		return '#FC4E2A' ;
	}
	
	var geojson;
	
	//Sets color for properties which the cursor is moving over. 
	function highlightFeature(e) {
		if(interactive){
			var layer = e.target;
			layer.setStyle({
			color:'yellow',
			strokeWidth:"100"
			
			});
		
			if (!L.Browser.ie && !L.Browser.opera) {
			layer.bringToFront();
			}
			info.update(layer.feature.properties);
		}
	}
	
	/*function showDetails(e) {
		var layer = e.target;
		if (layer.feature.properties.clicked != null) {
			layer.feature.properties.clicked = !layer.feature.properties.clicked;
		} else {
			layer.feature.properties.clicked = true;
		}
		if (!L.Browser.ie && !L.Browser.opera) {
	        layer.bringToFront();
	    }
		clickedAreas.push(layer);
		info.update(layer.feature.properties);
	}
	*/
	
	
	//A central function. Sorts items based on whether they have been clicked or not. If they
	//have been and are clicked again, sets to false and vice versa. I am confused what pushToFront is
	//or how it interacts with the wider collection of items if there is one. 
	function showDetails(e) {
		if(interactive){
			var layer = e.target;
			if (layer.feature.properties.clicked != null) {
				layer.feature.properties.clicked = !layer.feature.properties.clicked;
			} else {
				layer.feature.properties.clicked = true;
			}
			if (!L.Browser.ie && !L.Browser.opera) {
		        layer.bringToFront();
		    }
			clickedAreas.push(layer);
			info.update(layer.feature.properties);
		}
	}
	
	
	
	//Try: just commenting first line. Then, the map still works but colors remain unchanged when clicked twice. 
	//Used to reset the color, size, etc of items to their default state(ie. after being clicked twice)
	function resetHighlight(e) {
		geojson.resetStyle(e.target);
	    info.update();
	}

	//Calls the functions on their corresponding events for EVERY feature(from tutorial)
	function onEachFeature(feature, layer) {
	    layer.on({
	        mouseover: highlightFeature,
	        mouseout: resetHighlight,
	        click: showDetails
	    });
	}
	
	//What does this do?
	geojson = L.geoJson(pompeiiPropertyData, {
		style: style,
	    onEachFeature: onEachFeature
	}).addTo(map);
	
	var info = L.control();
	info.onAdd = function(map) {
		// create a div with a class "info"
		this._div = L.DomUtil.create('div', 'info'); 
	    this.update();
	    return this._div;
	};
	
	// method that we will use to update the control based on feature properties passed
	info.update = function (props) {
		if(showHover){
			this._div.innerHTML = (props ? props.Property_Name + ", " + props.PRIMARY_DO
					: 'Hover over property to see name');
		}
	};

	info.addTo(map);
	
	//Used to acquire all of the items clicked for search(red button "Click here to search).
	//Does this by iterating through the list of clicked items and adding them to uniqueClicked,
	//then returning uniqueClicked. 
	function getUniqueClicked() {
		var uniqueClicked = [];
		var length = clickedAreas.length;
		for (var i = 0; i < length; i++) {
			var property = clickedAreas[i];
			if (!uniqueClicked.includes(property)) {
				uniqueClicked.push(property)
			}
		}
		return uniqueClicked;
	}
	//Collects the ids of the clicked item objects(the id property).
	//I disagree with many of these function names. 
	function collectClicked() {
		var propIdsOfClicked = [];
		
		var selectedProps = getUniqueClicked();
		var length = selectedProps.length;
		for (var i=0; i<length; i++) {
			var property = selectedProps[i];
			var propertyID = property.feature.properties.Property_Id;
			propIdsOfClicked.push(propertyID);
		}
		
		return propIdsOfClicked;
	}
	
	//I am confused as to the workings of this function. Looks like it is the hig/results?drawing=allhest level function
	//for searching after the user clicks the search button. 
	function DoSubmit() {
		var highlighted = collectClicked();
		var argString = "";
		if (highlighted.length > 0){
			for (var i = 0; i < highlighted.length; i++) {
				argString = argString + "property=" + highlighted[i];
				argString = argString + "&";
			}

			window.location = "results?" + argString;
			return true;
		}
		else {
			document.getElementById("hidden_p").style.visibility = "visible";
		}
	}
	
	
	//Displays the Selected Properties and their corresponding information in an HTML table formatted. 
	//Achieved by mixing html and javascript, accessing text properties of the regions(items). 
	function displayHighlightedRegions() {
		var clickedAreasTable = getUniqueClicked();
		
		var html = "<table><tr><th>Selected Properties:</th></tr>";
		var length = clickedAreasTable.length;
		for (var i=0; i<length; i++) {
			var property = clickedAreasTable[i];
			if (property.feature.properties.clicked === true) {
				html += "<tr><td><li>" +property.feature.properties.Property_Name + ", " + 
						property.feature.properties.PRIMARY_DO + "<p>"+property.feature.properties.Number_Of_Graffiti+" graffiti</p>"+ "</li></td></tr>";
			}
		}
		html += "</table";
		//Checks to avoid error for element is null.
		var elem = document.getElementById("newDiv");
		  if(typeof elem !== 'undefined' && elem !== null) {
			  document.getElementById("newDiv").innerHTML = html;
		  }
			
		// when you click anywhere on the map, it updates the table
	}
	
	
	//Handles the events(they're not handled above??).
	var el = document.getElementById("search");
	if(el!=null){
		el.addEventListener("click", DoSubmit, false);
	}
	
	var el2 = document.getElementById("pompeiimap");
	if(el2!=null){
		el2.addEventListener("click", displayHighlightedRegions, false);
	}
	
//	var locationNeeded = false;
//	if (document.title == "Ancient Graffiti Project :: Property Info") {
//		locationNeeded = true;
//	}
//	
//	// can the propertyId even be reverse accessed? Humm...
//	function focusOnProperty(propId) {
//		
//	} **** this feature is currently in the works
}
	
	
	
	








































//WITHOUT PARAMETERS












/*
function initmap() {
	var mapboxAccessToken = 'pk.eyJ1IjoibWFydGluZXphMTgiLCJhIjoiY2lxczduaG5wMDJyc2doamY0NW53M3NnaCJ9.TeA0JhIaoNKHUUJr2HyLHQ';
	
	var southWest = L.latLng(40.746, 14.48),
	northEast = L.latLng(40.754, 14.498),
	bounds = L.latLngBounds(southWest, northEast);
	
	//var map = new L.map('pompeiimap', {
		//center: [40.750, 14.4884],
		//zoom: 16,
		//minZoom: 16,
		//maxBounds: bounds
	//}); //this just sets my access token
	var map = new L.map('pompeiimap', {
	center: [40.750, 14.4884],
	zoom: 16,
	minZoom: 16,
	maxBounds: bounds
	}); //this just sets my access token
	
	//Sinks with mapbox(?), why do we need access tokens security?
	var mapboxUrl = 'https://api.mapbox.com/styles/v1/martineza18/ciqsdxkit0000cpmd73lxz8o5/tiles/256/{z}/{x}/{y}?access_token=' + mapboxAccessToken;
	
	//Is this the background-most layer? Default for leaflet?(why the map's back is grey?)
	var grayscale = new L.tileLayer(mapboxUrl, {id: 'mapbox.light', attribution: 'Mapbox Light'});
	
	//I see the clicked areas collection, but what about the rest of the items? Are they just obscurely stored by Leaflet or GeoJSON?
	var clickedAreas = [];
	
	map.addLayer(grayscale);
	L.geoJson(pompeiiPropertyData).addTo(map);
	
	//Sets the style of the portions of the map. Color is the outside borders. There are different colors for 
	//clicked or normal fragments. When clicked, items are stored in a collection. These collections will have the color
	//contained inside of else. 
	function style(feature) {
		
		var fillColor=getFillColor(feature.properties);
		var borderColor='black';
		if (feature.properties.clicked == true) {
			borderColor='yellow';
		} 
		
			return { 
	    	fillColor:fillColor,
	    	width:200,
	    	height:200, 
	        weight: 2,
	        opacity: 1,
	        color: borderColor,
	        fillOpacity: 0.7,
	    };
		L.geoJson(pompeiiPropertyData, {style: style}).addTo(map);
	}
	
	function getFillColor(propertyObject){
		if(0<=propertyObject.Number_Of_Graffiti && propertyObject.Number_Of_Graffiti<=1){
			return '#FFEDA0';
			
		}
		
		else if(1<propertyObject.Number_Of_Graffiti && propertyObject.Number_Of_Graffiti<=5){
			return '#FEB24C';
			
		}
		
		else if(5<propertyObject.Number_Of_Graffiti && propertyObject.Number_Of_Graffiti<=10){
			return '#FD8D3C';
		}
		else if(10<propertyObject.Number_Of_Graffiti && propertyObject.Number_Of_Graffiti<=15){
			return '#FC4E2A' ;
		}
		else if(15<propertyObject.Number_Of_Graffiti && propertyObject.Number_Of_Graffiti<=20){
			return '#E31A1C';
			
		}
		else if(20<propertyObject.Number_Of_Graffiti && propertyObject.Number_Of_Graffiti<=25){
			return '#BD0026';
		}
		else{
			return '#800026';
		}
	}
	
	var geojson;
	
	//Sets color for properties which the cursor is moving over. 
	function highlightFeature(e) {
		var layer = e.target;
		layer.setStyle({
			color:'yellow',
			strokeWidth:"100"
			
		});
		
		if (!L.Browser.ie && !L.Browser.opera) {
			layer.bringToFront();
		}
		info.update(layer.feature.properties);
	}
	
	function showDetails(e) {
		var layer = e.target;
		if (layer.feature.properties.clicked != null) {
			layer.feature.properties.clicked = !layer.feature.properties.clicked;
		} else {
			layer.feature.properties.clicked = true;
		}
		if (!L.Browser.ie && !L.Browser.opera) {
	        layer.bringToFront();
	    }
		clickedAreas.push(layer);
		info.update(layer.feature.properties);
	}
	
	
	
	//A central function. Sorts items based on whether they have been clicked or not. If they
	//have been and are clicked again, sets to false and vice versa. I am confused what pushToFront is
	//or how it interacts with the wider collection of items if there is one. 
	function showDetails(e) {
		var layer = e.target;
		if (layer.feature.properties.clicked != null) {
			layer.feature.properties.clicked = !layer.feature.properties.clicked;
		} else {
			layer.feature.properties.clicked = true;
		}
		if (!L.Browser.ie && !L.Browser.opera) {
	        layer.bringToFront();
	    }
		clickedAreas.push(layer);
		info.update(layer.feature.properties);
	}
	
	
	
	//Try: just commenting first line. Then, the map still works but colors remain unchanged when clicked twice. 
	//Used to reset the color, size, etc of items to their default state(ie. after being clicked twice)
	function resetHighlight(e) {
		geojson.resetStyle(e.target);
	    info.update();
	}
	//Calls the functions on their corresponding events for EVERY feature(from tutorial)
	function onEachFeature(feature, layer) {
	    layer.on({
	        mouseover: highlightFeature,
	        mouseout: resetHighlight,
	        click: showDetails
	    });
	}
	
	//What does this do?
	geojson = L.geoJson(pompeiiPropertyData, {
		style: style,
	    onEachFeature: onEachFeature
	}).addTo(map);
	
	var info = L.control();
	info.onAdd = function(map) {
		// create a div with a class "info"
		this._div = L.DomUtil.create('div', 'info'); 
	    this.update();
	    return this._div;
	};
	
	// method that we will use to update the control based on feature properties passed
	info.update = function (props) {
	    this._div.innerHTML = (props ? props.Property_Name + ", " + props.PRIMARY_DO
	        : 'Hover over property to see name');
	};

	info.addTo(map);
	
	//Used to acquire all of the items clicked for search(red button "Click here to search).
	//Does this by iterating through the list of clicked items and adding them to uniqueClicked,
	//then returning uniqueClicked. 
	function getUniqueClicked() {
		var uniqueClicked = [];
		var length = clickedAreas.length;
		for (var i = 0; i < length; i++) {
			var property = clickedAreas[i];
			if (!uniqueClicked.includes(property)) {
				uniqueClicked.push(property)
			}
		}
		return uniqueClicked;
	}
	//Collects the ids of the clicked item objects(the id property).
	//I disagree with many of these function names. 
	function collectClicked() {
		var propIdsOfClicked = [];
		
		var selectedProps = getUniqueClicked();
		var length = selectedProps.length;
		for (var i=0; i<length; i++) {
			var property = selectedProps[i];
			var propertyID = property.feature.properties.Property_Id;
			propIdsOfClicked.push(propertyID);
		}
		return propIdsOfClicked;
	}
	
	//I am confused as to the workings of this function. Looks like it is the hig/results?drawing=allhest level function
	//for searching after the user clicks the search button. 
	function DoSubmit() {
		var highlighted = collectClicked();
		var argString = "";
		if (highlighted.length > 0){
			for (var i = 0; i < highlighted.length; i++) {
				argString = argString + "property=" + highlighted[i];
				argString = argString + "&";
			}

			window.location = "results?" + argString;
			return true;
		}
		else {
			document.getElementById("hidden_p").style.visibility = "visible";
		}
	}
	
	
	//Displays the Selected Properties and their corresponding information in an HTML table formatted. 
	//Achieved by mixing html and javascript, accessing text properties of the regions(items). 
	function displayHighlightedRegions() {
		var clickedAreasTable = getUniqueClicked();
		
		var html = "<table><tr><th>Selected Properties:</th></tr>";
		var length = clickedAreasTable.length;
		for (var i=0; i<length; i++) {
			var property = clickedAreasTable[i];
			if (property.feature.properties.clicked === true) {
				html += "<tr><td><li>" +property.feature.properties.Property_Name + ", " + 
						property.feature.properties.PRIMARY_DO + "<p>"+property.feature.properties.Number_Of_Graffiti+" graffiti</p>"+ "</li></td></tr>";
			}
		}
		html += "</table";
		document.getElementById("newDiv").innerHTML = html;
		// when you click anywhere on the map, it updates the table
	}
	
	
	//Handles the events(they're not handled above??).
	var el = document.getElementById("search");
	el.addEventListener("click", DoSubmit, false);
	
	var el2 = document.getElementById("pompeiimap");
	el2.addEventListener("click", displayHighlightedRegions, false);
	
//	var locationNeeded = false;
//	if (document.title == "Ancient Graffiti Project :: Property Info") {
//		locationNeeded = true;
//	}
//	
//	// can the propertyId even be reverse accessed? Humm...
//	function focusOnProperty(propId) {
//		
//	} **** this feature is currently in the works
}
	
	
	
	*/
	
	
	
	