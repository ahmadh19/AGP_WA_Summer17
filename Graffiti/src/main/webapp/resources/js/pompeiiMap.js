var pompeiiMap;

var SELECTED_COLOR = '#800000';
var DEFAULT_COLOR = '#FEB24C';
var wallBorderColor='black';
var wallFillColor='black';

function initPompeiiMap(moreZoom=false,showHover=true,colorDensity=true,interactive=true,propertyIdToHighlight=0,propertyIdListToHighlight=[],zoomOnOneProperty) {
	// this just sets my access token
	var mapboxAccessToken = 'pk.eyJ1IjoibWFydGluZXphMTgiLCJhIjoiY2lxczduaG5wMDJyc2doamY0NW53M3NnaCJ9.TeA0JhIaoNKHUUJr2HyLHQ';
	var borderColor;
	var southWest = L.latLng(40.746, 14.48),
	northEast = L.latLng(40.754, 14.494),
	bounds = L.latLngBounds(southWest, northEast);
	
	var currentZoomLevel;
	var showInsulaMarkers;
	
	var regioViewZoomLevel=16;
	
	// The minimum zoom level to show insula view instead of property
	// view (smaller zoom level means more zoomed out)
	var insulaViewZoomLevel=17;
	
	if(moreZoom){
		currentZoomLevel=15;
	}
	else{
		currentZoomLevel=16;
	}
	
	var zoomLevelForIndividualProperty=19;
	var totalInsulaGraffitisDict=new Array();
	// The list of active insula markers.
	// Can be iterated through to remove all markers from the map(?)
	var insulaMarkersList=[];
	var regioMarkersList=[];
	
	// I see the clicked areas collection, but what about the rest of the items?
	// Are they just obscurely stored by Leaflet or GeoJSON?
	var clickedAreas = [];
	// A list filled with nested list of the full name, id, and short name of
	// each insula selected.
	var clickedInsula=[];
	
	// Holds the center latitudes and longitudes of all insula on the map.
	var insulaCentersDict=[];
	var insulaGroupIdsList=[];
	var insulaShortNamesDict=[];
	
	// Variables for all things regio:
	var regioCentersDict={};
	var regioNamesList=[];
	var graffitiInEachRegioDict={};
	
	// Syncs with mapbox
	var mapboxUrl = 'https://api.mapbox.com/styles/v1/martineza18/ciqsdxkit0000cpmd73lxz8o5/tiles/256/{z}/{x}/{y}?access_token=' + mapboxAccessToken;
	
	var grayscale = new L.tileLayer(mapboxUrl, {id: 'mapbox.light', attribution: 'Mapbox Light'});
	
	// Fires when the map is initialized
	pompeiiMap = new L.map('pompeiimap', {
		center: [40.750, 14.4884],
		zoom: currentZoomLevel,
		minZoom: currentZoomLevel,
		maxZoom:20,
		maxBounds: bounds,
	});
	
	var propertyLayer = L.geoJson(pompeiiPropertyData, { style: propertyStyle, onEachFeature: onEachPropertyFeature });
	//propertyLayer.addTo(pompeiiMap);
	
	var pompeiiWallsLayer = L.geoJson(pompeiiWallsData, {style: wallStyle, onEachFeature: onEachWallFeature});
	pompeiiWallsLayer.addTo(pompeiiMap);
	
	pompeiiMap.addLayer(grayscale);
	
	var pompeiiInsulaLayer = L.geoJson(pompeiiInsulaData, { style: propertyStyle, onEachFeature: onEachPropertyFeature });
	pompeiiInsulaLayer.addTo(pompeiiMap)
	
	if( interactive && colorDensity){ 
		// Insula Functions:
		makeInsulaCentersDict(); 
		makeTotalInsulaGraffitiDict();
		makeInsulaIdsListShortNamesList(); 

		// Regio Functions:
		makeRegioCentersDict(); 
		makeTotalRegioGraffitiDict();
		makeListOfRegioNames();

		dealWithLabelsAndSelection(); 
		pompeiiMap.addControl(new L.Control.Compass({autoActive: true, position: "bottomleft"})); 
	}
	 
	
	// A listener for zoom events.
	pompeiiMap.on('zoomend', function(e) {
		dealWithInsulaLevelPropertyView();
		dealWithLabelsAndSelection();
	});
	
	// Shows or hides insula labels depending on zoom levels and if the map is
	// interactive
	function dealWithLabelsAndSelection(){
		if(interactive){
			if(!insulaViewZoomThresholdReached() && !regioViewZoomThresholdReached()){
				updateBorderColors();
			}
			else if(regioViewZoomThresholdReached()){
				removeInsulaLabels();
				displayRegioLabels();
			}
			else if(insulaViewZoomThresholdReached()) {
				removeRegioLabels();
				displayInsulaLabels();
			}
			else {
				displayInsulaLabels();
			}
		}
	}
	
	// Centers the map around a single property
	function showCloseUpView(){
		if(propertyIdToHighlight){
			var newCenterCoordinates=[];
			propertyLayer.eachLayer(function(layer){
				if(layer.feature!=undefined){
					if(layer.feature.properties.Property_Id==propertyIdToHighlight){
						newCenterCoordinates=layer.getBounds().getCenter();
						propertyLayer.setView(newCenterCoordinates,zoomLevelForIndividualProperty);
					}
				}	
			});
		}
		else if(propertyIdListToHighlight.length==1){
			var newCenterCoordinates=[];
			var idOfListHighlight=propertyIdListToHighlight[0];
			propertyLayer.eachLayer(function(layer){
				if(layer.feature!=undefined){
					if(layer.feature.properties.Property_Id==idOfListHighlight){
						newCenterCoordinates=layer.getBounds().getCenter();
						pompeiiMap.setView(newCenterCoordinates,zoomLevelForIndividualProperty);
					}
				}
			});
		}
	}
	
	showCloseUpView();
	
	// Builds the global list of insula ids.
	function makeInsulaIdsListShortNamesList(){
		var currentInsulaId=183;
		pompeiiMap.eachLayer(function(layer){
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
	
	function makeListOfRegioNames(){
		var someName;
		pompeiiMap.eachLayer(function(layer){
			if(layer.feature!=undefined && layer.feature.properties.PRIMARY_DO){
				// console.log(layer.feature.properties.PRIMARY_DO);
				someName=layer.feature.properties.PRIMARY_DO.split(".")[0];
				if(regioNamesList.indexOf(someName)==-1){
					regioNamesList.push(someName);
				}
			}
		});
	}
	
	function makeTotalRegioGraffitiDict(){
		var currentNumberOfGraffiti;
		var currentRegioName;
		var regioNamesSoFar=[];
		pompeiiMap.eachLayer(function(layer){
			if(layer.feature!=undefined && layer.feature.properties.PRIMARY_DO){
				currentRegioName=layer.feature.properties.PRIMARY_DO.split(".")[0];
				currentNumberOfGraffiti=layer.feature.properties.Number_Of_Graffiti;
				if(regioNamesSoFar.indexOf(currentRegioName)==-1){
					regioNamesSoFar.push(currentRegioName);
					graffitiInEachRegioDict[currentRegioName]=currentNumberOfGraffiti;
				}
				else{
					graffitiInEachRegioDict[currentRegioName]+=currentNumberOfGraffiti;
				}
			}
		});
	}
	
	
	// Builds the dictionary of the graffiti in each insula
	// This works well as graffiti numbers should not change over the session.
	// Modifies the clojure wide variable once and only once at the beginning of
	// the program
	function makeTotalInsulaGraffitiDict(){
		totalInsulaGraffitisDict=new Array();
		pompeiiMap.eachLayer(function(layer){
			if(insulaViewZoomThresholdReached() && layer.feature!=undefined){
				graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
				currentInsulaNumber=layer.feature.properties.insula_id;
				if(totalInsulaGraffitisDict[currentInsulaNumber]!=undefined){
					totalInsulaGraffitisDict[currentInsulaNumber]+=graffitiInLayer;
				}
				else{
					totalInsulaGraffitisDict[currentInsulaNumber]=graffitiInLayer;
				}
			}
		});
	}
	
	// Meant to show the insula short name labels at the given x/y coordinates
	// (given as a normal list in Java array form)
	function showALabelOnMap(xYCoordinates,textToDisplay,textSize="small", markerType){
		var myIcon= L.divIcon({ 
		    iconSize: new L.Point(0, 0),
			iconSize:0,
		    html: textToDisplay
		});
		
		var myMarker;
		myMarker=new L.marker([xYCoordinates[1], xYCoordinates[0]], {icon: myIcon}).addTo(pompeiiMap);
		
		if(markerType=="insula") {
			insulaMarkersList.push(myMarker);
		} else if (markerType=="regio") {
			regioMarkersList.push(myMarker)
		}
	}
	
	// Removes each of the insula labels from the map.
	// Meant to be used for when the user zooms past the zoom threshold.
	// Stopped being used due to recommendations at the demo.
	function removeInsulaLabels(){
		var i=0;
		for(i;i<insulaMarkersList.length;i++){
			pompeiiMap.removeLayer(insulaMarkersList[i]);
		}
	}
	
	function removeRegioLabels(){
		var i=0;
		for(i;i<regioMarkersList.length;i++){
			pompeiiMap.removeLayer(regioMarkersList[i]);
		}
	}
	
	// Shows the short names of each insula in black
	// at the center ccoordinates.
	function displayInsulaLabels(){
		// console.log("11");
		var i;
		var insulaId;
		var insulaCenterCoordinates;
		var shortInsulaName;
		// Alerts are working console.log is not(why?)
		for(i=0;i<insulaGroupIdsList.length;i++){
			insulaId=insulaGroupIdsList[i];
			insulaCenterCoordinates=insulaCentersDict[insulaId];
			shortInsulaName=insulaShortNamesDict[insulaId];
			if(insulaCenterCoordinates!=null){
				showALabelOnMap(insulaCenterCoordinates,shortInsulaName, "small", "insula");
			}
		}
	}
	
	function displayRegioLabels(){
		var i;
		var regioCenterCoordinates;
		var regioName;
		for(i=0;i<regioNamesList.length;i++){ 
			regioName=regioNamesList[i];
			regioCenterCoordinates=regioCentersDict[regioName];
			if(regioCenterCoordinates!=null){
				showALabelOnMap(regioCenterCoordinates,regioName,"large", "regio");
			}
		}
	}
	
	function addMoreLatLng(oldList,newList){
		oldList=[oldList[0]+newList[0],oldList[1]+newList[1]];
		return oldList;
	}
	
	// This way will take more compiler time.
	// Trying to do it inside make center dict was too confusing/complex for me.
	function makeTotalPropsPerRegioDict(totalPropsSoFarDict){
		var regioList=[];
		var currentRegio;
		var currentCount;
		pompeiiMap.eachLayer(function(layer){
			if(layer.feature!=undefined && layer.feature.properties.PRIMARY_DO){
				currentRegio=layer.feature.properties.PRIMARY_DO.split(".")[0];
				
				if(regioList.indexOf(currentRegio)==-1){
					currentCount=1;
					totalPropsSoFarDict[currentRegio]=currentCount; 
					regioList.push(currentRegio);
				}
				else{
					currentCount=totalPropsSoFarDict[currentRegio];
					totalPropsSoFarDict[currentRegio]=currentCount+1;
				}
			}
		});
		return totalPropsSoFarDict;
	}
	
	// Works like the maker for insula centers dict but for Regio instead.
	// Needed to account for the fact that Regio were not ordered one to the
	// other in database.
	function makeRegioCentersDict(){
		var currentRegioName;
		var latLngList;
		var totalPropsSoFarDict={};
		var regioNamesSoFar=[];
		var currentRegioName;
		totalPropsSoFarDict=makeTotalPropsPerRegioDict(totalPropsSoFarDict);
		pompeiiMap.eachLayer(function(layer){
			if(layer.feature!=undefined && layer.feature.properties.PRIMARY_DO){
				currentRegioName=layer.feature.properties.PRIMARY_DO.split(".")[0];
				if(regioNamesSoFar.indexOf(currentRegioName)==-1){
					regioNamesSoFar.push(currentRegioName);
					if(layer.feature.geometry.coordinates!=undefined){
						regioCentersDict[currentRegioName]=findCenter(layer.feature.geometry.coordinates[0]);
					}
					else{
						regioCentersDict[currentRegioName]=0;
					}
				}
				else{
					if(layer.feature.geometry.coordinates!=undefined){
						regioCentersDict[currentRegioName]=addMoreLatLng(regioCentersDict[currentRegioName],[findCenter(layer.feature.geometry.coordinates[0])[0],findCenter(layer.feature.geometry.coordinates[0])[1]]);
					}
				}
			}
		});
		for(var key in regioCentersDict){
			var div=[regioCentersDict[key][0]/totalPropsSoFarDict[key],regioCentersDict[key][1]/totalPropsSoFarDict[key]];
			regioCentersDict[key]=div;
		}
	}
	
	// This function gets and returns a "dictionary" of the latitude and
	// longitude of each insula given its id(as index).
	// Used to find where to place the labels of each insula on the map, upon
	// iteration through this list.
	function makeInsulaCentersDict(){
		// console.log("12");
		var currentInsulaNumber;
		// Manually set as the first insula id for pompeii
		var oldInsulaNumber=183;
		var xSoFar=0;
		var ySoFar=0;
		var latLngList;
		var currentCoordinatesList;
		var propertiesSoFar=0;
		pompeiiMap.eachLayer(function(layer){
			propertiesSoFar+=1;
			if(layer.feature!=undefined && interactive){
				currentInsulaNumber=layer.feature.properties.insula_id;
				currentCoordinatesList=layer.feature.geometry.coordinates;
				if(currentInsulaNumber==oldInsulaNumber){
					currentInsulaNumber=layer.feature.properties.insula_id;
					// If a change in insula number has occurred, find the
					// center of the coordinates and add them to the dictionary
					var i=0;
					// This passes in the coordinates list for just one property
					// in the insula which are then added
					xAndYAddition=findCenter(currentCoordinatesList[0]);
					xSoFar+=xAndYAddition[0];
					ySoFar+=xAndYAddition[1];
				}
				else{
					// Add to dictionary:
					// Both divisions are required
					latLngList=[xSoFar/propertiesSoFar,ySoFar/propertiesSoFar];
					// This treats the currentInsulaNumber as a key to the
					// dictionary
					
					insulaCentersDict[oldInsulaNumber]=latLngList;
					// Reset old variables:
					xSoFar=0;
					ySoFar=0;
					propertiesSoFar=0;
					oldInsulaNumber=currentInsulaNumber;
					// This passes in the coordinates list for just one property
					// in the insula which are then added
					xAndYAddition=findCenter(currentCoordinatesList[0]);
					xSoFar+=xAndYAddition[0];
					ySoFar+=xAndYAddition[1];
				}
			}
		});
	}
	
	// Uses math to directly find and return the latitude and longitude of the
	// center of a list of coordinates.
	// Returns a list of the latitude, x and the longitude, y
	function findCenter(coordinatesList){
		var i=0;
		var x=0;
		var y=0
		var pointsSoFar=0;
		for(i;i<coordinatesList.length;i++){
			x+=coordinatesList[i][0];
			y+=coordinatesList[i][1];
			pointsSoFar+=1;
		}
		return [x/pointsSoFar,y/pointsSoFar];
	}
	
	
	// Responsible for showing the map view on the insula level.
	function dealWithInsulaLevelPropertyView(){
		pompeiiMap.eachLayer(function(layer){
			if(insulaViewZoomThresholdReached() && layer.feature!=undefined && !regioViewZoomThresholdReached()){
				currentInsulaNumber=layer.feature.properties.insula_id;
				numberOfGraffitiInGroup=totalInsulaGraffitisDict[currentInsulaNumber];
				newFillColor=getFillColor(numberOfGraffitiInGroup);
				layer.setStyle({fillColor:newFillColor});
				layer.setStyle({color: getFillColor(numberOfGraffitiInGroup)});
			}
			else if(regioViewZoomThresholdReached() && colorDensity && layer.feature!=undefined && layer.feature.properties.PRIMARY_DO){
				regioName=layer.feature.properties.PRIMARY_DO.split(".")[0];
				numberOfGraffitiInGroup=graffitiInEachRegioDict[regioName];
				newFillColor=getFillColor(numberOfGraffitiInGroup);
				layer.setStyle({fillColor:newFillColor});
				layer.setStyle({color: getFillColor(numberOfGraffitiInGroup)});
			} else if(layer.feature && !layer.feature.properties.PRIMARY_DO){
				layer.setStyle({fillColor:'pink'});
			}	
			// Resets properties when user zooms back in
			if (!insulaViewZoomThresholdReached() && colorDensity && layer.feature!=undefined){
				layer.setStyle({color: getBorderColorForCloseZoom(layer.feature)});
				graffitiInLayer=layer.feature.properties.Number_Of_Graffiti;
				layer.setStyle({fillColor: getFillColor(graffitiInLayer)});
				layer.setStyle({color: getFillColor(graffitiInLayer)});
			} else if( layer.feature && ! layer.feature.properties.PRIMARY_DO){
				layer.setStyle({fillColor:'pink'});
			}
		});
	}
	
	function regioViewZoomThresholdReached(){
		currentZoomLevel=pompeiiMap.getZoom();
		return (currentZoomLevel<=regioViewZoomLevel && colorDensity);
	}
	
	function insulaViewZoomThresholdReached(){
		currentZoomLevel=pompeiiMap.getZoom();
		return (currentZoomLevel<=insulaViewZoomLevel && colorDensity);
	}
	
	function isPropertySelected(feature) {
		return feature.properties.clicked == true || feature.properties.Property_Id==propertyIdToHighlight || propertyIdListToHighlight.indexOf(feature.properties.Property_Id)>=0;
	}
	
	function getBorderColorForCloseZoom(feature){
		if (isPropertySelected(feature)) {
			return 'black';
		}
		return 'white';
	}
	
	function updateBorderColors(){
		propertyLayer.eachLayer(function(layer){
			if(layer.feature!=undefined && layer.feature.properties.clicked ){
				borderColor=getBorderColorForCloseZoom(layer.feature);
			}
		});
	}
	
	// Sets the style of the portions of the map. Color is the outside borders.
	// There are different colors for
	// clicked or normal fragments. When clicked, items are stored in a
	// collection. These collections will have the color
	// contained inside of else.
	function propertyStyle(feature) {
		borderColor=getBorderColorForCloseZoom(feature);
		fillColor=getFillColorForFeature(feature);
		return { 
	    		fillColor:fillColor,
	        weight: 1,
	        opacity: 1,
	        color: borderColor,
	        fillOpacity: 0.7,
	    };
	}
	
	function wallStyle(feature) {
		return { 
	    		fillColor: wallFillColor,
	        weight: 1,
	        opacity: 1,
	        color: wallBorderColor,
	        fillOpacity: 0.7,
	    };
	}
	
	function getFillColorForFeature(feature){
		// If the property is selected and there is no colorDensity, make the
		// fill color be maroon(dark red).
		if(isPropertySelected(feature)){
			return SELECTED_COLOR;
		}
		// an orangey-yellow
		return DEFAULT_COLOR;
	}
	
	function getFillColor(numberOfGraffiti){
		if(colorDensity){
			return numberOfGraffiti <= 2   ? '#FFEDC0' :
			   numberOfGraffiti <= 5   ? '#FFEDA0' :
			   numberOfGraffiti <= 10  ? '#fed39a' :
			   numberOfGraffiti <= 20  ? '#fec880' :
			   numberOfGraffiti <= 30  ? '#FEB24C' :
			   numberOfGraffiti <= 40  ? '#fe9b1b' :
			   numberOfGraffiti <= 60  ? '#fda668' :
		       numberOfGraffiti <= 80  ? '#FD8D3C' :
			   numberOfGraffiti <= 100 ? '#fd7a1c' :
		       numberOfGraffiti <= 130 ? '#fc6c4f' :
			   numberOfGraffiti <= 160 ? '#FC4E2A' :
			   numberOfGraffiti <= 190 ? '#fb2d04' :
			   numberOfGraffiti <= 210 ? '#ea484b' :
			   numberOfGraffiti <= 240 ? '#E31A1C' :
			   numberOfGraffiti <= 270 ? '#b71518' :
			   numberOfGraffiti <= 300 ? '#cc0029' :
			   numberOfGraffiti <= 330 ? '#b30024' :
			   numberOfGraffiti <= 360 ? '99001f' :
			   numberOfGraffiti <= 390 ? '#80001a' :
			   numberOfGraffiti <= 420 ? '#660014' :
			   numberOfGraffiti <= 460 ? '#4d000f' :
			   numberOfGraffiti <= 500 ? '#33000a' :
										 '#000000';
		}
		
		// an orangey-yellow
		return DEFAULT_COLOR;
	}
	
	// Sets color for properties which the cursor is moving over.
	function highlightFeature(e) {
		if(interactive && !insulaViewZoomThresholdReached()){
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
	
	// Sorts items based on whether they have been clicked
	// or not. If they have been and are clicked again, sets to false and vice
	// versa.
	function showDetails(e) {
		if(interactive){
			if(!insulaViewZoomThresholdReached()){
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
			else if(! regioViewZoomThresholdReached()){
				checkForInsulaClick(e.target);
			}
		}
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
	
	// On click, sees if a new insula id # has been selected. If so, adds it to
	// the list of selected insula.
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
		// Only adds the new id if it is already in the list
		
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
		var html = "<strong>Selected Insula:</strong><ul>";
		var numberOfInsulaSelected=clickedInsula.length;
		for (var i=0; i<numberOfInsulaSelected; i++) {
			html += "<li>"+clickedInsula[i][0] + ", " +
					"<p>"+totalInsulaGraffitisDict[clickedInsula[i][1]]+" graffiti</p>"+ "</li>"
		}
		html += "</ul>";
		// Checks to avoid error for element is null.
		var elem = document.getElementById("selectionDiv");
		if(typeof elem !== 'undefined' && elem !== null) {
			document.getElementById("selectionDiv").innerHTML = html;
		}
	}
	
	function displayHighlightedRegio(){
		/*
		 * var html = "<table><tr><th>Selected Regio:</th></tr>"; var
		 * numberOfInsulaSelected=clickedInsula.length; for (var i=0; i<numberOfInsulaSelected;
		 * i++) { html += "<tr><td><li>"+clickedInsula[i][0] + ", " + "<p>"+totalInsulaGraffitisDict[clickedInsula[i][1]]+"
		 * graffiti</p>"+ "</li></td></tr>" } html += "</table"; //Checks
		 * to avoid error for element is null. var elem =
		 * document.getElementById("selectionDiv"); if(typeof elem !==
		 * 'undefined' && elem !== null) {
		 * document.getElementById("selectionDiv").innerHTML = html; }
		 */
	}
	
	
	// Try: just commenting first line. Then, the map still works but colors
	// remain unchanged when clicked twice.
	// Used to reset the color, size, etc of items to their default state (ie.
	// after being clicked twice)
	function resetHighlight(e) {
		if(interactive && !insulaViewZoomThresholdReached()){
			propertyLayer.resetStyle(e.target);
			info.update();
		}
	}

	// Calls the functions on their corresponding events for EVERY feature
	function onEachPropertyFeature(feature, layer) {
	    layer.on({
	        mouseover: highlightFeature,
	        mouseout: resetHighlight,
	        click: showDetails,
	    });
	}
	
	function onEachWallFeature(feature, layer) {
	    layer.on({
	        mouseover: highlightFeature
	    });
	}
	
	// dealWithInsulaLevelPropertyView();
	
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
		// TODO: Only do for the properties?
		info.update = function (props) {
			if(showHover && props && props.PRIMARY_DO){
				this._div.innerHTML = (props ? props.Property_Name + ", " + props.PRIMARY_DO
						: 'Hover over property to see name');
			}
		};
		info.addTo(pompeiiMap);
	}
	
	updateHoverText();
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
			pompeiiMap.eachLayer(function(layer){
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
	
	// Used to acquire all of the items clicked for search(red button "Click
	// here to search).
	// Does this by iterating through the list of clicked items and adding them
	// to uniqueClicked, then returning uniqueClicked.
	function getUniqueClicked() {
		var uniqueClicked = [];
		var listInSelectedInsula;
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
	
	// Collects the ids of the clicked item objects (the property id).
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
	
	// creates url to call for searching when the user clicks the search button.
	function searchForProperties() {
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
		// when you click on the map, it updates the selection info
		if(!insulaViewZoomThresholdReached()){
			var clickedAreasTable = getUniqueClicked();
			var html = "<strong>Selected Properties:</strong><ul>";
			var length = clickedAreasTable.length;
			for (var i=0; i<length; i++) {
				var property = clickedAreasTable[i];
				if (property.feature.properties.clicked && property.feature.properties.PRIMARY_DO) {
					html += "<li>" +property.feature.properties.Property_Name + ", " + 
							property.feature.properties.PRIMARY_DO + "<p>"+property.feature.properties.Number_Of_Graffiti+" graffiti</p>"+ "</li>=";
				}
			}
			html += "</ul>";
			// Checks to avoid error for element is null.
			var elem = document.getElementById("selectionDiv");
			  if(typeof elem !== 'undefined' && elem !== null) {
				  document.getElementById("selectionDiv").innerHTML = html;
			  }
		}
		else if( !regioViewZoomThresholdReached()){
			displayHighlightedInsula();
			var clickedAreasTable = getUniqueClicked();
		}	
	}
	
	// Handles the events
	var el = document.getElementById("search");
	if(el!=null){
		el.addEventListener("click", searchForProperties, false);
	}
	
	var el2 = document.getElementById("pompeiimap");
	if(el2!=null){
		el2.addEventListener("click", displayHighlightedRegions, false);
	}
	
}