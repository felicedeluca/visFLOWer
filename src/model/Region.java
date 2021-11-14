package model;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Region implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String country_code;
	public String country_code_ISO3;
	public String name;
	public String fullName;

	public double latitude;
	public double longitude;

	public Point2D pixelCoordinate;
	public Point2D routingPoint = null;//Point on the bisector

	public double angle;

	public int valueSize;

	public SITE_TYPE type;


	public ArrayList<Flow> outgoingFlows;
	public ArrayList<Flow> incomingFlows;
	
	public String description;
	public String wiki;


	public enum SITE_TYPE {
		SOURCE, TARGET
	}	
	
	
	public Region(String country_code, String name, String fullName, double latitude, double longitude, SITE_TYPE type){

		this.country_code = country_code;
		this.name = name;
		this.fullName = fullName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.angle = 0;
		this.pixelCoordinate = new Point2D.Double(0,0);
		this.type = type;
		this.valueSize = 0;

		this.outgoingFlows = new ArrayList<Flow>();
		this.incomingFlows = new ArrayList<Flow>();

	}
	
	public Region(String country_code, String country_code_ISO3, String name, String fullName, double latitude, double longitude, SITE_TYPE type){

		this.country_code = country_code;
		this.name = name;
		this.fullName = fullName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.angle = 0;
		this.pixelCoordinate = new Point2D.Double(0,0);
		this.type = type;
		this.valueSize = 0;

		this.outgoingFlows = new ArrayList<Flow>();
		this.incomingFlows = new ArrayList<Flow>();
		
		this.country_code_ISO3 = country_code_ISO3;

	}

	public double angleFromPoint(Point2D point){

		double angle = Math.atan2(this.pixelCoordinate.getY()-point.getY(), this.pixelCoordinate.getX()-point.getX());

		if(angle<0) angle = 2*Math.PI-Math.abs(angle);

		this.angle = angle;

		return this.angle;

	}


	public double outgoingValue(double filter){

		double outgoingvalue = 0;

		for (Flow f : outgoingFlows){
			if(f.value > filter){
				outgoingvalue += f.value;
			}
		}

		return outgoingvalue;

	}


}
