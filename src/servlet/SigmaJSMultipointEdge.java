package servlet;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class SigmaJSMultipointEdge extends SigmaJSEdge {
	
	public Point2D outfallPoint;
	public ArrayList<Point2D> turnPoints;

	public Point2D controlPoint;
	
	private static final long serialVersionUID = 1L;
	

	public SigmaJSMultipointEdge(String id, String source, String target, Point2D outfallPoint, ArrayList<Point2D> turnPoints, Point2D controlPoint) {
		
		super(id, source, target, 1, EDGE_TYPE.MULTIPOINT_ORTOGONAL,0);
		
		this.outfallPoint = outfallPoint;
		this.turnPoints = turnPoints;
		this.controlPoint = controlPoint;
		
		
		
	}


}
