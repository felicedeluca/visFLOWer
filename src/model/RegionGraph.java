package model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import algorithms.Cluster;


public class RegionGraph {
	
	public ArrayList<Region> sitesS;
	public ArrayList<Region> sitesT;
	public Rectangle2D boundingBox;
	public Point2D centroid;
	public Rectangle2D regionT;
	public Rectangle2D graphBoundingBox;

	public ArrayList<Flow> flows;
	public ArrayList<Cluster> clusters;
	public ArrayList<OnePixelEdge> onePixelEdges;
	
	public double regionTRay;
	
	public RegionGraph(ArrayList<Region> sitesS, ArrayList<Region> sitesT, ArrayList<ArrayList<Region>> clusters, ArrayList<Flow> flows){
		
		this.clusters = new ArrayList<Cluster>();
		for(ArrayList<Region> clusterRegions : clusters){
			addClusterWithRegions(clusterRegions);
		}
		
		this.sitesS = sitesS;
		this.sitesT = sitesT;
		this.flows = flows;
		
		calculateTargetsSize();
		
		ArrayList<Region> collection = new ArrayList<Region>();
		collection.addAll(sitesS);
		collection.addAll(sitesT);
				
		Mercator projAdj = new Mercator(collection);
		projAdj.start();
		
		onePixelEdges = new ArrayList<OnePixelEdge>();
		
		updateRegions();
		centerCentroid();
	}
	
	public void updateRegions(){
		
		calculateBoundingBox();
		updateRegionsAngle();
		
	}
	
	private void centerCentroid(){
		
		for(Region r : sitesS){
			double x = r.pixelCoordinate.getX();
			double y = r.pixelCoordinate.getY();
			r.pixelCoordinate.setLocation(x-Math.abs(this.centroid.getX()),y-Math.abs(this.centroid.getY()));
		}
		
		for(Region r : sitesT){
			double x = r.pixelCoordinate.getX();
			double y = r.pixelCoordinate.getY();
			r.pixelCoordinate.setLocation(x-Math.abs(this.centroid.getX()),y-Math.abs(this.centroid.getY()));
		}
		
	}
	
	public void addClusterWithRegions(ArrayList<Region> regions){
		
		Cluster currCluster = new Cluster(regions);
		clusters.add(currCluster);
	}
	
	public Region getFarestTRegion()
	{
		centroid = getCentroidOfRegions(sitesT);
		return getFarestRegion(this.centroid, sitesT);
	}
	
	public Region getFarestSRegion()
	{
//		åcentroid = getCentroidOfRegions(sitesS);
		return getFarestRegion(this.centroid, sitesS);
	}
	
	public Region getClosestSRegion()
	{
		Region closest = getClosestRegion(centroid, sitesS);
		return closest;
	}
	
	public void printClusters(){
		
		System.out.println("RegionGraph printClusters: ");
		for(int i=0; i<clusters.size(); i++){
			Cluster c = clusters.get(i);
			System.out.println("\n\nCluster: " + i);
			
			for(int j=0; j<c.regions.size(); j++){
				Region r = c.regions.get(j);
				System.out.println(r.name);
			}
			
			
		}
		
	}
	
	// Private Methods
	private void calculateBoundingBox(){
		
		centroid = getCentroidOfRegions(sitesT);
		
		//bounding box
		double halfSide = getMinDelta(centroid, sitesS);
		this.boundingBox = new Rectangle2D.Double(centroid.getX()-halfSide, centroid.getY()-halfSide, halfSide*2, halfSide*2);
		
		//region T
		updateRegionT();
		
		//graph bounding box
		updateGraphBoundingBox();
		
		 
	}
	
