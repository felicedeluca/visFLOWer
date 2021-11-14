package algorithms;

import model.RegionGraph;
import model.StateMachineState;

public abstract class CompressStrategy extends StateMachineState {
	
	RegionGraph graph;
			
	public CompressStrategy(RegionGraph graph)
	{
		this.graph = graph;
	}
	
	@Override
	public void start() {

		System.out.println("Starting: " + this.name);

		graph.updateRegions();
		compress(graph);
		graph.updateRegions();

	}
	
	protected abstract void compress(RegionGraph graph);
	
}
