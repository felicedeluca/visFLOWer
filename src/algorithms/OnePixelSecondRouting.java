package algorithms;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;

import model.Configurator;
import model.Flow;
import model.OnePixelEdge;
import model.Region;
import model.RegionGraph;
import utilities.PointsRotator;

public class OnePixelSecondRouting extends SecondRouteStrategy {

	private double flowFilter;

	public OnePixelSecondRouting(RegionGraph graph) {
		super(graph);

		this.name = "One Pixel Second Routng";
	}

	@Override
	protected void route() {
		for (Cluster c : graph.clusters) {

			double theta = c.routingAngle;
			Point2D origin = graph.centroid;
			
			Configurator sharedConfigurator = Configurator.getInstance();
			flowFilter = sharedConfigurator.filterflow;

			rotateRegions(c, -theta, origin);
			defineRedPoints(c);
			routeCluster(c);
			routeRedArcOnSites(c);
			splitMainFlow(c);
			rotateRegions(c, theta, origin);

		}
	}

	private void rotateRegions(Cluster cluster, double theta, Point2D origin) {
		
		PointsRotator.rotatePoint(cluster.endPoint, theta, origin);
		
		for (Region t : graph.sitesT){
			
			PointsRotator.rotatePoint(t.pixelCoordinate, theta, origin);
		}
		
		for (OnePixelEdge one : cluster.onePixelEdges){
			PointsRotator.rotatePoint(one.controlPoint, theta, origin);
			PointsRotator.rotatePoint(one.outfallPoint, theta, origin);
			PointsRotator.rotatePoint(one.targetPoint, theta, origin);
			
			for(Point2D point : one.turnPoints){
				PointsRotator.rotatePoint(point, theta, origin);
			}
		}
		
		for(MainFlowSegment mfs : cluster.mainFlowSegments){
			PointsRotator.rotatePoint(mfs.controlPoint, theta, origin);
			PointsRotator.rotatePoint(mfs.firstRoutePoint, theta, origin);
			PointsRotator.rotatePoint(mfs.secondRoutePoint, theta, origin);

		}

		for (Region r : cluster.regions) {
			
			PointsRotator.rotatePoint(r.pixelCoordinate, theta, origin);
			PointsRotator.rotatePoint(r.routingPoint, theta, origin);

			for(Flow f : graph.flows){

				if(f.source.equals(r)){

					if(f.startPoint != null && f.turnPoint != null){
												
						PointsRotator.rotatePoint(f.turnPoint, theta, origin);
						//PointsRotator.rotatePoint(f.startPoint, theta, origin);
						

					}

				}
			}

		}


	}

	private void defineRedPoints(Cluster c) {

		int numSites = 0;
		int regionsNum = c.regions.size();

		//Check how many one pixel edges are in the cluster i.e. how many flows have an Abs diff>flowFilter
		for(Region r : c.regions){
			for(Flow f : graph.flows){
				//TODO change here for filter Flow
				if(f.source.equals(r) && Math.abs(f.diff)>flowFilter){
					numSites ++;
					break;
				}
			}
		}
		
		//Setup the outfall points for the onepixel edges. Here the points have a position that depends on numSites
		double initialGap = 0;
		double gap = 0;
		double width = regionsNum*2+1; //width of the main flow

		if (numSites>1){
		gap = (width-4)/(numSites-1);
		initialGap = 1;
		}else{
			gap = (width-1)/(2);
			initialGap = gap;
		}
		
		ArrayList<Point2D> anchorPoints = new ArrayList<Point2D>();

		Point2D routingPoint = c.endPoint;
		c.endPoint.setLocation(c.endPoint.getX(), 0);
		double routingPX = routingPoint.getX();
		double routingPY = routingPoint.getY() - (width/2);

		for(int i=0; i<numSites; i++){

			double currY = routingPY + initialGap + (gap*i);
			Point2D currRedPoint = new Point2D.Double(routingPX, currY);
			anchorPoints.add(currRedPoint);

		}

		c.anchorRedPoints = anchorPoints;
	}

	private void routeRedArcOnSites(Cluster c) {

		//All Clusters are projected on x axis so can be easily routed

		for(Region r : c.regions)
			for (Flow f : graph.flows)
				//TODO change here to filter diff
				if(f.source.equals(r) && Math.abs(f.diff)>flowFilter)
					if(f.startPoint != null)
						f.turnPoint = new Point2D.Double(f.target.pixelCoordinate.getX(), f.startPoint.getY());

	}

