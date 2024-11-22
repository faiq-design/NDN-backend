package com.marksman.census.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marksman.census.bo.Surveyor;
import com.marksman.census.constants.SurveyorType;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.message.MessageType;
import com.marksman.census.service.SystemService;
import com.marksman.census.util.CommonUtil;
import com.marksman.census.util.StringUtils;

@Controller
public class SystemControllerImpl implements SystemController {

	@Autowired
	SystemService systemService;
	@Autowired
	SurveyorDao surveyorDao;

	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public @ResponseBody
	Map<String, Object> checkUpdates(SurveyorType appType,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		return systemService.checkUpdates(appType, request, response);
	}

	@Override
	public @ResponseBody
	void authenticateSurveyor(HttpServletRequest request,
			HttpServletResponse response)

	{

		try {
			int surveyorId = StringUtils.parseInt(
					request.getHeader("surveyorId"), 0);
			logger.info("Authentication request for DB upload against surveyorId : "
					+ surveyorId);
			Surveyor surveyor = surveyorDao.getSurveyorById(surveyorId);
			if (surveyor != null
					&& "Y".equalsIgnoreCase(surveyor.getIsFileUploadAllowed())) {

				CommonUtil.writeErrorMessage(HttpServletResponse.SC_OK,
						MessageType.SUCCESS, response);
			} else if ("N".equalsIgnoreCase(surveyor.getIsFileUploadAllowed())) {
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.ERROR_EXPORT_DB, response);
			} else {
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.ERROR_AUTHORIZATION, response);
			}

		} catch (Exception e) {

			logger.error(e, e);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR_SERVER, response);
		}
	}

	@Override
	public @ResponseBody
	Map<String, Object> forgotPassword(@RequestParam String email,
			HttpServletRequest request) {

		logger.info("userName: " + email);
		Map<String, Object> jsonMap = null;
		try {

			jsonMap = systemService.forgotPassword(email);
		} catch (Exception ex) {

			logger.error(ex, ex);
		}
		return jsonMap;
	}

	@Override
	public Map<String, Object> moveFiles(String email,
			HttpServletRequest request) throws Exception {
		return systemService.mvFiles();
	}

}
