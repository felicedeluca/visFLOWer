var umbriaImg;

function setupEdges(){

	sigma.utils.pkg('sigma.canvas.edges');


	sigma.canvas.edges.boundarycircle = function(edge, source, target, context,
			settings) {

		var color = '#000000', prefix = settings('prefix') || '', edgeColor = '#000000';

		var startX = source[prefix + 'x'];
		var endX = target[prefix + 'x'];
		var ray = (endX - startX) / 2;

		if (!umbriaImg) {

			umbriaImg = new Image();
			umbriaImg.onload = function() {// 
				s.refresh();

			
			}
			
			
			if(selectedDataset==1){
			umbriaImg.src = 'img/maplayer.png';
			}else{
				
				umbriaImg.src = 'img/Piemonte-map.png';
				
			}
			
		}

		context.strokeStyle = color;
		context.lineWidth = 2;
		context.fillStyle = '#FFFFFF';
		// context.fillStyle = '#a9a9a9';
		context.beginPath();
		context.arc(source[prefix + 'x'] + ray, source[prefix + 'y'], ray, 0,
				2 * Math.PI, true);
		context.closePath();
		context.beginPath();
		context.closePath();
		context.stroke();
		context.fill();
		context.save();
		context.drawImage(umbriaImg, source[prefix + 'x'], source[prefix + 'y']
		- ray, ray * 2, ray * 2);
		context.restore();

	};

	sigma.canvas.edges.main = function(edge, source, target, context, settings) {

		
		
		
		if (!s){return;}
		
		var filterMode = document.getElementById("filterCheckbox").checked;

		
		var firstLeaderColor = colorFromValueInWhiteBlueRange(edge.singleValue, graphObj.graphConfigurator);
		var backboneLeaderColor = (filterMode && !source.grayed) ? 
				colorFromValueInWhiteBlueRange(edge.singleValue, graphObj.graphConfigurator)
				:
				colorFromValueInWhiteBlueRange(edge.flowValue, graphObj.graphConfigurator);
		
		var	prefix = settings('prefix') || '',
			backboneLeaderSize = edge[prefix + 'size'] || 1;
		
		
		var curve_1 = mapNode(edge.firstRoutePoint, settings);
		var curve_2 = mapNode(edge.secondRoutePoint, settings);
		var controlPoint = mapNode(edge.controlPoint, settings);
		
		
		var turnPoint = mapNode(edge.turnPoint, settings);
		

		var sourceX = source[prefix + 'x'];
		var sourceY = source[prefix + 'y'];

		var targetX = target[prefix + 'x'];
		var targetY = target[prefix + 'y'];

		//FirstLeader
		context.beginPath();
		context.save();
		context.strokeStyle = firstLeaderColor;
		context.lineWidth = 3;
//		context.moveTo(sourceX, sourceY);
//		context.lineTo(turnPoint.x, turnPoint.y);
		context.moveTo(curve_1.x, curve_1.y);
		context.quadraticCurveTo(controlPoint.x,controlPoint.y,curve_2.x, curve_2.y);
		context.stroke();
		
		
		context.beginPath();
		context.strokeStyle = backboneLeaderColor;
		context.lineWidth = backboneLeaderSize;
		context.moveTo(turnPoint.x, turnPoint.y);
		context.lineTo(targetX, targetY);
		context.stroke();

	};
	
//	sigma.canvas.edges.mainsmooth = function(edge, source, target, context, settings) {
//
//		
//		var filterMode = document.getElementById("filterCheckbox").checked;
//
//		var strokeColor = (filterMode && !source.grayed) ? 
//				colorFromValueInWhiteBlueRange(edge.singleValue, graphObj.graphConfigurator)
//				:
//				colorFromValueInWhiteBlueRange(edge.flowValue, graphObj.graphConfigurator);
//				
//			var	prefix = settings('prefix') || '';
//		
//		
//
//		var sourceX = source[prefix + 'x'];
//		var sourceY = source[prefix + 'y'];
//
//		var targetX = target[prefix + 'x'];
//		var targetY = target[prefix + 'y'];
//
//		context.beginPath();
//		context.strokeStyle = strokeColor;
//		context.lineWidth = edge[prefix + 'size'];
//		context.moveTo(sourceX, sourceY);
//		context.lineTo(targetX, targetY);
//		context.stroke();
//
//	};	

	sigma.canvas.edges.smooth = function(edge, source, target, context,
			settings) {

		var strokeColor = colorFromValueInWhiteBlueRange(edge.flowValue,
				graphObj.graphConfigurator), prefix = settings('prefix') || '';

		var sourceX = source[prefix + 'x'];
		var sourceY = source[prefix + 'y'];

		var targetX = target[prefix + 'x'];
		var targetY = target[prefix + 'y'];

		context.beginPath();
		context.strokeStyle = strokeColor;
		context.lineWidth = edge[prefix + 'size'];
		context.moveTo(sourceX, sourceY);
		context.lineTo(targetX, targetY);
		context.stroke();

	};
	
	sigma.canvas.edges.secondarysmooth = function(edge, source, target, context,
			settings) {

		var strokeColor = edge.color, prefix = settings('prefix') || '';

		var sourceX = source[prefix + 'x'];
		var sourceY = source[prefix + 'y'];

		var targetX = target[prefix + 'x'];
		var targetY = target[prefix + 'y'];

		context.save();
		context.beginPath();
		if(edge.dashed){
			context.setLineDash([1,2]);
		}
        context.strokeStyle = strokeColor;
		context.lineWidth = edge[prefix + 'size'];
		context.moveTo(sourceX, sourceY);
		context.lineTo(targetX, targetY);
		context.stroke();
		context.restore();

	};


	sigma.canvas.edges.whiteboundarycircle = function(edge, source, target, context,
			settings) {

		var strokeColor = '#000000',
		prefix = settings('prefix') || '';

		var startX = source[prefix + 'x'];
		var endX = target[prefix + 'x'];
		var ray = (endX - startX) / 2;

		context.strokeStyle = strokeColor;
		context.lineWidth = 2;
		context.fillStyle = '#FFFFFF';
		context.beginPath();
		context.arc(source[prefix + 'x'] + ray, source[prefix + 'y'], ray, 0,
				2 * Math.PI, true);
		context.closePath();
		context.stroke();
		context.fill();

	};


	sigma.canvas.edges.multipoint_orthogonal = function(edge, source, target, context, settings){

		if (!s){return;}
		
		var strokeColor = 'black',
		prefix = settings('prefix') || '',
		size = edge[prefix + 'size'] || 1;
		
		
		var turn_1 = mapNode(edge.turnPoints[0], settings);
		var turn_2 = mapNode(edge.turnPoints[1], settings);

		var controlPoint = mapNode(edge.controlPoint, settings);
		var outfallPoint = mapNode(edge.outfallPoint, settings);

		var sourceX = source[prefix + 'x'];
		var sourceY = source[prefix + 'y'];

		var targetX = target[prefix + 'x'];
		var targetY = target[prefix + 'y'];

		context.beginPath();
		context.save();
		context.strokeStyle = strokeColor;
		context.lineWidth = 1;
		context.moveTo(sourceX, sourceY);
		context.lineTo(turn_1.x, turn_1.y);
		context.quadraticCurveTo(controlPoint.x,controlPoint.y,turn_2.x, turn_2.y);
		context.lineTo(outfallPoint.x, outfallPoint.y);
		context.stroke();
		
		
		context.beginPath();
		context.strokeStyle = strokeColor;
		context.lineWidth = edge[prefix + 'size'] *2;
		context.moveTo(outfallPoint.x, outfallPoint.y);
		context.lineTo(targetX, targetY);
		context.stroke();


	}


}


function controlPoint(x1, y1, x2, y2) {
	return {
		x: (x1 + x2) / 2 + (y2 - y1) / 4,
		y: (y1 + y2) / 2 + (x1 - x2) / 4
	};
};