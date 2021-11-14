

function drawLegend(graphConfiguration) {

	var PIXEL_RATIO = (function() {
		var ctx = document.getElementById("canvas2").getContext("2d"), dpr = window.devicePixelRatio || 1, bsr = ctx.webkitBackingStorePixelRatio
				|| ctx.mozBackingStorePixelRatio
				|| ctx.msBackingStorePixelRatio
				|| ctx.oBackingStorePixelRatio
				|| ctx.backingStorePixelRatio || 1;

		return dpr / bsr;
	})();

	createHiDPICanvas = function(w, h, ratio) {
		if (!ratio) {
			ratio = PIXEL_RATIO;
		}
				
		var can = document.getElementById("canvas2");
		can.width = w * ratio;
		can.height = h * ratio;
		can.style.width = w + "px";
		can.style.height = h + "px";
		can.getContext("2d").setTransform(ratio, 0, 0, ratio, 0, 0);
		return can;
	}

	//Cluster Value
	var minClusterValue = parseFloat(graphConfiguration.minClusterValue);
	var maxClusterValue = parseFloat(graphConfiguration.maxClusterValue);
	var avgClusterValue = (minClusterValue + maxClusterValue) / 2;
	
	//T Incoming Value
	var minSiteTIncomingValue = parseFloat(graphConfiguration.minSiteTIncomingValue);
	var maxSiteTIncomingValue = parseFloat(graphConfiguration.maxSiteTIncomingValue);
	var avgSiteTIncomingValue = (minSiteTIncomingValue + maxSiteTIncomingValue) / 2;
	
	//Positive Discrepancy Value
	var minPositiveDiscrepancyValue = parseFloat(graphConfiguration.minPositiveDiscrepancyValue);
	var maxPositiveDiscrepancyValue = parseFloat(graphConfiguration.maxPositiveDiscrepancyValue);
	var avgPositiveDiscrepancyValue = (minPositiveDiscrepancyValue+maxPositiveDiscrepancyValue)/2;	
	
	//Negative Discrepancy Value
	var minNegativeDiscrepancyValue = parseFloat(graphConfiguration.minNegativeDiscrepancyValue);
	var maxNegativeDiscrepancyValue = parseFloat(graphConfiguration.maxNegativeDiscrepancyValue);
	var avgNegativeDiscrepancyValue = (minNegativeDiscrepancyValue+maxNegativeDiscrepancyValue)/2;	
	

	var myCanvas = createHiDPICanvas(250, 200);

	var c = document.getElementById("canvas2");
	var context = c.getContext("2d");
	

	var leftMargin = 15;
	var imageGap = 30;
	var topMargin = 15;


	var blueBarFrame = {
		x : topMargin,
		y : leftMargin,
		width : 20,
		heigth : 108,
		src : 'img/BlueLegend.png',
		minText : parseFloat(minClusterValue),
		avgText : parseFloat(avgClusterValue).toFixed(0),
		maxText : parseFloat(maxClusterValue).toFixed(0)
	};

	var sitesTCircleLeftBound = blueBarFrame.x + blueBarFrame.width + 70
	var minCircleRay = 10;
	var maxCircleRay = 35;
	var avgCircleRay = (minCircleRay + maxCircleRay) / 2;

	var minCircle = {
		x : sitesTCircleLeftBound,
		y : topMargin,
		ray : minCircleRay,
		text : parseFloat(minSiteTIncomingValue).toFixed(0)
	};

	var avgCircle = {
		x : sitesTCircleLeftBound,
		y : minCircle.y + (minCircle.ray * 2) + imageGap,
		ray : avgCircleRay,
		text : parseFloat(avgSiteTIncomingValue).toFixed(0)
	};

	var maxCircle = {
		x : sitesTCircleLeftBound,
		y : avgCircle.y + (avgCircle.ray * 2) + imageGap,
		ray : maxCircleRay,
		text : parseFloat(maxSiteTIncomingValue).toFixed(0)
	};
	
	var greenBarFrame = {

		x : maxCircle.x + (maxCircle.ray),
		y : topMargin,
		width : blueBarFrame.width,
		heigth : blueBarFrame.heigth,
		src : 'img/GreenLegend.png',
		minText : parseFloat(minPositiveDiscrepancyValue).toFixed(0),
		avgText : parseFloat(avgPositiveDiscrepancyValue).toFixed(0),
		maxText : parseFloat(maxPositiveDiscrepancyValue).toFixed(0)

	};

	var redBarFrame = {

		x : greenBarFrame.x + (greenBarFrame.width) + 32,
		y : topMargin,
		width : blueBarFrame.width,
		heigth : blueBarFrame.heigth,
		src : 'img/RedLegend.png',
		minText : parseFloat(minNegativeDiscrepancyValue).toFixed(0),
		avgText : parseFloat(avgNegativeDiscrepancyValue).toFixed(0),
		maxText : parseFloat(maxNegativeDiscrepancyValue).toFixed(0)
	};
	
	
	drawCircleLegend(minCircle, context);
	drawCircleLegend(avgCircle, context);
	drawCircleLegend(maxCircle, context);
	drawBarLegend(blueBarFrame, context);
	drawBarLegend(greenBarFrame, context);
	drawBarLegend(redBarFrame, context);

}

function drawCircleLegend(circle, context){
	
	context.beginPath();
	context.save();
	context.globalAlpha = 0.7;
	context.arc(circle.x, circle.y, circle.ray, 0, Math.PI * 2, true);
	context.fillStyle = siteTColor;
	context.fill();
	context.fillStyle = "black";
	context.textBaseline = 'middle';
	context.font = "13px Calibri";
	context.textAlign = 'center';
	context.fillText(circle.text, circle.x, circle.y);
	context.restore();

}

function drawBarLegend(frame, context){
	
	var image = new Image();
	image.onload = function() {
		context.save();
		context.drawImage(image, frame.x, frame.y, frame.width, frame.heigth);
		context.fillStyle = "black";
		context.textBaseline = 'top';
		context.font = "13px Calibri";
		context.textAlign = 'left';
		context.fillText(frame.minText, frame.x + frame.width + 5, frame.y);
		context.textBaseline = 'middle';
		context.fillText(frame.avgText, frame.x + frame.width + 5, frame.y + (frame.heigth/2));
		context.textBaseline = 'bottom';
		context.fillText(frame.maxText,	frame.x + frame.width + 5, frame.y + frame.heigth);
		context.restore();
	};
	image.src = frame.src;
	
}