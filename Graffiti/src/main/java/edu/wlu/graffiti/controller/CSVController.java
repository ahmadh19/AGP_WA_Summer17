package edu.wlu.graffiti.controller;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.data.export.GenerateCSV;

/**
 * 
 * @author Hammad Ahmad
 *
 */
@RestController
public class CSVController {
	
	private GenerateCSV generator = new GenerateCSV();
	
	@Resource
	private GraffitiDao graffitiDao;

	@Resource
	private FindspotDao findspotDao;

	@RequestMapping(value = "/graffito/AGP-{edrId}/csv", produces = "text/csv")
	public String getInscription(@PathVariable String edrId, HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=AGP-"+ edrId +".csv");
		return generator.serializeToCSV(graffitiDao.getInscriptionByEDR(edrId));
	}
	
	@RequestMapping(value = "/all/csv", produces = "text/csv")
	public String getInscriptions(HttpServletResponse response) {
		response.addHeader("Content-Disposition", "attachment; filename=all-inscriptions.csv");
		return generator.serializeToCSV(graffitiDao.getAllInscriptions());
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/filtered-results/csv", produces = "text/csv")
	public String getFilteredInscriptions(final HttpServletRequest request, HttpServletResponse response) {
		HttpSession s = request.getSession();
		List<Inscription> results = (List<Inscription>) s.getAttribute("filteredList");
		response.addHeader("Content-Disposition", "attachment; filename=filtered-results.csv");
		return generator.serializeToCSV(results);
	}
	
	/**
	@RequestMapping("/property/{city}/{insula}/{property}/csv")
	public Property getProperty(@PathVariable String city, @PathVariable String insula, @PathVariable String property) {
		return findspotDao.getPropertyByCityAndInsulaAndProperty(city, insula, property);
	}
	*/

}
