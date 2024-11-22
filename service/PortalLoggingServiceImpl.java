/**
 * 
 */
package com.marksman.census.service;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.marksman.census.bo.AdminProfile;
import com.marksman.census.dao.PortalDao;
import com.marksman.census.util.InetAddressUtil;
import com.marksman.census.util.StringUtils;

/**
 * @author Muhammad Awais
 * 
 */
public class PortalLoggingServiceImpl implements LoggingService
{

	@Autowired
	PortalDao portalDao;
	@Autowired
	PortalService portalService;

	@Override
	public void insertActivityLog(HttpServletRequest request, HttpServletResponse response,
			String requestType)
	{
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
		int userId = -1;
		if (StringUtils.isNullOrEmptyString(param.toString()))
		{
			// Gson gson = new Gson();
			JsonObject jsonObject = new JsonParser().parse(requestType).getAsJsonObject();
			userId = jsonObject.get("evaluatorId").getAsInt();
			param = new StringBuilder(requestType);
			requestType = "EVALUATE_SHOP";
		}
		else
		{
			userId = StringUtils.parseInt(request.getParameter("userId"), -1);

		}
		AdminProfile adminProfile = portalService.findUserById(userId);

		if (adminProfile == null)
		{
			adminProfile = new AdminProfile();
			adminProfile.setUserId(-1);
			adminProfile.setUserName(null);

		}
		String ipAddress = InetAddressUtil.getAddressFromRequest(request);
		String requestedUrl = request.getRequestURL().toString();
		adminProfile.setIpAddress(ipAddress);
		portalDao.insertPortalActivity(adminProfile, header.toString(), param.toString(),
				requestedUrl, requestType);

	}

}
