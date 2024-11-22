package com.marksman.census.service;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.marksman.census.bo.Build;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.constants.CommonConstants;
import com.marksman.census.constants.SurveyorType;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.dao.SystemDao;
import com.marksman.census.message.MessageType;
import com.marksman.census.util.CommonUtil;
import com.marksman.census.util.DateTimeUtilities;
import com.marksman.census.util.FileUtils;

public class SystemServiceImpl implements SystemService
{

	@Autowired
	CacheService cacheService;
	@Autowired
	ApplicationCacheService applicationCacheService;
	@Autowired
	private SurveyorDao surveyorDao;
	@Autowired
	private SystemDao systemDao;
	@Autowired
	private SurveyorServiceImpl surveyorServiceImpl;
	@Autowired
	private ValidationServiceImpl validationServiceImpl;
	@Autowired
	ServletContext servletContext;

	protected static Logger logger = Logger.getLogger(SystemServiceImpl.class);
	private ResourceBundle bundle = ResourceBundle.getBundle(CommonConstants.PROPERTY_FILE_NAME);

	@Override
	public Map<String, Object> checkUpdates(SurveyorType appType, HttpServletRequest request,
			HttpServletResponse response)
	{

		if (applicationCacheService.getBuildsMap().containsKey(appType.toString()))
		{

			Build build = applicationCacheService.getBuildsMap().get(appType.toString());
			String requestVersion = request.getHeader("version").split("_")[0];
			String imei = request.getHeader("imei");

			Integer surveyorId = Integer.parseInt(request.getHeader("surveyorId"));
			Surveyor surveyor = surveyorDao.getSurveyorById(surveyorId);
			int employeeId = surveyorId;
			if (request.getHeader("employeeId") != null)
			{
				employeeId = Integer.parseInt(request.getHeader("employeeId"));
			}

			/*
			 * logger.info(" request version : " + request.getHeader("version")
			 * + ", current version : " + build.getVersion());
			 */
			logger.info(" request version : " + requestVersion + ", current version : "
					+ build.getVersion() + " , employeeId : " + employeeId + ", surveyorId : "
					+ surveyorId + ", imei: " + imei);

			if ("Y".equalsIgnoreCase(surveyor.getForceLogin()))
			{
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.ERROR_FORCE_LOGIN, response);
				return null;
			}
			if (Boolean.parseBoolean(bundle.getString(CommonConstants.IMEI_VALIDATION).trim())
					&& !validationServiceImpl.isValidImei(imei, response))
			{

				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.ERROR_IMEI_PERMISSION, response);
				return null;
			}
			if (!surveyorDao.isValidEmployeeCode(surveyorId, employeeId))
			{
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.ERROR_INVALID_EMPLOYEECODE, response);
				return null;
			}
			/*
			 * if (Float.parseFloat(build.getVersion()) > 1.2 &&
			 * !this.isCorrectDateTime(request.getHeader("deviceDateTime"))) {
			 * CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
			 * MessageType.ERROR_TIME_DIFFERENCE, response); return; }
			 */
			if (request.getHeader("deviceDateTime") != null
					&& !this.isCorrectDateTime(request.getHeader("deviceDateTime")))
			{
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.ERROR_TIME_DIFFERENCE, response);
				return null;
			}
			if ("Y".equalsIgnoreCase(surveyor.getVersionAlert())
					&& !build.getVersion().equalsIgnoreCase(requestVersion))
			{
				response.setHeader("url", build.getDownloadUrl());
				response.setHeader("version", build.getVersion());
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED,
						MessageType.ERROR_VERSION, response);
				return null;
				/*
				 * if ( build.getVersion().equalsIgnoreCase( requestVersion)) {
				 * CommonUtil.writeErrorMessage(HttpServletResponse.SC_OK,
				 * MessageType.SUCCESS, response); return; return
				 * surveyorServiceImpl.refreshData(imei, surveyorId,
				 * appType.toString(), request, response); } else {
				 * 
				 * response.setHeader("url", build.getDownloadUrl());
				 * response.setHeader("version", build.getVersion());
				 * CommonUtil.writeErrorMessage(
				 * HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED,
				 * MessageType.ERROR_VERSION, response); return null; }
				 */
			}
			else
			{
				return surveyorServiceImpl.refreshData(imei, surveyor, appType.toString(),requestVersion, request,
						response);
			}
		}
		CommonUtil.writeErrorMessage(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				MessageType.ERROR_SERVER, response);
		return null;

	}

	@Override
	public Map<String, Object> forgotPassword(String email) throws Exception
	{

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		try
		{

			RandomStringUtils.randomAlphabetic(40);
			/*
			 * String requestExpiryDate =
			 * DateTimeUtilities.dateAdd(DateTimeUtilities
			 * .getCurrentTime(DateTimeConstants.DATE_TIME_FORMAT),
			 * Integer.parseInt(bundle
			 * .getString(CommonConstants.FORGOT_PASSWORD_LINK_EXPIRY_DAYS)),
			 * Calendar.DAY_OF_WEEK);
			 */
			logger.info("sending forgot password email to: " + email);

		}
		catch (Exception ex)
		{

			logger.error(ex, ex);
		}
		return jsonMap;
	}

	protected boolean isCorrectDateTime(String deviceTime)
	{

		Date deviceDateTime = DateTimeUtilities.stringToDate(deviceTime, "yyyy-MM-dd HH:mm:ss");
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(deviceDateTime);
		long deviceMillies = calendar1.getTimeInMillis();

		// Date currentDate =
		// DateTimeUtilities.getCurrentDateInDate(DateTimeConstants.DATE_FORMAT);
		Date currentDate = DateTimeUtilities.getCurrentTimestamp();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		long currentMillies = calendar.getTimeInMillis();

		/* / covert milliseconds to minutes / */
		float diff = Math.abs((currentMillies - deviceMillies) / 60000);
		int MinuteLimit = Integer
				.parseInt(bundle.getString(CommonConstants.TIME_DIFFERENCE).trim());
		if (diff > MinuteLimit)
		{
			// this.writeTimeDifferenceError(response);
			logger.error("Device Time is not correct, Please change your device time");
			return false;
		}
		return true;
	}

	@Override
	public Map<String, Object> mvFiles() throws Exception
	{
		List<Map<String, Object>> fileList = systemDao.getFileList();
		for (Map<String, Object> file : fileList)
		{

			String source = file.get("file_path").toString();
			String destination = source.replace("censusImages", "surveyImages");
			if ((new File(servletContext.getRealPath("/") + source)).exists())
			{
				logger.info("Moving file id : " + file.get("id") + " From : " + source + " To : "
						+ destination);
				FileUtils.copyFile(servletContext.getRealPath("/") + source,
						servletContext.getRealPath("/") + destination);
				FileUtils.deleteFile(servletContext.getRealPath("/") + source);
				systemDao.updatePath(destination, Integer.parseInt(file.get("id").toString()));
			}

		}
		return null;
	}
}