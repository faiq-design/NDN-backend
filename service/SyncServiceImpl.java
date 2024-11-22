package com.marksman.census.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.marksman.census.bo.Dsr;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.client.bo.Activity;
import com.marksman.census.client.bo.BrandStock;
import com.marksman.census.client.bo.BwuImage;
import com.marksman.census.client.bo.CheckInInfo;
import com.marksman.census.client.bo.DsrStockList;
import com.marksman.census.client.bo.Image;
import com.marksman.census.client.bo.InterceptionQuestionDetail;
import com.marksman.census.client.bo.InterceptionSurvey;
import com.marksman.census.client.bo.InvoiceImage;
import com.marksman.census.client.bo.MeBwuImage;
import com.marksman.census.client.bo.PposmData;
import com.marksman.census.client.bo.SingleSurvey;
import com.marksman.census.client.bo.SyncAsset;
import com.marksman.census.client.bo.SyncData;
import com.marksman.census.client.bo.SyncFamily;
import com.marksman.census.client.bo.SyncIndustry;
import com.marksman.census.client.bo.SyncProductDisplay;
import com.marksman.census.client.bo.SyncQuestion;
import com.marksman.census.client.bo.Time;
import com.marksman.census.client.bo.Visit;
import com.marksman.census.client.bo.VisitImage;
import com.marksman.census.client.bo.WsSurvey;
import com.marksman.census.constants.CommonConstants;
import com.marksman.census.constants.DateTimeConstants;
import com.marksman.census.constants.ShopConstants;
import com.marksman.census.constants.SurveyorType;
import com.marksman.census.constants.SysConstants;
import com.marksman.census.dao.AssetsDao;
import com.marksman.census.dao.BwusDao;
import com.marksman.census.dao.DsrDao;
import com.marksman.census.dao.FamiliesDao;
import com.marksman.census.dao.IndustriesDao;
import com.marksman.census.dao.ProductsDao;
import com.marksman.census.dao.QuestionsDao;
import com.marksman.census.dao.ShopsDao;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.logging.SyncLogging;
import com.marksman.census.message.MessageType;
import com.marksman.census.util.CommonUtil;
import com.marksman.census.util.DateTimeUtilities;
import com.marksman.census.util.FileUtils;
import com.marksman.census.util.StringUtils;

public class SyncServiceImpl implements SyncService {

	@Autowired
	ServletContext servletContext;
	@Autowired
	private ShopsDao shopsDao;
	@Autowired
	private SurveyorDao surveyorDao;
	@Autowired
	private QuestionsDao questionsDao;
	@Autowired
	private IndustriesDao industriesDao;
	@Autowired
	private FamiliesDao familiesDao;
	@Autowired
	private ProductsDao productsDao;
	@Autowired
	private AssetsDao assetsDao;
	@Autowired
	private BwusDao bwusDao;
	@Autowired
	private DsrDao dsrDao;
	@Autowired
	SyncLogging syncLogging;
	@Autowired
	FileUtils fileUtils;
	@Autowired
	ValidationServiceImpl validationServiceImpl;
	@Autowired
	ApplicationCacheService applicationCacheService;

	protected static Logger logger = Logger.getLogger(SyncServiceImpl.class);
	private ResourceBundle bundle = ResourceBundle
			.getBundle(CommonConstants.PROPERTY_FILE_NAME);

	@Override
	public Map<String, Object> syncData(String version,
			HttpServletRequest request, HttpServletResponse response)

