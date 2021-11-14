package servlet;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class SigmaJSNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final static String flagsFolderPath = "resources/img/flags/";
	// private static final String WEBINF = "WEB-INF";

	public String id;
	public String name;
	public double x;
	public double y;
	public double size;
	public String color;

	public String type;
	public String url;
	public String namecode;
	
	private final static int sourceSize = 10;

	public enum NODE_TYPE {
		S, T, ROUTING, ANCHOR,CENTROID
	}

	public SigmaJSNode(String identifier, String name, Point2D point, NODE_TYPE nodeType, double yOffset) {

		this.id = identifier;
		this.name = "";
		this.x = point.getX();
		this.y =  yOffset -point.getY();
		this.size = sourceSize;


		switch (nodeType) {

		case S: {
			color = "#000000";
			this.type = "image";
			this.url = getFlagsFolderFullPath() + name.replace(" ", "_") + ".png";
		}
			break;
		case T: {
			color = "#CCCCFF";
			this.type = "sitest";
			this.name = "";
		}
			break;
		case ROUTING: {
			size = 0;
			color = "#000000";
			this.type = "invisible";
			this.name = "";

		}
		break;

		case ANCHOR:{
			size = 0;
			color = "#FF0000";
			this.type = "invisible";
			this.name = "";
		}break;
		case CENTROID:{
			size = 0;
			color = "#000000";
			this.type = "centroid";
			this.name = "";
		}break;
		}

		if(this.name.length()>2){
			
			this.namecode = this.name.substring(0, 2);
			
		}
		
	}
	
	public SigmaJSNode(String identifier, String name, Point2D point, NODE_TYPE nodeType, int size, double yOffset) {
		
		

		this.id = identifier;
		this.name = "";
		this.x = point.getX();
		this.y =  yOffset -point.getY();
		this.size = sourceSize;

		switch (nodeType) {

		case S: {
			color = "#000000";
			this.type = "image";
			this.url = getFlagsFolderFullPath() + name.replace(" ", "_") + ".png";
			this.size = sourceSize;

		}
			break;
		case T: {
			color = "#CCCCFF";
			this.type = "sitest";
			this.size = size;
			this.name = name;

		}
			break;
		case ROUTING: {
			color = "#000000";
			this.type = "invisible";
			this.size = 0;

		}
			break;
			
		case ANCHOR:{
			color = "#FF0000";
			this.type = "invisible";
			this.size = 0;
		}
			break;
		case CENTROID:{
			color = "#000000";
			this.type = "centroid";
			this.name = "";
		}break;

		}
	if(this.name.length()>2){
			
			this.namecode = this.name.substring(0, 2);
			
		}

	}

	// private String getWebInfPath(){
	//
	// String filePath = "";
	//
	// URL url = SigmaJSNode.class.getResource("SigmaJSNode.class");
	//
	// String className = url.getFile();
	//
	// filePath = className.substring(0,className.indexOf(WEBINF) +
	// WEBINF.length());
	//
	// return filePath;
	//
	// }

	private String getFlagsFolderFullPath() {

		// String filePath = this.getWebInfPath();
		// filePath += "/"+flagsFolderPath;

		return "img/flags/";
	}
}
