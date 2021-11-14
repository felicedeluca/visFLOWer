package model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import main.StateMachine.Strategy;
import servlet.Period;
import database.DatabaseManager;

public class Configurator {

	public int steps = Integer.MAX_VALUE;
	public int clusters = -1;
	public Strategy strategy = Strategy.RADIAL;
	public int filterflow = 0;
	public ArrayList<String> sitesSCodes;
	public ArrayList<String> sitesTCodes;
	public ArrayList<String> clustersCodes;
	public ArrayList<String> pack;
	ArrayList<Region> allSitesSList = new ArrayList<Region>();
	DatabaseManager dbMng = new DatabaseManager();
	public Dimension screenSize;
	public Dimension regionTSize;
	public String[] months = {"1"};
	public String period = "1";
	public String dataset;

	private static Configurator instance = null;
	protected Configurator(){}
	public static Configurator getInstance(){
		if(instance == null){
			instance = new Configurator();
		}
		return instance;
	}




	public void flush(){
		steps = Integer.MAX_VALUE;
		clusters = -1;
		strategy = Strategy.RADIAL;
		filterflow = -1;
		allSitesSList = new ArrayList<Region>();

		sitesSCodes = new ArrayList<String>();
		sitesTCodes = new ArrayList<String>();
		clustersCodes = new ArrayList<String>();
		pack = new ArrayList<String>();
		
		screenSize = new Dimension(1000, 1000);
		regionTSize = new Dimension((int)Math.round(screenSize.getWidth()/2), (int)Math.round(screenSize.getHeight()/2));
		
		dbMng = new DatabaseManager();
		period = "1";
		dataset = null;

	}
	
	public void setupWithParam(VisFlowerUserParameters param){
		
		dataset = param.dataset;
		period = param.period;
		sitesSCodes = new ArrayList<String>(Arrays.asList(param.sitess));
		sitesTCodes = new ArrayList<String>(Arrays.asList(param.sitest));
		screenSize = new Dimension(param.screenWidth, param.screenHeight);
		regionTSize = new Dimension((int)Math.round(screenSize.getWidth()/2), (int)Math.round(screenSize.getHeight()/2));
		filterflow = Integer.parseInt(param.flowThreshold);
		
	}
	
	public ArrayList<Region> getSelectedSitesS(VisFlowerUserParameters param){
		
		ArrayList<Region> regions = new ArrayList<Region>();
		sitesSCodes = new ArrayList<String>(Arrays.asList(param.sitess));
	
		//To Resolve duplicated regions
		Set<String> mySet = new HashSet<String>();
		mySet.addAll(sitesSCodes);

		String[] distSitesSCodes = mySet.toArray(new String[mySet.size()]);

		if(distSitesSCodes.length>0){
			regions = dbMng.allRegionsWithCountryCodeInArray(distSitesSCodes);
		}
		else{
			regions = dbMng.allRegions();
		}

		allSitesSList.addAll(regions);


		generateClusters();

		return regions;

	}


	public ArrayList<Region> getAllSitesS(){


		ArrayList<Region> regions = generateSitesS();
		generateClusters();

		return regions;


	}

	public ArrayList<Region> getAllSitesTForParam(VisFlowerUserParameters param){

		return loadDestinationsForDataset(param.dataset)
;

	}	

	private ArrayList<Region> generateSitesS(){
		
		allSitesSList = new ArrayList<Region>();

		Set<String> mySet = new HashSet<String>();
		//mySet.addAll(sitesSCodes);
		
		String[] euPack = {"eu"};
		pack = new ArrayList<String>();
		addSPack(euPack);
		mySet.addAll(pack);
		
		for(String cluster : clustersCodes){
			String[] currClusterRegions = cluster.split(",");
			Collections.addAll(mySet, currClusterRegions);
		}

		String[] distSitesSCodes = mySet.toArray(new String[mySet.size()]);

		ArrayList<Region> allR = null;

		if(distSitesSCodes.length>0){
			allR = dbMng.allRegionsWithCountryCodeInArray(distSitesSCodes);
		}
		else{
			allR = dbMng.allRegions();
		}

		allSitesSList.addAll(allR);

		return allR;

	}



	private ArrayList<Region> loadDestinationsForDataset(String datasetIdentifier)
	{

		ArrayList<Region> regions = dbMng.allDestinationsInArray(null, datasetIdentifier);
		
		return regions;
	}
	
