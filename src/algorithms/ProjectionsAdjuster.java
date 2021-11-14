package algorithms;

import java.util.ArrayList;

import model.Region;
import model.StateMachineState;

public abstract class ProjectionsAdjuster extends StateMachineState {

	protected ArrayList<Region> allRegions;
	
	public ProjectionsAdjuster(ArrayList<Region> allRegions)
	{
		this.allRegions = allRegions;
	}

}
