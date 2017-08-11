/**
 * 
 */
package edu.wlu.graffiti.data.export;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import edu.wlu.graffiti.bean.Inscription;

/**
 * This class serializes Inscription objects and returns a string in XML format to represent
 * the objects.
 * @author Hammad Ahmad
 *
 */
public class GenerateEpidoc {
	
	public GenerateEpidoc() {
	}
	
	/**
	 * Serializes an inscription to XML.
	 * 
	 * @param i The inscription
	 * @return the string representation in CML format
	 */
	public String serializeToXML(Inscription i) {

		Element root = new Element("TEI");
		Document doc = new Document(root);
		
		Namespace ns = Namespace.getNamespace("ns", "http://www.tei-c.org/ns/1.0");
		root.setAttribute(new Attribute("space", "preserve", Namespace.XML_NAMESPACE));
		root.setAttribute(new Attribute("lang", "en", Namespace.XML_NAMESPACE));
		root.setAttribute(new Attribute("base", "ex-epidoctemplate.xml", Namespace.XML_NAMESPACE));
		root.addNamespaceDeclaration(ns);
		
		generateTEIHeader(i, root);
		generateFacsimile(i, root);
		generateBody(i, root);
		
		XMLOutputter out = new XMLOutputter();
		out.setFormat(Format.getPrettyFormat());

		return out.outputString(doc);
	}

	/**
	 * Serializes a list of inscriptions to XML.
	 * 
	 * @param inscriptions The list of inscription
	 * @return the string representation in XML format
	 */
	public String serializeToXML(List<Inscription> inscriptions) {
		
		XMLOutputter out = new XMLOutputter();
		out.setFormat(Format.getPrettyFormat());
		
		Element root = new Element("Inscriptions");
		Document doc = new Document(root);
		
		for(Inscription i : inscriptions) {

			Element inscriptionRoot = new Element("TEI");

			Namespace ns = Namespace.getNamespace("ns", "http://www.tei-c.org/ns/1.0");
			inscriptionRoot.setAttribute(new Attribute("space", "preserve", Namespace.XML_NAMESPACE));
			inscriptionRoot.setAttribute(new Attribute("lang", "en", Namespace.XML_NAMESPACE));
			inscriptionRoot.setAttribute(new Attribute("base", "ex-epidoctemplate.xml", Namespace.XML_NAMESPACE));
			inscriptionRoot.addNamespaceDeclaration(ns);
			
			generateTEIHeader(i, inscriptionRoot);
			generateFacsimile(i, inscriptionRoot);
			generateBody(i, inscriptionRoot);
			
			root.addContent(inscriptionRoot);
		}
		
		return out.outputString(doc);
		
	}
	