	{
		Map<String, Object> responMap = new HashMap<String, Object>();
		String imei = request.getHeader("imei");
		String appType = request.getHeader("appType") != null ? request
				.getHeader("appType") : "";
	   String surveyorId = request.getHeader("surveyorId");
		if (validationServiceImpl.isValidImei(imei, response)) {
			try {
				ArrayList<BwuImage> bwuImages = new ArrayList<BwuImage>();
				boolean isMultiPart = ServletFileUpload
						.isMultipartContent(request);
				Gson gson = new Gson();
				SyncData syncData = null;
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
								logger.debug( "\n validated Shop : " + reader);
								syncData = gson
										.fromJson(reader, SyncData.class);
								syncData.setBuildVersion(request
										.getHeader("version"));
								syncData.setImei(imei);
								logger.info("appType: " + appType);
								if (!StringUtils.isNullOrEmptyString(surveyorId)) {
									
								    int surveyorIdInt = Integer.parseInt(surveyorId);

								    if (surveyorIdInt > -1) {
								    	
								        syncData.getSyncShop().setSurveyorId(surveyorIdInt);
								    }
								}
								if (appType.equalsIgnoreCase("DSR")) {
									syncLogging
											.saveDsrLog(
													syncData.getSyncShop()
															.getDsrId(),
													syncData.getSyncShop()
															.getId(),
													DateTimeUtilities
															.getCurrentDate(DateTimeConstants.DATE_FORMAT3),
													reader);
								} else {
									syncLogging
											.saveLog(
													syncData.getSyncShop()
															.getSurveyorId(),
													syncData.getSyncShop()
															.getId(),
													DateTimeUtilities
															.getCurrentDate(DateTimeConstants.DATE_FORMAT3),
													reader);
								}

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
									logger.info("Image Type " + imageType);
									if (imageType
											.contains(CommonConstants.SHOP_IMAGE)) {
										logger.info("Saving shop image");
										this.saveShopImage(syncData, item);
									} else if (imageType
											.contains(CommonConstants.BWU_IMAGE)) {
										logger.info("Saving BWU image");
										this.saveBwuImage(syncData, bwuImages,
												item);
									} else if (imageType
											.contains(CommonConstants.PROGRAM_IMAGE)) {

										logger.info("Saving Program image");
										this.saveProgramImage(syncData, item);
									} else if (imageType
											.contains(CommonConstants.BILL_IMAGE)) {

										logger.info("Saving bill image");
										this.saveBillImage(syncData, item);
									} else if (imageType
											.contains(CommonConstants.DISPLAY_IMAGE)) {

										logger.info("Saving display image");
										this.saveDisplayImage(syncData, item);
									} else if (imageType
											.contains(CommonConstants.ASSET_IMAGE)) {

										logger.info("Saving asset image");
										this.saveAssetImage(syncData, item);
									}
								} catch (Exception ex) {
									logger.error(ex, ex);
									logger.error("shop id "
											+ syncData.getSyncShop().getId());
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
						logger.error("Error while saving sync data agianst shop : "
								+ syncData.getSyncShop().getId());
						CommonUtil.writeErrorMessage(
								HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
								MessageType.ERROR_SERVER, response);
						return null;
					}
					if (syncData != null) {
						saveData(syncData, bwuImages);
						logger.info("Sending sync response against shop id "
								+ syncData.getSyncShop().getId()
								+ ", client shop id : "
								+ syncData.getSyncShop().getClientShopId());
						responMap.put("syncResponse", CommonUtil
								.getResponseObj(syncData.getSyncShop().getId(),
										syncData.getSyncShop()
												.getClientShopId(), -1));
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

	@Override
	public Map<String, Object> syncVisit(String version,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responMap = new HashMap<String, Object>();
		String imei = request.getHeader(SysConstants.IMEI);
		if (validationServiceImpl.isValidImei(imei, response)) {
			try {
				ArrayList<MeBwuImage> meBwuImages = new ArrayList<MeBwuImage>();
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
												.getHeader(SysConstants.EMPLOYEE_ID));
								logger.debug( "\nvalidated Shop : " + reader);
								syncData = gson
										.fromJson(reader, SyncData.class);
								if (syncData.getVisit().getDsrId() > -1) {
									Dsr dsr = applicationCacheService
											.getDsrById(syncData.getVisit()
													.getDsrId());
									syncData.setDsrEmployeeId(dsr == null ? -1
											: dsr.getEmployeeId());
								}
								syncData.setBuildVersion(request
										.getHeader(SysConstants.VERSION));
								syncData.setImei(imei);
								syncData.setEmployeeId(StringUtils.parseInt(
										request.getHeader(SysConstants.EMPLOYEE_ID),
										0));
								syncData.setSurveyorId(StringUtils.parseInt(
										request.getHeader(SysConstants.SURVEYOR_ID),
										0));

								syncLogging
										.saveLog(
												syncData.getSurveyorId(),
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
									logger.info("Image Type ::   " + imageType);
									if (imageType
											.equalsIgnoreCase(CommonConstants.VISIT_SHOP_PICTURE)) {
										logger.info("Saving visit shop image");
										this.saveVisitImage(syncData, item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.SHELF_DISPLAY_PICTURE)) {
										logger.info("Saving shelf display image");
										this.saveShelfDisplayImage(syncData,
												item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.AUDIO_TYPE)) {
										logger.info("Saving Audio file.");
										this.saveAudioFiles(syncData, item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.DAMAGED_REPAIRED_PPOSM_PICTURE)) {
										logger.info("Saving Damaged Repaired PPOSM image.");
										this.saveDamageRepairedPPOSMImage(
												syncData, item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.TPOSM_PICTURE)
											&& syncData.getVisit()
													.getIsTposmAvailable()
													.equalsIgnoreCase("Y")) {
										logger.info("Saving TPOSM image.");
										this.saveTPOSMImage(syncData, item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.VISIT_IMAGE)) {
										logger.info("Saving Visit image.");
										this.saveVisitImages(syncData,
												visitImages, item, i);
										i++;
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.MP3_AUDIO_FILE)) {
										logger.info("Saving MP3 Audio file.");
										this.saveMp3AudioFiles(syncData, item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.BWU_IMAGE)) {
										logger.info("Saving ME BWU image");
										this.saveMEBwuImage(syncData,
												meBwuImages, item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.ME_COMPETITION_QUESTION_IMAGE)) {
										logger.info("Saving ME Competition Question image");
										this.saveMEQuestionImage(syncData, item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.QUESTION_IMAGE_TYPE)) {

										logger.info("Saving question image");
										this.saveQuestionImage(syncData, item);
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
						syncVisit(syncData, visitImages, meBwuImages);
						logger.info("Sending Shop id in response "
								+ " shop id : "
								+ syncData.getVisit().getShopId());
						responMap.put("syncResponse", CommonUtil
								.getResponseObj(syncData.getVisit()
										.getClientShopId(), -1, -1));
						// return responMap;
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

	@Override
	public Map<String, Object> endActivity(String version,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responMap = new HashMap<String, Object>();
		String imei = request.getHeader("imei");
		if (validationServiceImpl.isValidImei(imei, response)) {
			try {
				// ArrayList<Image> visitImages = new ArrayList<Image>();
				boolean isMultiPart = ServletFileUpload
						.isMultipartContent(request);
				Gson gson = new Gson();
				Activity activity = null;
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
								activity = gson
										.fromJson(reader, Activity.class);
								syncLogging
										.saveLogEndActivity(
												Integer.parseInt(request
														.getHeader("surveyorId")),
												activity.getActivityId(),
												DateTimeUtilities
														.getCurrentDate(DateTimeConstants.DATE_FORMAT3),
												reader);
							} else {
								try {
									if (activity == null) {

										CommonUtil
												.writeErrorMessage(
														HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
														MessageType.ERROR_MULTIPART_ORDER,
														response);
										return null;
									}

								} catch (Exception ex) {

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
						logger.error(
								"Error while saving sync data agianst activity : "
										+ activity.getActivityId(), ex);
						CommonUtil.writeErrorMessage(
								HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
								MessageType.ERROR_SERVER, response);
						logger.info("Error :", ex);
						return null;
					}

					if (activity != null) {
						syncEndActivity(activity);
						logger.info("Sending activity id in response "
								+ " activity id : " + activity.getActivityId());
						responMap
								.put("syncResponse", CommonUtil.getResponseObj(
										-1, -1, activity.getActivityFeedBack()
												.getActivityId()));
					}

				} else {

					logger.error("Data is not multi part ");
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_FORMAT_DATA, response);
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
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
								logger.debug("Build Version "
										+ request.getHeader("version")
										+ "\n validated Shop : " + reader);
								singleSurvey = gson.fromJson(reader,
										SingleSurvey.class);
								singleSurvey.setBuildVersion(request.getHeader(
										"version").split("_")[0]);
								singleSurvey.setImei(request.getHeader("imei"));
								syncLogging
										.saveLog(
												singleSurvey.getSurveyorId(),
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
									} else if (imageType
											.contains(CommonConstants.ME_DE_MEETING_IMAGE)) {
										logger.info("Saving Me-DE Meeting image");
										this.saveMeDEMeetingImage(singleSurvey,
												item);
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

	@Override
	public Map<String, Object> syncEndDayData(String version,
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
										+ "\n validated Shop : " + reader);
								singleSurvey = gson.fromJson(reader,
										SingleSurvey.class);
								singleSurvey.setBuildVersion(request.getHeader(
										"version").split("_")[0]);
								singleSurvey.setImei(request.getHeader("imei"));
								syncLogging
										.saveLog(
												singleSurvey.getSurveyorId(),
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
									} else if (imageType
											.contains(CommonConstants.ME_DE_MEETING_IMAGE)) {
										logger.info("Saving Me-DE Meeting image");
										this.saveMeDEMeetingImage(singleSurvey,
												item);
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
						this.saveSingleSurveysData(singleSurvey);
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

	@Override
	public Map<String, Object> syncAsmData(String version,
			HttpServletRequest request, HttpServletResponse response)

	{
		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			ArrayList<BwuImage> bwuImages = new ArrayList<BwuImage>();
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			SyncData syncData = null;
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
									+ "\n validated Shop : " + reader);
							syncData = gson.fromJson(reader, SyncData.class);
							syncData.setBuildVersion(request
									.getHeader("version"));
							syncData.setAsmVerification("1");
							syncData.getSyncShop().setSurveyorType(
									SurveyorType.ASM.toString());
							syncLogging
									.saveLog(
											syncData.getSyncShop()
													.getSurveyorId(),
											syncData.getSyncShop().getId(),
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
								String imageType = item.getName().split("_")[0];
								if (imageType
										.contains(CommonConstants.SHOP_IMAGE)) {
									logger.info("Saving shop image");
									this.saveShopImage(syncData, item);
								} else if (imageType
										.contains(CommonConstants.BWU_IMAGE)) {
									logger.info("Saving BWU image");
									this.saveBwuImage(syncData, bwuImages, item);
								} else if (imageType
										.contains(CommonConstants.PROGRAM_IMAGE)) {

									logger.info("Saving Program image");
									this.saveProgramImage(syncData, item);
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
					logger.error("Error while saving sync data agianst shop : "
							+ syncData.getSyncShop().getId(), ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					return null;
				}

				if (syncData != null) {
					saveData(syncData, bwuImages);
					logger.info("Sending sync response against shop id "
							+ syncData.getSyncShop().getId());
					responMap.put("syncResponse", CommonUtil.getResponseObj(
							syncData.getSyncShop().getId(), syncData
									.getSyncShop().getClientShopId(), -1));
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
		return responMap;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void saveData(SyncData syncData, ArrayList<BwuImage> bwuImages)
			throws Exception {

		logger.info("Saving data into db");
		checkingShopTime(syncData);
		// handling if startTime is missing from syncShop
		if (syncData.getSyncShop().getTime().getStartTime() == null) {
			syncData.getSyncShop()
					.getTime()
					.setStartTime(
							syncData.getSyncShop().getImage().getImageTime());
		}
		int shopId = shopsDao
				.getDuplicateShopId(
						syncData.getSyncShop().getClientShopId(),
						syncData.getSyncShop().getSurveyorId(),
						syncData.getSyncShop().getTime().getStartTime()
								.split("\\s+")[0]);
		// checking duplicate merchandiser shop
		if (shopId > 0) {
			logger.info("Entered into saveData Transaction Method. checking Duplicate Merchandiser Shop");
			syncData.getSyncShop().setId(shopId);

			// syncData.getSyncShop().setNewShop("N");
			return;

		}
		//
		if ("N".equalsIgnoreCase(syncData.getSyncShop().getNewShop())) {
			shopsDao.updateShop(syncData);
			this.removeDuplicateShop(syncData.getSyncShop().getId());

		}

		if (ShopConstants.NEW_SHOP.equalsIgnoreCase(syncData.getSyncShop()
				.getNewShop()) && syncData.getSyncShop().getClientShopId() == 0) {
			logger.info("new shop : " + syncData.getSyncShop().getNewShop()
					+ " and client ID : "
					+ syncData.getSyncShop().getClientShopId());
			// bug in client in which clients sends new shops with client shop
			// id 0
			return;
		}
		if (ShopConstants.NEW_SHOP.equalsIgnoreCase(syncData.getSyncShop()
				.getNewShop())
				&& !syncData.getSyncShop().getClientShopId()
						.equals(syncData.getSyncShop().getId())) {
			// bug in client in which clients sends new shops with different
			// client shop
			// id and shop id
			logger.info("New shop with error , shop id : "
					+ syncData.getSyncShop().getId() + ", client shop id : "
					+ syncData.getSyncShop().getClientShopId());
			syncData.getSyncShop().setClientShopId(0);
			return;
		}
		// checking shop time.
		// if shop time is more than maximum spent minute limit, it will be
		// replaced with limit.
		this.checkShopProductiveTime(syncData);
		this.saveShopData(syncData);
		if (bwuImages != null && bwuImages.size() > 0) {
			this.saveImagesData(syncData, bwuImages);
		} else if (syncData.getPposmData() != null
				&& syncData.getPposmData().size() > 0) {
			this.saveImagesData(syncData);
		}

		// this.syncVisit(syncData);

	}

	private void checkingShopTime(SyncData syncData) {
		if (syncData.getSyncShop().getTime() != null) {
			logger.error("checking start/end Time.");
			if (syncData.getSyncShop().getTime().getStartTime() == null) {
				logger.error("StartTime is missing.");
				syncData.getSyncShop()
						.getTime()
						.setStartTime(
								syncData.getSyncShop().getTime().getEndTime());

			}
			if (syncData.getSyncShop().getTime().getEndTime() == null) {
				logger.error("endTime is missing.");

			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void syncVisit(SyncData syncData,
			ArrayList<VisitImage> visitImages, ArrayList<MeBwuImage> meBwuImages)
			throws Exception {

		logger.info("Saving data into db");
		// checking if found existing survey then delete before dumping survey
		// data.
		syncData.getVisit().setClientShopId(syncData.getVisit().getShopId());
        if (shopsDao.isDuplicateMerchandiserSurveys(syncData.getVisit()
                .getShopId(), syncData.getSurveyorId(), syncData.getVisit()
                .getDateTime())) {
            logger.info("Duplicate survey found against shop id: "
                    + syncData.getVisit().getShopId());
			// shopsDao.deleteMerchandiserDuplicateSurvey(syncData.getVisit().getShopId(),
			// syncData.getSurveyorId(), syncData.getVisit().getDateTime());
  			return;
 		}

		// checking shop time.
		// if shop time is more than 20 minutes, it will be replaced with total
		// 20 minutes.
		this.checkShopProductiveTime(syncData);
		this.saveVisitSyncData(syncData, visitImages, meBwuImages);

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void saveSingleSurveyData(SingleSurvey singleSurvey)
			throws Exception {

		logger.info("Saving single survey data into db");
		ArrayList<DsrStockList> tempStockList = new ArrayList<DsrStockList>();
		if (singleSurvey.getSales() != null
				&& singleSurvey.getSales().size() > 0) {
			// ArrayList<StockTransferDetails> dsrSalesData = new
			// ArrayList<StockTransferDetails>();
			familiesDao.insertDsrSales(singleSurvey.getSurveyorId(),
					singleSurvey.getEmployeeId(), singleSurvey.getSales());
			// portalDao.updateTownStockAfterDsrSales(dsrSalesData);
		}
		if (singleSurvey.getWsSales() != null
				&& singleSurvey.getWsSales().size() > 0) {
			logger.info("Saving single survey whole sale sales into db");
			familiesDao.insertWholesaleSales(singleSurvey.getSurveyorId(),
					singleSurvey.getEmployeeId(), singleSurvey.getWsSales());
		}
		if (singleSurvey.getCheckInInfos().size() > 0) {

			for (CheckInInfo checkInInfo : singleSurvey.getCheckInInfos()) {
				checkInInfo
						.setId(surveyorDao.insertCheckIn(
								singleSurvey.getSurveyorId(),
								singleSurvey.getEmployeeId(), checkInInfo,
								singleSurvey.getBuildVersion(),
								singleSurvey.getImei()));
				if (checkInInfo.getDsrAttendanceList() != null
						&& checkInInfo.getDsrAttendanceList().size() > 0) {
					surveyorDao.insertDsrAttendance(
							singleSurvey.getSurveyorId(),
							singleSurvey.getEmployeeId(), checkInInfo,
							checkInInfo.getDsrAttendanceList());
				}
				
				if(checkInInfo.getStockList() != null && checkInInfo.getStockList().size() > 0){
					for (DsrStockList stockList : checkInInfo.getStockList()) {
						if (stockList.getStock() > 0) {
							tempStockList.add(stockList);
						}
					}
					if (tempStockList != null && tempStockList.size() > 0) {
						logger.info("DE Stock");
						dsrDao.insertDsrStockEntry(singleSurvey.getDsrId(),
								checkInInfo, tempStockList, singleSurvey.getDsrEmployeeId(),singleSurvey.getSurveyorId());
					}
				}
				
			}
		}

		if (singleSurvey.getBreaks() != null
				&& singleSurvey.getBreaks().size() > 0) {
			surveyorDao.insertSISBreakTime(singleSurvey.getSurveyorId(),
					singleSurvey.getEmployeeId(), singleSurvey.getBreaks());
		}
		/*
		 * if(singleSurvey.getActivity() != null) {
		 * surveyorDao.insertActivity(singleSurvey.getActivity()); }
		 */

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void saveSingleSurveysData(SingleSurvey singleSurvey)
			throws Exception {

		logger.info("Saving single survey data into db");

		if (singleSurvey.getSales() != null
				&& singleSurvey.getSales().size() > 0) {
			// ArrayList<StockTransferDetails> dsrSalesData = new
			// ArrayList<StockTransferDetails>();
			familiesDao.insertDsrSales(singleSurvey.getSurveyorId(),
					singleSurvey.getEmployeeId(), singleSurvey.getSales());
			// portalDao.updateTownStockAfterDsrSales(dsrSalesData);
		}
		if (singleSurvey.getWsSales() != null
				&& singleSurvey.getWsSales().size() > 0) {
			logger.info("Saving single survey whole sale sales into db");
			familiesDao.insertWholesaleSales(singleSurvey.getSurveyorId(),
					singleSurvey.getEmployeeId(), singleSurvey.getWsSales());
		}
		if (singleSurvey.getCheckInInfos().size() > 0) {

			for (CheckInInfo checkInInfo : singleSurvey.getCheckInInfos()) {
				checkInInfo
						.setId(surveyorDao.insertEndDay(
								singleSurvey.getSurveyorId(),
								singleSurvey.getEmployeeId(), checkInInfo,
								singleSurvey.getBuildVersion(),
								singleSurvey.getImei()));
				if (checkInInfo.getDsrAttendanceList() != null
						&& checkInInfo.getDsrAttendanceList().size() > 0) {
					surveyorDao.insertDsrAttendance(
							singleSurvey.getSurveyorId(),
							singleSurvey.getEmployeeId(), checkInInfo,
							checkInInfo.getDsrAttendanceList());
				}
			}
		}

		if (singleSurvey.getBreaks() != null
				&& singleSurvey.getBreaks().size() > 0) {
			surveyorDao.insertSISBreakTime(singleSurvey.getSurveyorId(),
					singleSurvey.getEmployeeId(), singleSurvey.getBreaks());
		}
		/*
		 * if(singleSurvey.getActivity() != null) {
		 * surveyorDao.insertActivity(singleSurvey.getActivity()); }
		 */

	}

	private void saveBwuImage(SyncData syncData, ArrayList<BwuImage> bwuImages,
			FileItemStream item) throws Exception {

		try {

			String temporaryPath = fileUtils.storeCensusFile(servletContext
					.getRealPath("/"), syncData.getSyncShop().getId(),
					CommonConstants.BWU, item);
			BwuImage bwuImage = new BwuImage();
			bwuImage.setSurveyorId(syncData.getSyncShop().getSurveyorId());
			bwuImage.setShopId(syncData.getSyncShop().getId());
			bwuImage.setMerchandiserShopId(syncData.getSyncShop()
					.getMerchandiserShopId());
			bwuImage.setIndustryId(Integer
					.parseInt(item.getName().split("_")[2]));
			bwuImage.setId(Integer.parseInt(item.getName().split("_")[3]));
			bwuImage.setImageUrl(temporaryPath);
			bwuImages.add(bwuImage);
		} catch (Exception ex) {
			logger.error(ex, ex);
			logger.error("Error while saving bwu image against shop : "
					+ syncData.getSyncShop().getId(), ex);
			throw ex;
		}
	}

	private void saveSisBwuImage(SyncData syncData,
			ArrayList<BwuImage> bwuImages, FileItemStream item)
			throws Exception {

		try {
			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.BWU, item);
			BwuImage bwuImage = new BwuImage();
			bwuImage.setSurveyorId(syncData.getSurveyorId());
			bwuImage.setShopId(syncData.getVisit().getShopId());
			bwuImage.setIndustryId(Integer
					.parseInt(item.getName().split("_")[2]));
			bwuImage.setId(Integer.parseInt(item.getName().split("_")[3]));
			bwuImage.setImageUrl(temporaryPath);
			bwuImages.add(bwuImage);
		} catch (Exception ex) {
			logger.error(ex, ex);
			logger.error("Error while saving bwu image against shop : "
					+ syncData.getSyncShop().getId(), ex);
			throw ex;
		}
	}

	private void saveMEBwuImage(SyncData syncData,
			ArrayList<MeBwuImage> meBwuImages, FileItemStream item)
			throws Exception {

		try {

			if (item.getFieldName().split("_")[5].equalsIgnoreCase("PRE")) {
				String temporaryPath = fileUtils.storeSurveyFile(servletContext
						.getRealPath("/"), syncData.getVisit().getShopId(),
						CommonConstants.BWU, item);
				MeBwuImage meBwu = new MeBwuImage();
				meBwu.setPreImageUrl(temporaryPath);
				meBwu.setRemarkId(Integer.parseInt(item.getFieldName().split(
						"_")[6]));
				meBwu.setBwuId(Integer
						.parseInt(item.getFieldName().split("_")[3]));
				meBwuImages.add(meBwu);

			} else if (item.getFieldName().split("_")[5]
					.equalsIgnoreCase("POST")) {
				String temporaryPath = fileUtils.storeSurveyFile(servletContext
						.getRealPath("/"), syncData.getVisit().getShopId(),
						CommonConstants.BWU, item);
				MeBwuImage meBwu = new MeBwuImage();
				meBwu.setPostImageUrl(temporaryPath);
				meBwu.setRemarkId(Integer.parseInt(item.getFieldName().split(
						"_")[6]));
				meBwu.setBwuId(Integer
						.parseInt(item.getFieldName().split("_")[3]));
				meBwuImages.add(meBwu);
			}

		} catch (Exception ex) {
			logger.error(ex, ex);
			logger.error("Error while saving bwu image against shop : "
					+ syncData.getSyncShop().getId(), ex);
			throw ex;
		}
	}

	private void saveMEQuestionImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {

			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.ME_COMPETITION_QUESTION_IMAGE, item);
			for (int i = 0; i < syncData.getVisit().getQuestionData().size(); i++) {
				if (syncData.getVisit().getQuestionData().get(i)
						.getQuestionId() == Integer.parseInt(item
						.getFieldName().split("_")[2])) {
					syncData.getVisit().getQuestionData().get(i).getImage()
							.setImageUrl(temporaryPath);
				}
			}

		} catch (Exception ex) {
			logger.error(ex, ex);
			logger.error("Error while saving ME Competitio Question image : "
					+ syncData.getSyncShop().getId(), ex);
			throw ex;
		}
	}

	private void saveShopImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {
			String temporaryPath = fileUtils.storeCensusFile(servletContext
					.getRealPath("/"), syncData.getSyncShop().getId(),
					CommonConstants.SHOP, item);
			syncData.getSyncShop().getImage().setImageUrl(temporaryPath);
		} catch (Exception ex) {
			logger.error(ex, ex);
			logger.error("Error while saving shop image against shop : "
					+ syncData.getSyncShop().getId(), ex);
			throw ex;
		}
	}

	private void saveSelfieImage(SingleSurvey singleSurvey, FileItemStream item)
			throws Exception {

		try {
			String imageDate = item.getName().split("_")[1];
			String imageDateFinal = imageDate.split("\\.")[0];
			String temporaryPath = fileUtils.storeSelfieFile(
					servletContext.getRealPath("/"),
					singleSurvey.getSurveyorId(), CommonConstants.SELFIE, item);
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

	private void saveMeDEMeetingImage(SingleSurvey singleSurvey,
			FileItemStream item) throws Exception {

		try {
			String temporaryPath = fileUtils.storeSelfieFile(
					servletContext.getRealPath("/"),
					singleSurvey.getSurveyorId(),
					CommonConstants.ME_DE_MEETING_IMAGE, item);

			for (CheckInInfo checkInInfo : singleSurvey.getCheckInInfos()) {

				if (checkInInfo.getDeMeeting().getRemarkId() == CommonConstants.DE_MEETING_IMAGE) {
					checkInInfo.getDeMeeting().getImage()
							.setImageUrl(temporaryPath);
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

	private void saveProgramImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {
			String temporaryPath = fileUtils.storeCensusFile(servletContext
					.getRealPath("/"), syncData.getSyncShop().getId(),
					CommonConstants.PROGRAM, item);
			Integer industryId = Integer.parseInt(item.getName().split("_")[1]);
			this.setIndustryProgramImagePath(syncData.getIndustryData(),
					industryId, temporaryPath);
		} catch (Exception ex) {
			logger.error(ex, ex);
			logger.error("Error while saving shop image against shop : "
					+ syncData.getSyncShop().getId(), ex);
			throw ex;
		}
	}

	private void saveBillImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {
			String temporaryPath = fileUtils.storeCensusFile(servletContext
					.getRealPath("/"), syncData.getSyncShop().getId(),
					CommonConstants.BILL, item);
			this.setBillImagePath(syncData, temporaryPath);
		} catch (Exception ex) {
			logger.error(ex, ex);
			logger.error("Error while saving shop image against shop : "
					+ syncData.getSyncShop().getId(), ex);
			throw ex;
		}
	}

	private void saveDisplayImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {
			Integer displayNo = -1;
			Integer categoryId = -1;

			String temporaryPath = fileUtils.storeCensusFile(servletContext
					.getRealPath("/"), syncData.getSyncShop().getId(),
					CommonConstants.DISPLAY, item);
			Integer displayId = Integer.parseInt(item.getName().split("_")[1]);
			if (displayId == 1) {

				displayNo = Integer.parseInt(item.getName().split("_")[2]);
			} else {

				categoryId = Integer.parseInt(item.getName().split("_")[2]);
			}

			this.setDisplayImagePath(syncData.getSyncProductDisplays(),
					categoryId, displayId, displayNo, temporaryPath);
		} catch (Exception ex) {
			logger.error(ex, ex);
			logger.error("Error while saving display image against shop : "
					+ syncData.getSyncShop().getId(), ex);
			throw ex;
		}
	}

	private void saveAssetImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {
			String temporaryPath = fileUtils.storeCensusFile(servletContext
					.getRealPath("/"), syncData.getSyncShop().getId(),
					CommonConstants.ASSET, item);
			Integer assetId = Integer.parseInt(item.getName().split("_")[2]);
			this.setAssetImagePath(syncData.getSyncAsset(), assetId,
					temporaryPath);
		} catch (Exception ex) {
			logger.error(ex, ex);
			logger.error("Error while saving shop image against shop : "
					+ syncData.getSyncShop().getId(), ex);
			throw ex;
		}
	}

	private void saveRetailAuditImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {

			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.RETAIL_AUDIT_IMAGE, item);
			String imageKey = item.getFieldName();

			this.setRetailAuditImagePath(syncData.getVisit().getBrandStock(),
					imageKey, temporaryPath);

		} catch (Exception ex) {

			logger.error(
					"Error while saving retail audit image in sis Visit Service : "
							+ syncData.getVisit().getShopId(), ex);
			throw ex;
		}
	}

	private void setRetailAuditImagePath(List<BrandStock> brandStockList,
			String imageKey, String imageUrl) {
		for (BrandStock brandStock : brandStockList) {
			if (brandStock.getImage() != null) {
				if (brandStock.getImage().getImageUrl() != null
						&& brandStock.getImage().getImageUrl()
								.equalsIgnoreCase(imageKey)) {
					brandStock.getImage().setImageUrl(imageUrl);
					break;
				}
			}
		}
	}

	private void saveRemarkImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {

			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.SIS_REMARK_IMAGE, item);
			String imageKey = item.getFieldName();

			this.setRemakImagePath(syncData.getVisit(), imageKey, temporaryPath);

		} catch (Exception ex) {

			logger.error(
					"Error while saving retail audit image in sis Visit Service : "
							+ syncData.getVisit().getShopId(), ex);
			throw ex;
		}
	}

	private void setRemakImagePath(Visit visit, String imageKey, String imageUrl) {
		if (visit.getRemarkImage() != null) {
			if (visit.getRemarkImage().getImageUrl() != null
					&& visit.getRemarkImage().getImageUrl()
							.equalsIgnoreCase(imageKey)) {
				visit.getRemarkImage().setImageUrl(imageUrl);
			}
		}
	}

	private void saveBrandImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {

			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.BRAND_IMAGE, item);
			String imageKey = item.getFieldName();

			this.setBrandImagePath(syncData.getVisit(), imageKey, temporaryPath);

		} catch (Exception ex) {

			logger.error(
					"Error while saving retail audit image in sis Visit Service : "
							+ syncData.getVisit().getShopId(), ex);
			throw ex;
		}
	}

	private void setBrandImagePath(Visit visit, String imageKey, String imageUrl) {
		if (visit.getFamilyData() != null) {
			for (SyncFamily syncFamily : visit.getFamilyData()) {
				if (syncFamily.getBrandImage() != null) {
					if (syncFamily.getBrandImage().getImageUrl() != null
							&& syncFamily.getBrandImage().getImageUrl()
									.equalsIgnoreCase(imageKey)) {
						syncFamily.getBrandImage().setImageUrl(imageUrl);
						break;
					}
				}
			}
		} else if (visit.getBrandStock() != null) {
			for (BrandStock brandStock : visit.getBrandStock()) {
				if (brandStock.getBrandImage() != null) {
					if (brandStock.getBrandImage().getImageUrl() != null
							&& brandStock.getBrandImage().getImageUrl()
									.equalsIgnoreCase(imageKey)) {
						brandStock.getBrandImage().setImageUrl(imageUrl);
						break;
					}
				}
			}
		}
	}

	private void saveInvoiceImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {

			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.INVOICE_IMAGE, item);
			String imageKey = item.getFieldName();

			if (Float.parseFloat(syncData.getBuildVersion().split("_")[0]) > 1.5) {
				this.setInvoiceImagesPath(syncData.getVisit()
						.getInvoiceImages(), imageKey, temporaryPath);
			} else {
				this.setInvoiceImagePath(syncData.getVisit().getBrandStock(),
						imageKey, temporaryPath);
			}

		} catch (Exception ex) {

			logger.error(
					"Error while saving retail audit image in sis Visit Service : "
							+ syncData.getVisit().getShopId(), ex);
			throw ex;
		}
	}

	private void setInvoiceImagePath(List<BrandStock> brandStockList,
			String imageKey, String imageUrl) {
		for (BrandStock brandStock : brandStockList) {
			if (brandStock.getInvoiceImage() != null) {
				if (brandStock.getInvoiceImage().getImageUrl() != null
						&& brandStock.getInvoiceImage().getImageUrl()
								.equalsIgnoreCase(imageKey)) {
					brandStock.getInvoiceImage().setImageUrl(imageUrl);
					break;
				}
			}
		}
	}

	private void setInvoiceImagesPath(List<InvoiceImage> invoiceImages,
			String imageKey, String imageUrl) {
		for (InvoiceImage invoiceImage : invoiceImages) {
			if (invoiceImage.getImage() != null) {
				if (invoiceImage.getImage().getImageUrl() != null
						&& invoiceImage.getImage().getImageUrl()
								.equalsIgnoreCase(imageKey)) {
					invoiceImage.getImage().setImageUrl(imageUrl);
					break;
				}
			}
		}
	}

	private void saveVisitImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {
			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.VISIT_SHOP_PICTURE, item);
			syncData.getVisit().getVisitImage().setImageUrl(temporaryPath);
		} catch (Exception ex) {

			logger.error(
					"Error while saving Visit shop image in Visit Service : "
							+ syncData.getVisit().getShopId(), ex);
			throw ex;
		}
	}

	// By Hamed
	private void saveShelfDisplayImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {
			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.SHELF_DISPLAY_PICTURE, item);
			syncData.getVisit().getShelfDisplay().setImageUrl(temporaryPath);
		} catch (Exception ex) {

			logger.error(
					"Error while saving Shelf Display Image in Visit Service : "
							+ syncData.getVisit().getShopId(), ex);
			throw ex;
		}
	}

	// By Hamed
	private void saveAudioFiles(SyncData syncData, FileItemStream item)
			throws Exception {
		try {
			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.AUDIO_TYPE, item);
			syncData.getVisit().getAudioFile().setUrl(temporaryPath);
		} catch (Exception ex) {

			logger.error("Error while saving Audio Files In Visit Service : "
					+ syncData.getVisit().getShopId(), ex);
			throw ex;
		}
	}

	// By Hamed
	private void saveMp3AudioFiles(SyncData syncData, FileItemStream item)
			throws Exception {
		try {
			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.MP3_AUDIO_FILE, item);
			syncData.getVisit().getAudioFile().setUrl(temporaryPath);
		} catch (Exception ex) {

			logger.error(
					"Error while saving MP3 Audio Files In Visit Service : "
							+ syncData.getVisit().getShopId(), ex);
			throw ex;
		}
	}

	// By Hamed
	private void saveDamageRepairedPPOSMImage(SyncData syncData,
			FileItemStream item) throws Exception {
		try {
			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.DAMAGED_REPAIRED_PPOSM_PICTURE, item);
			syncData.getVisit().getPposmDisplay().getImage()
					.setImageUrl(temporaryPath);
		} catch (Exception ex) {

			logger.error(
					"Error while saving PPOSM Damaged Repaired Image In Visit Service : "
							+ syncData.getVisit().getShopId(), ex);
			throw ex;
		}
	}

	// By Hamed
	private void saveTPOSMImage(SyncData syncData, FileItemStream item)
			throws Exception {
		try {
			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.TPOSM_PICTURE, item);
			syncData.getVisit().getTposmImage().setImageUrl(temporaryPath);
		} catch (Exception ex) {

			logger.error("Error while saving TPOSM Image In Visit Service : "
					+ syncData.getVisit().getShopId(), ex);
			throw ex;
		}
	}

