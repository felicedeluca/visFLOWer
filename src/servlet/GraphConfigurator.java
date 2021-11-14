package servlet;

import java.io.Serializable;
import java.util.ArrayList;

public class GraphConfigurator implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public double minSiteTIncomingValue, maxSiteTIncomingValue;
	public double minClusterValue, maxClusterValue;
	public double minPositiveDiscrepancyValue, maxPositiveDiscrepancyValue;
	public double minNegativeDiscrepancyValue, maxNegativeDiscrepancyValue;
	public double minFlowValue, maxFlowValue;
	public double minSiteSOutgoingValue, maxSiteSOutgoingValue;
	public int filterFlow;
	public ArrayList<String> urls; 
	
	public GraphConfigurator(){
		
		
		urls = new ArrayList<String>();
	}
	

}
