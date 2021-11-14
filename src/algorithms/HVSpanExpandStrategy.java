package algorithms;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import model.Region;
import model.RegionGraph;

public class HVSpanExpandStrategy extends ExpandStrategy {

	double epsilon = 1; //custom gap for all shapes


	private enum UpOrDown {
		UP,
		DOWN
	}

	private enum LeftOrRight {
		LEFT,
		RIGHT
	}

	public HVSpanExpandStrategy(RegionGraph graph) {
		super(graph);
		super.name = "HV Span Expand Strategy";
	}

	@Override
	protected void expand(RegionGraph graph) {

		graph.updateRegions();
		
		//TODO adjust rect
		Rectangle2D bb = new Rectangle2D.Double(0,0,0,0);
		
		startHorizontalScan(graph.sitesS, bb, LeftOrRight.RIGHT);
		startHorizontalScan(graph.sitesS, bb, LeftOrRight.LEFT);

		startVerticalScan(graph.sitesS, bb, UpOrDown.DOWN);
		startVerticalScan(graph.sitesS, bb, UpOrDown.UP);

	}

	private void startHorizontalScan(ArrayList<Region> sites, Rectangle2D bb, LeftOrRight leftOrRight){

		ArrayList<Region> xOrderedSitesS = sites;
		xOrderedSitesS.sort(new xCoordinateRegionComparator());

		if(leftOrRight == LeftOrRight.LEFT) Collections.reverse(xOrderedSitesS);

		Region startingRegion = null;

		for(Region r : xOrderedSitesS){

			if(leftOrRight == LeftOrRight.RIGHT){

				if(r.pixelCoordinate.getX() > bb.getCenterX()){
					startingRegion = r;
					break;
				}

			}else if(leftOrRight == LeftOrRight.LEFT){

				if(r.pixelCoordinate.getX() < bb.getCenterX()){
					startingRegion = r;
					break;
				}

			}

		}

		if(startingRegion != null) 
		{
			horizontalScan(xOrderedSitesS, startingRegion, bb.getCenterX(), bb.getWidth(), leftOrRight);
		}


	}
	
	private void startVerticalScan(ArrayList<Region> sites, Rectangle2D bb, UpOrDown upOrDown){


		ArrayList<Region> yOrderedSitesS = sites;
		yOrderedSitesS.sort(new yCoordinateRegionComparator());

		if(upOrDown == UpOrDown.DOWN) Collections.reverse(yOrderedSitesS);

		Region startingRegion = null;

		for(Region r : yOrderedSitesS){

			if(upOrDown == UpOrDown.UP){

				if(r.pixelCoordinate.getY() > bb.getCenterY()){
					startingRegion = r;
					break;
				}

			}
			else if(upOrDown == UpOrDown.DOWN){

				if(r.pixelCoordinate.getY() < bb.getCenterY()){
					startingRegion = r;
					break;
				}

			}

		}

		if(startingRegion == null) return;
		else
		{
			verticalScan(yOrderedSitesS, startingRegion, bb.getCenterY(), bb.getHeight(), upOrDown);
		}


	}


	private void horizontalScan(ArrayList<Region> xOrderedSitesS, Region startingRegion, double pXCenter, double pWidth, LeftOrRight leftOrRight){

		double defaultSiteSWidth = 2; //this is set to a default value in the step of Expansion

		//calculating max delta i.e. max value for the span of the regions

		int startingRegionIndex = xOrderedSitesS.indexOf(startingRegion);

		double firstRx = startingRegion.pixelCoordinate.getX();

		double dxr = Math.abs(firstRx-pXCenter);
		double kxr = (pWidth+defaultSiteSWidth)/2; 

		double maxDelta = (kxr-dxr)*epsilon;

		for(int i=startingRegionIndex; i<xOrderedSitesS.size(); i++){

			Region vm = xOrderedSitesS.get(i);
			double vmX = vm.pixelCoordinate.getX();

			if(firstRx!=vmX) break;

			dxr = Math.abs(firstRx-vmX);
			kxr = (defaultSiteSWidth+defaultSiteSWidth)/2; 

			double distance = (kxr-dxr)*epsilon;
		
			double currDelta = distance;

			if(currDelta>=maxDelta){
				maxDelta = currDelta;
			}
		}

		shiftSiteHorizontally(xOrderedSitesS, startingRegion, maxDelta, leftOrRight);

		if(xOrderedSitesS.size() > startingRegionIndex+1){

			horizontalScan(xOrderedSitesS, xOrderedSitesS.get(startingRegionIndex+1), startingRegion.pixelCoordinate.getX(), defaultSiteSWidth, leftOrRight);
		}


	}

