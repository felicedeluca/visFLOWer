package algorithms;

import java.awt.geom.Point2D;

import model.Region;

public class MainFlowSegment {

	public Region sourceRegion;//, targetRegion;
	//public Point2D source, target;
	public Point2D turnPoint;
	public Point2D targetPoint;
	public double totValue;
	public int id;
	public double singleValue;
	
	public Point2D firstRoutePoint, secondRoutePoint, controlPoint;
	
	public int zIndex = 0;//Edge Layer

	
	
	static int nextID = 0;
	
	
//	public MainFlowSegment(Point2D source, Point2D target, double value, double singleValue){
//		this.source = source;
//		this.target = target;
//		this.value = value;
//		this.id = nextID++;
//		sourceRegion = null;
//		targetRegion = null;
//		this.singleValue = singleValue;
//	}
	
	public MainFlowSegment(Region sourceRegion, Point2D turnPoint, Point2D targetPoint, double totValue, double singleValue){
		
		this.sourceRegion = sourceRegion;
		this.turnPoint = turnPoint;
		this.targetPoint = targetPoint;
		this.totValue = totValue;
		this.id = nextID++;
		this.singleValue = singleValue;
		
		
		
	}
	
	
}
