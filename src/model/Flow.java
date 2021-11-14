package model;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class Flow  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Point2D startPoint;
	public Point2D turnPoint;
	
	public Region source;
	public Region target;
	
	public double value;
	public double diff;
	
	public Flow(Region source, Region target, double value){
		
		this.source = source;
		this.target = target;
		this.value = value;
		this.diff = 0;
		
	}
	

}
