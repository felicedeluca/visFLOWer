package algorithms;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import model.Region;
import model.RegionGraph;

public class HVExpandStrategy extends ExpandStrategy{

	private enum UpOrDown {
		UP,
		DOWN
	}

	private enum LeftOrRight {
		LEFT,
		RIGHT
	}

	public HVExpandStrategy(RegionGraph graph) {
		super(graph);
		super.name = "HVExpandStrategy";
	}

	@Override
	protected void expand(RegionGraph graph) {
	
		graph.updateRegions();
		Rectangle2D bb = graph.boundingBox;
		
		
		shiftRegionsFromXWithDelta(graph.sitesS, bb.getMinX(), 100, LeftOrRight.LEFT);
		shiftRegionsFromXWithDelta(graph.sitesS, bb.getMaxX(), 100, LeftOrRight.RIGHT);

		shiftRegionsFromYWithDelta(graph.sitesS, bb.getMinY(), 100, UpOrDown.DOWN);
		shiftRegionsFromYWithDelta(graph.sitesS, bb.getMaxY(), 100, UpOrDown.UP);
		
//		Point2D centroid = getCentroidOfRegions(graph.sitesT);
//		Region closestRegion = getClosestSRegion(centroid, graph.sitesS);
//
//		if(closestRegion != null){
//			
//			double delta = Math.max(Math.abs(centroid.getX()-closestRegion.pixelCoordinate.getX()), Math.abs(centroid.getY()-closestRegion.pixelCoordinate.getY()));
//
//			double minX = centroid.getX()-delta;
//			double minY = centroid.getY()-delta;
//
//			double maxX = centroid.getX()+delta;
//			double maxY = centroid.getY()+delta;
//
//			Point2D minPoint = new Point2D.Double(minX, minY);
//			Point2D maxPoint = new Point2D.Double(maxX, maxY);
//
//			
//
//		}
//	
	}

	private void shiftRegionsFromXWithDelta(ArrayList<Region> sites, double x, double delta, LeftOrRight leftOrRight){

		for(Region r : sites){
			
			double rx = r.pixelCoordinate.getX();
			double ry = r.pixelCoordinate.getY();
			
			switch(leftOrRight){
			case LEFT:{
				if(rx< x){
					rx -= delta; 
				}
			}
			break;
			case RIGHT:{
				if(rx> x) rx += delta;
			}
			break;
			}
			
			r.pixelCoordinate.setLocation(rx, ry);

		}

	}

	private void shiftRegionsFromYWithDelta(ArrayList<Region> sites, double y, double delta, UpOrDown upOrDown){

		for(Region r : sites){
			
			double rx = r.pixelCoordinate.getX();
			double ry = r.pixelCoordinate.getY();

			switch(upOrDown){
			case UP:{
				if(ry> y) ry += delta; 
			}
			break;
			case DOWN:{
				if(ry< y) ry -= delta;
			}
			break;
			}
			
			r.pixelCoordinate.setLocation(rx, ry);

		}
	}



//	private Point2D getCentroidOfRegions(ArrayList<Region> sites){
//
//		double sum_x = 0;
//		double sum_y = 0;
//
//		int i;
//
//		for (i=0; i<sites.size(); i++){
//			Region r = sites.get(i);
//			sum_x += r.pixelCoordinate.getX();
//			sum_y += r.pixelCoordinate.getY();
//		}
//
//		double centroid_x = sum_x / i;
//		double centroid_y = sum_y / i;
//
//		Point2D centroid = new Point2D.Double(centroid_x, centroid_y);
//
//		return centroid;
//	}
//
//	private Region getClosestSRegion(Point2D centroid, ArrayList<Region> sites){
//
//		Region closestRegion = null;
//		double minDelta = Double.MAX_VALUE;
//
//		for (Region r : sites){
//			
//			double currDelta = Math.sqrt(Math.pow(centroid.getX()-r.pixelCoordinate.getX(), 2) + Math.pow(centroid.getY()-r.pixelCoordinate.getY(), 2));
//
//			if(currDelta<minDelta){
//				minDelta = currDelta;
//				closestRegion = r;
//			}
//
//		}
//
//		return closestRegion;
//
//	}

}
