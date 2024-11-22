package com.marksman.census.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.marksman.census.constants.SurveyorType;

public interface ReportsController {

	@RequestMapping(value = "/report/export-data", method = RequestMethod.POST)
	public void exportData(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, String startDate, String endDate,
			int actionType, int[] viewType, Integer channel,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/brand-availability", method = RequestMethod.POST)
	public void brandAvailability(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer typeId, String startDate,
			String endDate, int actionType, int[] viewType,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/sources-of-purchase", method = RequestMethod.POST)
	public void sourcesOfPurchase(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer typeId, String startDate,
			String endDate, int actionType, int[] viewType,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/bill-images", method = RequestMethod.POST)
	public void billImages(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, String startDate, String endDate,
			int actionType, int[] viewType, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/time-spent", method = RequestMethod.POST)
	public void timeSpent(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, String startDate, String endDate,
			int actionType, int[] viewType, SurveyorType surveyorType,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/brandAvailability")
	public ModelAndView brandAvailabilityGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/sourcesOfPurchase")
	public ModelAndView sourcesOfPurchaseGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/billImages")
	public ModelAndView billImagesGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/export")
	public ModelAndView exportDataGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/timeSpent")
	public ModelAndView timeSpentGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/export-data-ajax")
	public Map<String, Object> ajax(Integer zoneId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/export-data-town-ajax")
	public Map<String, Object> townAjax(Integer regionId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/redflag-data", method = RequestMethod.POST)
	public void redFlagData(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, int actionType,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/redflag-list-data", method = RequestMethod.POST)
	public Map<String, Object> redFlagDataList(Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, Integer typeId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/redflag")
	public ModelAndView redFlagDataGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/summary-data", method = RequestMethod.POST)
	public void exportDssSummaryData(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, int actionType,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/summary-list-data", method = RequestMethod.POST)
	public Map<String, Object> exportDssSummaryDataList(Integer zoneId,
			Integer regionId, Integer townId, Integer areaId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/asm-summary")
	public ModelAndView exportAsmSummaryGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/asm-summary-data", method = RequestMethod.POST)
	public void exportAsmSummaryData(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, int actionType,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/asm-summary-list-data", method = RequestMethod.POST)
	public Map<String, Object> exportAsmSummaryDataList(Integer zoneId,
			Integer regionId, Integer townId, Integer areaId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/dss-summary")
	public ModelAndView exportDssSummaryGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/regional-summary")
	public ModelAndView exportRegionalSummaryGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/regional-summary-data")
	public void exportRegionalSummaryData(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/qc-efficiency")
	public ModelAndView exportQcEfficiencyGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/qc-efficiency-data")
	public void exportQcEfficiencyData(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/asmAssigned")
	public ModelAndView asmAssignedShopsGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/asm-assign-shops", method = RequestMethod.POST)
	public void asmAssignedShops(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, int actionType,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/evaluatedShops")
	public ModelAndView evaluatedShopsGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/evaluated-shops", method = RequestMethod.POST)
	public void evaluatedShops(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, String startDate, String endDate,
			int actionType, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/outOfScope")
	public ModelAndView outOfScopeShopsGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/out-of-scope-shops", method = RequestMethod.POST)
	public void outOfScopeShops(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, int actionType,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/activityLog")
	public ModelAndView activityLogGet(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/activity-log", method = RequestMethod.POST)
	public void activityLog(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, int actionType, String startDate, String endDate,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/report/dsrSale", method = RequestMethod.POST)
	public void dsrSaleData(String startDate, String endDate, String zoneId, String regionId, String townId,
			float measurmentFactor, HttpServletRequest request, HttpServletResponse response,
			String status, String measurmentTitle, Integer userId, Integer reportType);

	@RequestMapping(value = "/report/dsrSaleTarget", method = RequestMethod.POST)
	public void dsrSaleTargetSample(Integer month, Integer year,
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, Integer townId, Integer type);

	@RequestMapping(value = "/report/de-WWWRSummary", method = RequestMethod.POST)

	public void deWWWRData(String startDate, String endDate, String zoneId, String regionId, String townId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "portal/report/daily-evaluation-report", method = RequestMethod.POST)
	public void dailyEvaluationReport(String startDate, String endDate,
			String zoneId, String regionId, String townId, int actionType,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "portal/report/attendance-report", method = RequestMethod.POST)
	public void attendanceReport(String startDate, String endDate, String zoneId,
			String regionId, String townId, int actionType,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "portal/report/dashboard-data", method = RequestMethod.POST)
	public void rawDataReport(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate,
			Integer typeId, String zoneId, String regionId, String cityId, Integer userId, String month, String year);

	@RequestMapping(value = "portal/report/download-csv-report", method = RequestMethod.POST)
	public void downloadCsvReport(HttpServletRequest request,
			HttpServletResponse response, String key, String fileType);

	@RequestMapping(value = "portal/report/download-report", method = RequestMethod.POST)
	public void downloadReport(HttpServletRequest request,
			HttpServletResponse response, String key, String fileType);

	@RequestMapping(value = "portal/report/sale-achievement", method = RequestMethod.POST)
	public void SalesAchievementReport(int year, int month, int zoneId,
			int regionId, int townId, int typeId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "report/achievementSummary", method = RequestMethod.POST)
	public void saleAchievementSummary(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate,
			Integer zoneId, Integer regionId, Integer townId, Integer userId,
			Integer unitValue, String unitTitle);

	@RequestMapping(value = "portal/report/factory-stock-report", method = RequestMethod.POST)
	public void factoryStockReport(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate,
			String zoneId, String regionId, String townId, Integer userId);

	@RequestMapping(value = "portal/report/tm-wwwr-summary", method = RequestMethod.POST)
	public void tmWWWRSummary(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate,
			Integer zoneId, Integer regionId, Integer townId, Integer userId);

	// this is for xlsm formate report
	@RequestMapping(value = "/report/dsrSaleXlsm", method = RequestMethod.POST)
	public void dsrSaleDataXlsm(String startDate, String endDate, int zoneId,
			int regionId, int townId, float measurmentFactor,
			HttpServletRequest request, HttpServletResponse response,
			String status, String measurmentTitle, Integer userId);

	@RequestMapping(value = "portal/report/stockLedgerReport", method = RequestMethod.POST)
	public void stockLedgerReport(String startDate, String endDate, String zoneId,
			String regionId, Integer reportType, String townId, String projectName,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "portal/report/attendanceReportNew", method = RequestMethod.POST)
	public void attendanceReportNew(String startDate, String endDate,
			String zoneId, String regionId, String townId, int actionType,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/report/sis-export-data", method = RequestMethod.POST)
	public void sisExportData(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, String startDate, String endDate,
			int actionType, int[] viewType, String exportType,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/report/de/wholesale-report", method = RequestMethod.POST)
	public void deWholesaleReport(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer typeId, String startDate,
			String endDate, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/report/de/tks-report", method = RequestMethod.POST)
	public void deTksReport(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, String startDate, String endDate,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/report/ws-tks-SaleTarget", method = RequestMethod.POST)
	public void wsTksSaleTargetSample(Integer month, Integer year,
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, Integer townId, String shopType);

	@RequestMapping(value = "/report/getDSRSalesLog", method = RequestMethod.POST)
	public void getDSRSalesLog(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate);

	@RequestMapping(value = "/report/RAShopSample", method = RequestMethod.POST)
	public void RAShopSample(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/report/sis/market-survey-report", method = RequestMethod.POST)
	public void marketSurveyReport(Integer townId, Integer month, Integer year,
			String type, Integer typeId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/report/sis/master-data-report", method = RequestMethod.POST)
	public void masterDataReport(Integer townId, Integer month, Integer year,
			Integer regionId, Integer zoneId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/report/ws-comparison-report", method = RequestMethod.POST)
	public void wsComaprisonReport(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer typeId, String startDate,
			String endDate, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/report/sis/market-survey-report-new", method = RequestMethod.POST)
	public void marketSurveyReportNew(Integer townId, Integer month,
			Integer year, String type, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/report/sis/market-survey-report-dashboard", method = RequestMethod.POST)
	public void marketSurveyReportDashboard(Integer townId, Integer month,
			Integer year, String type, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/report/dsr-export-data", method = RequestMethod.POST)
	public void dsrExportData(Integer zoneId, Integer regionId, Integer townId,
			Integer areaId, Integer typeId, String startDate, String endDate,
			int actionType, int[] viewType, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/report/dsr-wise-productivity", method = RequestMethod.POST)
	public void dsrWiseProductivity(String zoneId, String regionId, String townId, Integer areaId,
			Integer typeId, String startDate, String endDate, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/report/tm-productivity-report", method = RequestMethod.POST)
	public void tmProductivityReport(Integer surveyorId, String startDate,
			String endDate, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/report/dsr-route-tagging-report", method = RequestMethod.POST)
	public void dsrRouteTaggingReport(String zoneId, String regionId,
			String townId, String areaId, Integer typeId, String startDate,
			String endDate, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/report/dsr-mtd-report", method = RequestMethod.POST)
	public void dsrMTDReport(String startDate, String endDate, int zoneId,
			int regionId, int townId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "portal/report/dsr-performance-report", method = RequestMethod.POST)
	public void dsrPerformance(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate,
			String zoneId, String regionId, String townId, Integer userId,Integer typeId, String projectName);

	@RequestMapping(value = "/report/export-data-v3", method = RequestMethod.POST)
	public void exportDataReportV3(HttpServletRequest request,
			HttpServletResponse response, String zoneId, String regionId,
			String townId, String startDate, String endDate, String measurmentTitle);
	
	@RequestMapping(value = "/report/dsrSalev4", method = RequestMethod.POST)
	public void dsrSaleV4Data(String startDate, String endDate, String zoneId, String regionId, String townId,
			float measurmentFactor, HttpServletRequest request, HttpServletResponse response,
			String status, String measurmentTitle, Integer userId, Integer reportType);
			
   @RequestMapping(value = "/report/topsales", method = RequestMethod.POST)
    public void topSalesData(String startDate, String endDate,
					 HttpServletRequest request, HttpServletResponse response,
					String measurmentTitle, Integer userId, String type, String zoneId,String regionId,String townId);
   
   @RequestMapping(value = "report/sis/sis-supervisor-data-report", method = RequestMethod.POST)
	public void sisSupervisorReport(Integer townId, Integer month, Integer year,
			Integer regionId, Integer zoneId, Integer typeId, HttpServletRequest request,
			HttpServletResponse response);
}
