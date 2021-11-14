package model;

import java.io.Serializable;

public class DetailedInfoInterface implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String sourceName;
	public String targetName;
	public String sourceID;
	public int flowValue;
	public String periodDescription;
	
	public String label;
	
	public DetailedInfoInterface(String source, String target, int flowValue, String periodDescription){
		
		this.sourceName = source;
		this.targetName = target;
		this.flowValue = flowValue;
		this.periodDescription = periodDescription;
		
	}

}
