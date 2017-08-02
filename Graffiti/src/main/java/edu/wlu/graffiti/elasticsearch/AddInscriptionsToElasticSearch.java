package edu.wlu.graffiti.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.data.rowmapper.InscriptionRowMapper;
import edu.wlu.graffiti.data.rowmapper.PropertyRowMapper;
import edu.wlu.graffiti.data.setup.Utils;

/**
 * This class gathers the inscriptions from the database and indexes them in the
 * Elasticsearch node
 * 
 * Run this file whenever there are changes made to the database to reflect the
 * changes in the Elasticsearch index
 * 
 * @author whitej
 * @author sprenkle - refactored to decrease duplicate code with row mapping;
 *         revised to use the updated DB schema; updated for Elasticsearch 2.x
 * @author cooperbaird - refactored to pull a lot of Elasticsearch stuff out and upgraded to
 *         Elasticsearch 5.x
 *
 */
public class AddInscriptionsToElasticSearch {

	private static String DB_PASSWORD;
	private static String DB_USER;
	private static String DB_DRIVER;

	private static String DB_URL;

	private static final String SELECT_ALL_INSCRIPTIONS = GraffitiDao.SELECT_STATEMENT;

	private static Connection newDBCon;

	private static final InscriptionRowMapper INSCRIPTION_ROW_MAPPER = new InscriptionRowMapper();
	private static final PropertyRowMapper PROPERTY_ROW_MAPPER = new PropertyRowMapper();
	
	@Autowired
    private static ElasticsearchTemplate esTemplate;
	
	@Autowired
    private static InscriptionService inscriptionService;
	
	/**
	 * Gathers all inscriptions from the database and maps the result set to
	 * inscription objects. Deletes all current documents from the Elasticsearch
	 * node. Indexes each inscription as a JSON document
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		init();

		try {
			createIndexAnalyzerMapping();

			PreparedStatement getInscriptions = newDBCon.prepareStatement(SELECT_ALL_INSCRIPTIONS);
			PreparedStatement getProperty = newDBCon.prepareStatement(FindspotDao.SELECT_BY_PROPERTY_ID_STATEMENT);

			ResultSet rs = getInscriptions.executeQuery();

			int rowNum = 0;
			int count = 0;

			List<Inscription> inscriptions = new ArrayList<Inscription>();

			while (rs.next()) {
				Inscription i = INSCRIPTION_ROW_MAPPER.mapRow(rs, rowNum);

				getProperty.setInt(1, i.getAgp().getProperty().getId());

				ResultSet propResults = getProperty.executeQuery();
				if (propResults.next()) {
					Property property = PROPERTY_ROW_MAPPER.mapRow(propResults, 1);
					i.getAgp().setProperty(property);
				}
				inscriptions.add(i);
				propResults.close();
				rowNum++;

				inscriptionService.save(i);
				count++;
			}
			
			rs.close();
			getInscriptions.close();
			newDBCon.close();

			System.out.println("Looking at " + rowNum + " inscriptions");
			System.out.println(count + " documents indexed");
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Implements a custom analyzer to fold special characters (like accents) to unicode,
	 * to stem english words, and to strip punctuation. Then, the index is created and mapped.
	 * @throws IOException 
	 */
	private static void createIndexAnalyzerMapping() throws IOException {
		XContentBuilder settingsBuilder = jsonBuilder()
				.startObject()
					.startObject("analysis")
						.startObject("filter")
							.startObject("punct_remove")
								.field("type", "pattern_replace")
								.field("pattern", "\\p{Punct}")
								.field("replacement", "")
							.endObject()
							.startObject("english_stop")
								.field("type", "stop")
								.field("stopwords", "_english_")
							.endObject()
							.startObject("light_english_stemmer")
								.field("type", "stemmer")
								.field("language", "light_english")
							.endObject()
							.startObject("english_possessive_stemmer")
								.field("type", "stemmer")
								.field("language", "possessive_english")
							.endObject()
						.endObject()
						.startObject("tokenizer")
							.startObject("custom_tokenizer")
								.field("type", "pattern")
								.field("pattern", "\\s|-(?![^\\[]*\\])") // splits at whitespace, and hyphen if not in square brackets
							.endObject()
						.endObject()
						.startObject("analyzer")
							.startObject("folding")
								.field("type", "custom")
								.field("tokenizer", "custom_tokenizer")
								.field("filter", new String[]{"punct_remove", "english_possessive_stemmer","lowercase", 
										"english_stop", "light_english_stemmer", "icu_folding"})
							.endObject()
						.endObject()
					.endObject()
				.endObject();
		
		esTemplate.deleteIndex(Inscription.class);
		esTemplate.createIndex(Inscription.class, settingsBuilder);
		esTemplate.putMapping(Inscription.class);
		esTemplate.refresh(Inscription.class);
	}

	public static void getConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
	}

	/**
	 * initializes the database and Elasticsearch connections
	 * 
	 * @throws IOException
	 */
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

	/**
	 * Creates the mapping for the inscription type. Doesn't analyze the fields
	 * with special characters that require an exact match
	 * 
	 * @throws IOException
	 */
	/*
	private static void createMapping() throws IOException {
		XContentBuilder mapping = jsonBuilder().startObject().startObject(ES_TYPE_NAME).startObject("properties")
				.startObject("id").field("type", "long").endObject().startObject("city").field("type", "keyword")
				.endObject().startObject("insula").startObject("properties").startObject("insula_id")
				.field("type", "long").endObject().startObject("insula_name").field("type", "text").endObject()
				.endObject().endObject().startObject("property") // property
				.startObject("properties").startObject("property_id").field("type", "long").endObject()
				.startObject("property_name").field("type", "text").endObject().startObject("property_number")
				.field("type", "text").endObject().startObject("property_types").field("type", "integer").endObject()
				.endObject().endObject().startObject("drawing") // drawing
				.startObject("properties").startObject("description_in_english").field("type", "text").endObject()
				.startObject("description_in_latin").field("type", "text").endObject().startObject("drawing_tags")
				.field("type", "text").endObject().startObject("drawing_tag_ids").field("type", "integer").endObject()
				.endObject().endObject().startObject("writing_style_in_english").field("type", "keyword")
				.endObject().startObject("language_in_english").field("type", "keyword") 
				.endObject().startObject("content").field("analyzer", "folding").field("type", "text").endObject()
				.startObject("summary").field("type", "text").endObject()
				.startObject("edr_id").field("store", "true").field("type", "keyword").endObject()
				.startObject("bibliography").field("type", "text").endObject()
				.startObject("cil").field("type", "text").endObject().startObject("comment").field("type", "text").endObject()
				.startObject("content_translation").field("analyzer", "folding").field("type", "text").endObject()
				.startObject("description_in_english").field("type", "text").endObject()
				.startObject("measurements").field("type", "text").endObject().endObject().endObject().endObject();

		client.admin().indices().preparePutMapping(ES_INDEX_NAME).setType(ES_TYPE_NAME).setSource(mapping).get();
	}
	*/
}