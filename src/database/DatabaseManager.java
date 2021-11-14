package database;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import model.Dataset;
import model.DetailedInfoInterface;
import model.Flow;
import model.Region;
import model.Region.SITE_TYPE;
import servlet.Period;

public class DatabaseManager {

	final static String databaseFolderPath = "resources/";
	final static String databaseName = "centroids.db";



	final static String destinationsTable = "destinations";
	final static String affluenceTable = "affluence";


	private static final String WEBINF = "WEB-INF";

	public String getWebInfPath(){

		String filePath = "";

		URL url = DatabaseManager.class.getResource("DatabaseManager.class");

		String className = url.getFile();

		filePath = className.substring(0,className.indexOf(WEBINF) + WEBINF.length());

		return filePath;

	}

	public String getDBFullPath(){

		String filePath = this.getWebInfPath();
		filePath += "/"+databaseFolderPath+databaseName;

		return filePath;
	}

	public ArrayList<Region> allRegions(){

		Connection c = null;
		Statement stmt = null;
		ArrayList<Region> regions = new ArrayList<Region>();

		try {

			String dbPath = getDBFullPath();

			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);


			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery("select * from centroids where id <> '' AND id <> 'ps';");

			while(rs.next()){

				String shortName = rs.getString("name");
				String fullName = rs.getString("full_name");
				String countryCode = rs.getString("id");

				double latitude = rs.getDouble("latitude");
				double longitude = rs.getDouble("longitude");

				Region currRegion = new Region(countryCode, shortName, fullName, latitude, longitude, SITE_TYPE.SOURCE);

				regions.add(currRegion);

			}

			rs.close();
			stmt.close();
			c.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}


