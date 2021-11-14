package algorithms;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;

import utilities.PointsRotator;
import model.Region;
import model.RegionGraph;

public class MentalMapClusterAdjStrategy extends ClusterAdjusterStrategy {

	double epsilon = 1; //custom gap for all shapes

	public MentalMapClusterAdjStrategy(RegionGraph graph) {
		super(graph);
		this.name = "Mental Map Cluster AdjStrategy";

	}

	@Override
	protected void adjust() {

		graph.updateRegions();

		for(Cluster c : graph.clusters){

			double theta = c.routingAngle;
			Point2D origin = graph.centroid;

			rotateRegions(c, -theta, origin);
			startVerticalScan(c);
			startHorizontalScan(c);
			rotateRegions(c, theta, origin);

		}
	}

	private void rotateRegions(Cluster cluster, double theta, Point2D origin){
		
		for(Region r : cluster.regions){

			PointsRotator.rotatePoint(r.pixelCoordinate, theta, origin);
			PointsRotator.rotatePoint(r.routingPoint, theta, origin);

		}

	}



	private void startHorizontalScan(Cluster cluster){

		ArrayList<Region> regions = cluster.regions;
		regions.sort(new xCoordinateRegionComparator());

		int defaultRegionEdgeSize = 3;
		int defaultRegionsEdgeGap = 20; 
		int defaultRegionsGap = (defaultRegionEdgeSize+defaultRegionsEdgeGap);

		for (int i=0; i<regions.size()-1; i++){

			Region r = regions.get(i);
			Region nextR = nextRegionOnSameSideOf(regions, r);

			if(nextR!=null){

				double delta = (nextR.pixelCoordinate.getX()-r.pixelCoordinate.getX())-defaultRegionsGap;

				if(delta<0) shiftHorizontally(regions, nextR, delta);

			}

		}
	}

	private void shiftHorizontally(ArrayList<Region> regions, Region region, double delta){

		double x = region.pixelCoordinate.getX();
		double y = region.pixelCoordinate.getY();

		x += Math.abs(delta);
		region.pixelCoordinate.setLocation(x, y);


		double routingX = region.routingPoint.getX();
		double routingY = region.routingPoint.getY();

		routingX += Math.abs(delta);
		region.routingPoint.setLocation(routingX, routingY);

		int rIndex = regions.indexOf(region);

		if(rIndex < regions.size()-1){

			Region nextR= regions.get(rIndex+1);

			if(nextR!=null){

				double deltaNext = (nextR.pixelCoordinate.getX()-region.pixelCoordinate.getX())-1;

				if(deltaNext<0){

					shiftHorizontally(regions, nextR, delta);
				}
			}
		}

	}

	private Region nextRegionOnSameSideOf(ArrayList<Region> regions, Region region){


		int regionIndex = regions.indexOf(region);

		for(int i = regionIndex+1; i<regions.size(); i++){

			Region currRegion = regions.get(i);

			if((region.pixelCoordinate.getY()>0 && currRegion.pixelCoordinate.getY()>0) || 
					(region.pixelCoordinate.getY()<0 && currRegion.pixelCoordinate.getY()<0)){
				return currRegion;				
			}

		}
		return null;

	}

	private void startVerticalScan(Cluster cluster){
		
		if(cluster.regions.size()==1) return;

		ArrayList<Region> regions = cluster.regions;
		regions.sort(new heigthRegionComparator());
		

		double edgeSize = cluster.regions.size()*2+1;
		double minSpanFromDirettrice = edgeSize/2+20;

		double defaultRegionsGap = 2;

		for (int i=0; i<regions.size(); i++){

			Region r = regions.get(i);
			double x = r.pixelCoordinate.getX();
			double y = r.pixelCoordinate.getY();
			double heigth = Math.abs(y);

			double delta = heigth - minSpanFromDirettrice;

			if(delta<0){
				Region nextR = nextRegionOnSameSideOf(regions, r);
				if(nextR!=null){
					double nextHeigth = Math.abs(nextR.pixelCoordinate.getY());
					double deltaNext = nextHeigth-(heigth+Math.abs(delta)+defaultRegionsGap);
					if(deltaNext<0){
						shiftVertically(regions, nextR, Math.abs(deltaNext));
					}
				}

				double ny = (y<=0) ? y-Math.abs(delta) : y+Math.abs(delta);

				r.pixelCoordinate.setLocation(x, ny);
			}
		}
		
	}

	private void shiftVertically(ArrayList<Region> regions, Region region, double delta){

		double x = region.pixelCoordinate.getX();
		double y = region.pixelCoordinate.getY();

		double defaultRegionsGap = 2;

		double ny = (y<=0) ? y-delta : y+delta;
		double heigth = Math.abs(ny);

		region.pixelCoordinate.setLocation(x, ny);

		int rIndex = regions.indexOf(region);

		if(rIndex < regions.size()-1){

			Region nextR = nextRegionOnSameSideOf(regions, region);

			if(nextR!=null){

				double nextHeigth = Math.abs(nextR.pixelCoordinate.getY());
				double deltaNext = nextHeigth-(heigth+defaultRegionsGap);
				if(deltaNext<0){
					shiftVertically(regions, nextR, Math.abs(deltaNext));

				}
				

			}

		}

	}

	class xCoordinateRegionComparator implements Comparator<Region> {

		@Override
		public int compare(Region r1, Region r2) {

			if (r1.pixelCoordinate.getX() > r2.pixelCoordinate.getX()) {
				return 1;
			} else if (r1.pixelCoordinate.getX() < r2.pixelCoordinate.getX()) {
				return -1;
			}

			return 0;
		}
	}
	
	class heigthRegionComparator implements Comparator<Region> {

		@Override
		public int compare(Region r1, Region r2) {

			if (Math.abs(r1.pixelCoordinate.getY()) > Math.abs(r2.pixelCoordinate.getY())) {
				return 1;
			} else if (Math.abs(r1.pixelCoordinate.getY()) < Math.abs(r2.pixelCoordinate.getY())) {
				return -1;
			}
			return 0;
		}
	}

}
