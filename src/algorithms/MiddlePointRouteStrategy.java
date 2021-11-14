package algorithms;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import model.Region;
import model.RegionGraph;

public class MiddlePointRouteStrategy extends RoutingStrategy {

	public MiddlePointRouteStrategy(RegionGraph graph) {
		super(graph);
		this.name = "Middle Point Route";

	}

	@Override
	protected void route() {
		
		ArrayList<Cluster> clusters = graph.clusters;
		
		Region farestTRegion = graph.getFarestTRegion();
		Region closestSRegion = graph.getClosestSRegion();
		
		double fTDist = graph.centroid.distance(farestTRegion.pixelCoordinate);
		double fSDist = graph.centroid.distance(closestSRegion.pixelCoordinate);
		
		double pointDist = (fSDist+fTDist)/2;
		
		graph.regionTRay = pointDist;

		for(Cluster c : clusters){
			double midAngle = calculateRoutingAngleForCluster(c);
			c.routingAngle = midAngle;
			
			for(Region r : c.regions){
				Point2D projPoint = projectRegionOnRoutingRay(r, midAngle, graph.centroid);
				r.routingPoint = projPoint;
			}
			
			double epx =graph.centroid.getX() + pointDist * Math.cos(midAngle);
			double epy =graph.centroid.getY() + pointDist * Math.sin(midAngle);
			c.endPoint = new Point2D.Double(epx, epy);
			
		}
	}

	private double calculateRoutingAngleForCluster(Cluster cluster){

		double minAngle = Double.MAX_VALUE;
		double maxAngle = Double.MIN_VALUE;

		for(Region r : cluster.regions){
			if(r.angle<minAngle) minAngle = r.angle;
			if(r.angle>maxAngle) maxAngle = r.angle;
		}

		double midAngle = (minAngle + maxAngle)/2 ;

		return midAngle;
	}
	
	private Point2D projectRegionOnRoutingRay(Region region, double angle, Point2D origin){
		
		double rangle = Math.atan2(region.pixelCoordinate.getY()-origin.getY(), region.pixelCoordinate.getX()-origin.getX());

		
		double alfa = Math.abs(angle-rangle);
	
		double i = origin.distance(region.pixelCoordinate);
		
		double cos = Math.cos(alfa);
		double dist = i*(cos+0.0000001);
		
		double x =origin.getX() + (dist * Math.cos(angle));
		double y =origin.getY() + (dist * Math.sin(angle));

		return new Point2D.Double(x, y);
		
	}

}