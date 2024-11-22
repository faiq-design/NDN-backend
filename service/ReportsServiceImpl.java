package com.marksman.census.service;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.marksman.census.bo.AdminProfile;
import com.marksman.census.bo.Area;
import com.marksman.census.bo.City;
import com.marksman.census.bo.Items;
import com.marksman.census.bo.Region;
import com.marksman.census.bo.Zone;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.constants.CommonConstants;
import com.marksman.census.constants.ReportTitles;
import com.marksman.census.constants.SurveyorType;
import com.marksman.census.constants.UserActivity;
import com.marksman.census.dao.AreaDao;
import com.marksman.census.dao.ReportsDao;
import com.marksman.census.logging.UserActivityLogging;
import com.marksman.census.report.ActivityLog;
import com.marksman.census.report.AsmSummary;
import com.marksman.census.report.AssignedShopList;
import com.marksman.census.report.AttendanceReport;
import com.marksman.census.report.AttendanceReportNew;
import com.marksman.census.report.BillImages;
import com.marksman.census.report.BrandAvailability;
import com.marksman.census.report.DailyEvaluationReport;
import com.marksman.census.report.DeTksReport;
import com.marksman.census.report.DeWWWRSummary;
import com.marksman.census.report.DeWholesaleReport;
import com.marksman.census.report.DsrExportData;
import com.marksman.census.report.DsrMTDReport;
import com.marksman.census.report.DsrPerformanceReport;
import com.marksman.census.report.DsrRouteTaggingReport;
import com.marksman.census.report.DsrSaleData;
import com.marksman.census.report.DsrSaleDataXlsm;
import com.marksman.census.report.DsrSaleTarget;
import com.marksman.census.report.DsrSaleV4Report;
import com.marksman.census.report.DsrWiseProductivityReport;
import com.marksman.census.report.DssSummary;
import com.marksman.census.report.EvaluatedShopList;
import com.marksman.census.report.ExportData;
import com.marksman.census.report.ExportDataV3Report;
import com.marksman.census.report.ExportToCsv;
import com.marksman.census.report.FactoryStockReport;
import com.marksman.census.report.OutOfScopeShopList;
import com.marksman.census.report.QcEfficiencySummary;
import com.marksman.census.report.RAExportData;
import com.marksman.census.report.RAShopSample;
import com.marksman.census.report.RedFlagShopList;
import com.marksman.census.report.RegionalSummary;
import com.marksman.census.report.SaleAchievement;
import com.marksman.census.report.SaleAchievementSummary;
import com.marksman.census.report.SisExportData;
import com.marksman.census.report.SisMarketSurveyDashboard;
import com.marksman.census.report.SisMarketSurveyReportNew;
import com.marksman.census.report.SisMasterDataReport;
import com.marksman.census.report.SisMonthlySurveyReport;
import com.marksman.census.report.SisSupervisorReport;
import com.marksman.census.report.SourcesOfPurchase;
import com.marksman.census.report.StockLedgerReport;
import com.marksman.census.report.TimeSpentShopList;
import com.marksman.census.report.TmProductivityReport;
import com.marksman.census.report.TmWWWRSummary;
import com.marksman.census.report.TopSalesReport;
import com.marksman.census.report.WSTKSSaleTarget;
import com.marksman.census.report.DsrSalesLog;
import com.marksman.census.report.WholesaleComparisonReport;
import com.marksman.census.util.DateTimeUtilities;
import com.marksman.census.util.FileUtils;
import com.marksman.census.util.SpringApplicationContext;
import com.marksman.census.util.StringUtils;


public class ReportsServiceImpl implements ReportsService {

	@Autowired
	private ExportData exportData;

	@Autowired
	private BrandAvailability brandAvailability;

	@Autowired
	private SourcesOfPurchase sourcesOfPurchase;

	@Autowired
	private BillImages billImages;

	@Autowired
	private TimeSpentShopList timeSpentShopList;

	@Autowired
	private RedFlagShopList redFlagShopList;

	@Autowired
	private DssSummary dssSummary;

	@Autowired
	private RegionalSummary regionalSummary;

	@Autowired
	private QcEfficiencySummary qcEfficiencySummary;

	@Autowired
	private AsmSummary asmSummary;

	@Autowired
	private AssignedShopList assignedShopList;

