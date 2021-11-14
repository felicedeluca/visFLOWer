var siteTColor = '#B266FF';

function setupNodes(){

sigma.utils.pkg('sigma.canvas.nodes');

//Flags
sigma.canvas.nodes.image = (function() {
  var _cache = {},
      _loading = {},
      _callbacks = {};


  // Return the renderer itself:
  var renderer = function(node, context, settings) {
    var args = arguments,
        prefix = settings('prefix') || '',
        size = node[prefix + 'size'],
        color = node.color || settings('defaultNodeColor'),
        url = node.url;
    
    var color;
    
    if(node.selected){
    	node.color = 'red';
    }
    else{
    	node.color = 'black';
    }
        
        
    if (_cache[url]) {
      context.save();

      // Draw the clipping disc:
      context.beginPath();
      context.arc(
        node[prefix + 'x'],
        node[prefix + 'y'],
        node[prefix + 'size'],
        0,
        Math.PI * 2,
        true
      );
      context.closePath();
      context.clip();

      // Draw the image
      context.drawImage(
        _cache[url],
        node[prefix + 'x'] - size,
        node[prefix + 'y'] - size,
        2 * size,
        2 * size
      );
      
      
      if(node.grayed == true){
      
      var imageData = context.getImageData(node[prefix + 'x'] - size, node[prefix + 'y'] - size,  2 * size,  2 * size);
        var data = imageData.data;

        for(var i = 0; i < data.length; i += 4) {
          var brightness = 0.34 * data[i] + 0.5 * data[i + 1] + 0.16 * data[i + 2];
          // red
          data[i] = brightness;
          // green
          data[i + 1] = brightness;
          // blue
          data[i + 2] = brightness;
        }

        // overwrite original image
        context.putImageData(imageData, node[prefix + 'x'] - size, node[prefix + 'y'] - size);
        
        }

      // Quit the "clipping mode":
      context.restore();

      // Draw the border:
      context.beginPath();
      context.arc(
        node[prefix + 'x'],
        node[prefix + 'y'],
        node[prefix + 'size'],
        0,
        Math.PI * 2,
        true
      );
      context.lineWidth = size / 5;
      context.strokeStyle = node.color || settings('defaultNodeColor');
      context.stroke();
    } else {
      sigma.canvas.nodes.image.cache(url);
      sigma.canvas.nodes.def.apply(
        sigma.canvas.nodes,
        args
      );
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


//Centroid
	sigma.canvas.nodes.centroid = function(node, context, settings) {

		
		//Following code is commented to avoid unnecesary drawing
		
		
//		centroid = node;
//		
//		var args = arguments, prefix = settings('prefix') || '', size = node[prefix
//				+ 'size'], color = node.color || settings('color'), url = node.url;
//
//		context.beginPath();
//		context.arc(node[prefix + 'x'], node[prefix + 'y'], 0, 0, Math.PI * 2,
//				true);
//		context.stroke();
	};
	
//Site T
	sigma.canvas.nodes.sitest = function(node, context, settings) {

		var args = arguments, prefix = settings('prefix') || '', size = node[prefix
				+ 'size'], color = node.color || settings('color'), url = node.url;
				
		var nodeAlpha;
		
		if(node.selected){
			nodeAlpha = 1;
		}else{
			nodeAlpha = 0.7;
		}

		context.beginPath();
		context.save();
		context.globalAlpha = nodeAlpha;
		context.arc(node[prefix + 'x'], node[prefix + 'y'], node['size'], 0,
				Math.PI * 2, true);
		context.fillStyle = siteTColor;
		context.fill();
		context.restore();
		context.save();
		context.fillStyle = "white";
		context.textBaseline = 'middle';
		context.font = "13px Calibri";
		context.textAlign = 'center';
		
		if(node.selected == true){
		
	  	context.strokeStyle = 'red';
	  	context.lineWidth = 1;
     	 context.stroke();
      }
		
		context.fillText(node.namecode, node[prefix + 'x'], node[prefix + 'y']);
		context.restore();
	};

//Routing Points
	sigma.canvas.nodes.invisible = function(node, context, settings) {

		var args = arguments, prefix = settings('prefix') || '', size = node[prefix
				+ 'size'], color = node.color || settings('color'), url = node.url;

		context.beginPath();
		context.arc(node[prefix + 'x'], node[prefix + 'y'], 0, 0, Math.PI * 2,
				true);
		context.stroke();
	};
}
