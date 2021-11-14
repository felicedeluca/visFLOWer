package model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class OnePixelEdge {
	
	public String identifier;
	public String targetPointIdentifier;
	public Region source;
	public Point2D controlPoint;

	public Point2D outfallPoint;
	public Point2D targetPoint;
	
	public ArrayList<Point2D> turnPoints;
	
	
	public OnePixelEdge(Region source, Point2D controlPoint, Point2D outfallPoint, Point2D targetPoint){
		
		this.identifier = source.country_code + "_ONE";

		this.source = source;
		this.controlPoint = controlPoint;
		this.outfallPoint = outfallPoint;
		this.targetPoint = targetPoint;

		this.targetPointIdentifier = this.identifier+"_TargetPoint";
		
		int yDeltaTurn = 0;
		if (this.source.pixelCoordinate.getY()-controlPoint.getY() >0){
			yDeltaTurn = 5;
		}
		else if (this.source.pixelCoordinate.getY()-controlPoint.getY()<0){
			yDeltaTurn = -5;
		}
		
		Point2D firstTurnPoint = new Point2D.Double(controlPoint.getX(), controlPoint.getY()+yDeltaTurn);
		Point2D secondTurnPoint = new Point2D.Double(controlPoint.getX()-5, controlPoint.getY());
		
		this.turnPoints = new ArrayList<Point2D>();
		this.turnPoints.add(firstTurnPoint);
		this.turnPoints.add(secondTurnPoint);
		
		
	}
	

	

}
