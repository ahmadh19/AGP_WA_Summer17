package edu.wlu.graffiti.data.setup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.annotation.Resource;

import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This program translates each property and it's features from the SQL database
 * and json shape files to pompeiiPropertyData.txt,pompeiiPropertyData.js,
 * herculaneumPropertyData.txt, and herculaneumPropertyData.js. Then, the data
 * can be used to efficiently provide data to geoJson for use in the maps of
 * Pompeii and Heracleum.
 * 
 * @author Alicia Martinez - v1.0
 * @author Kelly McCaffrey -Created functionality for getting the number of
 *         Graffiti and automating the process of copying to
 *         pompeiiPropertyData.js -Also added all functionality for the
 *         Herculaneum map.
 * @author Sara Sprenkle - refactored code to make it easier to change later;
 */

public class StorePropertiesFromDatabaseForgeoJsonMap {

	private static final String POMPEII_JAVASCRIPT_DATA_FILE_LOC = "src/main/webapp/resources/js/pompeiiPropertyData.js";
	private static final String POMPEII_INIT_JAVASCRIPT_LOC = "src/main/webapp/resources/js/PropertyDataFirst.txt";
	private static final String POMPEII_GEOJSON_FILE_LOC = "src/main/resources/geoJSON/eschebach.json";

	final static String SELECT_PROPERTY = FindspotDao.SELECT_BY_CITY_AND_INSULA_AND_PROPERTY_STATEMENT;

	final static String GET_NUMBER = GraffitiDao.FIND_BY_PROPERTY;

	final static String GET_PROPERTY_TYPE = "SELECT * FROM properties, propertytypes,"
			+ " propertytopropertytype WHERE properties.id = propertytopropertytype.property_id"
			+ " AND propertytypes.id = propertytopropertytype.property_type AND properties.id = ?";
	static Connection dbCon;

	private static PreparedStatement selectPropertyStatement;
	private static PreparedStatement getPropertyTypeStatement;
	private static PreparedStatement getNumberStatement;

	@Resource
	private static GraffitiDao graffitiDaoObject;

	public static void main(String args[]) throws JsonProcessingException, IOException {

		init();

		// storeHerculaneum();

		storePompeii();

		copyToJavascriptFiles();

	}