	// By Hamed
	private void saveVisitImages(SyncData syncData,
			ArrayList<VisitImage> visitImages, FileItemStream item, int i)
			throws Exception {

		try {

			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.VISIT_IMAGE, item);
			VisitImage visitImage = new VisitImage();
			VisitImage temp_visiImage = syncData.getVisit()
					.getVisitImageArrayList().get(i);
			visitImage.setImageUrl(temporaryPath);
			visitImage.setRemarks(temp_visiImage.getRemarks());
			visitImages.add(visitImage);

		} catch (Exception ex) {

			logger.error("Error while saving Visit image of Activity : "
					+ syncData.getSyncShop().getId(), ex);
			throw ex;
		}
	}

	private void setIndustryProgramImagePath(
			ArrayList<SyncIndustry> syncIndustryList, Integer industryId,
			String imageUrl) {

		for (SyncIndustry syncIndustry : syncIndustryList) {

			if (syncIndustry.getId() == industryId) {

				syncIndustry.getImage().setImageUrl(imageUrl);
				break;
			}
		}

	}

	private void setBillImagePath(SyncData syncData, String imageUrl) {

		Image image = new Image();
		image.setImageUrl(imageUrl);
		syncData.getBillData().add(image);

	}

	private void setDisplayImagePath(
			ArrayList<SyncProductDisplay> SyncProductDisplays,
			Integer categoryId, Integer displayId, Integer displayNo,
			String imageUrl) {

		for (SyncProductDisplay syncProductDisplay : SyncProductDisplays) {

			if ((syncProductDisplay.getCategoryId() == categoryId && syncProductDisplay
					.getDisplayId() == 2)
					|| (syncProductDisplay.getDisplayId() == displayId && syncProductDisplay
							.getDisplayNo() == displayNo)) {

				Image image = new Image();
				image.setImageUrl(imageUrl);
				syncProductDisplay.setImage(image);
				break;
			}
		}

	}

	private void setAssetImagePath(ArrayList<SyncAsset> assetData,
			Integer assetId, String imageUrl) {

		for (SyncAsset syncAsset : assetData) {

			if (syncAsset.getId() == assetId) {

				for (Image image : syncAsset.getImages()) {

					if (!image.getImageUrl().contains("censusImages")) {

						image.setImageUrl(imageUrl);
						break;
					}
				}
			}
		}

	}

	private void removeDuplicateShop(int shopId) throws Exception {

		logger.info("Saving duplicate shop against shop id : " + shopId);
		shopsDao.saveDuplicateShop(shopId);
		// shopsDao.deleteDuplicateShop(clientShopId, surveyorId,
		// shopAddedDate);
		shopsDao.deleteMerchandiserDuplicateShop(shopId);

	}

	private void saveShopData(SyncData syncData) throws Exception {
		logger.info("Start saveShopdata method.");
	//	logger.info("Shop ." + syncData);
		if (ShopConstants.NEW_SHOP.equalsIgnoreCase(syncData.getSyncShop()
				.getNewShop())
				&& !SurveyorType.ASM.toString().equals(
						syncData.getSyncShop().getSurveyorType())) {
			logger.info("Adding new shop");
			if (syncData.getSyncShop().getType() != null
					&& syncData.getSyncShop().getType()
							.equalsIgnoreCase("RETAIL_AUDIT")) {
				syncData.getSyncShop().setCityId(
						shopsDao.getCityIdForRA(syncData.getSyncShop()
								.getRegionId()));
			}
			syncData.getSyncShop().setId(
					shopsDao.insertShop(syncData.getSyncShop()));
		}

		if (syncData.getSyncShop().getType() != null
				&& (syncData.getSyncShop().getType().equalsIgnoreCase("SIS") || syncData
						.getSyncShop().getType()
						.equalsIgnoreCase("RETAIL_AUDIT"))) {

		} else {
			logger.info("Saving shop data in Merchandiser Shop against shop id "
					+ syncData.getSyncShop().getId());

			syncData.getSyncShop().setMerchandiserShopId(
					shopsDao.insertMerchandiserShop(syncData.getBuildVersion(),
							syncData.getImei(), syncData.getSyncShop()));
		}

		// Updating validation status
		shopsDao.updateShopStatus(syncData.getSyncShop().getId(), syncData
				.getSyncShop().getRemarkId(), syncData.getAsmVerification());

		logger.info("IndustryData size :: " + syncData.getIndustryData());
		if (syncData.getIndustryData() != null
				&& syncData.getIndustryData().size() > 0) {

			logger.info("Saving industry data against shop id "
					+ syncData.getSyncShop().getId());
			industriesDao.insertIndustryVolumes(syncData.getSyncShop(),
					syncData.getIndustryData());
		}
		// saving asset data will be used only in mondelez census
		logger.info("SyncAsset size :: " + syncData.getSyncAsset());
		if (syncData.getSyncAsset() != null
				&& syncData.getSyncAsset().size() > 0) {

			logger.info("Saving asset against shop id "
					+ syncData.getSyncShop().getId());
			for (SyncAsset syncAsset : syncData.getSyncAsset()) {

				syncAsset.setMerchandiserAssetId(assetsDao.insertAssetData(
						syncData.getSyncShop(), syncAsset));
				assetsDao.insertAssetImages(syncData.getSyncShop(),
						syncAsset.getMerchandiserAssetId(),
						syncAsset.getAssetRemarksList(), syncAsset.getImages());

			}
		}
		// saving posm data will be used only in mondelez census
		logger.info("SyncPosms size " + syncData.getSyncPosms());
		if (syncData.getSyncPosms() != null
				&& syncData.getSyncPosms().size() > 0) {

			logger.info("Saving POSM against shop id "
					+ syncData.getSyncShop().getId());
			bwusDao.insertPosm(syncData.getSyncShop(), syncData.getSyncPosms());
		}
		// saving categories data will be used only in mondelez census
		if (syncData.getSyncCategories() != null
				&& syncData.getSyncCategories().size() > 0) {

			logger.info("Saving product categoreis against shop id "
					+ syncData.getSyncShop().getId());
			productsDao.insertProductCategories(syncData.getSyncShop(),
					syncData.getSyncCategories());
		}
		// saving display audit data will be used only in mondelez census
		if (syncData.getSyncProductDisplays() != null
				&& syncData.getSyncProductDisplays().size() > 0) {

			logger.info("Saving product display data against shop id "
					+ syncData.getSyncShop().getId());
			for (SyncProductDisplay syncProductDisplay : syncData
					.getSyncProductDisplays()) {

				syncProductDisplay.setId(productsDao.insertProductDisplays(
						syncData.getSyncShop(), syncProductDisplay));
				productsDao.insertProductDisplayImages(syncData.getSyncShop(),
						syncProductDisplay.getId(),
						syncProductDisplay.getProductDisplayFacing());
			}
		}
		// saving bill data will be used only in mondelez census
		if (syncData.getBillData() != null && syncData.getBillData().size() > 0) {

			logger.info("Saving bill data against shop id "
					+ syncData.getSyncShop().getId());
			shopsDao.insertBillData(syncData.getSyncShop(),
					syncData.getBillData());
		}

		if (syncData.getFamilyData() != null
				&& syncData.getFamilyData().size() > 0) {

			logger.info("Saving family data against shop id "
					+ syncData.getSyncShop().getId());
			familiesDao.insertFamilyVolumes(syncData.getSyncShop(),
					syncData.getFamilyData());
		}
		// if (syncData.getSisFamilyData() != null &&
		// syncData.getSisFamilyData().size() > 0)
		// {
		// logger.info("Saving sis family data against shop id " +
		// syncData.getSyncShop().getId());
		// familiesDao.insertSisFamilyVolumes(syncData.getSyncShop(),
		// syncData.getSisFamilyData());
		// }
		// Same method will be used in PMI and Mondelez census
		logger.info("ProductData Data size " + syncData.getProductData());
		if (syncData.getProductData() != null
				&& syncData.getProductData().size() > 0) {

			logger.info("Saving product data against shop id "
					+ syncData.getSyncShop().getId());
			productsDao.insertProductVolumes(syncData.getSyncShop(),
					syncData.getProductData());
		}
		if (syncData.getSyncShop().getType() != null
				&& (syncData.getSyncShop().getType().equalsIgnoreCase("SIS") || syncData
						.getSyncShop().getType()
						.equalsIgnoreCase("RETAIL_AUDIT"))) {

		} else {
			logger.info("Saving questions data against shop id "
					+ syncData.getSyncShop().getId());
			questionsDao.insertMerchandiserQuestions(syncData);
		}
	}

	// BY Hamed
	private void saveVisitSyncData(SyncData syncData,
			ArrayList<VisitImage> visitImages, ArrayList<MeBwuImage> meBwuImages)
			throws Exception {

		// A bug in client in which client shop id is sent instead of server
		// shop id.Due to this shop id is picked from merchandiser shops against
		// client shop id
		Integer shopId = shopsDao.getShopIdByClientShopId(syncData.getVisit()
				.getShopId(), syncData.getSurveyorId());
		if (shopId > 0) {
			logger.error("Bug on Client,client id  "
					+ syncData.getVisit().getShopId()
					+ "is sent instead of server id : " + shopId);
			syncData.getVisit().setShopId(shopId);
		}
//		if (syncData != null && syncData.getVisit() != null && syncData.getVisit().getVisitType() != null 
//				&& !syncData.getVisit().getVisitType().isEmpty() && syncData.getVisit().getVisitType().equals("INTERCEPTION")) {
//			
//			if(syncData.getVisit().getInterceptionSurvey() != null
//					&& syncData.getVisit().getInterceptionSurvey().size() > 0){
//				
//				for(InterceptionSurvey item: syncData.getVisit().getInterceptionSurvey()){
//					Integer interceptionId = shopsDao.insertInterceptionSurvey(item,syncData.getSurveyorId(),syncData.getEmployeeId());
//					
//					shopsDao.InterceptionQuestionDetails(item.getInterceptionDetailList(), interceptionId);
//					
//				}
//			}
//		}
			
			Integer surveyId = shopsDao.insertVisitSyncData(syncData);
			
			if (syncData != null && syncData.getVisit() != null && syncData.getVisit().getVisitType() != null 
					&& !syncData.getVisit().getVisitType().isEmpty() && syncData.getVisit().getVisitType().equals("INTERCEPTION")) {
				
				if(syncData.getVisit().getInterceptionSurvey() != null
						&& syncData.getVisit().getInterceptionSurvey().size() > 0){
					
					for(InterceptionSurvey item: syncData.getVisit().getInterceptionSurvey()){
						Integer interceptionId = shopsDao.insertInterceptionSurvey(item,syncData.getSurveyorId(),syncData.getEmployeeId());
						
						shopsDao.InterceptionQuestionDetails(item.getInterceptionDetailList(), interceptionId);
						
					}
				}
			}
			if (syncData.getVisit().getBrandAvailability() != null
					&& syncData.getVisit().getBrandAvailability().size() > 0) {
				shopsDao.insertBrandAvailability(syncData, surveyId);
			}
			if (syncData.getVisit().getPposmDisplay() != null) {
				shopsDao.insertVisitSyncPposmDisplay(syncData, surveyId);

				if (syncData.getVisit().getPposmDisplay().getRemarkId() == 106
						|| syncData.getVisit().getPposmDisplay().getRemarkId() == 108) {
					this.shopsDao
							.updateAbnormlShop(syncData.getVisit().getShopId()); 
				}
			}
			if (syncData.getVisit().getQuestionData() != null
					&& syncData.getVisit().getQuestionData().size() > 0) {
				shopsDao.insertSyncQuestionData(syncData, surveyId);
			}
			
			try {
				if (syncData.getVisit().getDropSize() != null
						&& syncData.getVisit().getDropSize().getBrandList() != null) {
					if (syncData.getVisit().getDropSize().getBrandList().size() > 0) {
						shopsDao.insertDropSizeBrandListData(syncData, surveyId);
						shopsDao.updateSpotSellingRates(surveyId);
					}
				}
			} catch (Exception e) {
				logger.error(e, e);
			}
			// temporary else if check added for newer builds shifting image saving
			// to wasabi
			if (visitImages != null && visitImages.size() > 0) {
				shopsDao.insertVisitImagesData(visitImages, surveyId);
			} else if (syncData.getVisit().getVisitImageArrayList() != null
					&& syncData.getVisit().getVisitImageArrayList().size() > 0) {
				shopsDao.insertVisitImagesData(syncData.getVisit()
						.getVisitImageArrayList(), surveyId);
			}

			if (syncData.getVisit().getPposmAvailabilityRemarkId() > 0) {
				shopsDao.insertMePposmRemark(surveyId, syncData.getVisit()
						.getPposmAvailabilityRemarkId());

			}
			if (syncData.getVisit().getPposmAvailabilityRemarkId() == CommonConstants.ME_PPOSM_IMAGES) {
				shopsDao.insertMePposmImages(meBwuImages, surveyId);
			}
			if (syncData.getVisit().getFamilyData() != null
					&& syncData.getVisit().getFamilyData().size() > 0) {

				dsrDao.insertDsrShopFamilyData(syncData, surveyId);
			}
			if (syncData.getVisit().getVisitImageArrayList() != null
					&& syncData.getVisit().getVisitImageArrayList().size() > 0) {

				dsrDao.insertDEVisitImagesData(syncData.getVisit()
						.getVisitImageArrayList(), surveyId);
			}
			
		
		
		
		
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void syncEndActivity(Activity activity) throws Exception {

		if (activity.getActivityFeedBack().getDeDsrWorkings().size() > 0) {
			surveyorDao.insertDeDsrWorkings(activity);
		}
		if (activity.getActivityFeedBack().getBrandTrends().size() > 0) {
			surveyorDao.insertBrandTrends(activity);
		}
		if (activity.getActivityFeedBack().getMarketShares().size() > 0) {
			surveyorDao.insertMarketShares(activity);
		}
		if (activity.getActivityFeedBack().getQuestions().size() > 0) {
			surveyorDao.insertActivityQuestions(activity);
		}
		if (activity.getTime() != null) {
			surveyorDao.endActivity(activity);
		}

	}

	private void saveImagesData(SyncData syncData, ArrayList<BwuImage> bwuImages) {

		if (syncData.getPposmData() != null) {

			logger.info("Saving PPOSM data against shop id "
					+ syncData.getSyncShop().getId());
			for (PposmData pposmData : syncData.getPposmData()) {

				bwusDao.insertPposm(syncData.getSyncShop(), pposmData);
			}
			// ArrayList<PposmData> pposmList = syncData.getPposmData();
			bwusDao.insertBwuImages(syncData.getSyncShop(), bwuImages);
		}
	}

	private void saveImagesData(SyncData syncData) {

		if (syncData.getPposmData() != null) {

			logger.info("Saving PPOSM data against shop id "
					+ syncData.getSyncShop().getId());
			for (PposmData pposmData : syncData.getPposmData()) {

				bwusDao.insertPposm(syncData.getSyncShop(), pposmData);
			}
			// ArrayList<PposmData> pposmList = syncData.getPposmData();
			bwusDao.insertBwuImages(syncData);
		}
	}

	private void checkShopProductiveTime(SyncData syncData) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				DateTimeConstants.DATE_TIME_FORMAT);
		try {
			Date firstParsedDate;
			if (syncData.getSyncShop() != null) {
				Time time = syncData.getSyncShop().getTime();
				firstParsedDate = dateFormat.parse(time.getStartTime());
				Date secondParsedDate = dateFormat.parse(time.getEndTime());
				long milliSecDiff = secondParsedDate.getTime()
						- firstParsedDate.getTime();
				long secondDiff = milliSecDiff / 1000;
				long minutesDiff = secondDiff / 60;

				if (minutesDiff > Integer.parseInt(bundle
						.getString(CommonConstants.MAXIMUM_SHOP_SPENT_TIME))
						|| minutesDiff < CommonConstants.CHECK_NEGATIVE) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(firstParsedDate);
					cal.add(Calendar.MINUTE,
							Integer.parseInt(bundle
									.getString(CommonConstants.MAXIMUM_SHOP_SPENT_TIME)));
					syncData.getSyncShop().getTime()
							.setEndTime(dateFormat.format(cal.getTime()));
				}
			} else if (syncData.getVisit() != null) {

				Time time = syncData.getVisit().getTime();
				firstParsedDate = dateFormat.parse(time.getStartTime());
				Date secondParsedDate = dateFormat.parse(time.getEndTime());
				long milliSecDiff = secondParsedDate.getTime()
						- firstParsedDate.getTime();
				long secondDiff = milliSecDiff / 1000;
				long minutesDiff = secondDiff / 60;

				if (minutesDiff > Integer.parseInt(bundle
						.getString(CommonConstants.MAXIMUM_SHOP_SPENT_TIME))
						|| minutesDiff < CommonConstants.CHECK_NEGATIVE) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(firstParsedDate);
					cal.add(Calendar.MINUTE,
							Integer.parseInt(bundle
									.getString(CommonConstants.MAXIMUM_SHOP_SPENT_TIME)));
					syncData.getVisit().getTime()
							.setEndTime(dateFormat.format(cal.getTime()));
				}
			}

		} catch (Exception e) {
			logger.error(e, e);
			logger.error(
					"Exception occured while Shop spent time difference checking...."
							+ e, e);
		}

	}

	@Override
	public Map<String, Object> syncSisVisit(String version,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responMap = new HashMap<String, Object>();
		String imei = request.getHeader(SysConstants.IMEI);
			
		if (validationServiceImpl.isValidImei(imei, response)) {
			try {

				ArrayList<BwuImage> bwuImages = new ArrayList<BwuImage>();
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
												.getHeader(SysConstants.EMPLOYEE_ID));
								logger.info("\nvalidated Shop : " + reader);
								syncData = gson
										.fromJson(reader, SyncData.class);
								if (syncData.getVisit().getDsrId() > -1) {
									Dsr dsr = applicationCacheService
											.getDsrById(syncData.getVisit()
													.getDsrId());
									syncData.setDsrEmployeeId(dsr == null ? -1
											: dsr.getEmployeeId());
								}
								syncData.setBuildVersion(request
										.getHeader(SysConstants.VERSION));
								syncData.setImei(imei);
								syncData.setEmployeeId(StringUtils.parseInt(
										request.getHeader(SysConstants.EMPLOYEE_ID),
										0));

								syncLogging
										.saveLog(
												syncData.getSurveyorId(),
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
									logger.info("Image Type ::   " + imageType);
									if (imageType
											.equalsIgnoreCase(CommonConstants.VISIT_SHOP_PICTURE)) {
										logger.info("Saving visit shop image");
										this.saveVisitImage(syncData, item);
									}

									else if (imageType
											.equalsIgnoreCase(CommonConstants.TPOSM_PICTURE)
											&& syncData.getVisit()
													.getIsTposmAvailable()
													.equalsIgnoreCase("Y")) {
										logger.info("Saving TPOSM image.");
										this.saveTPOSMImage(syncData, item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.VISIT_IMAGE)) {
										logger.info("Saving Visit image.");
										this.saveVisitImages(syncData,
												visitImages, item, i);
										i++;
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.BWU_IMAGE)) {
										logger.info("Saving BWU image");
										this.saveSisBwuImage(syncData,
												bwuImages, item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.RETAIL_AUDIT_IMAGE)) {
										logger.info("Saving Retail Audit image");
										this.saveRetailAuditImage(syncData,
												item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.SIS_REMARK_IMAGE)) {
										logger.info("Saving Remark image");
										this.saveRemarkImage(syncData, item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.BRAND_IMAGE)) {
										logger.info("Saving Brand image");
										this.saveBrandImage(syncData, item);
									} else if (imageType
											.equalsIgnoreCase(CommonConstants.INVOICE_IMAGE)) {
										logger.info("Saving Invoice image");
										this.saveInvoiceImage(syncData, item);
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
					syncSisVisit(syncData, visitImages, bwuImages,StringUtils.extractVersion(request
							.getHeader(SysConstants.VERSION)));
					
						logger.info("Sending Shop id in response "
								+ " shop id : "
								+ syncData.getVisit().getShopId());
						responMap.put("syncResponse", CommonUtil
								.getResponseObj(
										syncData.getVisit().getShopId(),
										syncData.getVisit().getId(), -1));
						// return responMap;
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
		}
		else {
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR_IMEI_PERMISSION, response);
			return null;
		}
		return responMap;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void syncSisVisit(SyncData syncData,
			ArrayList<VisitImage> visitImages, ArrayList<BwuImage> bwuImages, String version)
			throws Exception {

		logger.info("Saving data into db");
		// syncData.getVisit().setClientShopId(syncData.getVisit().getId());
		if (shopsDao.isDuplicateSISMerchandiserAbnormalSurveys(syncData
				.getVisit().getShopId(), syncData.getSurveyorId(), syncData
				.getVisit().getDateTime())) {
			logger.info("Deleting abnormal survey of shop id: "
					+ syncData.getVisit().getShopId());

			this.removeDuplicateAbnormalSurvey(syncData);
		} else if (shopsDao.isDuplicateSISMerchandiserSurveys(syncData
				.getVisit().getShopId(), syncData.getSurveyorId(), syncData
				.getVisit().getDateTime())) {
			logger.info("Duplicate SIS survey found against shop id: "
					+ syncData.getVisit().getShopId());
			// shopsDao.deleteMerchandiserDuplicateSurvey(syncData.getVisit().getShopId(),
			// syncData.getSurveyorId(), syncData.getVisit().getDateTime());
			return;

		}
		this.saveSisVisitSyncData(syncData, visitImages, bwuImages, version);

	}

	private void saveSisVisitSyncData(SyncData syncData,
			ArrayList<VisitImage> visitImages, ArrayList<BwuImage> bwuImages, String version)
			throws Exception {
		logger.info("SIS RA Version check "+version);
		
        Integer surveyId = shopsDao.insertVisitSyncData(syncData);
		 

		// adding visit number for retail visit
		if (shopsDao.checkFirstRetailVisit(syncData.getVisit().getShopId())
				&& syncData.getVisit().getVisitType()
						.equalsIgnoreCase(CommonConstants.RETAIL_AUDIT)) {
			logger.debug("Adding visit number for retail visit");
			shopsDao.updateRetailVisitNumber(syncData.getVisit().getShopId(),
					surveyId);

		}
		if (syncData.getVisit().getShopTitle() != null) {
			// saving shop basic information in sis ra shop data table
			shopsDao.insertSisShopUpdateData(syncData, surveyId);
		}

		if (syncData.getVisit().getFamilyData() != null
				&& syncData.getVisit().getFamilyData().size() > 0) {
			familiesDao.insertSisFamilyVolumes(syncData, syncData.getVisit()
					.getFamilyData(), surveyId,version);
		}

		if (syncData.getVisit().getPposmData() != null
				&& syncData.getVisit().getPposmData().size() > 0) {
			savePpsomTposmImageData(syncData, bwuImages, surveyId);
		}

		if (syncData.getVisit().getQuestionData() != null
				&& syncData.getVisit().getQuestionData().size() > 0) {
			shopsDao.insertSyncQuestionData(syncData, surveyId);
		}

		if (syncData.getVisit().getBrandStock() != null
				&& syncData.getVisit().getBrandStock().size() > 0) {
			familiesDao.insertSyncBrandStockData(syncData, surveyId,version);
		}

		if (syncData.getVisit().getSchemeData() != null
				&& syncData.getVisit().getSchemeData().size() > 0) {
			familiesDao.insertSisSchemes(syncData, surveyId);
		}

//		if (visitImages.size() > 0) {
//			shopsDao.insertVisitImagesData(visitImages, surveyId);
//		}
//
 		if (syncData.getVisit().getInvoiceImages() != null
 				&& syncData.getVisit().getInvoiceImages().size() > 0) {
 			industriesDao.insertInvoiceImages(syncData.getVisit()
 					.getInvoiceImages(), surveyId);
  		}

		if (syncData.getVisit().getIsSegmentUpdated() != null
				&& syncData.getVisit().getIsSegmentUpdated().contentEquals("Y")) {
			shopsDao.updateShopSegment(syncData.getVisit().getShopId(),
					syncData.getVisit().getSegment());
		}

	}

	private void savePpsomTposmImageData(SyncData syncData,
			ArrayList<BwuImage> bwuImages, Integer surveyId) {

		logger.info("Saving PPOSM data against shop id "
				+ syncData.getVisit().getShopId());
		for (PposmData pposmData : syncData.getVisit().getPposmData()) {

			bwusDao.insertPpsomTposm(syncData, pposmData, surveyId);
		}
		if (bwuImages != null && bwuImages.size() > 0) {
			bwusDao.insertSisBwuImages(syncData, bwuImages, surveyId);
		} else {
			bwusDao.insertBwuSisImages(syncData, surveyId);
		}

	}

	@Override
	public Map<String, Object> surveyJsonUploader(HttpServletRequest request,
			HttpServletResponse response) {

		File folderPath = new File(
				"D:/Workspace/Java/ndn/ndn/census/WebRoot/survey_logs/20110101/");
		File[] listOfFolders = folderPath.listFiles();
		try {

			for (File folder : listOfFolders) {
				logger.debug("Folder :: " + folder);
				File[] listOfFiles = new File(folder.toString()).listFiles();
				for (File file : listOfFiles) {
					Gson gson = new Gson();
					logger.debug("File :: " + file);
					Integer shopId = StringUtils.parseInt(
							file.getName().split("_")[0], 0);
					String data = FileUtils.readFileString(file);
					if (shopId == -1) {

						SingleSurvey singleSurvey = gson.fromJson(data,
								SingleSurvey.class);
						logger.info("Saving Checkin data from json.....");
						if (singleSurvey != null) {
							this.saveSingleSurveyData(singleSurvey);
						}
					} else {
						SyncData syncData = gson.fromJson(data, SyncData.class);
						syncData.setBuildVersion("1.9_1.0");
						// syncData.setImei(null);
						if (syncData.getVisit() != null) {
							syncVisit(syncData, null, null);
						} else {
							saveData(syncData, null);
						}
					}

				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		return null;
	}

	@Override
	public Map<String, Object> syncWsSurvey(String version,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responMap = new HashMap<String, Object>();
		String imei = request.getHeader("imei");
		if (validationServiceImpl.isValidImei(imei, response)) {
			try {

				boolean isMultiPart = ServletFileUpload
						.isMultipartContent(request);
				Gson gson = new Gson();
				WsSurvey wsSurvey = null;
				if (isMultiPart) {
					ServletFileUpload upload = new ServletFileUpload();
					try {

						FileItemIterator itr = upload.getItemIterator(request);
						while (itr.hasNext()) {
							FileItemStream item = itr.next();
							if (item.isFormField()) {
								InputStream stream = item.openStream();
								String reader = Streams.asString(stream);
								String surveyorId = request
										.getHeader("surveyorid");
								String employeeId = request
										.getHeader("employeeid");
								logger.info("Build Version "
										+ request.getHeader("version")
										+ "\n validated Shop : " + reader);
								wsSurvey = gson
										.fromJson(reader, WsSurvey.class);
								wsSurvey.setBuildVersion(request.getHeader(
										"version").split("_")[0]);
								wsSurvey.setEmployeeId(StringUtils.parseInt(
										employeeId, -1));
								wsSurvey.setSurveyorId(StringUtils.parseInt(
										surveyorId, -1));
								// singleSurvey.setImei(request.getHeader("imei"));
								syncLogging
										.saveLog(
												wsSurvey.getSurveyorId(),
												wsSurvey.getShopId(),
												DateTimeUtilities
														.getCurrentDate(DateTimeConstants.DATE_FORMAT3),
												reader,
												SysConstants.WS_SURVEY_LOG);
							} else {
								try {
									if (wsSurvey == null) {

										CommonUtil
												.writeErrorMessage(
														HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
														MessageType.ERROR_MULTIPART_ORDER,
														response);
										return null;
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
										+ wsSurvey.getSurveyorId(), ex);
						CommonUtil.writeErrorMessage(
								HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
								MessageType.ERROR_SERVER, response);
						return null;
					}

					if (wsSurvey != null) {
						syncWsSurvey(wsSurvey);
						logger.info("Sending ws survey Shop id in response "
								+ " shop id : " + wsSurvey.getShopId());
						responMap.put("syncResponse", CommonUtil
								.getResponseObj(wsSurvey.getShopId(),
										wsSurvey.getId(), -1));
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
	private void syncWsSurvey(WsSurvey wsSurvey) throws Exception {

		Integer surveyId = surveyorDao.checkDuplicateWsSurvey(wsSurvey);
		if (surveyId < 0) {
			surveyId = surveyorDao.insertWsSurvey(wsSurvey);
		} else {
			surveyorDao.updateWsSurvey(wsSurvey);
			// Removing current sale
			familiesDao.removeWsSales(surveyId, wsSurvey);
		}

		if (wsSurvey.getWsSales() != null && wsSurvey.getWsSales().size() > 0) {
			logger.info("Saving single survey whole sale sales into db");
			familiesDao.insertWsSales(surveyId, wsSurvey);
		}

	}

	private void saveQuestionImage(SyncData syncData, FileItemStream item)
			throws Exception {

		try {
			String temporaryPath = fileUtils.storeSurveyFile(servletContext
					.getRealPath("/"), syncData.getVisit().getShopId(),
					CommonConstants.QUESTION_IMAGE_TYPE, item);
			Integer visitId = Integer.parseInt(item.getName().split("_")[1]);
			Integer questionId = Integer
					.parseInt(item.getName().split("[_.]+")[2]);
			this.setQuestionImagePath(syncData.getVisit().getQuestionData(),
					visitId, questionId, temporaryPath);
		} catch (Exception ex) {
			logger.error(ex, ex);
			logger.error("Error while saving shop image against shop : "
					+ syncData.getSyncShop().getId(), ex);
			throw ex;
		}
	}

	private void setQuestionImagePath(ArrayList<SyncQuestion> questionData,
			Integer visitId, Integer questionId, String imageUrl) {

		for (SyncQuestion syncQuestion : questionData) {

			if (syncQuestion.getQuestionId() == questionId) {
				syncQuestion.getQuestionImage().setImageUrl(imageUrl);
				break;
			}
		}

	}

	@Override
	public Map<String, Object> updateSisRaData(HttpServletRequest request,
			HttpServletResponse response)

	{
		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			ArrayList<BwuImage> bwuImages = new ArrayList<BwuImage>();
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			SyncData syncData = null;
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
									+ "\n validated Shop : " + reader);
							syncData = gson.fromJson(reader, SyncData.class);
							syncData.setBuildVersion(request
									.getHeader("version"));
							syncLogging
									.saveLog(
											syncData.getSyncShop()
													.getSurveyorId(),
											syncData.getSyncShop().getId(),
											DateTimeUtilities
													.getCurrentDate(DateTimeConstants.DATE_FORMAT3),
											reader);
						} else {

						}
					}
				} catch (Exception ex) {
					logger.error(ex, ex);
					logger.error("Error while saving sync data agianst shop : "
							+ syncData.getSyncShop().getId());
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					return null;
				}
				if (syncData != null) {
					saveData(syncData);
					logger.info("Sending sync response against shop id "
							+ syncData.getSyncShop().getId()
							+ ", client shop id : "
							+ syncData.getSyncShop().getClientShopId());
					responMap.put("syncResponse", CommonUtil.getResponseObj(
							syncData.getSyncShop().getId(), syncData
									.getSyncShop().getClientShopId(), -1));
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
		return responMap;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void saveData(SyncData syncData) throws Exception {
		shopsDao.updateSisShop(syncData.getSyncShop());
	}

	private void removeDuplicateAbnormalSurvey(SyncData syncData)
			throws Exception {

		// moving abnormal visited shop to duplicate table

		shopsDao.saveDuplicateSurvey(syncData.getVisit().getShopId(),
				syncData.getSurveyorId(), syncData.getVisit().getDateTime());

		// deleting abnormal shop visited on same day to avoid duplication
		shopsDao.deleteMerchandiserDuplicateSurvey(syncData.getVisit()
				.getShopId(), syncData.getSurveyorId(), syncData.getVisit()
				.getDateTime());

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
	
//	private void saveVisitImage(SyncData syncData, FileItemStream item)
//			throws Exception {
//
//		try {
//			String temporaryPath = fileUtils.storeSurveyFileForDsr(
//					servletContext.getRealPath("/"), syncData.getVisit()
//							.getShopId(), CommonConstants.VISIT_SHOP_PICTURE,
//					item);
//			logger.info("temporaryPath : " + temporaryPath);
//			syncData.getVisit().getVisitImage().setImageUrl(temporaryPath);
//		} catch (Exception ex) {
//
//			logger.error(
//					"Error while saving Visit shop image in Visit Service : "
//							+ syncData.getVisit().getShopId(), ex);
//			throw ex;
//		}
//	}
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
syncSisVisit(businessObject, null, null, version);
			}
		}
		return null;
	}
	
	
}