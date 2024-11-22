package com.marksman.census.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hdgf.streams.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.marksman.census.bo.Area;
import com.marksman.census.bo.Region;
import com.marksman.census.bo.Shop;
import com.marksman.census.bo.SisShop;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.bo.Zone;
import com.marksman.census.client.bo.Attendance;
import com.marksman.census.client.bo.BrandStock;
import com.marksman.census.client.bo.Family;
import com.marksman.census.client.bo.PposmData;
import com.marksman.census.client.bo.SummaryTag;
import com.marksman.census.client.bo.SyncBwu;
import com.marksman.census.client.bo.SyncData;
import com.marksman.census.client.bo.SyncFamily;
import com.marksman.census.client.bo.WorkActivity;
import com.marksman.census.constants.AsmShopStatus;
import com.marksman.census.constants.AsmSummaryType;
import com.marksman.census.constants.CommonConstants;
import com.marksman.census.constants.SurveyorType;
import com.marksman.census.constants.SysConstants;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.message.MessageType;
import com.marksman.census.service.ClientLoggingServiceImpl;
import com.marksman.census.service.PortalService;
import com.marksman.census.service.SisService;
import com.marksman.census.service.SurveyorService;
import com.marksman.census.service.ValidationService;
import com.marksman.census.util.CommonUtil;
import com.marksman.census.util.NotificationManager;
import com.marksman.census.util.StringUtils;

@Controller
public class SurveyorControllerImpl implements SurveyorController {

	@Autowired
	private SurveyorService surveyorService;

	@Autowired
	private ValidationService validationService;

	@Autowired
	private PortalService portalService;

	@Autowired
	private ClientLoggingServiceImpl clientLoggingService;

	@Autowired
	private SisService sisService;

	@Autowired
	private SurveyorDao surveyorDao;

	@Autowired
	private NotificationManager notificationManager;

	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public @ResponseBody
	Map<String, Object> login(String imei, String mCode, String password,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> jsonMap = null;
		logger.info("login request against imei : " + imei + ", m code : "
				+ mCode);
		clientLoggingService.insertActivityLog(request, response, "D.E Login");
		// String version = request.getHeader(SysConstants.VERSION);
		// if ("1.6".equalsIgnoreCase(StringUtils.extractVersion(version)))
		// {
		if (validationService.validateSurveyor(mCode, password,
				SurveyorType.DE, imei, response)) {
			surveyorService.updateForceLoginStatus(mCode);
			jsonMap = surveyorService.login(imei, mCode, request, response);
		}
		// }
		// else
		// {
		// if (validationService.validateSurveyor(mCode, SurveyorType.DE, imei,
		// response))
		// {
		// jsonMap = surveyorService.login(imei, mCode, request, response);
		// }
		// }
		return jsonMap;
	}

	@Override
	public @ResponseBody
	Map<String, Object> refreshData(String imei, Integer surveyorId,
			String surveyorType, HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> jsonMap = null;
		logger.info("Refersh Daata request against imei : " + imei
				+ ", surveyor Id : " + surveyorId);
		clientLoggingService.insertActivityLog(request, response,
				"Refresh_Data");
		// if (validationService.validateSurveyor(mCode,
		// SurveyorType.valueOf(surveyorType), imei, response)) {
		String version = request.getHeader(SysConstants.VERSION);
		Surveyor surveyor = surveyorDao.getSurveyorById(surveyorId);
		jsonMap = surveyorService.refreshData(imei, surveyor, surveyorType,version,
				request, response);
		// }
		return jsonMap;
	}

	@Override
	public @ResponseBody
	Map<String, Object> shopFascia(int shopId, String mCode,
			HttpServletRequest request, HttpServletResponse response) {

		String appType = request.getHeader(SysConstants.APP_TYPE);
		Map<String, Object> jsonMap = null;
		logger.info("shop fascia request against shop id : \t" + shopId
				+ ",\t m code : " + mCode + "\t  appType : " + appType);
		clientLoggingService
				.insertActivityLog(request, response, "Shop_Fascia");
		// if (validationService.validateSurveyor(mCode, SurveyorType.DSS, imei,
		// response)) {
		jsonMap = surveyorService.shopFascia(shopId, response, appType);
		// }
		return jsonMap;
	}