	private static void init() {

		// Sets database url using the configuration file.
		Properties prop = Utils.getConfigurationProperties();
		try {
			Class.forName(prop.getProperty("db.driverClassName"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			dbCon = DriverManager.getConnection(prop.getProperty("db.url"), prop.getProperty("db.user"),
					prop.getProperty("db.password"));
			selectPropertyStatement = dbCon.prepareStatement(SELECT_PROPERTY);
			getPropertyTypeStatement = dbCon.prepareStatement(GET_PROPERTY_TYPE);
			getNumberStatement = dbCon.prepareStatement(GET_NUMBER);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Stores the data for Herculaneum in herculaneumPropertyData.txt
	 */
	private static void storeHerculaneum() {
		try {
			// creates the file we will later write the updated graffito to
			// This will write to updated eschebach.txt(where the graffito is
			// the combination of properties with attributes)
			// Want to add number of graffiti to this.

			PrintWriter herculaneumTextWriter = new PrintWriter(
					"src/main/webapp/resources/js/herculaneumPropertyData.txt", "UTF-8");

			ObjectMapper herculaneumMapper = new ObjectMapper();
			JsonFactory herculaneumJsonFactory = new JsonFactory();
			// JsonParser herculaneumJsonParser =
			// herculaneumJsonFactory.createParser(new
			// File("src/main/resources/geoJSON/herculaneum.json"));
			JsonParser herculaneumJsonParser = herculaneumJsonFactory
					.createParser(new File("src/main/resources/geoJSON/herculaneumTest.json"));

			JsonNode herculaneumRoot = herculaneumMapper.readTree(herculaneumJsonParser);
			JsonNode herculaneumFeaturesNode = herculaneumRoot.path("features");

			Iterator<JsonNode> herculaneumIterator = herculaneumFeaturesNode.elements();

			while (herculaneumIterator.hasNext()) {
				JsonNode field = herculaneumIterator.next();
				String fieldText = field.toString();

				// System.out.println("Herc field text "+fieldText);

				// converts the above string into an InputStream so I can use it
				// in
				// the json parser to iterate through the different tokens
				InputStream stream = new ByteArrayInputStream(fieldText.getBytes(StandardCharsets.UTF_8));

				JsonParser parseLine = herculaneumJsonFactory.createParser(stream);

				while (parseLine.nextToken() != JsonToken.END_OBJECT) {
					String fieldname = parseLine.getCurrentName();

					// System.out.println("Herculaneum fieldname: "+fieldname);

					// System.out.println("PRIMARY_DO");

					// when the token is the PRIMARY_DO field, we go to the next
					// token and that is the value
					// if("PRIMARY_DO".equals(fieldname)) {
					if ("PRIMARY_DO".equals(fieldname)) {
						parseLine.nextToken();
						String primarydo = parseLine.getText();
						// System.out.println("Herculaneum primarydo:
						// "+primarydo);
						if (!primarydo.contains(".")) {
							continue;
						}
						String[] parts = primarydo.split("\\.");

						String pt1 = parts[0];
						String pt2 = parts[1];
						String pt3 = parts[2];

						String insulaName = pt1 + "." + pt2;
						String propertyNum = pt3;
						try {
							selectPropertyStatement.setString(1, "Herculaneum");
							selectPropertyStatement.setString(2, insulaName);
							selectPropertyStatement.setString(3, propertyNum);

							ResultSet rs = selectPropertyStatement.executeQuery();

							if (rs.next()) {
								int propertyId = rs.getInt("id");

								String propertyName = rs.getString("property_name");
								String addProperties = rs.getString("additional_properties");
								String italPropName = rs.getString("italian_property_name");
								String insulaDescription = rs.getString("description");
								String insulaPleiadesId = rs.getString("insula_pleiades_id");
								String propPleiadesId = rs.getString("property_pleiades_id");

								getNumberStatement.setInt(1, propertyId);
								ResultSet numberOnPropResultSet = getNumberStatement.executeQuery();
								int numberOfGraffitiOnProperty = 0;
								if (numberOnPropResultSet.next()) {
									numberOfGraffitiOnProperty = numberOnPropResultSet.getInt(1);
								}

								getPropertyTypeStatement.setInt(1, propertyId);
								ResultSet resultset = getPropertyTypeStatement.executeQuery();
								String propertyType = "";
								if (resultset.next()) {
									propertyType = resultset.getString("name");
								}

								ObjectNode graffito = (ObjectNode) field;
								ObjectNode properties = (ObjectNode) graffito.path("properties");

								properties.put("Property_Id", propertyId);
								properties.put("Number_Of_Graffiti", numberOfGraffitiOnProperty);
								properties.put("Property_Name", propertyName);
								properties.put("Additional_Properties", addProperties);
								properties.put("Italian_Property_Name", italPropName);
								properties.put("Insula_Description", insulaDescription);
								properties.put("Insula_Pleiades_Id", insulaPleiadesId);
								properties.put("Property_Pleiades_Id", propPleiadesId);
								properties.put("Property_Type", propertyType);

								JsonNode updatedProps = (JsonNode) properties;
								graffito.set("properties", updatedProps);

								// write the newly updated graffito to text file
								// System.out.println(graffito);

								// jsWriter.println(graffito+",");
								herculaneumTextWriter.println(graffito + ",");
								// readFirstEsch.close();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
			herculaneumTextWriter.close();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parses the data file Stores the data for Pompeii in
	 * herculaneumPropertyData.txt
	 * 
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	private static void storePompeii() throws JsonProcessingException, IOException {

		try {
			PrintWriter pompeiiTextWriter = new PrintWriter("src/main/webapp/resources/js/pompeiiPropertyData.txt",
					"UTF-8");

			// creates necessary objects to parse the original GeoJSON file
			ObjectMapper pompeiiMapper = new ObjectMapper();
			JsonFactory pompeiiJsonFactory = new JsonFactory();
			JsonParser pompeiiJsonParser = pompeiiJsonFactory.createParser(new File(POMPEII_GEOJSON_FILE_LOC));
			// this accesses the 'features' level of the GeoJSON document
			JsonNode pompeiiRoot = pompeiiMapper.readTree(pompeiiJsonParser);
			JsonNode pompeiiFeaturesNode = pompeiiRoot.path("features");

			// iterates over the features node
			Iterator<JsonNode> featureIterator = pompeiiFeaturesNode.elements();

			while (featureIterator.hasNext()) {
				JsonNode featureNode = featureIterator.next();

				JsonNode primaryDONode = featureNode.findValue("PRIMARY_DO");
				if (primaryDONode == null) {
					System.out.println("No PRIMARY_DO in " + featureNode);
					continue;
				}

				String primaryDO = primaryDONode.textValue();
				if (primaryDO == null || !primaryDO.contains(".")) {
					System.out.println("Problem with primaryDO?: " + primaryDO);
					continue;
				}

				String[] parts = primaryDO.split("\\.");

				String pt1 = parts[0];
				String pt2 = parts[1];
				String pt3 = parts[2];

				String insulaName = pt1 + "." + pt2;
				String propertyNum = pt3;

				// Parse the geometry and get rid of the z coordinates
				Polygon p = parseGeometryAndRemoveCoordinates(featureNode);

				try {
					selectPropertyStatement.setString(1, "Pompeii");
					selectPropertyStatement.setString(2, insulaName);
					selectPropertyStatement.setString(3, propertyNum);

					ResultSet rs = selectPropertyStatement.executeQuery();

					if (rs.next()) {
						int propertyId = rs.getInt("id");

						String propertyName = rs.getString("property_name");
						String addProperties = rs.getString("additional_properties");
						String italPropName = rs.getString("italian_property_name");
						String insulaDescription = rs.getString("description");
						String insulaPleiadesId = rs.getString("insula_pleiades_id");
						String propPleiadesId = rs.getString("property_pleiades_id");

						getNumberStatement.setInt(1, propertyId);
						ResultSet numberOnPropResultSet = getNumberStatement.executeQuery();
						int numberOfGraffitiOnProperty = 0;
						if (numberOnPropResultSet.next()) {
							numberOfGraffitiOnProperty = numberOnPropResultSet.getInt(1);
						}

						getPropertyTypeStatement.setInt(1, propertyId);
						ResultSet resultset = getPropertyTypeStatement.executeQuery();
						String propertyType = "";
						if (resultset.next()) {
							propertyType = resultset.getString("name");
						}

						ObjectNode graffito = (ObjectNode) featureNode;
						ObjectNode properties = (ObjectNode) graffito.path("properties");
						properties.put("Property_Id", propertyId);
						properties.put("Number_Of_Graffiti", numberOfGraffitiOnProperty);
						properties.put("Property_Name", propertyName);
						properties.put("Additional_Properties", addProperties);
						properties.put("Italian_Property_Name", italPropName);
						properties.put("Insula_Description", insulaDescription);
						properties.put("Insula_Pleiades_Id", insulaPleiadesId);
						properties.put("Property_Pleiades_Id", propPleiadesId);
						properties.put("Property_Type", propertyType);

						JsonNode updatedProps = (JsonNode) properties;
						graffito.set("properties", updatedProps);

						pompeiiTextWriter.println(graffito + ",");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			pompeiiTextWriter.close();
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static Polygon parseGeometryAndRemoveCoordinates(JsonNode featureNode) {
		JsonNode geometryNode = featureNode.findValue("geometry");
		JsonParser coordParse = geometryNode.traverse();
		Polygon p = null;

		GeoJsonObject object;
		try {
			object = new ObjectMapper().readValue(coordParse, GeoJsonObject.class);

			if (object instanceof Polygon) {
				p = (Polygon) object;
				System.out.println(p.getCoordinates());
				List<List<LngLatAlt>> newCoordList = new ArrayList<List<LngLatAlt>>();
				for (List<LngLatAlt> coordList : p.getCoordinates()) {
					List<LngLatAlt> aList = new ArrayList<LngLatAlt>();
					for (LngLatAlt coord : coordList) {
						System.out.println(coord);
						LngLatAlt newCoord = new LngLatAlt(coord.getLongitude(), coord.getLatitude());
						aList.add(newCoord);
					}
					newCoordList.add(aList);
				}
				p.setCoordinates(newCoordList);
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * Iterates through all of the coordinates in a JSON Geometry object and
	 * removes the last(Z-coords) in each part of the list. Designed only for
	 * geoJson polygons with coordinates in "circle" form(?!)
	 * 
	 * @param geometryObject
	 * @return
	 */
	private static ObjectNode removeZCoordinates(ObjectNode geometryObject) {
		System.out.println("Geometry object received by function: " + geometryObject);
		ObjectNode coordinates = (ObjectNode) geometryObject.path("Type");
		System.out.println("Here are the coordinates: " + coordinates);
		return geometryObject;
	}

	/**
	 * An independent function for copying from pompeiiPropertyData.txt to
	 * pompeiiPropertyData.js with necessary js-specific components. Copies the
	 * data from pompeiiPropertyData.txt to updateEschebach.js in between the [
	 * ] First, creates and writes to a textFile. Then, saves it as a .js file
	 * by renaming it.
	 * 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private static void copyToJavascriptFiles() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter pompeiiJsWriter = new PrintWriter(POMPEII_JAVASCRIPT_DATA_FILE_LOC, "UTF-8");
		// Writes the beginning part of the js file, which it fetches from
		// another text file called jsEschebachFirst.txt.
		// This is the only way I found to format this part of the javascript.
		File jsFirst = new File(POMPEII_INIT_JAVASCRIPT_LOC);
		Scanner jsReadFirst = new Scanner(jsFirst);
		while (jsReadFirst.hasNext()) {
			String content = jsReadFirst.nextLine();
			pompeiiJsWriter.println(content);
		}

		// Copies from pompeiiPropertyData.txt to pompeiiPropertyData.js for the
		// body portion of the file.
		File pompeiiUpdatedPText = new File("src/main/webapp/resources/js/pompeiiPropertyData.txt");
		Scanner pompeiiReadFromText = new Scanner(pompeiiUpdatedPText);
		String pompeiiContent;
		while (pompeiiReadFromText.hasNext()) {
			pompeiiContent = pompeiiReadFromText.nextLine();
			pompeiiJsWriter.println(pompeiiContent);
		}

		PrintWriter herculaneumJsWriter = new PrintWriter("src/main/webapp/resources/js/herculaneumPropertyData.js",
				"UTF-8");
		jsFirst = new File(POMPEII_INIT_JAVASCRIPT_LOC);
		jsReadFirst = new Scanner(jsFirst);
		while (jsReadFirst.hasNext()) {
			String content = jsReadFirst.nextLine();
			// herculaneumJsWriter.println(content);
		}

		// Copies from herculaneumPropertyData.txt to herculaneumPropertyData.js
		// for the body portion of the file.
		File herculaneumUpdatedPText = new File("src/main/webapp/resources/js/herculaneumPropertyData.txt");
		Scanner herculaneumReadFromText = new Scanner(herculaneumUpdatedPText);
		String herculaneumContent;
		while (herculaneumReadFromText.hasNext()) {
			herculaneumContent = herculaneumReadFromText.nextLine();
			herculaneumJsWriter.println(herculaneumContent);
		}

		herculaneumJsWriter.println("]};");
		herculaneumReadFromText.close();
		herculaneumJsWriter.close();

		pompeiiJsWriter.println("]};");
		jsReadFirst.close();
		pompeiiReadFromText.close();
		pompeiiJsWriter.close();
	}

}
