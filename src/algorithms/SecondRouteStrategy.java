package algorithms;

import model.RegionGraph;
import model.StateMachineState;

abstract public class SecondRouteStrategy extends StateMachineState {

	RegionGraph graph;
	
	public SecondRouteStrategy(RegionGraph graph){
		this.graph = graph;
	}
	
	@Override
	public void start() {
		System.out.println("Starting: " + this.name);

		route();
		
	}
	
	abstract protected void route();

}
