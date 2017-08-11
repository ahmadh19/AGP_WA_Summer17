/**
 * 
 */
package edu.wlu.graffiti.data.setup;

/**
 * Handles translating the EDR info into AGP info as well as importing the [new]
 * AGP data.
 * 
 * @author sprenkle
 */
public class ImportAGPData {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// translate from EDR to AGP
		AddEDRLinksToApparatus.addEDRLinksToApparatus();
		ExtractEDRLanguageForAGPInfo.updateAGPLanguage();
		ExtractWritingStyleForAGPInfo.updateWritingStyle();

		InsertFiguralInformation.insertFiguralInfo();
		UpdateSummaryTranslationCommentaryPlus.updateInfo();
	}

}