	private void updateGraphBoundingBox(){
		
		double maxX = Double.MIN_VALUE;
		double minX = Double.MAX_VALUE;
		
		double maxY = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		
		for(Region t : sitesT){
			
			double x = t.pixelCoordinate.getX();
			double y = t.pixelCoordinate.getY();
			
			if(x>maxX) maxX = x;
			if(x<minX) minX = x;
			if(y>maxY) maxY = y;
			if(y<minY) minY = y;
			
		}
		
		for(Region s : sitesS){
			
			double x = s.pixelCoordinate.getX();
			double y = s.pixelCoordinate.getY();
			
			if(x>maxX) maxX = x;
			if(x<minX) minX = x;
			if(y>maxY) maxY = y;
			if(y<minY) minY = y;
			
		}
		
		double width = Math.abs(maxX)+Math.abs(minX);
		double heigth = Math.abs(maxY)+Math.abs(minY);

		this.graphBoundingBox = new Rectangle2D.Double(minX, minY, width, heigth);
		
	}
	
	
	private void updateRegionT(){
		
double maxSide = 0;
		
		for(Region t : sitesT){
			
			double currDistX = Math.abs(centroid.getX()-t.pixelCoordinate.getX());
			double currDistY = Math.abs(centroid.getY()-t.pixelCoordinate.getY());
			
			double currMaxSide = Math.max(currDistX, currDistY);
			
			if(maxSide<currMaxSide) maxSide=currMaxSide;
			
		}
		
		this.regionT = new Rectangle2D.Double(centroid.getX()-maxSide, centroid.getY()-maxSide, maxSide*2, maxSide*2);
		
	}
	
	

	private Point2D getCentroidOfRegions(ArrayList<Region> sites){

		double sum_x = 0;
		double sum_y = 0;

		int i;

		for (i=0; i<sites.size(); i++){
			Region r = sites.get(i);
			sum_x += r.pixelCoordinate.getX();
			sum_y += r.pixelCoordinate.getY();
		}
		
		double centroid_x = Math.round(sum_x / i);
		double centroid_y =Math.round(sum_y / i);
		

		Point2D centroid = new Point2D.Double(centroid_x, centroid_y);
		
		return centroid;
	}

	private Region getClosestRegion(Point2D centroid, ArrayList<Region> sites){

		Region closestRegion = null;
		double minDelta = Double.MAX_VALUE;

		for (Region r : sites){

			double currDelta = centroid.distance(r.pixelCoordinate);// Math.sqrt(Math.pow(centroid.getX()-r.pixelCoordinate.getX(), 2) + Math.pow(centroid.getY()-r.pixelCoordinate.getY(), 2));

			if(currDelta<minDelta){
				minDelta = currDelta;
				closestRegion = r;
			}

		}

		return closestRegion;

	}
	
	
	
	private double getMinDelta(Point2D centroid, ArrayList<Region> sites){

		Region closest = getClosestRegion(centroid, sites);
		double minDelta = Math.max(Math.abs(centroid.getX() -closest.pixelCoordinate.getX()), Math.abs(centroid.getY()-closest.pixelCoordinate.getY()));
		return minDelta;
	}
	
	public Region getClosestTRegion()
	{
		Region closest = getClosestRegion(centroid, sitesT);
		return closest;
	}
	private Region getFarestRegion(Point2D centroid, ArrayList<Region> sites){
		
		Region farestRegion = null;
		double maxDelta = Double.MIN_VALUE;

		for (Region r : sites){

			double currDelta = centroid.distance(r.pixelCoordinate);
			if(currDelta>maxDelta){
				maxDelta = currDelta;
				farestRegion = r;
				
			}
		}
		return farestRegion;
	}
	
	private void updateRegionsAngle(){
		
		for (Region r : this.sitesS){
			r.angleFromPoint(centroid);
		}
		
		for (Region r : this.sitesT){
			r.angleFromPoint(centroid);
		}
		
	}
	
	
	//Graph Operations
	private void calculateTargetsSize(){
		
		for(Region currT : this.sitesT){
			
			int incomingValue = 0;
			
			for(Flow currF : this.flows)
				if(currF.target.equals(currT))
					incomingValue += currF.value;
				
			
			currT.valueSize = incomingValue;
			
		}
		
	}
}
