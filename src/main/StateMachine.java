package main;

import java.util.ArrayList;

import model.Configurator;
import model.RegionGraph;
import model.StateMachineState;
import statemachine.targetregionpositionhandler.HVTargetRegionPositionHandler;
import algorithms.HVCompressStrategy;
import algorithms.HVExpandStrategy;
import algorithms.HVSpanExpandStrategy;
import algorithms.MaxAngleCutClusteringStrategy;
import algorithms.MentalMapClusterAdjStrategy;
import algorithms.MiddlePointRouteStrategy;
import algorithms.OnePixelSecondRouting;
import algorithms.RadialPositioning;
import algorithms.ScreenFit;

public class StateMachine {
	
	
	public enum Strategy {
		HV,
		RADIAL,
		HVSPAN
	}
	
	
	ArrayList<StateMachineState> states;
	public RegionGraph graph;
	
	int steps;
	Strategy strategy;
	
	public StateMachine(){
		Configurator sharedConfigurator = Configurator.getInstance();
		this.steps = sharedConfigurator.steps;
		this.strategy = sharedConfigurator.strategy;
	}
	
	
	public RegionGraph startOnGraph(RegionGraph graph){
		
		this.graph = graph;
		start();

		return graph;
	}
	
	
	private void start(){
		
		setup();
		
		for(int i=0; (i<states.size() && i<this.steps); i++){
			
			StateMachineState sms = states.get(i);
			sms.start();
		}
			
	}
	
	
	private void setup(){
		
		states = new ArrayList<StateMachineState>();
				
		ScreenFit fit = new ScreenFit(this.graph);
		states.add(fit);
		
		switch (strategy) {
		case HV:{
			HVExpandStrategy hvExpandS = new HVExpandStrategy(this.graph);
			HVCompressStrategy hvCompressS = new HVCompressStrategy(this.graph);
			HVTargetRegionPositionHandler hvPosition = new HVTargetRegionPositionHandler(this.graph);
			states.add(hvExpandS);
			states.add(fit);
			states.add(hvCompressS);
			states.add(fit);
			states.add(hvPosition);
			states.add(fit);
		}
			
			break;
		case RADIAL:{
			RadialPositioning radPositioning = new RadialPositioning(this.graph);
			states.add(radPositioning);
		}
			
			break;
		case HVSPAN:{
			HVSpanExpandStrategy hvSpanExpandS = new HVSpanExpandStrategy(this.graph);
			states.add(hvSpanExpandS);
		}
			break;

		default:
			break;
		}

		if(graph.clusters.size()==0){
			MaxAngleCutClusteringStrategy maxAngleCutClusteringStrategy = new MaxAngleCutClusteringStrategy(this.graph);
			states.add(maxAngleCutClusteringStrategy);
		}
		
		graph.printClusters();
		
		MiddlePointRouteStrategy midPointRouteS = new MiddlePointRouteStrategy(this.graph);
		MentalMapClusterAdjStrategy mentalMapclusterAdjS = new MentalMapClusterAdjStrategy(this.graph);
		states.add(midPointRouteS);
		states.add(mentalMapclusterAdjS);
		
		OnePixelSecondRouting opsRountingS = new OnePixelSecondRouting(this.graph);
		states.add(opsRountingS);
		
		

	}

}
