package com.marksman.census.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.marksman.census.bo.Area;
import com.marksman.census.bo.BrandPriceDetails;
import com.marksman.census.bo.Build;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.client.bo.CheckInInfo;
import com.marksman.census.client.bo.DsrStockList;
import com.marksman.census.client.bo.ShopRoute;
import com.marksman.census.client.bo.SingleSurvey;
import com.marksman.census.client.bo.SummaryData;
import com.marksman.census.client.bo.SummaryTag;
import com.marksman.census.client.bo.SyncData;
import com.marksman.census.client.bo.VisitImage;
import com.marksman.census.constants.CommonConstants;
import com.marksman.census.constants.DateTimeConstants;
import com.marksman.census.constants.SurveyorType;
import com.marksman.census.constants.SysConstants;
import com.marksman.census.crons.SmsSendingCron;
import com.marksman.census.dao.AreaDao;
import com.marksman.census.dao.DsrDao;
import com.marksman.census.dao.ShopsDao;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.logging.SyncLogging;
import com.marksman.census.message.MessageType;
import com.marksman.census.util.CommonUtil;
import com.marksman.census.util.DateTimeUtilities;
import com.marksman.census.util.FileUtils;
import com.marksman.census.util.StringUtils;

public class DsrServiceImpl implements DsrService {

	@Autowired
	CacheService cacheService;
	@Autowired
	ApplicationCacheService applicationCacheService;
	@Autowired
	private AreaDao areaDao;
	@Autowired
	private ShopsDao shopsDao;
	@Autowired
	private DsrDao dsrDao;
	@Autowired
	private SurveyorDao surveyorDao;
	@Autowired
	ServletContext servletContext;
	@Autowired
	ValidationServiceImpl validationServiceImpl;
	@Autowired
	SyncLogging syncLogging;
	@Autowired
	FileUtils fileUtils;
	@Autowired
	SmsSendingCron smsSendingCron;

	protected static Logger logger = Logger.getLogger(DsrServiceImpl.class);

	private ResourceBundle bundle = ResourceBundle
			.getBundle(CommonConstants.PROPERTY_FILE_NAME);

