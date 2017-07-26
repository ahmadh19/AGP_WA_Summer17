package edu.wlu.graffiti.data.setup;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Inserts Description and Translations of figural graffiti from spreadsheet
 * into database
 * 
 * @author Sara Sprenkle
 *
 */
public class InsertDrawingDescriptionAndTranslations {
	
	// These are the locations of the data within the CSV file
	private static final int LOCATION_OF_EDR_ID = 0;
	private static final int LOCATION_OF_LATIN_DESCRIPTION = 10;
	private static final int LOCATION_OF_ENGLISH_DESCRIPTION = 11;

	private static final String UPDATE_DESCRIPTION = "UPDATE figural_graffiti_info SET description_in_english=? WHERE edr_id=?";
	private static final String UPDATE_DESCRIPTION_TRANSLATION = "UPDATE figural_graffiti_info SET description_in_latin=? WHERE edr_id=?";

	private static Connection newDBCon;
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;

	public static void main(String[] args) {
		init();

		try {
			updateTranslations("data/EDRData/herc_figural.csv");

			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void updateTranslations(String datafileName) {
		PreparedStatement descriptionUpdate = null;
		PreparedStatement translationStmt = null;

		Reader in = null;
		Iterable<CSVRecord> records;
		try {
			in = new FileReader(datafileName);
			records = CSVFormat.EXCEL.parse(in);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}

		try {
			descriptionUpdate = newDBCon.prepareStatement(UPDATE_DESCRIPTION);
			translationStmt = newDBCon.prepareStatement(UPDATE_DESCRIPTION_TRANSLATION);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (CSVRecord record : records) {
			String edrID = Utils.cleanData(record.get(LOCATION_OF_EDR_ID));
			String description_in_english = Utils.cleanData(record.get(LOCATION_OF_ENGLISH_DESCRIPTION));
			String description_in_latin = Utils.cleanData(record.get(LOCATION_OF_LATIN_DESCRIPTION));

			try {
				descriptionUpdate.setString(2, edrID);
				translationStmt.setString(2, edrID);

				descriptionUpdate.setString(1, description_in_english);
				translationStmt.setString(1, description_in_latin);

				descriptionUpdate.executeUpdate();
				translationStmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		try {
			descriptionUpdate.close();
			translationStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static void init() {
		getConfigurationProperties();

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			newDBCon = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void getConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
	}

}
