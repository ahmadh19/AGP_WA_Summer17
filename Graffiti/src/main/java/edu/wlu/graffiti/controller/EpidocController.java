package edu.wlu.graffiti.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Property;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.data.epidocs.GenerateEpidoc;

@RestController
public class EpidocController {
	
	private GenerateEpidoc generator = new GenerateEpidoc();
	
	@Resource
	private GraffitiDao graffitiDao;

	@Resource
	private FindspotDao findspotDao;

	@RequestMapping(value = "/graffito/AGP-{edrId}/xml", produces = "application/xml" )
	public String getInscription(@PathVariable String edrId) {
		return generator.serializeToXML(graffitiDao.getInscriptionByEDR(edrId));
	}
	
	@RequestMapping(value = "/all/xml", produces = "application/xml")
	public List<Inscription> getInscriptions() {
		return graffitiDao.getAllInscriptions();
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/filtered-results/xml", produces = "application/xml")
	public List<Inscription> getFilteredInscriptions(final HttpServletRequest request) {
		HttpSession s = request.getSession();
		List<Inscription> results = (List<Inscription>) s.getAttribute("filteredList");
		//System.out.println(results.size());
		for(Inscription i : results) {
			//generator.serializeEmployeeToXML(i);
		}
		return results;
	}
	
	@RequestMapping("/property/{city}/{insula}/{property}/xml")
	public Property getProperty(@PathVariable String city, @PathVariable String insula, @PathVariable String property) {
		return findspotDao.getPropertyByCityAndInsulaAndProperty(city, insula, property);
	}

}
