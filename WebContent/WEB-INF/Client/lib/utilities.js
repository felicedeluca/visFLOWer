
function colorFromValueInWhiteBlueRange(segmentValue, graphConfigurator) {

	var maxColorRangeValue = 100;
	var minColorRangeValue = 1;

	var mainFlowSlope = 1.0
	* (maxColorRangeValue - minColorRangeValue)
	/ (graphConfigurator.maxClusterValue - graphConfigurator.minClusterValue);
	var value = minColorRangeValue + mainFlowSlope
	* (segmentValue - graphConfigurator.minClusterValue);

	value = Math.ceil(value / 10);

	var b = 255;
	var g = 255 - Math.floor((255 * value / 10));
	var r = 255 - Math.floor((255 * value / 10));
	
	
    return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);

//	return rgbToHex(r, g, b) {
//	    return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
//	}

//	var rgb = "'rgb(" + r + "," + g + "," + b + ");'";

//	return rgb;

}

function showMoreInfoAboutNode(e){


	$("#moreinfo").html("");
	
	var description = e.description;
	
	description.replace("\\r","<br />"); 
	description.replace("\\n","<br />"); 
	

	$("#moreinfo").append(description);

	$("#moreinfo").show();


}
