package algorithms;


import model.RegionGraph;
import model.StateMachineState;

public abstract class ExpandStrategy extends StateMachineState{
	
	RegionGraph graph;
	
	public ExpandStrategy(RegionGraph graph){
		
		this.graph = graph;
		
	}
	
	@Override
	public void start(){
	
		System.out.println("Starting: " + this.name);
		
		graph.updateRegions();
		expand(graph);
		graph.updateRegions();

		
	}
	
	protected abstract void expand(RegionGraph graph);
	
}
