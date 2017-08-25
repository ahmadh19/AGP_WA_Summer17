var hercMap;

var SELECTED_COLOR = '#800000';
// an orangey-yellow
var DEFAULT_COLOR = '#FEB24C';

function initHerculaneumMap(moreZoom=false,showHover=true,colorDensity=true,interactive=true,propertyIdToHighlight=0,propertyIdListToHighlight=[],zoomOnOneProperty) {
	// this just sets my access token
	var mapboxAccessToken = 'pk.eyJ1IjoibWFydGluZXphMTgiLCJhIjoiY2lxczduaG5wMDJyc2doamY0NW53M3NnaCJ9.TeA0JhIaoNKHUUJr2HyLHQ';
	var borderColor;
	var fillColor;
	var southWest = L.latLng(40.8040619, 14.343131),
	northEast = L.latLng(40.8082619, 14.351131),
	bounds = L.latLngBounds(southWest, northEast);
	
	var currentZoomLevel;
	
	// The maximum zoom level to show insula view instead of property
	// view(smaller zoom level means more zoomed out)
	var insulaViewZoomLevel=17;
	
	if(moreZoom){
		currentZoomLevel=17;
	}
	else{
		currentZoomLevel=18;
	}
	
	var showInsulaMarkers;
	var zoomLevelForIndividualProperty=18;
	var initialZoomNotCalled=true;
	var totalInsulaGraffitisDict=new Array();
	var graffitiInLayer;
	var numberOfGraffitiInGroup;
	
	var insulaMarkersList=[];
	var clickedInsula=[];
	
	// Holds the center latitudes and longitudes of all insula on the map.
	var insulaCentersDict=[];
	var insulaNumProperties=[];
	var pointsAccumulatorDict=[];

	var insulaGroupIdsList=[];
	var insulaShortNamesDict=[];
	
	// Fires when the map is initialized
	hercMap = new L.map('herculaneummap', {
		center: [40.8059119, 14.3473933], 
		zoom: currentZoomLevel,
		minZoom: currentZoomLevel-1,
		maxZoom:20,
		maxBounds: bounds,
	});
	
	var herculaneumProperties = L.geoJson(herculaneumPropertyData, {
		style: propertyStyle,
	    onEachFeature: onEachProperty
	}).addTo(hercMap);
	
	
	// Sinks with mapbox(?), why do we need access tokens security?
	var mapboxUrl = 'https://api.mapbox.com/styles/v1/martineza18/ciqsdxkit0000cpmd73lxz8o5/tiles/256/{z}/{x}/{y}?access_token=' + mapboxAccessToken;
	
	var clickedAreas = [];
	
	var propertyLevelLegend = L.control({position: 'bottomright'});

	propertyLevelLegend.onAdd = function (map) {

		var div = L.DomUtil.create('div', 'info legend'),
			grades = [0, 5, 10],
			labels = [],
			from, to;
		labels.push(
				'<i style="background:' + getFillColor(0) + '"></i> ' + 0);
		
		for (var i = 0; i < grades.length; i++) {
			from = grades[i];
			to = grades[i + 1];

			labels.push(
				'<i style="background:' + getFillColor(from + 1) + '"></i> ' +
				(from+1) + (to ? '&ndash;' + to : '+'));
		}

		div.innerHTML = labels.join('<br>');
		return div;
	};
	
	var insulaLevelLegend = L.control({position: 'bottomright'});

	insulaLevelLegend.onAdd = function (map) {

		var div = L.DomUtil.create('div', 'info legend'),
			//grades = [0, 5, 10, 20, 30, 40, 60, 80, 100, 130, 160, 190, 210, 240, 270, 300, 330, 360, 390, 420, 460, 500],
			grades = [0, 5, 10, 20, 30, 40, 60, 80],
			labels = [],
			from, to;
		
		labels.push(
				'<i style="background:' + getFillColor(0) + '"></i> ' + 0);
		
		for (var i = 0; i < grades.length; i++) {
			from = grades[i];
			to = grades[i + 1];

			labels.push(
				'<i style="background:' + getFillColor(from + 1) + '"></i> ' +
				(from+1) + (to ? '&ndash;' + to : '+'));
		}

		div.innerHTML = labels.join('<br>');
		return div;
	};
	
	if(interactive){
		makeInsulaCentersDict();
		makeTotalInsulaGraffitiDict();
		makeInsulaIdsListShortNamesList();
		displayInsulaLabels();
		//insulaLevelLegend.addTo(hercMap);
		//legend.remove(hercMap);
		
		hercMap.addControl(new L.Control.Compass({autoActive: true, position: "bottomleft"}));
	}
	
	// A listener for zoom events.
	hercMap.on('zoomend', function(e) {
		dealWithInsulaLevelView();
		dealWithInsulaLabelsAndSelectionOnZoom();
	});
	
	// Centers the map around a single property
	function showCloseUpView(){
		if(propertyIdToHighlight){
			var newCenterCoordinates=[];
			hercMap.eachLayer(function(layer){
				if(layer.feature!=undefined){
					if(layer.feature.properties.Property_Id==propertyIdToHighlight){
						newCenterCoordinates=layer.getBounds().getCenter();
						hercMap.setView(newCenterCoordinates,zoomLevelForIndividualProperty);
					}
				}
			});
		}
		else if(propertyIdListToHighlight.length==1){
			newCenterCoordinates=[];
			hercMap.eachLayer(function(layer){
				if(layer.feature!=undefined){
					if(layer.feature.properties.Property_Id==propertyIdListToHighlight[0]){
						newCenterCoordinates=layer.getBounds().getCenter();
						hercMap.setView(newCenterCoordinates,zoomLevelForIndividualProperty);
					}
				}
			});
		}
	}
	
	if(propertyIdToHighlight!=0 || propertyIdListToHighlight.length==1)	{
		showCloseUpView();
	}
	
	// Responsible for showing the map view on the insula level.
	function dealWithInsulaLevelView(){
		// This must be reset
		totalInsulaGraffitisDict=new Array();
		hercMap.eachLayer(function(layer){
			if(zoomedOutThresholdReached() && layer.feature!=undefined){
				graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
				layer.setStyle({color: getFillColor(graffitiInLayer)});
				currentInsulaNumber=layer.feature.properties.insula_id;
				if(totalInsulaGraffitisDict[currentInsulaNumber]!=undefined){
					totalInsulaGraffitisDict[currentInsulaNumber]+=graffitiInLayer;
				}
				else{
					totalInsulaGraffitisDict[currentInsulaNumber]=graffitiInLayer;
				}
				if(propertyLevelLegend._map) {
					propertyLevelLegend.remove(hercMap);
				}
				//insulaLevelLegend.addTo(hercMap);
			}
			// Resets properties when user zooms back in
			else if (colorDensity && layer.feature!=undefined){
				layer.setStyle({color: getBorderColorForCloseZoom(layer.feature)});
				graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
				layer.setStyle({fillColor: getFillColor(graffitiInLayer)});
				if(insulaLevelLegend._map) {
					insulaLevelLegend.remove(hercMap);
				}
				propertyLevelLegend.addTo(hercMap);
			}
			
		});
		// The second loop fills in the colors of the properties to match the
		// total group color.
		// Again, only runs if the above conditions are true.
		// Empty slots are caused by there not yet being a group at those
		// indexes yet them being surrounded by values.
		hercMap.eachLayer(function(layer){
			if(zoomedOutThresholdReached() && layer.feature!=undefined){
				currentInsulaNumber=layer.feature.properties.insula_id;
				numberOfGraffitiInGroup=totalInsulaGraffitisDict[currentInsulaNumber];
				// For an unknown reason, forEachLayer loops through two times
				// instead of one.
				// We compensate by dividing number of graffiti by two(?).
				newFillColor=getFillColor(numberOfGraffitiInGroup/2);
				layer.setStyle({fillColor:newFillColor});
				layer.setStyle({color: getFillColor(numberOfGraffitiInGroup)});
				
				borderColor=newFillColor;
			}
			
		});
		
	}
	
	// Returns a new array with the contents of the previous index absent
	// We must search for a string in the array because, again, indexOf does not
	// work for nested lists.
	function removeStringedListFromArray(someArray,stringPortion){
		var newArray=[];
		var i;
		for(i=0;i<someArray.length;i++){
			if(""+someArray[i]!=stringPortion){
				newArray.push(someArray[i]);
			}
		}
		return newArray;
	}
	
	function updateBorderColors(){
		hercMap.eachLayer(function(layer){
			if(layer.feature!=undefined && layer.feature.properties.clicked ){
				borderColor=getBorderColorForCloseZoom(layer.feature);
				// layer.feature.setStyle(color,borderColor);
			}
		});
	}
	
	// Shows or hides insula labels depending on zoom levels and if the map is
	// interactive
	function dealWithInsulaLabelsAndSelectionOnZoom(){
		// console.log("5");
		if(interactive){
			if(!zoomedOutThresholdReached()){
				if(showInsulaMarkers){
					// removeInsulaLabels();
					showInsulaMarkers=false;
					// This shows selected properties from the insula when the
					// map zooms in.
					updateBorderColors();
				}
			}
			else if(!showInsulaMarkers){
				// displayInsulaLabels();
				showInsulaMarkers=true;
			}
		}
	}
	
	// Builds the global list of insula ids.
	function makeInsulaIdsListShortNamesList(){
		var currentInsulaId=183;
		hercMap.eachLayer(function(layer){
			if(layer.feature!=undefined){
				if(layer.feature.properties.insula_id!=currentInsulaId){
					if(insulaGroupIdsList.indexOf(currentInsulaId)==-1){
						insulaGroupIdsList.push(currentInsulaId);
					}
				}
				currentInsulaId=layer.feature.properties.insula_id;
				insulaShortNamesDict[currentInsulaId]=layer.feature.properties.short_insula_name;	
			}
		});
	}
	
	// Builds the dictionary of the graffiti in each insula
	// This works well as graffiti numbers should not change over the session.
	// Modifies the clojure wide variable once and only once at the beginning of
	// the program
	function makeTotalInsulaGraffitiDict(){
		totalInsulaGraffitisDict=new Array();
		hercMap.eachLayer(function(layer){
			if(zoomedOutThresholdReached() && layer.feature!=undefined){
				graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
				currentInsulaNumber=layer.feature.properties.insula_id;
				if(totalInsulaGraffitisDict[currentInsulaNumber]!=undefined){
					totalInsulaGraffitisDict[currentInsulaNumber]+=graffitiInLayer;
				}
				else {
					totalInsulaGraffitisDict[currentInsulaNumber]=graffitiInLayer;
				}
			}
		});
	}
	
	// This function gets and returns a "dictionary" of the latitude and
	// longitude of each insula given its id (as index).
	// Used to find where to place the labels of each insula on the map, upon
	// iteration through this list.
	// This creates a weighted average; it isn't really the center.
	function makeInsulaCentersDict(){
		var currentInsulaNumber;
		
		if( interactive ) {
			// total up the centers of all the properties for each insula.
			hercMap.eachLayer(function(layer){
				if(layer.feature!=undefined){
					currentInsulaNumber=layer.feature.properties.insula_id;
					currentCoordinatesList=layer.feature.geometry.coordinates;
					// The array is in an array in an array
					propertyCenter=findCenter(currentCoordinatesList[0][0]);
					// SS: Not sure why this code doesn't work.
					//center = layer.getBounds().getCenter();
					//propertyCenter=[center.lat, center.lng];
					//console.log(propertyCenter);
					
					// create a new entry
					if( pointsAccumulatorDict[currentInsulaNumber] == undefined) {
						pointsAccumulatorDict[currentInsulaNumber] = [0,0];
						insulaNumProperties[currentInsulaNumber] = 0;
					}
					// update entry
					pointsAccumulatorDict[currentInsulaNumber][0] += propertyCenter[0];
					pointsAccumulatorDict[currentInsulaNumber][1] += propertyCenter[1];
					insulaNumProperties[currentInsulaNumber] += 1;
				}
			});
			
			// calculate the averages
			hercMap.eachLayer(function(layer){
				if(layer.feature!=undefined){
					currentInsulaNumber=layer.feature.properties.insula_id;
					
					numProperties = insulaNumProperties[currentInsulaNumber];
					xTotal = pointsAccumulatorDict[currentInsulaNumber][0];
					yTotal = pointsAccumulatorDict[currentInsulaNumber][1];

					centerCoord = [xTotal/numProperties, yTotal/numProperties];
					insulaCentersDict[currentInsulaNumber]=centerCoord;
					
					//console.log( currentInsulaNumber + " " + numProperties + " " + centerCoord);
				}
			});
			
		}
	}
	
	// Uses math to directly find and return the latitude and longitude of the
	// center of a list of coordinates.
	// Seems to be just one long list; not broken into coordinates
	// Returns a list of the latitude, x and the longitude, y
	function findCenter(coordinatesList){
		var x=0;
		var y=0;
		var pointsSoFar=0;
		for(var i=0;i<coordinatesList.length;i++){
			x+=coordinatesList[i][0];
			y+=coordinatesList[i][1];
			pointsSoFar+=1;
		}
		return [x/pointsSoFar,y/pointsSoFar];
	}
	
	
	// Marks all properties inside of selected insula as selected by
	// adding them to the clickedInsula list.
	function selectPropertiesInAllSelectedInsula(uniqueClicked){
		if(interactive){
			var i=0;
			var currentInsulaId;
			var currentInsula;
			var listOfSelectedInsulaIds=[];
			for(i;i<clickedInsula.length;i++){
				currentInsula=clickedInsula[i];
				currentInsulaId=currentInsula[1];
				listOfSelectedInsulaIds.push(currentInsulaId);	
			}
			hercMap.eachLayer(function(layer){
				if(layer.feature!=undefined){
					if(listOfSelectedInsulaIds.indexOf(layer.feature.properties.insula_id)!=-1 && !uniqueClicked.includes(layer)){
						uniqueClicked.push(layer);
						layer.feature.properties.clicked=true;

					}
				}
			});
		}
		return uniqueClicked;

	}
	
	var createLabelIcon = function(labelClass,labelText){
		return L.divIcon({ 
			className: labelClass,
			html: labelText
		});
	}
	
	function showALabelOnMap(xYCoordinates,textToDisplay){
		if(textToDisplay!="OutsideWalls" && textToDisplay!="WithinWalls"){
		var myIcon = L.divIcon({ 
		    // iconSize: new L.Point(0, 0),
			// iconSize:0,
			className: "labelClassHerculaneum",
		    html: textToDisplay
		});
		// you can set .my-div-icon styles in CSS
		var myMarker=new L.marker([xYCoordinates[1], xYCoordinates[0]], {icon: myIcon}).addTo(hercMap);
		insulaMarkersList.push(myMarker);
		}
	}
	
	// Shows the short names of each insula in black
	// at the center coordinates.
	function displayInsulaLabels(){
		var i;
		var insulaId;
		var insulaCenterCoordinates;
		var shortInsulaName;
		for(i=0;i<insulaGroupIdsList.length;i++){
			insulaId=insulaGroupIdsList[i];
			insulaCenterCoordinates=insulaCentersDict[insulaId];
			// console.log(insulaCentersDict+"h");
			shortInsulaName=insulaShortNamesDict[insulaId];
			if(insulaCenterCoordinates && ! isNaN(insulaCenterCoordinates[0]) && ! isNaN(insulaCenterCoordinates[1])){
				showALabelOnMap(insulaCenterCoordinates,shortInsulaName);
			}
		}
	}
	
	function zoomedOutThresholdReached(){
		currentZoomLevel=hercMap.getZoom();
		return (currentZoomLevel<=insulaViewZoomLevel && colorDensity);
	}
	
	function zoomedInThresholdReached(){
		currentZoomLevel=hercMap.getZoom();
		return (currentZoomLevel>=zoomLevelForIndividualProperty && colorDensity);
	}
	
	function getBorderColorForCloseZoom(feature){
		borderColor='white';
		if (isPropertySelected(feature)) {
			return 'black';
		}
		return 'white';
	}
	
	function isPropertySelected(feature) {
		return feature.properties.clicked == true || feature.properties.Property_Id==propertyIdToHighlight || propertyIdListToHighlight.indexOf(feature.properties.Property_Id)>=0;
	}
	
	// Sets the style of the portions of the map. Color is the outside borders.
	// There are different colors for
	// clicked or normal fragments. When clicked, items are stored in a
	// collection. These collections will have the color
	// contained inside of else.
	function propertyStyle(feature) {
		// Displays the insula level view at the start of the run if necessary
		borderColor=getBorderColorForCloseZoom(feature);
		if( isPropertySelected(feature)) {
			fillColor = SELECTED_COLOR;
		}
		else if( colorDensity ) {
			fillColor = getFillColor( feature.properties.Number_Of_Graffiti);
		} else {
			fillColor=getFillColorByFeature(feature);
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
	}
	
	function getFillColor(numberOfGraffiti){
		if(colorDensity){
			if( zoomedOutThresholdReached() ) { // for insula level
				return numberOfGraffiti == 0   ? '#FFEDC0' :
					   numberOfGraffiti <= 5   ? '#FFEDA0' :
					   numberOfGraffiti <= 10  ? '#fed39a' :
					   numberOfGraffiti <= 20  ? '#fec880' :
					   numberOfGraffiti <= 90 ? '#FEB24C':
												 '#000000';
			} else { // for property level
				return numberOfGraffiti == 0   ? '#FFEDC0' :
					   numberOfGraffiti <= 5   ? '#FFEDA0' :
					   numberOfGraffiti <= 10  ? '#fed39a' :
					   numberOfGraffiti <= 90  ? '#fec880' :
												 '#000000';
			}
		}
		
		return DEFAULT_COLOR;
	}
	
	function getFillColorByFeature(feature){
		if( isPropertySelected(feature)) {
			return SELECTED_COLOR;
		}
		return DEFAULT_COLOR;
	}
	
	// On click, sees if a new insula id # has been selected. If so, adds it to
	// the list of
	// selected insula.
	function checkForInsulaClick(clickedProperty){
		// Clicked property is a layer
		// layer.feature.properties.insula_id
		
		// indexOf does not work for nested lists. Thus, we have no choice but
		// to use it with strings.
		var clickedInsulaAsString=""+clickedInsula;
		var clickedInsulaFullName=clickedProperty.feature.properties.full_insula_name;
		var clickedInsulaId=clickedProperty.feature.properties.insula_id;
		var clickedInsulaShortName=clickedProperty.feature.properties.short_insula_name;
		var targetInsulaString=""+[clickedInsulaFullName,clickedInsulaId,clickedInsulaShortName];
		var indexOfInsulaName=clickedInsulaAsString.indexOf(targetInsulaString);

		// Only adds the new id if it is not already in the list
		if(indexOfInsulaName==-1){
			clickedInsula.push([clickedInsulaFullName,clickedInsulaId,clickedInsulaShortName]);
		}
		// Otherwise, removed the insula id from the list to deselect it
		else{
			clickedInsula=removeStringedListFromArray(clickedInsula,targetInsulaString);
		}
	}
	
	// Used on click for insula level view in place of display selected regions
	// In charge of the right information only, does not impact the actual map
	function displayHighlightedInsula(){
		// clickedInsula.push([clickedInsulaFullName,clickedInsulaId,clickedInsulaShortName]);
		var html = "<strong>Selected Insula:</strong><ul>";
		var numberOfInsulaSelected=clickedInsula.length;
		for (var i=0; i<numberOfInsulaSelected; i++) {
			html += "<li>"+clickedInsula[i][0] + ", " +
					"<p>"+totalInsulaGraffitisDict[clickedInsula[i][1]]+" graffiti</p>"+ "</li>"
		}
		html += "</ul>";
		// Checks to avoid error for element is null.
		var elem = document.getElementById("toSearch");
		if(typeof elem !== 'undefined' && elem !== null) {
			document.getElementById("toSearch").innerHTML = html;
		}
	}
	
	// Sets color for properties which the cursor is moving over.
	function highlightFeature(e) {
		if(interactive && !zoomedOutThresholdReached()){
			var layer = e.target;
			layer.setStyle({
				color:'maroon',
				strokeWidth:"100"
			});
		
			if (!L.Browser.ie && !L.Browser.opera) {
				layer.bringToFront();
			}
			info.update(layer.feature.properties);
		}
	}
	
	
	// If they have been clicked and are clicked again, sets to false and vice versa. I am
	// confused what pushToFront is
	// or how it interacts with the wider collection of items if there is one.
	function showDetails(e) {
		if(interactive){
			if(!zoomedOutThresholdReached()){
				var layer = e.target;
				if (layer.feature.properties.clicked != null) {
					layer.feature.properties.clicked = !layer.feature.properties.clicked;
					if(layer.feature.properties.clicked == false) {
						resetHighlight(e);
						var index = clickedAreas.indexOf(layer);
						if(index > -1) {
							clickedAreas.splice(index, 1);
						}
					} else {
						e.target.setStyle({fillColor:SELECTED_COLOR});
						clickedAreas.push(layer);
					}
				} else {
					layer.feature.properties.clicked = true;
					e.target.setStyle({fillColor:SELECTED_COLOR});
					clickedAreas.push(layer);
				}
				if (!L.Browser.ie && !L.Browser.opera) {
			        layer.bringToFront();
			    }
				info.update(layer.feature.properties);
			}
			else {
				checkForInsulaClick(e.target);
			}
		}
	}
	
	
	// Used to reset the color, size, etc of items to their default state(ie.
	// after being clicked twice)
	function resetHighlight(e) {
		if(interactive && !zoomedOutThresholdReached()){
			herculaneumProperties.resetStyle(e.target);
			info.update();
		}
	}

	// Calls the functions on their corresponding events for EVERY feature(from
	// tutorial)
	function onEachProperty(feature, layer) {
	    layer.on({
	        mouseover: highlightFeature,
	        mouseout: resetHighlight,
	        click: showDetails,
	    });
	}
	
	// Putting this after the above appears to make it this start correctly.
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
	
	// method that we will use to update the control based on feature properties
	// passed
	function updateHoverText(){
		info.update = function (props) {
			if(showHover){
				this._div.innerHTML = (props ? props.Property_Address + " " + props.Property_Name
						: 'Hover over property to see name');
			}
		};
	
		info.addTo(hercMap);
	}
	
	updateHoverText();
	
	// Used to acquire all of the items clicked for search(red button "Click
	// here to search).
	// Does this by iterating through the list of clicked items and adding them
	// to uniqueClicked,
	// then returning uniqueClicked.
	function getUniqueClicked() {
		var uniqueClicked = [];
		var length = clickedAreas.length;
		for (var i = 0; i < length; i++) {
			var property = clickedAreas[i];
			if (!uniqueClicked.includes(property)) {
				uniqueClicked.push(property)
			}
		}
		uniqueClicked=selectPropertiesInAllSelectedInsula(uniqueClicked);
		return uniqueClicked;
	}

	// Collects the ids of the clicked item objects(the id property).
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
	
	// function called when user clicks the search button.
	function searchProperties() {
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
	
	// Displays the Selected Properties and their corresponding information in
	// an HTML table formatted.
	// Achieved by mixing html and javascript, accessing text properties of the
	// regions(items).
	function displayHighlightedRegions() {
		if(!zoomedOutThresholdReached()){
			var clickedAreasTable = getUniqueClicked();
			
			var html = "<strong>Selected Properties:</strong><ul>";
			var length = clickedAreasTable.length;
			for (var i=0; i<length; i++) {
				var property = clickedAreasTable[i];
				/* alert(property.feature.geometry.coordinates); */
				if (property.feature.properties.clicked === true) {
					
					html += "<li>" +property.feature.properties.Property_Address + ", " +property.feature.properties.Property_Name + ", " + 
							"<p>"+property.feature.properties.Number_Of_Graffiti+" graffiti</p>"+ "</li>";
				}
			}
			html += "</ul>";
			// Checks to avoid error for element is null.
			var elem = document.getElementById("toSearch");
			if(typeof elem !== 'undefined' && elem !== null) {
				document.getElementById("toSearch").innerHTML = html;
			}
				
		}
		else{
			displayHighlightedInsula();
			var clickedAreasTable = getUniqueClicked();
		}	
	}
	
	// handles additional events.
	var el = document.getElementById("search");
	if(el!=null){
		el.addEventListener("click", searchProperties, false);
	}
	
	var el2 = document.getElementById("herculaneummap");
	if(el2!=null){
		el2.addEventListener("click", displayHighlightedRegions, false);
	}
	
}
	
	
	
	




	