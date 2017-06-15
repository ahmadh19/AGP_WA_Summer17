/**
 * 
 */
package edu.wlu.graffiti.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a property
 * 
 * @author Sara Sprenkle
 * 
 */
public class Property {

	private int id;
	private String property_number;
	private String property_name;
	private String italianPropertyName;
	private Insula insula;
	private List<PropertyType> propertyTypes;
	private String pleiadesId="";
	private String commentary="";
	private String locationKey="";
	private static final Map<String, Integer> numerals = new TreeMap<String, Integer>();

	/**
	 * 
	 */
	public Property() {
		super();
		
		numerals.put("I", 1);
		numerals.put("II", 2);
		numerals.put("III", 3);
		numerals.put("IV", 4);
		numerals.put("V", 5);
		numerals.put("VI", 6);
		numerals.put("VII", 7);
		numerals.put("VIII", 8);
		numerals.put("IX", 9);
		numerals.put("X", 10);
	}
	
	public Property(int id) {
		this();
		this.id = id;
		this.locationKey = "p" + id;
	}

	/**
	 * @param id
	 * @param property_number
	 * @param property_name
	 * @param insula
	 */
	public Property(int id, String property_number, 
			String property_name, Insula insula) {
		super();
		this.id = id;
		this.property_number = property_number;
		this.property_name = property_name;
		this.insula = insula;
		this.propertyTypes = new ArrayList<PropertyType>();
		this.locationKey = "p" + id;
	}

	public List<PropertyType> getPropertyTypes() {
		return propertyTypes;
	}

	public void setPropertyTypes(List<PropertyType> propertyTypes) {
		this.propertyTypes = propertyTypes;
	}

	/**
	 * @return the id
	 */
	@JsonIgnore
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
		this.locationKey = "p" + id;
	}

	/**
	 * @return the property_number
	 */
	public String getPropertyNumber() {
		return property_number;
	}

	/**
	 * @param property_number
	 *            the property_number to set
	 */
	public void setPropertyNumber(String property_number) {
		this.property_number = property_number;
	}

	/**
	 * @return the property_name
	 */
	public String getPropertyName() {
		return property_name;
	}

	/**
	 * @param property_name
	 *            the property_name to set
	 */
	public void setPropertyName(String property_name) {
		this.property_name = property_name;
	}

	/**
	 * @return the insula
	 */
	public Insula getInsula() {
		return insula;
	}

	/**
	 * @param insula
	 *            the insula to set
	 */
	public void setInsula(Insula insula) {
		this.insula = insula;
	}

	/**
	 * @return the pleiadesId
	 */
	public String getPleiadesId() {
		return pleiadesId;
	}

	/**
	 * @param pleiadesId the pleiadesId to set
	 */
	public void setPleiadesId(String pleiadesId) {
		this.pleiadesId = pleiadesId;
	}

	/**
	 * @return the italianPropertyName
	 */
	public String getItalianPropertyName() {
		return italianPropertyName;
	}

	/**
	 * @param italianPropertyName the italianPropertyName to set
	 */
	public void setItalianPropertyName(String italianPropertyName) {
		this.italianPropertyName = italianPropertyName;
	}

	/**
	 * @return the commentary
	 */
	public String getCommentary() {
		return commentary;
	}

	/**
	 * @param commentary the commentary to set
	 */
	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}
	
	/**
	 * @return the locationKey
	 */
	@JsonIgnore
	public String getLocationKey() {
		return locationKey;
	}
	
	/**
	 * @param shortName the short name of the insula (region.insula)
	 * @param propertyNumber the property number
	 * @return the URL of the PompeiiianPictures for the specific region, insula, and property number
	 */
	public String getPompeiiinPicturesURL() {
		String shortName = insula.getShortName();
		String numeral = shortName.substring(0, shortName.indexOf('.')).trim();
		int region = numerals.get(numeral); // convert roman numeral to integer
		
		String insulaNum = shortName.substring(shortName.indexOf('.') + 1);
		if(insulaNum.length() == 1)
			insulaNum = "0" + insulaNum;

		String propertyNumber = property_number;
		if(propertyNumber.length() == 1)
			propertyNumber = "0" + propertyNumber;
		
		
		return "http://pompeiiinpictures.com/pompeiiinpictures/R"+region+"/"+region+" "+insulaNum+" "+propertyNumber+".htm";
	}

}
