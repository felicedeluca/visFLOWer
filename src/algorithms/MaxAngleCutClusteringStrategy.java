package algorithms;

import java.util.ArrayList;
import java.util.Comparator;

import model.Configurator;
import model.Region;
import model.RegionGraph;

public class MaxAngleCutClusteringStrategy extends ClusteringStrategy {
	
	int clusters;

	public MaxAngleCutClusteringStrategy(RegionGraph graph) {
		super(graph);
		this.name = "Max Angle Cut Clustering Strategy";
		
		Configurator sharedConfigurator = Configurator.getInstance();
		this.clusters = (sharedConfigurator.clusters>=0) ? sharedConfigurator.clusters : 5;
		
	}

	@Override
	protected void cluster() {

		graph.sitesS.sort(new RegionAngleComparator());

		ArrayList<RegionSector> sectors =  calculateRegionSectors(graph.sitesS, true);
		ArrayList<ArrayList<Region>> clusters = splitAndCluster(sectors, graph.sitesS);
		
		ArrayList<ArrayList<Region>> acuteClusters = acuteClusters(clusters);
		
		addClusterToGraph(acuteClusters);
		//splitBigAngleCluster(clusters);
		
		

	}
	
	private ArrayList<ArrayList<Region>> acuteClusters(ArrayList<ArrayList<Region>> clusters){
		
		boolean split = false;
		
		ArrayList<ArrayList<Region>> nuoviClusters = new ArrayList<ArrayList<Region>>(); 
		
		for(ArrayList<Region> regions : clusters){
			
			if(regions.size()<2) {
				nuoviClusters.add(regions);
				continue;
			};
			
			regions.sort(new RegionAngleComparator());
			
			Region first = regions.get(0);
			Region last = regions.get(regions.size()-1);
			
			double delta = Math.abs(last.angle-first.angle);
			
			if(delta<1.22173048){
				nuoviClusters.add(regions);
				continue;
			};
			
			regions.sort(new RegionAngleComparator());
			ArrayList<RegionSector> currSectors = calculateRegionSectors(regions, false);
			currSectors.sort(new RegionSectorComparator());
			
			ArrayList<RegionSector> splitSector = new ArrayList<RegionSector>();

			RegionSector currRS = currSectors.get(0);
			currRS.splitted = true;
			splitSector.add(currRS);

			ArrayList<ArrayList<Region>>  splitClusters = createCluster(splitSector, regions);
			nuoviClusters.addAll(splitClusters);
			
			split = true;

		}
		
		if(split){
			return acuteClusters(nuoviClusters);
		}
		else
		{
			return nuoviClusters;
		}
		
	}
	
	private ArrayList<ArrayList<Region>> createCluster(ArrayList<RegionSector> splitSector, ArrayList<Region> regions){
		
		splitSector.sort(new RegionSectorSplitAngleComparator());
		
		//1. add all Regions in only one Cluster here as ArrayList and prepare otherClusters in an Array
		int sectorsNum = splitSector.size();

		ArrayList<ArrayList<Region>>clusters = new ArrayList<ArrayList<Region>>();

		//Init each ArrayList<Region>
		for(int i=0; i<sectorsNum; i++){
			ArrayList<Region> currCluster = new ArrayList<Region>();
			clusters.add(currCluster);
		}

		//Junk Cluster to insert all exceeding regions
		ArrayList<Region> junkCluster = new ArrayList<Region>();
		
		//2. Iterate through the regions and put in the right cluster
		for(Region rwa : regions){
			
			for(int i=0; i<splitSector.size(); i++){
				
				boolean clustered = false;
				//setting major because splitSector are sort by angle descendent
				if(rwa.angle > splitSector.get(i).midAngle){
					ArrayList<Region> currCluster= clusters.get(i);
					currCluster.add(rwa);
					clustered = true;
					break;
				}
				
				//the cluster not inserted goes into the first cluster since is the leftmost
				else if(!clustered && i == splitSector.size()-1){
					junkCluster.add(rwa);
					clustered = true;
					break;
				}
				
				
			}
		}

		clusters.add(junkCluster);
		
		return clusters;
	}

