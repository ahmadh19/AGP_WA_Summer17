package edu.wlu.graffiti.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
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
	@RequestMapping(value = "/ThemeGraffiti", method = RequestMethod.GET)
	public String featuredHits2(final HttpServletRequest request) {

		final List<Inscription> greatestTranslationHits = this.graffitiDao.getGreatestTranslationHits();
		request.setAttribute("translationHits", greatestTranslationHits);
		return "Rough_Draft_Theme_Mock_Up";

	}
	}
