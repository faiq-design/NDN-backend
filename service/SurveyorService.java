package com.marksman.census.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.marksman.census.bo.Area;
import com.marksman.census.bo.Region;
import com.marksman.census.bo.Shop;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.bo.Zone;
import com.marksman.census.client.bo.BrandStock;
import com.marksman.census.client.bo.Family;
import com.marksman.census.client.bo.SummaryTag;
import com.marksman.census.client.bo.SyncBwu;
import com.marksman.census.client.bo.SyncData;
import com.marksman.census.client.bo.SyncFamily;
import com.marksman.census.client.bo.SyncIndustry;
import com.marksman.census.client.bo.SyncQuestion;
import com.marksman.census.client.bo.SyncShop;
import com.marksman.census.client.bo.WorkActivity;
import com.marksman.census.constants.AsmShopStatus;
import com.marksman.census.constants.AsmSummaryType;

public interface SurveyorService {

	public Map<String, Object> login(String imei, String mCode,
			HttpServletRequest request, HttpServletResponse response);

	public Map<String, Object> refreshData(String imei, Surveyor surveyor,
			String surveyorType, String version, HttpServletRequest request,
			HttpServletResponse response);

	public Map<String, Object> shopFascia(int shopId,
			HttpServletResponse response, String appType);

	public Map<String, Object> asmLogin(String imei, String mCode,
			HttpServletResponse response);

	public Map<String, Object> tmLogin(String imei, String mCode,
			HttpServletRequest request, HttpServletResponse response);

	public Map<String, Object> meLogin(String imei, String mCode,
			HttpServletRequest request, HttpServletResponse response);

	public Map<String, Object> getAsmShopSummary(int areaId,
			HttpServletResponse response);

	public Map<String, Object> getAsmDssShopSummary(int surveyorId,
			HttpServletResponse response);

	public Map<String, Object> getAsmShopStatusSummary(int surveyorId,
			AsmSummaryType asmSummaryType, HttpServletResponse response);

	public Map<String, Object> getAsmShopList(int surveyorId,
			AsmSummaryType asmSummaryType, AsmShopStatus asmShopStatus,
			HttpServletResponse response);

	public Map<String, Object> getAsmShopDetail(int shopId,
			int merchandiserShopId, HttpServletResponse response);

	public Map<String, Object> getAsmShopData(int shopId,
			int merchandiserShopId, HttpServletResponse response);

	public Map<String, Object> getSurveyorErrorList(int surveyorId,
			HttpServletResponse response);

	public List<com.marksman.census.bo.Region> getRegions(
			HttpServletResponse response);

	public List<Area> getAreas(int regionId, HttpServletResponse response);

	public List<Shop> getShops(int areaId, HttpServletResponse response);

	public Map<String, Object> getDesAndDsrsByActivityId(Integer activityId);

	public Object getActivity(Integer tmId);

	public Object getdeDetails(Integer deId);

	public List<Shop> getShopsForME(int areaId, int dsrId,
			HttpServletResponse response, Double latitude, Double longitude,
			int surveyorId);

	public List<Surveyor> getDeListByRegionId(int regionId);

	public List<Zone> getZones();

	public List<Region> getRegionsByZoneId(int zoneId);

	public List<Surveyor> getDeListByTmId(Integer tmId);

	public ArrayList<SummaryTag> getSummaryForTM(int surveyorId, String type,
			String surveyorType);

	public String getServerTime();

	public List<Map<String, Object>> getAttendanceSummary(String startDate,
			String endDate, Integer zoneId, Integer regionId);

	public List<Map<String, Object>> getSaleSummary(String startDate,
			String endDate, Integer deId, Integer regionId);

	public SyncShop getSyncShopData(Integer shopId);

	public ArrayList<SyncQuestion> getShopQuestionData(Integer shopId,
			Integer msId);

	public ArrayList<SyncFamily> getShopFamilyData(Integer shopId, Integer msId);

	public ArrayList<SyncIndustry> getShopIndustryData(Integer shopId,
			Integer msId);

	public ArrayList<SummaryTag> getDETimeSummary(Integer surveyorId,
			String type);

	public ArrayList<SyncBwu> getShopBwuData(Integer shopId, Integer msId);

	public List<Region> getSisRegions(Integer userId, Integer zoneId,
			String type, float version);

	public List<Area> getSisAreas(Integer townId);

	public ArrayList<SyncFamily> getOffTakeLastVisit(Integer shopId);

	public List<Shop> getShops(int areaId, Integer townId, Integer regionId,
			String appType, HttpServletResponse response, Integer activityId,
			String type, String userType);

	public List<BrandStock> getPreviousPurchaseStock(Integer shopId);

	public void updateForceLoginStatus(String mCode);

	public int insertSisActivity(int surveyorId, Integer townId,
			Integer shopsTarget, String title, String visitType,
			Integer regionId) throws Exception;

	public List<WorkActivity> getSisActivities(Integer townId, int surveyorId,
			String appType, String visitType, Integer regionId, float version);

	public void sisJoinActivityRequest(Integer activityId, Integer surveyorId,
			String isOwner, String requestStatus) throws Exception;

	public List<WorkActivity> getSisActivityRequestList(Integer surveyorId);

	public void updateRequestStatus(int requestId, String requestStatus)
			throws Exception;

	public SyncData getSisShopData(HttpServletRequest request,
			HttpServletResponse respons, Integer shopId);

	public void endSisActivity(Integer activityId) throws Exception;

	public List<Surveyor> getSisSupervisors(Integer zoneId, Integer regionId);

	public List<Family> getWSTKSSaleDetails(Integer shopId, String shopType);

	public ArrayList<SummaryTag> getSummaryForSIS(Integer activity_id,
			String type, float version);

	public void updateUserToken(int surveyorId, String userToken);

	public List<BrandStock> getSisPreviousPurchaseStock(Integer regionId);

	public List<Map<String, Object>> getDsrAttendanceForDe(String visitDate,
			Integer deId);

	public ArrayList<SummaryTag> getSummaryForDsr(int id, String type);

	public List<Shop> getDsrNewPendingShops(int deId, int dsrId,
			HttpServletResponse response) throws Exception;

	public void updateDsrNewShopStatus(int shopId, String shopStatus,
			HttpServletResponse response) throws Exception;


	
}
