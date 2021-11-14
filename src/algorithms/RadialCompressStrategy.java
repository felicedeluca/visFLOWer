package algorithms;

import java.awt.geom.Point2D;

import model.Region;
import model.RegionGraph;

public class RadialCompressStrategy extends CompressStrategy {

	public RadialCompressStrategy(RegionGraph graph) {
		super(graph);
		super.name = "RadialCompressStrategy";

	}

	@Override
	protected void compress(RegionGraph graph) {
		
		Point2D centroid = graph.centroid;
		
		Region farest = graph.getFarestSRegion();
		Region closest = graph.getClosestSRegion();
		
		double maxRegionDistance = centroid.distance(farest.pixelCoordinate);
		double maxScreenDistance = centroid.distance(closest.pixelCoordinate)+1000;
		
		for(Region r : graph.sitesS){

			double distance = centroid.distance(r.pixelCoordinate);
		
			double nuovaDistance = distance*maxScreenDistance/maxRegionDistance;
			
			double expansionDistance = nuovaDistance-distance;
						
			double x = r.pixelCoordinate.getX();
			double y = r.pixelCoordinate.getY();

			double angle = r.angleFromPoint(centroid);

			x += Math.cos(angle)*expansionDistance;
			y += Math.sin(angle)*expansionDistance;

			r.pixelCoordinate.setLocation(x, y);

		}

	}

}
