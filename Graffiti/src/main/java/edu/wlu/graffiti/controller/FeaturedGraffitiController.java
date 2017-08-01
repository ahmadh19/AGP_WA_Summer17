package edu.wlu.graffiti.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Theme;
import edu.wlu.graffiti.dao.DrawingTagsDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.PropertyTypesDao;
import edu.wlu.graffiti.dao.ThemeDao;

@Controller
public class FeaturedGraffitiController {
	
	@Resource
	// The @Resource injects an instance of the GraffitiDao at runtime. The
	// GraffitiDao instance is defined in graffiti-servlet.xml.
	private GraffitiDao graffitiDao;

	@Resource
	private DrawingTagsDao drawingTagsDao;

	@Resource
	private PropertyTypesDao propertyTypesDao;

	@Resource
	private FindspotDao findspotDao;

	@Resource
	private InsulaDao insulaDao;
	
	@Resource
	private ThemeDao themeDao;
	
	@RequestMapping(value = "/TranslationQuiz", method = RequestMethod.GET)
	public String featuredHits(final HttpServletRequest request) {

		final List<Inscription> greatestTranslationHits = this.graffitiDao.getGreatestTranslationHits();
		request.setAttribute("translationHits", greatestTranslationHits);

		return "newTranslationQuiz";

	}
	@RequestMapping(value = "/themes/{themeName}", method = RequestMethod.GET)
	public String searchThemedGraffiti(@PathVariable String themeName, final HttpServletRequest request) {

		// Old code:
		//final List<Inscription> greatestTranslationHits = this.graffitiDao.getGreatestTranslationHits();
		//request.setAttribute("translationHits", greatestTranslationHits);
		
		Theme theme = themeDao.getThemeByName(themeName);
		
		List<Inscription> inscriptions = graffitiDao.getInscriptionByTheme(theme.getId());
		request.setAttribute("inscriptions", inscriptions);
		request.setAttribute("theme", themeDao.getThemeByName(themeName));
		
		return "themedGraffitiResults";

	}
	
	@RequestMapping(value = "/new-featured-graffiti/figural-graffiti")
	public String featuredFiguralGraffiti(final HttpServletRequest request) {
		return "figuralGraffiti";
	}

}
