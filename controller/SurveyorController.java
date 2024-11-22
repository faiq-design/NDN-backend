package com.marksman.census.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.marksman.census.bo.Area;
import com.marksman.census.bo.Region;
import com.marksman.census.bo.Shop;
import com.marksman.census.bo.SisShop;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.bo.Zone;
import com.marksman.census.client.bo.Attendance;
import com.marksman.census.client.bo.Family;
import com.marksman.census.client.bo.WorkActivity;
import com.marksman.census.client.bo.BrandStock;
import com.marksman.census.client.bo.SummaryTag;
import com.marksman.census.client.bo.SyncData;
import com.marksman.census.client.bo.SyncFamily;
import com.marksman.census.constants.AsmShopStatus;
import com.marksman.census.constants.AsmSummaryType;

public interface SurveyorController {

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Map<String, Object> login(String imei, String mCode,
			String password, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/data/refresh", method = RequestMethod.POST)
	public Map<String, Object> refreshData(String imei, Integer surveyorId,
			String surveyorType, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/shop-image", method = RequestMethod.POST)
	public Map<String, Object> shopFascia(int shopId, String mCode,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/asm/login", method = RequestMethod.POST)
	public Map<String, Object> asmLogin(String imei, String mCode,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/tm/login", method = RequestMethod.POST)
	public Map<String, Object> tmLogin(String imei, String mCode,
			String password, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/me/login", method = RequestMethod.POST)
	public Map<String, Object> meLogin(String imei, String mCode,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/asm/summary", method = RequestMethod.POST)
	public Map<String, Object> getAsmShopSummary(int areaId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/asm/dss-summary", method = RequestMethod.POST)
	public Map<String, Object> getAsmDssShopSummary(int surveyorId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/asm/status-summary", method = RequestMethod.POST)
	public Map<String, Object> getAsmShopStatusSummary(int surveyorId,
			AsmSummaryType asmSummaryType, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/asm/shop-list"/* , method = RequestMethod.POST */)
	public Object getAsmShopList(int surveyorId, AsmSummaryType asmSummaryType,
			AsmShopStatus asmShopStatus, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/asm/error-list", method = RequestMethod.POST)
	public Map<String, Object> getSurveyorErrorList(int surveyorId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/asm/shop-detail", method = RequestMethod.POST)
	public Map<String, Object> getAsmShopDetail(int shopId,
			int merchandiserShopId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/asm/shop-data", method = RequestMethod.POST)
	public Map<String, Object> getAsmShopData(int shopId,
			int merchandiserShopId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/client/regions", method = RequestMethod.GET)
	public List<Region> getRegions(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/client/areas", method = RequestMethod.POST)
	public List<Area> getAreas(int regionId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/client/shops", method = RequestMethod.POST)
	public List<Shop> getShops(int areaId, Integer townId, Integer activityId,
			String type, Integer regionId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "client/getDesAndDsrsByActivityId", method = RequestMethod.POST)
	public Map<String, Object> getDesAndDsrsByActivityId(Integer activityId);

	@RequestMapping(value = "client/getActivityByTmId")
	public Object getActivity(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "client/me/getdeDetails", method = RequestMethod.POST)
	public Object getDeDetails(HttpServletRequest request,
			HttpServletResponse response, Integer deId);

	@RequestMapping(value = "/client/me/shops", method = RequestMethod.POST)
	public List<Shop> getShopsForME(int areaId, int dsrId,
			HttpServletRequest request, HttpServletResponse response,
			Double latitude, Double longitude);

	@RequestMapping(value = "/client/me/getDeList", method = RequestMethod.POST)
	public List<Surveyor> getDeListByRegionId(Integer regionId,
			HttpServletRequest request, HttpServletResponse response,
			Integer tmId);

	@RequestMapping(value = "/client/me/getZoneList")
	public List<Zone> getZones(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/client/me/getRegionList", method = RequestMethod.POST)
	public List<Region> getRegionsByZoneId(HttpServletRequest request,
			HttpServletResponse response, Integer zoneId);

	@RequestMapping(value = "/client/tm/getUsersForAttendanceApprovals", method = RequestMethod.POST)
	public List<Attendance> getUsersForAttendanceApprovals(
			HttpServletRequest request, HttpServletResponse response,
			String userType, String selectedDate, Integer zoneId,
			Integer regionId, Integer userId);

	/*
	 * @RequestMapping(value = "/client/de/updateAttendanceForApproval" , method
	 * = RequestMethod.POST) public Map<String, Object>
	 * UpdateAttendanceForApprovals(HttpServletRequest request,
	 * HttpServletResponse respons , ArrayList<Attendance> approvedAttendance ,
	 * String selectedDate, Integer surveyorId );
	 */

	@RequestMapping(value = "/client/tm/getSummaryForTM", method = RequestMethod.POST)
	public ArrayList<SummaryTag> getSummaryForTM(HttpServletRequest request,
			HttpServletResponse respons, Integer surveyorId, String type);

	@RequestMapping(value = "/client/get-server-time", method = RequestMethod.GET)
	public void getServerTime(HttpServletRequest request,
			HttpServletResponse respons);

	@RequestMapping(value = "/client/de/attendance-summary", method = RequestMethod.POST)
	public List<Map<String, Object>> getAttendanceSummary(
			HttpServletRequest request, HttpServletResponse respons,
			String startDate, String endDate, Integer zoneId, Integer regionId);

	@RequestMapping(value = "/client/sale-summary", method = RequestMethod.POST)
	public List<Map<String, Object>> getSaleSummary(HttpServletRequest request,
			HttpServletResponse respons, String startDate, String endDate,
			Integer deId, Integer regionId);

	@RequestMapping(value = "/client/shop-data", method = RequestMethod.GET)
	public SyncData getShopData(HttpServletRequest request,
			HttpServletResponse respons, Integer shopId);

	@RequestMapping(value = "/client/de/time-summary", method = RequestMethod.GET)
	public ArrayList<SummaryTag> getDETimeSummary(HttpServletRequest request,
			HttpServletResponse respons, Integer surveyorId, String type);

	@RequestMapping(value = "/client/de/update-password", method = RequestMethod.POST)
	public Map<String, Object> updatePassword(HttpServletRequest request,
			HttpServletResponse respons, String mCode, String oldPassword,
			String newPassword);

	@RequestMapping(value = "/sis/login", method = RequestMethod.POST)
	public Map<String, Object> sisLogin(String imei, String mCode,
			String password, String token, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/client/sis/regions", method = RequestMethod.POST)
	public List<Region> getSisRegions(HttpServletRequest request,
			HttpServletResponse response, Integer userId, Integer zoneId,
			String type);

	@RequestMapping(value = "/client/sis/areas", method = RequestMethod.GET)
	public List<Area> getSisAreas(HttpServletRequest request,
			HttpServletResponse response, Integer townId);

	@RequestMapping(value = "/client/sis/offTakeLastVisit", method = RequestMethod.GET)
	public List<SyncFamily> getOffTakeLastVisit(Integer shopId,
			HttpServletRequest request, HttpServletResponse response);

	// this service will not be in use after sis build version 2.1
	@RequestMapping(value = "/client/sis/previousPurchaseStock", method = RequestMethod.GET)
	public List<BrandStock> getPreviousPurchaseStock(Integer shopId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/client/sis/start-activity", method = RequestMethod.POST)
	public void startSisActivity(HttpServletRequest request,
			HttpServletResponse response, Integer townId, Integer shopsTarget,
			String title, String visitType, Integer regionId);

	@RequestMapping(value = "/client/sis/activities", method = RequestMethod.GET)
	public List<WorkActivity> getSisActivities(HttpServletRequest request,
			HttpServletResponse response, Integer townId, String visitType,
			Integer regionId);

	@RequestMapping(value = "/client/sis/join-activity", method = RequestMethod.POST)
	public void sisJoinActivityRequest(HttpServletRequest request,
			HttpServletResponse response, Integer activityId);

	@RequestMapping(value = "/client/sis/request-list", method = RequestMethod.GET)
	public List<WorkActivity> getSisActivityRequestList(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/client/sis/update-request-status", method = RequestMethod.POST)
	public void updateRequestStatus(HttpServletRequest request,
			HttpServletResponse response, Integer requestId,
			String requestStatus);

	@RequestMapping(value = "/client/sis/shop-data", method = RequestMethod.GET)
	public SyncData getSisShopData(HttpServletRequest request,
			HttpServletResponse respons, Integer shopId);

	@RequestMapping(value = "/client/sis/end-activity", method = RequestMethod.POST)
	public void endSisActivity(HttpServletRequest request,
			HttpServletResponse response, Integer activityId);

	@RequestMapping(value = "/client/sis/supervisors", method = RequestMethod.GET)
	public List<Surveyor> getSisSupervisors(HttpServletRequest request,
			HttpServletResponse response, Integer zoneId, Integer regionId);

	@RequestMapping(value = "/client/sales-achievement", method = RequestMethod.GET)
	public List<Family> getSalesTargets(HttpServletRequest request,
			HttpServletResponse response, Integer shopId, String shopType);

	@RequestMapping(value = "/client/sis/getSummary", method = RequestMethod.GET)
	public ArrayList<SummaryTag> getSummaryForSIS(HttpServletRequest request,
			HttpServletResponse respons, Integer activity_id, String type);

	@RequestMapping(value = "/client/refreshtoken", method = RequestMethod.POST)
	public void updateToken(HttpServletRequest request,
			HttpServletResponse respons, String token);

	@RequestMapping(value = "/client/sis/shops", method = RequestMethod.POST)
	public SisShop getSisShops(int areaId, Integer townId, Integer activityId,
			String type, Integer regionId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/client/get-dsr-checkins", method = RequestMethod.POST)
	public List<Map<String, Object>> getDsrAttendanceForDe(
			HttpServletRequest request, HttpServletResponse respons,
			String visitDate, Integer deId);

	@RequestMapping(value = "/client/get-summary-for-dsr", method = RequestMethod.POST)
	public ArrayList<SummaryTag> getSummaryForDsr(HttpServletRequest request,
			HttpServletResponse respons, Integer id, String type);

	@RequestMapping(value = "/client/dsr-pending-new-shops", method = RequestMethod.GET)
	public List<Shop> getDsrNewPendingShops(int deId, int dsrId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/client/update-new-shop-validation-status", method = RequestMethod.POST)
	public void updateDsrAddedShopStatus(int shopId, String shopStatus,
			HttpServletRequest request, HttpServletResponse response);
}