	@Override
	public Map<String, Object> login(String imei, String dCode, String version,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {

			Surveyor surveyor = dsrDao.getDsrDetailByDsrCode(dCode);
			dsrDao.updateDsrStock(surveyor.getDsrId());
			

			data.put("industries", applicationCacheService.getIndustries());
			data.put("families", dsrDao.getFamilyList(surveyor.getDsrId()));
			data.put("routes", applicationCacheService.getDsrRoutes());
			data.put("remarks", applicationCacheService.getRemarks());
			data.put("remarksTypes", applicationCacheService.getRemarksTypes());
			data.put("promotions", dsrDao.getPromotionList(surveyor.getDsrId(),-1,request.getHeader("type")));
			data.put("questions", applicationCacheService.getQuestions());
			data.put("options", applicationCacheService.getOptions());
			data.put("questionOptions",
					applicationCacheService.getQuestionOptions());
			data.put("stockList", dsrDao.getStockList(surveyor.getDsrId(),surveyor.getCity().getId(),version));
			data.put("attendanceLocations",
					dsrDao.getAttendanceList(surveyor.getDsrId()));
			data.put("areas", areaDao.getDsrAreas(surveyor.getDsrId()));
			data.put("checkInInfo",
					dsrDao.getDsrAttendance(surveyor.getDsrId()));
			data.put("endDayInfo", dsrDao.getDsrEndDay(surveyor.getDsrId()));
			data.put("bwus", applicationCacheService.getBwu());
			data.put("industryBwus",
					applicationCacheService.getDsrIndustryBwu());
			data.put(
					"wholesaleChannel",
					new ArrayList<String>(Arrays.asList(bundle
							.getString(SysConstants.WHOLE_SALE_CHANNEL).trim()
							.split(","))));
			data.put("shopGroups", applicationCacheService.getShopsGroups());
			// data.put("radius", bundle.getString(SysConstants.SHOP_RADIUS));
			data.put("radius", surveyor.getAllowedRadius());
			data.put("promotionStockList", surveyorDao.getDEPromotionList ("-1",dCode));
			data.put(
					"landmarks",
					new ArrayList<String>(Arrays.asList(bundle
							.getString(SysConstants.LANDMARKS).trim()
							.split(","))));
			data.put(
					"segments",
					new ArrayList<String>(Arrays
							.asList(bundle.getString(SysConstants.SEGMENTS)
									.trim().split(","))));
			data.put(
					"profiles",
					new ArrayList<String>(Arrays.asList(bundle
							.getString(SysConstants.PROFILE).trim().split(","))));

			data.put(
					"areaType",
					new ArrayList<String>(Arrays.asList(bundle
							.getString(SysConstants.AREA_TYPE).trim()
							.split(","))));

			data.put("visits", dsrDao.getShopVisits(surveyor.getDsrId()));
			data.put("banks", applicationCacheService.getBanks());

			data.put("surveyor", surveyor);
			jsonMap.put("data", data);
		} catch (Exception ex) {

			logger.error("Exception occured while login against imei " + imei,
					ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return jsonMap;
	}

	@Override
	public List<ShopRoute> dsrShopList(Integer routeId, Integer dsrId,String version,
			HttpServletResponse response) {
		return dsrDao.dsrShopList(routeId, dsrId, version);
	}
	
	@Override
	public List<Area> getDsrAreas(Integer dsrId,
			HttpServletResponse response) {
		return areaDao.getDsrAreas(dsrId);
	}
	@Override
	public List<Map<String, Object>> getDsrList(Integer deId) {

		return dsrDao.getDsrList(deId);
	}

	@Override
	public void updateForceLoginStatus(String dCode) {
		dsrDao.updateForceLoginStatus(dCode);
	}

	@Override
	public Map<String, Object> syncVisit(String version,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responMap = new HashMap<String, Object>();
		String imei = request.getHeader(SysConstants.IMEI);
		if (validationServiceImpl.isValidImei(imei, response)) {
			try {
				ArrayList<VisitImage> visitImages = new ArrayList<VisitImage>();
				boolean isMultiPart = ServletFileUpload
						.isMultipartContent(request);
				Gson gson = new Gson();
				SyncData syncData = null;
				int i = 0;
				if (isMultiPart) {
					ServletFileUpload upload = new ServletFileUpload();
					try {

						FileItemIterator itr = upload.getItemIterator(request);
						while (itr.hasNext()) {
							FileItemStream item = itr.next();
							if (item.isFormField()) {

								InputStream stream = item.openStream();
								String reader = Streams.asString(stream);
								logger.info("Build Version "
										+ request.getHeader("version")
										+ " & IMEI : "
										+ imei
										+ "\n employee id "
										+ request
												.getHeader(SysConstants.EMPLOYEE_ID)
										+ "\n de employee id"
										+ request
												.getHeader(SysConstants.De_EMPLOYEE_ID)
										);
								logger.debug("\nvalidated Shop : " + reader);
								syncData = gson
										.fromJson(reader, SyncData.class);
								syncData.setBuildVersion(request
										.getHeader(SysConstants.VERSION));
								syncData.setImei(imei);
								syncData.setEmployeeId(StringUtils.parseInt(
										request.getHeader(SysConstants.EMPLOYEE_ID),
										0));
								syncData.setDeEmployeeId(StringUtils.parseInt(
										request.getHeader(SysConstants.De_EMPLOYEE_ID),
										0));
								syncLogging
										.saveDsrLog(
												syncData.getDsrId(),
												syncData.getVisit().getShopId(),
												DateTimeUtilities
														.getCurrentDate(DateTimeConstants.DATE_FORMAT3),
												reader);
							} else {
								try {
									if (syncData == null) {

										CommonUtil
												.writeErrorMessage(
														HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
														MessageType.ERROR_MULTIPART_ORDER,
														response);
										return null;
									}
									String imageType = item.getName()
											.split("_")[0];
									// visitImages =
									// syncData.getVisit().getVisitImageArrayList();
									logger.info("Image Type ::   " + imageType);
									if (imageType
											.contains(CommonConstants.VISIT_SHOP_PICTURE)) {
										logger.info("Saving visit shop image");
										this.saveVisitImage(syncData, item);
									} else if (imageType
											.contains(CommonConstants.VISIT_IMAGE)) {
										logger.info("Saving Visit image.");
										this.saveVisitImages(syncData,
												visitImages, item, i);
										i++;
									}

								} catch (Exception ex) {
									logger.info("Error :" + ex, ex);
									CommonUtil
											.writeErrorMessage(
													HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
													MessageType.FILE_SAVING_SERVER,
													response);
									return null;
								}
							}
						}
					} catch (Exception ex) {
						logger.info("Error :" + ex, ex);
						logger.error(
								"Error while saving sync data agianst shop : "
										+ syncData.getVisit().getShopId(), ex);
						CommonUtil.writeErrorMessage(
								HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
								MessageType.ERROR_SERVER, response);
						return null;
					}

					if (syncData != null) {
						syncVisit(syncData, visitImages,
								StringUtils.extractVersion(request
										.getHeader(SysConstants.VERSION)));
						logger.info("Sending Shop id in response "
								+ " shop id : "
								+ syncData.getVisit().getShopId()
								+ " and visit id : "
								+ syncData.getVisit().getId());
						responMap.put("syncResponse", CommonUtil
								.getResponseObj(
										syncData.getVisit().getShopId(),
										syncData.getVisit().getId(), -1));
						return responMap;
					}

				} else {

					logger.error("Data is not multi part ");
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_FORMAT_DATA, response);
					return null;
				}
			} catch (Exception e) {
				logger.error(e, e);
				CommonUtil.writeErrorMessage(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						MessageType.FILE_SAVING_SERVER, response);
				return null;
			}
		} else {
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR_IMEI_PERMISSION, response);
			return null;
		}
		return responMap;
	}