	@Autowired
	private EvaluatedShopList evaluatedShopList;

	@Autowired
	private OutOfScopeShopList outOfScopeShopList;

	@Autowired
	private ActivityLog activityLog;

	@Autowired
	private AreaDao areaDao;

	@Autowired
	private ApplicationCacheService applicationCacheService;

	@Autowired
	private UserActivityLogging userActivityLogging;

	@Autowired
	private DsrSaleData dsrSale;
	
	@Autowired
	private DsrSaleV4Report dsrSaleV4;
	
	@Autowired
	private TopSalesReport topSales;

	// this is for xlsm formate report
	@Autowired
	private DsrSaleDataXlsm dsrSaleXlsm;

	@Autowired
	private DsrSaleTarget dsrSaleTarget;

	@Autowired
	private DeWWWRSummary deWWWRSummary;

	@Autowired
	private DailyEvaluationReport dailyEvaluationReport;

	@Autowired
	private AttendanceReport attendanceReport;

	@Autowired
	private ExportToCsv exportCsv;

	@Autowired
	private SaleAchievement saleAchievement;

	@Autowired
	private SaleAchievementSummary saleAchievementSummary;

	@Autowired
	private FactoryStockReport factoryStockReport;

	@Autowired
	private TmWWWRSummary tmWWWRSummary;
	
	@Autowired
	private WSTKSSaleTarget wsTksSaleTarget;
	
	@Autowired
	private DsrSalesLog dsrSalesLog;

	@Autowired
	StockLedgerReport stockLedger;

	@Autowired
	AttendanceReportNew attendanceReportNew;
	@Autowired
	SisExportData sisExportData;
	@Autowired
	RAExportData raExportData;
	@Autowired
	DeWholesaleReport deWholesaleReport;
	@Autowired
	DeTksReport deTksReport;
	@Autowired
	ServletContext servletContext;
	@Autowired
	PortalLoggingServiceImpl portalLoggingService;
	@Autowired
	SisMonthlySurveyReport sisMonthlySurveyReport;
	@Autowired
	SisMasterDataReport sisMasterDataReport;
	@Autowired
	WholesaleComparisonReport wholesaleComparisonReport;
	@Autowired
	DsrWiseProductivityReport dsrWiseProductivityReport;
	@Autowired
	SisSupervisorReport sisSupervisorReport;
	
	@Autowired
	private RAShopSample raShopSample;
	@Autowired
	private ReportsDao reportsDao;
	@Autowired
	private SisMarketSurveyReportNew sisMarketSurveyReportNew;
	@Autowired
	private SisMarketSurveyDashboard sisMarketSurveyDashboard;
	@Autowired
	private DsrExportData dsrExportData;
	@Autowired
	private TmProductivityReport tmProductivityReport;
	@Autowired
	private DsrRouteTaggingReport dsrRouteTaggingReport;
	@Autowired
	private DsrMTDReport dsrMTDReport;
	@Autowired
	private DsrPerformanceReport dsrPerformanceReport;
	@Autowired
	private ExportDataV3Report exportDataV3Report;
	
	
	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void createExportData(HttpServletResponse resp,
			HttpServletRequest request, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer type, String startDate,
			String endDate, int reportType, int[] viewType, Integer channel) {

		portalLoggingService.insertActivityLog(request, resp, "EXPORT_DATA");
		exportData.createReport(resp, request, zoneId, regionId, townId,
				areaId, type, startDate, endDate, reportType, viewType, channel);

	}

	@Override
	public void createDsrSaleData(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, float measurmentFactor,
			String status, String measurmentTitle, Integer reportType) {

		dsrSale.createReport(resp, request, startDate, endDate, zoneId,
				regionId, townId, measurmentFactor, status, measurmentTitle, reportType);

	}

	@Override
	public void createDsrSaleTarget(HttpServletResponse resp,
			HttpServletRequest request, int month, int year, Integer zoneId,
			Integer regionId, Integer townId, Integer type) {

		dsrSaleTarget.createReport(resp, request, month, year, zoneId,
				regionId, townId, type);

	}

	@Override
	public void createDeWWWRSummaryReport(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId) {
		deWWWRSummary.createReport(resp, request, startDate, endDate, zoneId,
				regionId, townId);

	}

