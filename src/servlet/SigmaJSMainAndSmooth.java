package servlet;

import java.awt.geom.Point2D;

public class SigmaJSMainAndSmooth extends SigmaJSEdge {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Point2D turnPoint;
	public Point2D firstRoutePoint, secondRoutePoint, controlPoint;


	public SigmaJSMainAndSmooth(String id, String source, String target, Point2D turnPoint,
			double size, int flowValue, Point2D firstRoutePoint, Point2D secondRoutePoint, Point2D controlPoint) {
		super(id, source, target, size, EDGE_TYPE.MAIN, flowValue);
		
		this.turnPoint = turnPoint;
		this.firstRoutePoint = firstRoutePoint;
		this.secondRoutePoint = secondRoutePoint;
		this.controlPoint = controlPoint;

	}

}
