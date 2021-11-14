package servlet;

import java.io.Serializable;
import java.util.Comparator;

public class SigmaJSEdge implements Serializable, Comparator<SigmaJSEdge>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static String mapFolderPath = "resources/img/";
	
	
	public String id;
	public String source;
	public String target;
	public double size;
	public String color;
	public String type = "def";
	public EDGE_TYPE edgeType;
	public String url;
	public int flowValue;
	public int singleValue;
	
	public boolean dashed = false;

	public double angle;
	
	public double zIndex, turnx, turny;
	
	public enum EDGE_TYPE {
		MAIN, SECONDARY, BOUNDARY, SECTOR_DELIMITER, SMOOTH, SECONDARY_SMOOTH,WHITE_BOUNDARY, MULTIPOINT_ORTOGONAL, MAIN_SMOOTH
		}
	
	public SigmaJSEdge(String id, String source, String target, double size, EDGE_TYPE edgeType, int flowValue){
		
		this.id = id;
		this.source = source;
		this.target = target;
		this.size = size;
		this.edgeType = edgeType;
		this.zIndex = 0;
		this.flowValue = flowValue;

		
		switch(edgeType){
		case MAIN:{
			color = "#000000";
			this.type = "main";
			this.zIndex = 0;


		}break;
		
		case MAIN_SMOOTH:{
			color = "#000000";
			this.type = "mainsmooth";
			this.zIndex = 0;


		}break;
		
		case SECONDARY:{
			color = "#000000";
			this.type = "secondary";
			this.zIndex = 2000;
		}break;
		
		case BOUNDARY:{
			color = "#000000";
			this.type = "boundarycircle";
			this.zIndex = -300;
			this.size = 4;
			this.url = getMapFolderFullPath() + "maplayer.png";

		}
		break;
		case WHITE_BOUNDARY:{
			color = "#000000";
			this.type = "whiteboundarycircle";
			this.zIndex = -500;
			this.size = 4;

		}
		break;
		case SECTOR_DELIMITER:{
			color = "#000000";
			this.type = "sector_edge";
			this.zIndex = 1;


		}
		break;
		case SMOOTH:{
			color = "#000000";
			this.type = "smooth";
			this.size = 3;
			this.zIndex = -1;


		}
		break;
		
		case SECONDARY_SMOOTH:{
			color = "#000000";
			this.type = "secondarysmooth";
			this.zIndex = 1000;
		}break;
		
		
		case MULTIPOINT_ORTOGONAL:{
			color = "#000000";
			this.type = "multipoint_orthogonal";
			this.zIndex = 1000;
		}break;
		
		}
		
	}

	@Override
	public int compare(SigmaJSEdge arg0, SigmaJSEdge arg1) {

		return 0;
	}

	
	private String getMapFolderFullPath() {

		// String filePath = this.getWebInfPath();
		// filePath += "/"+flagsFolderPath;

		return "img/";
	}
}
