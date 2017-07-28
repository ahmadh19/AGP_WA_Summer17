/*
sudo * GraffitiController.java is the main backend controller of the Ancient Graffiti Project. It handles most of the
 * controls regarding the requests.
 */
package edu.wlu.graffiti.controller;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.wlu.graffiti.bean.DrawingTag;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.dao.DrawingTagsDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.PropertyTypesDao;
import edu.wlu.graffiti.data.setup.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(value="Graffiti", description="Operations pertaining to the graffiti.")
public class GraffitiController {

	@Resource
	// The @Resource injects an instance of the GraffitiDao at runtime. The
	// GraffitiDao instance is defined in graffiti-servlet.xml.
	private GraffitiDao graffitiDao;

	@Resource
	private DrawingTagsDao drawingTagsDao;

	@Resource
	private PropertyTypesDao propertyTypesDao;

	@Resource
	private FindspotDao findspotDao;

	@Resource
	private InsulaDao insulaDao;

	public static final String WRITING_STYLE_PARAM_NAME = "writing_style";
	public static final String WRITING_STYLE_SEARCH_DESC = "Writing Style";
	public static final String DRAWING_CATEGORY_SEARCH_DESC = "Drawing Category";
	public static final String PROPERTY_TYPE_SEARCH_DESC = "Property Type";

	public static final String WRITING_STYLE_GRAFFITI_INSCRIBED = "Graffito/incised";

	public static final String CITY_FIELD_NAME = "city";
	public static final String LANGUAGE_IN_ENGLISH_FIELD_NAME = "language_in_english";
	public static final String WRITING_STYLE_IN_ENGLISH_FIELD_NAME = "writing_style_in_english";
	public static final String PROPERTY_TYPES_FIELD_NAME = "property.property_types";
	public static final String PROPERTY_ID_FIELD_NAME = "property.property_id";
	public static final String INSULA_NAME_FIELD_NAME = "insula.insula_name";
	public static final String INSULA_ID_FIELD_NAME = "insula.insula_id";

	/** default size in elasticsearch is 10 */
	private static final int NUM_RESULTS_TO_RETURN = 2000;

	/** elastic search configuration properties */
	private static String ES_HOSTNAME;
	private static int ES_PORT_NUM;
	private static String ES_TYPE_NAME;
	private static String ES_INDEX_NAME;
	private static String ES_CLUSTER_NAME;

	@Resource
	private FindspotDao propertyDao;

	private TransportClient client;

	private Settings settings;

	private static String[] searchDescs = { "Content Keyword", "Global Keyword", "City", "Insula", "Property",
			PROPERTY_TYPE_SEARCH_DESC, DRAWING_CATEGORY_SEARCH_DESC, WRITING_STYLE_SEARCH_DESC, "Language" };

	private static String[] searchFields = { "content",
			"content content_translation summary city insula.insula_name property.property_name property.property_types"
					+ "cil description writing_style language edr_id bibliography"
					+ " drawing.description_in_english drawing.description_in_latin drawing.drawing_tags",
			CITY_FIELD_NAME, INSULA_ID_FIELD_NAME, PROPERTY_ID_FIELD_NAME, PROPERTY_TYPES_FIELD_NAME,
			"drawing.drawing_tag_ids", WRITING_STYLE_IN_ENGLISH_FIELD_NAME, LANGUAGE_IN_ENGLISH_FIELD_NAME };

	private Properties prop = null;

	private void init() {
		if (prop == null) {
			prop = Utils.getConfigurationProperties();
			ES_HOSTNAME = prop.getProperty("es.loc");
			ES_PORT_NUM = Integer.parseInt(prop.getProperty("es.port"));
			ES_INDEX_NAME = prop.getProperty("es.index");
			ES_TYPE_NAME = prop.getProperty("es.type");
			ES_CLUSTER_NAME = prop.getProperty("es.cluster_name");
		}
		settings = Settings.builder().put("cluster.name", ES_CLUSTER_NAME).build();
	}

