package algorithms;

import java.awt.geom.Point2D;

import model.Region;
import model.RegionGraph;


public class RadialExpandStrategy extends ExpandStrategy {


	
	public RadialExpandStrategy(RegionGraph graph) {
		super(graph);
		super.name = "Radial Expand Strategy";

	}

	@Override
	protected void expand(RegionGraph graph) {
		
		Point2D centroid = graph.centroid;
		
		double expansionDistance = Math.max(graph.regionT.getWidth()/2, graph.regionT.getHeight()/2);
		
		for(Region r : graph.sitesS){
			
			double x = r.pixelCoordinate.getX();
			double y = r.pixelCoordinate.getY();
			
			double angle = r.angleFromPoint(centroid);
			
				x += Math.cos(angle)*expansionDistance;
				y += Math.sin(angle)*expansionDistance;
			
			r.pixelCoordinate.setLocation(x, y);
			
		}
		
		
	}
	
}
