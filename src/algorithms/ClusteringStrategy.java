package algorithms;

import model.RegionGraph;
import model.StateMachineState;

public abstract class ClusteringStrategy extends StateMachineState {

	RegionGraph graph;
	
	public ClusteringStrategy(RegionGraph graph){
		this.graph = graph;
		this.name = "Clustering Strategy";
	}
	
	@Override
	public void start() {
		System.out.println("Starting: " + this.name);

		graph.updateRegions();
		cluster();
		graph.updateRegions();
		
	}
	
	protected abstract void cluster();
	
	

}