	@Override
	public @ResponseBody
	Map<String, Object> asmLogin(String imei, String mCode,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> jsonMap = null;
		logger.info("login request against imei : " + imei + ", m code : "
				+ mCode);
		clientLoggingService.insertActivityLog(request, response, "ASM_Login");
		if (validationService.validateSurveyor(mCode, SurveyorType.ASM, imei,
				response)) {
			jsonMap = surveyorService.asmLogin(imei, mCode, response);
		}
		return jsonMap;
	}

	@Override
	public @ResponseBody
	Map<String, Object> getAsmShopSummary(int areaId,
			HttpServletRequest request, HttpServletResponse response) {

		clientLoggingService.insertActivityLog(request, response,
				"ASM_Shop_Summary");
		logger.info("Getting ASM summary against area id  : " + areaId);
		return surveyorService.getAsmShopSummary(areaId, response);

	}

	@Override
	public @ResponseBody
	Map<String, Object> getAsmDssShopSummary(int surveyorId,
			HttpServletRequest request, HttpServletResponse response) {

		logger.info("Getting ASM DSS summary against surveyor id  : "
				+ surveyorId);
		clientLoggingService.insertActivityLog(request, response,
				"ASM_DSS_Shop_Summary");
		return surveyorService.getAsmDssShopSummary(surveyorId, response);

	}

	@Override
	public @ResponseBody
	Map<String, Object> getAsmShopStatusSummary(int surveyorId,
			AsmSummaryType asmSummaryType, HttpServletRequest request,
			HttpServletResponse response) {

		logger.info("Getting ASM shop status summary against surveyor id  : "
				+ surveyorId + ", summary type : " + asmSummaryType);
		clientLoggingService.insertActivityLog(request, response,
				"ASM_shop_status_summary");
		return surveyorService.getAsmShopStatusSummary(surveyorId,
				asmSummaryType, response);

	}

	@Override
	public @ResponseBody
	Object getAsmShopList(int surveyorId, AsmSummaryType asmSummaryType,
			AsmShopStatus asmShopStatus, HttpServletRequest request,
			HttpServletResponse response) {

		logger.info("Getting shop list against surveyor id  : " + surveyorId
				+ ",summary type : " + asmSummaryType.toString()
				+ ",shop status : " + asmShopStatus.toString());
		return new Gson().toJson(surveyorService.getAsmShopList(surveyorId,
				asmSummaryType, asmShopStatus, response));

	}

	@Override
	public @ResponseBody
	Map<String, Object> getSurveyorErrorList(int surveyorId,
			HttpServletRequest request, HttpServletResponse response) {

		logger.info("Getting shop error list against surveyor Id  : "
				+ surveyorId);
		return surveyorService.getSurveyorErrorList(surveyorId, response);

	}

	@Override
	public @ResponseBody
	Map<String, Object> getAsmShopDetail(int shopId, int merchandiserShopId,
			HttpServletRequest request, HttpServletResponse response) {

		logger.info("Getting shop detail against shop Id  : " + shopId + shopId
				+ ",merchandiserShopId : " + merchandiserShopId);
		return surveyorService.getAsmShopDetail(shopId, merchandiserShopId,
				response);

	}

	@Override
	public @ResponseBody
	Map<String, Object> getAsmShopData(int shopId, int merchandiserShopId,
			HttpServletRequest request, HttpServletResponse response) {

		logger.info("Getting shop data against shop Id  : " + shopId
				+ ",merchandiserShopId : " + merchandiserShopId);
		return surveyorService.getAsmShopData(shopId, merchandiserShopId,
				response);

	}

	// By Hamed
	@Override
	public @ResponseBody
	List<Region> getRegions(HttpServletRequest request,
			HttpServletResponse response) {

		logger.info("Getting regions data from cache " + -1);
		return surveyorService.getRegions(response);
	}