	public void routeCluster(Cluster c){

		ArrayList<Region> flowingRegions = new ArrayList<Region>();

		//Setup regions with a flow higher than flow filter
		for(Region r : c.regions){
			for(Flow f : graph.flows){
				//TODO change here to filter diff
				if(f.source.equals(r) && Math.abs(f.diff)>flowFilter){
					flowingRegions.add(r);
					break;
				}
			}
		}

		flowingRegions.sort(new radialOrderComparator());

		
		ArrayList<Point2D> redPoints = c.anchorRedPoints;
		redPoints.sort(new redPointComparator());

		
		for(int i=0; i<flowingRegions.size(); i++){

			Region r = flowingRegions.get(i);
			
			Point2D anchorPoint = redPoints.get(i);
			Point2D controlPoint =  new Point2D.Double(r.pixelCoordinate.getX(), anchorPoint.getY());
			Point2D targetPoint = new Point2D.Double(anchorPoint.getX()-10, anchorPoint.getY()*2);

			OnePixelEdge rOPE = new OnePixelEdge(r,controlPoint,anchorPoint, targetPoint);
			
			c.onePixelEdges.add(rOPE);
			
			//TODO remove next line when reengineering
			graph.onePixelEdges.add(rOPE);

			for(Flow f : graph.flows){
				//TODO change here to filter diff
				if(f.source.equals(r) && Math.abs(f.diff)>flowFilter){
					f.startPoint = rOPE.targetPoint;
				}
			}


		}


	}

	private void splitMainFlow(Cluster c){
		
		ArrayList<Region> regions = c.regions;
		regions.sort(new RegionXComparator());
		
		double mainFlowWidth = c.regions.size()*2+1;
		
		Point2D endPoint = c.endPoint;

		
		double currFlowValue = 0;

		for (int i=0; i<regions.size(); i++){
			
			Region r = regions.get(i);
			currFlowValue += r.outgoingValue(0); // filter null
			
			
			MainFlowSegment currSegment = new MainFlowSegment(r, r.routingPoint, endPoint, currFlowValue, r.outgoingValue(0));
			currSegment.zIndex = i;
			
			double yDeltaCP = 0;
			double firstYDeltaTurn = 0;
			
			if (r.pixelCoordinate.getY()-r.routingPoint.getY() >0){
				yDeltaCP = mainFlowWidth/2+1;
				firstYDeltaTurn = 5;
			}
			else if (r.pixelCoordinate.getY()-r.routingPoint.getY()<0){
				yDeltaCP = -(mainFlowWidth/2 +1);
				firstYDeltaTurn = -5; 
			}
			
			Point2D smoothControlPoint = new Point2D.Double(r.pixelCoordinate.getX(), r.routingPoint.getY()+yDeltaCP);
			Point2D firstTurnPoint = new Point2D.Double(smoothControlPoint.getX(), smoothControlPoint.getY()+firstYDeltaTurn);
			Point2D secondTurnPoint = new Point2D.Double(smoothControlPoint.getX()-Math.abs(7), smoothControlPoint.getY()-firstYDeltaTurn);
						
			currSegment.firstRoutePoint = firstTurnPoint;
			currSegment.secondRoutePoint = secondTurnPoint;
			currSegment.controlPoint = smoothControlPoint;
			
			c.mainFlowSegments.add(currSegment);

		}

	}

	class radialOrderComparator implements Comparator<Region> {

		@Override
		public int compare(Region r1, Region r2) {

			if(r1.pixelCoordinate.getY()<=0 && r2.pixelCoordinate.getY()<=0){

				if(r1.pixelCoordinate.getX() >= r2.pixelCoordinate.getX()){
					return 1;
				}
				else{
					return -1;
				}

			}
			else if(r1.pixelCoordinate.getY()>=0 && r2.pixelCoordinate.getY()>=0){

				if(r1.pixelCoordinate.getX()>= r2.pixelCoordinate.getX()){
					return -1;
				}
				else{
					return +1;
				}

			}

			else if(r1.pixelCoordinate.getY()>=0 && r2.pixelCoordinate.getY()<=0){

				return +1;

			}

			else{
				return -1;
			}

		}
	}

	
	class RegionXComparator implements Comparator<Region> {

		@Override
		public int compare(Region r1, Region r2) {

			if(r1.pixelCoordinate.getX() > r2.pixelCoordinate.getX()){
				return -1;
			}
			else if(r1.pixelCoordinate.getX() < r2.pixelCoordinate.getX()){
				return +1;
			}
			return 0;
		}
	}
	
	class RegionYComparator implements Comparator<Region> {

		@Override
		public int compare(Region r1, Region r2) {

			if(r1.pixelCoordinate.getY() > r2.pixelCoordinate.getY()){
				return -1;
			}
			else if(r1.pixelCoordinate.getY() < r2.pixelCoordinate.getY()){
				return +1;
			}
			return 0;
		}
	}

	class redPointComparator implements Comparator<Point2D> {

		@Override
		public int compare(Point2D r1, Point2D r2) {

			if(r1.getY() > r2.getY()){
				return 1;
			}
			else if(r1.getY() < r2.getY()){
				return -1;
			}
			return 0;
		}
	}
}
