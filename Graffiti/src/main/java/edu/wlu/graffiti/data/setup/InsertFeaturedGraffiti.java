package edu.wlu.graffiti.data.setup;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.wlu.graffiti.dao.ThemeDao;

/**
 * 
 * @author Hammad Ahmad
 *
 */
public class InsertFeaturedGraffiti {

	private static final String INSERT_FEATURED_GRAFFITI = "INSERT INTO graffititothemes " + "(graffito_id, theme_id) "
			+ "VALUES (?, ?)";
	private static final String SET_INSCRIPTION_AS_THEMED = "UPDATE agp_inscription_info " + "SET is_themed=true " + "where edr_id=(?)";
	private static final String GET_THEME_ID = "SELECT theme_id FROM themes WHERE name = ?";
	
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;
	static Connection newDBCon;

	public static void main(String[] args) {
		init();

		try {
			insertFeaturedGraffiti();

			newDBCon.close();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	private static void insertFeaturedGraffiti() {
		try {
			PreparedStatement pstmt = newDBCon.prepareStatement(INSERT_FEATURED_GRAFFITI);
			PreparedStatement pstmt2 = newDBCon.prepareStatement(SET_INSCRIPTION_AS_THEMED);
			PreparedStatement pstmt3 = newDBCon.prepareStatement(GET_THEME_ID);
			
			Properties featuredGraffiti = new Properties();

			featuredGraffiti.load(new FileReader("data/AGPData/featuredGraffiti.prop"));
			Enumeration<Object> propKeys = featuredGraffiti.keys();

			while (propKeys.hasMoreElements()) {

				Object edrId = propKeys.nextElement();
				Object theme = featuredGraffiti.get(edrId);
				System.out.println(edrId + ": " + theme);
				
				pstmt3.setString(1, (String) theme);
				ResultSet rs = pstmt3.executeQuery();
				int themeId = -1;
				while(rs.next()) {
					themeId = rs.getInt("theme_id");
				}
				
				if(themeId != -1) {
					pstmt.setString(1, (String) edrId);
					pstmt.setInt(2, themeId);
					pstmt.executeUpdate();
					
					pstmt2.setString(1, (String) edrId);
					pstmt2.executeUpdate();
				}
			}
			pstmt.close();
		} catch (IOException | SQLException e) {
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