	private void saveVisitImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {
			String temporaryPath = fileUtils.storeSurveyFileForDsr(
					servletContext.getRealPath("/"), syncData.getVisit()
							.getShopId(), CommonConstants.VISIT_SHOP_PICTURE,
					item);
			syncData.getVisit().getVisitImage().setImageUrl(temporaryPath);
		} catch (Exception ex) {

			logger.error(
					"Error while saving Visit shop image in Visit Service : "
							+ syncData.getVisit().getShopId(), ex);
			throw ex;
		}
	}

	private void saveVisitImages(SyncData syncData,
			ArrayList<VisitImage> visitImages, FileItemStream item, int i)
			throws Exception {

		try {

			String temporaryPath = fileUtils.storeSurveyFileForDsr(
					servletContext.getRealPath("/"), syncData.getVisit()
							.getShopId(), CommonConstants.VISIT_IMAGE, item);
			VisitImage visitImage = new VisitImage();
			VisitImage temp_visiImage = syncData.getVisit()
					.getVisitImageArrayList().get(i);
			visitImage.setImageUrl(temporaryPath);
			visitImage.setRemarks(temp_visiImage.getRemarks());
			visitImage.setImageType(temp_visiImage.getImageType());
			visitImages.add(visitImage);

		} catch (Exception ex) {

			logger.error("Error while saving Visit image of Activity : "
					+ syncData.getSyncShop().getId(), ex);
			throw ex;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void syncVisit(SyncData syncData,
			ArrayList<VisitImage> visitImages, String version) throws Exception {

		logger.info("Saving data into db");

		syncData.getVisit().setClientShopId(syncData.getVisit().getShopId());
		if (shopsDao.isDuplicateDsrSurveys(syncData.getVisit().getShopId(),
				syncData.getDsrId(), syncData.getVisit().getDateTime(),
				syncData.getVisit().getRemarkId())) {

			logger.error("Duplicate survey found against shop id: "
					+ syncData.getVisit().getShopId());
			return;
		}
		// checking shop time.
		// if shop time is more than 20 minutes, it will be replaced with total
		// 20 minutes.
		// this.checkShopProductiveTime(syncData);
		this.saveVisitSyncData(syncData, visitImages, version);

	}

	private void saveVisitSyncData(SyncData syncData,
			ArrayList<VisitImage> visitImages, String version) throws Exception {

		Integer surveyId = dsrDao.insertVisitSyncData(syncData);
		if (syncData.getVisit().getFamilyData() != null
				&& syncData.getVisit().getFamilyData().size() > 0) {

			dsrDao.insertDsrShopFamilyData(syncData, surveyId);
		}
		if (syncData.getVisit().getQuestionData() != null
				&& syncData.getVisit().getQuestionData().size() > 0) {

			dsrDao.insertDsrShopQuestionData(syncData, surveyId);
		}
		
		if (syncData.getVisit().getVisitImageArrayList() != null
				&& syncData.getVisit().getVisitImageArrayList().size() > 0) {

			dsrDao.insertVisitImagesData(syncData.getVisit()
					.getVisitImageArrayList(), surveyId);
		}
		// dsr to update phone number on visit
		if (syncData.getVisit().getPhone() != null
				&& !StringUtils.isNullOrEmptyString(syncData.getVisit()
						.getPhone())) {

			dsrDao.updateShopPhoneNumber(syncData.getVisit().getShopId(),
					syncData.getVisit().getPhone());
			smsSendingCron.sendSaleSummary(surveyId, syncData.getVisit()
					.getPhone(), syncData.getVisit().getDateTime());

		}
		dsrDao.updateShopVisitHistory(syncData.getVisit().getShopId(), syncData
				.getVisit().getTime().getStartTime());
		// temporary updation until build is fully circulated
		dsrDao.updateFamilyPrices(surveyId);
		
		dsrDao.updateDsrAppVersion(syncData.getDsrId(), version);
	}

	@Override
	public Map<String, Object> syncCheckInData(String version,
			HttpServletRequest request, HttpServletResponse response)

	{
		Map<String, Object> responMap = new HashMap<String, Object>();
		String imei = request.getHeader("imei");
		if (validationServiceImpl.isValidImei(imei, response)) {
			try {

				boolean isMultiPart = ServletFileUpload
						.isMultipartContent(request);
				Gson gson = new Gson();
				SingleSurvey singleSurvey = null;
				if (isMultiPart) {
					ServletFileUpload upload = new ServletFileUpload();
					try {

						FileItemIterator itr = upload.getItemIterator(request);
						while (itr.hasNext()) {
							FileItemStream item = itr.next();
							if (item.isFormField()) {
								InputStream stream = item.openStream();
								String reader = Streams.asString(stream);
								logger.info("Build Version "
										+ request.getHeader("version")
										);
								logger.debug("\n validated Shop : " + reader);
								singleSurvey = gson.fromJson(reader,
										SingleSurvey.class);
								singleSurvey.setBuildVersion(request.getHeader(
										"version").split("_")[0]);
								singleSurvey.setImei(request.getHeader("imei"));
								syncLogging
										.saveDsrLog(
												singleSurvey.getDsrId(),
												-1,
												DateTimeUtilities
														.getCurrentDate(DateTimeConstants.DATE_FORMAT3),
												reader);
							} else {
								try {
									if (singleSurvey == null) {

										CommonUtil
												.writeErrorMessage(
														HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
														MessageType.ERROR_MULTIPART_ORDER,
														response);
										return null;
									}
									String imageType = item.getFieldName();
									logger.info("Image Type " + imageType);
									if (imageType
											.contains(CommonConstants.SELFIE_IMAGE)) {
										logger.info("Saving Selfie image");
										this.saveSelfieImage(singleSurvey, item);
									}
								} catch (Exception ex) {
									logger.error(ex, ex);
									CommonUtil
											.writeErrorMessage(
													HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
													MessageType.FILE_SAVING_SERVER,
													response);
									return null;
								}
							}
						}
					} catch (Exception ex) {
						logger.error(ex, ex);
						logger.error(
								"Error while saving sync data agianst surveyor id : "
										+ singleSurvey.getSurveyorId(), ex);
						CommonUtil.writeErrorMessage(
								HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
								MessageType.ERROR_SERVER, response);
						return null;
					}

					if (singleSurvey != null) {
						this.saveSingleSurveyData(singleSurvey);
						// logger.info("Sending sync response against surveyor id "
						// + singleSurvey.getSurveyorId());
						// CommonUtil.writeErrorMessage(HttpServletResponse.SC_OK,
						// MessageType.SUCCESS, response);
					}

				} else {

					logger.error("Data is not multi part ");
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_FORMAT_DATA, response);
					return null;
				}
			} catch (Exception e) {
				logger.error(e, e);

				CommonUtil.writeErrorMessage(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						MessageType.FILE_SAVING_SERVER, response);
				return null;
			}
		} else {
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR_IMEI_PERMISSION, response);
			return null;
		}
		return responMap;
	}

	private void saveSelfieImage(SingleSurvey singleSurvey, FileItemStream item)
			throws Exception {

		try {
			String imageDate = item.getName().split("_")[1];
			String imageDateFinal = imageDate.split("\\.")[0];
			String temporaryPath = fileUtils.storeSelfieFileForDsr(
					servletContext.getRealPath("/"), singleSurvey.getDsrId(),
					CommonConstants.SELFIE, item);
			logger.info("imageDate :: " + imageDate);
			logger.info("imageDateFinal :: " + imageDateFinal);
			logger.info("SELFIE image path :: " + temporaryPath);
			for (CheckInInfo checkInInfo : singleSurvey.getCheckInInfos()) {
				String attendanceTime = checkInInfo.getDateTime().split(" ")[0];
				/*
				 * if (checkInInfo.getDateTime().equalsIgnoreCase(imageDate)) {
				 * checkInInfo.getMorningImage().setImageUrl(temporaryPath); }
				 */
				if (attendanceTime.equalsIgnoreCase(imageDateFinal)) {
					checkInInfo.getMorningImage().setImageUrl(temporaryPath);
				}
			}
		} catch (Exception ex) {
			logger.error(ex, ex);
			logger.error(
					"Error while saving selfie image against surveyor id : "
							+ singleSurvey.getSurveyorId(), ex);
			throw ex;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void saveSingleSurveyData(SingleSurvey singleSurvey)
			throws Exception {

		logger.info("Saving dsr attendance.....");
		ArrayList<DsrStockList> tempStockList = new ArrayList<DsrStockList>();
		if (singleSurvey.getCheckInInfos().size() > 0) {

			for (CheckInInfo checkInInfo : singleSurvey.getCheckInInfos()) {
				checkInInfo.setId(dsrDao.insertCheckIn(
						singleSurvey.getSurveyorId(),
						singleSurvey.getEmployeeId(), singleSurvey.getDsrId(),
						checkInInfo, singleSurvey.getBuildVersion(),
						singleSurvey.getImei()));

				for (DsrStockList stockList : checkInInfo.getStockList()) {
					if (stockList.getStock() > 0) {
						tempStockList.add(stockList);
					}
				}
				if (tempStockList != null && tempStockList.size() > 0) {
					dsrDao.insertDsrStockEntry(singleSurvey.getDsrId(),
							checkInInfo, tempStockList, singleSurvey.getEmployeeId(),-1);
				}
			}
		}

		if (singleSurvey.getTposmInventory() != null
				&& singleSurvey.getTposmInventory().size() > 0) {
			dsrDao.insertTposmInventory(singleSurvey.getDsrId(),
					singleSurvey.getTposmInventory());
		}
		/*
		 * if(singleSurvey.getActivity() != null) {
		 * surveyorDao.insertActivity(singleSurvey.getActivity()); }
		 */

	}

	@Override
	public Map<String, Object> checkUpdates(SurveyorType appType,
			HttpServletRequest request, HttpServletResponse response) {

		if (applicationCacheService.getBuildsMap().containsKey(
				appType.toString())) {

			String appTypee = request.getHeader("appType");
			Build build = applicationCacheService.getBuildsMap().get(
					appTypee.toString());
			String version = request.getHeader("version").split("_")[0];
			String imei = request.getHeader("imei");

			Integer surveyorId = Integer.parseInt(request
					.getHeader("surveyorId"));
			Integer dsrId = Integer.parseInt(request.getHeader("dsrId"));
			Surveyor surveyor = dsrDao.getDsrById(dsrId);
			int employeeId = surveyorId;
			if (request.getHeader("employeeId") != null) {
				employeeId = Integer.parseInt(request.getHeader("employeeId"));
			}
			int cityId = -1;
			if (request.getHeader("cityId") != null) {
				cityId = Integer.parseInt(request.getHeader("cityId"));
			}
			logger.info(" request version : " + version
					+ ", current version : " + build.getVersion()
					+ " , employeeId : " + employeeId + ", surveyorId : "
					+ surveyorId + ", imei: " + imei + ", appType: "
					+ appTypee.toString() + ", cityId:" + cityId );

			if ("Y".equalsIgnoreCase(surveyor.getForceLogin())) {
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.ERROR_FORCE_LOGIN, response);
				return null;
			}

			if (Boolean.parseBoolean(bundle.getString(
					CommonConstants.IMEI_VALIDATION).trim())
					&& !validationServiceImpl.isValidImei(imei, response)) {

				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.ERROR_IMEI_PERMISSION, response);
				return null;
			}
			if (!dsrDao.isValidEmployeeCode(dsrId, employeeId)) {
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.ERROR_INVALID_EMPLOYEECODE, response);
				return null;
			}

			if (request.getHeader("deviceDateTime") != null
					&& !this.isCorrectDateTime(request
							.getHeader("deviceDateTime"))) {
				CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
						MessageType.ERROR_TIME_DIFFERENCE, response);
				return null;
			}
			if ("Y".equalsIgnoreCase(surveyor.getVersionAlert())
					&& version.compareTo("5.4") < 0) {
				response.setHeader("url", build.getDownloadUrl());
				response.setHeader("version", build.getVersion());
				CommonUtil.writeErrorMessage( 	
						HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED,
						MessageType.ERROR_VERSION, response);
				return null;

			} else {

				return refreshData(imei, surveyorId, appType.toString(),
						request, response, dsrId, cityId,version);
			}
		}
		CommonUtil.writeErrorMessage(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				MessageType.ERROR_SERVER, response);
		return null;

	}

	protected boolean isCorrectDateTime(String deviceTime) {

		Date deviceDateTime = DateTimeUtilities.stringToDate(deviceTime,
				"yyyy-MM-dd HH:mm:ss");
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(deviceDateTime);
		long deviceMillies = calendar1.getTimeInMillis();

		Date currentDate = DateTimeUtilities.getCurrentTimestamp();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		long currentMillies = calendar.getTimeInMillis();

		float diff = Math.abs((currentMillies - deviceMillies) / 60000);
		int MinuteLimit = Integer.parseInt(bundle.getString(
				CommonConstants.TIME_DIFFERENCE).trim());
		if (diff > MinuteLimit) {

			logger.error("Device Time is not correct, Please change your device time");
			return false;
		}
		return true;
	}

	private Map<String, Object> refreshData(String imei, Integer surveyorId,
			String surveyorType, HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, Integer cityId, String version) {

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {

			Surveyor surveyor = dsrDao.getDsrDetailByDsrId(dsrId);
			dsrDao.updateDsrStock(surveyor.getDsrId());

			data.put("industries", applicationCacheService.getIndustries());
			data.put("families", dsrDao.getFamilyList(surveyor.getDsrId()));
			data.put("routes", applicationCacheService.getDsrRoutes());
			data.put("remarks", applicationCacheService.getRemarks());
			data.put("remarksTypes", applicationCacheService.getRemarksTypes());
			data.put("promotions", dsrDao.getPromotionList(surveyor.getDsrId(),-1,request.getHeader("type")));
			data.put("questions", applicationCacheService.getQuestions());
			data.put("options", applicationCacheService.getOptions());
			data.put("questionOptions",
					applicationCacheService.getQuestionOptions());
			 data.put("stockList", dsrDao.getStockList(surveyor.getDsrId(),cityId, version));
			data.put("attendanceLocations",
					dsrDao.getAttendanceList(surveyor.getDsrId()));
			data.put("areas", areaDao.getDsrAreas(surveyor.getDsrId()));
			data.put("shops", dsrDao.dsrShopList(-1, surveyor.getDsrId(),version));
			data.put("bwus", applicationCacheService.getBwu());
			data.put("industryBwus",
					applicationCacheService.getDsrIndustryBwu());
			data.put(
					"wholesaleChannel",
					new ArrayList<String>(Arrays.asList(bundle
							.getString(SysConstants.WHOLE_SALE_CHANNEL).trim()
							.split(","))));
			data.put("shopGroups", applicationCacheService.getShopsGroups());
			data.put("radius", surveyor.getAllowedRadius());
			data.put(
					"landmarks",
					new ArrayList<String>(Arrays.asList(bundle
							.getString(SysConstants.LANDMARKS).trim()
							.split(","))));
			data.put(
					"segments",
					new ArrayList<String>(Arrays
							.asList(bundle.getString(SysConstants.SEGMENTS)
									.trim().split(","))));
			data.put(
					"profiles",
					new ArrayList<String>(Arrays.asList(bundle
							.getString(SysConstants.PROFILE).trim().split(","))));

			data.put(
					"areaType",
					new ArrayList<String>(Arrays.asList(bundle
							.getString(SysConstants.AREA_TYPE).trim()
							.split(","))));

			data.put("visits", dsrDao.getShopVisits(surveyor.getDsrId()));
			data.put("banks", applicationCacheService.getBanks());

			data.put("surveyor", surveyor);
			jsonMap.put("data", data);
		} catch (Exception ex) {

			logger.error("Exception occured while login against imei " + imei,
					ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return jsonMap;
	}

	@Override
	public ArrayList<SummaryTag> dsrSaleSummary(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, String date, String version) {
		return dsrDao.dsrSaleSummary(dsrId, date, version);

	}

	@Override
	public Map<String, Object> dsrStock(Integer dsrId,String version,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			dsrDao.updateDsrStock(dsrId);

			data.put("stockList", dsrDao.getStockList(dsrId,-1,version));

			jsonMap.put("data", data);
		} catch (Exception ex) {

			logger.error("Exception occured while getting stock", ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return jsonMap;
	}

	@Override
	public Map<String, Object> syncDayEndData(String version,
			HttpServletRequest request, HttpServletResponse response)

	{
		Map<String, Object> responMap = new HashMap<String, Object>();
		String imei = request.getHeader("imei");
		if (validationServiceImpl.isValidImei(imei, response)) {
			try {

				boolean isMultiPart = ServletFileUpload
						.isMultipartContent(request);
				Gson gson = new Gson();
				SingleSurvey singleSurvey = null;
				if (isMultiPart) {
					ServletFileUpload upload = new ServletFileUpload();
					try {

						FileItemIterator itr = upload.getItemIterator(request);
						while (itr.hasNext()) {
							FileItemStream item = itr.next();
							if (item.isFormField()) {
								InputStream stream = item.openStream();
								String reader = Streams.asString(stream);
								logger.info("Build Version "
										+ request.getHeader("version"));
								logger.debug( "\n validated Shop : " + reader);
								singleSurvey = gson.fromJson(reader,
										SingleSurvey.class);
								singleSurvey.setBuildVersion(request.getHeader(
										"version").split("_")[0]);
								singleSurvey.setImei(request.getHeader("imei"));
								syncLogging
										.saveDsrLog(
												singleSurvey.getDsrId(),
												-1,
												DateTimeUtilities
														.getCurrentDate(DateTimeConstants.DATE_FORMAT3),
												reader);
							} else {
								try {
									if (singleSurvey == null) {

										CommonUtil
												.writeErrorMessage(
														HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
														MessageType.ERROR_MULTIPART_ORDER,
														response);
										return null;
									}
									String imageType = item.getFieldName();
									logger.info("Image Type " + imageType);
									if (imageType
											.contains(CommonConstants.SELFIE_IMAGE)) {
										logger.info("Saving Selfie image");
										this.saveSelfieImage(singleSurvey, item);
									}
								} catch (Exception ex) {
									logger.error(ex, ex);
									CommonUtil
											.writeErrorMessage(
													HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
													MessageType.FILE_SAVING_SERVER,
													response);
									return null;
								}
							}
						}
					} catch (Exception ex) {
						logger.error(ex, ex);
						logger.error(
								"Error while saving sync data agianst surveyor id : "
										+ singleSurvey.getSurveyorId(), ex);
						CommonUtil.writeErrorMessage(
								HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
								MessageType.ERROR_SERVER, response);
						return null;
					}

					if (singleSurvey != null) {
						this.saveSingleSurveyDayEndData(singleSurvey);
						// logger.info("Sending sync response against surveyor id "
						// + singleSurvey.getSurveyorId());
						// CommonUtil.writeErrorMessage(HttpServletResponse.SC_OK,
						// MessageType.SUCCESS, response);
					}

				} else {

					logger.error("Data is not multi part ");
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_FORMAT_DATA, response);
					return null;
				}
			} catch (Exception e) {
				logger.error(e, e);

				CommonUtil.writeErrorMessage(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						MessageType.FILE_SAVING_SERVER, response);
				return null;
			}
		} else {
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR_IMEI_PERMISSION, response);
			return null;
		}
		return responMap;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void saveSingleSurveyDayEndData(SingleSurvey singleSurvey)
			throws Exception {

		logger.info("Saving dsr day end.....");
		ArrayList<DsrStockList> tempStockList = new ArrayList<DsrStockList>();
		if (singleSurvey.getCheckInInfos().size() > 0) {

			for (CheckInInfo checkInInfo : singleSurvey.getCheckInInfos()) {
				checkInInfo.setId(dsrDao.insertDayEnd(
						singleSurvey.getSurveyorId(),
						singleSurvey.getEmployeeId(), singleSurvey.getDsrId(),
						checkInInfo, singleSurvey.getBuildVersion(),
						singleSurvey.getImei()));
				for (DsrStockList stockList : checkInInfo.getStockList()) {
					if (stockList.getStock() < 0) {
						tempStockList.add(stockList);
					}
				}
				if (tempStockList != null && tempStockList.size() > 0) {
					dsrDao.insertDayEndStockEntry(singleSurvey.getDsrId(),
							checkInInfo, tempStockList, singleSurvey.getEmployeeId());
				}
			}
		}

		if (singleSurvey.getTposmInventory() != null
				&& singleSurvey.getTposmInventory().size() > 0) {
			dsrDao.insertTposmInventory(singleSurvey.getDsrId(),
					singleSurvey.getTposmInventory());
		}
		/*
		 * if(singleSurvey.getActivity() != null) {
		 * surveyorDao.insertActivity(singleSurvey.getActivity()); }
		 */

	}

	@Override
	public List<Map<String, Object>> getShopPreviousThreeVisits(
			HttpServletRequest request, HttpServletResponse response,
			Integer shopId) {
		try {
			return dsrDao.getShopPreviousThreeVisits(shopId);
		} catch (Exception ex) {

			logger.error(
					"Exception occured while getting previous three visits", ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}

	}

	@Override
	public ArrayList<SummaryTag> getDsrSummary(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, String date,
			String type) {

		SummaryData summaryData = new SummaryData();
		summaryData = dsrDao.getDsrSummary(dsrId, date, type);
		return summaryData.getSummarData();

	}

	@Override
	public List<Map<String, Object>> promotionList(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, Integer shopId) {
		try {
			return dsrDao.getPromotionList(dsrId, shopId,request.getHeader("type"));
		} catch (Exception ex) {

			logger.error("Exception occured while getting promotionlist", ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}

	}

	@Override
	public Map<String, Object> stockList(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, Integer cityId,String version, String appType) {

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {

			data.put("stockList", dsrDao.getStockLists(dsrId, cityId, version, appType));

			jsonMap.put("data", data);
		} catch (Exception ex) {

			logger.error("Exception occured while getting stock", ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return jsonMap;
	}
	
	@Override
	public ArrayList<SummaryTag> dsrBrandWiseSaleSummary(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, String brandName, String version) {
		return dsrDao.dsrBrandWiseSaleSummary(dsrId, brandName, version);

	}
	
	@Override
	public int updateProfile(Surveyor updateProfile)
			throws Exception {
		return dsrDao.updateProfile(updateProfile);
	}
	
	@Override
	public ArrayList<SummaryTag> getTgtVsAch(int dsrId, String type) {
		return dsrDao.TgtVsAch(dsrId, type);

	}
	
	@Override
	public Map<String, Object> mvFiles(
			HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> responMap = new HashMap<String, Object>();
		String file = request.getHeader(SysConstants.FILE);
		String version = request.getHeader("version");
			
		if (validationServiceImpl.isValidImei(file, response)) {
		
			try {
				this.mvFiles(version);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			try {
//
//				ArrayList<BwuImage> bwuImages = new ArrayList<BwuImage>();
//				ArrayList<VisitImage> visitImages = new ArrayList<VisitImage>();
//				boolean isMultiPart = ServletFileUpload
//						.isMultipartContent(request);
//				Gson gson = new Gson();
//				SyncData syncData = null;
//				
//				int i = 0;
//				if (isMultiPart) {
//					ServletFileUpload upload = new ServletFileUpload();
//					try {
//
//						FileItemIterator itr = upload.getItemIterator(request);
//						while (itr.hasNext()) {
//							FileItemStream item = itr.next();
//							if (item.isFormField()) {
//								InputStream stream = item.openStream();
//								String reader = Streams.asString(stream);
//								logger.info("Build Version "
//										+ request.getHeader("version")
//										+ " & IMEI : "
//										+ file
//										+ "\n employee id "
//										+ request
//												.getHeader(SysConstants.EMPLOYEE_ID));
//								logger.debug("\nvalidated Shop : " + reader);
//								syncData = gson
//										.fromJson(reader, SyncData.class);
//								if (syncData.getVisit().getDsrId() > -1) {
//									Dsr dsr = applicationCacheService
//											.getDsrById(syncData.getVisit()
//													.getDsrId());
//									syncData.setDsrEmployeeId(dsr == null ? -1
//											: dsr.getEmployeeId());
//								}
//								syncData.setBuildVersion(request
//										.getHeader(SysConstants.VERSION));
//								syncData.setEmployeeId(StringUtils.parseInt(
//										request.getHeader(SysConstants.EMPLOYEE_ID),
//										0));
//
//								syncLogging
//										.saveLog(
//												syncData.getSurveyorId(),
//												syncData.getVisit().getShopId(),
//												DateTimeUtilities
//														.getCurrentDate(DateTimeConstants.DATE_FORMAT3),
//												reader);
//							} else {
//								try {
//									if (syncData == null) {
//
//										CommonUtil
//												.writeErrorMessage(
//														HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//														MessageType.ERROR_MULTIPART_ORDER,
//														response);
//										return null;
//									}
//									String imageType = item.getName()
//											.split("_")[0];
//									logger.info("Image Type ::   " + imageType);
//									if (imageType
//											.equalsIgnoreCase(CommonConstants.VISIT_SHOP_PICTURE)) {
//										logger.info("Saving visit shop image");
//										this.saveVisitImage(syncData, item);
//									}
//
//									else if (imageType
//											.equalsIgnoreCase(CommonConstants.TPOSM_PICTURE)
//											&& syncData.getVisit()
//													.getIsTposmAvailable()
//													.equalsIgnoreCase("Y")) {
//										logger.info("Saving TPOSM image.");
//										this.saveTPOSMImage(syncData, item);
//									} else if (imageType
//											.equalsIgnoreCase(CommonConstants.VISIT_IMAGE)) {
//										logger.info("Saving Visit image.");
//										this.saveVisitImages(syncData,
//												visitImages, item, i);
//										i++;
//									} else if (imageType
//											.equalsIgnoreCase(CommonConstants.BWU_IMAGE)) {
//										logger.info("Saving BWU image");
//										this.saveSisBwuImage(syncData,
//												bwuImages, item);
//									} else if (imageType
//											.equalsIgnoreCase(CommonConstants.RETAIL_AUDIT_IMAGE)) {
//										logger.info("Saving Retail Audit image");
//										this.saveRetailAuditImage(syncData,
//												item);
//									} else if (imageType
//											.equalsIgnoreCase(CommonConstants.SIS_REMARK_IMAGE)) {
//										logger.info("Saving Remark image");
//										this.saveRemarkImage(syncData, item);
//									} else if (imageType
//											.equalsIgnoreCase(CommonConstants.BRAND_IMAGE)) {
//										logger.info("Saving Brand image");
//										this.saveBrandImage(syncData, item);
//									} else if (imageType
//											.equalsIgnoreCase(CommonConstants.INVOICE_IMAGE)) {
//										logger.info("Saving Invoice image");
//										this.saveInvoiceImage(syncData, item);
//									}
//
//								} catch (Exception ex) {
//									logger.info("Error :" + ex, ex);
//									CommonUtil
//											.writeErrorMessage(
//													HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//													MessageType.FILE_SAVING_SERVER,
//													response);
//									return null;
//								}
//							}
//						}
//					} catch (Exception ex) {
//						logger.info("Error :" + ex, ex);
//						logger.error(
//								"Error while saving sync data agianst shop : "
//										+ syncData.getVisit().getShopId(), ex);
//						CommonUtil.writeErrorMessage(
//								HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//								MessageType.ERROR_SERVER, response);
//						return null;
//					}
//
//					if (syncData != null) {
//						syncSisVisit(syncData, visitImages, bwuImages);
//						logger.info("Sending Shop id in response "
//								+ " shop id : "
//								+ syncData.getVisit().getShopId());
//						responMap.put("syncResponse", CommonUtil
//								.getResponseObj(
//										syncData.getVisit().getShopId(),
//										syncData.getVisit().getId(), -1));
//						// return responMap;
//					}
//
//				} else {
//
//					logger.error("Data is not multi part ");
//					CommonUtil.writeErrorMessage(
//							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//							MessageType.ERROR_FORMAT_DATA, response);
//					return null;
//				}
//			} catch (Exception e) {
//				logger.error(e, e);
//				CommonUtil.writeErrorMessage(
//						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//						MessageType.FILE_SAVING_SERVER, response);
//				return null;
//			}
		}
		else {
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR_IMEI_PERMISSION, response);
			return null;
		}
		return responMap;
	}
	
	
	public Map<String, Object> mvFiles(String version) throws Exception
	{
		List<Map<String, Object>> fileList = shopsDao.getFileLists();
		for (Map<String, Object> file : fileList)
		{

			String source = file.get("file_path").toString();
			String jsonFilePath = servletContext.getRealPath("/") + source;
			  StringBuilder jsonContent = new StringBuilder();
			if (!jsonFilePath.isEmpty())
			{
				BufferedReader br = null;
				try {
				    br = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFilePath), "UTF-8"));
				  
				    String line;
				    while ((line = br.readLine()) != null) {
				        jsonContent.append(line);
				    }
				    // Rest of your code here
				} catch (IOException e) {
				    e.printStackTrace();
				} finally {
				    if (br != null) {
				        try {
				            br.close();
				        } catch (IOException e) {
				            e.printStackTrace();
				        }
				    }
				}
				
				// Deserialize JSON into Java Business Object
		        Gson gson = new Gson();
		        SyncData businessObject = gson.fromJson(jsonContent.toString(), SyncData.class);
logger.debug("g"+businessObject);
//saveData(businessObject, null);
syncVisit(businessObject, null, version);
			}
		}
		return null;
	}

}