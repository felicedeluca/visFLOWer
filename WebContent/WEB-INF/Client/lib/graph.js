var graphObj;

var s;

var sigmaFilter;

function loadGraph(graph) {

	graphObj = graph;
	document.getElementById("filterCheckbox").checked = false;


	setupNodes();
	setupEdges();
	loadCustomPlugins();
	drawLegend(graph.graphConfigurator);

	var sigmaSettings = {
			minEdgeSize : 0,
			minNodeSize : 0,
			maxEdgeSize : graph.maxEdgeSize,
			maxNodeSize : 35,
			zoomingRatio : 1,
			doubleClickEnabled : false,
			verbose : true,
			enableCamera : true,
	};

	var urls = graph.graphConfigurator.urls, loaded = 0, img;

	// Then, wait for all images to be loaded before instanciating sigma:
	urls
	.forEach(function(url) {
		sigma.canvas.nodes.image
		.cache(
				url,
				function() {
					if (++loaded === urls.length) {
						// Instantiate sigma:
						s = new sigma({
							graph : graph,
							renderer : {
								container : document.getElementById('container'),
								type : 'canvas'
							},
							settings : sigmaSettings
						});

						s.refresh();
						//sigma.plugins.drawControlPoints(s);
						bindMouseActions();

					}
				});
	});


}