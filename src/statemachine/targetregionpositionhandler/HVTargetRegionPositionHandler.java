package statemachine.targetregionpositionhandler;

import model.RegionGraph;
import statemachine.TargetRegionPositionHandlerStrategy;

public class HVTargetRegionPositionHandler extends
		TargetRegionPositionHandlerStrategy {

	public HVTargetRegionPositionHandler(RegionGraph graph) {
		super(graph);
	}

	@Override
	protected void scalePosition() {
		
//		graph.updateRegions();
//		
//		Point2D centroid = graph.centroid;
//		Rectangle2D bb = graph.boundingBox;
//		
//		double farX = Double.MIN_VALUE;
//		double farY = Double.MIN_VALUE;
//		
//		double fx = 1, fy= 1;
//		
//		for(Region r : graph.sitesT){
//			
//			double radX = Math.abs(centroid.getX() -r.pixelCoordinate.getX());
//			double radY = Math.abs(centroid.getY() -r.pixelCoordinate.getY());
//						
//			if(radX >= farX)
//			{
//				farX = radX;
//				fx = centroid.getX() + radX;
//			}
//			
//			if(radY >= farY){
//				farY = radY;
//				fy = centroid.getY() + radY;
//
//			}
//
//			
//		}
//		
////		double bbMaxX = bb.getMaxX();//centroid.getX()+bb.getWidth()/2; 
////		double bbMinX = bb.getMinX();//centroid.getX()-bb.getWidth()/2; 
////
////		double bbMaxY = bb.getMaxY();//centroid.getY()+bb.getHeight()/2; 
////		double bbMinY = bb.getMinY();//centroid.getY()+bb.getHeight()/2; 
//
//		
//		for(Region r : graph.sitesT){
//			
//			double rx = r.pixelCoordinate.getX();//r.pixelCoordinate.getX();
//			double ry = r.pixelCoordinate.getY();
//			
//			double nx=rx;
//			double ny=ry;
//			
////			if(rx > centroid.getX()){
////				nx = (rx*bbMaxX)/fx;
////			}
////			
////			else if(rx < centroid.getX()){
////				nx = (rx*bbMinX)/(fx-centroid.getX());
////			}
////			
////			if(ry > centroid.getY()){
////				
////				ny = (ry*bbMaxY)/fy;
////			}
////			
////			else if(ry < centroid.getY()){
////				ny = (ry*bbMinY)/(fy-centroid.getY());
////			}
//			
//			r.pixelCoordinate.setLocation(nx, ny);
//		}
//		
//		graph.updateRegions();
//		
//		
		
		
	}

}
