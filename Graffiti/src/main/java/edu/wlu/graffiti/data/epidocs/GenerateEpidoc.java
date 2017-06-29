/**
 * 
 */
package edu.wlu.graffiti.data.epidocs;

import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import edu.wlu.graffiti.bean.Inscription;

/**
 * @author Hammad Ahmad
 *
 */
public class GenerateEpidoc {
	
	public GenerateEpidoc() {
	}
	
	public String serializeToXML(Inscription i) {

		// create all the elements
		Element root = new Element("TEI");
		Document doc = new Document(root);
		
		Namespace ns = Namespace.getNamespace("ns", "http://www.tei-c.org/ns/1.0");
		root.setAttribute(new Attribute("space", "preserve", Namespace.XML_NAMESPACE));
		root.setAttribute(new Attribute("lang", "en", Namespace.XML_NAMESPACE));
		root.setAttribute(new Attribute("base", "ex-epidoctemplate.xml", Namespace.XML_NAMESPACE));
		root.addNamespaceDeclaration(ns);
		
		
		Element teiHeader = new Element("teiHeader");
		Element fileDesc = new Element("fileDesc");
		Element titleStmt = new Element("titleStmt");
		
		Element title = new Element("title").setText(Integer.toString(i.getId())); //change this if needed
		titleStmt.addContent(title);
		fileDesc.addContent(titleStmt);
		
		Element publicationStmt = new Element("publicationStmt");
		Element authority = new Element("authority");
		
		Element idno = new Element("idno");
		idno.setAttribute("type", "filename");
		
		publicationStmt.addContent(authority);
		publicationStmt.addContent(idno);
		fileDesc.addContent(publicationStmt);
		
		Element sourceDesc = new Element("sourceDesc");
		Element msDesc = new Element("msDesc");
		Element msIdentifier = new Element("msIdentifier");
		Element repository = new Element("repository").setText("EDR");
		Element idno_msIdentifier = new Element("idno").setText(i.getEdrId());
		
		msIdentifier.addContent(repository);
		msIdentifier.addContent(idno_msIdentifier);
		msDesc.addContent(msIdentifier);
		
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
		layoutDesc.addContent(layout);
		objectDesc.addContent(layoutDesc);
		physDesc.addContent(objectDesc);
		
		Element handDesc = new Element("handDesc");
		Element handNote = new Element("handNote").setText("Letter heights: ");
		
		Element height_handNote = new Element("height");
		if(i.getAgp().getMinLetterHeight() != null && i.getAgp().getMaxLetterHeight() != null) {
			height_handNote.setAttribute("min", i.getAgp().getMinLetterHeight());
			height_handNote.setAttribute("max", i.getAgp().getMaxLetterHeight());
		}
		height_handNote.setAttribute("scope", "letter");
		height_handNote.setText("{letter_height_min}-{letter_height_max}"); //TODO: fix this!
		handNote.addContent(height_handNote);
		handDesc.addContent(handNote);
		physDesc.addContent(handDesc);
		
		Element decoDesc = new Element("decoDesc");
		Element decoNote1 = new Element("decoNote").setText(i.getAgp().getFiguralInfo().getDescriptionInLatin());
		Element decoNote2 = new Element("decoNote").setText(i.getAgp().getFiguralInfo().getDescriptionInEnglish());;
		decoNote1.setAttribute("lang", "la", Namespace.XML_NAMESPACE);
		decoNote2.setAttribute("lang", "en", Namespace.XML_NAMESPACE);
		decoDesc.addContent(decoNote1);
		decoDesc.addContent(decoNote2);
		physDesc.addContent(decoDesc);
		msDesc.addContent(physDesc);
		
		Element history = new Element("history");
		Element origin = new Element("origin");
		Element origPlace = new Element("origPlace").setText(i.getAncientCity());
		Element placeName = new Element("placeName");
		placeName.setAttribute("ref", "URI");
		placeName.setAttribute("type", "property_number");
		Element origDate = new Element("origDate").setText("{date_of_origin}"); //TODO: fix this!
		origin.addContent(origPlace);
		origin.addContent(placeName);
		origin.addContent(origDate);
		history.addContent(origin);
		
		Element provenance1 = new Element("provenance").setText(i.getEDRFindSpot());
		provenance1.setAttribute("type", "found");
		history.addContent(provenance1);
		
		Element provenance2 = new Element("provenance").setText("Modern location(s) (if any from repository, above)");
		provenance2.setAttribute("type", "observed");
		history.addContent(provenance2);
		msDesc.addContent(history);
	
		sourceDesc.addContent(msDesc);
		fileDesc.addContent(sourceDesc);
		
		teiHeader.addContent(fileDesc);
		root.addContent(teiHeader);
		
		Element facsimilie = new Element("facsimilie");
		Element graphic = new Element("graphic").setAttribute("url", "photograph of text or monument");
		facsimilie.addContent(graphic);
		root.addContent(facsimilie);
		
		Element text = new Element("text");
		Element body = new Element("body");
		Element div1 = new Element("div");
		div1.setAttribute("type", "edition");
		div1.setAttribute("space", "preserve", Namespace.XML_NAMESPACE);
		div1.setAttribute("lang", "en", Namespace.XML_NAMESPACE);
		Element ab = new Element("ab");
		Element lb = new Element("ab").setAttribute("n", "1");
		ab.addContent(lb);
		ab.setText(i.getContent());
		div1.addContent(ab);
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
		
		XMLOutputter out = new XMLOutputter();
		out.setFormat(Format.getPrettyFormat());

		return out.outputString(doc);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