	// Maps to the search.jsp page currently receives information from
	// regions.txt file for the dropdown menu
	// that holds the information for each property in an ancient city (i.e.
	// Pompeii)
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String searchForm(final HttpServletRequest request) {

		String city = request.getParameter("city");
		String message;
		HttpSession s = request.getSession();

		if (city != null && !city.isEmpty()) {
			if (city.toLowerCase().equals("pompeii")) {
				request.setAttribute("city", "Pompeii");
				city = "Pompeii";
				message = "Click on the map to search for graffiti in a particular city-block.";
			} else if (city.toLowerCase().equals("herculaneum")) {
				request.setAttribute("city", "Herculaneum");
				city = "Herculaneum";
				message = "Click on one or more properties within the map, then hit the \"Search\" button below.";
			} else {
				request.setAttribute("error", "Error: " + city + " is not a valid city.");
				return "index";
			}

			final InputStream inStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(city.toLowerCase() + "_map.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			Document doc;

			List<String> coords = new ArrayList<String>();
			List<String> regionNames = new ArrayList<String>();
			List<String> regionIds = new ArrayList<String>();

			try {
				dBuilder = dbFactory.newDocumentBuilder();
				doc = dBuilder.parse(inStream);
				doc.getDocumentElement().normalize();

				NodeList areaList = doc.getElementsByTagName("area");

				for (int i = 0; i < areaList.getLength(); i++) {

					Element area = (Element) areaList.item(i);
					String regionCoords = area.getAttribute("coords");
					String regionName = area.getAttribute("alt");
					int regionId;

					if (city.equals("Pompeii")) { // use insula ids
						regionId = this.insulaDao.getInsulaByCityAndInsula(city, regionName).get(0).getId();
						regionIds.add("i" + String.valueOf(regionId));
					} else { // use property ids
						String insulaAndProp = regionName.split(" ")[0];
						try {
							int ind = insulaAndProp.lastIndexOf(".");
							String insula = insulaAndProp.substring(0, ind);
							String property_number = insulaAndProp.substring(ind + 1);
							// System.out.println(insulaAndProp + " " + city + "
							// " + insula + " " + property_number);
							regionId = this.findspotDao
									.getPropertyByCityAndInsulaAndProperty(city, insula, property_number).getId();
							regionIds.add("p" + String.valueOf(regionId));
						} catch (IndexOutOfBoundsException e) {
							// not one of the "regular" properties
							// String uniqueId = insulaAndProp + i;
							// System.out.println(city + " " + regionName);
							regionId = this.findspotDao.getPropertyByCityAndProperty(city, regionName).getId();
							regionIds.add("p" + regionId);
						}
					}
					coords.add(regionCoords);
					regionNames.add(regionName);
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException | IOException e) {
				e.printStackTrace();
			}
			request.setAttribute("coords", coords);
			request.setAttribute("regionNames", regionNames);
			request.setAttribute("regionIds", regionIds);
			request.setAttribute("message", message);
			request.setAttribute("displayImage", request.getContextPath() + "/resources/images/" + city + ".jpg");

			// Allows attributes to be set but goes to the pompeiiMap url if the
			// city clicked on is pompeii.
			if (city.toLowerCase().equals("pompeii")) {
				// return "pompeiiMap";
				return "searchPompeii";
			}
			if (city.toLowerCase().equals("herculaneum")) {
				// return "pompeiiMap";
				return "herculaneumMap";
			}
			s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
			return "search";
		} else {
			return "index";
		}
	}

	@RequestMapping(value = "/featured-graffiti", method = RequestMethod.GET)
	public String featuredHits(final HttpServletRequest request) {

		final List<Inscription> greatestFiguralHits = this.graffitiDao.getGreatestFiguralHits();
		final List<Inscription> greatestTranslationHits = this.graffitiDao.getGreatestTranslationHits();
		request.setAttribute("figuralHits", greatestFiguralHits);
		request.setAttribute("translationHits", greatestTranslationHits);

		return "featuredGraffiti";

	}

	@RequestMapping(value = "/new-featured-graffiti", method = RequestMethod.GET)
	public String newFeaturedHits(final HttpServletRequest request) {

		final List<Inscription> greatestFiguralHits = this.graffitiDao.getGreatestFiguralHits();
		final List<Inscription> greatestTranslationHits = this.graffitiDao.getGreatestTranslationHits();
		request.setAttribute("figuralHits", greatestFiguralHits);
		request.setAttribute("translationHits", greatestTranslationHits);

		return "newFeaturedGraffiti";

	}

	// maps to inputData.jsp page which is used to input inscription to the
	// database using a csv file
	@RequestMapping(value = "/inputData", method = RequestMethod.GET)
	public String inputData(final HttpServletRequest request) {
		return "inputData";
	}

	/*
	 * reads in the file inputed from inputData.jsp and currently reads each
	 * line and check to see if it contains the string "EDR" to check if that
	 * line is a valid line holding information for a new inscription. Thus it
	 * is required for the inscriptions in the csv file to have an EDR number
	 * starting with EDR
	 */
	@RequestMapping(value = "/inputDataComplete", method = RequestMethod.POST)
	public String inputDataComplete(final HttpServletRequest request) {

		final ArrayList<ArrayList<String>> inscriptions = new ArrayList<ArrayList<String>>();
		try {
			BufferedReader file_in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line = new String("");
			int count = 0;
			while ((line = file_in.readLine()) != null) {
				System.out.println(count);
				if (line.contains("EDR")) {
					System.out.println(line);
					inscriptions.add(separateFields(line));
				}
				count++;
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		this.graffitiDao.insertEdrInscription(inscriptions);

		return "inputDataComplete";
	}

	// Used for mapping the URI to show inscriptions. It has a similar structure
	// to the mapping done to /result below
	@RequestMapping(value = "/region/{city}", method = RequestMethod.GET)
	public String cityPage(@PathVariable String city, HttpServletRequest request) {
		String searches = city;
		final List<Inscription> resultsList = this.graffitiDao.getInscriptionsByCity(searches);
		request.setAttribute("resultsLyst", resultsList);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}", method = RequestMethod.GET)
	public String insulaPage(@PathVariable String city, @PathVariable String insula, HttpServletRequest request,
			HttpServletResponse response) {
		// System.out.println("insulaPage: " + insula);
		int insula_id = getInsulaId(city, insula);
		final List<Inscription> inscriptions = this.graffitiDao.getInscriptionsByCityAndInsula(city, insula_id);
		request.setAttribute("resultsLyst", inscriptions);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}/{property}", method = RequestMethod.GET)
	public String propertyPage(@PathVariable String city, @PathVariable String property, @PathVariable String insula,
			HttpServletRequest request) {
		// System.out.println("propertyPage: " + property);
		int insula_id = getInsulaId(city, insula);
		int property_id = getPropertyId(city, insula, property);
		final List<Inscription> inscriptions = this.graffitiDao.getInscriptionsByCityAndInsulaAndPropertyNumber(city,
				insula_id, property_id);
		request.setAttribute("resultsLyst", inscriptions);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		return "displayData";
	}

	@RequestMapping(value = "/region/{city}/{insula}/{property}/{id}", method = RequestMethod.GET)
	public String dataPage(@PathVariable String city, @PathVariable String property, @PathVariable String insula,
			@PathVariable int id, HttpServletRequest request) {
		int insula_id = getInsulaId(city, insula);
		int property_id = getPropertyId(city, insula, property);
		final List<Inscription> allInscriptions = this.graffitiDao.getInscriptionsByCityAndInsulaAndPropertyNumber(city,
				insula_id, property_id);
		final List<Inscription> resultsList2 = new ArrayList<Inscription>();

		if (id < allInscriptions.size()) {
			Inscription whichInsc = allInscriptions.get(id);
			resultsList2.add(whichInsc);
		}
		request.setAttribute("resultsLyst", resultsList2);
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		return "displayData";
	}

	// helper method to get insula id for given insula name
	private int getInsulaId(String city, String insula) {
		final List<Insula> ins = this.insulaDao.getInsulaByCityAndInsula(city, insula.toUpperCase());
		if (ins != null && !ins.isEmpty()) {
			return ins.get(0).getId();
		}
		return -1;
	}

	// helper method to get property id for given property number
	private int getPropertyId(String city, String insula, String property_number) {
		final Property prop = this.findspotDao.getPropertyByCityAndInsulaAndProperty(city, insula.toUpperCase(),
				property_number);
		if (prop != null) {
			return prop.getId();
		}
		return -1;
	}

	// The default page is sent to index.jsp
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String indexPage(final HttpServletRequest request) {

		request.setAttribute("drawingCategories", drawingTagsDao.getDrawingTags());
		request.setAttribute("propertyTypes", findspotDao.getPropertyTypes());

		return "index";
	}

	// Maps to the details page once an individual result has been selected in
	// the results page
	@RequestMapping(value = "/graffito/AGP-{edr}", method = RequestMethod.GET)
	public String graffito(final HttpServletRequest request, @PathVariable String edr) {
		final Inscription i = this.graffitiDao.getInscriptionByEDR(edr);
		if (i == null) {
			request.setAttribute("error", "Error: " + edr + " is not a valid EDR id.");
			return "index";
		} else {
			Set<DrawingTag> tags = i.getAgp().getFiguralInfo().getDrawingTags();
			List<String> names = new ArrayList<String>();
			for (DrawingTag tag : tags) {
				names.add(tag.getName());
			}
			String city = i.getAncientCity();
			request.setAttribute("drawingCategories", names);
			request.setAttribute("images", i.getImages());
			request.setAttribute("imagePages", i.getPages());
			request.setAttribute("thumbnails", i.getThumbnails());
			request.setAttribute("findLocationKeys", findLocationKeys(i));
			request.setAttribute("inscription", i);
			request.setAttribute("city", city);
			request.getSession().setAttribute("returnFromEDR", edr);

			// Decides which jsp page to travel to when user clicks "More
			// Information" on Search page.
			if (city.equals("Pompeii")) {
				return "moreGraffitoInformation";
			} else {
				return "details";
			}
			// return "details";
		}
	}
	
	// TODO: add annotation produces = "text/html" for the api docs?
	@ApiOperation(value="Searches for inscriptions and returns the results. The base URI lists "
			+ "all inscriptions by default. Various parameters can be added to the URI to filter "
			+ "results as the user wishes.",
			 notes="A detailed overview of possible parameters is as follows: <br/> "
			+ "city={cityName}, where the cities are as follows: [Pompeii, Herculaneum]. <br/>"
			+ "insula={insulaID} <br/>"
			+ "property={propertyID} <br/>"
			+ "property_type={propertyType}<br/>"
			+ "drawing_category={dcID}, where the dcIDs are as follows: [All=0, Boats=1, Geometric designs=2, Animals=3, Erotic Images=4, Other=5, Human figures=6, Gladiators=7, Plants=8]. <br/>"
			+ "writing_style={writingStyle}, where the writing styles are as follows: [Graffito/incised, charcoal, other].<br/>"
			+ "language={language}, where the languages are as follows: [Latin, Greek, Latin/Greek, other].<br/>"
			+ "global={searchString}, where the search string can be any text to search globally for. <br/>"
			+ "content={searchString}, where the search string can be any text to search the content for. <br/>"
			+ "Mutiple parameters passed in the URI can be separated using an ampersand symbol, '&'.")
	@RequestMapping(value = "/results", method = RequestMethod.GET)
	public String search(final HttpServletRequest request) {
		init();
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<Inscription> inscriptions = searchResults(request);

		request.setAttribute("resultsLyst", inscriptions);
		request.setAttribute("searchQueryDesc", "filtering");
		request.setAttribute("findLocationKeys", findLocationKeys(inscriptions));
		// return "results";
		return "searchResults";
	}

	private List<Inscription> searchResults(final HttpServletRequest request) {
		// System.out.println("We're in FilterController: " +
		// request.getQueryString());

		try {
			client = new PreBuiltTransportClient(settings).addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName(ES_HOSTNAME), ES_PORT_NUM));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		SearchResponse response;
		String searchedProperties = "";
		String searchedDrawings = "";

		List<Inscription> inscriptions = new ArrayList<Inscription>();

		// List of parameter strings for each given search term
		List<String> parameters = new ArrayList<String>();
		// List of search terms
		List<String> searchTerms = new ArrayList<String>();
		// List of field names to search for each different search term
		List<String> fieldNames = new ArrayList<String>();

		// Gather all of the request parameters and make an array of those
		// arrays to loop through and check if null
		String[] content = request.getParameterValues("content");
		String[] global = request.getParameterValues("global");
		String[] city = request.getParameterValues("city");
		String[] insula = request.getParameterValues("insula");
		String[] property = request.getParameterValues("property");
		String[] propertyType = request.getParameterValues("property_type");
		String[] drawingCategory = request.getParameterValues("drawing_category");
		String[] writingStyle = request.getParameterValues(WRITING_STYLE_PARAM_NAME);
		String[] language = request.getParameterValues("language");

		String[][] searches = { content, global, city, insula, property, propertyType, drawingCategory, writingStyle,
				language };

		// Determine which parameters have been given; populate the
		// parameters, searchTerms, and fieldNames lists accordingly
		for (int i = 0; i < searches.length; i++) {
			if (searches[i] != null) {
				parameters.add(arrayToString(searches[i]));
				searchTerms.add(searchDescs[i]);
				fieldNames.add(searchFields[i]);
			}
		}

		// This is the main query; does an AND of all sub-queries
		BoolQueryBuilder query = boolQuery();

		// For each given search term, we build a sub-query
		// Special cases are Global Keyword, Content Keyword, and Property
		// searches; all others are simple match queries
		for (int i = 0; i < searchTerms.size(); i++) {

			// System.out.println(searchTerms.get(i) + ": " +
			// parameters.get(i));

			// Searches has_figural_component if user selected "All"
			// drawings
			if (searchTerms.get(i).equals(DRAWING_CATEGORY_SEARCH_DESC) && parameters.get(i).contains("All")) {
				// SES: I think that query should be okay because the other
				// drawing categories are numbers.
				// As soon as we have the query "All", then we get all the
				// figural graffiti and we can skip
				// the rest of the drawing-related query.
				BoolQueryBuilder allDrawingsQuery = boolQuery();
				allDrawingsQuery.should(matchQuery("has_figural_component", true));
				query.must(allDrawingsQuery);
			} else if (searchTerms.get(i).equals("Global Keyword")) {
				// Checks content, city, insula name, property name,
				// property types, drawing description, drawing tags,
				// writing style, language, EAGLE id, and bibliography for a
				// keyword match
				BoolQueryBuilder globalQuery;
				// QueryBuilder fuzzyQuery;
				// QueryBuilder exactQuery;
				QueryBuilder myTestQuery;

				String[] a = fieldNames.get(i).split(" ");

				globalQuery = boolQuery();

				/*
				 * fuzzyQuery = multiMatchQuery(parameters.get(i), a[0], a[1],
				 * a[2], a[3], a[4], a[5], a[6], a[7], a[8]).fuzziness("AUTO");
				 * exactQuery = multiMatchQuery(parameters.get(i), a[9], a[10]);
				 * 
				 * // For EDR id and bibliography, users want exact results.
				 * 
				 * globalQuery.should(fuzzyQuery);
				 * globalQuery.should(exactQuery);
				 */

				myTestQuery = multiMatchQuery(parameters.get(i), a);
				globalQuery.should(myTestQuery);

				query.must(globalQuery);
			} else if (searchTerms.get(i).equals("Content Keyword")) {
				BoolQueryBuilder contentQuery = boolQuery();
				String[] params = parameters.get(i).split(" ");

				for (String param : params) {
					contentQuery.must(matchQuery(fieldNames.get(i), param));// .fuzziness("AUTO"));
				}
				query.must(contentQuery);
			} else if (searchTerms.get(i).equals("Property")) {
				BoolQueryBuilder propertiesQuery = boolQuery();

				String[] properties = parameters.get(i).split(" ");

				for (int j = 0; j < properties.length; j++) {
					QueryBuilder propertyIdQuery;
					BoolQueryBuilder propertyQuery;

					String propertyID = properties[j];

					propertyIdQuery = termQuery(fieldNames.get(i), propertyID);

					propertyQuery = boolQuery().must(propertyIdQuery);

					propertiesQuery.should(propertyQuery);
				}
				query.must(propertiesQuery);
			} else if (searchTerms.get(i).equals(WRITING_STYLE_SEARCH_DESC)
					&& parameters.get(i).equalsIgnoreCase("other")) {
				// special handling of the writing style being "other"
				query.mustNot(termQuery(fieldNames.get(i), "charcoal"));
				query.mustNot(termQuery(fieldNames.get(i), WRITING_STYLE_GRAFFITI_INSCRIBED));
			} else {
				BoolQueryBuilder otherQuery = boolQuery();
				String[] params = parameters.get(i).split(" ");

				for (String param : params) {
					// System.out.println(searchTerms.get(i) + ": match " +
					// param + " in " + fieldNames.get(i));
					otherQuery.should(termQuery(fieldNames.get(i), param));
				}
				query.must(otherQuery);
			}
		}

		response = client.prepareSearch(ES_INDEX_NAME).setTypes(ES_TYPE_NAME).setQuery(query).addStoredField("edr_id")
				.setSize(NUM_RESULTS_TO_RETURN).addSort("edr_id", SortOrder.ASC).execute().actionGet();
		
		for (SearchHit hit : response.getHits()) {
			// System.out.println(hit);
			inscriptions.add(hitToInscription(hit));
		}
		
		//client.close(); // This line slows down searching tremendously for some reason
		HttpSession session = request.getSession();
		if (inscriptions.size() > 0) {
			// System.out.println(inscriptions.get(0));
			request.setAttribute("mapName", inscriptions.get(0).getAncientCity());
		}
		request.setAttribute("searchedProperties", searchedProperties);
		request.setAttribute("searchedDrawings", searchedDrawings);

		// Used in sidebarSearchMenu.jsp
		request.setAttribute("cities", findspotDao.getCityNames());
		request.setAttribute("drawingCategories", drawingTagsDao.getDrawingTags());
		request.setAttribute("propertyTypes", findspotDao.getPropertyTypes());
		request.setAttribute("insulaList", insulaDao.getInsula());
		request.setAttribute("propertiesList", findspotDao.getProperties());

		session.setAttribute("returnURL", ControllerUtils.getFullRequest(request));

		return inscriptions;
	}

	// Turns an array like ["Pompeii", "Herculaneum"] into a string like
	// "Pompeii Herculaneum" for Elasticsearch match query
	private static String arrayToString(String[] parameters) {
		StringBuilder sb = new StringBuilder();
		sb.append(parameters[0].replace("_", " "));
		for (int i = 1; i < parameters.length; i++) {
			sb.append(" ").append(parameters[i].replace("_", " "));
		}
		return sb.toString();
	}

	private Inscription hitToInscription(SearchHit hit) {
		String edrID = hit.getField("edr_id").getValue();
		Inscription inscription = graffitiDao.getInscriptionByEDR(edrID);
		return inscription;
	}

	private static List<String> findLocationKeys(final List<Inscription> inscriptions) {
		final List<String> locationKeys = new ArrayList<String>();
		if (inscriptions != null) {
			final Set<String> locationKeysSet = new TreeSet<String>();
			for (final Inscription inscription : inscriptions) {
				locationKeysSet.add(inscription.getSpotKey());
				/*locationKeysSet.add(inscription.getGenSpotKey());*/
			}
			locationKeys.addAll(locationKeysSet);
		}
		return locationKeys;
	}
	
	private static List<String> findLocationKeys(final Inscription inscription) {
		final List<String> locationKeys = new ArrayList<String>();
		final Set<String> locationKeysSet = new TreeSet<String>();

		locationKeysSet.add(inscription.getSpotKey());
		/*locationKeysSet.add(inscription.getGenSpotKey());*/
		locationKeys.addAll(locationKeysSet);
		return locationKeys;
	}

	private static ArrayList<String> separateFields(String line) {
		final ArrayList<String> fields = new ArrayList<String>();
		while (line.contains(",")) {
			fields.add(line.substring(0, line.indexOf(",")));
			line = line.substring(line.indexOf(",") + 1);
		}
		fields.add(line);
		return fields;
	}

	public GraffitiDao getGraffitiDao() {
		return graffitiDao;
	}

	public void setGraffitiDao(final GraffitiDao graffitiDao) {
		this.graffitiDao = graffitiDao;
	}

	@ApiOperation(value="Filters the inscriptions and returns the results without any styling. The base URI lists "
			+ "all inscriptions by default. Various parameters can be added to the URI to filter "
			+ "results as the user wishes.",
			 notes="A detailed overview of possible parameters is as follows: <br/> "
			+ "city={cityName}, where the cities are as follows: [Pompeii, Herculaneum]. <br/>"
			+ "insula={insulaID} <br/>"
			+ "property={propertyID} <br/>"
			+ "property_type={propertyType}<br/>"
			+ "drawing_category={dcID}, where the dcIDs are as follows: [All=0, Boats=1, Geometric designs=2, Animals=3, Erotic Images=4, Other=5, Human figures=6, Gladiators=7, Plants=8]. <br/>"
			+ "writing_style={writingStyle}, where the writing styles are as follows: [Graffito/incised, charcoal, other].<br/>"
			+ "language={language}, where the languages are as follows: [Latin, Greek, Latin/Greek, other].<br/>"
			+ "global={searchString}, where the search string can be any text to search globally for. <br/>"
			+ "content={searchString}, where the search string can be any text to search the content for. <br/>"
			+ "Mutiple parameters passed in the URI can be separated using an ampersand symbol, '&'.")
	@RequestMapping(value = "/filter", method = RequestMethod.GET)
	public String filterResults(final HttpServletRequest request) {
		init();
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<Inscription> inscriptions = searchResults(request);

		request.setAttribute("resultsLyst", inscriptions);
		request.setAttribute("searchQueryDesc", "filtering");
		request.setAttribute("findLocationKeys", findLocationKeys(inscriptions));
		return "filter";
	}

	@RequestMapping(value = "/admin/report", method = RequestMethod.GET)
	public String reportResults(final HttpServletRequest request) {
		init();
		HttpSession s = request.getSession();
		s.setAttribute("returnURL", ControllerUtils.getFullRequest(request));
		List<Inscription> inscriptions = searchResults(request);
		request.setAttribute("resultsLyst", inscriptions);
		request.setAttribute("searchQueryDesc", "filtering");
		return "admin/report";
	}

}