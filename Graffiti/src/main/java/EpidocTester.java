
public class EpidocTester {

	private static final String content = 
			"〈:columna I〉"+
			"\nD"+
			"\nD"+

			"\n〈:columna II〉"+
			"\nR"+
			"\nAta(vus?)"+
			"\nB"+
			"\n((:caduceus))"+
			"\nS"+
			"\nS"+
			
			"\n〈:columna III〉"+
			"\n((:caduceus))"+
			"\nS"+
			"\nCX"+
			
			"\n〈:columna IV〉"+
			"\nS"+
			"\nS"+
			"\nV̂ôt̂r(oni?)"+
			
			"\n〈:columna V〉"+
			"\nAseli="+
			"\na="+
			"\nm (:Asellam)"+
			"\nCXL";
	
	private static final String content2 = "C(aius) Anneus (:Annaeus)\nCapito"
			+ "\neq(ues) coh(ortis) X̅ pr(aetoriae)\n((centuria)) Grati";
	
	private static final String content3 = "Mula fellaat (:fellat) [A]ntoni"
			+ "\nFortunata a(eris) a(ssibus) II\n((:herma mulieris))";
	
	public static String transformContentToEpidoc(String content) {
		StringBuilder returnString = new StringBuilder();
		if(content.contains("columna")) { // if content is split across columns
			markContentWithColumns(content, returnString);
		} else if(content.contains("((:")) { // if content contains the description of a figural graffito
			markContentWithDrawing(content, returnString);
			
		} else { // if no columns or images in content
			addLBTagsToContent(content, returnString);
		}
		return returnString.toString().trim();
	}

	// ARE WE ONLY GOING TO EVER HAVE ONE DRAWING PER CONTENT?
	private static void markContentWithDrawing(String content, StringBuilder returnString) {
		String nonFiguralContent = content.substring(0, content.indexOf("((:"));
		String figuralContent = content.substring(content.indexOf("((:") + 3, content.indexOf("))")); 
		addLBTagsToContent(nonFiguralContent, returnString);
		returnString.append("<figure><graphic url='http://...' /><figDesc>" + figuralContent + "</figDesc></figure>");
	}

	private static void markContentWithColumns(String content, StringBuilder returnString) {
		String[] splitContentAcrossColumns = content.split(".*columna.*");
		for(int i = 1; i < splitContentAcrossColumns.length; i++) {
			char letter = (char) ('a'+ i-1);
			returnString.append("<div type='textpart' subtype='column' n='" + letter + "'>\n");
			addLBTagsToContent(splitContentAcrossColumns[i].trim(), returnString);
			returnString.append("</div>\n");
		}
	}

	private static void addLBTagsToContent(String content, StringBuilder returnString) {
		String[] splitContent = content.split("\n *");
		for(int i = 0; i < splitContent.length; i++ ) {
			returnString.append("<lb n='" + Integer.toString(i+1) + "'/>" + splitContent[i] + "\n");
		}
	}
	
	public static void main(String[] args) {
		//System.out.print(content + "\n\n\n");
		System.out.println("Content with images example:\n" + transformContentToEpidoc(content3) + "\n\n");
		System.out.println("Content with column breaks example:\n" + transformContentToEpidoc(content) + "\n\n");
		System.out.println("Simple content example:\n" + transformContentToEpidoc(content2));
		
	}

}
