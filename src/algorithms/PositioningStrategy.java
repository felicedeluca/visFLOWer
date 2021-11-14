package algorithms;

import model.RegionGraph;
import model.StateMachineState;

public abstract class PositioningStrategy extends StateMachineState {

	RegionGraph graph;
	
	public PositioningStrategy(RegionGraph graph){
		this.graph = graph;
	}
	
	@Override
	public void start() {
		graph.updateRegions();
		place(graph);
		graph.updateRegions();
	}
	
	protected abstract void place(RegionGraph graph);


}
