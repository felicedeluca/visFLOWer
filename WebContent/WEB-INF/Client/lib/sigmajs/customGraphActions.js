function bindMouseActions(){

	s.bind('clickNode', function(e) {


		// Clicked Node
		var selectedNode = e.data.node;

		// Deselect each
		// node before
		// select the new
		var nodes = s.graph.nodes();

		// change selected
		// Node status
		selectedNode.selected = !selectedNode.selected;

		for ( var nodeIndex in nodes) {

			nodes[nodeIndex].grayed = false;

			if (nodes[nodeIndex].id != selectedNode.id
					&& nodes[nodeIndex].type == selectedNode.type) {
				nodes[nodeIndex].selected = false;

				if (selectedNode.selected == true) {
					nodes[nodeIndex].grayed = true;
				}

			}

		}

		s.refresh();

		filter();

	});

	s.bind('overNode', function(e) {

		if(e.data.node.type == 'sitest' && $("#wikiCheckbox").is(':checked')){

			showMoreInfoAboutNode(e.data.node);
			}
		

	});
	s.bind('outNode', function(e) {
		$("#moreinfo").hide();
	});
	
	s.bind('overEdge outEdge clickEdge doubleClickEdge rightClickEdge', function(e) {
		  console.log(e.type, e.data.edge, e.data.captor);
		});
	
	s.bind('clickEdge', function(e) {


		// Clicked Node
		var selectedNode = e.data.edge;

//		// Deselect each
//		// node before
//		// select the new
//		var nodes = s.graph.nodes();
//
//		// change selected
//		// Node status
//		selectedNode.selected = !selectedNode.selected;
//
//		for ( var nodeIndex in nodes) {
//
//			nodes[nodeIndex].grayed = false;
//
//			if (nodes[nodeIndex].id != selectedNode.id
//					&& nodes[nodeIndex].type == selectedNode.type) {
//				nodes[nodeIndex].selected = false;
//
//				if (selectedNode.selected == true) {
//					nodes[nodeIndex].grayed = true;
//				}
//
//			}
//
//		}

		s.refresh();

		filter();

	});


}



function showDetails() {

	if (!s) {return;}

	var nodes = s.graph.nodes();

	var selectedTargetNodeId;
	var selectedSourceNodeId;

	for ( var nodeIndex in nodes) {
		var currNode = nodes[nodeIndex];
		if (currNode.selected) {
			if (currNode.type == 'sitest') {
				selectedTargetNodeId = currNode.id;
			} else if (currNode.type == 'image') {
				selectedSourceNodeId = currNode.id;
			}
		}
	}

	if (selectedSourceNodeId || selectedTargetNodeId) {

		var detailedInfoParam;
		var url;

		if (selectedSourceNodeId && selectedTargetNodeId) {
			url = servleturl+'/details/both';
			detailedInfoParam = {
					sitesS : [selectedSourceNodeId],
					sitesT : [selectedTargetNodeId]
			};
		}

		else if (selectedSourceNodeId) {

			url = servleturl+'/details/source';
			detailedInfoParam = {
					sitesS : [selectedSourceNodeId],
					sitesT : selectedTIDs

			};

		} else if (selectedTargetNodeId) {

			url = servleturl+'/details/target';
			detailedInfoParam = {
					sitesS : selectedSIDs,
					sitesT : [selectedTargetNodeId]
			};

		}
		
		detailedInfoParam.month = selectedPeriod;
		detailedInfoParam.dataset = selectedDataset;
		
		var jsonDetails = JSON.stringify(detailedInfoParam);


		$.ajax({
			type : 'POST',
			dataType : "json",
			url : url,
			data : jsonDetails,
			contentType : 'application/json; charset=utf-8',
			success : function(data) {
				d3chart(data);
				$('#dialog').dialog({
			        width:'auto',
				  	title: data.title

			});
			}

		});
	}

}

function filter() {

	var filterMode = document.getElementById("filterCheckbox").checked;

	if(!filterMode || !s ){ 
		sigmaFilter.undo().edgesBy(
				function(e) {
					return (1);
				}).apply();
		return;
	}

	var nodes = s.graph.nodes();

	var selectedTargetNodeId = '';
	var selectedSourceNodeId = '';

	for ( var nodeIndex in nodes) {
		var currNode = nodes[nodeIndex];
		if (currNode.selected) {
			if (currNode.type == 'sitest') {
				selectedTargetNodeId = currNode.id;
			} else if (currNode.type == 'image') {
				selectedSourceNodeId = currNode.id;
			}
		}
	}

	// Initialize the Filter API
	sigmaFilter = new sigma.plugins.filter(s);

	sigmaFilter.undo().edgesBy(
			function(e) {
				return ((e.id.indexOf(selectedSourceNodeId) >= 0) ||
						(e.type.indexOf('boundarycircle') >= 0));
			}).apply();

}