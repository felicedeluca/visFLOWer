package servlet;

import java.awt.geom.Point2D;

public class SigmaJSSiteTNode extends SigmaJSNode {
	
	public String description;
	public String wiki;

	public SigmaJSSiteTNode(String identifier, String name, Point2D point, int incomingFlow, double yOffset, String description, String wiki) {
		
		super(identifier, name, point, NODE_TYPE.T, incomingFlow, yOffset);
		
		this.description = description;
		this.wiki = wiki;
		

	}

	private static final long serialVersionUID = -8783731660751751817L;

}