	@Override
	public void createSourcesOfPurchase(HttpServletResponse resp,
			Integer zoneId, Integer regionId, Integer townId, Integer areaId,
			Integer type, String startDate, String endDate, int reportType,
			int[] viewType) {

		userActivityLogging.saveUserActivity(UserActivity.SOURCES_OF_PURCHASE,
				reportType, zoneId, regionId, townId, areaId, startDate,
				endDate);
		sourcesOfPurchase.createReport(resp, zoneId, regionId, townId, areaId,
				type, startDate, endDate, reportType, viewType);

	}

	@Override
	public void createBillImages(HttpServletRequest request,
			HttpServletResponse resp, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer type, String startDate,
			String endDate, int reportType, int[] viewType) {

		userActivityLogging.saveUserActivity(UserActivity.BILL_IMAGES,
				reportType, zoneId, regionId, townId, areaId, startDate,
				endDate);
		billImages.createReport(request, resp, zoneId, regionId, townId,
				areaId, type, startDate, endDate, reportType, viewType);

	}

	@Override
	public void createBrandAvailability(HttpServletResponse resp,
			Integer zoneId, Integer regionId, Integer townId, Integer areaId,
			Integer type, String startDate, String endDate, int reportType,
			int[] viewType) {

		userActivityLogging.saveUserActivity(UserActivity.BRAND_AVAILABILITY,
				reportType, zoneId, regionId, townId, areaId, startDate,
				endDate);
		brandAvailability.createReport(resp, zoneId, regionId, townId, areaId,
				type, startDate, endDate, reportType, viewType);

	}

	@Override
	public void createTimeSpent(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, Integer type,
			String startDate, String endDate, int reportType, int[] viewType,
			SurveyorType surveyorType) {

		userActivityLogging.saveUserActivity(UserActivity.TIME_SPENT,
				reportType, zoneId, regionId, townId, areaId, startDate,
				endDate);
		timeSpentShopList.createReport(resp, zoneId, regionId, townId, areaId,
				type, startDate, endDate, reportType, viewType);

	}

	@Override
	public void createRedFlag(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, int reportType) {
		userActivityLogging.saveUserActivity(UserActivity.RED_FLAG, reportType,
				zoneId, regionId, townId, areaId, null, null);
		redFlagShopList.createReport(resp, zoneId, regionId, townId, areaId,
				reportType);
	}

	@Override
	public void createDssSummary(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, int reportType) {

		userActivityLogging.saveUserActivity(UserActivity.DSS_SUMMARY,
				reportType, zoneId, regionId, townId, areaId, null, null);
		dssSummary.createDssSummaryReport(resp, zoneId, regionId, townId,
				areaId, reportType);
	}

	@Override
	public void createRegionalSummary(HttpServletResponse resp) {

		userActivityLogging.saveUserActivity(UserActivity.REGIONAL_SUMMARY, 0,
				null, null, null, null, null, null);
		regionalSummary.createRegionalSummaryReport(resp);
	}

	@Override
	public void createQcEfficiency(HttpServletResponse resp) {

		userActivityLogging.saveUserActivity(UserActivity.QC_EFFICIENCY, 0,
				null, null, null, null, null, null);
		qcEfficiencySummary.createQcEfficiencyReport(resp);
	}

	@Override
	public void createAsmSummary(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, int reportType) {

		userActivityLogging.saveUserActivity(UserActivity.ASM_SUMMARY,
				reportType, zoneId, regionId, townId, areaId, null, null);
		asmSummary.createDssSummaryReport(resp, zoneId, regionId, townId,
				areaId, reportType);
	}

	@Override
	public void asmAssignedShops(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, int reportType) {
		userActivityLogging.saveUserActivity(UserActivity.ASSIGNED_SHOPS,
				reportType, zoneId, regionId, townId, areaId, null, null);
		assignedShopList.createReport(resp, zoneId, regionId, townId, areaId,
				reportType);
	}

	@Override
	public void evaluatedShops(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, String startDate,
			String endDate, int reportType) {
		userActivityLogging.saveUserActivity(UserActivity.EVALUATED_SHOPS,
				reportType, zoneId, regionId, townId, areaId, startDate,
				endDate);
		evaluatedShopList.createReport(resp, zoneId, regionId, townId, areaId,
				startDate, endDate, reportType);
	}

