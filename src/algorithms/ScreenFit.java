package algorithms;

import java.awt.Dimension;
import java.util.ArrayList;

import model.Configurator;
import model.Region;
import model.RegionGraph;
import model.StateMachineState;

public class ScreenFit extends StateMachineState{

	ArrayList<Region> allRegions;
	RegionGraph graph;

	Dimension screenSize;
	Dimension regionTSize;

	static final double regionTration = 0.5;


	public ScreenFit (RegionGraph graph){

		ArrayList<Region> collection = new ArrayList<Region>();
		collection.addAll(graph.sitesS);
		collection.addAll(graph.sitesT);

		this.graph = graph;

		this.allRegions = collection;
		super.name = "ScreenFit";


		Configurator sharedConfigurator = Configurator.getInstance();
		screenSize = sharedConfigurator.screenSize;
		regionTSize = sharedConfigurator.regionTSize;

	}

	@Override
	public void start() {

		graph.updateRegions();
		//roundCoordinates();
		translateBoundingBoxAtOrigin();
		compressRegions();
		translateCentroidAtOrigin();
		scaleTRegion();
		translateCentroidAtOrigin();

		graph.updateRegions();

	}


	private void scaleTRegion(){

		double currMaxSide = 0;

//		Region maxdistr = null;

		for(Region r : graph.sitesT){

			double distx = Math.abs(graph.centroid.getX()-r.pixelCoordinate.getX());
			double disty = Math.abs(graph.centroid.getY()-r.pixelCoordinate.getY());

			double maxRegionSide = Math.max(distx, disty);

			if(currMaxSide<maxRegionSide){
				currMaxSide = maxRegionSide;
//				maxdistr = r;
			}

		}

		double desideredMaxSide = Math.min(regionTSize.getWidth(), regionTSize.getHeight());

		double ratio = (desideredMaxSide/2)/currMaxSide;

		for(Region currRegion : graph.sitesT){		

			double oldX = currRegion.pixelCoordinate.getX();
			double oldY = currRegion.pixelCoordinate.getY();

			double nuovaX = oldX*ratio;
			double nuovaY = oldY*ratio;

			currRegion.pixelCoordinate.setLocation(nuovaX, nuovaY);

		}

	}

	private void translateCentroidAtOrigin()
	{
		graph.updateRegions();
		translateSitesWithDelta(-graph.centroid.getX(), -graph.centroid.getY());
		graph.updateRegions();
	}

	private void translateBoundingBoxAtOrigin()
	{
		//min value of all x and y coordiantes
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;

		for(Region r : allRegions){

			if(r.pixelCoordinate.getX() <= minX){
				minX = r.pixelCoordinate.getX();
			}

			if(r.pixelCoordinate.getY() <= minY){
				minY = r.pixelCoordinate.getY();
			}
		}


		translateSitesWithDelta(-minX, -minY);
	}


	private void translateSitesWithDelta(double deltax, double deltay){

		for(Region r : allRegions){

			double x = r.pixelCoordinate.getX();
			double y = r.pixelCoordinate.getY();

			x += deltax;
			y += deltay;

			r.pixelCoordinate.setLocation(x, y);

		}

	}

	private void compressRegions(){

		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;

		double maxSide = Math.min(screenSize.width, screenSize.height);

//		Region farR = null;

		double maxFar = 0;

		for(Region r : allRegions){

			double currMaxSide = Math.max(r.pixelCoordinate.getX(), r.pixelCoordinate.getY());


			if(maxFar<=currMaxSide){
//				farR = r;
				maxFar = currMaxSide;
			}
			
			
			//TODO Remove after screen shot 
			if(r.pixelCoordinate.getX() >= maxX) maxX = r.pixelCoordinate.getX();
			if(r.pixelCoordinate.getY() >= maxY) maxY = r.pixelCoordinate.getY();
			
		}

		double ratio = maxFar/maxSide;
		
//		double xRatio = maxX/screenSize.width;
//		double yRatio = maxY/screenSize.height;


		for(Region r : allRegions){

			double x = r.pixelCoordinate.getX();
			double y = r.pixelCoordinate.getY();

			x /= ratio;
			y /= ratio;
//			
//			x /= xRatio;
//			y /= yRatio;

			r.pixelCoordinate.setLocation(x, y);

		}
	}

//	private void roundCoordinates(){
//
//		for(Region r : allRegions){
//
//			double x = r.pixelCoordinate.getX();
//			double y = r.pixelCoordinate.getY();
//
//			x = Math.round(x);
//			y = Math.round(y);;
//
//			r.pixelCoordinate.setLocation(x, y);
//
//		}
//
//		graph.updateRegions();
//	}

}