	// By hamed
	@Override
	public @ResponseBody
	List<Area> getAreas(int regionId, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("Getting List of Areas BO data from cache " + -1);
		return surveyorService.getAreas(regionId, response);
	}

	// By Hamed
	/*
	 * @Override public @ResponseBody List<Shop> getShops(int areaId,
	 * HttpServletRequest request, HttpServletResponse response) {
	 * logger.info("Getting List of Shops BO data from Service Layer " + -1);
	 * return surveyorService.getShops(areaId, response); }
	 */

	@Override
	public @ResponseBody
	List<Shop> getShops(int areaId, Integer townId, Integer activityId,
			String type, Integer regionId, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("Getting List of Shops BO data from Service Layer");
		String appType = request.getHeader(SysConstants.APP_TYPE);
		String userType = request.getHeader("userType");
		List<Shop> shopList = null;
		if ("sis".equalsIgnoreCase(appType)
				|| "sis_qa".equalsIgnoreCase(appType)) {
			shopList = surveyorService.getShops(areaId, townId, regionId,
					appType, response, activityId, type,userType);
		} else {
			shopList = surveyorService.getShops(areaId, response);
		}
		return shopList;
	}

	@Override
	public @ResponseBody
	Map<String, Object> tmLogin(String imei, String mCode, String password,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> jsonMap = null;
		logger.info(" T.M login request against imei : " + imei + ", m code : "
				+ mCode);
		clientLoggingService.insertActivityLog(request, response, "T.M login");
		String version = request.getHeader(SysConstants.VERSION);
		// if ("1.4".equalsIgnoreCase(StringUtils.extractVersion(version)))
		// {
		//
		// if (validationService
		// .validateSurveyor(mCode, password, SurveyorType.TM, imei, response))
		// {
		// jsonMap = surveyorService.tmLogin(imei, mCode, request, response);
		// }
		//
		// }
		// else
		// {
		// if (validationService.validateSurveyor(mCode, SurveyorType.TM, imei,
		// response))
		// {
		// jsonMap = surveyorService.tmLogin(imei, mCode, request, response);
		// }
		// }

		if (validationService.validateSurveyor(mCode, password,
				SurveyorType.TM, imei, response)) {
			jsonMap = surveyorService.tmLogin(imei, mCode, request, response);
			
		}
		logger.info("json:="+jsonMap);
		return jsonMap;
		
	}

	@Override
	public @ResponseBody
	Map<String, Object> meLogin(String imei, String mCode,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> jsonMap = null;
		logger.info("M.E login request against imei : " + imei + ", m code : "
				+ mCode);
		clientLoggingService.insertActivityLog(request, response, "M.E login");
		if (validationService.validateSurveyor(mCode, SurveyorType.ME, imei,
				response)) {
			jsonMap = surveyorService.meLogin(imei, mCode, request, response);
		}
		return jsonMap;
	}

	@Override
	@ResponseBody
	public Map<String, Object> getDesAndDsrsByActivityId(Integer activityId) {

		return surveyorService.getDesAndDsrsByActivityId(activityId);
	}

	@Override
	@ResponseBody
	public Object getActivity(HttpServletRequest request,
			HttpServletResponse response) {
		int tmId = Integer.parseInt(request.getHeader("surveyorId"));
		clientLoggingService
				.insertActivityLog(request, response, "TM_Activity");
		return surveyorService.getActivity(tmId);
	}

	@Override
	@ResponseBody
	public Object getDeDetails(HttpServletRequest request,
			HttpServletResponse response, Integer deId) {
		return surveyorService.getdeDetails(deId);
	}

	@Override
	@ResponseBody
	public List<Shop> getShopsForME(int areaId, int dsrId,
			HttpServletRequest request, HttpServletResponse response,
			Double latitude, Double longitude) {

		int tmId = request.getIntHeader("surveyorid");
		response.setHeader("radius", Integer.toString(SysConstants.MAP_RADIUS));
		List<Shop> shopList = new ArrayList<Shop>();
		shopList = surveyorService.getShopsForME(areaId, dsrId, response,
				latitude, longitude, tmId);

		return shopList;
	}

