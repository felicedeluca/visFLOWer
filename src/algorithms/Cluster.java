package algorithms;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;

import model.Flow;
import model.OnePixelEdge;
import model.Region;

public class Cluster {

	public ArrayList<Region> regions;

	public Region leftMostRegion;
	public Region rightMostRegion;


	public Point2D routingPoint = null;
	public double routingAngle = 0.0;
	public Point2D endPoint = null;
	public ArrayList<Point2D> anchorRedPoints;
	
	public ArrayList<MainFlowSegment> mainFlowSegments;
	
	public ArrayList<OnePixelEdge> onePixelEdges;
	

	public Cluster(ArrayList<Region> regions){
		
		mainFlowSegments = new ArrayList<MainFlowSegment>();
		onePixelEdges = new ArrayList<OnePixelEdge>();

		this.regions = regions;

		this.regions.sort(new RegionAngleComparator());
		this.anchorRedPoints = new ArrayList<Point2D>();


		if(regions.size()>1){

			leftMostRegion = regions.get(0);
			rightMostRegion = regions.get(regions.size()-1);

		}

	}

	public double totalFlowValue(){

		double total = 0;
		for(Region r : regions)
			for(Flow f : r.outgoingFlows)
				total += f.value;
		return total;
	}

	private class RegionAngleComparator implements Comparator<Region> {

		@Override
		public int compare(Region r1, Region r2) {

			if (r1.angle > r2.angle) {
				return -1;
			} else if (r1.angle < r2.angle){
				return 1;
			}
			return 0;
		}
	}


}
