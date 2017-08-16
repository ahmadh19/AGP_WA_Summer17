import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EpidocTester {

	private static String content = 
			"〈:columna I〉"+
			"\nD"+
			"\nD"+

			"\n〈:columna II〉"+
			"\nR"+
			"\nAta(vus)"+
			"\nB"+
			"\n((:caduceus))"+
			"\nS[- - -]"+
			"\nS"+
			
			"\n〈:columna III〉"+
			"\n((:caduceus))"+
			"\nS"+
			"\nCX"+
			"\n[X]anthi"+
			
			"\n〈:columna IV〉"+
			"\nS"+
			"\nS"+
			"\nV̂ôt̂r(oni)"+
			"\n[[Augustus]]"+
			
			"\n〈:columna V〉"+
			"\nAse(li?)="+
			"\na="+
			"\nm (:Asellam)"+
			"\nCXL";
	
	private static String content2 = "C(aius) [- - -]foroeus (:Annaeus)\nCapito"
			+ "\neq(ues) coh(ortis?) X̅ pr(aetoriae)\n((centuria)) Grati";
	
	private static String content3 = "Mula fellaat (:fellat) [A]ntoni"
			+ "\nFortunata a(eris) a(ssibus) II\n((:herma mulieris))";
	
	private static String regexClass = "[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]";
	
	public static String transformContentToEpidoc(String content) {
		StringBuilder returnString = new StringBuilder();
		Pattern pattern;
		Matcher matcher;
		
		if(content.contains(":columna")) { // if content is split across columns, mark those columns
			content = markContentWithColumns(content);
		} else { 
			content = addLBTagsToContent(content);
		}	
		
		pattern = Pattern.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\)");
		matcher = pattern.matcher(content);
		if(matcher.find()) {
			content = addAbbreviationTags(content);
		}
		
		pattern = Pattern.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\?\\)");
		matcher = pattern.matcher(content);
		if(matcher.find()) {
			content = addAbbreviationTagsWithUncertainty(content);
		}
		
		pattern = Pattern.compile("\\[\\- \\- \\-\\]|\\[\\-\\-\\-\\]");
		matcher = pattern.matcher(content);
		if(matcher.find()) {
			System.out.println("here");
			content = addLostContentTags(content);
		}
		
		pattern = Pattern.compile("\\(\\(\\:[^\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\)\\)");
		matcher = pattern.matcher(content);
		if(matcher.find()) {
			//System.out.println("here");
			content = markContentWithFigureTags(content);
		}
		
		pattern = Pattern.compile("\\[\\[[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\]\\]");
		matcher = pattern.matcher(content);
		if(matcher.find()) {
			content = addIntentionallyErasedTags(content);
		}
		
		pattern = Pattern.compile("\\[[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\]");
		matcher = pattern.matcher(content);
		if(matcher.find()) {
			content = addOncePresentButNowErasedTags(content);
		}
		
		return content;
	}

	private static String markContentWithFigureTags(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\(\\(\\:[^\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\)\\)");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<figure><figDesc>" + temp.replaceAll("\\(\\(\\:", "").replaceAll("\\)\\)", "") + "</figDesc></figure>");
		}
		return content;
	}

	private static String markContentWithColumns(String content) {
		StringBuilder returnString = new StringBuilder();
		String[] splitContentAcrossColumns = content.split(".*\\:columna.*");
		for(int i = 1; i < splitContentAcrossColumns.length; i++) {
			char letter = (char) ('a'+ i-1);
			returnString.append("<div type='textpart' subtype='column' n='" + letter + "'>");
			returnString.append(addLBTagsToContent(splitContentAcrossColumns[i].trim()));
			returnString.append("</div>");
		}
		return returnString.toString().trim();
	}

	private static String addLBTagsToContent(String content) {
		StringBuilder returnString = new StringBuilder();
		String[] splitContent = content.split("\n *");
		for(int i = 0; i < splitContent.length; i++ ) {
			returnString.append("<lb n='" + Integer.toString(i+1) + "'/>" + splitContent[i]);
		}
		return returnString.toString().trim();
	}
	
	private static String addAbbreviationTags(String content) {
		String temp;
		Pattern pattern = Pattern.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\)");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()) {
			temp = matcher.group(0);
			String abbr = temp.split("\\(")[0];
			String ex = temp.split("\\(")[1].split("\\)")[0];
			content = content.replace(temp, "<expan><abbr>"+abbr+"</abbr><ex>"+ex+"</ex></expan>");
		}
		return content;
	}
	
	private static String addAbbreviationTagsWithUncertainty(String content) {
		String temp;
		Pattern pattern = Pattern.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\?\\)");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()) {
			temp = matcher.group(0);
			String abbr = temp.split("\\(")[0];
			String ex = temp.split("\\(")[1].split("\\?\\)")[0];
			content = content.replace(temp, "<expan><abbr>"+abbr+"</abbr><ex cert='low'>"+ex+"</ex></expan>");
		}
		return content;
	}
	
	private static String addLostContentTags(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\[\\- \\- \\-\\]|\\[\\-\\-\\-\\]");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, temp.replaceAll("\\[\\- \\- \\-\\]|\\[\\-\\-\\-\\]", "<gap reason='lost' extent='unknown' unit='character'/>"));
			}
		return content;
	}
	
	private static String addIntentionallyErasedTags(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\[\\[[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\]\\]");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<del rend='erasure'><supplied reason='lost'>" + temp.replaceAll("\\[\\[|\\]\\]", "") + "</supplied></del>");
		}
		return content;
	}
	
	private static String addOncePresentButNowErasedTags(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\[[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\]");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<supplied reason='lost'>" + temp.replaceAll("\\[|\\]", "") + "</supplied>");
		}
		return content;
	}
	
	private static String extractCIL(String bib) {
		Pattern pattern = Pattern.compile("CIL\\s04\\,\\s[0-9]{5}[a-zA-Z]*\\s*\\([0-9]\\)");
		Matcher matcher = pattern.matcher(bib);
		if(matcher.find()) {
			return matcher.group(0).split("\\s*\\(")[0];	
		}
		return "";
	}
	
	public static void main(String[] args) {
		//System.out.print(content + "\n\n\n");
		//System.out.println("Content with images example:\n" + transformContentToEpidoc(content3) + "\n\n");
		//System.out.println("Content with column breaks example:\n" + transformContentToEpidoc(content) + "\n\n");
		//System.out.println("Simple content example:\n" + transformContentToEpidoc(content2));
		//System.out.print(transformContentToEpidoc(content3).replaceAll("<", "\n<"));
		//System.out.println(transformContentToEpidoc(content3) + "\nStarting the printing:");
		//String[] array = transformContentToEpidoc(content3).split("<");
		//for(String str : array) {
		//	System.out.print(str);
		//}
		/*
		if(content2.split("[a-zA-Z]*\\([a-zA-Z]+\\)").length > 1) {
			System.out.println("Yes");
		} else {
			System.out.println("No");
		}
		*/
		//System.out.println("Original Content: \n" + content2 + "\n");
		/*
		String temp;
		Pattern pattern = Pattern.compile("\\[[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\]");
		Matcher matcher = pattern.matcher(content);
		while(matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<supplied reason='lost'>" + temp.replaceAll("\\[|\\]", "") + "</supplied>");
		}
		System.out.println(content);
		*/
		//System.out.println(transformContentToEpidoc("((:herma viri))"));
		//System.out.println(transformContentToEpidoc("[---]culus"));
		
		//String nonFiguralContent = content.substring(0, content.indexOf("((:"));
		//String figuralContent = content.substring(content.indexOf("((:") + 3, content.indexOf("))")); 
		//addLBTagsToContent(nonFiguralContent, returnString);
		
		//System.out.println("<figure><graphic url='http://...' /><figDesc>" + figuralContent + "</figDesc></figure>");
		//System.out.println(content2);
		//String[] temp = content.split("[a-zA-Z]*\\([a-zA-Z]+\\)");
		//for(String str : temp) System.out.println(str);
		
		System.out.println(extractCIL("CIL 04, 08600 (1)<br>A. Varone, Titulorum graphio exaratorum qui in CIL Vol. IV collecti sunt imagines, Roma 2012, vol. I, p. 136 con foto (2)"));
	
	}

}
