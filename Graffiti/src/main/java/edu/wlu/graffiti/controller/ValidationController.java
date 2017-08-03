package edu.wlu.graffiti.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.wlu.graffiti.dao.GraffitiDao;

@Controller
@RequestMapping("/updateTest")
public class ValidationController {
	
	@Resource
	private GraffitiDao graffitiDao;

	// Display the form on the get request
	/*
	@RequestMapping(method = RequestMethod.GET)
	public String showValidationForm(Map model, final HttpServletRequest request) {
		final String edr = request.getParameter("EDR");
		final Inscription inscription = this.graffitiDao
				.getInscriptionByEDR(edr);

		
		Map<String,String> country = new LinkedHashMap<String,String>();
		country.put("US", "United Stated");
		country.put("CHINA", "China");
		country.put("SG", "Singapore");
		country.put("MY", "Malaysia");
		
		request.setAttribute("inscriptions", inscription);
		return "updateTest";
	}
	*/

	// Process the form.
//	@RequestMapping(method = RequestMethod.POST)
//	public String processValidatinForm(@Valid Inscription validationForm,
//			BindingResult result, Map model) {
//		if (result.hasErrors()) {
//			return "updateTest";
//		}
//		// Add the saved validationForm to the model
//		return "editComplete";
//	}
	
	
	public GraffitiDao getGraffitiDao() {
		return graffitiDao;
	}

	public void setGraffitiDao(final GraffitiDao graffitiDao) {
		this.graffitiDao = graffitiDao;
	}

}