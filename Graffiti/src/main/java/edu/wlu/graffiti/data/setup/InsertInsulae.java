package edu.wlu.graffiti.data.setup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class InsertInsulae {

	private static final String DELIMITER = ";";

	private static final String INSERT_PROPERTY_STMT = "INSERT INTO insula " + "(modern_city, short_name, full_name) VALUES (?,?,?)";

	static Connection newDBCon;

	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;

	public static void main(String[] args) {
		init();

		try {
			insertProperties("data/herculaneum_insulae.csv");
			insertProperties("data/pompeii_insulae.csv");
			newDBCon.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	private static void insertProperties(String datafileName) {
		try {
			PreparedStatement pstmt = newDBCon.prepareStatement(INSERT_PROPERTY_STMT);

			BufferedReader br = new BufferedReader(new FileReader(datafileName));

			String line;

			while ((line = br.readLine()) != null) {
				String[] data = line.split(DELIMITER);
				String modernCity = data[0];
				String insula = data[1];
				String fullname = data[2];

				pstmt.setString(1, modernCity);
				pstmt.setString(2, insula);
				pstmt.setString(3, fullname);
				try {
					System.out.println(modernCity + " " + insula);
					pstmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			br.close();
			pstmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
