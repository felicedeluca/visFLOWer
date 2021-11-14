package utilities;

import java.awt.geom.Point2D;

public class PointsRotator {

	private static PointsRotator instance = null;
	
	
	private PointsRotator(){
		
	}
	
	public PointsRotator getInstance(){
		
		if(instance == null){
			instance = new PointsRotator();
		}
		
		return instance;
		
	}
	
	
	public static void rotatePoint(Point2D point, double theta, Point2D origin){
		
		double cx = origin.getX();
		double cy = origin.getY();

		double px = Math.cos(theta) * (point.getX() - cx) - Math.sin(theta) * (point.getY() - cy) + cx;
		double py = Math.sin(theta) * (point.getX() - cx) + Math.cos(theta) * (point.getY() - cy) + cy;
		
		point.setLocation(px, py);

	}

	
}
