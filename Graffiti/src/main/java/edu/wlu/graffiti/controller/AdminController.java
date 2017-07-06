package edu.wlu.graffiti.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.dao.DrawingTagsDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.PropertyTypesDao;

@Controller
public class AdminController {
	
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
	
	@RequestMapping(value = "/AdminFunctions", method = RequestMethod.GET)
	public String adminFunctions(final HttpServletRequest request) {
		return "admin/AdminFunctions";
	}

	
	@RequestMapping(value = "/admin/editGraffito", method = RequestMethod.GET)
	public String editGraffito(final HttpServletRequest request) {

		return "admin/editGraffito";

	}

	// Update a graffito page - sharmas
	@RequestMapping(value = "/admin/updateGraffito", method = RequestMethod.GET)
	public String updateGraffito(final HttpServletRequest request) {

		String id = request.getParameter("edrID");
		if (id == null || id.equals("")) {
			request.setAttribute("msg", "Please enter an EDR number");
			return "admin/editGraffito";
		}

		request.getSession().setAttribute("edrID", id);

		Inscription element = graffitiDao.getInscriptionByEDR(id);

		if (element == null) {
			request.setAttribute("msg", "Not a valid EDR number");
			return "admin/editGraffito";
		}

		request.setAttribute("graffito", element);

		return "admin/updateGraffito";

	}

	// Update a graffito controller
	@RequestMapping(value = "/admin/updateGraffito", method = RequestMethod.POST)
	public String adminUpdateGraffito(final HttpServletRequest request) {

		// updating AGP Inscription Information
		String edrID = (String) request.getSession().getAttribute("edrID");

		// updating AGP Inscriptions
		String summary = request.getParameter("summary");
		String commentary = request.getParameter("commentary");
		String cil = request.getParameter("cil");
		String langner = request.getParameter("langner");
		String floor_to_graffito_height = request.getParameter("floor_to_graffito_height");
		String content_translation = request.getParameter("content_translation");
		String graffito_height = request.getParameter("graffito_height");
		String graffito_length = request.getParameter("graffito_length");
		String letter_height_min = request.getParameter("letter_height_min");
		String letter_height_max = request.getParameter("letter_height_max");
		String charHeights = request.getParameter("character_heights");
		String figural = request.getParameter("figural");
		String ghFig = request.getParameter("gh_fig");
		String ghTrans = request.getParameter("gh_trans");

		boolean hasFiguralComponent = false;
		boolean isfeaturedHitFig = false;
		boolean isfeaturedHitTrans = false;

		if (figural != null) {
			hasFiguralComponent = true;
		}
		if (ghFig != null) {
			isfeaturedHitFig = true;
		}
		if (ghTrans != null) {
			isfeaturedHitTrans = true;
		}

		List<Object> agpOneDimArrList = new ArrayList<Object>();
		agpOneDimArrList.add(summary);
		agpOneDimArrList.add(content_translation);
		agpOneDimArrList.add(cil);
		agpOneDimArrList.add(langner);
		agpOneDimArrList.add(floor_to_graffito_height);
		agpOneDimArrList.add(graffito_height);
		agpOneDimArrList.add(graffito_length);
		agpOneDimArrList.add(letter_height_min);
		agpOneDimArrList.add(letter_height_max);
		agpOneDimArrList.add(charHeights);
		agpOneDimArrList.add(commentary);
		agpOneDimArrList.add(hasFiguralComponent);
		agpOneDimArrList.add(isfeaturedHitFig);
		agpOneDimArrList.add(isfeaturedHitTrans);

		graffitiDao.updateAgpInscription(agpOneDimArrList, edrID);

		if (hasFiguralComponent) {
			String drawingDescriptionLatin = request.getParameter("drawing_description_latin");
			String drawingDescriptionEnglish = request.getParameter("drawing_description_english");

			graffitiDao.updateDrawingInfo(drawingDescriptionLatin, drawingDescriptionEnglish, edrID);

		}

		if (isfeaturedHitFig || isfeaturedHitTrans) {
			String ghCommentary = request.getParameter("gh_commentary");
			String ghPreferredImage = request.getParameter("gh_preferred_image");
			graffitiDao.updateGreatestHitsInfo(edrID, ghCommentary, ghPreferredImage);
		}

		// updating drawing tags
		String[] drawingTags = request.getParameterValues("drawingCategory");
		graffitiDao.clearDrawingTags(edrID);

		if (drawingTags != null) {
			graffitiDao.insertDrawingTags(edrID, drawingTags);
		}

		request.setAttribute("msg", "The graffito has been successfully updated in the database");

		Inscription element = graffitiDao.getInscriptionByEDR(edrID);

		request.setAttribute("graffito", element);

		return "admin/updateGraffito";

	}
}
