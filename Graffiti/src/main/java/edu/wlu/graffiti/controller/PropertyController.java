/*
 * PropertyController -- handles serving property information
 */
package edu.wlu.graffiti.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.bean.PropertyType;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.PropertyTypesDao;

@Controller
public class PropertyController {

	@Resource
	private PropertyTypesDao propertyTypesDao;

	@Resource
	private FindspotDao propertyDao;

	@Resource
	private InsulaDao insulaDao;

	@RequestMapping(value = "/properties/{city}/{insula}/{property}", method = RequestMethod.GET)
	public String propertyPage(@PathVariable String city, @PathVariable String property, @PathVariable String insula,
			HttpServletRequest request) {
		//System.out.println("propertyPage: " + property);
		try {
			final Property prop = this.propertyDao.getPropertyByCityAndInsulaAndProperty(city, insula, property);
			request.setAttribute("prop", prop);
			List<String> locationKeys = new ArrayList<>();
			locationKeys.add(prop.getLocationKey());
			//System.out.println("Loc Key: " + prop.getLocationKey());
			// request.setAttribute("findLocationKeys", prop.getId());
			// request.setAttribute("findLocationKeys", prop.getLocationKey());
			request.setAttribute("findLocationKeys", locationKeys);
			return "property/propertyInfo";
		} catch (Exception e) {
			request.setAttribute("message", "No property with address " + city + " " + insula + " " + property);
			return "property/error";
		}
	}
	
	@RequestMapping(value = "/properties", method = RequestMethod.GET)
	public String searchProperties(final HttpServletRequest request) {
		// get all the property types, all the properties and their mappings to
		// property types

		final List<Property> pompeiiProperties = propertyDao.getPropertiesByCity("Pompeii");

		for (Property p : pompeiiProperties) {
			p.setPropertyTypes(propertyDao.getPropertyTypeForProperty(p.getId()));
		}
		
		request.setAttribute("pompeiiProperties", pompeiiProperties);
		
		final List<Property> herculaneumProperties = propertyDao.getPropertiesByCity("Herculaneum");

		for (Property p : herculaneumProperties) {
			p.setPropertyTypes(propertyDao.getPropertyTypeForProperty(p.getId()));
		}
		
		request.setAttribute("herculaneumProperties", herculaneumProperties);

		return "property/propertyList";
	}
	
	@RequestMapping(value = "/properties/Pompeii", method = RequestMethod.GET)
	public String searchPompeiiProperties(final HttpServletRequest request) {
		// get all the property types, all the properties and their mappings to
		// property types
		
		final List<Property> properties = propertyDao.getPropertiesByCity("Pompeii");
		
		request.setAttribute("pompeiiProperties", properties);

		return "property/propertyList";
	}
	
	@RequestMapping(value = "/properties/Herculaneum", method = RequestMethod.GET)
	public String searchHerculaneumProperties(final HttpServletRequest request) {
		// get all the property types, all the properties and their mappings to
		// property types
		
		final List<Property> properties = propertyDao.getPropertiesByCity("Herculaneum");
		
		request.setAttribute("herculaneumProperties", properties);

		return "property/propertyList";
	}

	// Maps to the details page once an individual result has been selected in
	// the results page
	/*
	 * @RequestMapping(value = "/property/{pleiadesid}", method =
	 * RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE) public
	 * String graffitoXML(final HttpServletRequest request, HttpServletResponse
	 * response,
	 * 
	 * @PathVariable String edr) { response.setContentType("application/xml");
	 * final List<Inscription> results =
	 * this.graffitiDao.getInscriptionByEDR(edr); Inscription i = null; if
	 * (results.isEmpty()) { request.setAttribute("error", "Error: " + edr +
	 * " is not a valid EAGLE id."); return "index"; } else { i =
	 * results.get(0); Set<DrawingTag> tags = i.getDrawingTags(); List<String>
	 * names = new ArrayList<String>(); for (DrawingTag tag : tags) {
	 * names.add(tag.getName()); } int num = i.getNumberOfImages();
	 * request.setAttribute("drawingCategories", names);
	 * request.setAttribute("images", getImages(i, num));
	 * request.setAttribute("imagePages", getPages(i, num));
	 * request.setAttribute("thumbnails", getThumbnails(i, num));
	 * request.setAttribute("findLocationKeys", findLocationKeys(results));
	 * request.setAttribute("inscription", i); request.setAttribute("city",
	 * i.getAncientCity()); return "details"; } }
	 */

}
