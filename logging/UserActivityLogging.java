package com.marksman.census.logging;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.marksman.census.bo.Area;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.constants.ReportTitles;
import com.marksman.census.constants.UserActivity;
import com.marksman.census.dao.AreaDao;
import com.marksman.census.dao.SsrDao;
import com.marksman.census.service.SyncServiceImpl;
import com.marksman.census.util.SpringApplicationContext;
import com.marksman.census.util.StringUtils;

public class UserActivityLogging
{
	@Autowired
	ServletContext servletContext;
	protected static Logger logger = Logger.getLogger(SyncServiceImpl.class);

	@Autowired
	ApplicationCacheService applicationCacheService;
	@Autowired
	AreaDao areaDao;
	@Autowired
	SsrDao ssrDao;

	public void saveUserActivity(UserActivity userActivity, int reportType, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, String startDate, String endDate)
	{

		ssrDao.saveUserActivity(SpringApplicationContext.getAdmionProfile(), userActivity,
				getParamerters(reportType, zoneId, regionId, townId, areaId, startDate, endDate));
	}

	public String getParamerters(int reportType, Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, String startDate, String endDate)
	{

		StringBuilder parm = new StringBuilder();

		parm.append("ReportType = ").append(ReportTitles.geTitle(reportType));
		if (!StringUtils.isNullOrEmptyInteger(zoneId))
		{
			parm.append(" , Zone = ")
					.append(applicationCacheService.getZoneById(zoneId).getTitle());
		}
		if (!StringUtils.isNullOrEmptyInteger(regionId))
		{
			parm.append(" , Region = ").append(
					applicationCacheService.getRegionById(regionId).getTitle());
		}
		if (!StringUtils.isNullOrEmptyInteger(townId))
		{
			parm.append(" , Town = ")
					.append(applicationCacheService.getCityById(townId).getTitle());
		}
		if (!StringUtils.isNullOrEmptyInteger(areaId))
		{
			Area area = areaDao.getAreasById(areaId);
			parm.append(" , Area = ").append(area.getTitle());
		}
		parm.append(" , StartDate = ").append(startDate).append(" , EndDate = ").append(endDate);

		return parm.toString();
	}
}