	public ArrayList<Flow> generateFlows(ArrayList<Region> sources, ArrayList<Region> targets, String period, String datasetIdentifier)
	{
		

		ArrayList<Flow> flows =  new ArrayList<Flow>();
						
		for (Region s : sources){
			
			
			for (Region t : targets){
				Flow currFlow = dbMng.retrieveFlow(s, t, period, datasetIdentifier);
				if(currFlow != null)// && currFlow.value>0) Remove comment to avoid zero flows
				{
					double diff = dbMng.calculateDiff(currFlow, sources, targets, period, datasetIdentifier);
					currFlow.diff = diff;
					flows.add(currFlow); 
					s.outgoingFlows.add(currFlow);
					t.incomingFlows.add(currFlow);
				}	
			}
			
		}
		
		ArrayList<String> sourceCodes = new ArrayList<String>();
		for(Region r : sources){
			sourceCodes.add(r.country_code);
		}
		
		ArrayList<String> targetCodes = new ArrayList<String>();
		for(Region r : targets){
			targetCodes.add(r.country_code);
		}
		
		String[] sourceCodesArray = sourceCodes.toArray(new String[sourceCodes.size()]);
		String[] targetCodesArray = targetCodes.toArray(new String[targetCodes.size()]);
		
		
		
		 double totMonth = dbMng.globalSumForPeriod(sourceCodesArray, targetCodesArray, period, datasetIdentifier) ;
		if(filterflow ==-1) {filterflow = (int)Math.round(totMonth*5/1000);}
		 //filterflow = 0;
		
		return flows;
	}

	public void addSPack(String[] spack){

		if(this.pack==null) this.pack = new ArrayList<String>();

		for(String s : spack){

			if(s.equalsIgnoreCase("eu")){
			
				this.pack.add("at");
				this.pack.add("be");
				this.pack.add("bg");
				this.pack.add("ch");
				this.pack.add("cz");
				this.pack.add("de");
				this.pack.add("dk");
				this.pack.add("ee");
				this.pack.add("es");
				this.pack.add("fi");
				this.pack.add("fr");
				this.pack.add("gb");
				this.pack.add("gr");
				this.pack.add("hr");
				this.pack.add("hu");
				 this.pack.add("is");//could be removed
				this.pack.add("ie");
				this.pack.add("lt");
				this.pack.add("lu");
				 this.pack.add("lv");//could be removed
				this.pack.add("mt");
				this.pack.add("nl");
				this.pack.add("no");
				this.pack.add("pl");
				this.pack.add("pt");
				this.pack.add("ro");
				this.pack.add("ru");
				this.pack.add("se");
				this.pack.add("si");
				this.pack.add("sk");
				this.pack.add("tr");
				this.pack.add("ua");

			}

		}

	}

	public ArrayList<ArrayList<Region>> generateClusters(){

		ArrayList<ArrayList<Region>> clustersList = new ArrayList<ArrayList<Region>>();

		for(String cluster : clustersCodes){

			ArrayList<Region> currcluster = new ArrayList<Region>();

			String[] currClusterRegions = cluster.split(",");

			if(currClusterRegions.length>1){
							
				ArrayList<Region> regions = dbMng.allRegionsWithCountryCodeInArray(currClusterRegions);
				
				currcluster.addAll(regions);
				
			}

			clustersList.add(currcluster);

		}

		return clustersList;

	}
	
	public ArrayList<DetailedInfoInterface> detailedInfo(ArrayList<String> sourceCode, ArrayList<String> targetCode, String month2, String datasetIdentifier){
		
		ArrayList<DetailedInfoInterface> infos = new ArrayList<DetailedInfoInterface>();
		
		infos = dbMng.detailedInfo(sourceCode, targetCode, month2, datasetIdentifier);
		
		return infos;
		
	}
	
	
	public ArrayList<Region> regionsForDataset(String datasetidentifier){
		
		ArrayList<Region> regions = new ArrayList<Region>();
		regions = dbMng.regionsForDataset(datasetidentifier);

		
		return regions;
		
	}

	public ArrayList<Period> periodsForDataset(String datasetidentifier){
		
		ArrayList<Period> periods = new ArrayList<Period>();
		periods = dbMng.periodsForDataset(datasetidentifier);

		
		return periods;
		
	}
	
	public ArrayList<Dataset> allDatasets(){

		ArrayList<Dataset> datasets = dbMng.allDatasets();
		
		return datasets;
	}
}