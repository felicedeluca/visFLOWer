package servlet;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import model.Configurator;
import model.Flow;
import model.OnePixelEdge;
import model.Region;
import model.RegionGraph;
import servlet.SigmaJSEdge.EDGE_TYPE;
import servlet.SigmaJSNode.NODE_TYPE;
import algorithms.Cluster;
import algorithms.MainFlowSegment;

public class SigmaJSGraphGateway  implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int maxColorRangeValue = 100;
	private static final int minColorRangeValue = 1;

	public ArrayList<SigmaJSNode> nodes;
	public ArrayList<SigmaJSEdge> edges;

	public Point2D minPoint;
	public Point2D maxPoint;

	public Point2D regionSCentroid;
	public int regionSBoundingBoxsize;

	public int maxEdgeSize;
	public int minNodeSize, maxNodeSize;

	double maxYCoordinate;

	public GraphConfigurator graphConfigurator;



	public SigmaJSGraphGateway(RegionGraph graph){


		setupGraphConfigurator(graph);


		maxEdgeSize = 0;

		maxNodeSize = Integer.MIN_VALUE;
		minNodeSize = Integer.MAX_VALUE;

		nodes = new ArrayList<SigmaJSNode>();
		edges = new ArrayList<SigmaJSEdge>();

		Configurator sharedConfigurator = Configurator.getInstance();
		maxYCoordinate = sharedConfigurator.screenSize.getHeight();

		this.regionSCentroid = graph.centroid;

		String centroidID = "centroid";
		nodes.add(new SigmaJSNode(centroidID, "", this.regionSCentroid, NODE_TYPE.CENTROID, (int)graph.regionTRay, maxYCoordinate));
		addCircleRegion(graph);

		ClusterRegionDistanceComparator crdc = new ClusterRegionDistanceComparator(graph.centroid);

		if(graph.clusters.size()>0){


			for(Cluster c : graph.clusters){

				int currEdgeSize = c.regions.size()*2+1;

				if (maxEdgeSize<currEdgeSize){
					maxEdgeSize = currEdgeSize;
				}


				c.regions.sort(crdc);

				for(Region r : c.regions){

					if(r.routingPoint!=null){

						String nodeID = r.country_code;
						String routeNodeID = nodeID+"_Routing";

						SigmaJSNode currSiteSNode = new SigmaJSNode(nodeID, r.name, r.pixelCoordinate, NODE_TYPE.S,maxYCoordinate);

						nodes.add(currSiteSNode);
						nodes.add(new SigmaJSNode(routeNodeID, "", r.routingPoint, NODE_TYPE.ROUTING,maxYCoordinate));

						this.graphConfigurator.urls.add(currSiteSNode.url);

						SigmaJSEdge smoothLeader = new SigmaJSEdge(nodeID+"_flow", nodeID, routeNodeID, 3, EDGE_TYPE.SMOOTH, (int)r.outgoingValue(0));
						edges.add(smoothLeader);


						for(OnePixelEdge one : c.onePixelEdges){

							if(!one.source.equals(r)) continue;

							SigmaJSNode targetONEjsNode = new SigmaJSNode(one.targetPointIdentifier,"", one.targetPoint, NODE_TYPE.ANCHOR, maxYCoordinate);

							Point2D controlPoint = new Point2D.Double(one.controlPoint.getX(), maxYCoordinate-one.controlPoint.getY());
							Point2D outFallPoint = new Point2D.Double(one.outfallPoint.getX(), maxYCoordinate-one.outfallPoint.getY());

							ArrayList<Point2D> turnPoints = new ArrayList<Point2D>();
							for (Point2D currPoint : one.turnPoints){
								turnPoints.add(new Point2D.Double(currPoint.getX(), maxYCoordinate-currPoint.getY()));
							}

							String multipointEdgeIdentifier = one.identifier + "_multipointOrthogonal";

							SigmaJSMultipointEdge multipointEdge = new SigmaJSMultipointEdge(multipointEdgeIdentifier, nodeID, one.targetPointIdentifier,outFallPoint, turnPoints, controlPoint);
							nodes.add(targetONEjsNode);
							edges.add(multipointEdge);

							break;

						}

					}
				}

				convertMainFlowSegmentEdgesForCluster(c);

			}


		}else{


			for(Region r : graph.sitesS){

				String nodeID = r.country_code;				
				SigmaJSNode currSiteSNode = new SigmaJSNode(nodeID, r.name, r.pixelCoordinate, NODE_TYPE.S,maxYCoordinate);
				nodes.add(currSiteSNode);

			}

			for(Region r : graph.sitesT){

				String nodeID = r.country_code;				
				SigmaJSNode currSiteSNode = new SigmaJSNode(nodeID, r.name, r.pixelCoordinate, NODE_TYPE.S,maxYCoordinate);
				nodes.add(currSiteSNode);


			}

		}

		for(Region r : graph.sitesT){
			maxNodeSize = Math.max(maxNodeSize, r.valueSize);
			minNodeSize = Math.min(minNodeSize, r.valueSize);
		}

		double t_site_output_end = 35;
		double t_site_output_start = 10;
		double t_site_input_end = maxNodeSize;
		double t_site_input_start = minNodeSize;
		double t_site_slope = 1.0 * (t_site_output_end - t_site_output_start) / (t_site_input_end - t_site_input_start);


		for(Region r : graph.sitesT){

			String nodeID = r.country_code;
			double output = t_site_output_start + t_site_slope * (r.valueSize - t_site_input_start);
			int incomingFlow = (int)Math.round(output);

			SigmaJSSiteTNode siteT = new SigmaJSSiteTNode(nodeID, r.name, r.pixelCoordinate, incomingFlow, maxYCoordinate, r.description, r.wiki);
			nodes.add(siteT);
		}


		for(Flow f : graph.flows){

			if(Math.abs(f.diff) > sharedConfigurator.filterflow){

				for(OnePixelEdge one : graph.onePixelEdges){

					if(f.source.equals(one.source)){

						String edgeID = f.source.country_code+"_"+f.target.country_code+"_innerGradientArc";


						String color = (f.diff>0) ? colorFromValueInGreenRange(f.diff) : colorFromValueInRedRange(Math.abs(f.diff));;
						boolean dashed = (f.value==0);

						SigmaJSEdge currEdge = new SigmaJSEdge(edgeID, one.targetPointIdentifier, f.target.country_code, 3, EDGE_TYPE.SECONDARY_SMOOTH, 0);
						currEdge.color = color;
						currEdge.dashed = dashed;
						edges.add(currEdge);
					}


				}

			}

		}

		try{

			edges.sort(new EdgeColorComparator());

		}catch(Exception e){

			System.out.println("impossibile ordinare");;

		}


	}

	private void addCircleRegion(RegionGraph graph){

		Configurator sharedConfigurator = Configurator.getInstance();
		double maxYCoordinate = sharedConfigurator.screenSize.getHeight();

		String boundingBoxLEFTPointID = "boundingBoxleftPoint";
		String boundingBoxRIGTHPointID = "boundingBoxRightPoint";
		Point2D leftPointCoordinate = new Point2D.Double((graph.centroid.getX()-graph.regionTRay)+10, graph.centroid.getY());
		Point2D rigthPointCoordinate = new Point2D.Double((graph.centroid.getX()+graph.regionTRay)-10, graph.centroid.getY());
		nodes.add(new SigmaJSNode(boundingBoxLEFTPointID, "", leftPointCoordinate, NODE_TYPE.ANCHOR, maxYCoordinate));
		nodes.add(new SigmaJSNode(boundingBoxRIGTHPointID, "", rigthPointCoordinate, NODE_TYPE.ANCHOR, maxYCoordinate));
		edges.add(new SigmaJSEdge("Image_Circonference", boundingBoxLEFTPointID, boundingBoxRIGTHPointID, 1, EDGE_TYPE.BOUNDARY, 0));

		String whiteBoundingBoxLEFTPointID = "whiteboundingBoxleftPoint";
		String whiteBoundingBoxRIGTHPointID = "whiteboundingBoxRightPoint";
		Point2D whiteleftPointCoordinate = new Point2D.Double((graph.centroid.getX()-graph.regionTRay), graph.centroid.getY());
		Point2D whiterigthPointCoordinate = new Point2D.Double((graph.centroid.getX()+graph.regionTRay), graph.centroid.getY());
		nodes.add(new SigmaJSNode(whiteBoundingBoxLEFTPointID, "", whiteleftPointCoordinate, NODE_TYPE.ANCHOR, maxYCoordinate));
		nodes.add(new SigmaJSNode(whiteBoundingBoxRIGTHPointID, "", whiterigthPointCoordinate, NODE_TYPE.ANCHOR, maxYCoordinate));
		edges.add(new SigmaJSEdge("White_Circonference", whiteBoundingBoxLEFTPointID, whiteBoundingBoxRIGTHPointID, 1, EDGE_TYPE.WHITE_BOUNDARY, 0));

	}

	private String colorFromValueInGreenRange(double rawInputValue){

		double diff_flow_slope = 1.0 * (maxColorRangeValue - minColorRangeValue) / (this.graphConfigurator.maxPositiveDiscrepancyValue - this.graphConfigurator.minPositiveDiscrepancyValue);
		double output = minColorRangeValue + diff_flow_slope * (rawInputValue - this.graphConfigurator.minPositiveDiscrepancyValue);

		double value = Math.round(output/10);

		int r = 200 - (int) Math.floor((200*value/10)) ; ;
		int g = 255;
		int b = 200 - (int) Math.floor((200*value/10)) ;
		//	String rgb = "rgb("+r+","+g+","+b+");";
		String hex = String.format("#%02x%02x%02x", r, g, b);
		return hex;
	}

	private String colorFromValueInRedRange(double rawInputValue){


		double diff_flow_slope = 1.0 * (maxColorRangeValue - minColorRangeValue) / (Math.abs(this.graphConfigurator.maxPositiveDiscrepancyValue) - Math.abs(this.graphConfigurator.minPositiveDiscrepancyValue));
		double output = minColorRangeValue + diff_flow_slope * (rawInputValue - Math.abs(this.graphConfigurator.minPositiveDiscrepancyValue));

		double value = Math.round(output/10);

		int r = 255;
		int g = 200 - (int) Math.floor((200*value/10)) ;
		int b = 200 - (int) Math.floor((200*value/10)) ;
		//		String rgb = "rgb("+r+","+g+","+b+");";
		String hex = String.format("#%02x%02x%02x", r, g, b);

		return hex;
	}


	// Blue Edge color is defined by client
	//	private String colorFromValueInWhiteBlueRange(double segmentValue){
	//
	//		double mainFlowSlope = 1.0 * (maxColorRangeValue - minColorRangeValue) / (this.graphConfigurator.maxClusterValue - this.graphConfigurator.minClusterValue);
	//		double value = minColorRangeValue + mainFlowSlope * (segmentValue - this.graphConfigurator.minClusterValue);
	//
	//		value = Math.ceil(value/10);
	//
	//		int b = 255;
	//		int g = 255 - (int) Math.floor((255*value/10)) ;
	//		int r = 255 - (int) Math.floor((255*value/10)) ;
	//			
	//		String rgb = "rgb("+r+","+g+","+b+");";
	//		
	//		return rgb;
	//
	//	}


	private void convertMainFlowSegmentEdgesForCluster(Cluster c){

		int edgeWidth = c.regions.size()*2+1;
		double maxYCoordinate = Configurator.getInstance().screenSize.getHeight();


		for(MainFlowSegment currSegment : c.mainFlowSegments){

			Region sourceRegion = currSegment.sourceRegion;
			Point2D firstTurnPoint = new Point2D.Double(currSegment.firstRoutePoint.getX(), maxYCoordinate-currSegment.firstRoutePoint.getY());
			Point2D secondTurnPoint = new Point2D.Double(currSegment.secondRoutePoint.getX(), maxYCoordinate-currSegment.secondRoutePoint.getY());
			Point2D controlPoint = new Point2D.Double(currSegment.controlPoint.getX(), maxYCoordinate-currSegment.controlPoint.getY());
			Point2D routingPoint = new Point2D.Double(sourceRegion.routingPoint.getX(), maxYCoordinate-sourceRegion.routingPoint.getY());

			String targetSegmentID = sourceRegion.country_code + "_Main_Target_Routing";


			SigmaJSNode target = null;

			for(SigmaJSNode sjsn : nodes){
				if(sjsn.id.equals(targetSegmentID)){
					target = sjsn;
					break;
				}
			}

			if(target == null){
				Point2D targetPoint = new Point2D.Double(currSegment.targetPoint.getX(), currSegment.targetPoint.getY());
				target  = new SigmaJSNode(targetSegmentID, "", targetPoint, NODE_TYPE.ROUTING, maxYCoordinate);
				nodes.add(target);
			}

			SigmaJSMainAndSmooth edge = new SigmaJSMainAndSmooth(currSegment.id+"Main_Edge_"+sourceRegion.country_code, sourceRegion.country_code, targetSegmentID, routingPoint, edgeWidth, (int)currSegment.totValue, firstTurnPoint, secondTurnPoint, controlPoint);

			edge.singleValue = (int)currSegment.singleValue;
			edge.zIndex += currSegment.zIndex;
			edges.add(edge);

		}


	}


	/**
	 * @param graph
	 */
	private void setupGraphConfigurator(RegionGraph graph){

		double maxSiteTIncomingValue = Double.MIN_VALUE;
		double minSiteTIncomingValue = Double.MAX_VALUE;

		double minClusterValue = Double.MAX_VALUE;
		double maxClusterValue = Double.MIN_VALUE;

		double minPositiveDiscrepancyValue = Double.MAX_VALUE;
		double maxPositiveDiscrepancyValue = Double.MIN_VALUE;

		double minNegativeDiscrepancyValue = Double.MAX_VALUE;
		double maxNegativeDiscrepancyValue = Double.MIN_VALUE;

		double minFlowValue = Double.MAX_VALUE;
		double maxFlowValue = Double.MIN_VALUE;

		Configurator sharedConfigurator = Configurator.getInstance();
		int filterFlow = sharedConfigurator.filterflow;




		//Incoming Value
		for(Region r : graph.sitesT){
			int incomingValue = r.valueSize;
			if(incomingValue<minSiteTIncomingValue) minSiteTIncomingValue = incomingValue;
			if(incomingValue>maxSiteTIncomingValue) maxSiteTIncomingValue = incomingValue;
		}

		//Cluster Value
		for(Cluster c : graph.clusters){

			if(c.totalFlowValue()<minClusterValue) minClusterValue = c.totalFlowValue();
			if(c.totalFlowValue()>maxClusterValue) maxClusterValue = c.totalFlowValue();

		}

		//Outgoing Value, Positive and Negative Discrepancy
		for(Flow f : graph.flows){



			if(f.value<minFlowValue) minFlowValue = f.value;
			if(f.value>maxFlowValue) maxFlowValue = f.value;

			if(Math.abs(f.diff)<filterFlow) continue;

			if(f.diff>0){
				if(f.diff<minPositiveDiscrepancyValue) minPositiveDiscrepancyValue = f.diff;
				if(f.diff>maxPositiveDiscrepancyValue) maxPositiveDiscrepancyValue = f.diff;
			}

			if(f.diff<0){
				if(Math.abs(f.diff)<Math.abs(minNegativeDiscrepancyValue)) minNegativeDiscrepancyValue = f.diff;
				if(Math.abs(f.diff)>Math.abs(maxNegativeDiscrepancyValue)) maxNegativeDiscrepancyValue = f.diff;
			}

		}

		this.graphConfigurator = new GraphConfigurator();
		graphConfigurator.minSiteTIncomingValue = minSiteTIncomingValue; //white
		graphConfigurator.maxSiteTIncomingValue = maxSiteTIncomingValue; //blu scuro
		graphConfigurator.minClusterValue = minClusterValue; //minimo cerchio t
		graphConfigurator.maxClusterValue = maxClusterValue; //massimocerchio t
		graphConfigurator.minPositiveDiscrepancyValue = minPositiveDiscrepancyValue; //verde chiaro
		graphConfigurator.maxPositiveDiscrepancyValue = maxPositiveDiscrepancyValue; // verde scuro
		graphConfigurator.minNegativeDiscrepancyValue = minNegativeDiscrepancyValue; //rosso scuro
		graphConfigurator.maxNegativeDiscrepancyValue = maxNegativeDiscrepancyValue; //rosso chiaro
		graphConfigurator.minFlowValue = minFlowValue; //archi outgoing singoli
		graphConfigurator.maxFlowValue = maxFlowValue; //archi outgoing singoli
		graphConfigurator.filterFlow = filterFlow; //

	}


	class EdgeColorComparator implements Comparator<SigmaJSEdge> {


		@Override
		public int compare(SigmaJSEdge e1, SigmaJSEdge e2) {

			if (e1.zIndex < e2.zIndex) {
				return -1;
			} else if (e1.zIndex > e2.zIndex){
				return 1;
			}

			return 0;

		}
	}



	class ClusterRegionDistanceComparator implements Comparator<Region> {

		Point2D center;

		public ClusterRegionDistanceComparator(Point2D center) {

			this.center = center;

		}

		@Override
		public int compare(Region r1, Region r2) {

			double distance1 = (r1.routingPoint != null)?center.distance(r1.routingPoint):0;
			double distance2 = (r2.routingPoint != null)?center.distance(r2.routingPoint):0;

			if (distance1 > distance2) {
				return 1;
			} else if (distance1 < distance2) {
				return -1;
			}

			return 0;

		}
	}
}