	@Override
	@ResponseBody
	public List<Surveyor> getDeListByRegionId(Integer regionId,
			HttpServletRequest request, HttpServletResponse response,
			Integer tmId) {
		List<Surveyor> deList = null;
		if (regionId > 0) {
			deList = surveyorService.getDeListByRegionId(regionId);
		} else if (tmId > 0) {
			deList = surveyorService.getDeListByTmId(tmId);
		}
		return deList;
	}

	@Override
	@ResponseBody
	public List<Zone> getZones(HttpServletRequest request,
			HttpServletResponse response) {

		return surveyorService.getZones();

	}

	@Override
	@ResponseBody
	public List<Region> getRegionsByZoneId(HttpServletRequest request,
			HttpServletResponse response, Integer zoneId) {
		return surveyorService.getRegionsByZoneId(zoneId);
	}

	@Override
	@ResponseBody
	public List<Attendance> getUsersForAttendanceApprovals(
			HttpServletRequest request, HttpServletResponse response,
			String userType, String selectedDate, Integer zoneId,
			Integer regionId, Integer userId) {
		clientLoggingService.insertActivityLog(request, response,
				"Attendance_Approvals");
		return portalService.getUsersForAttendanceApprovals(userType,
				selectedDate, zoneId, regionId);
	}

	/*
	 * @Override public Map<String, Object>
	 * UpdateAttendanceForApprovals(HttpServletRequest request,
	 * HttpServletResponse respons, ArrayList<Attendance> approvedAttendance,
	 * String selectedDate, Integer surveyorId) { Map<String, Object> map = new
	 * HashMap<String, Object>(); try{
	 * portalService.UpdateAttendanceForApprovals( approvedAttendance,
	 * selectedDate,surveyorId); map.put("status", true); } catch(Exception e) {
	 * logger.debug(
	 * "Exception occured while updating attendance approval status" + e);
	 * map.put("status", false); } return map; }
	 */

	@Override
	@ResponseBody
	public ArrayList<SummaryTag> getSummaryForTM(HttpServletRequest request,
			HttpServletResponse respons, Integer surveyorId, String type) {
		String surveyorType = request.getHeader(SysConstants.SURVEYOR_TYPE);
		surveyorType = StringUtils.isNullOrEmptyString(surveyorType) ? "DE"
				: surveyorType;
		clientLoggingService.insertActivityLog(request, respons, "Summary_For_"
				+ surveyorType);
		return surveyorService.getSummaryForTM(surveyorId, type, surveyorType);

	}