		return regions;

	}

	public ArrayList<Region> allRegionsWithCountryCodeInArray(String[] countryCodesArray){

		Connection c = null;
		Statement stmt = null;
		ArrayList<Region> regions = new ArrayList<Region>();
		String query = "";
		try {

			String dbPath = getDBFullPath();

			Class.forName("org.sqlite.JDBC");

			c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);		      
			stmt = c.createStatement();
			query = "select centroids.id, centroids.name, centroids.latitude, centroids.longitude, centroids.full_name"
					+ " FROM centroids join isocode on centroids.id = isocode.iso3166v2"
					+ " WHERE centroids.id <> ''";

			query += " AND ";

			for (int i=0; i<countryCodesArray.length; i++){

				if (i>0){
					query += " or ";
				}

				query += "("
						+ "centroids.id LIKE '" + countryCodesArray[i] + "' "
						+ "or "
						+ "isocode.iso3166v3 LIKE '" +countryCodesArray[i]+ "'"
						+ ")";
			}

			query += ";";

			ResultSet rs = stmt.executeQuery(query);

			while(rs.next()){

				String shortName = rs.getString("name");
				String fullName = rs.getString("full_name");
				String countryCode = rs.getString("id");

				double latitude = rs.getDouble("latitude");
				double longitude = rs.getDouble("longitude");

				Region currRegion = new Region(countryCode, shortName, fullName, latitude, longitude, SITE_TYPE.SOURCE);

				regions.add(currRegion);
				
			}

			rs.close();
			stmt.close();
			c.close();

		} catch ( Exception e ) {
			System.err.println("Crashed with query " + query + " " + e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}


		return regions;

	}

	public ArrayList<Region> allDestinationsInArray(String[] destinationsCodesArray, String datasetIdentifier){


		Connection c = null;
		Statement stmt = null;
		ArrayList<Region> regions = new ArrayList<Region>();

		try {

			String dbPath = getDBFullPath();

			Class.forName("org.sqlite.JDBC");

			c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);		      
			stmt = c.createStatement();
			String query = "select destinations.id as id, destinations.name as name, destinations.latitude as latitude, destinations.longitude as longitude, destinations.Description as Description, destinations.Wiki as Wiki";
			query += " FROM affluence join destinations on id_t = destinations.id  ";
			query += " WHERE id_dataset = "+datasetIdentifier+" ";
			query += " group by destinations.id order by destinations.name;";

			ResultSet rs = stmt.executeQuery(query);

			while(rs.next()){


				String id = rs.getString("id");
				String name = rs.getString("name");
				double latitude = rs.getDouble("latitude");
				double longitude = rs.getDouble("longitude");
				String description = rs.getString("Description");
				String wiki = rs.getString("Wiki");

				Region currRegion = new Region(id, name, name, latitude, longitude, SITE_TYPE.TARGET);
				currRegion.description = description;
				currRegion.wiki = wiki;

				regions.add(currRegion);

			}

			rs.close();
			stmt.close();
			c.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}


		return regions;

	}

	public void loadRegionsFromSharedConfigurator(){
		//Configurator sharedConfigurator = Configurator.getInstance();



	}


	public Flow retrieveFlow(Region source, Region target, String idPeriod, String idDataset)
	{


		Connection c = null;
		Statement stmt = null;
		Flow flow = null;
		String query = "";

		try {

			String dbPath = getDBFullPath();

			Class.forName("org.sqlite.JDBC");

			c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);		      
			stmt = c.createStatement();

			query = "select value from " + affluenceTable + ""
					+ " where id_s LIKE '"+ source.country_code + "' "
					+ " AND id_t LIKE '"+target.country_code+"' "
					+ "AND id_period = '"+ idPeriod +"' "
					+ "AND id_dataset ='" + idDataset +"';" ;
			ResultSet rs = stmt.executeQuery(query);

			while(rs.next()){

				double value = rs.getDouble("value");
				flow = new Flow(source, target, value); 

			}

			rs.close();
			stmt.close();
			c.close();

		} catch ( Exception e ) {

			System.out.println("Error Query: " + query);
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}


		return flow;
	}


	public double calculateDiff(Flow currFlow, ArrayList<Region> sources, ArrayList<Region> targets, String idPeriod, String datasetIdentifier){

		Connection c = null;
		Statement stmt = null;
		double diff = 0;
		double globalSum = 0;
		double exitSum = 0;
		double totSite = 0.1;

		try {

			String dbPath = getDBFullPath();

			Class.forName("org.sqlite.JDBC");

			c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);		      
			stmt = c.createStatement();
			ResultSet rs = null;

			String globalSumQuery = globalSumQuery(sources, targets, idPeriod, datasetIdentifier);
			rs = stmt.executeQuery(globalSumQuery);
			while(rs.next()){
				globalSum = rs.getDouble("sum");
			}

			ArrayList<String> targetCodes = new ArrayList<String>();
			for(Region r : targets){

				targetCodes.add(r.country_code);

			}

			String exitSumQuery = exitSumQuery(currFlow.source, targetCodes.toArray(new String[targetCodes.size()]), idPeriod, datasetIdentifier);
			rs = stmt.executeQuery(exitSumQuery);
			while(rs.next()){
				exitSum = rs.getDouble("sum");
			}

			ArrayList<Region> oneSiteList = new ArrayList<Region>();
			oneSiteList.add(currFlow.target);
			String totSiteQuery = globalSumQuery(sources, oneSiteList, idPeriod, datasetIdentifier);
			rs = stmt.executeQuery(totSiteQuery);
			while(rs.next()){
				totSite = rs.getDouble("sum") + 0.1;
			}

			rs.close();
			stmt.close();
			c.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}

		try{

			diff = currFlow.value - (totSite/globalSum) * exitSum;



		}catch(Exception e){

			diff = 0;

		}

		return diff;

	}


	private String globalSumQuery(ArrayList<Region> sources, ArrayList<Region> targets, String idPeriod, String datasetIdentifier){

		String query = "select DISTINCT SUM(value) as sum from "+ affluenceTable +" where id_dataset = " + datasetIdentifier
				+ "" 
				+ "";
		query += ""
				+ " and ( ";

		for (int i=0; i<targets.size(); i++){

			if (i>0){
				query += " or ";
			}

			query += "id_t LIKE '" + targets.get(i).country_code + "' ";
		}

		query += ") AND (";

		for (int i=0; i<sources.size(); i++){

			if (i>0){
				query += " or ";
			}

			query += "id_s LIKE '" + sources.get(i).country_code + "' ";
		}

		query += " ) AND id_period = '"+idPeriod+"';";


		// System.out.println("Total sum query: " + query);

		return query;

	}

	private String exitSumQuery(Region source, String[] targetCodes, String idPeriod, String datasetIdentifier){


		String where = "";
		where += " id_dataset = '" + datasetIdentifier + "' ";
		where += " and id_s LIKE '" + source.country_code +"' ";
		where += " AND id_period = '"+idPeriod+"'";


		String targetCondition = "";

		for(String targetString : targetCodes){

			if (targetString == null) continue;

			if(!targetString.equals("")){

				if(!targetCondition.equals("")){
					targetCondition += " OR ";
				}

				else{
					if (!where.equals("")) where += " AND ";

					targetCondition += " ( ";

				}

				targetCondition +=  " id_t = '"+targetString+"' ";

			}


		}
		where += targetCondition;
		if(!targetCondition.equals("")) where+= " ) ";

		String query = "select DISTINCT SUM(value) as sum from "+ affluenceTable +" where " + where +";";
		// System.out.println("Exit sum Query "+query);
		return query;

	}

	public double globalSumForPeriod(String[] sourceCodes, String[] targetCodes, String idPeriod, String datasetIdentifier){

		Connection c = null;
		Statement stmt = null;

		double incomingTotMonth = 0;

		String query = "";


		try {

			String dbPath = getDBFullPath();

			Class.forName("org.sqlite.JDBC");

			c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);		      
			stmt = c.createStatement();
			ResultSet rs = null;

			String where = "";

			where +=" id_period = '"+idPeriod+"'";
			where +=" and id_dataset = " + datasetIdentifier;


			String sourceCondition = "";

			for(String sourceString : sourceCodes){

				if(sourceString == null) continue;

				if(!sourceString.equals("")){

					if(!sourceCondition.equals("")){
						sourceCondition += " OR ";
					}
					else{
						if (!where.equals("")) where += " AND ";

						sourceCondition += " ( ";

					}

					sourceCondition +=  " id_s LIKE '"+sourceString+"' ";

				}



			}

			where += sourceCondition;
			if(!sourceCondition.equals("")) where+= " ) ";

			String targetCondition = "";


			for(String targetString : targetCodes){

				if (targetString == null) continue;

				if(!targetString.equals("")){

					if(!targetCondition.equals("")){
						targetCondition += " OR ";
					}

					else{
						if (!where.equals("")) where += " AND ";

						targetCondition += " ( ";

					}

					targetCondition +=  " id_t = '"+targetString+"' ";

				}



			}

			where += targetCondition;
			if(!targetCondition.equals("")) where+= " ) ";

			query = "select sum(value) as sum from affluence where "+ where +";";
			rs = stmt.executeQuery(query);
			while(rs.next()){
				incomingTotMonth = rs.getDouble("sum");
			}

			rs.close();
			stmt.close();
			c.close();

			// System.out.println("Global Sum Query :" + query);


		} catch ( Exception e ) {
			System.out.println("Query Error :" + query);
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}


		return incomingTotMonth;

	}

	public ArrayList<DetailedInfoInterface> detailedInfo(ArrayList<String> sourceCodes, ArrayList<String> targetCodes, String idPeriod, String datasetIdentifier){

		ArrayList<DetailedInfoInterface> infos = new ArrayList<DetailedInfoInterface>();

		Connection c = null;
		Statement stmt = null;

		try {

			String dbPath = getDBFullPath();

			Class.forName("org.sqlite.JDBC");

			c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);		      
			stmt = c.createStatement();
			ResultSet rs = null;

			String where = " id_dataset = '" + datasetIdentifier + "' ";

			if(!idPeriod.equals("")){
				where +=" and id_period = '"+idPeriod+"' ";
			}

			if(sourceCodes != null){

				String sourceCondition = "";

				for(String sourceString : sourceCodes){

					if(sourceString == null) continue;

					if(!sourceString.equals("")){

						if(!sourceCondition.equals("")){
							sourceCondition += " OR ";
						}
						else{
							if (!where.equals("")) where += " AND ";

							sourceCondition += " ( ";

						}

						sourceCondition +=  " id_s LIKE '"+sourceString+"' ";

					}

				}

				where += sourceCondition;
				if(!sourceCondition.equals("")) where+= " ) ";

			}
			if(targetCodes != null){

				String targetCondition = "";


				for(String targetString : targetCodes){

					if (targetString == null) continue;

					if(!targetString.equals("")){

						if(!targetCondition.equals("")){
							targetCondition += " OR ";
						}

						else{
							if (!where.equals("")) where += " AND ";

							targetCondition += " ( ";

						}

						targetCondition +=  " id_t = '"+targetString+"' ";

					}

				}

				where += targetCondition;
				if(!targetCondition.equals("")) where+= " ) ";

			}

			String affluences = "select centroids.name as sourceName, affluence.id_s as sourceId, destinations.name as destinationName, affluence.value as flowValue, period.description as periodDescription  from affluence join destinations on affluence.id_t = destinations.id join period on period.id = affluence.id_period join `centroids` on centroids.id = affluence.id_s where "+where+" ;";
			rs = stmt.executeQuery(affluences);
			while(rs.next()){

				String id_s = rs.getString("sourceName");
				String id_t = rs.getString("destinationName");
				int value = rs.getInt("flowValue");
				String periodDescription = rs.getString("periodDescription");


				DetailedInfoInterface currDetail = new DetailedInfoInterface(id_s, id_t, value, periodDescription);
				currDetail.sourceID =  rs.getString("sourceId");
				infos.add(currDetail);

			}

			rs.close();
			stmt.close();
			c.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}



		return infos;

	}


	public ArrayList<Region> regionsForDataset(String datasetidentifier){

		Connection c = null;
		Statement stmt = null;
		ArrayList<Region> regions = new ArrayList<Region>();

		String query = "SELECT id_s, name, isocode.iso3166v3 as iso3 FROM `centroids` join 'affluence' on centroids.id = id_s  join isocode on centroids.id = isocode.iso3166v2  where id_dataset = "+datasetidentifier+" group by id_s order by name";

		try {

			String dbPath = getDBFullPath();

			Class.forName("org.sqlite.JDBC");

			c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);		      
			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery(query);

			while(rs.next()){


				String id = rs.getString("id_s");
				String name = rs.getString("name");
				String iso3 = rs.getString("iso3");

				Region currRegion = new Region(id, iso3, name, name, 0.0, 0.0, SITE_TYPE.SOURCE);

				regions.add(currRegion);

			}

			rs.close();
			stmt.close();
			c.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}


		return regions;

	}


	public ArrayList<Period> periodsForDataset(String datasetidentifier){

		Connection c = null;
		Statement stmt = null;
		ArrayList<Period> periods = new ArrayList<Period>();

		String query = "SELECT period.id as periodid, period.description as periddescription, reference_1, reference_2, reference_3, year FROM `period` join 'affluence' on period.id = id_period where id_dataset = "+datasetidentifier+" group by periodid";

		try {

			String dbPath = getDBFullPath();

			Class.forName("org.sqlite.JDBC");

			c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);		      
			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery(query);

			while(rs.next()){

				Period currPeriod = new Period();

				String id = rs.getString("periodid");
				String description = rs.getString("periddescription");
				String reference_1 =  rs.getString("reference_1");
				String reference_2 =  rs.getString("reference_2");
				String reference_3 =  rs.getString("reference_3");
				String year =  rs.getString("year");

				currPeriod.identifier = id;
				currPeriod.description = description;
				currPeriod.reference_1 = reference_1;
				currPeriod.reference_2 = reference_2;
				currPeriod.reference_3 = reference_3;
				currPeriod.year = year;

				periods.add(currPeriod);

			}

			rs.close();
			stmt.close();
			c.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}


		return periods;

	}

	public ArrayList<Dataset> allDatasets(){

		Connection c = null;
		Statement stmt = null;
		ArrayList<Dataset> datasets = new ArrayList<Dataset>();

		String query = "SELECT * from dataset";

		try {

			String dbPath = getDBFullPath();

			Class.forName("org.sqlite.JDBC");

			c = DriverManager.getConnection("jdbc:sqlite:"+dbPath);		      
			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery(query);

			while(rs.next()){

				Dataset currDataset = new Dataset();

				String id = rs.getString("id");
				String name = rs.getString("name");
				String description = rs.getString("description");

				currDataset.identifier = id;
				currDataset.name = name;
				currDataset.description = description;

				datasets.add(currDataset);

			}

			rs.close();
			stmt.close();
			c.close();

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}


		return datasets;
	}

}
