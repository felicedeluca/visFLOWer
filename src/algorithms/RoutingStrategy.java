package algorithms;

import model.RegionGraph;
import model.StateMachineState;

public abstract class RoutingStrategy  extends StateMachineState {

	RegionGraph graph;
	
	public RoutingStrategy(RegionGraph graph){
		this.graph = graph;
		this.name = "Routing Strategy";
	}
	
	@Override
	public void start() {

		System.out.println("Starting " + this.name);
		graph.updateRegions();
		route();
		graph.updateRegions();
		
	}
	
	protected abstract void route();
	

}
