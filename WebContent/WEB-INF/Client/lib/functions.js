/**
 * 
 */

/* Global Variable to store selected Regions. */

var allDatasets = [];
var allSources = [];

var flowThreshold = -1;


var selectedDataset;


var selectedSIDs = [];
var selectedTIDs = [];
var clusters = [];
var sitess = [];
var page;
var periods = [];
var selectedPeriod = "1";

var selectedGeo = [];

//var servleturl = 'http://mozart.diei.unipg.it:8080/visflowerservletbeta';
var servleturl = 'http://localhost:8080/GeoFlowWeb';


function changePeriod(periodpar) {

	selectedPeriod = periodpar;
	graphify();

}

function load() {
	
	showDatasets();

}

function showDatasets(){
	
$('#leftsidebar').html('');
	
	var url = servleturl+"/alldatasets";
	
	$.ajax({
		type : 'GET',
		dataType: "json",
		url : url,
		contentType : 'application/json; charset=utf-8',
		success : function(data) {
			
			$('#rightsidebar').hide();
			$('#leftsidebar').hide();
			
			allDatasets = data;
			$('#container').html('<h1>VisFLOWer<h1>');
			$('#container').append('<h2>Visual Analysis of Touristic Flows<h2>'); 
			
			for (i = 0; i < data.length; i++) { 

				var inputFieldString = "<br /><input class='jQueryUIButton' id='"+ data[i].description+"_button' type='button' value='"+data[i].name+ "'  onclick='showSourcesForDataset("+data[i].identifier+");'/>"
				
				$('#container').append(inputFieldString);
			
				periods.push(data[i].identifier);
				
			}
			
			$(".jQueryUIButton").button();

		

		}
	});
	
}

function ajaxBindLoading(a){

	$(document).bind("ajaxSend", function(){
		$(a).hide();
	}).bind("ajaxComplete", function(){ 
		$(a).show();
	});
}

function updateSidebar(selectedDataset) {

	$('#leftsidebar').html('');
	$('#rightsidebar').html('');

	
	showPeriodButtons(selectedDataset);
	

	$('#rightsidebar').append(
	"<input id='details'   class='jQueryUIButton'   type='button' value='Details' onclick='showDetails();'/><br />");

	$('#rightsidebar').append(
	"<input id='filterCheckbox' type='checkbox' value='Filter' onclick='filter();'/>Filter Mode<br />");
	//$('#rightsidebar').append(
	//"<input id='wikiCheckbox' type='checkbox' value='Wiki'/>Show Wiki<br />");

	
	$(document).bind("ajaxSend", function(){
		$("#spinner").show();
	}).bind("ajaxComplete", function(){ 
		$("#spinner").hide();
	});
	
	$(".jQueryUIButton").button();



}

function showPeriodButtons(datasetIdentifier){
	
	$('#leftsidebar').html('');
	
	$('#leftsidebar').append("<label style='font-size: 13px;'> <h2>Select Period</h2></label>");

			for (i = 0; i < periods.length; i++) { 
				
				if(periods[i].identifier == selectedPeriod) {
					
					var inputFieldString = "<label style='width:100%; font-size: 13px;'> <h3>"+periods[i].description+"</h3></label>"
					$('#leftsidebar').append(inputFieldString);
					
				}
				
				else{
					var inputFieldString = "<input   class='jQueryUIButton'   id='"+ periods[i].description+"' type='button' value='"+periods[i].description+"' onclick='changePeriod("+periods[i].identifier+");' style='width:100%;'/><br />"
					$('#leftsidebar').append(inputFieldString);
					ajaxBindLoading("#"+ periods[i].description);
				}
				

			}
	
			$(".jQueryUIButton").button();

}

function setupPeriodsForDataset(datasetIdentifier){
	var url = servleturl+"/periodsfordataset";

	var json = JSON.stringify(datasetIdentifier);
	
	$.ajax({
		type : 'POST',
		dataType: "json",
		url : url,
		data:  json,
		contentType : 'application/json; charset=utf-8',
		success : function(data) {
	
	periods = [];

	periods = data;
	
	if(data.length>0){
		selectedPeriod = periods[0].identifier;
	}
	

		}
	});
	
	
}

function showSourcesForDataset(datasetIdentifier){

	var url = servleturl+"/datasetvalue";
	selectedDataset = datasetIdentifier;

	
	var json = JSON.stringify(datasetIdentifier);

	
	$.ajax({
		type : 'POST',
		dataType: "json",
		url : url,
		data:  json,
		contentType : 'application/json; charset=utf-8',
		success : function(data) {
			$('#container').html(' ');
			loadSourcesCheckboxSelection(data);
			setupPeriodsForDataset(datasetIdentifier)
		}
	});
	
}