	@Override
	@ResponseBody
	public void getServerTime(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			JsonObject responseJson = new JsonObject();
			String serverDateTime = surveyorService.getServerTime();
			responseJson.addProperty("serverDateTime", serverDateTime);
			logger.info("server date time: " + responseJson.toString());
			PrintWriter out = response.getWriter();
			out.println(responseJson.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getAttendanceSummary(
			HttpServletRequest request, HttpServletResponse respons,
			String startDate, String endDate, Integer zoneId, Integer regionId) {
		clientLoggingService.insertActivityLog(request, respons,
				"Attendance_Summary");
		return surveyorService.getAttendanceSummary(startDate, endDate, zoneId,
				regionId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getSaleSummary(HttpServletRequest request,
			HttpServletResponse respons, String startDate, String endDate,
			Integer deId, Integer regionId) {
		clientLoggingService
				.insertActivityLog(request, respons, "Sale_Summary");
		return surveyorService.getSaleSummary(startDate, endDate, deId,
				regionId);
	}

	@Override
	@ResponseBody
	public SyncData getShopData(HttpServletRequest request,
			HttpServletResponse respons, Integer shopId) {
		clientLoggingService.insertActivityLog(request, respons, "Shop_Data");

		SyncData syncData = new SyncData();
		syncData.setSyncShop(surveyorService.getSyncShopData(shopId));
		syncData.setQuestionData(surveyorService.getShopQuestionData(shopId,
				syncData.getSyncShop().getMerchandiserShopId()));
		syncData.setFamilyData(surveyorService.getShopFamilyData(shopId,
				syncData.getSyncShop().getMerchandiserShopId()));
		syncData.setIndustryData(surveyorService.getShopIndustryData(shopId,
				syncData.getSyncShop().getMerchandiserShopId()));

		if ("Y".equalsIgnoreCase(syncData.getSyncShop().getIsPposm())) {
			ArrayList<PposmData> pposmDataList = new ArrayList<PposmData>();
			PposmData pposmData = new PposmData();
			ArrayList<SyncBwu> syncBwu = new ArrayList<SyncBwu>();
			syncBwu = surveyorService.getShopBwuData(shopId, syncData
					.getSyncShop().getMerchandiserShopId());
			boolean check = false;
			for (SyncBwu bwu : syncBwu) {

				if (bwu.getImages().isEmpty() || bwu == null) {
					check = true;
				}
			}

			if (!syncBwu.isEmpty()) {
				if (check) {
					syncData.setPposmData(null);
					syncData.getSyncShop().setIsPposm("N");
				} else {
					pposmData.setSyncBwus(syncBwu);
					pposmData.setIndustryId(syncBwu.get(0).getIndustryId());
					pposmDataList.add(pposmData);
					syncData.setPposmData(pposmDataList);
				}
			}

		}

		if ("Y".equalsIgnoreCase(syncData.getSyncShop().getIsIncomplete())) {
			syncData.getSyncShop().setIsPposm("");
		}
		return syncData;
	}

	@Override
	public ArrayList<SummaryTag> getDETimeSummary(HttpServletRequest request,
			HttpServletResponse respons, Integer surveyorId, String type) {
		clientLoggingService.insertActivityLog(request, respons,
				"Summary_For_TM");
		return surveyorService.getDETimeSummary(surveyorId, type);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updatePassword(HttpServletRequest request,
			HttpServletResponse respons, String mCode, String oldPassword,
			String newPassword) {
		clientLoggingService.insertActivityLog(request, respons,
				"Update_Password");
		return validationService.updatePassword(request, respons, mCode,
				oldPassword, newPassword);
	}

	@Override
	@ResponseBody
	public Map<String, Object> sisLogin(String imei, String mCode,
			String password, String token, HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> jsonMap = null;
		String appType = request.getHeader("appType");
		logger.info("SIS login request against imei : " + imei + ", m code : "
				+ mCode + ", token : " + token + ", App Type :: " + appType);
		clientLoggingService.insertActivityLog(request, response, appType
				+ "_LOGIN");
		if (validationService.validateSurveyor(mCode, password, appType
				.equalsIgnoreCase("sis") ? SurveyorType.SIS
				: SurveyorType.SIS_QA , imei, response)) {
			surveyorDao.updatUserToken(mCode, token);
			jsonMap = sisService.login(imei, mCode, request, response);
		}
		return jsonMap;
	}

	@Override
	@ResponseBody
	public List<Region> getSisRegions(HttpServletRequest request,
			HttpServletResponse response, Integer userId, Integer zoneId,
			String type) {
		float version = Float.parseFloat(request.getHeader("version")
				.split("_")[0]);
		return surveyorService.getSisRegions(userId, zoneId, type, version);
	}

	@Override
	@ResponseBody
	public List<Area> getSisAreas(HttpServletRequest request,
			HttpServletResponse response, Integer townId) {
		return surveyorService.getSisAreas(townId);
	}

	@Override
	@ResponseBody
	public List<SyncFamily> getOffTakeLastVisit(Integer shopId,
			HttpServletRequest request, HttpServletResponse response) {

		return surveyorService.getOffTakeLastVisit(shopId);
	}

	@Override
	@ResponseBody
	public List<BrandStock> getPreviousPurchaseStock(Integer shopId,
			HttpServletRequest request, HttpServletResponse response) {
		return surveyorService.getPreviousPurchaseStock(shopId);
	}

	@Override
	@ResponseBody
	public void startSisActivity(HttpServletRequest request,
			HttpServletResponse response, Integer townId, Integer shopsTarget,
			String title, String visitType, Integer regionId) {
		try {
			int surveyorId = Integer.parseInt(request.getHeader("surveyorId"));
			clientLoggingService.insertActivityLog(request, response,
					"SIS_Start_Activity");
			float version = Float.parseFloat(request.getHeader("version")
					.split("_")[0]);
			int regionId1 = -1;
			if (Float.compare(version, (float) 1.1) > 0.0) {
				regionId1 = regionId;
			} else {
				regionId1 = -1;
			}
			int activityId = surveyorService.insertSisActivity(surveyorId,
					townId, shopsTarget, title, visitType, regionId1);
			surveyorService.sisJoinActivityRequest(activityId, surveyorId, "Y",
					"APPROVED");
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_CREATED,
					MessageType.SUCCESS, response);
		} catch (Exception e) {
			logger.error(e, e);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR, response);

		}

	}

	@Override
	@ResponseBody
	public List<WorkActivity> getSisActivities(HttpServletRequest request,
			HttpServletResponse response, Integer townId, String visitType,
			Integer regionId) {
		int surveyorId = Integer.parseInt(request.getHeader("surveyorId"));
		float version = Float.parseFloat(request.getHeader("version")
				.split("_")[0]);
		String appType = request.getHeader("appType");
		return surveyorService.getSisActivities(townId, surveyorId, appType,
				visitType, regionId, version);
	}

	@Override
	@ResponseBody
	public void sisJoinActivityRequest(HttpServletRequest request,
			HttpServletResponse response, Integer activityId) {
		try {
			int surveyorId = Integer.parseInt(request.getHeader("surveyorId"));
			clientLoggingService.insertActivityLog(request, response,
					"SIS_Join_Activity");
			surveyorService.sisJoinActivityRequest(activityId, surveyorId, "N",
					"PENDING");
			notificationManager.sendJoinActivityRequestNotification(activityId,
					surveyorId);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_CREATED,
					MessageType.JOIN_ACTIVITY_REQUEST, response);
		} catch (Exception e) {
			logger.error(e, e);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR, response);
		}
	}

	@Override
	@ResponseBody
	public List<WorkActivity> getSisActivityRequestList(
			HttpServletRequest request, HttpServletResponse response) {
		int surveyorId = Integer.parseInt(request.getHeader("surveyorId"));
		return surveyorService.getSisActivityRequestList(surveyorId);
	}

	@Override
	@ResponseBody
	public void updateRequestStatus(HttpServletRequest request,
			HttpServletResponse response, Integer requestId,
			String requestStatus) {
		try {
			// int surveyorId =
			// Integer.parseInt(request.getHeader("surveyorId"));
			clientLoggingService.insertActivityLog(request, response,
					"Update_Request_Status");
			surveyorService.updateRequestStatus(requestId, requestStatus);
			notificationManager.sendApproveDisApproveNotification(requestId,
					requestStatus);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_CREATED,
					MessageType.SUCCESS, response);
		} catch (Exception e) {
			logger.error(e, e);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR, response);
		}
	}

	@Override
	@ResponseBody
	public SyncData getSisShopData(HttpServletRequest request,
			HttpServletResponse respons, Integer shopId) {

		return surveyorService.getSisShopData(request, respons, shopId);
	}

	@Override
	@ResponseBody
	public void endSisActivity(HttpServletRequest request,
			HttpServletResponse response, Integer activityId) {
		try {
			clientLoggingService.insertActivityLog(request, response,
					"SIS_END_Activity");
			surveyorService.endSisActivity(activityId);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_CREATED,
					MessageType.END_SIS_ACTIVITY, response);
		} catch (Exception e) {
			logger.error(e, e);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR, response);

		}

	}

	@Override
	@ResponseBody
	public List<Surveyor> getSisSupervisors(HttpServletRequest request,
			HttpServletResponse response, Integer zoneId, Integer regionId) {

		clientLoggingService.insertActivityLog(request, response,
				"SIS_Supervisors");
		return surveyorService.getSisSupervisors(zoneId, regionId);

	}

	@Override
	@ResponseBody
	public List<Family> getSalesTargets(HttpServletRequest request,
			HttpServletResponse response, Integer shopId, String shopType) {

		clientLoggingService.insertActivityLog(request, response,
				"Sales_Targets");
		return surveyorService.getWSTKSSaleDetails(shopId, shopType);

	}

	@Override
	@ResponseBody
	public ArrayList<SummaryTag> getSummaryForSIS(HttpServletRequest request,
			HttpServletResponse respons, Integer activity_id, String type) {
		String surveyorType = request.getHeader(SysConstants.SURVEYOR_TYPE);
		surveyorType = StringUtils.isNullOrEmptyString(surveyorType) ? "DE"
				: surveyorType;
		float version = Float.parseFloat(request.getHeader("version")
				.split("_")[0]);
		;
		clientLoggingService.insertActivityLog(request, respons, "Summary_For_"
				+ surveyorType);
		return surveyorService.getSummaryForSIS(activity_id, type, version);

	}

	@Override
	@ResponseBody
	public void updateToken(HttpServletRequest request,
			HttpServletResponse respons, String token) {
		int surveyorId = Integer.parseInt(request.getHeader("surveyorId"));
		surveyorService.updateUserToken(surveyorId, token);

	}

	@Override
	public @ResponseBody
	SisShop getSisShops(int areaId, Integer townId, Integer activityId,
			String type, Integer regionId, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("Getting List of Shops BO data from Service Layer");
		String appType = request.getHeader(SysConstants.APP_TYPE);
		String userType = request.getHeader("userType");
		SisShop shops = new SisShop();
		List<Shop> shopList = null;
		List<BrandStock> previousData = null;

		shopList = surveyorService.getShops(areaId, townId, regionId, appType,
				response, activityId, type, userType);
		if (type.equalsIgnoreCase(CommonConstants.RETAIL_AUDIT)) {
			previousData = surveyorService
					.getSisPreviousPurchaseStock(regionId);
		}

		shops.setShopList(shopList);
		shops.setPreviousData(previousData);

		return shops;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDsrAttendanceForDe(
			HttpServletRequest request, HttpServletResponse respons,
			String visitDate, Integer deId) {
		clientLoggingService
				.insertActivityLog(request, respons, "DSR_CHECKINS");
		return surveyorService.getDsrAttendanceForDe(visitDate, deId);
	}

	@Override
	@ResponseBody
	public ArrayList<SummaryTag> getSummaryForDsr(HttpServletRequest request,
			HttpServletResponse respons, Integer id, String type) {

		return surveyorService.getSummaryForDsr(id, type);

	}

	@Override
	@ResponseBody
	public List<Shop> getDsrNewPendingShops(int deId, int dsrId,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info("dsr newly added pending shops request for de : " + deId
				+ ", dsr : " + dsrId);
		clientLoggingService.insertActivityLog(request, response,
				"DSR_NEW_SHOPS");
		List<Shop> newlyAddedPendingShopList = null;
		try {
			newlyAddedPendingShopList = surveyorService.getDsrNewPendingShops(
					deId, dsrId, response);
		} catch (Exception ex) {
			logger.error(
					"Exception occured while getting list of newly added pending shops of dsr",
					ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
		}
		return newlyAddedPendingShopList;
	}

	@Override
	public void updateDsrAddedShopStatus(int shopId, String shopStatus,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info("updating dsr new shop status shopId: " + shopId
				+ ", status : " + shopStatus);
		clientLoggingService.insertActivityLog(request, response,
				"Update_Dsr_new_Shop_status");
		try {
			surveyorService
					.updateDsrNewShopStatus(shopId, shopStatus, response);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_CREATED,
					MessageType.SUCCESS, response);
		} catch (Exception e) {
			logger.error(e, e);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR_SERVER, response);
		}
	}
	
	
}