	/**
	 * Generate the TEI header section of the XML.
	 * 
	 * @param i
	 * @param root
	 */
	private void generateTEIHeader(Inscription i, Element root) {
		Element teiHeader = new Element("teiHeader");
		Element fileDesc = new Element("fileDesc");
		Element titleStmt = new Element("titleStmt");
		
		Element title = new Element("title").setText(i.getAgp().getAgpId()); //change this if needed
		titleStmt.addContent(title);
		fileDesc.addContent(titleStmt);
		
		Element publicationStmt = new Element("publicationStmt");
		Element authority = new Element("authority");
		
		Element idno1 = new Element("idno");
		idno1.setAttribute("type", "filename");
		
		Element publisher = new Element("publisher").setText("Ancient Graffiti Project");
		Element idno2 = new Element("idno").setAttribute("ref", "URI");
		idno2.setText("http://ancientgraffiti.org/Graffiti/graffito/AGP-" + i.getEdrId());
		
		Element availability = new Element("availability");
		Element pAvailability =  new Element("p").setText("This work is licensed under a Creative Commons "
				+ "Attribution-NonCommercial-ShareAlike 4.0 International License.");
		availability.addContent(pAvailability);
		
		publicationStmt.addContent(authority);
		publicationStmt.addContent(publisher);
		publicationStmt.addContent(idno1);
		publicationStmt.addContent(idno2);
		publicationStmt.addContent(availability);
		fileDesc.addContent(publicationStmt);
		
		Element sourceDesc = new Element("sourceDesc");
		Element msDesc = new Element("msDesc");
		
		Element msIdentifier = new Element("msIdentifier");
		Element repository = new Element("repository").setText("EDR");
		Element idno_msIdentifier = new Element("idno").setText(i.getEdrId());
		
		msIdentifier.addContent(repository);
		msIdentifier.addContent(idno_msIdentifier);
		msDesc.addContent(msIdentifier);
		
		Element msIdentifier2 = new Element("msIdentifier");
		Element repository2 = new Element("repository").setText("AGP");
		Element idno_msIdentifier2 = new Element("idno").setText("AGP-" + i.getEdrId());
		
		msIdentifier2.addContent(repository2);
		msIdentifier2.addContent(idno_msIdentifier2);
		msDesc.addContent(msIdentifier2);
		
		Element physDesc = new Element("physDesc");
		Element objectDesc = new Element("objectDesc");
		Element supportDesc = new Element("supportDesc");
		Element support = new Element("support");
		
		supportDesc.addContent(support);
		objectDesc.addContent(supportDesc);
		
		Element layoutDesc = new Element("layoutDesc");
		Element layout = new Element("layout");
		
		Element rs = new Element("rs");
		rs.setAttribute("type", "execution");
		rs.setText(i.getAgp().getWritingStyleInEnglish());
		layout.addContent(rs);
		
		Element dim = new Element("dim");
		dim.setAttribute("type", "fromGround");
		dim.setAttribute("unit", "centimeter");
		dim.setAttribute("min", "0.24");
		dim.setAttribute("max", "0.11");
		dim.setText(i.getAgp().getHeightFromGround());
		layout.addContent(dim);
		
		if(i.getAgp().getGraffitoHeight() != null && i.getAgp().getGraffitoLength() != null) {
			Element dimensions = new Element("dimensions");
			Element height = new Element("height");
			Element width = new Element("width");
			height.setAttribute("unit", "centimeter");
			width.setAttribute("unit", "centimeter");
			height.setText(i.getAgp().getGraffitoHeight());
			width.setText(i.getAgp().getGraffitoLength());
			dimensions.addContent(height);
			dimensions.addContent(width);
			layout.addContent(dimensions);
		}
		layoutDesc.addContent(layout);
		objectDesc.addContent(layoutDesc);
		physDesc.addContent(objectDesc);
		
		Element handDesc = new Element("handDesc");
		
		if(i.getAgp().getMinLetterHeight() != null && i.getAgp().getMaxLetterHeight() != null 
				&& !i.getAgp().getMinLetterHeight().equals("") && !i.getAgp().getMaxLetterHeight().equals("")
				&& !i.getAgp().getMinLetterHeight().equals("null") && !i.getAgp().getMaxLetterHeight().equals("null")) { 
			Element handNote1 = new Element("handNote").setText("Letter heights: ");
			Element height_handNote1 = new Element("height");
			height_handNote1.setAttribute("min", i.getAgp().getMinLetterHeight());
			height_handNote1.setAttribute("max", i.getAgp().getMaxLetterHeight());
			height_handNote1.setAttribute("scope", "letter");
			height_handNote1.setText(Float.toString(Float.valueOf(i.getAgp().getMinLetterHeight()) - 
					Float.valueOf(i.getAgp().getMaxLetterHeight()))); 
			//System.out.println(i.getAgp().getMinLetterHeight());
			handNote1.addContent(height_handNote1);
			handDesc.addContent(handNote1);
		}
		
		if(i.getAgp().getIndividualLetterHeights() != null && !i.getAgp().getIndividualLetterHeights().equals("")
				&& !i.getAgp().getIndividualLetterHeights().equals("null")) { 
			Element handNote2 = new Element("handNote").setText("[Specific letter] height: ");
			Element height_handNote2 = new Element("height");
			height_handNote2.setAttribute("min", "0.01");
			height_handNote2.setAttribute("max", "0.015");
			height_handNote2.setAttribute("scope", "individualLetter");
			height_handNote2.setText(i.getAgp().getIndividualLetterHeights()); 
			handNote2.addContent(height_handNote2);
			handDesc.addContent(handNote2);
		}
		
		if(i.getAgp().getMinLetterWithFlourishesHeight() != null && i.getAgp().getMaxLetterWithFlourishesHeight() != null
				&& !i.getAgp().getMinLetterWithFlourishesHeight().equals("") && !i.getAgp().getMaxLetterWithFlourishesHeight().equals("")
				&& !i.getAgp().getMinLetterWithFlourishesHeight().equals("null") && !i.getAgp().getMaxLetterWithFlourishesHeight().equals("null")) { 
			Element handNote3 = new Element("handNote").setText("Flourish height: ");
			Element height_handNote3 = new Element("height");
			height_handNote3.setAttribute("min", "0.01");
			height_handNote3.setAttribute("max", "0.015");
			height_handNote3.setAttribute("scope", "flourishLetter");
			height_handNote3.setText(Double.toString(Double.valueOf(i.getAgp().getMinLetterWithFlourishesHeight()) - 
					Double.valueOf(i.getAgp().getMaxLetterWithFlourishesHeight()))); 
			handNote3.addContent(height_handNote3);
			handDesc.addContent(handNote3);
		}
		
		physDesc.addContent(handDesc);

		if(i.getAgp().hasFiguralComponent()) {
			Element decoDesc = new Element("decoDesc");
			Element decoNote1 = new Element("decoNote").setText(i.getAgp().getFiguralInfo().getDescriptionInLatin());
			Element decoNote2 = new Element("decoNote").setText(i.getAgp().getFiguralInfo().getDescriptionInEnglish());;
			decoNote1.setAttribute("lang", "la", Namespace.XML_NAMESPACE);
			decoNote2.setAttribute("lang", "en", Namespace.XML_NAMESPACE);
			decoDesc.addContent(decoNote1);
			decoDesc.addContent(decoNote2);
			physDesc.addContent(decoDesc);
		}
		
		msDesc.addContent(physDesc);
		
		Element history = new Element("history");
		Element origin = new Element("origin");
		Element origPlace = new Element("origPlace").setText(i.getAncientCity());
		Element placeName = new Element("placeName");
		placeName.setAttribute("ref", "URI");
		placeName.setAttribute("type", "property_number");
		placeName.setText("ancientgraffiti.org/Graffiti/property/" + 
					i.getAgp().getProperty().getInsula().getCity().getName() + 
					"/" + i.getAgp().getProperty().getInsula().getShortName() + "/" + 
					i.getAgp().getProperty().getPropertyNumber());
		Element origDate = new Element("origDate");
		String dateBeginning, dateEnd;
		String notBefore = "";
		String notAfter = "";
		dateBeginning = i.getDateBeginning();
		dateEnd = i.getDateEnd();
		if(dateBeginning == null || dateEnd == null) {
			origDate.setText("unknown");
		} else {
			boolean inBC = false;
			
			// map the Italion explanations for date to English translations
			Map<String, String> translations = new HashMap<String, String>();
			translations.put("archaeologia", "archaeological context");
			translations.put("formulae", "terminology");
			translations.put("historia, antiquitates", "historical context");
			translations.put("lingua", "terminology");
			translations.put("nomina", "onomastics");
			translations.put("palaeographia", "palaeography");
			translations.put("prosopographia", "prosopography");
			
			StringBuilder dateExplanation = new StringBuilder();
			String[] dateExplanations = i.getDateExplanation().split("\\;\\s*");
			for(int index = 0; index < dateExplanations.length - 1; index++) {
				String str = dateExplanations[index];
				if(translations.get(str) != null) {
					dateExplanation.append(translations.get(str) + " and ");
				}
			}
			if(translations.get(dateExplanations[dateExplanations.length - 1]) != null) {
				dateExplanation.append(translations.get(dateExplanations[dateExplanations.length - 1]));
			}
			
			// -40, for example, means 40 B.C.
			if(dateBeginning.contains("-") && dateEnd.contains("-")) {
				// remove the negative signs
				dateBeginning = dateBeginning.replace("-", "");
				dateEnd = dateEnd.replace("-", "");
				inBC = true;
			}
			
			if(dateBeginning.length() == 1) {
				notBefore = "000" + dateBeginning;
			} else if(dateBeginning.length() == 2) {
				notBefore = "00" + dateBeginning;
			}
			if(dateEnd.length() == 1) {
				notAfter = "000" + dateEnd;
			} else if(dateEnd.length() == 2) {
				notAfter = "00" + dateEnd;
			}
			origDate.setAttribute("notBefore", notBefore).setAttribute("notAfter", notAfter);
			origDate.setAttribute("evidence", dateExplanation.toString());
			if(inBC) {
				origDate.setText(dateBeginning + "-" + dateEnd + " B.C.");
			} else {
				origDate.setText(dateBeginning + "-" + dateEnd + " C.E.");
			}
			
		}
		origin.addContent(origPlace);
		origin.addContent(placeName);
		origin.addContent(origDate);
		history.addContent(origin);
		
		Element provenance1 = new Element("provenance").setText(i.getEDRFindSpot());
		provenance1.setAttribute("type", "found");
		history.addContent(provenance1);
		
		Element provenance2 = new Element("provenance");
		if(i.getAncientCity().equals("Pompeii")) {
			provenance2.setText("Pompei");
		} else if(i.getAncientCity().equals("Herculaneum")) {
			provenance2.setText("Ercolano");
		} else if(i.getAncientCity().equals("Smyrna")) {
			provenance2.setText("Izmir, Turkey");
		}
		
		provenance2.setAttribute("type", "observed");
		history.addContent(provenance2);
		msDesc.addContent(history);
	
		sourceDesc.addContent(msDesc);
		fileDesc.addContent(sourceDesc);
		
		teiHeader.addContent(fileDesc);
		root.addContent(teiHeader);
	}
	
	
	/**
	 * Generate the facsimilie section of the XML.
	 * 
	 * @param i
	 * @param root
	 */
	private void generateFacsimile(Inscription i, Element root) {
		List<String> images = i.getImages(); // get all image URLs
		Element facsimilie = new Element("facsimilie");
		for(String url : images) {
			Element graphic = new Element("graphic").setAttribute("url", url);
			facsimilie.addContent(graphic);
		}
		root.addContent(facsimilie);
	}

	
	/**
	 * Generate the body section of the XML.
	 * 
	 * @param i
	 * @param root
	 */
	private void generateBody(Inscription i, Element root) {
		Element text = new Element("text");
		Element body = new Element("body");
		Element div1 = new Element("div");
		div1.setAttribute("type", "edition");
		div1.setAttribute("space", "preserve", Namespace.XML_NAMESPACE);
		div1.setAttribute("lang", "en", Namespace.XML_NAMESPACE);
		
		// Use SAXBuilder + StringReader to turn the string content into XML elements	
		SAXBuilder contentBuilder = new SAXBuilder();
		try {
			Document tempDoc = contentBuilder.build(new StringReader("<ab>" + i.getAgp().getEpidoc() + "</ab>"));
			Element temp = tempDoc.detachRootElement();
			div1.addContent(temp);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		body.addContent(div1);
		
		Element div2 = new Element("div").setAttribute("type", "apparatus");
		Element p_div2 = new Element("p").setText(i.getApparatus());
		div2.addContent(p_div2);
		body.addContent(div2);
		
		Element div3 = new Element("div").setAttribute("type", "translation");
		Element p_div3 = new Element("p").setText(i.getAgp().getContentTranslation());
		div3.addContent(p_div3);
		body.addContent(div3);
		
		Element div4 = new Element("div").setAttribute("type", "commentary");
		Element p_div4 = new Element("p").setText(i.getAgp().getCommentary());
		div4.addContent(p_div4);
		body.addContent(div4);
		
		Element div5 = new Element("div").setAttribute("type", "bibliography");
		Element p_div5 = new Element("p").setText(i.getBibliography());
		div5.addContent(p_div5);
		body.addContent(div5);
		
		Element div6 = new Element("div").setAttribute("type", "summary");
		Element p_div6 = new Element("p").setText(i.getAgp().getSummary());
		div6.addContent(p_div6);
		body.addContent(div6);
		
		text.addContent(body);
		root.addContent(text);
	}

	
}
