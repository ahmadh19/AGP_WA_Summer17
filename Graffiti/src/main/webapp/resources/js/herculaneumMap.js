var hercMap;

var SELECT_COLOR = '#800000';
var DEFAULT_COLOR = '#FEB24C';

function initHerculaneumMap(moreZoom=false,showHover=true,colorDensity=true,interactive=true,propertyIdToHighlight=0,propertyIdListToHighlight=[],zoomOnOneProperty) {
	//this just sets my access token
	var mapboxAccessToken = 'pk.eyJ1IjoibWFydGluZXphMTgiLCJhIjoiY2lxczduaG5wMDJyc2doamY0NW53M3NnaCJ9.TeA0JhIaoNKHUUJr2HyLHQ';
	var borderColor;
	var fillColor;
	var southWest = L.latLng(40.8040619, 14.343131),
	northEast = L.latLng(40.8082619, 14.351131),
	bounds = L.latLngBounds(southWest, northEast);
	
	var currentZoomLevel;
	var propertySelected;
	
	//The maximum zoom level to show insula view instead of property view(smaller zoom level means more zoomed out)
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
	var currentInsulaNumber;
	var graffitiInLayer;
	var numberOfGraffitiInGroup;
	
	var insulaMarkersList=[];
	var clickedInsula=[];
	
	//Holds the center latitudes and longitudes of all insula on the map. 
	var insulaCentersDict=[];
	var insulaGroupIdsList=[];
	var insulaShortNamesDict=[];
	
	//Fires when the map is initialized
	hercMap = new L.map('herculaneummap', {
		center: [40.8059619, 14.347131],
		zoom: currentZoomLevel,
		minZoom: currentZoomLevel-1,
		maxZoom:20,
		maxBounds: bounds,
		//Here is the +/- button for zoom
	});
	
	var herculaneumProperties = L.geoJson(herculaneumPropertyData, {
		style: style,
	    onEachFeature: onEachProperty
	}).addTo(hercMap);
	
	
	//Sinks with mapbox(?), why do we need access tokens security?
	var mapboxUrl = 'https://api.mapbox.com/styles/v1/martineza18/ciqsdxkit0000cpmd73lxz8o5/tiles/256/{z}/{x}/{y}?access_token=' + mapboxAccessToken;
	
	//I see the clicked areas collection, but what about the rest of the items? Are they just obscurely stored by Leaflet or GeoJSON?
	var clickedAreas = [];
	
	if(interactive){
		makeInsulaCentersDict();
		makeTotalInsulaGraffitiDict();
		makeInsulaIdsListShortNamesList();
		displayInsulaLabels();
		
		hercMap.addControl(new L.Control.Compass({autoActive: true, position: "bottomleft"}));
	}
	
	//A listener for zoom events. 
	hercMap.on('zoomend', function(e) {
		dealWithInsulaLevelView();
		dealWithInsulaLabelsAndSelectionOnZoom();
	});
	
	//Centers the map around a single property
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
	
	//Responsible for showing the map view on the insula level. 
	function dealWithInsulaLevelView(){
		//This must be reset
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
		hercMap.eachLayer(function(layer){
			if(zoomedOutThresholdReached() && layer.feature!=undefined){
				currentInsulaNumber=layer.feature.properties.insula_id;
				numberOfGraffitiInGroup=totalInsulaGraffitisDict[currentInsulaNumber];
				//For an unknown reason, forEachLayer loops through two times instead of one. 
				//We compensate by dividing number of graffiti by two(?). 
				newFillColor=getFillColor(numberOfGraffitiInGroup/2);
				layer.setStyle({fillColor:newFillColor});
				layer.setStyle({color: getFillColor(numberOfGraffitiInGroup)});
				
				borderColor=newFillColor;
			}
			
		});
	}
	
	//Returns a new array with the contents of the previous index absent
	//We must search for a string in the array because, again, indexOf does not work for nested lists. 
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
				//layer.feature.setStyle(color,borderColor);
			}
		});
	}
	
	//Shows or hides insula labels depending on zoom levels and if the map is interactive
	function dealWithInsulaLabelsAndSelectionOnZoom(){
		//console.log("5");
		if(interactive){
			if(!zoomedOutThresholdReached()){
				if(showInsulaMarkers){
					//removeInsulaLabels();
					showInsulaMarkers=false;
					//This shows selected properties from the insula when the map zooms in.
					updateBorderColors();
				}
			}
			else if(!showInsulaMarkers){
				//displayInsulaLabels();
				showInsulaMarkers=true;
			}
		}
	}
	
	//Builds the global list of insula ids. 
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
	
	//Builds the dictionary of the graffiti in each insula
	//This works well as graffiti numbers should not change over the session.
	//Modifies the clojure wide variable once and only once at the beginning of the program
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
	//This function gets and returns a "dictionary" of the latitude and longitude of each insula given its id(as index).
	//Used to find where to place the labels of each insula on the map, upon iteration through this list.
	function makeInsulaCentersDict(){
		var currentInsulaNumber;
		//Manually set as the first insula id
		var oldInsulaNumber=183;
		var xSoFar=0;
		var ySoFar=0;
		var latLngList;
		var currentCoordinatesList;
		var propertiesSoFar=0;
		//console.log("Into find cds loop:");
		
		hercMap.eachLayer(function(layer){
			propertiesSoFar+=1;
			if(layer.feature!=undefined && interactive){
				currentInsulaNumber=layer.feature.properties.insula_id;
				currentCoordinatesList=layer.feature.geometry.coordinates;
				//console.log("Here is current coordinates list for layer:");
				//console.log(currentCoordinatesList+" ");
				if(currentInsulaNumber==oldInsulaNumber){
					currentInsulaNumber=layer.feature.properties.insula_id;
					//If a change in insula number has occurred, find the center of the coordinates and add them to the dictionary
					var i=0;
					
					//This passes in the coordinates list for just one property in the insula which are then added
					xAndYAddition=findCenter(currentCoordinatesList[0]);
					//console.log("Here are xY additions");
					//console.log(xAndYAddition+":");
					xSoFar+=xAndYAddition[0];
					ySoFar+=xAndYAddition[1];
				}
				else{
					//Add to dictionary:
					//Both divisions are required
					latLngList=[xSoFar/propertiesSoFar,ySoFar/propertiesSoFar];
					//This treats the currentInsulaNumber as a key(dictionary form)
					
					insulaCentersDict[oldInsulaNumber]=latLngList;
					//Reset old variables:
					xSoFar=0;
					ySoFar=0;
					propertiesSoFar=0;
					oldInsulaNumber=currentInsulaNumber;
					//This passes in the coordinates list for just one property in the insula which are then added
					xAndYAddition=findCenter(currentCoordinatesList[0]);
					xSoFar+=xAndYAddition[0];
					ySoFar+=xAndYAddition[1];
				}
			}
		});
	}
	
	//Uses math to directly find and return the latitude and longitude of the center of a list of coordinates. 
	//Returns a list of the latitude, x and the longitude, y
	function findCenter(coordinatesList){
		coordinatesList=coordinatesList[0];
		//console.log("Coords list in find center:");
		//console.log(coordinatesList);
		////console.log("Here are coords passed to find center:");
		////console.log(coordinatesList+":");
		var i=0;
		var x=0;
		var y=0
		var pointsSoFar=0;
		for(i;i<coordinatesList.length;i++){
			x+=coordinatesList[i][0];
			y+=coordinatesList[i][1];
			pointsSoFar+=1;
		}
		//console.log("X in findCenter:");
		//console.log(x+":");
		//console.log("y in findCenter:");
		//console.log(y+":");
		//console.log("Points so far in find center:");
		//console.log(pointsSoFar);
		return [x/pointsSoFar,y/pointsSoFar];
	}
	
	
	//Marks all properties inside of selected insula as selected by
	//adding them to the clickedInsula list.
	function selectPropertiesInAllSelectedInsula(uniqueClicked){
		//console.log("22");
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
		//console.log("14");
		//console.log(xYCoordinates);
		//This was breaking bc/the new wall properties
		//Commented out for now, change to deal with them later
		var myIcon = L.divIcon({ 
		    //iconSize: new L.Point(0, 0), 
			iconSize:0,
		    html: textToDisplay
		});
		// you can set .my-div-icon styles in CSS
		var myMarker=new L.marker([xYCoordinates[1], xYCoordinates[0]], {icon: myIcon}).addTo(hercMap);
		insulaMarkersList.push(myMarker);
	}
	
	//Shows the short names of each insula in black
	//at the center coordinates. 
	function displayInsulaLabels(){
		////console.log("15");
		var i;
		var insulaId;
		var insulaCenterCoordinates;
		var shortInsulaName;
		//Alerts are working console.log is not(why?)
		for(i=0;i<insulaGroupIdsList.length;i++){
			insulaId=insulaGroupIdsList[i];
			insulaCenterCoordinates=insulaCentersDict[insulaId];
			//console.log(insulaCentersDict+"h");
			shortInsulaName=insulaShortNamesDict[insulaId];
			if(! isNaN(insulaCenterCoordinates[0]) && ! isNaN(insulaCenterCoordinates[1])){
				showALabelOnMap(insulaCenterCoordinates,shortInsulaName);
			}
		}
	}
	
	function zoomedOutThresholdReached(){
		currentZoomLevel=hercMap.getZoom();
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
		//propertySelected=false;
		
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
	
	//On click, sees if a new insula id # has been selected. If so, adds it to the list of 
	//selected insula. 
	function checkForInsulaClick(clickedProperty){
		//Clicked property is a layer
		//layer.feature.properties.insula_id
		
		//indexOf does not work for nested lists. Thus, we have no choice but to use it with strings. 
		var clickedInsulaAsString=""+clickedInsula;
		var clickedInsulaFullName=clickedProperty.feature.properties.full_insula_name;
		var clickedInsulaId=clickedProperty.feature.properties.insula_id;
		var clickedInsulaShortName=clickedProperty.feature.properties.short_insula_name;
		var targetInsulaString=""+[clickedInsulaFullName,clickedInsulaId,clickedInsulaShortName];
		var indexOfInsulaName=clickedInsulaAsString.indexOf(targetInsulaString);

		//Only adds the new id if it is already in the list
		if(indexOfInsulaName==-1){
			clickedInsula.push([clickedInsulaFullName,clickedInsulaId,clickedInsulaShortName]);
		}
		//Otherwise, removed the insula id from the list to deselect it
		else{
			clickedInsula=removeStringedListFromArray(clickedInsula,targetInsulaString);
		}
	}
	
	//Used on click for insula level view in place of display selected regions
	//In charge of the right information only, does not impact the actual map
	function displayHighlightedInsula(){
		//clickedInsula.push([clickedInsulaFullName,clickedInsulaId,clickedInsulaShortName]);
		var html = "<strong>Selected Insula:</strong><ul>";
		var numberOfInsulaSelected=clickedInsula.length;
		for (var i=0; i<numberOfInsulaSelected; i++) {
			html += "<li>"+clickedInsula[i][0] + ", " +
					"<p>"+totalInsulaGraffitisDict[clickedInsula[i][1]]+" graffiti</p>"+ "</li>"
		}
		html += "</ul>";
		//Checks to avoid error for element is null.
		var elem = document.getElementById("toSearch");
		if(typeof elem !== 'undefined' && elem !== null) {
			document.getElementById("toSearch").innerHTML = html;
		}
	}
	
	//Sets color for properties which the cursor is moving over. 
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
	
	
	//A central function. Sorts items based on whether they have been clicked or not. If they
	//have been and are clicked again, sets to false and vice versa. I am confused what pushToFront is
	//or how it interacts with the wider collection of items if there is one. 
	function showDetails(e) {
		
		if(interactive){
			if(!zoomedOutThresholdReached()){
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
			else{
				checkForInsulaClick(e.target);
			}
		}
	}
	
	
	//Try: just commenting first line. Then, the map still works but colors remain unchanged when clicked twice. 
	//Used to reset the color, size, etc of items to their default state(ie. after being clicked twice)
	function resetHighlight(e) {
		if(interactive && !zoomedOutThresholdReached()){
			herculaneumProperties.resetStyle(e.target);
			info.update();
		}
	}

	//Calls the functions on their corresponding events for EVERY feature(from tutorial)
	function onEachProperty(feature, layer) {
	    layer.on({
	        mouseover: highlightFeature,
	        mouseout: resetHighlight,
	        click: showDetails,
	    });
	   
	}
	
	//Putting this after the above appears to make it this start correctly.
	 if(initialZoomNotCalled==true){
		   //console.log("16");
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
				this._div.innerHTML = (props ? props.Property_Name /* + props.Property_Address */
						: 'Hover over property to see name');
			}
		};
	
		info.addTo(hercMap);
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
		uniqueClicked=selectPropertiesInAllSelectedInsula(uniqueClicked);
		return uniqueClicked;
	}

	//Collects the ids of the clicked item objects(the id property).
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
	
	//Displays the Selected Properties and their corresponding information in an HTML table formatted. 
	//Achieved by mixing html and javascript, accessing text properties of the regions(items). 
	function displayHighlightedRegions() {
		if(!zoomedOutThresholdReached()){
			var clickedAreasTable = getUniqueClicked();
			
			var html = "<strong>Selected Properties:</strong><ul>";
			var length = clickedAreasTable.length;
			for (var i=0; i<length; i++) {
				var property = clickedAreasTable[i];
				/*alert(property.feature.geometry.coordinates);*/
				if (property.feature.properties.clicked === true) {
					
					html += "<li>" +property.feature.properties.Property_Name + ", " + 
							"<p>"+property.feature.properties.Number_Of_Graffiti+" graffiti</p>"+ "</li>";
				}
			}
			html += "</ul>";
			//Checks to avoid error for element is null.
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
	
	
	
	




	