	@Override
	public void outOfScopeShops(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, int reportType) {
		userActivityLogging.saveUserActivity(UserActivity.OUT_OF_SCOPE,
				reportType, zoneId, regionId, townId, areaId, null, null);
		outOfScopeShopList.createReport(resp, zoneId, regionId, townId, areaId,
				reportType);
	}

	@Override
	public void activityLog(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, int reportType,
			String startDate, String endDate) {
		userActivityLogging.saveUserActivity(UserActivity.ACTIVITY_LOG,
				reportType, zoneId, regionId, townId, areaId, null, null);
		activityLog.createReport(resp, zoneId, regionId, townId, areaId,
				reportType, startDate, endDate);
	}

	public ModelAndView setAttributes(ModelAndView modelAndView) {

		String regionId = "";
		int reportType = 0;
		String type = ReportTitles.geTitle(reportType);
		String zoneId = null;

		AdminProfile userInfo = SpringApplicationContext.getAdmionProfile();

		if (userInfo != null) {
			if (userInfo.getZoneId() != -1) {
				modelAndView.addObject("zoneId", userInfo.getZoneId());
				modelAndView.addObject("zoneName", applicationCacheService
						.getZoneById(userInfo.getZoneId()).getTitle());
				zoneId = userInfo.getZoneId() + "";
				reportType = ReportTitles.ZONAL.getValue();
				type = ReportTitles.ZONAL.getTitle();
				if (userInfo.getRegionId() != -1) {
					modelAndView.addObject("regionId", userInfo.getRegionId());
					modelAndView.addObject(
							"regionName",
							applicationCacheService.getRegionById(
									userInfo.getRegionId()).getTitle());
					regionId = userInfo.getRegionId() + "";
					reportType = ReportTitles.REGIONAL.getValue();
					type = ReportTitles.REGIONAL.getTitle();
				} else {
					if (!StringUtils.isNullOrEmptyString(zoneId)) {
						ArrayList<Region> regionList = (ArrayList<Region>) applicationCacheService
								.getRegionsByZoneId(StringUtils.parseInt(
										zoneId, -1));
						modelAndView.addObject("regionList", regionList);
					}
				}
				if (userInfo.getAreaId() != -1) {
					modelAndView.addObject("areaId", userInfo.getAreaId());
					modelAndView.addObject("areaName",
							areaDao.getAreasById(userInfo.getAreaId())
									.getTitle());
					reportType = ReportTitles.AREA.getValue();
					type = ReportTitles.AREA.getTitle();
				} else {
					if (!StringUtils.isNullOrEmptyString(zoneId)
							&& !StringUtils.isNullOrEmptyString(regionId)) {
						ArrayList<City> cityList = (ArrayList<City>) applicationCacheService
								.getCitiesByRegionId(StringUtils.parseInt(
										regionId, -1));
						ArrayList<Area> areaList = (ArrayList<Area>) areaDao
								.getAreasByRegionId(StringUtils.parseInt(
										regionId, -1));
						modelAndView.addObject("areaList", areaList);
						modelAndView.addObject("townList", cityList);
					}
				}
			} else {
				ArrayList<Zone> zoneList = (ArrayList<Zone>) applicationCacheService
						.getZones();
				modelAndView.addObject("zoneList", zoneList);
				reportType = ReportTitles.NATIONAL.getValue();
			}
		} else {
			ArrayList<Zone> zoneList = (ArrayList<Zone>) applicationCacheService
					.getZones();
			modelAndView.addObject("zoneList", zoneList);
			reportType = ReportTitles.NATIONAL.getValue();
		}

		ArrayList<Items> typeList = getTypeList(reportType);

		modelAndView.addObject("typeList", typeList);
		modelAndView.addObject("regionId", regionId);
		modelAndView.addObject("reportType", reportType);
		modelAndView.addObject("type", type);
		modelAndView.addObject("zoneId", zoneId);

		return modelAndView;
	}

