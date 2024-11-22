package com.marksman.census.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marksman.census.constants.SurveyorType;
import com.marksman.census.service.DataService;
import com.marksman.census.service.ValidationService;

@Controller
public class DataControllerImpl implements DataController
{

	@Autowired
	DataService dataService;

	@Autowired
	ValidationService validationService;

	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public @ResponseBody
	Map<String, Object> refresh(String imei, String mCode, HttpServletRequest request,
			HttpServletResponse response)
	{

		Map<String, Object> jsonMap = null;
		logger.info("refresh request against imei : " + imei + ", m code : " + mCode);

		if (request.getHeader("surveyortype") != null && request.getHeader("surveyortype").equalsIgnoreCase("TM")){
			if (validationService.validateSurveyor(mCode, SurveyorType.TM, imei, response))
			{
				jsonMap = dataService.refresh(imei, mCode, response);
			}
			
		} else {
			if (validationService.validateSurveyor(mCode, SurveyorType.DE, imei, response))
			{
				jsonMap = dataService.refresh(imei, mCode, response);
			}
		}
		return jsonMap;
	}
}