	private void verticalScan(ArrayList<Region> yOrderedSitesS, Region startingRegion, double pYCenter, double pHeight, UpOrDown upOrDown){

		double defaultSiteSHeight = 2; //this is set to a default value in the step of Expansion
		
		//calculating max delta i.e. max value for the span of the regions

		int startingRegionIndex = yOrderedSitesS.indexOf(startingRegion);

		double firstRy = startingRegion.pixelCoordinate.getY();

		double dxr = Math.abs(firstRy-pYCenter);
		double kxr = (pHeight+defaultSiteSHeight)/2; 

		double maxDelta = (kxr-dxr)*epsilon;

		for(int i=startingRegionIndex; i<yOrderedSitesS.size(); i++){

			Region vm = yOrderedSitesS.get(i);
			double vmY = vm.pixelCoordinate.getY();

			if(firstRy!=vmY) break;

			dxr = Math.abs(firstRy-vmY);
			kxr = (defaultSiteSHeight+defaultSiteSHeight)/2; 

			double distance = (kxr-dxr)*epsilon;
			double currDelta = distance;
			
			if(currDelta>=maxDelta){
				maxDelta = currDelta;
			}

		}

		shiftSiteVertically(yOrderedSitesS, startingRegion, maxDelta, upOrDown);

		if(yOrderedSitesS.size() > startingRegionIndex+1){

			verticalScan(yOrderedSitesS, yOrderedSitesS.get(startingRegionIndex+1), startingRegion.pixelCoordinate.getY(), defaultSiteSHeight, upOrDown);
			
		}


	}
	
	private void shiftSiteHorizontally(ArrayList<Region> xOrderedSitesS, Region referenceRegion, double delta, LeftOrRight leftOrRight){

		double pXCenter = referenceRegion.pixelCoordinate.getX();
		
//		double refX = referenceRegion.pixelCoordinate.getX();
//		double refY = referenceRegion.pixelCoordinate.getY();

		for(int i=xOrderedSitesS.indexOf(referenceRegion); i<xOrderedSitesS.size(); i++){

			Region r = xOrderedSitesS.get(i);

			double rx = r.pixelCoordinate.getX();
			double ry = r.pixelCoordinate.getY();

			switch(leftOrRight){
			case LEFT:{
				if(rx< pXCenter) rx -= delta; 
			}
			break;
			case RIGHT:{
				if(rx> pXCenter) rx += delta;
			}
			break;
			}

			r.pixelCoordinate.setLocation(rx, ry);

		}


	}

	private void shiftSiteVertically(ArrayList<Region> yOrderedSitesS, Region referenceRegion, double delta, UpOrDown upOrDown){

		double pYCenter = referenceRegion.pixelCoordinate.getY();
		
//		double refX = referenceRegion.pixelCoordinate.getX();
//		double refY = referenceRegion.pixelCoordinate.getY();

		for(int i=yOrderedSitesS.indexOf(referenceRegion); i<yOrderedSitesS.size(); i++){

			Region r = yOrderedSitesS.get(i);

			double rx = r.pixelCoordinate.getX();
			double ry = r.pixelCoordinate.getY();

			switch(upOrDown){
			case DOWN:{
				if(ry< pYCenter) ry -= delta; 
			}
			break;
			case UP:{
				if(ry> pYCenter) ry += delta;
			}
			break;
			}

			r.pixelCoordinate.setLocation(rx, ry);

		}


	}

	class xCoordinateRegionComparator implements Comparator<Region> {

		@Override
		public int compare(Region r1, Region r2) {

			if (r1.pixelCoordinate.getX() > r2.pixelCoordinate.getX()) {
				return 1;
			} else if (r1.pixelCoordinate.getX() < r2.pixelCoordinate.getX()){
				return -1;
			}
			return 0;
		}
	}


	class yCoordinateRegionComparator implements Comparator<Region> {

		@Override
		public int compare(Region r1, Region r2) {

			if (r1.pixelCoordinate.getY() > r2.pixelCoordinate.getY()) {
				return 1;
			} else if (r1.pixelCoordinate.getY() < r2.pixelCoordinate.getY()){
				return -1;
			}
			return 0;
		}
	}

}
