package edu.wlu.graffiti.data.setup;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Property;

/**
 * Import the data from EDR
 * 
 * (Next: handle updates)
 * 
 * @author Sara Sprenkle
 *
 */
public class ImportEDRData {

	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;

	private static final String INSERT_INSCRIPTION_STATEMENT = "INSERT INTO edr_inscriptions "
			+ "(edr_id, ancient_city, find_spot, measurements, writing_style, \"language\", date_of_origin) "
			+ "VALUES (?,?,?,?,?,?,?)";

	private static final String UPDATE_INSCRIPTION_STATEMENT = "UPDATE edr_inscriptions SET "
			+ "ancient_city=?, find_spot=?, measurements=?, writing_style=?, \"language\"=?, date_of_origin=?"
			+ "WHERE edr_id = ?";

	private static final String CHECK_INSCRIPTION_STATEMENT = "SELECT COUNT(*) FROM edr_inscriptions"
			+ " WHERE edr_id = ?";

	private static final String INSERT_AGP_METADATA = "INSERT INTO agp_inscription_info (edr_id) " + "VALUES (?)";

	private static final String UPDATE_PROPERTY = "UPDATE agp_inscription_info SET "
			+ "property_id = ? WHERE edr_id = ?";

	private static final String UPDATE_CONTENT = "UPDATE edr_inscriptions SET " + "content = ? WHERE edr_id = ?";

	private static final String UPDATE_BIB = "UPDATE edr_inscriptions SET " + "bibliography = ? WHERE edr_id = ?";

	private static final String UPDATE_APPARATUS = "UPDATE edr_inscriptions SET " + "apparatus = ? WHERE edr_id = ?";

	// private static final String UPDATE_DESCRIPTION = "UPDATE
	// agp_inscription_annotations SET "
	// + "description = ? WHERE edr_id = ?";

	private static final String SELECT_INSULA_AND_PROPERTIES = "select *, insula.id as insula_id, properties.id as property_id from insula, properties where insula_id = insula.id";

	private static final String INSERT_PHOTO_STATEMENT = "INSERT INTO photos (edr_id, photo_id) " + "VALUES (?, ?)";

	private static Connection dbCon;

	private static PreparedStatement insertAGPMetaStmt;
	private static PreparedStatement insertPStmt;

	private static PreparedStatement updatePStmt;
	private static PreparedStatement selPStmt;
	private static PreparedStatement updatePropertyStmt;
	private static PreparedStatement updateContentStmt;
	private static PreparedStatement updateBibStmt;
	private static PreparedStatement updateApparatusStmt;
	private static PreparedStatement insertPhotoStmt;

	private static Map<String, HashMap<String, Insula>> cityToInsulaMap;

	private static Map<Integer, HashMap<String, Property>> insulaToPropertyMap;

	private static List<Pattern> patternList;

