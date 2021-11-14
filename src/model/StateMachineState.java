package model;

public abstract class StateMachineState {
	
	public String name;
	public RegionGraph graph;
	
	public abstract void start();

}