	public ArrayList<Items> getTypeList(int type) {
		ArrayList<Items> typeList = new ArrayList<Items>();

		if (type == 0) {
			typeList.add(new Items(ReportTitles.NATIONAL.getValue(),
					ReportTitles.NATIONAL.getTitle()));
			typeList.add(new Items(ReportTitles.ZONAL.getValue(),
					ReportTitles.ZONAL.getTitle()));
			typeList.add(new Items(ReportTitles.REGIONAL.getValue(),
					ReportTitles.REGIONAL.getTitle()));
			typeList.add(new Items(ReportTitles.TOWN.getValue(),
					ReportTitles.TOWN.getTitle()));
			typeList.add(new Items(ReportTitles.AREA.getValue(),
					ReportTitles.AREA.getTitle()));
		} else if (type == 1) {
			typeList.add(new Items(ReportTitles.ZONAL.getValue(),
					ReportTitles.ZONAL.getTitle()));
			typeList.add(new Items(ReportTitles.REGIONAL.getValue(),
					ReportTitles.REGIONAL.getTitle()));
			typeList.add(new Items(ReportTitles.TOWN.getValue(),
					ReportTitles.TOWN.getTitle()));
			typeList.add(new Items(ReportTitles.AREA.getValue(),
					ReportTitles.AREA.getTitle()));
		} else if (type == 2) {
			typeList.add(new Items(ReportTitles.REGIONAL.getValue(),
					ReportTitles.REGIONAL.getTitle()));
			typeList.add(new Items(ReportTitles.TOWN.getValue(),
					ReportTitles.TOWN.getTitle()));
			typeList.add(new Items(ReportTitles.AREA.getValue(),
					ReportTitles.AREA.getTitle()));
		} else if (type == 3) {
			typeList.add(new Items(ReportTitles.TOWN.getValue(),
					ReportTitles.TOWN.getTitle()));
		} else if (type == 4) {
			typeList.add(new Items(ReportTitles.AREA.getValue(),
					ReportTitles.AREA.getTitle()));
		}

		return typeList;
	}

	@Override
	public void dailyEvaluationReport(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, int actionType) {
		dailyEvaluationReport.createReport(response, zoneId, regionId, townId,
				startDate, endDate, actionType);

	}

	@Override
	public void attendanceReport(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, int actionType) {
		attendanceReport.createReport(response, zoneId, regionId, townId,
				startDate, endDate, actionType);

	}

	@Override
	public void createCsvReport(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate,
            Map<String, Object> reportData, String zoneId, String regionId, String cityId, String month, String year) {
        exportCsv.createReport(request, response, startDate, endDate, reportData, zoneId, regionId, cityId, month, year);

	}

	@Override
	public void createSaleAchievementReport(HttpServletResponse resp,
			HttpServletRequest request, int year, int month, int zoneId,
			int regionId, int townId, int typeId) {

		saleAchievement.createReport(resp, request, year, month, zoneId,
				regionId, townId, typeId);

	}

	@Override
	public void saleAchievementSummary(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			Integer zoneId, Integer regionId, Integer townId,
			Integer unitValue, String unitTitle) {
		saleAchievementSummary.createReport(response, zoneId, regionId, townId,
				startDate, endDate, unitValue, unitTitle);

	}

	@Override
	public void factoryStockReport(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId) {
		factoryStockReport.createReport(response, zoneId, regionId, townId,
				startDate, endDate);

	}

	@Override
	public void tmWWWRSummary(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			Integer zoneId, Integer regionId, Integer townId) {
		tmWWWRSummary.createReport(response, request, startDate, endDate,
				zoneId, regionId, townId);
	}

	// this is for xlsm formate report
	@Override
	public void dsrSaleDataXlsm(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			int zoneId, int regionId, int townId, float measurmentFactor,
			String status, String measurmentTitle) {
		dsrSaleXlsm.createReport(response, request, startDate, endDate, zoneId,
				regionId, townId, measurmentFactor, status, measurmentTitle);

	}

	@Override
	public void stockLedgerReport(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, Integer reportType, String projectName) {

		stockLedger.createReport(response, zoneId, regionId, townId, startDate,
				endDate, reportType, projectName);
	}

	@Override
	public void attendanceReportNew(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, int actionType) {
		attendanceReportNew.createReport(response, zoneId, regionId, townId,
				startDate, endDate, actionType);

	}