	public static void main(String[] args) {
		init();

		try {
			readPropertiesAndInsula();
			updateInscriptions("data/EDRData/epigr.csv");
			updateContent("data/EDRData/testo_epigr.csv");
			updateBibliography("data/EDRData/editiones.csv");
			updateApparatus("data/EDRData/apparatus.csv");
			updatePhotoInformation("data/EDRData/foto.csv");
			dbCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void readPropertiesAndInsula() throws SQLException {
		System.out.println("Reading in properties and insula");
		cityToInsulaMap = new HashMap<String, HashMap<String, Insula>>();
		insulaToPropertyMap = new HashMap<Integer, HashMap<String, Property>>();

		Statement infoStmt = dbCon.createStatement();

		ResultSet rs = infoStmt.executeQuery(SELECT_INSULA_AND_PROPERTIES);

		while (rs.next()) {
			String modernCity = rs.getString("modern_city");
			String insName = rs.getString("short_name");
			String propNum = rs.getString("property_number");
			String propName = rs.getString("property_name");
			int insID = rs.getInt("insula_id");
			int propID = rs.getInt("property_id");

			if (!cityToInsulaMap.containsKey(modernCity)) {
				cityToInsulaMap.put(modernCity, new HashMap<String, Insula>());
			}
			Insula ins = new Insula(insID, modernCity, insName, "");
			cityToInsulaMap.get(modernCity).put(insName, ins);

			Property p = new Property(propID, propNum, propName, ins);

			if (!insulaToPropertyMap.containsKey(insID)) {
				insulaToPropertyMap.put(insID, new HashMap<String, Property>());
			}

			insulaToPropertyMap.get(insID).put(propNum, p);

		}
		rs.close();
		infoStmt.close();

		for (String city : cityToInsulaMap.keySet()) {
			System.out.println("city: " + city);
			for (String s : cityToInsulaMap.get(city).keySet()) {
				System.out.println("    - " + s + ": " + cityToInsulaMap.get(city).get(s));
			}
		}

	}

	/**
	 * Update the apparatus information in each of the inscription entries.
	 * 
	 * @param apparatusFileName
	 */
	private static void updateApparatus(String apparatusFileName) {
		String eagleID = "";
		try {
			Reader in = new FileReader(apparatusFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				eagleID = Utils.cleanData(record.get(0));
				String apparatus = Utils.cleanData(record.get(1));

				try {

					updateApparatusStmt.setString(1, apparatus);
					updateApparatusStmt.setString(2, eagleID);

					int updated = updateApparatusStmt.executeUpdate();
					if (updated != 1) {
						System.err.println("\nSomething went wrong with " + eagleID);
						System.err.println(apparatus);
					}
				} catch (SQLException e) {

					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("\nSomething went wrong with " + eagleID);
		}
	}

	private static void updateBibliography(String bibFileName) {
		try {
			Reader in = new FileReader(bibFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = Utils.cleanData(record.get(0));
				String bib = Utils.cleanData(record.get(1));

				try {
					updateBibStmt.setString(1, bib);
					updateBibStmt.setString(2, eagleID);

					int updated = updateBibStmt.executeUpdate();
					if (updated != 1) {
						System.err.println("\nSomething went wrong with " + eagleID);
						System.err.println(bib);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void updateContent(String contentFileName) {

		try {
			Reader in = new FileReader(contentFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = Utils.cleanData(record.get(0));
				String content = Utils.cleanData(record.get(1));

				try {
					content = cleanContent(content);

					updateContentStmt.setString(1, content);
					updateContentStmt.setString(2, eagleID);

					int updated = updateContentStmt.executeUpdate();
					if (updated != 1) {
						System.err.println("\nSomething went wrong with " + eagleID);
						System.err.println(content);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Replaces the <br>
	 * tags with newlines. We handle the line breaks in our code.
	 * 
	 * @param content
	 * @return
	 */
	private static String cleanContent(String content) {
		return content.replace("<br>", "\n");
	}

	private static void updateInscriptions(String datafileName) {
		try {

			Reader in = new FileReader(datafileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = Utils.cleanData(record.get(0));
				String ancient_city = Utils.cleanData(record.get(3));
				String findSpot = Utils.cleanData(record.get(5));
				String dateOfOrigin = Utils.cleanData(record.get(17));
				String alt = Utils.cleanData(record.get(18));
				String lat = Utils.cleanData(record.get(19));
				String littAlt = Utils.cleanData(record.get(21));

				String measurements = createMeasurementField(alt, lat, littAlt);

				String writingStyle = Utils.cleanData(record.get(10));
				String language = Utils.cleanData(record.get(11));

				selPStmt.setString(1, eagleID);

				ResultSet rs = selPStmt.executeQuery();

				int count = 0;

				if (rs.next()) {
					count = rs.getInt(1);
				} else {
					System.err.println(eagleID + ":\nSomething went wrong with the SELECT statement!");
				}

				if (count == 0) {
					insertEagleInscription(eagleID, ancient_city, findSpot, measurements, writingStyle, language,
							dateOfOrigin);
				} else {
					updateEagleInscription(eagleID, ancient_city, findSpot, measurements, writingStyle, language,
							dateOfOrigin);
				}

				// update AGP Metadata
				updateAGPMetadata(eagleID, ancient_city, findSpot);

			}

			in.close();
			insertPStmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the AGP Metadata for this inscription
	 * 
	 * @param eagleID
	 * @param ancient_city
	 * @param findSpot
	 * @throws SQLException
	 */
	private static void updateAGPMetadata(String eagleID, String ancient_city, String findSpot) throws SQLException {

		insertAGPMetaStmt.setString(1, eagleID);

		try {
			insertAGPMetaStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String address = convertFindSpotToAddress(findSpot);

		// TODO
		// we're going to skip these because I can't handle them yet.

		if (!address.contains(".")) {
			System.err.println(eagleID + ": Couldn't handle address " + address);
			return;
		}

		String insula = "";
		String propertyNum = "";

		if (ancient_city.equals("Pompeii")) {
			insula = address.substring(0, address.lastIndexOf('.'));
			propertyNum = address.substring(address.lastIndexOf('.') + 1);
		} else {
			insula = address.substring(0, address.indexOf('.'));
			propertyNum = address.substring(address.lastIndexOf('.') + 1);
		}

		// Look up ids from the HashMaps
		// handle alternative spellings

		if (!cityToInsulaMap.containsKey(ancient_city)) {
			System.err.println();
			System.err.println(eagleID + ": " + ancient_city + "not found");
			return;
		}

		if (!cityToInsulaMap.get(ancient_city).containsKey(insula)) {
			System.err.println();
			System.err.println(eagleID + ": Insula " + insula + " not found in " + ancient_city + ", " + address);
			return;
		}

		int insulaID = cityToInsulaMap.get(ancient_city).get(insula).getId();

		if (!insulaToPropertyMap.get(insulaID).containsKey(propertyNum)) {
			System.err.println();
			System.err.println(eagleID + ": Property " + propertyNum + " in Insula " + insula + " in " + ancient_city + " not found");
			return;
		}

		int propertyID = insulaToPropertyMap.get(insulaID).get(propertyNum).getId();

		// update property info

		updatePropertyStmt.setInt(1, propertyID);
		updatePropertyStmt.setString(2, eagleID);

		int response = updatePropertyStmt.executeUpdate();

		if (response != 1) {
			System.err.println("WHAT? " + eagleID);
		}

	}

	/**
	 * Parses the findspot for the address
	 * 
	 * @param findSpot
	 * @return
	 */
	private static String convertFindSpotToAddress(String findSpot) {
		// Example: Pompei (Napoli) VII.12.18-20, Lupanare, cella b
		// Example: Ercolano (Napoli), Insula III.11, Casa del Tramezzo di Legno

		// Hack to handle Insula Orientalis special addresses
		if (findSpot.contains("Insula Orientalis ")) {
			findSpot = findSpot.replace("Insula Orientalis ", "InsulaOrientalis");
		}

		Matcher matcher = patternList.get(0).matcher(findSpot);
		if (matcher.matches()) {
			return matcher.group(1);
		}

		matcher = patternList.get(1).matcher(findSpot);
		if (matcher.matches()) {
			return matcher.group(2);
		} else {
			return findSpot;
		}
	}

	private static void insertEagleInscription(String eagleID, String ancient_city, String findSpot,
			String measurements, String writingStyle, String language, String date_of_origin) throws SQLException {
		insertPStmt.setString(1, eagleID);
		insertPStmt.setString(2, ancient_city);
		insertPStmt.setString(3, findSpot);
		insertPStmt.setString(4, measurements);
		insertPStmt.setString(5, writingStyle);
		insertPStmt.setString(6, language);
		insertPStmt.setString(7, date_of_origin);

		try {
			insertPStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void updateEagleInscription(String eagleID, String ancient_city, String findSpot,
			String measurements, String writingStyle, String language, String date_of_origin) throws SQLException {
		updatePStmt.setString(1, ancient_city);
		updatePStmt.setString(2, findSpot);
		updatePStmt.setString(3, measurements);
		updatePStmt.setString(4, writingStyle);
		updatePStmt.setString(5, language);
		updatePStmt.setString(6, date_of_origin);
		updatePStmt.setString(7, eagleID);

		try {
			updatePStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insert the photo information into database
	 * 
	 * @param string
	 */
	private static void updatePhotoInformation(String dataFileName) {
		try {
			Reader in = new FileReader(dataFileName);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {
				String eagleID = Utils.cleanData(record.get(0));
				String photoID = Utils.cleanData(record.get(21));
				insertPhotoInformation(eagleID, photoID);
			}

			in.close();
			insertPStmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void insertPhotoInformation(String eagleID, String photoID) throws SQLException {
		insertPhotoStmt.setString(1, eagleID);
		insertPhotoStmt.setString(2, photoID);

		try {
			insertPhotoStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static String createMeasurementField(String alt, String lat, String littAlt) {
		// Example: alt.: 2.50 lat.: 7.50 litt. alt.: 2-2,5

		StringBuffer sb = new StringBuffer();
		sb.append("height: ");
		sb.append(alt);
		sb.append(" width: ");
		sb.append(lat);
		sb.append(" letter height: ");
		sb.append(littAlt);

		return sb.toString();
	}

	private static void init() {
		getConfigurationProperties();

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			dbCon = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

			insertAGPMetaStmt = dbCon.prepareStatement(INSERT_AGP_METADATA);
			insertPStmt = dbCon.prepareStatement(INSERT_INSCRIPTION_STATEMENT);
			updatePStmt = dbCon.prepareStatement(UPDATE_INSCRIPTION_STATEMENT);
			selPStmt = dbCon.prepareStatement(CHECK_INSCRIPTION_STATEMENT);

			updatePropertyStmt = dbCon.prepareStatement(UPDATE_PROPERTY);
			updateContentStmt = dbCon.prepareStatement(UPDATE_CONTENT);
			// updateDescriptionStmt =
			// dbCon.prepareStatement(UPDATE_DESCRIPTION);
			updateBibStmt = dbCon.prepareStatement(UPDATE_BIB);
			updateApparatusStmt = dbCon.prepareStatement(UPDATE_APPARATUS);

			insertPhotoStmt = dbCon.prepareStatement(INSERT_PHOTO_STATEMENT);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		patternList = new ArrayList<Pattern>();
		patternList.add(Pattern.compile("^\\.* \\((\\w*\\.\\w*)\\) \\w*"));

		patternList
				.add(Pattern.compile("^\\w+ \\(\\w+\\),? ([\\w'.-]* )* ?\\(?([\\w'.-]*+)\\)?(,[\\w\\s-,'.\\(\\)]*)?"));
		// TODO: Need to update the pattern to handle Insula Orientalis I
	}

	public static void getConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
	}
}
