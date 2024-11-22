package com.marksman.census.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.marksman.census.dao.ShopsDao;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.message.MessageType;
import com.marksman.census.util.CommonUtil;

public class DataServiceImpl implements DataService
{

	@Autowired
	private SurveyorDao surveyorDao;
	@Autowired
	private ShopsDao shopsDao;
	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public Map<String, Object> refresh(String imei, String mCode, HttpServletResponse response)
	{

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		try
		{

			jsonMap.put("redFlag", shopsDao.getShopsStatus(mCode));
		}
		catch (Exception ex)
		{

			logger.error("Exception occured while refreshing against imei " + imei, ex);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return jsonMap;
	}
}
