function loadCustomPlugins(){
	

  if (typeof sigma === 'undefined')
    throw 'sigma is not declared';

  sigma.utils.pkg('sigma.plugins');

  sigma.plugins.drawControlPoints = function(s, options) {
    var o = options || {},
    p = s.camera.prefix;

    s.graph.edges().forEach(function (edge, index, edgeArray) {
    var source = s.graph.nodes(edge.source), target = s.graph.nodes(edge.target),
        cp = controlPoint(source.x, source.y, target.x, target.y);

    cp = {
    id: edge.id + 'cp',
    size: 0.5,
    label: 'Control point for ' + edge.id,
    xOriginal: cp.x,
    yOriginal: cp.y,
    x:
        (
          (cp.x - s.camera.x) * Math.cos(s.camera.angle) +
          (cp.y - s.camera.y) * Math.sin(s.camera.angle)
        ) / s.camera.ratio,

    y:
        (
          (cp.y - s.camera.y) * Math.cos(s.camera.angle) -
          (cp.x - s.camera.x) * Math.sin(s.camera.angle)
        ) / s.camera.ratio
    };

        s.graph.addNode(cp);
    });

    s.refresh();
    
    
   
  }

  sigma.plugins.kill = function(s) { };


}

function mapNode(node, settings){
	
	if(!s){return;}

	var camera = s.cameras[0],
	renderer = s.renderers[0],
	bounds = sigma.utils.getBoundaries(s.graph, '', true),
	minX = bounds.minX,
	minY = bounds.minY,
	maxX = bounds.maxX,
	maxY = bounds.maxY,

	scale = Math.min(renderer.width / Math.max(maxX - minX, 1), 
			renderer.height / Math.max(maxY - minY, 1));

	var margin =
		( settings('rescaleIgnoreSize') ? 0 : (settings('maxNodeSize') || sizeMax) / scale ) +
		(settings('sideMargin') || 0);
	maxX += margin;
	minX -= margin;
	maxY += margin;
	minY -= margin;

	scale = Math.min(renderer.width / Math.max(maxX - minX, 1), 
			renderer.height / Math.max(maxY - minY, 1));

	var cos = Math.cos(camera.angle),
		sin = Math.sin(camera.angle),
		mappedNode = { x: (node.x - (maxX + minX) / 2) * scale,
		y: (node.y - (maxY + minY) / 2) * scale }

	mappedNode = {
		x:
			(
					(mappedNode.x - camera.x) * cos +
					(mappedNode.y - camera.y) * sin
			) / camera.ratio + (renderer.width / 2),

			y:
				(
						(mappedNode.y - camera.y) * cos -
						(mappedNode.x - camera.x) * sin
				) / camera.ratio + (renderer.height / 2)
	};

	return mappedNode;
	
}

function controlPoint(x1, y1, x2, y2) {
    return {
        x: (x1 + x2) / 2 + (y2 - y1) / 4,
        y: (y1 + y2) / 2 + (x1 - x2) / 4
      };
    };