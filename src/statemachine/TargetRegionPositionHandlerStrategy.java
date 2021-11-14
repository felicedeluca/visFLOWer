package statemachine;

import model.RegionGraph;
import model.StateMachineState;

public abstract class TargetRegionPositionHandlerStrategy extends StateMachineState {

	public RegionGraph graph;
	
	public TargetRegionPositionHandlerStrategy(RegionGraph graph){
		
		this.graph = graph;
		
	}
	
	@Override
	public void start() {

		graph.updateRegions();
		scalePosition();
	}

	protected abstract void scalePosition();
}
