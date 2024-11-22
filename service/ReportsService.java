package com.marksman.census.service;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.marksman.census.bo.Items;
import com.marksman.census.constants.SurveyorType;

public interface ReportsService {

	public void createExportData(HttpServletResponse resp,
			HttpServletRequest request, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer type, String startDate,
			String endDate, int reportType, int[] viewType, Integer channel);

	public void createBrandAvailability(HttpServletResponse resp,
			Integer zoneId, Integer regionId, Integer townId, Integer areaId,
			Integer type, String startDate, String endDate, int reportType,
			int[] viewType);

	public void createSourcesOfPurchase(HttpServletResponse resp,
			Integer zoneId, Integer regionId, Integer townId, Integer areaId,
			Integer type, String startDate, String endDate, int reportType,
			int[] viewType);

	public void createBillImages(HttpServletRequest request,
			HttpServletResponse resp, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer type, String startDate,
			String endDate, int reportType, int[] viewType);

	public void createTimeSpent(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, Integer type,
			String startDate, String endDate, int reportType, int[] viewType,
			SurveyorType surveyorType);

	public void createRedFlag(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, int reportType);

	public void createDssSummary(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, int reportType);

	public void createRegionalSummary(HttpServletResponse resp);

	public void createQcEfficiency(HttpServletResponse resp);

	public void createAsmSummary(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, int reportType);

	public ModelAndView setAttributes(ModelAndView modelAndView);

	public ArrayList<Items> getTypeList(int type);

	public void asmAssignedShops(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, int reportType);

	public void evaluatedShops(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, String startDate,
			String endDate, int reportType);

	public void outOfScopeShops(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, int reportType);

	public void activityLog(HttpServletResponse resp, Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, int reportType,
			String startDate, String endDate);

	public void createDsrSaleData(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, float measurmentFactor,
			String status, String measurmentTitle, Integer reportType);

	public void createDsrSaleTarget(HttpServletResponse resp,
			HttpServletRequest request, int month, int year, Integer zoneId,
			Integer regionId, Integer townId, Integer type);

	public void createDeWWWRSummaryReport(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId);

	public void dailyEvaluationReport(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, int actionType);

	public void createCsvReport(HttpServletRequest request,
			HttpServletResponse response, String startDate, String endDate,
			Map<String, Object> reportData, String zoneId, String regionId,
			String cityId, String month, String year);

	public void createSaleAchievementReport(HttpServletResponse resp,
			HttpServletRequest request, int year, int month, int zoneId,
			int regionId, int townId, int typeId);

	public void attendanceReport(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, int actionType);

	public void saleAchievementSummary(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			Integer zoneId, Integer regionId, Integer townId,
			Integer unitValue, String unitTitle);

	public void factoryStockReport(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId);

	public void tmWWWRSummary(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			Integer zoneId, Integer regionId, Integer townId);

	public void dsrSaleDataXlsm(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			int zoneId, int regionId, int townId, float measurmentFactor,
			String status, String measurmentTitle);

	public void stockLedgerReport(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, Integer reportType, String projectName);

	public void attendanceReportNew(HttpServletResponse response,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, int actionType);

	void createSisExportData(HttpServletResponse resp,
			HttpServletRequest request, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer type, String startDate,
			String endDate, int reportType, int[] viewType, String exportType);

	public void deWholesaleReport(HttpServletResponse response,
			HttpServletRequest request, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer typeId, String startDate,
			String endDate);

	public void deTksReport(HttpServletResponse response,
			HttpServletRequest request, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer typeId, String startDate,
			String endDate);

	void createWSTKSSaleTarget(HttpServletResponse resp,
			HttpServletRequest request, int month, int year, Integer zoneId,
			Integer regionId, Integer townId, String shopType);

	void createDSRSalesLog(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate);

	public void createRAShopSample(HttpServletResponse resp,
			HttpServletRequest request);

	public void marketSurveyReport(HttpServletResponse response,
			HttpServletRequest request, Integer townId, Integer month,
			Integer year, String type, Integer typeId);

	public void masterDataReport(HttpServletResponse response,
			HttpServletRequest request, Integer townId, Integer month,
			Integer year, Integer regionId, Integer zoneId);

	public void wsComaprisonReport(HttpServletResponse response,
			HttpServletRequest request, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer typeId, String startDate,
			String endDate);

	public void marketSurveyReportNew(HttpServletResponse response,
			HttpServletRequest request, Integer townId, Integer month,
			Integer year, String type);

	public void marketSurveyReportDashboard(HttpServletResponse response,
			HttpServletRequest request, Integer townId, Integer month,
			Integer year, String type);

	void createDsrExportData(HttpServletResponse resp,
			HttpServletRequest request, Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, Integer type, String startDate,
			String endDate, int reportType, int[] viewType);

	public void dsrWiseProductivity(HttpServletResponse response,
			HttpServletRequest request, String zoneId, String regionId,
			String townId, Integer areaId, Integer typeId, String startDate,
			String endDate);

	public void tmProductivityReport(HttpServletResponse response,
			HttpServletRequest request, Integer surveyorId, String startDate,
			String endDate);

	public void dsrRouteTaggingReport(HttpServletResponse response,
			HttpServletRequest request, String zoneId, String regionId,
			String townId, String areaId, Integer typeId, String startDate,
			String endDate);

	public void createDsrMTDSummaryReport(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			int zoneId, int regionId, int townId);

	public void dsrPerformance(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, Integer typeId, String projectName);
	
	public void exportDataReportV3(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			String zoneIds, String regionIds, String townIds, String measurmentTitle);
	
	public void createDsrSaleV4Data(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate,
			String zoneId, String regionId, String townId, float measurmentFactor,
			String status, String measurmentTitle, Integer reportType);
	
	public void createTopSalesData(HttpServletResponse resp,
			HttpServletRequest request, String startDate, String endDate
		, String measurmentTitle, String type, String zoneId,String regionId,String townId);
	
	public void sisSupervisorReport(HttpServletResponse response,
			HttpServletRequest request, Integer townId, Integer month,
			Integer year, Integer regionId, Integer zoneId, Integer typeId);
	
}
