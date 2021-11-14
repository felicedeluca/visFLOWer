package algorithms;

import java.awt.geom.Point2D;

import model.Configurator;
import model.Region;
import model.RegionGraph;

public class RadialPositioning extends PositioningStrategy {

	Configurator sharedConfigurator;

	public RadialPositioning(RegionGraph graph) {
		super(graph);
		this.name = "Radial Positioning";
		sharedConfigurator = Configurator.getInstance();
	}

	@Override
	protected void place(RegionGraph graph) {

		graph.updateRegions();
		
		Point2D centroid = graph.centroid;
		centroid.setLocation(Math.round(centroid.getX()), Math.round(centroid.getY()));
		

		double maxDistance = Math.min(sharedConfigurator.screenSize.getHeight(),sharedConfigurator.screenSize.getWidth())*(Math.sqrt(2)/2);
		double minDistance = Math.max(graph.regionT.getWidth(), graph.regionT.getHeight())*(Math.sqrt(2)/2);

		Region farestS = graph.getFarestSRegion();

		Region closestS = graph.getClosestSRegion();

		double farestDist = farestS.pixelCoordinate.distance(centroid);
		double closestDist = closestS.pixelCoordinate.distance(centroid);
		

		if (farestDist != closestDist) {

			double slope = 1.0 * (maxDistance - minDistance) / (maxDistance - closestDist);

			for (Region r : graph.sitesS) {

				double angle = r.angleFromPoint(centroid);
				
				double regionDist = r.pixelCoordinate.distance(centroid);

				double ray = minDistance + slope * (regionDist - closestDist);
				
				if(ray>maxDistance){
					System.out.println("maggiore");
				}

				double x = Math.cos(angle) * ray;
				double y = Math.sin(angle) * ray;

				r.pixelCoordinate.setLocation(x, y);

			}

		}
		
		else{
			for (Region r : graph.sitesS) {

				double angle = r.angleFromPoint(centroid);

				double x = Math.cos(angle) * minDistance;
				double y = Math.sin(angle) * minDistance;

				r.pixelCoordinate.setLocation(x, y);

			}
		}
		
	}

}
