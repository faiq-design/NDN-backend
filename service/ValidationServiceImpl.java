package com.marksman.census.service;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.marksman.census.bo.Dsr;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.constants.CommonConstants;
import com.marksman.census.constants.SurveyorType;
import com.marksman.census.constants.SysConstants;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.message.MessageType;
import com.marksman.census.util.CommonUtil;
import com.marksman.census.util.PasswordStorage;
import com.marksman.census.util.StringUtils;

public class ValidationServiceImpl implements ValidationService
{
	protected Logger logger = Logger.getLogger(this.getClass());
	private ResourceBundle bundle = ResourceBundle.getBundle(CommonConstants.PROPERTY_FILE_NAME);
	@Autowired
	private SurveyorDao surveyorDao;

	/*
	 * @Override public String getAllowedVariance() { return
	 * bundle.getString(CommonConstants.ALLOWD_VARIENCE_PERCENTAGE).trim(); }
	 */

	@Override
	public int isValidCode(String mCode, SurveyorType surveyorType, HttpServletResponse response)
	{

		int surveyorId = surveyorDao.getSurveyorId(mCode, surveyorType);
		if (surveyorId == 0)
		{

			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR_AUTHORIZATION, response);
			return 0;
		}
		return surveyorId;
	}

	@Override
	public boolean isValidImei(String imei, HttpServletResponse response)
	{
		if (StringUtils.isNullOrEmptyString(imei))
		{
			return true;
		}
		if (Boolean.parseBoolean(bundle.getString(CommonConstants.IMEI_VALIDATION).trim()))
		{
			// Device is sending OK if IMEI permission is not given
			if ("OK".equalsIgnoreCase(imei))
			{

				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.ERROR_IMEI_PERMISSION, response);
				return false;
			}
			if (!surveyorDao.isValidImei(imei))
			{

				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.ERROR_AUTHENTICATION, response);
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean validateSurveyor(String mCode, SurveyorType surveyorType, String imei,
			HttpServletResponse response)
	{
		try
		{
			int surveyorId = isValidCode(mCode, surveyorType, response);
			if (surveyorId == 0 || !isValidImei(imei, response))
			{

				return false;
			}
			surveyorDao.insertSurveyorLogin(imei, surveyorId);

		}
		catch (Exception ex)
		{
			logger.error("Exception while validation surveyor m code : " + mCode + ", imei : "
					+ imei, ex);
			return false;
		}
		return true;
	}

	@Override
	public boolean validateSurveyor(String mCode, String password, SurveyorType surveyorType,
			String imei, HttpServletResponse response)
	{
		try
		{
			int surveyorId = isValidCode(mCode, password, surveyorType, response);
			if (surveyorId == 0 || !isValidImei(imei, response))
			{
				return false;
			}
			surveyorDao.insertSurveyorLogin(imei, surveyorId);

		}
		catch (Exception ex)
		{
			logger.error("Exception while validation surveyor m code : " + mCode + ", imei : "
					+ imei, ex);
			return false;
		}
		return true;
	}

	private int isValidCode(String mCode, String password, SurveyorType surveyorType,
			HttpServletResponse response)
	{
		try
		{

			Surveyor surveyor = surveyorDao.authenticateSurveyor(mCode, surveyorType.toString());
			if (surveyor != null && surveyor.getActive().equalsIgnoreCase("N"))
			{

				CommonUtil.writeErrorMessage(HttpServletResponse.SC_UNAUTHORIZED,
						MessageType.USER_DISABLED, response);
				return 0;

			}
			else if (surveyor == null || surveyor.getPassword().isEmpty()
					|| surveyor.getPassword() == null)
			{

				CommonUtil.writeErrorMessage(HttpServletResponse.SC_UNAUTHORIZED,
						MessageType.ERROR_AUTHORIZATION, response);
				return 0;
			}
			else if (surveyor != null)
			{
				if (PasswordStorage.verifyPassword(password, surveyor.getPassword()))
				{
					return surveyor.getId();
				}
				else
				{
					CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
							MessageType.INVALID_CREDENTIALS, response);
					return 0;
				}

			}

		}
		catch (Exception e)
		{
			logger.error(e, e);
		}
		return 0;
	}

	@Override
	public Map<String, Object> updatePassword(HttpServletRequest request,
			HttpServletResponse respons, String mCode, String oldPassword, String newPassword)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();

		try
		{
			String surveyorType = request.getHeader(SysConstants.SURVEYOR_TYPE);
			surveyorType = StringUtils.isNullOrEmptyString(surveyorType) ? "DE" : surveyorType;
			logger.info("SURVEYOR_TYPE : " + surveyorType);
			int surveyorId = StringUtils.parseInt(request.getHeader(SysConstants.SURVEYOR_ID), -1);
			String appType = request.getHeader(SysConstants.APP_TYPE);
			appType = StringUtils.isNullOrEmptyString(appType) ? "DE" : appType;
			if(appType.equalsIgnoreCase("DSR")){
				map = (HashMap<String, Object>) this.updateDSRPassword(oldPassword, newPassword, mCode,
						surveyorId, respons, surveyorType);
			} else {
				map = (HashMap<String, Object>) this.updatePassword(oldPassword, newPassword, mCode,
						surveyorId, respons, surveyorType);
			}
			
			return map;
		}
		catch (Exception e)
		{
			logger.error("Exception Occured while updating password : Exception :" + e);
			return map;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private Map<String, Object> updatePassword(String oldPassword, String newPassword,
			String mCode, int surveyorId, HttpServletResponse respons, String surveyortype)
			throws Exception
	{

		Surveyor surveyor = surveyorDao.authenticateSurveyor(mCode, surveyortype);
		if (PasswordStorage.verifyPassword(oldPassword, surveyor.getPassword()))
		{
			if (surveyorDao.updatePassword(mCode, PasswordStorage.createHash(newPassword)) > 0)
			{
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_OK,
						MessageType.PASSWORD_UPDATE_SUCCESS, respons);
			}
			else
			{
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.PASSWORD_UPDATE_ERROR, respons);
			}
		}
		else
		{
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_UNAUTHORIZED,
					MessageType.INVALID_CREDENTIALS, respons);
		}

		return null;

	}

	@Override
	public boolean validateDsr(String dCode, String password, SurveyorType surveyorType,
			String imei, HttpServletResponse response)
	{
		try
		{
			int dsrId = isValidDsrCode(dCode, password, surveyorType, response);
			if (dsrId == 0 || !isValidImei(imei, response))
			{
				return false;
			}
			surveyorDao.insertSurveyorLogin(imei, dsrId);

		}
		catch (Exception ex)
		{
			logger.error("Exception while validation dsr code : " + dCode + ", imei : " + imei, ex);
			return false;
		}
		return true;
	}

	private int isValidDsrCode(String dCode, String password, SurveyorType surveyorType,
			HttpServletResponse response)
	{
		try
		{

			Dsr dsr = surveyorDao.authenticateDsr(dCode, surveyorType.toString());
			if (dsr != null && dsr.getActive().equalsIgnoreCase("N"))
			{

				CommonUtil.writeErrorMessage(HttpServletResponse.SC_UNAUTHORIZED,
						MessageType.USER_DISABLED, response);
				return 0;

			}
			else if (dsr == null || dsr.getPassword().isEmpty() || dsr.getPassword() == null)
			{

				CommonUtil.writeErrorMessage(HttpServletResponse.SC_UNAUTHORIZED,
						MessageType.ERROR_AUTHORIZATION, response);
				return 0;
			}
			else if (dsr != null)
			{
				if (PasswordStorage.verifyPassword(password, dsr.getPassword()))
				{
					return dsr.getId();
				}
				else
				{
					CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
							MessageType.INVALID_CREDENTIALS, response);
					return 0;
				}

			}

		}
		catch (Exception e)
		{
			logger.error(e, e);
		}
		return 0;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private Map<String, Object> updateDSRPassword(String oldPassword, String newPassword,
			String mCode, int surveyorId, HttpServletResponse respons, String surveyortype)
			throws Exception
	{

		Surveyor surveyor = surveyorDao.authenticateDSRSurveyor(mCode, surveyortype);
		if (PasswordStorage.verifyPassword(oldPassword, surveyor.getPassword()))
		{
			if (surveyorDao.updateDsrPassword(mCode, PasswordStorage.createHash(newPassword)) > 0)
			{
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_OK,
						MessageType.PASSWORD_UPDATE_SUCCESS, respons);
			}
			else
			{
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.PASSWORD_UPDATE_ERROR, respons);
			}
		}
		else
		{
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_UNAUTHORIZED,
					MessageType.INVALID_CREDENTIALS, respons);
		}

		return null;

	}
}
