package com.marksman.census.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.marksman.census.util.XlsxUtilities;

public class InitializeService
{
	@Autowired
	private CacheService cacheService;
	protected final Logger logger = Logger.getLogger(this.getClass());

	public void init()
	{
		try
		{
			logger.info("****************Build cache started*************************");
			cacheService.buildCache();
			logger.info("****************Build cache ended***************************");
		}
		catch (Exception e)
		{
			logger.error(e, e);
		}
		XlsxUtilities.setReportTheme();
	}

}
