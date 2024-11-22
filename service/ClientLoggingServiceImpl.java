/**
 * 
 */
package com.marksman.census.service;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.marksman.census.constants.SysConstants;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.util.StringUtils;

/**
 * @author Muhammad Awais
 * 
 */
public class ClientLoggingServiceImpl implements LoggingService
{

	@Autowired
	private SurveyorDao surveyorDao;
	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void insertActivityLog(HttpServletRequest request, HttpServletResponse response,
			String requestType)
	{
		try
		{

			String url = request.getRequestURL().toString();
			String version = request.getHeader(SysConstants.VERSION);
			String imei = request.getHeader(SysConstants.IMEI);

			Enumeration<?> en = request.getHeaderNames();

			StringBuilder header = new StringBuilder();

			StringBuilder param = new StringBuilder();
			while (en.hasMoreElements())
			{
				String str = (String) en.nextElement();
				header.append(str).append(":").append(request.getHeader(str)).append("; ");
			}

			en = request.getParameterNames();
			while (en.hasMoreElements())
			{
				String str = (String) en.nextElement();
				param.append(str).append(":").append(request.getParameter(str)).append("; ");
			}

			int shopId = StringUtils.parseInt(request.getParameter("shopId"), -1);
			String _surveyorId = request.getParameter("surveyorId") != null ? request
					.getParameter("surveyorId") : request.getHeader("surveyorid");
			String _dsrId = request.getParameter("dsrId") != null ? request
					.getParameter("dsrId") : request.getHeader("dsrId");
			int surveyorId = StringUtils.parseInt(_surveyorId, -1);
			int dsrId = StringUtils.parseInt(_dsrId, -1);
			String mCode = request.getParameter("mCode");
			surveyorDao.insertClientActivityLog(url, StringUtils.extractVersion(version), imei,
					shopId, surveyorId, mCode, header.toString(), param.toString(), requestType, dsrId);

		}
		catch (Exception e)
		{
			logger.error("while saving client activity log : " + e, e);
		}
	}
}