function loadSourcesCheckboxSelection(data){
	
	$('#container').append('<h2>Select sources</h2>');

	loadMap(data);
	
	var fieldsetString = '';

//Uncomment following linws to checkbox selections
	
//	allSources = [];
//	selectedSIDs = [];
//	var fieldsetString = ' '+'<fieldset class="checkbox-grid"><legend style="font-size: 17px;""><h2>Select sources</h2></legend>'
//	+'<ul class="checkbox">';
//	
//	fieldsetString += '<br />';
//	fieldsetString += '<br />';
//	fieldsetString += '<br />';
//
//	
//	for (i = 0; i < data.length; i++) { 
//		fieldsetString = fieldsetString + '<li><input type="checkbox" id='+data[i].country_code+' value='+data[i].fullName+' checked onchange="toggleRegionSelection(this);" /><label for='+data[i].country_code+' style="font-size: 13px;">'+data[i].fullName+'</label></li>';
//		allSources.push(data[i]);
//		selectedSIDs.push(data[i].country_code);
//	}
//	
//	
//	fieldsetString = fieldsetString + '</ul>';
//	
//	fieldsetString = fieldsetString + "<br /><input class='jQueryUIButton'  id='selectAllSources' type='button' value='Select all' onclick='selectAllSources();'/>";
//	fieldsetString = fieldsetString + "<input  class='jQueryUIButton'    id='deselectAllSources' type='button' value='Deselect all' onclick='deselectAllSources();'/><br />";
//	fieldsetString += '<br />';
//	fieldsetString = '';

	fieldsetString += "<label style='font-size: 15px;'><h3>Flow filter: </h3></label>";
	fieldsetString = fieldsetString + ' <input class="jQueryUICheckbox" id="thresholdAutoInput" name="thresholdAutoInput" type="checkbox" value="html" checked="checked"  onchange="thresholdAutoInputChanged();"/><label for="thresholdAutoInput" style="font-size: 13px;">Auto Flow Filter</label><br /><br />';

	fieldsetString = fieldsetString + '<input class="jQueryUISlider" id="thresholdInput"  type="range" name="thresholdInput" min="0" max="100" onchange="updateThreshold(this.value);">';                                                      
	fieldsetString = fieldsetString + '<input type="text" id="thresholdTextInput" style=" border: none; background: transparent; font-size: 13px;" value="">';

//	fieldsetString += '<br />';

	fieldsetString = fieldsetString + '<input  class="jQueryUIButton"  style="font-size: 13px; float:right;  id="next" type="button" value="Next" onclick="graphify();"/><br />';
//	fieldsetString = fieldsetString + '</fieldset>';
	$('#container').append(fieldsetString);
	
	$(".jQueryUIButton").button();
	$(".jQueryUISlider").slider();
	


}

function toggleRegionSelection(checkbox) {
    var checked = $(checkbox).prop('checked');
    if(checked){
    	if(selectedSIDs.indexOf(checkbox.id)<0){
    		selectedSIDs.push(checkbox.id);
    	}
    }
    else{
    	if(selectedSIDs.indexOf(checkbox.id)>=0){
    		selectedSIDs.splice(selectedSIDs.indexOf(checkbox.id),1);
    	}
    }
    
   
}

function thresholdAutoInputChanged(){
	var val;
	
	if(document.getElementById('thresholdAutoInput').checked){
		val = -1;
	}else{
		val = document.getElementById('thresholdInput').value;
	}
	
	updateThreshold(val)
}

function updateThreshold(val){
	
	
		flowThreshold = val

	if(val==-1){
		document.getElementById('thresholdTextInput').value="";
		document.getElementById('thresholdAutoInput').checked = true;
	}else{
		document.getElementById('thresholdTextInput').value = val;
		document.getElementById('thresholdAutoInput').checked = false;
	}
	 
}

function selectAllSources(){
	
	selectedSIDs = [];

	var cboxes=document.getElementsByClassName("checkbox-grid").getElementsByTagName("input");
	
	for (j=0; j<cboxes.length; j++) {
	cboxes[j].checked=true;
	selectedSIDs.push(data[i].id);

	}
	
}

function deselectAllSources(){
	
	selectedSIDs = [];
	
}


function graphify() {


	var url = servleturl+"/visflower/";

	var dataString = '';
	var width = $('#container').width();
	var height = $('#container').height();
	var graph = {

			"sitess" : selectedSIDs,
			"sitest" : [],
			"screenWidth" : width,
			"screenHeight" : height,
			"period" : selectedPeriod,
			"dataset" : selectedDataset,
			"flowThreshold" : flowThreshold
	};

	var jsongraph = JSON.stringify(graph);
	$.ajax({
		type : 'POST',
		dataType: "json",
		url : url,
		data:  jsongraph,
		contentType : 'application/json; charset=utf-8',
		success : function(data) {
			$('#rightsidebar').show();
			$('#leftsidebar').show();
			$('#container').html(' ');
			updateSidebar(selectedDataset);
			loadGraph(data);


		}

	});

}


function loadMap(data) {
	
	var iDiv = document.createElement('div');
	iDiv.id = 'dataMapDiv';
	iDiv.className = 'dataMapDiv';
	document.getElementById('container').appendChild(iDiv);
	
	selectedSIDs = [];
	
	var unavailableColor = 'gray';
	var notSelectedColor = 'green';
	var selectedColor = 'blue';
	
	var obj  = data.map(function(a) {return a.country_code_ISO3;});
	
	var availableCountries = obj.reduce(function(o, v, i) {
		  o[v] = selectedColor;
		  selectedSIDs.push(v);
		  return o;
		}, {});
	
	var map = new Datamap({
		element : iDiv,
		responsive : true,
		scope : 'world',
		
		fills : {
			defaultFill : unavailableColor,
			data : availableCountries
		},
		geographyConfig : {
			popupTemplate : function(geo, data) {
				return [ '<div class="hoverinfo"><strong>',
						geo.properties.name, '</strong></div>' ].join('');
			}
		},
		done : function(datamap) {
	
			datamap.updateChoropleth(availableCountries);
			
			datamap.svg.selectAll('.datamaps-subunit').on('click',
					function(geography) {

				if(availableCountries[geography.id]){
						
							var index = selectedSIDs.indexOf(geography.id);
							if (index < 0) {
								availableCountries[geography.id] = selectedColor;
								selectedSIDs.push(geography.id);
							} else {
								availableCountries[geography.id] = notSelectedColor;
								selectedSIDs.splice(index, 1);
							}
							
							datamap.updateChoropleth(availableCountries);

						}
					});
		}
	});

}