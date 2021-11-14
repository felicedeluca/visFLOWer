package main;

import java.util.ArrayList;

import model.Configurator;
import model.Dataset;
import model.DetailedInfoContainer;
import model.DetailedInfoInterface;
import model.DetailedInfoParameters;
import model.Flow;
import model.Region;
import model.RegionGraph;
import model.VisFlowerUserParameters;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import servlet.Period;
import servlet.SigmaJSGraphGateway;

@RestController
public class VisFLOWerGateway {
	
	
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	String pong(@RequestBody String pong){
		return "pong to :" + pong + "\n";
	}
	
	
	@RequestMapping(value = "/alldatasets", method = RequestMethod.GET)
	ArrayList<Dataset> allDatasets(){

		ArrayList<Dataset> datasets = Configurator.getInstance().allDatasets();
		
		return datasets;
	}
	
	@RequestMapping(value = "/datasetvalue", method = RequestMethod.POST)
	ArrayList<Region> datasetvalue(@RequestBody int datasetidentifier){

		ArrayList<Region> regions = Configurator.getInstance().regionsForDataset(""+datasetidentifier);
		
		return regions;
	}
	
	@RequestMapping(value = "/periodsfordataset", method = RequestMethod.POST)
	ArrayList<Period> periodsForDataset(@RequestBody String datasetidentifier){

		ArrayList<Period> periods = Configurator.getInstance().periodsForDataset(datasetidentifier);
		
		return periods;
	}
	
	
	
	@RequestMapping(value = "/details/{type}", method = RequestMethod.POST)
	DetailedInfoContainer  detailedInfos(@PathVariable("type") String type, @RequestBody DetailedInfoParameters param){
		
		
		Configurator sharedConfigurator = Configurator.getInstance();
		
		ArrayList<DetailedInfoInterface> infosArray = null;
		String textTag = null;
		String title = "Info: ";

		
		if(type.equals("source")){
			infosArray = sharedConfigurator.detailedInfo(param.sitesS, param.sitesT, param.month, param.dataset);
			textTag = "targetName";
			title += (infosArray.size()>0) ? infosArray.get(0).periodDescription + " ": "";
			title += (param.sitesS.size()>0) ? " from " + infosArray.get(0).sourceName + " ": "";

		}else if(type.equals("target")){
			infosArray = sharedConfigurator.detailedInfo(param.sitesS, param.sitesT, param.month, param.dataset);
			textTag = "sourceID";
			title += (infosArray.size()>0) ? infosArray.get(0).periodDescription + " ": "";
			title += (param.sitesT.size()>0) ? " to " + infosArray.get(0).targetName + " ": "";

		}else if(type.equals("both")){
			infosArray = sharedConfigurator.detailedInfo(param.sitesS, param.sitesT, "", param.dataset);
			textTag = "periodDescription";
			title += "Over time ";
			title += (param.sitesS.size()>0) ? " from " + infosArray.get(0).sourceName + " ": "";
			title += (param.sitesT.size()>0) ? " to " + infosArray.get(0).targetName + " ": "";
		}
		else{
			
			return null;
		}
		
		DetailedInfoContainer infosContainer = new DetailedInfoContainer();
		infosContainer.infos = infosArray;
		infosContainer.textTag = textTag;
		infosContainer.title = title;
		
		
		return infosContainer;
		
	}
	
	
	
	
	@RequestMapping(value = "/visflower", method = RequestMethod.POST)
	SigmaJSGraphGateway graph(@RequestBody VisFlowerUserParameters param) {

		try {
			
			Configurator sharedConfigurator = Configurator.getInstance();
			sharedConfigurator.flush();
			sharedConfigurator.setupWithParam(param);
			
			ArrayList<Region> sitesS = Configurator.getInstance()
					.getSelectedSitesS(param);
			ArrayList<Region> sitesT = Configurator.getInstance()
					.getAllSitesTForParam(param);
			
			if (sitesS.size()==0) return null;
			
			ArrayList<ArrayList<Region>> clusters = Configurator.getInstance()
					.generateClusters();
			
			
			ArrayList<Flow> flows = Configurator.getInstance().generateFlows(sitesS, sitesT, param.period, param.dataset);
			
			RegionGraph graph = new RegionGraph(sitesS, sitesT, clusters, flows);

			StateMachine stateMachine = new StateMachine();
			stateMachine.startOnGraph(graph);

			SigmaJSGraphGateway sigmaJSGraphGateway = new SigmaJSGraphGateway(
					graph);

			return sigmaJSGraphGateway;

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("si è verificato un errore ");
			return null;
		}
	}
}
