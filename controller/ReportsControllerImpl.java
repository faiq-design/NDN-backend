package com.marksman.census.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.marksman.census.bo.Area;
import com.marksman.census.bo.City;
import com.marksman.census.bo.ExportShop;
import com.marksman.census.bo.Region;
import com.marksman.census.bo.SurveyorShops;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.constants.DateTimeConstants;
import com.marksman.census.constants.SurveyorType;
import com.marksman.census.constants.SysConstants;
import com.marksman.census.dao.AreaDao;
import com.marksman.census.dao.DssSummaryDao;
import com.marksman.census.dao.ReportsDao;
import com.marksman.census.service.PortalLoggingServiceImpl;
import com.marksman.census.service.ReportsService;
import com.marksman.census.util.DateTimeUtilities;
import com.marksman.census.util.FileUtils;
import com.marksman.census.util.SpringApplicationContext;

@Controller
public class ReportsControllerImpl implements ReportsController {

	@Autowired
	ReportsService reportsService;

	@Autowired
	DssSummaryDao dssSummaryDao;

	@Autowired
	ApplicationCacheService applicationCacheService;

	@Autowired
	AreaDao areaDao;

	@Autowired
	ReportsDao reportsDao;

	@Autowired
	ServletContext servletContext;
	@Autowired
	PortalLoggingServiceImpl portalLoggingService;

	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void exportData(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, String startDate, String endDate,
			int actionType, int[] viewType, Integer channel,
			HttpServletRequest request, HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"EXPORT_DATA_REPORT");
		reportsService.createExportData(response, request, zoneId, regionId,
				townId, areaId, typeId, startDate, endDate, actionType,
				viewType, channel);
	}

	@Override
	public void dsrSaleData(String startDate, String endDate, String zoneId,
			String regionId, String townId, float measurmentFactor,
			HttpServletRequest request, HttpServletResponse response,
			String status, String measurmentTitle, Integer userId,
			Integer reportType) {
		String reportTitle = reportType == SysConstants.DSR_SALE_OLD ? "DSR Sale Data updated Report"
				: reportType == SysConstants.DSR_SALE_NEW ? "DSR_Sale_V2_Report"
						: "DSR_Sale_V3_Report";
		portalLoggingService.insertActivityLog(request, response, reportTitle);
		reportsService.createDsrSaleData(response, request, startDate, endDate,
				zoneId, regionId, townId, measurmentFactor, status,
				measurmentTitle, reportType);
	}

	@Override
	public void dsrSaleTargetSample(Integer month, Integer year,
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, Integer townId, Integer type) {

		reportsService.createDsrSaleTarget(response, request, month, year,
				zoneId, regionId, townId, type);
	}

	@Override
	public void deWWWRData(String startDate, String endDate, String zoneId,
			String regionId, String townId, HttpServletRequest request,
			HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"DE_WWWR_SUMMARY_REPORT");
		reportsService.createDeWWWRSummaryReport(response, request, startDate,
				endDate, zoneId, regionId, townId);

	}

	@Override
	public void brandAvailability(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer typeId, String startDate,
			String endDate, int actionType, int[] viewType,
			HttpServletRequest request, HttpServletResponse response) {

		reportsService.createBrandAvailability(response, zoneId, regionId,
				townId, areaId, typeId, startDate, endDate, actionType,
				viewType);
	}

	@Override
	public void sourcesOfPurchase(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer typeId, String startDate,
			String endDate, int actionType, int[] viewType,
			HttpServletRequest request, HttpServletResponse response) {

		reportsService.createSourcesOfPurchase(response, zoneId, regionId,
				townId, areaId, typeId, startDate, endDate, actionType,
				viewType);
	}

	@Override
	public void billImages(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, String startDate, String endDate,
			int actionType, int[] viewType, HttpServletRequest request,
			HttpServletResponse response) {

		reportsService.createBillImages(request, response, zoneId, regionId,
				townId, areaId, typeId, startDate, endDate, actionType,
				viewType);
	}

	@Override
	public void timeSpent(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, String startDate, String endDate,
			int actionType, int[] viewType, SurveyorType surveyorType,
			HttpServletRequest request, HttpServletResponse response) {
		reportsService.createTimeSpent(response, zoneId, regionId, townId,
				areaId, typeId, startDate, endDate, actionType, viewType,
				surveyorType);
	}

	@Override
	public void redFlagData(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, int actionType,
			HttpServletRequest request, HttpServletResponse response) {

		reportsService.createRedFlag(response, zoneId, regionId, townId,
				areaId, actionType);
	}

	@Override
	public void exportDssSummaryData(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, int actionType,
			HttpServletRequest request, HttpServletResponse response) {
		reportsService.createDssSummary(response, zoneId, regionId, townId,
				areaId, actionType);
	}

	@Override
	public void exportRegionalSummaryData(HttpServletRequest request,
			HttpServletResponse response) {
		reportsService.createRegionalSummary(response);
	}

	@Override
	public void exportQcEfficiencyData(HttpServletRequest request,
			HttpServletResponse response) {
		reportsService.createQcEfficiency(response);
	}

	@Override
	public void exportAsmSummaryData(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, int actionType,
			HttpServletRequest request, HttpServletResponse response) {
		reportsService.createAsmSummary(response, zoneId, regionId, townId,
				areaId, actionType);
	}

	@Override
	public ModelAndView redFlagDataGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/redFlagSummary");
			reportsService.setAttributes(modelAndView);
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while generating red flag summary jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public @ResponseBody
	Map<String, Object> redFlagDataList(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer typeId,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responseMap = null;
		try {
			responseMap = new HashMap<String, Object>();
			List<ExportShop> surveyorList = dssSummaryDao.getRedFlagShopList(
					zoneId, regionId, townId, areaId, typeId);
			responseMap.put("surveyorList", surveyorList);
		} catch (Exception ex) {

			logger.error("Error while getting red flag list jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return responseMap;
	}

	@Override
	public ModelAndView exportDataGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/exportData");
			modelAndView = reportsService.setAttributes(modelAndView);
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while getting export data jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public ModelAndView brandAvailabilityGet(HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/brandAvailability");
			modelAndView = reportsService.setAttributes(modelAndView);
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while getting brand availability data jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public ModelAndView sourcesOfPurchaseGet(HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/sourcesOfPurchase");
			modelAndView = reportsService.setAttributes(modelAndView);
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while getting sources of purchase data jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public ModelAndView billImagesGet(HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/billImages");
			modelAndView = reportsService.setAttributes(modelAndView);
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while getting bill images data jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public ModelAndView timeSpentGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/timeSpent");
			modelAndView = reportsService.setAttributes(modelAndView);
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while getting time spent jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public ModelAndView asmAssignedShopsGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/asmAssignedShops");
			modelAndView = reportsService.setAttributes(modelAndView);
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while getting ASM assigned shops jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public ModelAndView evaluatedShopsGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/evaluatedShops");
			modelAndView = reportsService.setAttributes(modelAndView);
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while getting evaluated jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public @ResponseBody
	Map<String, Object> ajax(Integer zoneId, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> responseMap = null;
		try {
			responseMap = new HashMap<String, Object>();
			if (zoneId != null) {
				ArrayList<Region> regionList = (ArrayList<Region>) applicationCacheService
						.getRegionsByZoneId(zoneId);
				responseMap.put("regionList", regionList);
			}
		} catch (Exception ex) {

			logger.error("Error while Getting Region Against Zone", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return responseMap;
	}

	@Override
	public @ResponseBody
	Map<String, Object> townAjax(Integer regionId, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> responseMap = null;
		try {
			responseMap = new HashMap<String, Object>();
			if (regionId != null) {

				ArrayList<City> cityList = (ArrayList<City>) applicationCacheService
						.getCitiesByRegionId(regionId);
				ArrayList<Area> areaList = (ArrayList<Area>) areaDao
						.getAreasByRegionId(regionId);
				responseMap.put("town", cityList);
				responseMap.put("area", areaList);
			}
		} catch (Exception ex) {

			logger.error("Error while Getting Region Against Zone", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return responseMap;
	}

	@Override
	public ModelAndView exportDssSummaryGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/dssSummary");
			reportsService.setAttributes(modelAndView);
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while uploading parental file", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public ModelAndView exportRegionalSummaryGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/regionalSummary");
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while getting regional summary jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public ModelAndView exportQcEfficiencyGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/qcEfficiencySummary");
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while getting qc efficiency jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public @ResponseBody
	Map<String, Object> exportDssSummaryDataList(Integer zoneId,
			Integer regionId, Integer townId, Integer areaId,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responseMap = null;
		try {
			responseMap = new HashMap<String, Object>();
			List<SurveyorShops> surveyorList = dssSummaryDao.getDsrShopSummary(
					zoneId, regionId, townId, areaId);
			responseMap.put("surveyorList", surveyorList);
		} catch (Exception ex) {

			logger.error("Error while Getting Region Against Zone", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return responseMap;
	}

	@Override
	public ModelAndView exportAsmSummaryGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/asmSummary");
			reportsService.setAttributes(modelAndView);
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while uploading parental file", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public @ResponseBody
	Map<String, Object> exportAsmSummaryDataList(Integer zoneId,
			Integer regionId, Integer townId, Integer areaId,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responseMap = null;
		try {
			responseMap = new HashMap<String, Object>();
			List<SurveyorShops> surveyorList = dssSummaryDao.getAsmSummary(
					zoneId, regionId, townId, areaId);
			responseMap.put("surveyorList", surveyorList);
		} catch (Exception ex) {

			logger.error("Error while Getting Region Against Zone", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return responseMap;
	}

	@Override
	public void asmAssignedShops(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, int actionType,
			HttpServletRequest request, HttpServletResponse response) {
		reportsService.asmAssignedShops(response, zoneId, regionId, townId,
				areaId, actionType);
	}

	@Override
	public void evaluatedShops(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, String startDate, String endDate,
			int actionType, HttpServletRequest request,
			HttpServletResponse response) {
		reportsService.evaluatedShops(response, zoneId, regionId, townId,
				areaId, startDate, endDate, actionType);
	}

	@Override
	public ModelAndView outOfScopeShopsGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/outOfScopeShops");
			modelAndView = reportsService.setAttributes(modelAndView);
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while getting out of scope shop jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public void outOfScopeShops(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, int actionType,
			HttpServletRequest request, HttpServletResponse response) {
		reportsService.outOfScopeShops(response, zoneId, regionId, townId,
				areaId, actionType);
	}

	@Override
	public ModelAndView activityLogGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/activityLog");
			modelAndView = reportsService.setAttributes(modelAndView);
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Error while getting activity log jsp", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public void activityLog(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, int actionType, String startDate, String endDate,
			HttpServletRequest request, HttpServletResponse response) {
		reportsService.activityLog(response, zoneId, regionId, townId, areaId,
				actionType, startDate, endDate);
	}

	@Override
	public void dailyEvaluationReport(String startDate, String endDate,
			String zoneId, String regionId, String townId, int actionType,
			HttpServletRequest request, HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				actionType == 1 ? "DAILY_EVALUATION_VISIT_WISE_REPORT"
						: "DAILY_EVALUATION_DATE_WISE_REPORT");
		reportsService.dailyEvaluationReport(response, request, startDate,
				endDate, zoneId, regionId, townId, actionType);

	}

	@Override
	public void attendanceReport(String startDate, String endDate, String zoneId,
			String regionId, String townId, int actionType,
			HttpServletRequest request, HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"ATTENDANCE_REPORT");
		reportsService.attendanceReport(response, request, startDate, endDate,
				zoneId, regionId, townId, actionType);

	}

	@Override
	public void rawDataReport(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate,
			Integer typeId, String zoneId, String regionId, String cityId, Integer userId, String month, String year) {
		Map<String, Object> reportData = reportsDao.getQuery(typeId);
		portalLoggingService.insertActivityLog(request, response,  reportData
				.get("title").toString());
		reportsService.createCsvReport(request, response, startDate, endDate,
				reportData, zoneId, regionId, cityId, month, year);

	}

	@Override
	public void downloadCsvReport(HttpServletRequest request,
			HttpServletResponse response, String key, String fileType) {

		String directory = File.separator + "portalGeneratedReports"
				+ File.separator + DateTimeUtilities.getCurrentDate()
				+ File.separator;
		String path = directory + key + fileType;
		String deleteFilePath = servletContext.getRealPath("/") + path;
		if (fileType != null && fileType.equalsIgnoreCase(".csv")) {

			response.setContentType("application/zip");
			response.setHeader("Content-Disposition", "attachment;filename="
					+ key.split("_")[0] + ".zip");

			byte[] buffer = new byte[1024];
			try {
				FileInputStream in = new FileInputStream(deleteFilePath);
				ZipOutputStream zos = new ZipOutputStream(
						response.getOutputStream());
				ZipEntry ze = new ZipEntry("Dashboard" + fileType);
				zos.putNextEntry(ze);

				int len;
				while ((len = in.read(buffer)) > 0) {

					zos.write(buffer, 0, len);
				}
				in.close();
				zos.closeEntry();
				zos.close();
				deleteReport(deleteFilePath);

			} catch (Exception ex) {
				logger.error(ex, ex);
			}

		} else if (fileType != null && fileType.equalsIgnoreCase(".xlsx")) {
			try {
				// response.setContentType("application/vnd.ms-excel");
				response.setContentType("txt/plain");
				response.setHeader("Content-Disposition",
						"attachment;filename=" + key.split("_")[0] + "."
								+ fileType);

				InputStream in = request.getServletContext()
						.getResourceAsStream(path);
				OutputStream out = response.getOutputStream();

				byte[] buffer = new byte[1048];

				int numBytesRead;
				while ((numBytesRead = in.read(buffer)) > 0) {
					out.write(buffer, 0, numBytesRead);
				}

				deleteReport(deleteFilePath);
				out.flush();
				out.close();

			} catch (Exception e) {
				logger.error(e, e);
			}
		}

	}

	private void deleteReport(final String deleteFilePath) {
		new Thread() {
			public void run() {
				FileUtils.deleteFile(deleteFilePath);
			}
		}.start();
	}

	@Override
	public void downloadReport(HttpServletRequest request,
			HttpServletResponse response, String key, String fileType) {
		try {

			String directory = File.separator + "portalGeneratedReports"
					+ File.separator + DateTimeUtilities.getCurrentDate()
					+ File.separator;

			key = key + "." + fileType;

			String path = directory + key;
			logger.info("downloading report from directory :: " + path);
			String deleteFilePath = servletContext.getRealPath("/") + path;
			// response.setContentType("application/vnd.ms-excel");
			response.setContentType("txt/plain");
			response.setHeader("Content-Disposition", "attachment;filename="
					+ key.split("_")[0] + "." + fileType);

			InputStream in = request.getServletContext().getResourceAsStream(
					path);
			OutputStream out = response.getOutputStream();

			byte[] buffer = new byte[1048];

			int numBytesRead;
			while ((numBytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, numBytesRead);
			}

			deleteReport(deleteFilePath);
			out.flush();
			out.close();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	@Override
	public void SalesAchievementReport(int year, int month, int zoneId,
			int regionId, int townId, int typeId, HttpServletRequest request,
			HttpServletResponse response) {

		portalLoggingService.insertActivityLog(request, response,
				"Sales_Achievement_Report");
		reportsService.createSaleAchievementReport(response, request, year,
				month, zoneId, regionId, townId, typeId);
	}

	@Override
	public void saleAchievementSummary(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate,
			Integer zoneId, Integer regionId, Integer townId, Integer userId,
			Integer unitValue, String unitTitle) {
		portalLoggingService.insertActivityLog(request, response,
				"Sale_Achievement_Summary");
		reportsService.saleAchievementSummary(response, request, startDate,
				endDate, zoneId, regionId, townId, unitValue, unitTitle);
	}

	@Override
	public void factoryStockReport(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate,
			String zoneId, String regionId, String townId, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"Factory_Stock_Report");
		reportsService.factoryStockReport(response, request, startDate,
				endDate, zoneId, regionId, townId);

	}

	@Override
	public void tmWWWRSummary(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate,
			Integer zoneId, Integer regionId, Integer townId, Integer userId) {

		portalLoggingService.insertActivityLog(request, response,
				"TM_WWWR_SUMMARY");
		reportsService.tmWWWRSummary(response, request, startDate, endDate,
				zoneId, regionId, townId);

	}

	// this is for xlsm formate report
	@Override
	public void dsrSaleDataXlsm(String startDate, String endDate, int zoneId,
			int regionId, int townId, float measurmentFactor,
			HttpServletRequest request, HttpServletResponse response,
			String status, String measurmentTitle, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"DSR_SALE_DATA_REPORT_XLSM");
		reportsService.dsrSaleDataXlsm(response, request, startDate, endDate,
				zoneId, regionId, townId, measurmentFactor, status,
				measurmentTitle);

	}

	@Override
	public void stockLedgerReport(String startDate, String endDate, String zoneId,
			String regionId, Integer reportType, String townId,String projectName,
			HttpServletRequest request, HttpServletResponse response) {
		String reportTitle = reportType == SysConstants.FACTORY_STOCK_LEDGER ? "Factory_Stock_Ledger_Report"
				: reportType == SysConstants.TERRITORY_STOCK_LEDGER ? "Territory_Stock_Ledger_Report"
						: "Town_Stock_Ledger_Report";
		portalLoggingService.insertActivityLog(request, response, reportTitle);
		reportsService.stockLedgerReport(response, request, startDate, endDate,
				zoneId, regionId, townId, reportType, projectName);

	}

	@Override
	public void attendanceReportNew(String startDate, String endDate,
			String zoneId, String regionId, String townId, int actionType,
			HttpServletRequest request, HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"ATTENDANCE_REPORT_NEW");
		reportsService.attendanceReportNew(response, request, startDate,
				endDate, zoneId, regionId, townId, actionType);
	}

	@Override
	public void sisExportData(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, String startDate, String endDate,
			int actionType, int[] viewType, String exportType,
			HttpServletRequest request, HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"SIS_EXPORT_DATA_REPORT");
		reportsService.createSisExportData(response, request, zoneId, regionId,
				townId, areaId, typeId, startDate, endDate, actionType,
				viewType, exportType);
	}

	@Override
	public void deWholesaleReport(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer typeId, String startDate,
			String endDate, HttpServletRequest request,
			HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"DE_WHOLESALE_SALE_REPORT");
		reportsService.deWholesaleReport(response, request, zoneId, regionId,
				townId, areaId, typeId, startDate, endDate);
	}

	@Override
	public void deTksReport(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, String startDate, String endDate,
			HttpServletRequest request, HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"DE_TKS_SALE_REPORT");
		reportsService.deTksReport(response, request, zoneId, regionId, townId,
				areaId, typeId, startDate, endDate);
	}

	@Override
	public void wsTksSaleTargetSample(Integer month, Integer year,
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, Integer townId, String shopType) {

		reportsService.createWSTKSSaleTarget(response, request, month, year,
				zoneId, regionId, townId, shopType);
	}

	@Override
	public void getDSRSalesLog(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate) {
		portalLoggingService.insertActivityLog(request, response,
				"DSR_SALES_LOG");
		reportsService.createDSRSalesLog(response, request, startDate, endDate);
	}

	@Override
	public void RAShopSample(HttpServletRequest request,
			HttpServletResponse response) {

		reportsService.createRAShopSample(response, request);
	}

	@Override
	public void marketSurveyReport(Integer townId, Integer month, Integer year,
			String type, Integer typeId, HttpServletRequest request,
			HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"SIS_MARKET_SURVEY_REPORT");
		reportsService.marketSurveyReport(response, request, townId, month,
				year, type, typeId);
	}

	@Override
	public void masterDataReport(Integer townId, Integer month, Integer year,
			Integer regionId, Integer zoneId, HttpServletRequest request,
			HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"SIS_MASTER_DATA_REPORT");
		reportsService.masterDataReport(response, request, townId, month, year,
				regionId, zoneId);
	}

	@Override
	public void wsComaprisonReport(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer typeId, String startDate,
			String endDate, HttpServletRequest request,
			HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"WS_COMPARISON_REPORT");
		reportsService.wsComaprisonReport(response, request, zoneId, regionId,
				townId, areaId, typeId, startDate, endDate);
	}

	@Override
	public void marketSurveyReportNew(Integer townId, Integer month,
			Integer year, String type, HttpServletRequest request,
			HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"SIS_MARKET_SURVEY_REPORT_NEW");
		reportsService.marketSurveyReportNew(response, request, townId, month,
				year, type);
	}

	@Override
	public void marketSurveyReportDashboard(Integer townId, Integer month,
			Integer year, String type, HttpServletRequest request,
			HttpServletResponse response) {
		reportsService.marketSurveyReportDashboard(response, request, townId,
				month, year, type);
	}

	@Override
	public void dsrExportData(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, String startDate, String endDate,
			int actionType, int[] viewType, HttpServletRequest request,
			HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"DSR_EXPORT_DATA_REPORT");
		reportsService.createDsrExportData(response, request, zoneId, regionId,
				townId, areaId, typeId, startDate, endDate, actionType,
				viewType);
	}

	@Override
	public void dsrWiseProductivity(String zoneId, String regionId,
			String townId, Integer areaId, Integer typeId, String startDate,
			String endDate, HttpServletRequest request,
			HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"DSR_WISE_PRODUCTIVITY");
		reportsService.dsrWiseProductivity(response, request, zoneId, regionId,
				townId, areaId, typeId, startDate, endDate);
	}

	@Override
	public void tmProductivityReport(Integer surveyorId, String startDate,
			String endDate, HttpServletRequest request,
			HttpServletResponse response) {

		reportsService.tmProductivityReport(response, request, surveyorId,
				startDate, endDate);
	}

	@Override
	public void dsrRouteTaggingReport(String zoneId, String regionId,
			String townId, String areaId, Integer typeId, String startDate,
			String endDate, HttpServletRequest request,
			HttpServletResponse response) {
		reportsService.dsrRouteTaggingReport(response, request, zoneId,
				regionId, townId, areaId, typeId, startDate, endDate);
	}

	@Override
	public void dsrMTDReport(String startDate, String endDate, int zoneId,
			int regionId, int townId, HttpServletRequest request,
			HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"DSR_MTD_SUMMARY_REPORT");
		reportsService.createDsrMTDSummaryReport(response, request, startDate,
				endDate, zoneId, regionId, townId);

	}

	@Override
	public void dsrPerformance(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate,
			String zoneId, String regionId, String townId, Integer userId, Integer typeId, String projectName) {
		portalLoggingService.insertActivityLog(request, response,
				"Dsr_Performance_Report");
		reportsService.dsrPerformance(response, request, startDate, endDate,
				zoneId, regionId, townId, typeId, projectName);

	}

	@Override
	public void exportDataReportV3(HttpServletRequest request,
			HttpServletResponse response, String zoneId, String regionId,
			String townId, String startDate, String endDate, String measurmentTitle) {
		portalLoggingService.insertActivityLog(request, response,
				"Export_Data_Report_V3");
		reportsService.exportDataReportV3(response, request, startDate,
				endDate, zoneId, regionId, townId, measurmentTitle);
		                                           
	}
	
	@Override
	public void dsrSaleV4Data(String startDate, String endDate, String zoneId,
			String regionId, String townId, float measurmentFactor,
			HttpServletRequest request, HttpServletResponse response,
			String status, String measurmentTitle, Integer userId,
			Integer reportType) {
		portalLoggingService.insertActivityLog(request, response, "DSR_Sale_V4_Report");
		reportsService.createDsrSaleV4Data(response, request, startDate, endDate,
				zoneId, regionId, townId, measurmentFactor, status,
				measurmentTitle, reportType);
	}
	
	@Override
	public void topSalesData(String startDate, String endDate,
			HttpServletRequest request, HttpServletResponse response,
			 String measurmentTitle, Integer userId,
			String type, String zoneId,String regionId,String townId ) {
		portalLoggingService.insertActivityLog(request, response, "Top_Sales_Report");
		reportsService.createTopSalesData(response, request, startDate, endDate,
				measurmentTitle,type,   zoneId,  regionId,  townId);
	}
	
	@Override
	public void sisSupervisorReport(Integer townId, Integer month, Integer year,
			Integer regionId, Integer zoneId, Integer typeId, HttpServletRequest request,
			HttpServletResponse response) {
		portalLoggingService.insertActivityLog(request, response,
				"SIS_SUPERVISOR_DATA_REPORT");
		reportsService.sisSupervisorReport(response, request, townId, month, year,
				regionId, zoneId, typeId);
	}
	
}
