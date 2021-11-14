package algorithms;

import model.RegionGraph;
import model.StateMachineState;

public abstract class ClusterAdjusterStrategy extends StateMachineState {

	RegionGraph graph;

	public ClusterAdjusterStrategy(RegionGraph graph) {
		
		this.name = "Cluster Adjuster Strategy";
		this.graph = graph;
	}
	
	@Override
	public void start() {
		
		System.out.println("Starting: " + this.name);

		adjust();
		
		
	}
	
	protected abstract void adjust();

}