	@Override
	public void createSisExportData(HttpServletResponse resp,
			HttpServletRequest request, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer type, String startDate,
			String endDate, int reportType, int[] viewType, String exportType) {

		if(exportType.equalsIgnoreCase(CommonConstants.RETAIL_AUDIT))
		{
			raExportData.createReport(resp, request, zoneId, regionId, townId,
					areaId, type, startDate, endDate, reportType, viewType);
		}
		else
		{
			sisExportData.createReport(resp, request, zoneId, regionId, townId,
					areaId, type, startDate, endDate, reportType, viewType);
		}
		
		

	}

	@Override
	public void deWholesaleReport(HttpServletResponse resp,
			HttpServletRequest request, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer type, String startDate,
			String endDate) {
		try {

			JSONObject obj = new JSONObject();
			String folderPath = getFolderPath();
			String key = generateKey("DeWholesaleReport");
			String filePath = folderPath + key + ".xlsx";
			logger.info("Destination File Path = " + filePath);

			deWholesaleReport.createReport(resp, request, filePath, zoneId,
					regionId, townId, areaId, type, startDate, endDate);

			logger.info("Generated file key :: " + key);
			obj.put("key", key);
			obj.put("fileType", "xlsx");
			resp.setContentType("text/json");
			resp.getWriter().println(obj.toString());
			resp.getWriter().flush();
			resp.getWriter().close();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	@Override
	public void deTksReport(HttpServletResponse resp,
			HttpServletRequest request, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer type, String startDate,
			String endDate) {
		try {

			JSONObject obj = new JSONObject();
			String folderPath = getFolderPath();
			String key = generateKey("DeTksReport");
			String filePath = folderPath + key + ".xlsx";
			logger.info("Destination File Path = " + filePath);

			deTksReport.createReport(resp, request, filePath, zoneId,
					regionId, townId, areaId, type, startDate, endDate);

			logger.info("Generated file key :: " + key);
			obj.put("key", key);
			obj.put("fileType", "xlsx");
			resp.setContentType("text/json");
			resp.getWriter().println(obj.toString());
			resp.getWriter().flush();
			resp.getWriter().close();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	private String generateKey(String reportName) {
		return reportName + "_" + String.valueOf(System.currentTimeMillis())
				+ "_" + RandomStringUtils.randomNumeric(8);
	}

	private String getFolderPath() {
		String folderPath = servletContext.getRealPath("/")
				+ "/portalGeneratedReports/"
				+ DateTimeUtilities.getCurrentDate() + "/";
		if (!FileUtils.isExistsFolder(folderPath)) {
			FileUtils.createFolders(folderPath);
		}
		return folderPath;
	}
	
	@Override
	public void createWSTKSSaleTarget(HttpServletResponse resp,
			HttpServletRequest request, int month, int year, Integer zoneId,
			Integer regionId, Integer townId, String shopType) {

		wsTksSaleTarget.createReport(resp, request, month, year, zoneId,
				regionId, townId, shopType);

	}
	
	@Override
	public void createDSRSalesLog(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate) {
		
		try {

			JSONObject obj = new JSONObject();
			String folderPath = getFolderPath();
			String key = generateKey("DsrSalesLogReport");
			String filePath = folderPath + key + ".xlsx";
			logger.info("Destination File Path = " + filePath);

			dsrSalesLog.createReport(resp, request, filePath, startDate, endDate);

			logger.info("Generated file key :: " + key);
			obj.put("key", key);
			obj.put("fileType", "xlsx");
			resp.setContentType("text/json");
			resp.getWriter().println(obj.toString());
			resp.getWriter().flush();
			resp.getWriter().close();

		} catch (Exception e) {
			logger.error(e, e);
		}

	}
	
	@Override
	public void createRAShopSample(HttpServletResponse resp,
			HttpServletRequest request) {

		raShopSample.createReport(resp, request);

	}
	
	@Override
	public void marketSurveyReport(HttpServletResponse resp,
			HttpServletRequest request, Integer townId, Integer month,
			Integer year, String type, Integer typeId) {
		try {

			JSONObject obj = new JSONObject();
			String folderPath = getFolderPath();
			String cityName = reportsDao.getCityName(townId);
			String key = null;
			if(type.equalsIgnoreCase("PRO")){
				key = generateKey("PRO Market Survey Report " + cityName);
			} else {
				key = generateKey("KTC Market Survey Report " + cityName);
			}
			
			String filePath = folderPath + key + ".xlsx";
			logger.info("Destination File Path = " + filePath);

			sisMonthlySurveyReport.createReport(resp, request, filePath, townId,
					month, year, type, typeId);

			logger.info("Generated file key :: " + key);
			obj.put("key", key);
			obj.put("fileType", "xlsx");
			resp.setContentType("text/json");
			resp.getWriter().println(obj.toString());
			resp.getWriter().flush();
			resp.getWriter().close();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	@Override
	public void masterDataReport(HttpServletResponse resp,
			HttpServletRequest request, Integer townId, Integer month,
			Integer year, Integer regionId, Integer zoneId) {
		try {

			JSONObject obj = new JSONObject();
			String folderPath = getFolderPath();
			String key = generateKey("Master Data Report");
			String filePath = folderPath + key + ".xlsx";
			logger.info("Destination File Path = " + filePath);

			sisMasterDataReport.createReport(resp, request, filePath, townId,
					month, year, regionId, zoneId);

			logger.info("Generated file key :: " + key);
			obj.put("key", key);
			obj.put("fileType", "xlsx");
			resp.setContentType("text/json");
			resp.getWriter().println(obj.toString());
			resp.getWriter().flush();
			resp.getWriter().close();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	@Override
	public void wsComaprisonReport(HttpServletResponse resp,
			HttpServletRequest request, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer type, String startDate,
			String endDate) {
		try {

			JSONObject obj = new JSONObject();
			String folderPath = getFolderPath();
			String key = generateKey("WS COMPARISON ANALYSIS");
			String filePath = folderPath + key + ".xlsx";
			logger.info("Destination File Path = " + filePath);

			wholesaleComparisonReport.createReport(resp, request, filePath, zoneId,
					regionId, townId, areaId, type, startDate, endDate, "WHOLESALE_COMPARISON");

			logger.info("Generated file key :: " + key);
			obj.put("key", key);
			obj.put("fileType", "xlsx");
			resp.setContentType("text/json");
			resp.getWriter().println(obj.toString());
			resp.getWriter().flush();
			resp.getWriter().close();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	@Override
	public void marketSurveyReportNew(HttpServletResponse resp,
			HttpServletRequest request, Integer townId, Integer month,
			Integer year, String type) {
		try {

			JSONObject obj = new JSONObject();
			String folderPath = getFolderPath();
			String cityName = reportsDao.getCityName(townId);
			String key = null;
			if(type.equalsIgnoreCase("PRO")){
				key = generateKey("PRO Market Survey New " + cityName);
			} else {
				key = generateKey("KTC Market Survey New " + cityName);
			}
			
			String filePath = folderPath + key + ".xlsx";
			logger.info("Destination File Path = " + filePath);

			sisMarketSurveyReportNew.createReport(resp, request, filePath, townId,
					month, year, type);

			logger.info("Generated file key :: " + key);
			obj.put("key", key);
			obj.put("fileType", "xlsx");
			resp.setContentType("text/json");
			resp.getWriter().println(obj.toString());
			resp.getWriter().flush();
			resp.getWriter().close();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	@Override
	public void marketSurveyReportDashboard(HttpServletResponse resp,
			HttpServletRequest request, Integer townId, Integer month,
			Integer year, String type) {
		try {

			JSONObject obj = new JSONObject();
			String folderPath = getFolderPath();
			String cityName = reportsDao.getCityName(townId);
			String key = null;
			if(type.equalsIgnoreCase("PRO")){
				key = generateKey("PRO Market Survey Dashboard " + cityName);
			} else {
				key = generateKey("KTC Market Survey Dashboard " + cityName);
			}
			
			String filePath = folderPath + key + ".xlsx";
			logger.info("Destination File Path = " + filePath);

			sisMarketSurveyDashboard.createReport(resp, request, filePath, townId,
					month, year, type);

			logger.info("Generated file key :: " + key);
			obj.put("key", key);
			obj.put("fileType", "xlsx");
			resp.setContentType("text/json");
			resp.getWriter().println(obj.toString());
			resp.getWriter().flush();
			resp.getWriter().close();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	@Override
	public void createDsrExportData(HttpServletResponse resp,
			HttpServletRequest request, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer type, String startDate,
			String endDate, int reportType, int[] viewType) {

		dsrExportData.createReport(resp, request, zoneId, regionId, townId,
					areaId, type, startDate, endDate, reportType, viewType);

	}
	
	@Override
	public void dsrWiseProductivity(HttpServletResponse resp,
			HttpServletRequest request, String zoneId, String regionId,
			String townId, Integer areaId, Integer type, String startDate,
			String endDate) {
		try {

			JSONObject obj = new JSONObject();
			String folderPath = getFolderPath();
			String key = generateKey("Dsr Productivity Report");
			String filePath = folderPath + key + ".xlsx";
			logger.info("Destination File Path = " + filePath);

			dsrWiseProductivityReport.createReport(resp, request, filePath, zoneId,
					regionId, townId, areaId, type, startDate, endDate, "");

			logger.info("Generated file key :: " + key);
			obj.put("key", key);
			obj.put("fileType", "xlsx");
			resp.setContentType("text/json");
			resp.getWriter().println(obj.toString());
			resp.getWriter().flush();
			resp.getWriter().close();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	@Override
	public void tmProductivityReport(HttpServletResponse resp,
			HttpServletRequest request, Integer surveyorId, String startDate, String endDate) {
		try {

			JSONObject obj = new JSONObject();
			String folderPath = getFolderPath();
			String key = generateKey("TM Productivity Report");
			String filePath = folderPath + key + ".xlsx";
			logger.info("Destination File Path = " + filePath);

			tmProductivityReport.createReport(resp, request, filePath, surveyorId, startDate, endDate);

			logger.info("Generated file key :: " + key);
			obj.put("key", key);
			obj.put("fileType", "xlsx");
			resp.setContentType("text/json");
			resp.getWriter().println(obj.toString());
			resp.getWriter().flush();
			resp.getWriter().close();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	@Override
	public void dsrRouteTaggingReport(HttpServletResponse resp,
			HttpServletRequest request, String zoneId, String regionId,
			String townId, String areaId, Integer type, String startDate,
			String endDate) {
		try {

			JSONObject obj = new JSONObject();
			String folderPath = getFolderPath();
			String key = generateKey("DSR Route Tag");
			String filePath = folderPath + key + ".xlsx";
			logger.info("Destination File Path = " + filePath);

			dsrRouteTaggingReport.createReport(resp, request, filePath, zoneId,
					regionId, townId, areaId, type, startDate, endDate, "");

			logger.info("Generated file key :: " + key);
			obj.put("key", key);
			obj.put("fileType", "xlsx");
			resp.setContentType("text/json");
			resp.getWriter().println(obj.toString());
			resp.getWriter().flush();
			resp.getWriter().close();

		} catch (Exception e) {
			logger.error(e, e);
		}
	}
	
	@Override
	public void createDsrMTDSummaryReport(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			int zoneId, int regionId, int townId) {
		dsrMTDReport.createReport(resp, request, startDate, endDate, zoneId,
				regionId, townId);

	}
	
	@Override
	public void dsrPerformance(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, Integer typeId, String projectName) {
		dsrPerformanceReport.createReport(resp, request, startDate, endDate, zoneId,
				regionId, townId, typeId, projectName);

	}
	
	@Override
	public void exportDataReportV3(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			String zoneIds, String regionIds, String townIds, String measurmentTitle) {
		exportDataV3Report.createReport(resp, request, startDate, endDate, zoneIds,
				regionIds, townIds, measurmentTitle);

	}
	
	
	@Override
	public void createDsrSaleV4Data(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, float measurmentFactor,
			String status, String measurmentTitle, Integer reportType) {

		dsrSaleV4.createReport(resp, request, startDate, endDate, zoneId,
				regionId, townId, measurmentFactor, status, measurmentTitle, reportType);

	}
	
	@Override
	public void createTopSalesData(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			 String measurmentTitle, String type, String zoneId,String regionId,String townId ) {

       topSales.createReport(resp, request, startDate, endDate, measurmentTitle, type,   zoneId,  regionId,  townId);

	}
	
	
	@Override
	public void sisSupervisorReport(HttpServletResponse res,
			HttpServletRequest req, Integer townId, Integer month,
			Integer year, Integer regionId, Integer zoneId, Integer typeId) {
	
			sisSupervisorReport.createReports(res, req, townId,
					month, year, regionId, zoneId, typeId);
	}
	


}