	private ArrayList<RegionSector> calculateRegionSectors(ArrayList<Region> rwa, boolean circluar){

		ArrayList<RegionSector> sectors = new ArrayList<RegionSector>();

		for(int i=0; i<rwa.size(); i++){

			if(i==rwa.size()-1 && !circluar) break;
			
			Region currRWA = rwa.get(i);
			Region nextRWA = (i==rwa.size()-1) ? rwa.get(0) : rwa.get(i+1);

			double currAngle = currRWA.angle;
			double nextAngle = (i==rwa.size()-1) ? (nextRWA.angle + (2*Math.PI)) : nextRWA.angle;

			double gap =  Math.abs(nextAngle - currAngle) ;

			RegionSector currRS = new RegionSector(currRWA, nextRWA,gap);

			sectors.add(currRS);			

		}

		sectors.sort(new RegionSectorComparator()); //Sorted by angle size

		return sectors;

	}

	private ArrayList<ArrayList<Region>> splitAndCluster(ArrayList<RegionSector> sectors, ArrayList<Region> regionsWithAngles){

		sectors.sort(new RegionSectorComparator());
		regionsWithAngles.sort(new RegionAngleComparator());

		ArrayList<RegionSector> splitSector = new ArrayList<RegionSector>();

		for (int i = 0; (i<sectors.size() && i<this.clusters); i++){
			RegionSector currRS = sectors.get(i);
			currRS.splitted = true;
			splitSector.add(currRS);
		}

		splitSector.sort(new RegionSectorSplitAngleComparator());

//		//1. add all Regions in only one Cluster here as ArrayList and prepare otherClusters in an Array
//
//		int sectorsNum = splitSector.size();
//
//		ArrayList<ArrayList<Region>>clusters = new ArrayList<ArrayList<Region>>();
//
//		
//		//Init each ArrayList<Region>
//		for(int i=0; i<sectorsNum; i++){
//
//			ArrayList<Region> currCluster = new ArrayList<Region>();
//			clusters.add(currCluster);
//		}
//
//		//Junk Cluster to insert all exceeding regions
//		ArrayList<Region> junkCluster = new ArrayList<Region>();
//		
//		//2. Iterate through the regions and put in the right cluster
//		for(Region rwa : regionsWithAngles){
//			for(int i=0; i<splitSector.size(); i++){
//				
//				boolean clustered = false;
//				//setting major because splitSector are sort by angle descendent
//				if(rwa.angle > splitSector.get(i).midAngle){
//					ArrayList<Region> currCluster= clusters.get(i);
//					currCluster.add(rwa);
//					clustered = true;
//					break;
//				}
//				
//				//the cluster not inserted goes into the first cluster since is the leftmost
//				else if(!clustered && i == splitSector.size()-1){
//					junkCluster.add(rwa);
//					clustered = true;
//					break;
//				}
//				
//				
//			}
//		}
//
//		clusters.add(junkCluster);
		
		ArrayList<ArrayList<Region>>clusters = createCluster(splitSector, regionsWithAngles);
		
		
		return clusters;
		
	}
	
	private void addClusterToGraph(ArrayList<ArrayList<Region>> clusters){
		//3. Add Cluster to the Graph
				for(ArrayList<Region> currCluster : clusters){
					this.graph.addClusterWithRegions(currCluster);
				}
	}
	

	class RegionAngleComparator implements Comparator<Region> {

		@Override
		public int compare(Region r1, Region r2) {

			if (r1.angle > r2.angle) {
				return -1;
			} else if(r1.angle < r2.angle) {
				return 1;
			}
			
			return 0;
		}
	}
	
	
	class RegionSector{

		public Region firstRegion;
		public Region secondRegion;

		double angle;
		double midAngle;
		boolean splitted = false;

		public RegionSector(Region firstRegion, Region secondRegion, double angle){

			this.firstRegion = firstRegion;
			this.secondRegion = secondRegion;
			this. angle = angle;
			this.midAngle = (firstRegion.angle + secondRegion.angle)/2;
		}
	}
	//double slope = 1.0 * (output_end - output_start) / (input_end - input_start)

	//output = output_start + round(slope * (input - input_start))


	class RegionSectorComparator implements Comparator<RegionSector> {

		@Override
		public int compare(RegionSector r1, RegionSector r2) {

			if (r1.angle > r2.angle) {
				return -1;
			} else if (r1.angle < r2.angle) {
				return 1;
			}
			return 0;
		}
	}

	class RegionSectorSplitAngleComparator implements Comparator<RegionSector> {

		@Override
		public int compare(RegionSector r1, RegionSector r2) {

			if (r1.midAngle > r2.midAngle) {
				return -1;
			} else if (r1.midAngle < r2.midAngle){
				return 1;
			}
			return 0;
		}
	}

}
