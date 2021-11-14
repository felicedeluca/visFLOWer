<html>
<head>
<title><%=request.getAttribute("title")%></title>
</head>
<style>
#graph-container {
	top: 0;
	bottom: 0;
	left: 0;
	right: 0;
	position: absolute;
	background-color: rgba(0, 0, 255, 0.04);
}
</style>

<canvas id="myCanvas">top:0;
  top;0;
  left:0;
  right:0;
  position:absolute;
   style="border:1px solid #d3d3d3;"
   </canvas>

<div id="graph-container"></div>
<script src="sigma.min.js"></script>
<script src="sigma.parsers.json.min.js"></script>
<script>
	sigma.utils.pkg('sigma.canvas.nodes');
	sigma.utils.pkg('sigma.canvas.edges');
	
	sigma.canvas.edges.t = function(edge, source, target, context, settings) {
	  var color = edge.color,
	      prefix = settings('prefix') || '',
	      edgeColor = settings('edgeColor'),
	      defaultNodeColor = settings('defaultNodeColor'),
	      defaultEdgeColor = settings('defaultEdgeColor');

	  if (!color)
	    switch (edgeColor) {
	      case 'source':
	        color = source.color || defaultNodeColor;
	        break;
	      case 'target':
	        color = target.color || defaultNodeColor;
	        break;
	      default:
	        color = defaultEdgeColor;
	        break;
	    }

	  context.strokeStyle = color;
	  context.lineWidth = edge[prefix + 'size'] || 1;
	  context.beginPath();
	  context.moveTo(
	    source[prefix + 'x'],
	    source[prefix + 'y']
	  );
	  context.lineTo(
	    source[prefix + 'x'],
	    target[prefix + 'y']
	  );
	  context.lineTo(
	    target[prefix + 'x'],
	    target[prefix + 'y']
	  );
	  context.stroke();
	};
	sigma.canvas.nodes.image = (function() {
		var _cache = {}, _loading = {}, _callbacks = {};

		// Return the renderer itself:
		var renderer = function(node, context, settings) {

			var args = arguments, prefix = settings('prefix') || '', size = node[prefix
					+ 'size'], color = node.color
					|| settings('defaultNodeColor'), url = node.url;

			if (_cache[url]) {
				context.save();

				// Draw the clipping disc:
				context.beginPath();
				context.arc(node[prefix + 'x'], node[prefix + 'y'], node[prefix
						+ 'size'], 0, Math.PI * 2, true);
				context.closePath();
				context.clip();

				// Draw the image
				context.drawImage(_cache[url], node[prefix + 'x'] - size,
						node[prefix + 'y'] - size, 2 * size, 2 * size);

				// Quit the "clipping mode":
				context.restore();

				// Draw the border:
				context.beginPath();
				context.arc(node[prefix + 'x'], node[prefix + 'y'], node[prefix
						+ 'size'], 0, Math.PI * 2, true);
				context.lineWidth = size / 9;
				context.strokeStyle = node.color
						|| settings('defaultNodeColor');
				context.stroke();
			} else {
				sigma.canvas.nodes.image.cache(url);
				sigma.canvas.nodes.def.apply(sigma.canvas.nodes, args);
			}
		 
		};

		// Let's add a public method to cache images, to make it possible to
		// preload images before the initial rendering:
		renderer.cache = function(url, callback) {
			if (callback)
				_callbacks[url] = callback;

			if (_loading[url])
				return;

			var img = new Image();

			img.onload = function() {
				_loading[url] = false;
				_cache[url] = img;

				if (_callbacks[url]) {
					_callbacks[url].call(this, img);
					delete _callbacks[url];
				}
			};

			_loading[url] = true;
			img.src = url;
		};

		return renderer;
	})();

	var data = <%=request.getAttribute("json")%>
	console.log(data)
	
	
	function addBoundingBox(graph){
		
		graph.nodes.push({
			id : 'Bounding Box',
			label : 'Centroid',
			type : 'def',
			url : null,
			x : data.regionSCentroid.x,
			y : 1000 - data.regionSCentroid.y,
			size : 0,//data.regionSBoundingBoxsize,
			color : '#000000'
		});
		
		graph.nodes.push({
			id : 'bb1',
			label : 'top left',
			type : 'def',
			url : null,
			x : data.regionSCentroid.x-data.regionSBoundingBoxsize/2,
			y : 1000 - (data.regionSCentroid.y-data.regionSBoundingBoxsize/2),
			size : 0,//data.regionSBoundingBoxsize,
			color : '#00FF00'
		});

		graph.nodes.push({
			id : 'bb2',
			label : 'top right',
			type : 'def',
			url : null,
			x : data.regionSCentroid.x+data.regionSBoundingBoxsize/2,
			y : 1000 - (data.regionSCentroid.y-data.regionSBoundingBoxsize/2),
			size : 0,//data.regionSBoundingBoxsize,
			color : '#00FF00'
		});
		
		graph.nodes.push({
			id : 'bb3',
			label : 'bb3',
			type : 'def',
			url : null,
			x : data.regionSCentroid.x+data.regionSBoundingBoxsize/2,
			y : 1000 - (data.regionSCentroid.y+data.regionSBoundingBoxsize/2),
			size : 0,//data.regionSBoundingBoxsize,
			color : '#00FF00'
		});

		
		graph.nodes.push({
			id : 'bb4',
			label : 'bb4',
			type : 'def',
			url : null,
			x : data.regionSCentroid.x-data.regionSBoundingBoxsize/2,
			y : 1000 - (data.regionSCentroid.y+data.regionSBoundingBoxsize/2),
			size : 0,//data.regionSBoundingBoxsize,
			color : '#00FF00'
		});
		
		graph.edges.push({
	        id: 'e1',
	        source: 'bb1',
	        target: 'bb2',
	      });
		graph.edges.push({
	        id: 'e2',
	        source: 'bb2',
	        target: 'bb3',
	      });
	    graph.edges.push({
	        id: 'e3',
	        source: 'bb3',
	        target: 'bb4',
	      });
	    graph.edges.push({
	        id: 'e4',
	        source: 'bb4',
	        target: 'bb1',
	      });
	  
	    return graph;
		
	}

	
	graph = addBoundingBox(data);


	s = new sigma({
		graph : graph,
		renderer : {
			container : document.getElementById('graph-container'),
			type : 'canvas'
		},
		settings : {
			//minNodeSize : 8
		}
	});
	
	

	s.refresh();
</script>

</body>
</html>