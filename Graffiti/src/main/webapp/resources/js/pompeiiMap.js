
var map;



function initmap(moreZoom=false,showHover=true,colorDensity=true,interactive=true,propertyIdToHighlight=0,propertyIdListToHighlight=[],zoomOnOneProperty) {
	//this just sets my access token
	var mapboxAccessToken = 'pk.eyJ1IjoibWFydGluZXphMTgiLCJhIjoiY2lxczduaG5wMDJyc2doamY0NW53M3NnaCJ9.TeA0JhIaoNKHUUJr2HyLHQ';
	var borderColor;
	var fillColor;
	var southWest = L.latLng(40.746, 14.48),
	
	northEast = L.latLng(40.754, 14.498),
	bounds = L.latLngBounds(southWest, northEast);
	
	var currentZoomLevel;
	var propertySelected;
	
	//The maximum zoom level to show insula view instead of property view(smaller zoom level means more zoomed out)
	var insulaViewZoomLevel=17;
	
	if(!moreZoom){
		currentZoomLevel=16;
	}
	else{
		currentZoomLevel=15;
	}
	var zoomLevelForIndividualProperty=18;
	var initialZoomNotCalled=true;
	var totalInsulaGraffitisDict=new Array();
	var currentInsulaNumber;
	var graffitiInLayer;
	var numberOfGraffitiInGroup;
	var justStarted=true;
	
	
	//Fires when the map is initialized

	map = new L.map('pompeiimap', {
		center: [40.750, 14.4884],
		zoom: currentZoomLevel,
		minZoom: currentZoomLevel,
		maxBounds: bounds,
		//Here is the +/- button for zoom
	})
	
	
	//Sinks with mapbox(?), why do we need access tokens security?
	var mapboxUrl = 'https://api.mapbox.com/styles/v1/martineza18/ciqsdxkit0000cpmd73lxz8o5/tiles/256/{z}/{x}/{y}?access_token=' + mapboxAccessToken;
	
	//This adds more realistic features to the background like streets. Commented out bc/shape files are off positionally and more details shows it to users. 
	//var grayscale = new L.tileLayer(mapboxUrl, {id: 'mapbox.light', attribution: 'Mapbox Light'});

	//I see the clicked areas collection, but what about the rest of the items? Are they just obscurely stored by Leaflet or GeoJSON?
	var clickedAreas = [];
	
	
	//map.addLayer(grayscale);
	L.geoJson(pompeiiPropertyData).addTo(map);
	
	//A listener for zoom events. 
	map.on('zoomend', function(e) {
		dealWithInsulaLevelView();
	});
	
	//Centers the map around a single property
	function showCloseUpView(){
		if(propertyIdToHighlight){
			var newCenterCoordinates=[];
			map.eachLayer(function(layer){
				if(layer.feature!=undefined){
					if(layer.feature.properties.Property_Id==propertyIdToHighlight){
						newCenterCoordinates=layer.getBounds().getCenter();
						map.setView(newCenterCoordinates,zoomLevelForIndividualProperty);
					}
				}
			});
		}
	}
	
	
	if(propertyIdToHighlight!=0)
	{
		showCloseUpView();
	}
	
	//Responsible for showing the map view on the insula level. 
	function dealWithInsulaLevelView(){
		
		//This must be reset
		totalInsulaGraffitisDict=new Array();
		map.eachLayer(function(layer){
			if(zoomedOutThresholdReached() && layer.feature!=undefined){
				graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
				layer.setStyle({color: getFillColor(graffitiInLayer)});
				currentInsulaNumber=getFirstDigitInString(layer.feature.properties.PRIMARY_DO);
				if(totalInsulaGraffitisDict[currentInsulaNumber]!=undefined){
					totalInsulaGraffitisDict[currentInsulaNumber]+=graffitiInLayer;
				}
				else{
					totalInsulaGraffitisDict[currentInsulaNumber]=graffitiInLayer;
				}
			}
			//Resets properties when user zooms back in
			else if (colorDensity && layer.feature!=undefined){
				layer.setStyle({color: getBorderColorForCloseZoom(layer.feature)});
				graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
				layer.setStyle({fillColor: getFillColor(graffitiInLayer)});
			}
			
		});
		//The second loop fills in the colors of the properties to match the total group color. 
		//Again, only runs if the above conditions are true. 
		//Empty slots are caused by there not yet being a group at those indexes yet them being surrounded by values. 
		
		map.eachLayer(function(layer){
			if(zoomedOutThresholdReached() && layer.feature!=undefined){
				currentInsulaNumber=getFirstDigitInString(layer.feature.properties.PRIMARY_DO);
				numberOfGraffitiInGroup=totalInsulaGraffitisDict[currentInsulaNumber];
				layer.setStyle({fillColor: getFillColor(numberOfGraffitiInGroup)});
				layer.setStyle({color: getFillColor(numberOfGraffitiInGroup)});
				fillColor=getFillColor(numberOfGraffitiInGroup);
				borderColor=fillColor;
			}
			
		});
	}
	
	//Gets and returns the first digit 1-9 in a string of characters. Returned in string form. 
	//Assumes no primary DO over 10(?)
	function getFirstDigitInString(oneString){
		//Converts string to list of chars so we can take the index
		//oneString=oneString.split('');
		var character;
		var i;
		for(i=0;i<oneString.length;i++){
			character=oneString[i];
			if(['0','1','2','3','4','5','6','7','8','9'].indexOf(character)>=0){
				return character;
			}
		}
		//this should never happen
		return false;
	}
	
	function zoomedOutThresholdReached(){
		currentZoomLevel=map.getZoom();
		return (currentZoomLevel<=insulaViewZoomLevel && colorDensity);
	}
	
	function getBorderColorForCloseZoom(feature){
		borderColor='white';
		if (feature.properties.clicked == true || feature.properties.Property_Id==propertyIdToHighlight || propertyIdListToHighlight.indexOf(feature.properties.Property_Id)>=0) {
			propertySelected=true;
			return 'black';
		}
		return 'white';
	}
	
	//Sets the style of the portions of the map. Color is the outside borders. There are different colors for 
	//clicked or normal fragments. When clicked, items are stored in a collection. These collections will have the color
	//contained inside of else. 
	function style(feature) {
		//Displays the insula level view at the start of the run if necessary
		propertySelected=false;
		
		borderColor=getBorderColorForCloseZoom(feature);
		fillColor=getFillColor(feature.properties.Number_Of_Graffiti);
		//Try: setStyle instead of returning if this was called using the zoomListener(extra boolean param to check this??)
		return { 
	    	fillColor:fillColor,
	    	width:200,
	    	height:200, 
	        weight: 1,
	        opacity: 1,
	        color: borderColor,
	        fillOpacity: 0.7,
	    };
		
	    //Problem: this does not appear to be called(statement not logging). 
	    //However, when moved before the return statement console prints too much recursion and map breaks. 
	    //Is this a form of javascript recursion?
	    //Maybe I need to use the return contents in my zoom listener?
		//L.geoJson(pompeiiPropertyData, {style: style}).addTo(map);
	}
	
	function getFillColor(numberOfGraffiti){
		//Hex darkens color as number it represents decreases
		if(colorDensity){

			if(0<=numberOfGraffiti && numberOfGraffiti<=2){
				return '#FFEDC0';
			}
			
			else if(2<numberOfGraffiti && numberOfGraffiti<=5){
				return '#FFEDA0';
				
			}
			
			else if(5<numberOfGraffiti && numberOfGraffiti<=10){
				return '#fed39a';
				
			}
			
			else if(10<numberOfGraffiti && numberOfGraffiti<=20){
				return '#fec880';
				
			}
			
			else if(20<numberOfGraffiti && numberOfGraffiti<=30){
				return '#FEB24C';
				
			}
			
			else if(30<numberOfGraffiti && numberOfGraffiti<=40){
				return '#fe9b1b';
				
			}
			
			else if(40<numberOfGraffiti && numberOfGraffiti<=60){
				return '#fda668';
			}
			
			else if(60<numberOfGraffiti && numberOfGraffiti<=80){
				return '#FD8D3C';
			}
			
			else if(80<numberOfGraffiti && numberOfGraffiti<=100){
				return '#fd7a1c';
			}
			
			
			else if(100<numberOfGraffiti && numberOfGraffiti<=130){
				return '#fc6c4f' ;
			}
			
			else if(130<numberOfGraffiti && numberOfGraffiti<=160){
				return '#FC4E2A' ;
			}
			
			else if(160<numberOfGraffiti && numberOfGraffiti<=190){
				return '#fb2d04' ;
			}
			else if(190<numberOfGraffiti && numberOfGraffiti<=210){
				return '#ea484b';
				
			}
			
			else if(210<numberOfGraffiti && numberOfGraffiti<=240){
				return '#E31A1C';
				
			}
			
			else if(240<numberOfGraffiti && numberOfGraffiti<=270){
				return '#b71518';
				
			}
			
			else if(270<numberOfGraffiti && numberOfGraffiti<=300){
				return '#cc0029';
			}
			
			else if(300<numberOfGraffiti && numberOfGraffiti<=330){
				return '#b30024';
			}
			
			else if(330<numberOfGraffiti && numberOfGraffiti<=360){
				return '99001f';
			}
			
			else if(360<numberOfGraffiti && numberOfGraffiti<=390){
				return '#80001a';
			}
			
			else if(390 <numberOfGraffiti && numberOfGraffiti<=420){
				return '#660014';
			}
			
			else if(420<numberOfGraffiti && numberOfGraffiti<=460){
				return '#4d000f';
			}
			
			else if(460<numberOfGraffiti && numberOfGraffiti<=500){
				return '#33000a';
			}
			
			else{
				return '#000000';
			}
			}
		//If the property is selected and there is no colorDensity, make the fill color be maroon(dark red).
		//PropertySelected is a global variable altered within getBorderColorForCloseZoom and style.
		if(propertySelected){
			return '#800000';
		}
		//What should this be?
		//return '#800026';
		//return 'green';
		return '#FEB24C' ;
	}
	
	var geojson;
	
	//Sets color for properties which the cursor is moving over. 
	function highlightFeature(e) {
		if(interactive && !zoomedOutThresholdReached()){
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
	
	
	//A central function. Sorts items based on whether they have been clicked or not. If they
	//have been and are clicked again, sets to false and vice versa. I am confused what pushToFront is
	//or how it interacts with the wider collection of items if there is one. 
	function showDetails(e) {
		if(interactive && !zoomedOutThresholdReached()){
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
		if(interactive && !zoomedOutThresholdReached()){
		geojson.resetStyle(e.target);
	    info.update();
		}
	}

	//Calls the functions on their corresponding events for EVERY feature(from tutorial)
	function onEachFeature(feature, layer) {
	    layer.on({
	        mouseover: highlightFeature,
	        mouseout: resetHighlight,
	        click: showDetails,
	    });
	   
	}
	
	
	//What does this do?
	geojson = L.geoJson(pompeiiPropertyData, {
		style: style,
	    onEachFeature: onEachFeature
	    
	}).addTo(map);
	//Putting this after the above appears to make it this start correctly.
	 if(initialZoomNotCalled==true){
		   dealWithInsulaLevelView();
		   initialZoomNotCalled=false;
	 }
	
	var info = L.control();
	info.onAdd = function(map) {
		// create a div with a class "info"
		this._div = L.DomUtil.create('div', 'info'); 
	    this.update();
	    return this._div;
	};
	
	// method that we will use to update the control based on feature properties passed
	function updateHoverText(){
		info.update = function (props) {
			if(showHover){
				this._div.innerHTML = (props ? props.Property_Name + ", " + props.PRIMARY_DO
						: 'Hover over property to see name');
			}
		};
	
		info.addTo(map);
	}
	
	updateHoverText();
	
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
			/*alert(property.feature.geometry.coordinates);*/
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
	
	showCloseUpView();
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
	
	
	
	




	