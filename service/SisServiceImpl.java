/**
 * 
 */
package com.marksman.census.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.marksman.census.bo.Build;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.constants.CommonConstants;
import com.marksman.census.constants.SysConstants;
import com.marksman.census.dao.CitiesDao;
import com.marksman.census.dao.RegionsDao;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.dao.ZonesDao;
import com.marksman.census.message.MessageType;
import com.marksman.census.util.CommonUtil;

/**
 * @author Muhammad Awais
 * 
 */
public class SisServiceImpl implements SisService
{
	@Autowired
	ApplicationCacheService applicationCacheService;
	@Autowired
	SurveyorDao surveyorDao;
	@Autowired
	RegionsDao regionDao;
	@Autowired
	CitiesDao citiesDao;
	@Autowired
	ZonesDao zoneDao;

	protected Logger logger = Logger.getLogger(this.getClass());
	ResourceBundle bundle = ResourceBundle.getBundle(CommonConstants.PROPERTY_FILE_NAME);

	@Override
	public Map<String, Object> login(String imei, String mCode, HttpServletRequest request,
			HttpServletResponse response)
	{
		Build build = applicationCacheService.getBuildsMap().get("SIS");
		logger.info("Applicaiton version : " + request.getHeader("version"));
		String version = request.getHeader("version");
		if (version.contains("_"))
		{
			version = version.split("_")[0];
		}
		if (Boolean.parseBoolean(bundle.getString(CommonConstants.VERSION_ALERT).trim()) &&
				!build.getVersion().equalsIgnoreCase(version))
		{
			response.setHeader("url", build.getDownloadUrl());
			response.setHeader("version", build.getVersion());
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED,
					MessageType.ERROR_VERSION, response);
			return null;
		}
		else
		{
			return ndnLogin(imei, mCode, response);
		}
	}

	private Map<String, Object> ndnLogin(String imei, String mCode, HttpServletResponse response)
	{
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try
		{
			Surveyor surveyor = surveyorDao.getSurveyorDetail(mCode);

			data.put("zones", zoneDao.getZoneBySurveyor(surveyor.getId()));

			data.put("surveyor", surveyor);
			data.put("remarks", applicationCacheService.getRemarks());
			data.put("remarksTypes", applicationCacheService.getRemarksTypes());

			data.put("shopCategories", applicationCacheService.getShopsCategories());
			data.put("shopGroups", applicationCacheService.getShopsGroups());
			data.put(
					"landmarks",
					new ArrayList<String>(Arrays.asList(bundle.getString(SysConstants.LANDMARKS)
							.trim().split(","))));
			data.put(
					"segments",
					new ArrayList<String>(Arrays.asList(bundle.getString(SysConstants.SEGMENTS)
							.trim().split(","))));
			data.put(
					"profiles",
					new ArrayList<String>(Arrays.asList(bundle.getString(SysConstants.PROFILE)
							.trim().split(","))));

			data.put(
					"areaType",
					new ArrayList<String>(Arrays.asList(bundle.getString(SysConstants.AREA_TYPE)
							.trim().split(","))));
			data.put("cities", citiesDao.getCities(surveyor.getId()));
			data.put("questions", applicationCacheService.getQuestions());
			data.put("options", applicationCacheService.getOptions());
			data.put("questionOptions", applicationCacheService.getQuestionOptions());
			// data.put("industries", applicationCacheService.getIndustries());
			// data.put("families", applicationCacheService.getFamilies());
			data.put("industries", applicationCacheService.getSisIndustries());
			data.put("families", applicationCacheService.getSisFamilies());
			data.put("products", applicationCacheService.getProducts());
			data.put("bwus", applicationCacheService.getBwu());
			data.put("industryBwus", applicationCacheService.getSisIndustryBwu());
			data.put("checkInInfo",
					surveyorDao.getSisSurveyorAttendance(surveyor.getId()));
			data.put("endDayInfo", surveyorDao.getSisEndDay(surveyor.getId()));
			jsonMap.put("data", data);
		}
		catch (Exception ex)
		{

			logger.error("Exception occured while login against imei " + imei, ex);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return jsonMap;
	}

}
