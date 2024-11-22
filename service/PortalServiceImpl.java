package com.marksman.census.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marksman.census.bo.AdminProfile;
import com.marksman.census.bo.Area;
import com.marksman.census.bo.BrandAssignList;
import com.marksman.census.bo.BrandAssignment;
import com.marksman.census.bo.BrandPrice;
import com.marksman.census.bo.BrandPriceDetails;
import com.marksman.census.bo.BwuVisibility;
import com.marksman.census.bo.City;
import com.marksman.census.bo.CityStockAssignment;
import com.marksman.census.bo.Criteria;
import com.marksman.census.bo.DisplayAuditProductDetails;
import com.marksman.census.bo.Employee;
import com.marksman.census.bo.EvaluationRemarks;
import com.marksman.census.bo.ExportShop;
import com.marksman.census.bo.Factory;
import com.marksman.census.bo.Family;
import com.marksman.census.bo.Industry;
import com.marksman.census.bo.Menu;
import com.marksman.census.bo.RateAssignment;
import com.marksman.census.bo.Region;
import com.marksman.census.bo.Section;
import com.marksman.census.bo.Shop;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.bo.Zone;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.client.bo.Attendance;
import com.marksman.census.client.bo.CheckInInfo;
import com.marksman.census.client.bo.Items;
import com.marksman.census.client.bo.ShopAssetImage;
import com.marksman.census.client.bo.SingleSurvey;
import com.marksman.census.client.bo.StockTransfer;
import com.marksman.census.client.bo.SyncShop;
import com.marksman.census.client.bo.TransferCodeDetails;
import com.marksman.census.constants.CommonConstants;
import com.marksman.census.constants.DateTimeConstants;
import com.marksman.census.constants.EmployeeType;
import com.marksman.census.constants.Roles;
import com.marksman.census.constants.StockConstants;
import com.marksman.census.constants.SurveyorType;
import com.marksman.census.constants.SysConstants;
import com.marksman.census.crons.EmailSendingCron;
import com.marksman.census.dao.AreaDao;
import com.marksman.census.dao.BwusDao;
import com.marksman.census.dao.CitiesDao;
import com.marksman.census.dao.DsrDao;
import com.marksman.census.dao.EvaluationDao;
import com.marksman.census.dao.FamiliesDao;
import com.marksman.census.dao.IndustriesDao;
import com.marksman.census.dao.PortalDao;
import com.marksman.census.dao.RegionsDao;
import com.marksman.census.dao.ReportsDao;
import com.marksman.census.dao.ShopsDao;
import com.marksman.census.dao.SsrDao;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.dao.SystemDao;
import com.marksman.census.message.MessageType;
import com.marksman.census.portal.bo.DashboardReport;
import com.marksman.census.portal.bo.DuplicateClusters;
import com.marksman.census.portal.bo.FinanceSetup;
import com.marksman.census.portal.bo.ProductsDisplayDetail;
import com.marksman.census.report.ExportToCsv;
import com.marksman.census.report.PivotBasedReport;
import com.marksman.census.report.PrimaryTargetsVsAchievementReport;
import com.marksman.census.util.CommonUtil;
import com.marksman.census.util.DateTimeUtilities;
import com.marksman.census.util.FileUtils;
import com.marksman.census.util.PasswordStorage;
import com.marksman.census.util.PasswordStorage.CannotPerformOperationException;
import com.marksman.census.util.PasswordStorage.InvalidHashException;
import com.marksman.census.util.SpringApplicationContext;
import com.marksman.census.util.StringUtils;

import org.springframework.dao.DuplicateKeyException;

public class PortalServiceImpl implements PortalService {
	protected Logger logger = Logger.getLogger(this.getClass());
	private ResourceBundle bundle = ResourceBundle
			.getBundle(CommonConstants.PROPERTY_FILE_NAME);
	@Autowired
	SsrDao ssrDao;
	@Autowired
	ShopsDao shopsDao;
	@Autowired
	SurveyorDao surveyorDao;
	@Autowired
	IndustriesDao industriesDao;
	@Autowired
	FamiliesDao familiesDao;
	@Autowired
	AreaDao areaDao;
	@Autowired
	PortalDao portalDao;
	@Autowired
	BwusDao bwusDao;
	@Autowired
	ReportsService reportsService;
	@Autowired
	ApplicationCacheService applicationCacheService;
	@Autowired
	EvaluationDao evaluationDao;
	@Autowired
	SystemDao systemDao;
	@Autowired
	PrimaryTargetsVsAchievementReport pvaReport;
	@Autowired
	CitiesDao citiesDao;

	@Autowired
	RegionsDao regionsDao;

	@Autowired
	ReportsDao reportsDao;

	@Autowired
	FileUtils fileUtils;

	@Autowired
	ServletContext servletContext;

	@Autowired
	DsrDao dsrDao;
	@Autowired
	EmailSendingCron emailSendingCron;
	@Autowired
	private ExportToCsv exceRxcel;
	@Autowired
	private PivotBasedReport pivotBasedReport;
	

	@Override
	public ModelAndView createLogin(String userName, String password,
			Integer typeId, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView(
				"WEB-INF/jsp/createUserLogin");
		modelAndView.addObject("rolesList", portalDao.loadRolesList());
		modelAndView
				.addObject("time", DateTimeUtilities
						.getCurrentDate(DateTimeConstants.TIME_FORMAT));
		modelAndView.addObject("date", DateTimeUtilities
				.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
		modelAndView.addObject("userName", userName);
		modelAndView.addObject("role", SpringApplicationContext.getAuthority());

		if (userName != null && password != null) {
			try {
				int update = portalDao.createLogin(userName,
						PasswordStorage.createHash(password), typeId);
				if (update > 0) {
					modelAndView.addObject("message",
							"User Created Successfully !");
					modelAndView.addObject("textColor", "green");
				} else {
					modelAndView.addObject("message",
							"User Creation failed, Plz try later !");
					modelAndView.addObject("textColor", "red");
				}
			} catch (Exception ex) {
				logger.error(
						"Exception occured while creating login against user name "
								+ userName, ex);
				CommonUtil.writeErrorMessage(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						MessageType.ERROR_SERVER, response);
			}
		}
		return modelAndView;
	}

	@Override
	public ModelAndView portalLogin(String userName) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/ssrSummary");
			if (Roles.Validator.toString().equals(
					SpringApplicationContext.getAuthority())) {

				modelAndView.addObject("dsrShopSummary", ssrDao
						.getValidatorShopSummary(SpringApplicationContext
								.getAdmionProfile().getUserId()));
			} else if (Roles.Data_Exporter.toString().equals(
					SpringApplicationContext.getAuthority())) {

				modelAndView = new ModelAndView("WEB-INF/jsp/shopsSummary");
				modelAndView.addObject("dsrShopSummary", ssrDao
						.getValidatorShopSummary(SpringApplicationContext
								.getAdmionProfile().getUserId()));
			}

			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", userName);
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());

			return modelAndView;
		} catch (Exception ex) {

			logger.error("Exception occured while lodding login page", ex);
			return new ModelAndView("WEB-INF/jsp/error");
		}
	}

	@Override
	public ModelAndView loginPage() {

		ModelAndView modelAndView = new ModelAndView("WEB-INF/jsp/login");
		modelAndView.addObject("dateTime", DateTimeUtilities
				.getCurrentDate(DateTimeConstants.DATE_TIME_FORMAT2));
		return modelAndView;
	}

	@Override
	public ModelAndView logoutPage(HttpServletRequest request) {
		request.getSession().invalidate();
		ModelAndView modelAndView = new ModelAndView("redirect:" + "/");
		return modelAndView;
	}

	@Override
	public ModelAndView loadPendingShops(int surveyorId, int shopClosed) {

		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/pendingShopsList");
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("surveyor",
					surveyorDao.getSurveyorById(surveyorId));

			if (Roles.Validator.toString().equals(
					SpringApplicationContext.getAuthority())) {

				modelAndView.addObject("evaluated", ssrDao
						.getCurrentSsrEvaluated(SpringApplicationContext
								.getAdmionProfile().getUserId()));
			} else if (Roles.Data_Exporter.toString().equals(
					SpringApplicationContext.getAuthority())) {

				modelAndView = new ModelAndView("WEB-INF/jsp/shopList");
			}
			List<SyncShop> shopsList = surveyorDao.getPendingShopsList(
					surveyorId, shopClosed);

			if (shopsList.size() > 0) {

				modelAndView.addObject("pendingShopsList", shopsList);
			}
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("surveyor",
					surveyorDao.getSurveyorById(surveyorId));
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {

			logger.error("Exception occured while loading Pending shops List",
					ex);

		}

		return modelAndView;
	}

	@Override
	public ModelAndView basicScreening(int shopId, int merchandiserShopId,
			int surveyorId, String buildVersion, String shopClosed) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/basicScreening");
			modelAndView.addObject("screening", ssrDao.loadBasicScreening(
					surveyorId, shopId, merchandiserShopId));
			modelAndView.addObject("surveyorId", surveyorId);
			modelAndView.addObject("shopId", shopId);
			modelAndView.addObject("shopGroups", shopsDao.loadShopGroups());
			modelAndView.addObject("merchandiserShopId", merchandiserShopId);
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
			modelAndView.addObject("buildVersion", buildVersion);
			modelAndView.addObject("shopClosed", shopClosed);
		} catch (Exception ex) {
			logger.error("Exception occured while loading Basic Screening", ex);
		}
		return modelAndView;
	}

	@Override
	public void validateShop(Integer shopId, Integer merchandiserShopId,
			String updatedValue, String oldValue, String validationAction,
			String validationStatus, Integer bwuId, Integer industryId,
			Integer surveyorId) {
		int validatorId = SpringApplicationContext.getAdmionProfile()
				.getUserId();
		logger.info("validating shop : " + shopId + ",validationAction : "
				+ validationAction + ",validationStatus : " + validationStatus);
		if (validationStatus.equalsIgnoreCase("RED_FLAG")) {
			ssrDao.saveValidationActivity(shopId, merchandiserShopId,
					validatorId, updatedValue, oldValue, validationAction,
					validationStatus, bwuId);
			ssrDao.updateShopStatus(shopId, validationStatus);
		} else if (!StringUtils.isNullOrEmptyString(updatedValue)) {
			/******* IF USER HAS NOT UPDATED THEN NO ENTRY IN VALIDATION TABLE ******/
			if (!oldValue.equalsIgnoreCase(updatedValue)) {
				ssrDao.saveValidationActivity(shopId, merchandiserShopId,
						validatorId, updatedValue, oldValue, validationAction,
						validationStatus, bwuId);
			}

			if (validationAction.equalsIgnoreCase("SHOP_NAME")
					|| validationAction.equalsIgnoreCase("SHOP_GROUP")) {
				ssrDao.updateMercandiserShopTitle(shopId, updatedValue,
						validationAction);
			} else if (validationAction.equalsIgnoreCase("VISIBILITY")) {
				ssrDao.updateMerchBwuQuantity(shopId, updatedValue, bwuId,
						industryId, surveyorId, merchandiserShopId);
			}
		}

		if ((validationAction.equalsIgnoreCase("LOCATION_CHECK") && validationStatus
				.equalsIgnoreCase("COMPLETE"))
				|| validationStatus.equalsIgnoreCase("RED_FLAG")) {

			logger.info("validating shop : " + shopId + ",validationAction : "
					+ validationAction + ",validationStatus : "
					+ validationStatus);

			ssrDao.updateShopStatus(shopId, validationStatus);
		}

	}

	@Override
	public void updateDisplayAuditProductFacing(Integer surveyorId,
			Integer shopId, Integer merchandiserShopId, Integer categoryId,
			Integer productId, Integer displayId, Integer displayNumber,
			String oldFacing, String newFacing, String validationAction,
			String validationStatus, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("Validating shop for displayAudit : " + shopId
				+ ",validationAction : " + validationAction
				+ ",validationStatus : " + validationStatus);

		if (validationStatus.equalsIgnoreCase("RED_FLAG")) {
			ssrDao.saveValidationActivity(shopId, merchandiserShopId,
					surveyorId, newFacing, oldFacing, validationAction,
					validationStatus, -1);
			ssrDao.updateShopStatus(shopId, validationStatus);
		} else if (!StringUtils.isNullOrEmptyString(newFacing)) {
			ssrDao.saveValidationActivity(shopId, merchandiserShopId,
					surveyorId, newFacing, oldFacing, validationAction,
					validationStatus, -1);
			ssrDao.updateDisplayAuditProductFacing(surveyorId, shopId,
					merchandiserShopId, categoryId, displayId, displayNumber,
					productId, newFacing);
		}
	}

	@Override
	public void updateDisplayRemark(Integer surveyorId, Integer shopId,
			Integer merchandiserShopId, Integer categoryId, Integer displayId,
			Integer displayNumber, Integer displayRemark,
			HttpServletRequest request, HttpServletResponse response) {
		portalDao.updateDisplayRemark(surveyorId, shopId, merchandiserShopId,
				categoryId, displayId, displayNumber, displayRemark);
	}

	@Override
	public void validateShopCategories(Integer surveyorId, Integer shopId,
			Integer merchandiserShopId, Integer categoryId, Integer displayId,
			Integer displayNumber, String oldTotalFacings,
			String oldMndlzFacings, String oldGoldMndlzFacings,
			String newTotalFacings, String newMndlzFacings,
			String newGoldMndlzFacings, String validationAction,
			String validationStatus, HttpServletRequest request,
			HttpServletResponse response) {

		logger.info("validating shop : " + shopId + ",validationAction : "
				+ validationAction + ",validationStatus : " + validationStatus);

		if (validationStatus.equalsIgnoreCase("RED_FLAG")) {
			ssrDao.saveValidationActivityCategories(shopId, merchandiserShopId,
					surveyorId, newTotalFacings, oldTotalFacings,
					validationAction, validationStatus, categoryId, displayId,
					displayNumber, "total_facings");

			ssrDao.saveValidationActivityCategories(shopId, merchandiserShopId,
					surveyorId, newMndlzFacings, oldMndlzFacings,
					validationAction, validationStatus, categoryId, displayId,
					displayNumber, "mondelez_facings");

			ssrDao.saveValidationActivityCategories(shopId, merchandiserShopId,
					surveyorId, newGoldMndlzFacings, oldGoldMndlzFacings,
					validationAction, validationStatus, categoryId, displayId,
					displayNumber, "gold_mondelez_facings");
			ssrDao.updateShopStatus(shopId, validationStatus);
		} else {
			if (!oldTotalFacings.equalsIgnoreCase(newTotalFacings)) {
				ssrDao.saveValidationActivityCategories(shopId,
						merchandiserShopId, surveyorId, newTotalFacings,
						oldTotalFacings, validationAction, validationStatus,
						categoryId, displayId, displayNumber, "total_facings");
			}

			if (!oldMndlzFacings.equalsIgnoreCase(newMndlzFacings)) {
				ssrDao.saveValidationActivityCategories(shopId,
						merchandiserShopId, surveyorId, newMndlzFacings,
						oldMndlzFacings, validationAction, validationStatus,
						categoryId, displayId, displayNumber,
						"mondelez_facings");
			}

			if (!oldGoldMndlzFacings.equalsIgnoreCase(newGoldMndlzFacings)) {
				ssrDao.saveValidationActivityCategories(shopId,
						merchandiserShopId, surveyorId, newGoldMndlzFacings,
						oldGoldMndlzFacings, validationAction,
						validationStatus, categoryId, displayId, displayNumber,
						"gold_mondelez_facings");
			}

			ssrDao.updateDisplayDetails(shopId, merchandiserShopId, categoryId,
					displayId, displayNumber,
					StringUtils.parseInt(newTotalFacings, 0),
					StringUtils.parseInt(newMndlzFacings, 0),
					StringUtils.parseInt(newGoldMndlzFacings, 0));
		}
	}

	@Override
	public ModelAndView hotZone(Integer merchandiserShopId, Integer surveyorId,
			Integer shopId, Integer displayId, Integer displayNumber,
			String buildVersion, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			List<ProductsDisplayDetail> productsDisplayDetailData = portalDao
					.getHotZoneData(shopId, displayId, displayNumber);
			List<DisplayAuditProductDetails> displayAuditProductDetailsData = portalDao
					.getProductFacingsData(shopId, displayId, displayNumber);
			List<Integer> totalDisplayNumbers = portalDao
					.getDisplayNumbers(shopId);

			modelAndView = new ModelAndView("WEB-INF/jsp/hotZone");

			modelAndView.addObject("shopId", shopId);
			modelAndView.addObject("displayId", displayId);
			modelAndView.addObject("surveyorId", surveyorId);
			modelAndView.addObject("merchandiserShopId", merchandiserShopId);
			modelAndView.addObject("displayNumber", displayNumber);
			modelAndView.addObject("buildVersion", buildVersion);
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
			modelAndView.addObject("hotZoneData", productsDisplayDetailData);
			modelAndView.addObject("availableCategories",
					portalDao.getDisplayAuditCategories(shopId, displayId,
							displayNumber));
			modelAndView.addObject("totalDisplayNumbers", totalDisplayNumbers);
			modelAndView.addObject("displayNumbersCount",
					totalDisplayNumbers.size());
			modelAndView.addObject("shopId", shopId);
			modelAndView.addObject("categories",
					applicationCacheService.getProductCategories());
			modelAndView.addObject("displayAuditProductDetailsData",
					displayAuditProductDetailsData);
		} catch (Exception ex) {
			logger.error("Exception occured while loading hotZone : ", ex);
		}
		return modelAndView;
	}

	@Override
	public ModelAndView primaryShelf(Integer merchandiserShopId,
			Integer surveyorId, Integer shopId, Integer displayId,
			Integer displayNumber, String buildVersion,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			List<ProductsDisplayDetail> productsDisplayDetailData = portalDao
					.getPrimaryShelfData(shopId, displayId, displayNumber);
			List<DisplayAuditProductDetails> displayAuditProductDetailsData = portalDao
					.getProductFacingsData(shopId, displayId, displayNumber);
			modelAndView = new ModelAndView("WEB-INF/jsp/primaryShelf");

			modelAndView.addObject("shopId", shopId);
			modelAndView.addObject("displayId", displayId);
			modelAndView.addObject("surveyorId", surveyorId);
			modelAndView.addObject("merchandiserShopId", merchandiserShopId);
			modelAndView.addObject("displayNumber", displayNumber);
			modelAndView.addObject("buildVersion", buildVersion);
			modelAndView.addObject("availableCategories",
					portalDao.getDisplayAuditCategories(shopId, displayId,
							displayNumber));
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
			modelAndView.addObject("primaryShelfData",
					productsDisplayDetailData);
			modelAndView.addObject("categories",
					applicationCacheService.getProductCategories());
			modelAndView.addObject("displayAuditProductDetailsData",
					displayAuditProductDetailsData);
		} catch (Exception ex) {
			logger.error("Exception occured while loading hotZone : ", ex);
		}
		return modelAndView;
	}

	@Override
	public ModelAndView visibility(int shopId, int merchandiserShopId,
			int surveyorId, Integer categoryId, String buildVersion,
			String chillerType) {
		ModelAndView modelAndView = null;
		try {
			List<BwuVisibility> shopCillerList = new ArrayList<BwuVisibility>();

			shopCillerList = ssrDao.loadBwuByShop(shopId, merchandiserShopId,
					chillerType);
			modelAndView = new ModelAndView("WEB-INF/jsp/visibility");

			modelAndView.addObject("shopCillerList", shopCillerList);
			modelAndView.addObject("shopId", shopId);
			modelAndView.addObject("categoryId", categoryId);
			modelAndView.addObject("surveyorId", surveyorId);
			modelAndView.addObject("chillerType", chillerType);
			modelAndView.addObject("merchandiserShopId", merchandiserShopId);
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
			modelAndView.addObject("buildVersion", buildVersion);
		} catch (Exception ex) {
			logger.error("Exception occured while loading Visibility", ex);
		}
		return modelAndView;
	}

	@Override
	public ModelAndView volumeCheck(int shopId, int merchandiserShopId,
			int surveyorId, String buildVersion) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/volumeCheck");
			modelAndView.addObject("industryList", industriesDao
					.getIndustryVolumeCheck(shopId, merchandiserShopId));
			modelAndView.addObject("familyList", familiesDao
					.getFamilyVolumeCheck(shopId, merchandiserShopId));
			modelAndView.addObject("volumeCheck", ssrDao.loadVolumeCheck(
					surveyorId, shopId, merchandiserShopId));
			modelAndView.addObject("shopId", shopId);
			modelAndView.addObject("surveyorId", surveyorId);
			modelAndView.addObject("shop",
					shopsDao.getShopById(merchandiserShopId));
			modelAndView.addObject("merchandiserShopId", merchandiserShopId);
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
			modelAndView
					.addObject("allowedVariance", Float.valueOf(bundle
							.getString(
									CommonConstants.ALLOWD_VARIENCE_PERCENTAGE)
							.trim()));
			modelAndView.addObject("buildVersion", buildVersion);
		} catch (Exception ex) {
			logger.error("Exception occured while loading volume check", ex);
		}
		return modelAndView;
	}

	@Override
	public ModelAndView locationValidation(Integer shopId,
			Integer merchandiserShopId, Integer surveyorId, String buildVersion) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/location");
			modelAndView.addObject("location", areaDao.loadLocation(shopId));
			modelAndView.addObject("shopId", shopId);
			modelAndView.addObject("merchandiserShopId", merchandiserShopId);
			modelAndView.addObject("surveyorId", surveyorId);
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
			modelAndView.addObject("buildVersion", buildVersion);
		} catch (Exception ex) {
			logger.error("Exception occured while loading GPS Co-ordinates", ex);
		}
		return modelAndView;
	}

	@Override
	public ModelAndView shopsTag() {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/shopsMap");
			modelAndView = reportsService.setAttributes(modelAndView);
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {
			logger.error("Exception occured while loading Shops Tag", ex);
		}
		return modelAndView;
	}

	@Override
	public ModelAndView duplicateClusters() {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/duplicateClusters");
			modelAndView = reportsService.setAttributes(modelAndView);
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {
			logger.error("Exception occured while loading duplicate clusters.",
					ex);
		}
		return modelAndView;
	}

	@Override
	public ModelAndView duplicateShops(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, String longitude, String latitude) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/duplicateShops");
			modelAndView.addObject("shopsList", shopsDao.getDuplicateShops(
					zoneId, regionId, townId, areaId, longitude, latitude));
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {
			logger.error("Exception occured while loading duplicate shops.", ex);
		}
		return modelAndView;
	}

	@Override
	public Map<Integer, ExportShop> viewShopsTag(Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, String startDate,
			String endDate) {
		Map<Integer, ExportShop> shopsMap = null;
		try {
			shopsMap = shopsDao.getMerchandisedShops(zoneId, regionId, townId,
					areaId, startDate, endDate, 0, SurveyorType.DSS, -1);
		} catch (Exception ex) {
			logger.error("Exception occured while view Shops Tag", ex);
		}
		return shopsMap;
	}

	@Override
	public List<DuplicateClusters> getDuplicateClusters(Integer zoneId,
			Integer regionId, Integer townId, Integer areaId) {
		List<DuplicateClusters> clusters = null;
		try {
			clusters = shopsDao.getDuplicateClusters(zoneId, regionId, townId,
					areaId);
		} catch (Exception ex) {
			logger.error("Exception occured while view duplicate clusters", ex);
		}
		return clusters;
	}

	@Override
	public ModelAndView loadShopsToAssign(int surveyorId) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/assignShops");
			modelAndView.addObject("surveyorId", surveyorId);
			modelAndView.addObject("shopsList",
					surveyorDao.loadShopsToAssign(surveyorId));
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {
			logger.error("Exception occured while loading Shops to Assign", ex);
		}
		return modelAndView;
	}

	@Override
	public void saveAssignedShops(Integer surveyorId, String shopIds,
			String merchandiserShopId) {
		final String[] shops = shopIds.split(",");
		final String[] merchandiserShops = merchandiserShopId.split(",");

		shopsDao.insertAssignedShops(surveyorId, shops, merchandiserShops);
	}

	@Override
	public ModelAndView updatePassword(String newPassword,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/updatePassword");
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());

			if (!StringUtils.isNullOrEmptyString(newPassword)) {

				PasswordStorage.createHash(newPassword);
				int update = portalDao.updatePassword(SpringApplicationContext
						.getAdmionProfile().getUserId(), PasswordStorage
						.createHash(newPassword));
				if (update > 0) {
					modelAndView.addObject("message",
							"Password Updated Successfully. Please re-login!");
					modelAndView.addObject("textColor", "green");
				} else {
					modelAndView.addObject("message",
							"Password Updation failed, Please try later !");
					modelAndView.addObject("textColor", "red");
				}
			}
		} catch (Exception ex) {
			logger.error("Exception occured while updating password", ex);
		}
		return modelAndView;
	}

	@Override
	public ModelAndView loadBwuImages(int merchandiserShopId, int shopId,
			int industryId, int bwuId) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/bwuImages");
			modelAndView.addObject("imagesList", bwusDao.loadBwuImages(
					merchandiserShopId, shopId, industryId, bwuId));
			modelAndView.addObject("bwuId", bwuId);
			modelAndView.addObject("industryId", industryId);
			modelAndView.addObject("shopId", shopId);
			modelAndView.addObject("merchandiserShopId", merchandiserShopId);
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
		} catch (Exception ex) {
			logger.error("Exception occured while loading BWU images", ex);
		}
		return modelAndView;
	}

	@Override
	public ModelAndView getShopRemarks(Integer shopId, String requestType,
			Integer surveyorId, Integer merchandiserShopId, Integer remarkId) {
		ModelAndView modelAndView = null;
		try {
			if (!requestType.contains("none")) {
				industriesDao.updateShopRemarks(shopId, surveyorId,
						merchandiserShopId, requestType, remarkId);
			}

			modelAndView = new ModelAndView("WEB-INF/jsp/shopRemarks");
			modelAndView.addObject("sOPList", industriesDao.getShopsRemarks(
					shopId, 3, surveyorId, merchandiserShopId));
			modelAndView.addObject("rTMList", industriesDao.getShopsRemarks(
					shopId, 4, surveyorId, merchandiserShopId));
			modelAndView.addObject("assetRemarksList", industriesDao
					.getShopAssetsRemarks(shopId, merchandiserShopId));
			modelAndView.addObject("shopId", shopId);
			modelAndView.addObject("surveyorId", surveyorId);
			modelAndView.addObject("merchandiserShopId", merchandiserShopId);
		} catch (Exception ex) {
			logger.error("Exception occured while loading Shop Remarks", ex);
			ex.printStackTrace();
		}

		return modelAndView;
	}

	@Override
	public ModelAndView getShopAssetRemarks(Integer shopId, String requestType,
			Integer surveyorId, Integer merchandiserShopId, Integer remarkId,
			Integer merchandiserAssetId) {
		ModelAndView modelAndView = null;
		try {
			if (!requestType.contains("none")) {
				industriesDao.updateShopAssetsRemarks(shopId,
						merchandiserAssetId, merchandiserShopId, requestType,
						remarkId);

			}

			modelAndView = new ModelAndView("WEB-INF/jsp/shopRemarks");
			modelAndView.addObject("sOPList", industriesDao.getShopsRemarks(
					shopId, 3, surveyorId, merchandiserShopId));
			modelAndView.addObject("rTMList", industriesDao.getShopsRemarks(
					shopId, 4, surveyorId, merchandiserShopId));
			modelAndView.addObject("assetRemarksList", industriesDao
					.getShopAssetsRemarks(shopId, merchandiserShopId));
			modelAndView.addObject("shopId", shopId);
			modelAndView.addObject("surveyorId", surveyorId);
			modelAndView.addObject("merchandiserShopId", merchandiserShopId);
		} catch (Exception ex) {
			logger.error("Exception occured while loading Shop Remarks", ex);
			ex.printStackTrace();
		}

		return modelAndView;
	}

	@Override
	public void reEvaluateShop(Integer shopId) {
		shopsDao.reEvaluateShop(shopId);
	}

	@Override
	public List<Map<String, Object>> getMerchandiserListMap(String visitDate) {

		return evaluationDao.getMerchandiserListMap(visitDate);
	}

	@Override
	public List<Map<String, Object>> getMerchandiserShopListMap(
			Integer surveyorId, String startDate, String endDate) {

		return evaluationDao.getMerchandiserShopListMap(surveyorId, startDate,
				endDate);
	}

	@Override
	public Map<String, Object> getSectionListMap(Integer merchandiserShopId,
			Integer userTypeId, String visitType, String surveyorType) {
		ArrayList<Section> sectionList = new ArrayList<Section>();
		List<Criteria> criteriaList = null;
		Map<String, Object> shopSectionMap = new HashMap<String, Object>();
		Section shopDetail = new Section();
		Section shopFascia = new Section();
		Section soPurchase = new Section();
		Section sPattern = new Section();
		Section psHours = new Section();
		Section tpsom = new Section();
		// Section gInfo = new Section();
		Section sVisibility = new Section();
		Section shopLocations = new Section();
		Section oQuality = new Section();
		Section pref = new Section();
		Section sDisplay = new Section();
		Section salesData = new Section();
		Section qoSales = new Section();
		Section rtmData = new Section();
		Section audioFile = new Section();
		Section pposmArticleData = new Section();
		Section raFamily = new Section();
		Section groupNumeric = new Section();
		List<Map<String, Object>> retailDetail = null;
		List<Map<String, Object>> groupNumericDetail = null;
		List<Map<String, Object>> shopDetailMap = null;
		List<EvaluationRemarks> remarksList = null;
		List<Map<String, Object>> tpsomData = null;

		String shopType = "added";
		String isAudioAvailable = null;
		if (visitType.equalsIgnoreCase("added")) {
			shopDetailMap = evaluationDao
					.getAddedShopDetailList(merchandiserShopId);
		} else if (visitType.equalsIgnoreCase("SISRAadded")) {
			shopDetailMap = evaluationDao
					.getSISRAShopDetailList(merchandiserShopId);
			shopType = "SISRAadded";
		} else if (visitType
				.equalsIgnoreCase(SysConstants.VISIT_TYPE_RETAIL_AUDIT)
				|| visitType.equalsIgnoreCase(SysConstants.VISIT_TYPE_SIS)) {
			shopDetailMap = evaluationDao.getVisitedShopDetailList(
					merchandiserShopId, visitType);
		} else if (visitType.equalsIgnoreCase(SysConstants.VISIT_TYPE_ON_ROUTE)
				|| visitType
						.equalsIgnoreCase(SysConstants.VISIT_TYPE_OFF_ROUTE_DSR)
				|| visitType
						.equalsIgnoreCase(SysConstants.VISIT_TYPE_OFF_ROUTE_OTHER)) {
			shopDetailMap = evaluationDao
					.getDsrVisitedShopDetail(merchandiserShopId);
		} else {

			shopDetailMap = evaluationDao.getVisitedShopDetailList(
					merchandiserShopId, visitType);
			isAudioAvailable = shopDetailMap.get(0).get("is_audio_available")
					.toString();

			remarksList = evaluationDao.getEvaluationRemarksList();
		}

		shopDetail = evaluationDao.getShopDetailTagsList(shopDetailMap);

		// map section
		shopLocations.setSectionTitle("Location");
		// type 4 = GPRS co-ordinates
		shopLocations.setImageViewType(4);
		shopLocations.setMslTable(shopDetailMap);

		if (visitType.equalsIgnoreCase("ww")
				|| visitType.equalsIgnoreCase("wr")
				|| visitType.equalsIgnoreCase("town_storming")
				|| visitType.equalsIgnoreCase("visit")) {
			Section brandAvailability = new Section();
			Section visitPposmDisplay = new Section();
			Section tmVisitImagesDisplay = new Section();
			Section shelfDisplay = new Section();
			Section tposmDisplay = new Section();
			Section dropSize = new Section();
			Section dropSizeRemarks = new Section();

			shopFascia = evaluationDao.getShopFasciaImages(merchandiserShopId,
					visitType, surveyorType);

			if (!StringUtils.isNullOrEmptyString(shopFascia.getTagsList()
					.get(6).getValue())) {
				audioFile.setAudioFileUrl(shopFascia.getTagsList().get(6)
						.getValue());
				audioFile.setImageViewType(5);
				shopFascia.getTagsList().remove(6);
				audioFile.setSectionTitle("Audio Recording");

			}
			sectionList.add(shopFascia);
			sectionList.add(audioFile);

			sectionList.add(shopLocations);

			shelfDisplay = evaluationDao.getShelfDisplay(merchandiserShopId);
			if (!shelfDisplay.getImageList().isEmpty()) {
				shelfDisplay.setSectionTitle(" Shelf Display");
				shelfDisplay.setImageViewType(2);
				sectionList.add(shelfDisplay);
			}

			brandAvailability = evaluationDao
					.getBrandAvailabilityRemarks(merchandiserShopId);

			if (brandAvailability != null) {
				brandAvailability.setSectionTitle("Brand Availability");
				brandAvailability.setImageViewType(3);
				sectionList.add(brandAvailability);
			}

			visitPposmDisplay = evaluationDao
					.getVisitPposmDisplay(merchandiserShopId);
			if (visitPposmDisplay != null) {
				visitPposmDisplay.setSectionTitle("PPOSM Display");
				visitPposmDisplay.setImageViewType(2);
				sectionList.add(visitPposmDisplay);
			}

			if (visitType.equalsIgnoreCase("visit")) {
				tmVisitImagesDisplay = evaluationDao
						.getTMVisitImages(merchandiserShopId);
				if (tmVisitImagesDisplay != null) {
					tmVisitImagesDisplay.setSectionTitle("Survey Images");
					tmVisitImagesDisplay.setImageViewType(2);
					sectionList.add(tmVisitImagesDisplay);
				}
			}

			if (evaluationDao.getSalesCallQualityData(merchandiserShopId) != null) {

				Section saleCallQuality = new Section();
				saleCallQuality = evaluationDao
						.getSalesCallQualityData(merchandiserShopId);
				saleCallQuality.setSectionTitle("Sale Call Qaulity");
				saleCallQuality.setImageViewType(3);
				sectionList.add(saleCallQuality);

			} else {

			}

			tposmDisplay = evaluationDao.getTPOSMDisplay(merchandiserShopId);
			if (tposmDisplay != null) {
				tposmDisplay.setSectionTitle("TPOSM Display");
				tposmDisplay.setImageViewType(2);
				sectionList.add(tposmDisplay);
			}

			dropSizeRemarks = evaluationDao
					.getDropSizeRemarksdata(merchandiserShopId);
			if (dropSizeRemarks != null) {
				dropSizeRemarks.setSectionTitle("DropSize Remarks");
				dropSizeRemarks.setImageViewType(3);
				sectionList.add(dropSizeRemarks);
			}

			dropSize = evaluationDao.getDropSizeData(merchandiserShopId);
			if (dropSize != null) {
				dropSize.setSectionTitle("DropSize");
				dropSize.setImageViewType(3);
				sectionList.add(dropSize);
			}

		} else if (visitType.equalsIgnoreCase(SysConstants.VISIT_TYPE_ON_ROUTE)
				|| visitType
						.equalsIgnoreCase(SysConstants.VISIT_TYPE_OFF_ROUTE_OTHER)
				|| visitType
						.equalsIgnoreCase(SysConstants.VISIT_TYPE_OFF_ROUTE_DSR)) {
			shopFascia = evaluationDao.getShopFasciaImages(merchandiserShopId,
					visitType, surveyorType);

			sectionList.add(shopFascia);
			sectionList.add(audioFile);

			sectionList.add(shopLocations);

			Section sale = evaluationDao.getDsrSaleData(merchandiserShopId,
					"SALE");
			if (sale != null) {
				sale.setSectionTitle("Sale");
				// type 3 = tabular view
				sale.setImageViewType(3);
				sectionList.add(sale);
			}

			Section mtCollection = evaluationDao.getDsrSaleData(
					merchandiserShopId, "MT_COLLECTION");
			if (mtCollection != null) {
				mtCollection.setSectionTitle("MT Collection");
				// type 3 = tabular view
				mtCollection.setImageViewType(3);
				sectionList.add(mtCollection);
			}

			Section mtCash = evaluationDao.getDsrSaleData(merchandiserShopId,
					"MT_CASH");
			if (mtCash != null) {
				mtCash.setSectionTitle("MT CASH");
				// type 3 = tabular view
				mtCash.setImageViewType(3);
				sectionList.add(mtCash);
			}

			Section rjp = evaluationDao.getDsrSaleData(merchandiserShopId,
					"RJP");
			if (rjp != null) {
				rjp.setSectionTitle("RJP");
				// type 3 = tabular view
				rjp.setImageViewType(3);
				sectionList.add(rjp);
			}

			Section TPOSMImages = evaluationDao
					.getDsrSurveyTposmImages(merchandiserShopId);
			if (TPOSMImages != null && TPOSMImages.getImageList().size() > 0) {
				TPOSMImages.setSectionTitle("TPOSM Images");
				// type 3 = tabular view
				TPOSMImages.setImageViewType(2);
				sectionList.add(TPOSMImages);
			}

			Section surveyQuestions = evaluationDao.getSisQuestionData(
					merchandiserShopId, 18);
			if (surveyQuestions != null) {
				surveyQuestions.setSectionTitle("Survey");
				// type 3 = tabular view
				surveyQuestions.setImageViewType(3);
				sectionList.add(surveyQuestions);
			}

		} else if (visitType
				.equalsIgnoreCase(SysConstants.VISIT_TYPE_RETAIL_AUDIT)
				|| visitType.equalsIgnoreCase(SysConstants.VISIT_TYPE_SIS)) {
			shopFascia = evaluationDao.getShopFasciaImages(merchandiserShopId,
					visitType, surveyorType);

			sectionList.add(shopFascia);
			sectionList.add(audioFile);

			sectionList.add(shopLocations);

			retailDetail = evaluationDao
					.getSisRetailAuditData(merchandiserShopId);
			if (retailDetail != null) {
				raFamily.setSectionTitle("Retail Audit");
				raFamily.setMslTable(retailDetail);
				// type 3 = tabular view
				raFamily.setImageViewType(3);
				sectionList.add(raFamily);
			}

			Section invoicePicture = evaluationDao
					.getInvoicePicture(merchandiserShopId);
			if (invoicePicture != null
					&& invoicePicture.getImageList().size() > 0) {
				invoicePicture.setSectionTitle(" Invoice Picture");
				invoicePicture.setImageViewType(2);
				sectionList.add(invoicePicture);
			}

			Section invoicePictureIndustryWise = evaluationDao
					.getInvoicePictureIndustryWise(merchandiserShopId);
			if (invoicePictureIndustryWise != null
					&& invoicePictureIndustryWise.getImageList().size() > 0) {
				invoicePictureIndustryWise.setSectionTitle(" Invoice Picture");
				invoicePictureIndustryWise.setImageViewType(2);
				sectionList.add(invoicePictureIndustryWise);
			}

			// Section mcFamily =
			// evaluationDao.getSisFamilyData(merchandiserShopId, 1);
			// if (mcFamily != null)
			// {
			// mcFamily.setSectionTitle("MC Off Take");
			// // type 3 = tabular view
			// mcFamily.setImageViewType(3);
			// sectionList.add(mcFamily);
			// }

			groupNumericDetail = evaluationDao.getSisFamilyData(
					merchandiserShopId, -1);
			if (groupNumericDetail != null) {
				groupNumeric.setSectionTitle("Group Numeric");
				groupNumeric.setMslTable(groupNumericDetail);
				// type 3 = tabular view
				groupNumeric.setImageViewType(3);
				sectionList.add(groupNumeric);
			}

			Section stvQ = evaluationDao.getSisQuestionData(merchandiserShopId,
					18);
			if (stvQ != null) {
				stvQ.setSectionTitle("Sale Team Visit");
				// type 3 = tabular view
				stvQ.setImageViewType(3);
				sectionList.add(stvQ);
			}

			Section sQ = evaluationDao.getSisSchemeData(merchandiserShopId);

			if (sQ != null) {
				sQ.setSectionTitle("Scheme");
				// type 3 = tabular view
				sQ.setImageViewType(3);
				sectionList.add(sQ);
			}

			pposmArticleData = evaluationDao
					.getSisBWUPposmArticlesDisplay(merchandiserShopId);
			if (pposmArticleData != null) {
				pposmArticleData.setSectionTitle("PPOSM Articles");
				// type 3 = tabular view
				pposmArticleData.setImageViewType(2);
				sectionList.add(pposmArticleData);
			}

		} else if (visitType.equalsIgnoreCase("SISRAadded")) {
			shopFascia = evaluationDao.getShopFasciaImages(merchandiserShopId,
					shopType, surveyorType);
			sectionList.add(shopFascia);
			sectionList.add(shopLocations);
		} else {
			// shopDetail = evaluationDao.getShopDetailList(merchandiserShopId);
			shopFascia = evaluationDao.getShopFasciaImages(merchandiserShopId,
					shopType, surveyorType);
			sectionList.add(shopFascia);
			sectionList.add(shopLocations);
			soPurchase = evaluationDao.getQuestionData(merchandiserShopId, 1);

			if (soPurchase != null) {
				soPurchase.setSectionTitle("Sources Of Purchase");
				// type 3 = tabular view
				soPurchase.setImageViewType(3);
				sectionList.add(soPurchase);
			}

			sPattern = evaluationDao.getQuestionData(merchandiserShopId, 2);

			if (soPurchase != null) {
				sPattern.setSectionTitle("Selling Pattern");
				// type 3 = tabular view
				sPattern.setImageViewType(3);
				sectionList.add(sPattern);
			}

			psHours = evaluationDao.getQuestionData(merchandiserShopId, 3);
			if (psHours != null) {
				psHours.setSectionTitle("Peak Selling Hours");
				// type 3 = tabular view
				psHours.setImageViewType(3);
				sectionList.add(psHours);
			}

			/*
			 * gInfo = evaluationDao.getQuestionData(merchandiserShopId, 4);
			 * 
			 * if (gInfo != null) {
			 * gInfo.setSectionTitle("General Information"); // type 3 = tabular
			 * view gInfo.setImageViewType(3); sectionList.add(gInfo); }
			 */

			sVisibility = evaluationDao.getQuestionData(merchandiserShopId, 5);

			if (sVisibility != null) {
				sVisibility.setSectionTitle("Stock Visibility");
				// type 3 = tabular view
				sVisibility.setImageViewType(3);
				sectionList.add(sVisibility);
			}

			oQuality = evaluationDao.getQuestionData(merchandiserShopId, 6);

			if (oQuality != null) {
				oQuality.setSectionTitle("Outlet Quality");
				// type 3 = tabular view
				oQuality.setImageViewType(3);
				sectionList.add(oQuality);
			}

			pref = evaluationDao.getQuestionData(merchandiserShopId, 7);

			if (pref != null) {
				pref.setSectionTitle("Preference");
				// type 3 = tabular view
				pref.setImageViewType(3);
				sectionList.add(pref);
			}

			sDisplay = evaluationDao.getQuestionData(merchandiserShopId, 8);

			if (sDisplay != null) {
				sDisplay.setSectionTitle("Shelf Display");
				// type 3 = tabular view
				sDisplay.setImageViewType(3);
				sectionList.add(sDisplay);
			}

			// ppsomDisplay = evaluationDao.getQuestionData(merchandiserShopId,
			// 9);
			salesData = evaluationDao
					.getSalesDataForAddedShop(merchandiserShopId);

			if (salesData != null) {
				salesData.setSectionTitle("Sales Data");
				// type 3 = tabular view
				salesData.setImageViewType(3);
				sectionList.add(salesData);
			}

			pposmArticleData = evaluationDao
					.getBWUPposmArticlesDisplay(merchandiserShopId);
			if (pposmArticleData != null) {
				pposmArticleData.setSectionTitle("PPOSM Articles");
				// type 3 = tabular view
				pposmArticleData.setImageViewType(2);
				sectionList.add(pposmArticleData);
			}

			rtmData = evaluationDao.getRTMDataForAddedShop(merchandiserShopId);
			if (rtmData != null) {
				rtmData.setSectionTitle("RTM Data");
				// type 3 = tabular view
				rtmData.setImageViewType(3);
				sectionList.add(rtmData);
			}

			qoSales = evaluationDao.getQuestionData(merchandiserShopId, 10);

			if (qoSales != null) {
				qoSales.setSectionTitle("Quality of Sales");
				// type 3 = tabular view
				qoSales.setImageViewType(3);
				sectionList.add(qoSales);
			}

			Section others = evaluationDao.getQuestionData(merchandiserShopId,
					20);

			if (others != null) {
				others.setSectionTitle("Others");
				// type 3 = tabular view
				others.setImageViewType(3);
				sectionList.add(others);
			}
		}

		if (!visitType.equalsIgnoreCase("added")
				&& !visitType
						.equalsIgnoreCase(SysConstants.VISIT_TYPE_RETAIL_AUDIT)
				&& !visitType.equalsIgnoreCase(SysConstants.VISIT_TYPE_SIS)
				&& !visitType
						.equalsIgnoreCase(SysConstants.VISIT_TYPE_ON_ROUTE)
				&& !visitType
						.equalsIgnoreCase(SysConstants.VISIT_TYPE_OFF_ROUTE_DSR)
				&& !visitType
						.equalsIgnoreCase(SysConstants.VISIT_TYPE_OFF_ROUTE_OTHER)
				&& !visitType.equalsIgnoreCase("SISRAadded")) {
			String shopStatus = evaluationDao.getShopStatus(merchandiserShopId);
			criteriaList = evaluationDao.getCriteriaList(isAudioAvailable,
					merchandiserShopId);
			shopSectionMap.put("shopStatus", shopStatus);

		}
		if (userTypeId != null
				&& (userTypeId == 31 || userTypeId == 4 || userTypeId == 19
						|| userTypeId == 24 || userTypeId == 33
						|| userTypeId == 14 || userTypeId == 71)) {
			shopSectionMap.put("criteria", criteriaList);
			shopSectionMap.put("isEditable", true);
		} else {
			shopSectionMap.put("criteria", "");
			shopSectionMap.put("isEditable", false);
		}
		shopSectionMap.put("remarks", remarksList);
		shopSectionMap.put("shopDetails", shopDetail);
		shopSectionMap.put("section", sectionList);
		return shopSectionMap;
	}

	@Override
	public Map<String, Object> getAuthForPortal(HttpServletRequest request,
			HttpServletResponse response, String userName, String password) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			AdminProfile adminprofile = portalDao.getPasswordHashByUserName(
					userName, 0);
			String passwordHash = adminprofile.getPassword();
			if (adminprofile != null
					&& adminprofile.getStatus().equalsIgnoreCase("N")) {
				map.put("User", "This user is disabled.");
				map.put("Authenticated", false);
			} else if (passwordHash.isEmpty() || passwordHash == null) {
				map.put("User", "Wrong User Name");
				map.put("Authenticated", false);
			} else {
				try {
					if (PasswordStorage.verifyPassword(password, passwordHash)) {

						map.put("User", "Authenticated Successfully..!");
						map.put("Authenticated", true);

						AdminProfile adminData = portalDao
								.getPortalAdminDetails(adminprofile.getUserId())
								.get(0);
						int typeId = adminData.getTypeId();
						List<Map<String, Object>> userAllowedUrls = null;
						userAllowedUrls = portalDao.getAllowedUrls(typeId);

						HttpSession session = request.getSession(); //
						AdminProfile userData = portalDao
								.getPortalAdminDetails(adminprofile.getUserId())
								.get(0);
						map.put("user", userData);
						session.setAttribute("user", userData); // Set the User
																// object in the
																// session
						// map.put("user_menu",systemDao.getMenus(adminprofile.getTypeId()));
						ArrayList<Items> headersList = new ArrayList<Items>();
						ArrayList<Menu> menuList = new ArrayList<Menu>();
						List<Map<String, Object>> towns;
						List<Zone> zones;/* = new ArrayList<Zone>(); */
						List<City> townList;
						List<Region> regionList;/* = new ArrayList<Region>(); */
						headersList = portalDao.loadHeader(adminData
								.getTypeId());
						menuList = portalDao.loadAdminMenusFromDB(adminData
								.getTypeId());
						towns = citiesDao.getCity(-1);
						townList = applicationCacheService.getCities();
						if (userData.getZoneId() > -1
								&& userData.getRegionId() > -1) {
							List<Region> singleRegion = new ArrayList<Region>();
							List<Zone> singleZone = new ArrayList<Zone>();
							String[] allowedZoneList = userData.getZoneIds()
									.split(",");
							for (int i = 0; i < allowedZoneList.length; i++) {
								singleZone.add(applicationCacheService
										.getZoneById(Integer
												.parseInt(allowedZoneList[i])));
							}
							String[] allowedRegionList = userData
									.getRegionIds().split(",");
							for (int i = 0; i < allowedRegionList.length; i++) {
								singleRegion
										.add(applicationCacheService.getRegionById(Integer
												.parseInt(allowedRegionList[i])));
							}
							map.put("regions", singleRegion);
							map.put("zones", singleZone);
						} else if (userData.getZoneId() > -1
								&& userData.getRegionId() == -1) {
							List<Zone> singleZone = new ArrayList<Zone>();
							String[] allowedZoneList = userData.getZoneIds()
									.split(",");
							for (int i = 0; i < allowedZoneList.length; i++) {
								singleZone.add(applicationCacheService
										.getZoneById(Integer
												.parseInt(allowedZoneList[i])));
							}
							regionList = applicationCacheService
									.getRegionsByZoneIds(userData.getZoneIds());
							map.put("regions", regionList);
							map.put("zones", singleZone);
						} else {
							regionList = applicationCacheService.getRegions();
							zones = applicationCacheService.getZones();
							map.put("regions", regionList);
							map.put("zones", zones);

						}

						ArrayList<Menu> list = new ArrayList<Menu>();
						for (Items header : headersList) {
							Menu menu = new Menu();
							menu.setHeader(header.getTitle());
							for (Menu item : list) {
								if (header.getTitle().equalsIgnoreCase(
										item.getLable())) {
									menu.setSubMenu(false);
								} else if (!header.getTitle().equalsIgnoreCase(
										item.getLable())) {
									menu.setSubMenu(true);
								}
							}
							menu.setMenuList(getMenuList(menuList,
									header.getTitle()));
							list.add(menu);

						}
						map.put("list", list);
						map.put("town", townList);
						map.put("projectName",
								bundle.getString(SysConstants.PROJECT).trim());
						map.put("projectType",
								bundle.getString(SysConstants.PROJECT_TYPE)
										.trim());
						map.put("allowedUrls", userAllowedUrls);

					} else {
						map.put("User", "Worng Password.");
						map.put("Authenticated", false);
					}
				} catch (CannotPerformOperationException e) {

					logger.error(e, e);
				} catch (InvalidHashException e) {
					logger.error(e, e);
				}
			}
			return map;

		} catch (Exception e) {
			logger.error(e, e);
			map.put("User", "Wrong User Name");
			map.put("Authenticated", false);
		}
		return map;
	}

	private ArrayList<Menu> getMenuList(ArrayList<Menu> menuList, String title) {
		ArrayList<Menu> subMenu = new ArrayList<Menu>();

		for (Menu menu : menuList) {
			if (menu.getHeader().equalsIgnoreCase(title)) {
				subMenu.add(menu);
			}

		}
		return subMenu;
	}

	@Override
	public List<Map<String, Object>> getMerchandiserShopListNDN(
			Integer regionId, Integer zoneId, Integer cityId,
			Integer distributionId, String storeType, Integer channelId,
			String startDate, String endDate, Integer rteId,
			Integer surveyorId, String nonEvaluated, String zones,
			String regions) {
		return surveyorDao.getMerchandiserShopList(regionId, zoneId, cityId,
				distributionId, storeType, channelId, startDate, endDate,
				rteId, surveyorId, nonEvaluated, zones, regions);
	}

	@Override
	public ArrayList<ShopAssetImage> getCompletedShopListNDN(Integer zoneId,
			Integer regionId, String startDate, String endDate,
			Integer merchandiserId, Integer remarksId, String visitType) {
		/*
		 * (visitType.equalsIgnoreCase("ww") ||
		 * visitType.equalsIgnoreCase("wr")) { return
		 * surveyorDao.getCompletedVisitShops(regionId, zoneId, merchandiserId,
		 * startDate, endDate, remarksId, visitType); } else if
		 * (visitType.equalsIgnoreCase("visit")) { return
		 * surveyorDao.getCompletedVisitShops(regionId, zoneId, merchandiserId,
		 * startDate, endDate, remarksId, visitType); } else {
		 */
		return surveyorDao.getCompletedShops(regionId, zoneId, merchandiserId,
				startDate, endDate, remarksId);
		/* } */

	}

	@Override
	public ArrayList<ShopAssetImage> getCompletedVisitShopListNDN(
			Integer zoneId, Integer regionId, String startDate, String endDate,
			Integer merchandiserId, Integer remarksId, String visitType,
			String evaluated) {
		return surveyorDao.getCompletedVisitShops(regionId, zoneId,
				merchandiserId, startDate, endDate, remarksId, visitType,
				evaluated);
	}

	@Override
	public List<Region> getRegions(Integer userId) {
		AdminProfile ap = (AdminProfile) portalDao.getProfileType(userId);
		if (ap.getRegionId() == -1) {
			return portalDao.getRegionsForPortal(ap.getTypeId());
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getDesAndDsrs(Integer regionId) {
		return portalDao.getDesAndDsrsForPortal(regionId);
	}

	@Override
	public List<Map<String, Object>> getDsrsDetails(Integer surveyorId,
			Integer regionId, String status) {
		return portalDao.getDsrsDetailsForPortal(surveyorId, regionId, status);
	}

	@Override
	public List<Map<String, Object>> getDsrAreasDetails(Integer dsrId,
			Integer regionId) {
		return portalDao.getDsrAreasDetailsForPortal(dsrId, regionId);
	}

	@Override
	public Map<String, Object> updateDeName(Integer surveyorId, String deName,
			Integer userId, String deStatus, Integer employeeId,
			float basicSalary, float dailyAllownce, float fuelAllownce,
			float maintainanceAllownce, float otherAllowances) {
		return portalDao.updateDeName(surveyorId, deName, userId, deStatus,
				employeeId, basicSalary, dailyAllownce, fuelAllownce,
				maintainanceAllownce, otherAllowances);
	}

	@Override
	public Map<String, Object> updateDsrDetails(Integer surveyorId,
			String dsrName, Integer dsrId, String dsrStatus, Integer userId,
			String dsrType, Integer dsrEmployee, float basicSalary,
			float dailyAllownce, float fuelAllownce,
			float maintainanceAllownce, float otherAllowances, float vanSalary) {
		return portalDao.updateDsrDertails(surveyorId, dsrName, dsrId,
				dsrStatus, userId, dsrType, dsrEmployee, basicSalary,
				dailyAllownce, fuelAllownce, maintainanceAllownce,
				otherAllowances, vanSalary);
	}

	@Override
	public Map<String, Object> updateDsrAreasDetails(Integer dsrId,
			String blockName, Integer blockId, Integer userId,
			String blockStatus, String dsrType, Integer dsrVanId, Integer townId) {
		return portalDao.updateDsrAreasDertails(dsrId, blockName, blockId,
				userId, blockStatus, dsrType, dsrVanId, townId);
	}

	@Override
	public void addDsrFromPortal(Integer surveyorId, String dsrName,
			Integer userId, String dsrType, Integer dsrEmployee)
			throws Exception {
		this.insertDsrFromPortal(surveyorId, dsrName, userId, dsrType,
				dsrEmployee);

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void insertDsrFromPortal(int surveyorId, String dsrName,
			Integer userId, String dsrType, Integer dsrEmployee)
			throws Exception {
		int dsrId = portalDao.insertDSRFromProtal(surveyorId, dsrName, userId,
				dsrType, dsrEmployee);
		portalDao.insertMappDsrToSurveyorFromProtal(surveyorId, dsrId, userId);

	}

	@Override
	public void addBlockFromPortal(String blockName, Integer townId,
			Integer dsrId, Integer userId, Integer dsrVanId,
			Integer exportedAreaId) throws Exception {
		this.insertBlockFromPortal(blockName, townId, dsrId, userId, dsrVanId,
				exportedAreaId);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void insertBlockFromPortal(String blockName, Integer townId,
			Integer dsrId, Integer userId, Integer dsrVanId,
			Integer exportedAreaId) throws Exception {

		try {
			int areaId = portalDao.insertBlockFromProtal(blockName, townId,
					userId);
			if (dsrId != -1) {
				portalDao.insertMappBlockToDsrFromProtal(dsrId, areaId, userId);
			}
			if (dsrVanId != -1) {
				dsrId = dsrVanId;
				portalDao.insertMappBlockToDsrFromProtal(dsrId, areaId, userId);
			}
			if (exportedAreaId != -1) {
				portalDao.importShopsFromBlock(exportedAreaId, areaId);
			}
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}

	}

	@Override
	public List<Map<String, Object>> getTowns(Integer regionId) {

		return citiesDao.getCitiesForPortal(regionId);
	}

	@Override
	public void submitTransferedStock(StockTransfer stockTransfer)
			throws Exception {
		logger.info("request from : " + stockTransfer.getTransferDevice());
		// if ("WEB".equalsIgnoreCase(stockTransfer.getTransferDevice())) {
		if (stockTransfer.getTransferStatus().equalsIgnoreCase("SENT")) {
			this.submitTransferedStockInTransaction(stockTransfer);
		} else if (stockTransfer.getTransferStatus().equalsIgnoreCase(
				"RECEIVED")
				&& stockTransfer.getTransferType().equalsIgnoreCase(
						"FACTORY_INTAKE")) {
			this.submitTransferedStockInTransaction(stockTransfer);
		} else if (stockTransfer.getTransferStatus().equalsIgnoreCase(
				"RECEIVED")) {
			/* this.submitReceivedStock(stockTransfer); */
			this.submitReceivedStockInTransaction(stockTransfer);
		}
		// }

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void submitTransferedStockInTransaction(StockTransfer stockTransfer)
			throws Exception {
		int transfered_stock_submitted_id = 0;

		// There should be a separate method for transfer code generation
		StringBuilder sb = new StringBuilder();
		List<Region> regionlist = null;
		List<City> townList = null;
		List<Factory> factoryList = null;
		List<Factory> wareHouseList = null;

		if (stockTransfer.getTransferType().equalsIgnoreCase(
				"FACTORY_TO_WAREHOUSE")) {
			String factoryType = "FACTORY";
			String factoryType1 = "WAREHOUSE";

			factoryList = portalDao.getFactoriesForPortal(factoryType);
			wareHouseList = portalDao.getFactoriesForPortal(factoryType1);
			for (Factory factory : factoryList) {
				if (factory.getId() == stockTransfer.getFromSource()) {
					sb.append(factory.getTitle());
				}
			}
			for (Factory wareHouse : wareHouseList) {
				if (wareHouse.getId() == stockTransfer.getToSource()) {
					sb.append("-");
					sb.append("to");
					sb.append("-");
					sb.append(wareHouse.getTitle());
				}
			}

		} else if (stockTransfer.getTransferType().equalsIgnoreCase(
				"TERRITORY_TO_TERRITORY")) {
			regionlist = portalDao.getRegionsForPortal(0);
			for (Region region : regionlist) {
				if (region.getId() == stockTransfer.getFromSource()) {
					sb.append(region.getTitle());
				}
			}
			for (Region region : regionlist) {
				if (region.getId() == stockTransfer.getToSource()) {
					sb.append("-");
					sb.append("to");
					sb.append("-");
					sb.append(region.getTitle());
				}
			}
		} else if (stockTransfer.getTransferType().equalsIgnoreCase(
				"TERRITORY_TO_TOWN")) {
			regionlist = portalDao.getRegionsForPortal(0);
			townList = citiesDao.getCitiesByRegionId(stockTransfer
					.getReceiverTerritory());
			for (Region region : regionlist) {
				if (region.getId() == stockTransfer.getFromSource()) {
					sb.append(region.getTitle());
				}
			}
			for (City city : townList) {
				if (city.getId() == stockTransfer.getToSource()) {
					sb.append("-");
					sb.append("to");
					sb.append("-");
					sb.append(city.getTitle());
				}
			}
		} else if (stockTransfer.getTransferType().equalsIgnoreCase(
				"TOWN_TO_TOWN")) {
			List<City> senderTownList = null;
			townList = citiesDao.getCitiesByRegionId(stockTransfer
					.getReceiverTerritory());
			senderTownList = citiesDao.getCitiesByRegionId(stockTransfer
					.getSenderTerritory());
			for (City city : senderTownList) {
				if (city.getId() == stockTransfer.getFromSource()) {
					sb.append(city.getTitle());
				}
			}
			for (City city : townList) {
				if (city.getId() == stockTransfer.getToSource()) {
					sb.append("-");
					sb.append("to");
					sb.append("-");
					sb.append(city.getTitle());
				}
			}

		} else if (stockTransfer.getTransferType().equalsIgnoreCase(
				"TOWN_TO_TERRITORY")) {
			List<City> senderTownList = null;
			regionlist = portalDao.getRegionsForPortal(0);
			senderTownList = citiesDao.getCitiesByRegionId(stockTransfer
					.getSenderTerritory());
			for (City city : senderTownList) {
				if (city.getId() == stockTransfer.getFromSource()) {
					sb.append(city.getTitle());
				}
			}
			for (Region region : regionlist) {
				if (region.getId() == stockTransfer.getToSource()) {
					sb.append("-");
					sb.append("to");
					sb.append("-");
					sb.append(region.getTitle());
				}
			}

		} else if (stockTransfer.getTransferType().equalsIgnoreCase(
				"FACTORY_TO_TERRITORY")) {
			String factoryType = "FACTORY";
			factoryList = portalDao.getFactoriesForPortal(factoryType);
			regionlist = portalDao.getRegionsForPortal(0);
			for (Factory factory : factoryList) {
				if (factory.getId() == stockTransfer.getFromSource()) {
					sb.append(factory.getTitle());
				}
			}
			for (Region region : regionlist) {
				if (region.getId() == stockTransfer.getToSource()) {
					sb.append("-");
					sb.append("to");
					sb.append("-");
					sb.append(region.getTitle());
				}
			}

		} else if (stockTransfer.getTransferType().equalsIgnoreCase(
				"FACTORY_TO_TOWN")) {
			String factoryType = "FACTORY";
			factoryList = portalDao.getFactoriesForPortal(factoryType);
			townList = citiesDao.getCitiesByRegionId(stockTransfer
					.getReceiverTerritory());
			for (Factory factory : factoryList) {
				if (factory.getId() == stockTransfer.getFromSource()) {
					sb.append(factory.getTitle());
				}
			}
			for (City city : townList) {
				if (city.getId() == stockTransfer.getToSource()) {
					sb.append("-");
					sb.append("to");
					sb.append("-");
					sb.append(city.getTitle());
				}
			}
		} else if (stockTransfer.getTransferType().equalsIgnoreCase(
				"FACTORY_INTAKE")) {
			String factoryType = "FACTORY";
			factoryList = portalDao.getFactoriesForPortal(factoryType);
			for (Factory factory : factoryList) {
				if (factory.getId() == stockTransfer.getToSource()) {
					sb.append(factory.getTitle());
				}
			}
			sb.append("-");
			sb.append(stockTransfer.getTransferType().split("_")[1]);
		} else if (stockTransfer.getTransferType().equalsIgnoreCase(
				"WAREHOUSE_TO_TERRITORY")) {
			String factoryType = "WAREHOUSE";
			wareHouseList = portalDao.getFactoriesForPortal(factoryType);
			regionlist = portalDao.getRegionsForPortal(0);
			for (Factory wareHouse : wareHouseList) {
				if (wareHouse.getId() == stockTransfer.getFromSource()) {
					sb.append(wareHouse.getTitle());
				}
			}
			for (Region region : regionlist) {
				if (region.getId() == stockTransfer.getToSource()) {
					sb.append("-");
					sb.append("to");
					sb.append("-");
					sb.append(region.getTitle());
				}
			}

		} else if (stockTransfer.getTransferType().equalsIgnoreCase(
				"WAREHOUSE_TO_TOWN")) {
			String factoryType = "WAREHOUSE";
			wareHouseList = portalDao.getFactoriesForPortal(factoryType);
			townList = citiesDao.getCitiesByRegionId(stockTransfer
					.getReceiverTerritory());
			for (Factory wareHouse : wareHouseList) {
				if (wareHouse.getId() == stockTransfer.getFromSource()) {
					sb.append(wareHouse.getTitle());
				}
			}
			for (City city : townList) {
				if (city.getId() == stockTransfer.getToSource()) {
					sb.append("-");
					sb.append("to");
					sb.append("-");
					sb.append(city.getTitle());
				}
			}
		} else if (stockTransfer.getTransferType().equalsIgnoreCase(
				"TERRITORY_TO_FACTORY")) {
			String factoryType = "FACTORY";
			factoryList = portalDao.getFactoriesForPortal(factoryType);
			regionlist = portalDao.getRegionsForPortal(0);

			for (Region region : regionlist) {
				if (region.getId() == stockTransfer.getFromSource()) {

					sb.append(region.getTitle());
				}
			}
			for (Factory factory : factoryList) {
				if (factory.getId() == stockTransfer.getToSource()) {
					sb.append("-");
					sb.append("to");
					sb.append("-");
					sb.append(factory.getTitle());
				}
			}

		} else if (stockTransfer.getTransferType().equalsIgnoreCase(
				"TERRITORY_TO_WAREHOUSE")) {
			String factoryType = "WAREHOUSE";
			wareHouseList = portalDao.getFactoriesForPortal(factoryType);
			regionlist = portalDao.getRegionsForPortal(0);

			for (Region region : regionlist) {
				if (region.getId() == stockTransfer.getFromSource()) {

					sb.append(region.getTitle());
				}
			}
			for (Factory warehouse : wareHouseList) {
				if (warehouse.getId() == stockTransfer.getToSource()) {
					sb.append("-");
					sb.append("to");
					sb.append("-");
					sb.append(warehouse.getTitle());
				}
			}

		} else if (stockTransfer.getTransferType().equalsIgnoreCase(
				"WAREHOUSE_TO_FACTORY")) {
			String factoryType = "WAREHOUSE";
			String factoryType1 = "FACTORY";

			wareHouseList = portalDao.getFactoriesForPortal(factoryType);
			factoryList = portalDao.getFactoriesForPortal(factoryType1);
			for (Factory wareHouse : wareHouseList) {
				if (wareHouse.getId() == stockTransfer.getFromSource()) {
					sb.append(wareHouse.getTitle());
				}
			}
			for (Factory factory : factoryList) {
				if (factory.getId() == stockTransfer.getToSource()) {
					sb.append("-");
					sb.append("to");
					sb.append("-");
					sb.append(factory.getTitle());
				}
			}

		}

		sb.append("-");
		sb.append(stockTransfer.getTransferDate());
		stockTransfer.setTransferCode(sb.toString());

		transfered_stock_submitted_id = portalDao
				.saveTransferedStockData(stockTransfer);

		if (transfered_stock_submitted_id == (int) transfered_stock_submitted_id
				&& stockTransfer.getTransferedStock().size() > 0) {

			portalDao.insertStockTransferDetails(transfered_stock_submitted_id,
					stockTransfer.getTransferedStock());

			if (stockTransfer.getTransferType().equalsIgnoreCase(
					"TERRITORY_TO_TERRITORY")
					|| stockTransfer.getTransferType().equalsIgnoreCase(
							"TERRITORY_TO_TOWN")
					|| stockTransfer.getTransferType().equalsIgnoreCase(
							"TERRITORY_TO_FACTORY")
					|| stockTransfer.getTransferType().equalsIgnoreCase(
							"TERRITORY_TO_WAREHOUSE")) {

				portalDao.updateTerritoryStocksAfterSentStock(
						stockTransfer.getFromSource(),
						stockTransfer.getTransferedStock());

			} else if (stockTransfer.getTransferType().equalsIgnoreCase(
					"TOWN_TO_TOWN")
					|| stockTransfer.getTransferType().equalsIgnoreCase(
							"TOWN_TO_TERRITORY")) {

				portalDao.updateTownStocksAfterSentStock(
						stockTransfer.getFromSource(),
						stockTransfer.getTransferedStock());

			} else if (stockTransfer.getTransferType().equalsIgnoreCase(
					"FACTORY_TO_TERRITORY")
					|| stockTransfer.getTransferType().equalsIgnoreCase(
							"FACTORY_TO_TOWN")
					|| stockTransfer.getTransferType().equalsIgnoreCase(
							"FACTORY_TO_WAREHOUSE")
					|| stockTransfer.getTransferType().equalsIgnoreCase(
							"WAREHOUSE_TO_TERRITORY")
					|| stockTransfer.getTransferType().equalsIgnoreCase(
							"WAREHOUSE_TO_TOWN")
					|| stockTransfer.getTransferType().equalsIgnoreCase(
							"WAREHOUSE_TO_FACTORY")) {

				portalDao.updateFactoryStocksAfterSentStock(
						stockTransfer.getFromSource(),
						stockTransfer.getTransferedStock());

			} else if (stockTransfer.getTransferType().equalsIgnoreCase(
					"FACTORY_INTAKE")) {

				portalDao.updateFactoryStocksAfterIntakeStock(
						stockTransfer.getToSource(),
						stockTransfer.getTransferedStock());
				portalDao.updateReceivedStockDetails(
						transfered_stock_submitted_id,
						stockTransfer.getTransferedStock());
			}

		} else {
			throw new Exception("Stock Not transfered successfully.");
		}

	}

	@Override
	public List<Map<String, Object>> getImeisforPortal() {
		return portalDao.getImeisForPortal();
	}

	// By Muhammad Hamed
	@Override
	public Map<String, Object> updateImeiFromPortal(String imei,
			String imeiStatus) {
		return portalDao.updateImeiStatus(imei, imeiStatus);
	}

	@Override
	public List<Map<String, Object>> getFamiliesForPortal() {
		return familiesDao.getFamiliesForPortal();
	}

	@Override
	public List<Map<String, Object>> getDsrSaleTargetforPortal(Integer month,
			Integer year, Integer regionId, Integer townId, Integer zoneId,
			Integer type) {
		return portalDao.getDsrSaleTarget(month, year, regionId, townId,
				zoneId, type);
	}

	@Override
	public List<Map<String, Object>> getTerritoryFamiliesForPortal(
			Integer territoryId) {
		return null;
	}

	@Override
	public List<Map<String, Object>> getTownFamiliesForPortal(Integer townId) {
		return null;
	}

	@Override
	public List<Map<String, Object>> getTerritoryReceivedStock(
			String transferType, Integer territoryId, String transferCode) {
		return portalDao.getTerritoryReceivedStock(transferType, territoryId,
				transferCode);
	}

	@Override
	public List<Map<String, Object>> getTerritoryReceivedStockTransferCodes(
			String transferType, Integer territoryId) {
		return null;
	}

	@Override
	public void submitReceivedStock(StockTransfer stockTransfer)
			throws Exception {

		this.submitReceivedStockInTransaction(stockTransfer);

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void submitReceivedStockInTransaction(StockTransfer stockTransfer)
			throws Exception {

		try {
			if (portalDao.updateReceivedStock(stockTransfer)
					&& stockTransfer.getTransferedStock().size() > 0) {

				portalDao.updateReceivedStockDetails(
						stockTransfer.getTransferId(),
						stockTransfer.getTransferedStock());
				// discussion required why this method is called
				TransferCodeDetails transferCodeDetails = portalDao
						.getTransitDetails(stockTransfer.getTransferCode());

				if (stockTransfer.getTransferType().equalsIgnoreCase(
						"TERRITORY_TO_TERRITORY")
						|| stockTransfer.getTransferType().equalsIgnoreCase(
								"TOWN_TO_TERRITORY")) {

					portalDao.updateTerritoryStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());

				} else if (stockTransfer.getTransferType().equalsIgnoreCase(
						"TERRITORY_TO_TOWN")
						|| stockTransfer.getTransferType().equalsIgnoreCase(
								"TOWN_TO_TOWN")
						|| stockTransfer.getTransferType().equalsIgnoreCase(
								"WAREHOUSE_TO_TOWN")
						|| stockTransfer.getTransferType().equalsIgnoreCase(
								"FACTORY_TO_TOWN")) {

					portalDao.updateTownStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());

				} else if (stockTransfer.getTransferType().equalsIgnoreCase(
						"FACTORY_TO_TERRITORY")
						|| stockTransfer.getTransferType().equalsIgnoreCase(
								"WAREHOUSE_TO_TERRITORY")) {

					portalDao.updateTerritoryStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());

				}
				/*
				 * else if (stockTransfer.getTransferType().equalsIgnoreCase(
				 * "FACTORY_TO_TOWN")) {
				 * 
				 * portalDao.updateTownStocksAfterReceiveStock(
				 * transferCodeDetails.getReceiverLocationId(),
				 * stockTransfer.getTransferedStock()); }
				 */
				else if (stockTransfer.getTransferType().equalsIgnoreCase(
						"FACTORY_TO_WAREHOUSE")) {

					portalDao.updateWareHouseStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());
				} else if (stockTransfer.getTransferType().equalsIgnoreCase(
						"TERRITORY_TO_FACTORY")) {

					portalDao.updateWareHouseStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());
				} else if (stockTransfer.getTransferType().equalsIgnoreCase(
						"TERRITORY_TO_WAREHOUSE")
						|| stockTransfer.getTransferType().equalsIgnoreCase(
								"WAREHOUSE_TO_FACTORY")) {

					portalDao.updateWareHouseStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());
				}

			} else {
				throw new Exception("Received Stock Was Empty");
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public Map<String, Object> checkTransferCodeAvailability(String transferCode) {

		return null;
	}

	@Override
	public List<Map<String, Object>> getReceivedStockCodes(
			HttpServletRequest request) {
		// This method is not correct Please discuss
		int tmCode = 0;
		if (request.getHeader("surveyorId") != null) {

			tmCode = Integer.parseInt(request.getHeader("surveyorId"));
		}
		return portalDao.getReceivedStockCodes(tmCode);
	}

	@Override
	public List<Map<String, Object>> getReceivedStockCodesForPortal(
			HttpServletRequest request) {
		int adminCode = 0;
		if (request.getHeader("surveyorId") != null) {

			adminCode = Integer.parseInt(request.getHeader("surveyorId"));
		}
		return portalDao.getReceivedStockCodesForPortal(adminCode);
	}

	@Override
	public TransferCodeDetails getTransitDetails(String transitCode) {

		// ??????
		TransferCodeDetails transferCodeDetails = null;
		TransferCodeDetails transferCodeDetails_V2 = null;
		transferCodeDetails = portalDao.getTransitDetails(transitCode);

		transferCodeDetails_V2 = portalDao.getTransitDetails_V2(transitCode,
				transferCodeDetails.getTransferType());
		transferCodeDetails.setSender(transferCodeDetails_V2.getSender());
		transferCodeDetails.setReceiver(transferCodeDetails_V2.getReceiver());
		transferCodeDetails.setTransferCode(transitCode);

		return transferCodeDetails;

	}

	@Override
	public List<Map<String, Object>> getReceivedStock(Integer transferId) {

		return portalDao.getReceivedStock(transferId);
	}

	@Override
	public List<Map<String, Object>> getFactoryStockFamilies(Integer factoryId) {

		return familiesDao.getFactoryStockFamiliesForPortal(factoryId);
	}

	@Override
	public List<Map<String, Object>> getRegionsByZoneId(Integer zoneId) {
		// Should be from cache
		return regionsDao.getRegionsByZoneId(zoneId);
	}

	@Override
	public List<Map<String, Object>> getRegionsByZoneIdNew(String zoneId) {
		// Should be from cache
		return regionsDao.getRegionsByZoneIdNew(zoneId);
	}

	@Override
	public List<Map<String, Object>> getDistributionList(Integer regionId,
			Integer cityId) {

		return portalDao.getDistributionList(regionId, cityId);
	}

	@Override
	public Map<String, Object> updateDistribution(Integer distributionId,
			double distLat, double distLong, String distName, Integer totalDE,
			Integer totalDSR, Integer investment, String distType,
			Integer regionId, Integer cityId) {

		return portalDao.updateDistribution(distributionId, distLat, distLong,
				distName, totalDE, totalDSR, investment, distType, regionId,
				cityId);
	}

	@Override
	public List<Map<String, Object>> getBlockWiseShopList(Integer areaId,
			Integer regionId, Integer surveyorId, Integer dsrId) {

		return portalDao.getBlockWiseShopList(areaId, regionId, surveyorId,
				dsrId);
	}

	@Override
	public List<Map<String, Object>> getSurveyorList() {

		return portalDao.getSurveyorList();
	}

	@Override
	public List<Factory> getFactoryList(String type) {
		return portalDao.getFactoriesForPortal(type);
	}

	@Override
	public List<Map<String, Object>> getDeAttendanceList(Integer regionId,
			String startDate, String endDate, String attendanceType,
			Integer zoneId) {

		return portalDao.getDeAttendanceDataList(regionId, startDate, endDate,
				attendanceType, zoneId);

	}

	@Override
	public List<Map<String, Object>> getRemarks(String typeId) {

		return portalDao.getRemarks(typeId);
	}

	@Override
	public Map<String, Object> updateWorkType(Integer id, Integer workTypeId,
			Integer userId) {

		return portalDao.updateWorkType(id, workTypeId, userId);
	}

	// By Hamed
	@Override
	public List<Map<String, Object>> getTmProductivityNDN(Integer regionId,
			Integer zoneId, Integer cityId, Integer distributionId,
			String storeType, Integer channelId, String startDate,
			String endDate, Integer rteId, Integer surveyorId, String zones,
			String regions) {

		return surveyorDao.getTmProductivity(regionId, zoneId, cityId,
				distributionId, storeType, channelId, startDate, endDate,
				rteId, surveyorId, zones, regions);
	}

	@Override
	public Map<String, Object> insertEvaluationScore(JSONObject json) {
		// List<Criteria> criteriaList = null;
		// criteriaList = evaluationDao.getCriteriaList();
		return portalDao.insertEvaluationScore(json);

	}

	@Override
	public List<Map<String, Object>> getEmployeeList(Integer regionId) {
		return portalDao.getEmployeeList(regionId);
	}

	@Override
	public List<Map<String, Object>> getDsrs(Integer surveyorId, String dsrType) {

		return portalDao.getDsrsBySurveyorId(surveyorId, dsrType);
	}

	@Override
	public List<Map<String, Object>> getDsrsByRegionId(Integer regionId) {

		return portalDao.getDsrsByRegionId(regionId);
	}

	@Override
	public Map<String, Object> transferBlocks(String blocks, Integer dsrId,
			Integer userId) {
		// we need to transfer route tagging of block to new dsr
		// checking if route tagging exists for new dsr
		List<Integer> list = portalDao.getPreDsrRoutesDays(dsrId, 1);
		if (list.isEmpty()) {
			// insert route_tagging
			portalDao.insertDsrRoute(dsrId);
		}
		// update block tagging against new dsr
		portalDao.updateDsrBlockTagging(dsrId, blocks.split(","));
		portalDao.inactiveDsrBlocks(blocks, dsrId, userId);
		Map<String, Object> res = portalDao.transferBlocks(blocks, dsrId,
				userId);

		return res;
	}

	@Override
	public Map<String, Object> transferShops(String shops, Integer areaId,
			Integer userId) {
		return portalDao.transferShops(shops, areaId, userId);

	}

	@Override
	public List<Map<String, Object>> getQueryTypeList() {
		return portalDao.getQueryTypeList();
	}

	@Override
	public List<Map<String, Object>> getMerchandiserDsrSalesList(
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer familyId) {
		return portalDao.getMerchandiserDsrSalesList(startDate, endDate,
				zoneId, regionId, surveyorId, dsrId, familyId);
	}

	@Override
	public List<Map<String, Object>> getMerchandiserSpotSellingList(
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer familyId) {
		return portalDao.getMerchandiserSpotSellingList(startDate, endDate,
				zoneId, regionId, surveyorId, dsrId, familyId);
	}

	@Override
	public List<Map<String, Object>> getMerchandiserDsrShopSalesList(
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer familyId,
			String promotionType, String schemeTitle) {
		return portalDao.getMerchandiserDsrShopSalesList(startDate, endDate,
				zoneId, regionId, surveyorId, dsrId, familyId, promotionType,
				schemeTitle);
	}

	@Override
	public Map<String, Object> updateDsrSales(Integer dsrSaleId,
			float retailValue, float wholeSaleValue, Integer userId) {
		return portalDao.updateDsrSales(dsrSaleId, retailValue, wholeSaleValue,
				userId);
	}

	@Override
	public Map<String, Object> updateSpotSellingValue(Integer spotsellingId,
			float value, Integer userId) {
		return portalDao.updateSpotSellingValue(spotsellingId, value, userId);
	}

	@Override
	public Map<String, Object> updateDsrShopSales(Integer dsrSaleId,
			float saleValue, Integer userId, String promoType,
			float discountValue) {
		return portalDao.updateDsrShopSales(dsrSaleId, saleValue, userId,
				promoType, discountValue);
	}

	@Override
	public List<TransferCodeDetails> getTransactionsData(String startDate,
			String endDate, String transferType) {
		return portalDao.getStockTransactionsData(startDate, endDate,
				transferType);
	}

	@Override
	public List<Map<String, Object>> getTransactionStockDetails(
			Integer transferId) {

		return portalDao.getTransactionStockDetails(transferId);
	}

	@Override
	public List<Surveyor> getDeListByRegionId(Integer regionId, Integer zoneId,
			String surveyorType) {
		return surveyorDao.getDeListByRegionId(regionId, zoneId, surveyorType);
	}

	@Override
	public Map<String, Object> saveDSRSale(String date, Integer surveyorId,
			Integer dsrId, Integer familyId, float retailValue,
			float wholeSaleValue, Integer cityId) {
		Map<String, Object> res = new HashMap<String, Object>();
		boolean isExisting = portalDao.isDSRSaleRecordExisting(date,
				surveyorId, dsrId, familyId, cityId);
		if (isExisting) {
			res.put("status", true);
			res.put("message", "Already exist.");
			res.put("title", "Error");
		} else {
			portalDao.saveDSRSale(date, surveyorId, dsrId, familyId,
					retailValue, wholeSaleValue, cityId);
			res.put("status", false);
			res.put("title", "Success");
			res.put("message", "Record saved.");
		}
		return res;
	}

	public void reverseTransaction(Integer transactionId) throws Exception {

		try {
			this.reverseStockTransactionInTransaction(transactionId);
		} catch (Exception ex) {
			throw ex;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void reverseStockTransactionInTransaction(Integer transactionId)
			throws Exception {
		StockTransfer stockTransfer = new StockTransfer();
		TransferCodeDetails transferCodeDetails;
		transferCodeDetails = portalDao
				.getTransitDetailsByTransactionId(transactionId);
		stockTransfer.setTransferedStock(portalDao
				.getTransferedStockData(transactionId));

		if (transferCodeDetails.getTransferType().equalsIgnoreCase(
				StockConstants.FACTORY_TO_WAREHOUSE)
				|| transferCodeDetails.getTransferType().equalsIgnoreCase(
						StockConstants.FACTORY_TO_TERRITORY)
				|| transferCodeDetails.getTransferType().equalsIgnoreCase(
						StockConstants.FACTORY_TO_TOWN)) {

			portalDao.updateFactoryStocksAfterIntakeStock(
					transferCodeDetails.getSenderLocationId(),
					stockTransfer.getTransferedStock());

		} else if (transferCodeDetails.getTransferType().equalsIgnoreCase(
				StockConstants.WAREHOUSE_TO_TERRITORY)
				|| transferCodeDetails.getTransferType().equalsIgnoreCase(
						StockConstants.WAREHOUSE_TO_TOWN)) {
			portalDao.updateFactoryStocksAfterIntakeStock(
					transferCodeDetails.getSenderLocationId(),
					stockTransfer.getTransferedStock());

		} else if (transferCodeDetails.getTransferType().equalsIgnoreCase(
				StockConstants.TERRITORY_TO_TERRITORY)
				|| transferCodeDetails.getTransferType().equalsIgnoreCase(
						StockConstants.TERRITORY_TO_TOWN)) {

			portalDao.updateTerritoryStocksAfterReceiveStock(
					transferCodeDetails.getSenderLocationId(),
					stockTransfer.getTransferedStock());
		} else if (transferCodeDetails.getTransferType().equalsIgnoreCase(
				StockConstants.TOWN_TO_TOWN)
				|| transferCodeDetails.getTransferType().equalsIgnoreCase(
						StockConstants.TOWN_TO_TERRITORY)) {

			portalDao.updateTownStocksAfterReceiveStock(
					transferCodeDetails.getSenderLocationId(),
					stockTransfer.getTransferedStock());
		}
		portalDao.updateTransactionAfterReveresed(transactionId);
	}

	@Override
	public List<Map<String, Object>> getDSRsByDEId(Integer deId) {
		return portalDao.getDSRsByDEId(deId);
	}

	@Override
	public List<Attendance> getUsersForAttendanceApprovals(String userType,
			String selectedDate, Integer zoneId, Integer regionId) {

		return portalDao.getUsersForAttendanceApprovals(userType, selectedDate,
				zoneId, regionId);
	}

	@Override
	public void UpdateAttendanceForApprovals(
			ArrayList<Attendance> approvedAttendance, String selectedDate,
			Integer surveyorId) throws Exception {
		try {
			surveyorDao.updateAttendance(surveyorId, approvedAttendance);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<Map<String, Object>> getShopsForDETracking(Integer zoneId,
			Integer regionId, Integer surveyorId, Integer dsrId,
			String selectedDate, String mapViewType, String startDate,
			String endDate, String spentTime) {

		return shopsDao
				.getShopsForDeTracking(zoneId, regionId, surveyorId, dsrId,
						selectedDate, mapViewType, startDate, endDate,
						spentTime);
	}

	@Override
	public List<Surveyor> getDeListByRegionIdForDETracking(Integer zoneId,
			Integer regionId, String mapViewType, String selectedDate) {
		return surveyorDao.getDEListByRegionIdForDeTracking(regionId, zoneId,
				mapViewType, selectedDate);
	}

	@Override
	public List<Map<String, Object>> getDsrListBySurveyorIdForDeTracking(
			Integer surveyorId, String mapViewType, String selectedDate) {
		return surveyorDao.getDsrsBySurveyorIdForDETracking(surveyorId,
				mapViewType, selectedDate);
	}

	@Override
	public Map<String, Object> insertCityFromPortal(Integer regionId,
			String cityName, Integer cityId) {
		Map<String, Object> res = new HashMap<String, Object>();
		if (citiesDao.insertCityFromPortal(regionId, cityName, cityId) > -1) {
			res.put("status", true);
			res.put("message", "Town Has been Added.");
		} else {
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	public Map<String, Object> updateCityFromPortal(Integer cityId,
			String cityName) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			if (citiesDao.updateCityName(cityId, cityName)) {
				res.put("status", true);
				res.put("message", "Town Data Has been Changed.");
			} else {
				res.put("status", false);
				res.put("message", "Something went wrong..");
			}
		} catch (Exception e) {

			logger.debug(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	public AdminProfile findUserById(Integer userId) {
		AdminProfile ap = null;
		try {
			if (userId > 0) {
				ap = portalDao.getPortalAdminDetails(userId).get(0);
			}
		} catch (Exception e) {

		}
		return ap;
	}

	@Override
	public List<Map<String, Object>> getDeListByRegionIdNew(Integer zoneId,
			String regionId, String surveyorType, String status) {

		return portalDao
				.getSurveyorList(zoneId, regionId, surveyorType, status);
	}

	@Override
	public List<Map<String, Object>> getEmployeeList(String surveyorType,
			String mustHave) {

		return surveyorDao
				.getEmployeeListBySurveyorType(surveyorType, mustHave);
	}

	@Override
	public Map<String, Object> insertEmployeeFromPortal(
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> res = new HashMap<String, Object>();
		boolean isMultiPartRequest = ServletFileUpload
				.isMultipartContent(request);
		Employee employee = null;
		Gson gson = new Gson();
		try {

			if (isMultiPartRequest) {
				ServletFileUpload servletFileUpload = new ServletFileUpload();
				FileItemIterator fileItemIterator = servletFileUpload
						.getItemIterator(request);
				while (fileItemIterator.hasNext()) {
					FileItemStream fileItemStream = fileItemIterator.next();
					if (fileItemStream.isFormField()) {
						InputStream stream = fileItemStream.openStream();
						String reader = Streams.asString(stream);
						employee = gson.fromJson(reader, Employee.class);
					} else
						this.saveSelfieImage(employee, fileItemStream);
				}
			}

		} catch (Exception e) {
			logger.info(e, e);
		}
		try {
			if (surveyorDao.insertEmployeeFromPortal(employee) > 0) {
				res.put("status", true);
				res.put("message", "Employee has been added into system.");
			} else {
				res.put("status", false);
				res.put("message", "Something went wrong..");
			}
		} catch (Exception e) {

			logger.debug(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	public List<Map<String, Object>> getAvailableEmployeeListForDE(
			String surveyorType) {
		return surveyorDao.getAvailableEmployeeListForDE(surveyorType);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void insertDeFromPortal(String regionId, Integer employeeId,
			String deCode, String employeeType, Integer userId,
			float basicSalary, float dailyAllownce, float fuelAllownce,
			Float maintenanceAllownce, Float otherAllowance) throws Exception {
		logger.info("Saving surveyor data into db");
		insertSurveyorFromPortal(regionId, employeeId, deCode, employeeType,
				userId, basicSalary, dailyAllownce, fuelAllownce,
				maintenanceAllownce, otherAllowance);
	}

	private void insertSurveyorFromPortal(String regionId, Integer employeeId,
			String deCode, String employeeType, Integer userId,
			float basicSalary, float dailyAllownce, float fuelAllownce,
			Float maintenanceAllownce, Float otherAllowance) throws Exception {

		int surveyorId = surveyorDao.insertDeFromPortal(regionId, employeeId,
				deCode, employeeType, userId, basicSalary, dailyAllownce,
				fuelAllownce, maintenanceAllownce, otherAllowance);

		if (EmployeeType.TM.equalsIgnoreCase(employeeType)
				|| EmployeeType.SIS.equalsIgnoreCase(employeeType)) {
			surveyorDao.insertTmRegions(regionId, surveyorId);

		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void updateSurveyorRegion(String regionId, Integer surveyorId)
			throws Exception {
		logger.info("Saving surveyor data into db");
		insertTmRegions(regionId, surveyorId);
	}

	private void insertTmRegions(String regionId, Integer surveyorId)
			throws Exception {

		surveyorDao.insertTmRegions(regionId, surveyorId);

	}

	@Override
	public List<Map<String, Object>> getSecondarySale(Integer regionId,
			Integer cityId, Integer brandId, Integer month, Integer year) {
		return portalDao.getSecondarySale(regionId, cityId, brandId, month,
				year);

	}

	@Override
	public List<Map<String, Object>> getPrimarySale(Integer regionId,
			Integer cityId, Integer brandId, Integer month, Integer year) {
		return portalDao.getPrimarySale(regionId, cityId, brandId, month, year);
	}

	@Override
	public Map<String, Object> updatePasswordFromPortal(String oldPassword,
			String newPassword, Integer userId) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		try {
			map = (HashMap<String, Object>) this
					.updatePasswordFromPortalIntransaction(oldPassword,
							newPassword, userId);
			return map;
		} catch (Exception e) {
			logger.error("Exception Occured while updating password : Exception :"
					+ e);
			return map;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private Map<String, Object> updatePasswordFromPortalIntransaction(
			String oldPassword, String newPassword, Integer userId)
			throws Exception {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		AdminProfile adminProfile = portalDao.getPasswordHashByUserName(null,
				userId);
		if (PasswordStorage.verifyPassword(oldPassword,
				adminProfile.getPassword())) {
			if (portalDao.updatePassword(userId,
					PasswordStorage.createHash(newPassword)) > 0) {
				resp.put("status", true);
				resp.put("message", "Password Successfully Updated...!");
			} else {
				resp.put("status", false);
				resp.put("message",
						"Somthing Went Wrong during update password...!");
			}
		} else {
			resp.put("status", false);
			resp.put("message", "Wrong Old Password..");
		}

		return resp;

	}

	@Override
	public ArrayList<AdminProfile> getUsersDataForPasswordUpdation(
			Integer userTypeId) {
		return portalDao.getUsersDataForPasswordUpdation(userTypeId);
	}

	@Override
	public Map<String, Object> updatePasswordByAdmin(String newPassword,
			Integer userId, String userType) {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		try {
			int result = -1;
			if (userType.equalsIgnoreCase("portal")) {
				result = portalDao.updatePassword(userId,
						PasswordStorage.createHash(newPassword));
			} else if (userType.equalsIgnoreCase("app")) {
				result = surveyorDao.updatePassword(userId,
						PasswordStorage.createHash(newPassword));
			} else if (userType.equalsIgnoreCase("dsrApp")) {
				result = dsrDao.updateDsrPassword(userId,
						PasswordStorage.createHash(newPassword));
			}

			if (result > 0) {
				resp.put("status", true);
				resp.put("message", "Password Successfully Updated...!");
			} else {
				resp.put("status", false);
				resp.put("message",
						"Somthing Went Wrong during update password...!");
			}
		} catch (CannotPerformOperationException e) {
			logger.error(e, e);
			resp.put("status", false);
			resp.put("message",
					"Somthing Went Wrong during update password...!");
		}

		return resp;
	}

	@Override
	public List<Map<String, Object>> stockTransfersTypes() {
		return portalDao.stockTransfersTypes();
	}

	@Override
	public List<Map<String, Object>> getAdminRole() {
		return portalDao.getAdminRole();
	}

	@Override
	public Map<String, Object> getTableauTicket(HttpServletRequest request,
			HttpServletResponse response, String type) {

		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
			// ipAddress = "124.29.217.144";
		}
		logger.info("Remote Clinet ip :: " + ipAddress);
		String ticket = getTrustedTicket("https://tableau.rtdtradetracker.com",
				"", ipAddress);

		String tableauPath = portalDao.getTableauDashboardUrl(type);
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			data.put("ticket", ticket);
			data.put("tableauData", getTableauProperties());
			data.put("tableauPath", tableauPath);
			logger.info("Sending Ticket to Client : " + ticket);

		} catch (Exception e) {
			logger.error(e, e);
		}

		return data;
	}

	private Map<String, String> getTableauProperties() {
		Map<String, String> map = new HashMap<String, String>();
		if (!StringUtils.isNullOrEmptyString(bundle
				.getString(SysConstants.TABLEAU_URL))) {
			map.put(SysConstants.TABLEAU_URL,
					bundle.getString(SysConstants.TABLEAU_URL));
		}
		if (!StringUtils.isNullOrEmptyString(bundle
				.getString(SysConstants.TABLEAU_IFRAME))) {
			map.put(SysConstants.TABLEAU_IFRAME,
					bundle.getString(SysConstants.TABLEAU_IFRAME));
		}
		if (!StringUtils.isNullOrEmptyString(bundle
				.getString(SysConstants.TABLEAU_EMBED))) {
			map.put(SysConstants.TABLEAU_EMBED,
					bundle.getString(SysConstants.TABLEAU_EMBED));
		}
		if (!StringUtils.isNullOrEmptyString(bundle
				.getString(SysConstants.TABLEAU_DISPLAY_COUNT))) {
			map.put(SysConstants.TABLEAU_DISPLAY_COUNT,
					bundle.getString(SysConstants.TABLEAU_DISPLAY_COUNT));
		}
		if (!StringUtils.isNullOrEmptyString(bundle
				.getString(SysConstants.TABLEAU_APP_BANNER))) {
			map.put(SysConstants.TABLEAU_APP_BANNER,
					bundle.getString(SysConstants.TABLEAU_APP_BANNER));
		}
		if (!StringUtils.isNullOrEmptyString(bundle
				.getString(SysConstants.TABLEAU_VIZ_HOME))) {
			map.put(SysConstants.TABLEAU_VIZ_HOME,
					bundle.getString(SysConstants.TABLEAU_VIZ_HOME));
		}

		return map;
	}

	private String getTrustedTicket(String server, String targetSite,
			String remoteAddr) {
		StringBuffer reps = new StringBuffer("");
		URL url;
		try {

			url = new URL(server + "/trusted");

			HttpURLConnection req = (HttpURLConnection) url.openConnection();
			req.setRequestMethod("POST");

			String urlParameters = "server="
					+ URLEncoder.encode(server, "UTF-8") + "&username="
					+ bundle.getString(SysConstants.TABLEAU_USERNAME)
					+ "&client_ip=" + remoteAddr;

			logger.info("urlParameters" + urlParameters);
			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;
			req.setRequestProperty("Content-Length",
					Integer.toString(postDataLength));
			req.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			req.setRequestProperty("charset", "UTF-8");
			req.setUseCaches(false);
			req.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(req.getOutputStream());
			wr.write(postData);
			int respcode = req.getResponseCode();
			logger.info("respcode" + respcode);
			if (respcode == 200) {
				String inputline;
				BufferedReader in = new BufferedReader(new InputStreamReader(
						req.getInputStream()));
				while ((inputline = in.readLine()) != null) {
					reps.append(inputline);

				}
				logger.info("reps" + reps.toString());
				in.close();
			} else {
				return "No Ticket Found";
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		return reps.toString();
	}

	@Override
	public Map<String, Object> saveUserRole(String title,
			HttpServletResponse response) {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		try {
			if (portalDao.saveUserRole(title)) {
				resp.put("status", true);
				resp.put("message", "User role saved...!");
			} else {
				resp.put("status", false);
				resp.put("message", "User role not saved...!");
			}
		} catch (Exception e) {
			logger.error(e, e);
			resp.put("status", false);
			resp.put("message", "Somthing went wrong, please try later...!");
		}

		return resp;
	}

	@Override
	public Map<String, Object> updateUserRoleStatus(Integer roleId,
			String status, HttpServletResponse response) {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		try {
			if (portalDao.updateUserRoleStatus(roleId, status)) {
				resp.put("status", true);
				resp.put("message", "User role updated...!");
			} else {
				resp.put("status", false);
				resp.put("message", "User role not updated...!");
			}
		} catch (Exception e) {
			logger.error(e, e);
			resp.put("status", false);
			resp.put("message", "Somthing went wrong, please try later...!");
		}

		return resp;
	}

	@Override
	public Map<String, Object> getMenuList(Integer roleId) {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		resp.put("asignedList", portalDao.getAsignedMenuList(roleId));
		resp.put("menuList", portalDao.getMenuList(roleId));
		return resp;
	}

	@Override
	public Map<String, Object> updateMenuList(String menus, Integer roleId,
			Integer act) {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		if (1 == act && portalDao.updateUnAssignedMenuList(menus, roleId)) {
			resp.put("status", true);
			resp.put("message", "Menu list updated...!");
		} else if (2 == act && portalDao.updateAssignedMenuList(menus, roleId)) {
			resp.put("status", true);
			resp.put("message", "Menu list updated...!");

		} else {
			resp.put("status", false);
			resp.put("message", "Menu list not updated...!");
		}

		return resp;
	}

	@Override
	public List<Map<String, Object>> sisSurveyorList(String startDate,
			String endDate, Integer zoneId, Integer regionId, Integer cityId) {
		return portalDao.sisSurveyorList(startDate, endDate, zoneId, regionId,
				cityId);
	}

	@Override
	public Map<String, Object> updateStock(HttpServletRequest request,
			HttpServletResponse response, String stockType, String projectName) {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		boolean status = portalDao.updateStock(stockType, projectName);
		if (status) {
			resp.put("status", true);
			resp.put("message", stockType + " stock updated...!");
		} else {
			resp.put("status", false);
			resp.put("message", stockType + " stock not updated...!");
		}

		return resp;
	}

	@Override
	public List<Map<String, Object>> dsrSurveyorList(String startDate,
			String endDate, Integer zoneId, Integer regionId, Integer cityId,
			String zones, String regions) {
		return portalDao.dsrSurveyorList(startDate, endDate, zoneId, regionId,
				cityId, zones, regions);
	}

	@Override
	public ArrayList<ShopAssetImage> dsrShopList(Integer zoneId,
			Integer regionId, Integer cityId, String startDate, String endDate,
			Integer surveyorId, String visitType, String newShop) {
		return portalDao.dsrShopList(zoneId, regionId, cityId, startDate,
				endDate, surveyorId, visitType, newShop);
	}

	@Override
	public List<Industry> getIndustryList() {
		return applicationCacheService.getIndustries();
	}

	@Override
	public List<Family> getBrandList(Integer industryId) {
		return applicationCacheService.getFamilies(industryId);
	}

	public void createBrand(Integer industryId, String brandTitle,
			Integer brandSticks) throws Exception {
		insertBrand(industryId, brandTitle, brandSticks);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void insertBrand(Integer industryId, String brandTitle,
			Integer brandSticks) throws Exception {
		int brandId = portalDao
				.createBrand(industryId, brandTitle, brandSticks);
		if (industryId == 1) {
			portalDao.insertFactoryStock(brandId);
			portalDao.insertRegionStock(brandId);
			portalDao.insertCityStock(brandId);
		}
	}

	@Override
	public void createRegion(Integer zoneId, String regionTitle)
			throws Exception {
		portalDao.createRegion(zoneId, regionTitle);
	}

	@Override
	public void updateSurveyorStatus(String status, Integer surveyorId,
			String surveyorType, Integer dsrId) throws Exception {
		if (surveyorType.equalsIgnoreCase("DSR")) {
			int res = portalDao.updateDsrStatus(status, dsrId);
			if (res > 0) {
				portalDao.updateSurveyorDsrStatus(status, surveyorId, dsrId);
			}
		} else {
			portalDao.updateSurveyorStatus(status, surveyorId);
		}

	}

	@Override
	public void updateEmpStatus(String status, Integer employeeId)
			throws Exception {

		portalDao.updateEmpStatus(status, employeeId);

	}

	@Override
	public void updateStatus(String Active, Integer Id, Integer userId)
			throws Exception {

		portalDao.updateStatus(Active, Id, userId);
		portalDao.updateFPIStatus();
	}

	@Override
	public void updateDate(String inactiveDate, Integer employeeId)
			throws Exception {

		portalDao.updateDate(inactiveDate, employeeId);

	}

	@Override
	public void updateschemeDate(Integer Id, String Active,
			String inactiveDate, Integer userId) throws Exception {

		portalDao.updateschemeDate(Id, Active, inactiveDate, userId);
		portalDao.updateSchemeStockStatus();

	}

	@Override
	public void deleteSaleTarget(Integer targetId) throws Exception {
		portalDao.deleteSaleTarget(targetId);

	}

	@Override
	public List<Map<String, Object>> getWSTKSSaleTarget(Integer month,
			Integer year, Integer regionId, Integer townId, Integer zoneId,
			String shopType) {
		return portalDao.getWSTKSSaleTarget(month, year, regionId, townId,
				zoneId, shopType);
	}

	@Override
	public void deleteWSTKSSaleTarget(Integer targetId) throws Exception {
		portalDao.deleteWSTKSSaleTarget(targetId);

	}

	@Override
	public List<Map<String, Object>> getMerchandiserWSTKSSalesList(
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, String type, Integer shopId,
			Integer familyId) {
		if (type.equalsIgnoreCase("WS")) {
			return reportsDao.getDeWholesaleData(zoneId, regionId, -1, -1,
					startDate, endDate, surveyorId, shopId, familyId);
		} else {
			return reportsDao.getTksWholesaleData(zoneId, regionId, -1, -1,
					startDate, endDate, surveyorId, shopId, familyId);
		}
	}

	@Override
	public Map<String, Object> updateWSTKSSales(Integer surveyId, int familyId,
			float wholeSaleValue, String type, Integer remarksId,
			String updateType, int userId) {
		Map<String, Object> map = new HashMap<String, Object>();

		int remarkId = portalDao.updateWSTKSSales(surveyId, remarksId);
		if (updateType.equalsIgnoreCase("Sale")) {
			portalDao.updateWSTKSSales(remarkId, surveyId, familyId,
					wholeSaleValue, type, userId);
		} else {
			portalDao.removeWsSales(surveyId, remarkId, userId);
		}

		map.put("status", true);
		map.put("message", "Update successfull");

		return map;
	}

	@Override
	public List<Map<String, Object>> getMerchandiserWSTKSShopList(
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, String type, Integer shopId) {
		return portalDao.getWSTKSShopList(startDate, endDate, zoneId, regionId,
				surveyorId, dsrId, type, shopId);
	}

	@Override
	public Map<String, Object> addNewWSTKSSale(Integer surveyorId,
			Integer userId, String type, Integer shopId, String visitDate,
			Integer dsrId, Integer employeeId, Integer familyId, float value,
			String visitDateTime, Integer remark) {
		return portalDao.addNewWSTKSSale(surveyorId, userId, type, shopId,
				visitDate, dsrId, employeeId, familyId, value, visitDateTime,
				remark);
	}

	@Override
	public List<Shop> getRAShopList(Integer regionId) {
		return portalDao.getRAShopList(regionId);
	}

	@Override
	public void updateSurveyorEmloyee(Integer surveyorId, Integer employeeId,
			Integer userId, float basicSalary, float dailyAllownce,
			float fuelAllownce, Float maintenanceAllownce,
			Float otherAllowance, Integer cityId) throws Exception {
		portalDao.updateSurveyorEmloyee(surveyorId, employeeId, userId,
				basicSalary, dailyAllownce, fuelAllownce, maintenanceAllownce,
				otherAllowance, cityId);

	}

	@Override
	public List<Map<String, Object>> getAreasByRegionId(Integer regionId) {
		return portalDao.getAreasByRegionId(regionId);

	}

	@Override
	public void createUserFromPortal(String username, String password,
			Integer role, String zone, String region, String area)
			throws Exception {
		portalDao.createUserFromPortal(username,
				PasswordStorage.createHash(password), role, zone, region, area);

	}

	@Override
	public List<Map<String, Object>> getDsrListForAttendance(
			Integer surveyorId, String visitDate, Integer territoryId) {
		return portalDao.getDsrListForAttendance(surveyorId, visitDate,
				territoryId);
	}

	@Override
	public Map<String, Object> updateDsrAttendance(Integer id,
			Integer workTypeId, Integer userId, Integer surveyorId,
			String visitDate) {

		return portalDao.updateDsrAttendance(id, workTypeId, userId,
				surveyorId, visitDate);
	}

	@Override
	public List<Map<String, Object>> getWorkingDays(Integer userId,
			Integer month, Integer year) {
		return portalDao.getWorkingDays(userId, month, year);
	}

	@Override
	public Map<String, Object> updateWorkingDays(Integer userId, Integer month,
			Integer year, String value, String date) {
		return portalDao.updateWorkingDays(userId, month, year, value, date);
	}

	@Override
	public List<Industry> getSisIndustryList() {
		return applicationCacheService.getSisIndustries();
	}

	@Override
	public void createSisBrand(Integer industryId, String brandTitle,
			Integer userId) throws Exception {
		portalDao.createSisBrand(industryId, brandTitle, userId);
	}

	@Override
	public List<Family> getSisBrandList(Integer industryId) {
		return applicationCacheService.getSisFamilies(industryId);
	}

	@Override
	public List<Map<String, Object>> getNewSisBrands(Integer industryId,
			Integer zoneId, Integer regionId, Integer cityId,
			Integer surveyorId, String startDate, String endDate) {
		return familiesDao.getNewSisBrands(industryId, zoneId, regionId,
				cityId, surveyorId, startDate, endDate);
	}

	@Override
	public Map<String, Object> updateBrands(String newBrandId,
			Integer systemBrandId, Integer userId) {

		Map<String, Object> map = new HashMap<String, Object>();

		JSONArray jsonArray = new JSONArray(newBrandId);
		StringBuilder sisBrands = new StringBuilder();
		StringBuilder retailBrands = new StringBuilder();
		String type = null;
		String brandId = null;

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			type = jsonObject.optString("type");
			brandId = jsonObject.optString("id");
			if (type.equalsIgnoreCase(CommonConstants.SIS)) {
				sisBrands.append(brandId);
				sisBrands.append(",");
			} else if (type.equalsIgnoreCase(CommonConstants.RETAIL_AUDIT)) {
				retailBrands.append(brandId);
				retailBrands.append(",");
			}
		}

		if (sisBrands.length() > 0) {
			familiesDao.updateNewSISBrands(
					sisBrands.substring(0, sisBrands.length() - 1),
					systemBrandId, userId);
		}
		if (retailBrands.length() > 0) {
			familiesDao.updateNewRaBrands(
					retailBrands.substring(0, retailBrands.length() - 1),
					systemBrandId, userId);
		}

		map.put("status", true);
		map.put("message", "Brand tag successfully");

		return map;
	}

	@Override
	public List<Map<String, Object>> getShopProfile() {
		return portalDao.getShopProfile();
	}

	@Override
	public List<Map<String, Object>> getSisSurveyor(Integer regionId,
			Integer typeId) {
		return portalDao.getSisSurveyor(regionId, typeId);
	}

	@Override
	public void updateTownStatus(String status, Integer cityId, String userId)
			throws Exception {
		portalDao.updateTownStatus(status, cityId, userId);

	}

	@Override
	public void updateSisOldBrands(Integer industryId, Integer familyId)
			throws Exception {
		portalDao.updateSisOldBrands(industryId, familyId);

	}

	@Override
	public List<Map<String, Object>> getSisAttendanceList(String startDate,
			String endDate, Integer surveyorId, Integer typeId) {

		return portalDao.getSisAttendanceDataList(startDate, endDate,
				surveyorId, typeId);

	}

	@Override
	public List<Map<String, Object>> getAreasByTownId(Integer townId) {
		return portalDao.getAreasByTownId(townId);

	}

	@Override
	public Map<String, Object> insertAreaFromPortal(Integer townId,
			String areaName, Integer userId) {
		Map<String, Object> res = new HashMap<String, Object>();
		if (areaDao.insertAreaFromPortal(townId, areaName, userId) > -1) {
			res.put("status", true);
			res.put("message", "Block Has been Added.");
		} else {
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	private void saveSelfieImage(Employee employee, FileItemStream item)
			throws Exception {

		try {
			String temporaryPath = fileUtils.storeEmployeeProfileImage(
					servletContext.getRealPath("/"), employee.getName()
							.replaceAll("\\s+", ""), employee.getType(), item);

			logger.info("SELFIE image path :: " + temporaryPath);
			employee.setPath(temporaryPath);
		} catch (Exception ex) {
			logger.error(ex, ex);
			logger.error(
					"Error while saving employee profile image against employee name "
							+ employee.getName(), ex);
			throw ex;
		}
	}

	@Override
	public List<Map<String, Object>> getShopsForSisTracking(Integer zoneId,
			Integer regionId, Integer cityId, Integer surveyorId,
			String selectedDate, String mapViewType, String startDate,
			String endDate, String shopType) {

		return shopsDao.getShopsForSisTracking(zoneId, regionId, cityId,
				surveyorId, selectedDate, mapViewType, startDate, endDate,
				shopType);
	}

	@Override
	public List<Surveyor> getSisListByRegionIdForSurveyorTracking(
			Integer zoneId, Integer regionId, String mapViewType,
			String selectedDate, String surveyorType) {
		return surveyorDao.getSisListByRegionIdForSurveyorTracking(regionId,
				zoneId, mapViewType, selectedDate, surveyorType);
	}

	@Override
	public List<Map<String, Object>> getSchemes(Integer cityId, Integer shopId) {

		return portalDao.getSchemes(cityId, shopId);
	}

	@Override
	public List<Map<String, Object>> getOverallSchemes(String promoType,
			Integer brandId, String status, String type) {

		return portalDao.getOverallSchemes(promoType, brandId, status, type);
	}

	@Override
	public List<Map<String, Object>> getFocusBrands(Integer status,
			Integer brandId) {

		return portalDao.getFocusBrands(status, brandId);
	}

	@Override
	public Map<String, Object> updateSchemeTagging(String schemeId,
			Integer townId, Integer shopId, Integer userId) {

		Map<String, Object> map = new HashMap<String, Object>();

		// JSONArray jsonArray = new JSONArray(schemeId);

		// if (jsonArray != null) {
		// for (int i=0;i<jsonArray.length();i++){
		// listdata.add(jsonArray.getString(i));
		// }
		// }

		try {
			String[] elements = schemeId.split(",");
			List<String> fixedLenghtList = Arrays.asList(elements);
			ArrayList<String> listdata = new ArrayList<String>(fixedLenghtList);
			portalDao.insertSchemeTagging(listdata, townId, shopId, userId);
			map.put("status", true);
			map.put("message", "Scheme Assigned Successfully");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("status", false);
			map.put("message", "Error Assigning Scheme");
		}

		return map;
	}

	@Override
	public Map<String, Object> insertNewSchemeFromPortal(String schemeTitle,
			Integer familyId, String promotionType, String schemeType,
			Integer minimumQuantity, float discountQunatity,
			float discountPercent, float discountValue, float stockAssignment,
			String startDate, String endDate, boolean isCreate, Integer userId,
			String type, float productiveShop, float minimumSale,
			String shopProfile, float schemeDuration,
			float productiveBrandFrom, float productiveBrandTo,
			float maximumBudget, final String calculatioType,
			final Integer upperLimit) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			if (isCreate) {
				portalDao.insertNewPromotion(schemeTitle);
			}
			if (portalDao.insertNewSchemeFromPortal(schemeTitle, familyId,
					promotionType, schemeType, minimumQuantity,
					discountQunatity, discountPercent, discountValue,
					stockAssignment, startDate, endDate, userId, type,
					productiveShop, minimumSale, shopProfile, schemeDuration,
					productiveBrandFrom, productiveBrandTo, maximumBudget,
					calculatioType, upperLimit) > -1) {
				res.put("status", true);
				res.put("message", "Scheme Has been Added.");
			} else {
				res.put("status", false);
				res.put("message", "Something went wrong..");
			}

		} catch (DuplicateKeyException dke) {
			logger.error(dke);
			res.put("status", false);
			res.put("message", schemeTitle
					+ " already exists in the system to be created new.");
		} catch (Exception e) {

			logger.error(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	public void updateTownSchemeStatus(String status, Integer schemeId)
			throws Exception {
		portalDao.updateTownSchemeStatus(status, schemeId);

	}

	@Override
	public List<String> getPromoAssignedList(Integer promoId) {
		return citiesDao.getPromoAssignedList(promoId);
	}

	@Override
	public Map<String, Object> assignPromo(Integer promoId, String citiesList,
			float schemeStock, int familyId, String type) {

		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String[] elements = citiesList.split(",");
			List<String> fixedLenghtList = Arrays.asList(elements);
			ArrayList<String> listdata = new ArrayList<String>(fixedLenghtList);
			List<Map<String, Object>> citySales = null;
			Double totalSale = 0.0;
			Double totalAbsoluteZoneStockAllocation = 0.0;
			Double totalAbsoluteRegionStockAllocation = 0.0;
			Double totalAbsoluteCityStockAllocation = 0.0;
			Double totalAbsoluteDsrStockAllocation = 0.0;

			String promoType;

			promoType = portalDao.getPromoType(promoId);
			if (promoType.equalsIgnoreCase("SALE") && schemeStock > 0) {
				float totalAssignedStock = portalDao
						.getTotalAssignedStock(promoId);
				if (totalAssignedStock >= schemeStock) {
					map.put("status", false);
					map.put("message",
							"Please free up stock from already assigned towns to assign to new towns.");
					return map;
				}
				schemeStock = schemeStock - totalAssignedStock;
			}
			// if(promoId<=661){
			// map.put("status", false);
			// map.put("message",
			// "You cannot update town wise schemes.");
			// return map;
			// }
			// delete previous assigned schemes
			portalDao.deletePreviousAssignedSchemes(promoId);
			if (!listdata.get(0).equalsIgnoreCase("-1")) {
				// insert newly tagged scheme
				portalDao.assignPromo(listdata, promoId);
				if (promoType.equalsIgnoreCase("SALE")) {
					totalSale = portalDao.getTotalSaleofCity(citiesList,
							familyId);
					citySales = portalDao
							.getCitySaleValue(citiesList, familyId);
					int zoneIndex = 1;
					for (Map<String, Object> zoneSale : citySales) {
						double zoneStockAllocation = (Double) zoneSale
								.get("saleValue") * schemeStock;
						zoneStockAllocation = zoneStockAllocation / totalSale;
						zoneStockAllocation = (double) Math
								.round(zoneStockAllocation);
						totalAbsoluteZoneStockAllocation = zoneStockAllocation
								+ totalAbsoluteZoneStockAllocation;

						if (zoneIndex == citySales.size()) {
							double remainingStock = schemeStock
									- (double) totalAbsoluteZoneStockAllocation;
							zoneStockAllocation = zoneStockAllocation
									+ remainingStock;
							totalAbsoluteZoneStockAllocation = 0.0;
						}
						zoneIndex++;
						zoneSale.put("stockAllocation", zoneStockAllocation);
						int regionIndex = 1;
						for (Map<String, Object> regionSale : (List<Map<String, Object>>) zoneSale
								.get("regionListMap")) {
							double absoluteRegionStockAllocation = (Double) regionSale
									.get("saleValue") * zoneStockAllocation;
							absoluteRegionStockAllocation = absoluteRegionStockAllocation
									/ (Double) zoneSale.get("saleValue");
							absoluteRegionStockAllocation = (double) Math
									.round(absoluteRegionStockAllocation);
							totalAbsoluteRegionStockAllocation = absoluteRegionStockAllocation
									+ totalAbsoluteRegionStockAllocation;
							if (regionIndex == ((List<Map<String, Object>>) zoneSale
									.get("regionListMap")).size()) {
								double remainingStock = zoneStockAllocation
										- (double) totalAbsoluteRegionStockAllocation;
								absoluteRegionStockAllocation = absoluteRegionStockAllocation
										+ remainingStock;
								totalAbsoluteRegionStockAllocation = 0.0;
							}
							regionIndex++;
							regionSale.put("stockAllocation",
									absoluteRegionStockAllocation);
							int cityIndex = 1;
							for (Map<String, Object> citySale : (List<Map<String, Object>>) regionSale
									.get("cityListMap")) {
								double absoluteCityStockAllocation = (Double) citySale
										.get("saleValue")
										* absoluteRegionStockAllocation;
								absoluteCityStockAllocation = absoluteCityStockAllocation
										/ (Double) regionSale.get("saleValue");
								absoluteCityStockAllocation = (double) Math
										.round(absoluteCityStockAllocation);
								totalAbsoluteCityStockAllocation = absoluteCityStockAllocation
										+ totalAbsoluteCityStockAllocation;
								if (cityIndex == ((List<Map<String, Object>>) regionSale
										.get("cityListMap")).size()) {
									double remainingStock = absoluteRegionStockAllocation
											- (double) totalAbsoluteCityStockAllocation;
									absoluteCityStockAllocation = absoluteCityStockAllocation
											+ remainingStock;
									totalAbsoluteCityStockAllocation = 0.0;
								}
								cityIndex++;
								citySale.put("stockAllocation",
										absoluteCityStockAllocation);
								int dsrIndex = 1;
								for (Map<String, Object> dsrSale : (List<Map<String, Object>>) citySale
										.get("dsrListMap")) {
									double absoluteDsrStockAllocation = (Double) dsrSale
											.get("saleValue")
											* absoluteCityStockAllocation;
									absoluteDsrStockAllocation = absoluteDsrStockAllocation
											/ (Double) citySale
													.get("saleValue");
									absoluteDsrStockAllocation = (double) Math
											.round(absoluteDsrStockAllocation);
									totalAbsoluteDsrStockAllocation = absoluteDsrStockAllocation
											+ totalAbsoluteDsrStockAllocation;
									if (dsrIndex == ((List<Map<String, Object>>) citySale
											.get("dsrListMap")).size()) {
										double remainingStock = absoluteCityStockAllocation
												- (double) totalAbsoluteDsrStockAllocation;
										absoluteDsrStockAllocation = absoluteDsrStockAllocation
												+ remainingStock;
										totalAbsoluteDsrStockAllocation = 0.0;
									}
									dsrSale.put("stockAllocation",
											absoluteDsrStockAllocation);

									dsrIndex++;

								}
							}
						}
					}
					Gson gson = new Gson();
					logger.debug(gson.toJson(citySales));

					portalDao.insertCityPromoWithStock(citySales, promoId,
							totalSale, familyId);

				}
				if (type.equals("FPI")) {

					portalDao.updateFPIStatus();

				}
				logger.info("FPI scheme done");
				map.put("status", true);
				map.put("message", "Scheme Assigned Successfully");
			} else {
				portalDao.updateSchemeStatus(promoId);
				map.put("status", true);
				map.put("message", "Scheme De-Activated Successfully");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("status", false);
			map.put("message", "Error Assigning Scheme");
		}

		return map;
	}

	@Override
	public List<Map<String, Object>> getEditReceivedStock(
			HttpServletRequest request, String receivedDate, Integer userId,
			String type) {

		return portalDao.getEditReceivedStock(receivedDate, userId, type);
	}

	@Override
	public List<Map<String, Object>> getAdjustmentStock(Integer transferId) {

		return portalDao.getAdjustmentStock(transferId);
	}

	@Override
	public void submitAdjustedStock(StockTransfer stockTransfer)
			throws Exception {

		this.submitStockAdjusted(stockTransfer);

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void submitStockAdjusted(StockTransfer stockTransfer)
			throws Exception {

		try {
			if (portalDao.updateAdjustedStock(stockTransfer)
					&& stockTransfer.getTransferedStock().size() > 0) {
				// saving adjustment log
				portalDao.StockAdjustmentLog(stockTransfer.getTransferId(),
						stockTransfer.getTransferedStock(),
						stockTransfer.getStockReceiver(),
						stockTransfer.getToSource());

				// updating stock adjustment quantity
				portalDao.updateAdjustedStockDetails(
						stockTransfer.getTransferId(),
						stockTransfer.getTransferedStock());
				// discussion required why this method is called
				TransferCodeDetails transferCodeDetails = portalDao
						.getTransitDetails(stockTransfer.getTransferCode());

				if (stockTransfer.getTransferType().equalsIgnoreCase(
						"TERRITORY_TO_TERRITORY")
						|| stockTransfer.getTransferType().equalsIgnoreCase(
								"TOWN_TO_TERRITORY")) {

					portalDao.updateTerritoryStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());

				} else if (stockTransfer.getTransferType().equalsIgnoreCase(
						"TERRITORY_TO_TOWN")
						|| stockTransfer.getTransferType().equalsIgnoreCase(
								"TOWN_TO_TOWN")
						|| stockTransfer.getTransferType().equalsIgnoreCase(
								"WAREHOUSE_TO_TOWN")
						|| stockTransfer.getTransferType().equalsIgnoreCase(
								"FACTORY_TO_TOWN")) {

					portalDao.updateTownStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());

				} else if (stockTransfer.getTransferType().equalsIgnoreCase(
						"FACTORY_TO_TERRITORY")
						|| stockTransfer.getTransferType().equalsIgnoreCase(
								"WAREHOUSE_TO_TERRITORY")) {

					portalDao.updateTerritoryStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());

				}
				/*
				 * else if (stockTransfer.getTransferType().equalsIgnoreCase(
				 * "FACTORY_TO_TOWN")) {
				 * 
				 * portalDao.updateTownStocksAfterReceiveStock(
				 * transferCodeDetails.getReceiverLocationId(),
				 * stockTransfer.getTransferedStock()); }
				 */
				else if (stockTransfer.getTransferType().equalsIgnoreCase(
						"FACTORY_TO_WAREHOUSE")) {

					portalDao.updateWareHouseStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());
				} else if (stockTransfer.getTransferType().equalsIgnoreCase(
						"TERRITORY_TO_FACTORY")) {

					portalDao.updateWareHouseStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());
				} else if (stockTransfer.getTransferType().equalsIgnoreCase(
						"FACTORY_INTAKE")) {

					portalDao.updateFactoryStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());
				} else if (stockTransfer.getTransferType().equalsIgnoreCase(
						"TERRITORY_TO_WAREHOUSE")) {

					portalDao.updateWareHouseStocksAfterReceiveStock(
							transferCodeDetails.getReceiverLocationId(),
							stockTransfer.getTransferedStock());
				}

			} else {
				throw new Exception("Received Adjusted Stock Was Empty");
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public Map<String, Object> getRouteTagging(Integer dsrId, Integer type) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("routeAreas", portalDao.getRouteAreas(dsrId, type));
		data.put("preAssignedRoutes",
				portalDao.getPreAssignedroutes(dsrId, type));
		// jsonMap.put("data", data);

		return data;

	}

	@Override
	public Map<String, Object> assignDsrRoutes(Integer dsrId,
			String routesList, Integer type) {

		Map<String, Object> map = new HashMap<String, Object>();

		try {
			String[] elements = routesList.split(",");
			List<String> fixedLenghtList = Arrays.asList(elements);
			ArrayList<String> listdata = new ArrayList<String>(fixedLenghtList);
			ArrayList<String> routeIds = new ArrayList<String>();
			int[] routeDays = null;
			String[] parts = null;
			String listForDsrRoutes = null;

			// deleting previous routes in below dao calls

			List<Integer> list = new ArrayList<Integer>();
			list = portalDao.getPreDsrRoutesDays(dsrId, type);
			if (list != null && list.size() > 0) {
				portalDao.removeExistingRoute(dsrId, type);
				// converting list to string
				listForDsrRoutes = list.toString();
				listForDsrRoutes = listForDsrRoutes.substring(1,
						listForDsrRoutes.length() - 1);
				// deleting existing blocks
				portalDao.removeExistingRouteBlocks(listForDsrRoutes, type);

			}

			for (int i = 0; i < listdata.size(); i++) {
				parts = listdata.get(i).split("-");
				if (!routeIds.contains(parts[0])) {
					routeIds.add(parts[0]);
				}
			}
			if (routeIds != null && routeIds.size() > 0) {
				routeDays = portalDao.insertDsrRouteDays(routeIds, dsrId, type);
			}

			if (routeDays != null && routeDays.length > 0) {
				portalDao.insertDsrRouteBlocks(listdata, dsrId, type);
			}

			map.put("status", true);
			map.put("message", "Route Assigned Successfully");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("status", false);
			map.put("message", "Error Assigning Route");
		}

		return map;
	}

	@Override
	public List<Map<String, Object>> getStockSchemeCity(Integer promoId,
			Float schemeStock, Float consumedStock) {

		return portalDao.getCityPromoWithStock(promoId, schemeStock,
				consumedStock);
	}

	@Override
	public void assignStockCityScheme(CityStockAssignment cityStockAssignment)
			throws Exception {
		try {
			portalDao.assignStockCityScheme(cityStockAssignment);
			portalDao.updateSchemeStockStatus();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<String> getBrandAssignedList(Integer familyId) {
		return familiesDao.getBrandAssignedList(familyId);
	}

	@Override
	public void assignBrandList(BrandAssignment brandAssignment)
			throws Exception {
		try {
			int setId;
			for (BrandAssignList brand : brandAssignment.getBrandAssignList()) {
				setId = portalDao.getFamilySet(brand.getTitle());
				if (setId < 1) {
					setId = portalDao.insertFamilySet(brand.getTitle());
					// insert families against this set id
					// portalDao.insertFamiliesAgainstNewSet(setId);

				}
				portalDao.insertUpdateFamilyEntry(brand, setId,
						brandAssignment.getFamilyId());
				// log insert for price revision
				if (brand.getIsUpdated() != null
						&& brand.getIsUpdated().equalsIgnoreCase("Y")) {
					portalDao.insertBrandLog(brand, setId,
							brandAssignment.getFamilyId(),
							brandAssignment.getUserId());
				}

			}
			portalDao.deActiveBrandPrice(brandAssignment.getBrandPriceId());
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<Map<String, Object>> getBrandList() {

		return portalDao.getBrandList();
	}

	@Override
	public List<Map<String, Object>> getAllBrandList(Integer industryId,
			String type) {

		return portalDao.getAllBrandList(industryId, type);
	}

	@Override
	public List<Map<String, Object>> getFamilySetList() {

		return portalDao.getFamilySetList();
	}

	@Override
	public void insertDsrIntoEmployee(String dsrName) throws Exception {
		try {
			portalDao.insertDsrEmployee(dsrName);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<Map<String, Object>> getDsrEmployeeList() {
		return portalDao.getDsrEmployeeList();
	}

	@Override
	public void updateDsrEmloyee(Integer dsrId, Integer employeeId,
			Integer userId, float basicSalary, float dailyAllownce,
			float fuelAllownce, float maintainanceAllownce,
			float otherAllowance, Integer cityId, String registrationNo,
			String makeModel, String vanType, String dsrType) throws Exception {
		portalDao.updateDsrEmloyee(dsrId, employeeId, userId, basicSalary,
				dailyAllownce, fuelAllownce, maintainanceAllownce,
				otherAllowance, cityId, registrationNo, makeModel, vanType,
				dsrType);

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void insertDsrFromPortal(Integer employeeId, String deCode,
			String employeeType, Integer selectedDE, Integer userId,
			Integer rateList, float basicSalary, float dailyAllownce,
			float fuelAllownce, Float maintenanceAllownce,
			Float otherAllowance, Float vanSalary, String registrationNo,
			String makeModel, String vanType) throws Exception {
		logger.info("Saving dsr data into db");
		insertdsrFromPortal(employeeId, deCode, employeeType, selectedDE,
				userId, rateList, basicSalary, dailyAllownce, fuelAllownce,
				maintenanceAllownce, otherAllowance, vanSalary, registrationNo,
				makeModel, vanType);
	}

	private void insertdsrFromPortal(Integer employeeId, String deCode,
			String employeeType, Integer selectedDE, Integer userId,
			Integer rateList, float basicSalary, float dailyAllownce,
			float fuelAllownce, Float maintenanceAllownce,
			Float otherAllowance, Float vanSalary, String registrationNo,
			String makeModel, String vanType) throws Exception {
		int dsrId = dsrDao.insertDsrFromPortal(employeeId, deCode,
				employeeType, selectedDE, userId, rateList, basicSalary,
				dailyAllownce, fuelAllownce, maintenanceAllownce,
				otherAllowance, vanSalary, registrationNo, makeModel, vanType);
		if (dsrId > 0) {
			portalDao.surveyorDsrTagging(selectedDE, dsrId, userId);
			portalDao.addDsrFamilyStock(dsrId);
		}
	}

	@Override
	public List<Map<String, Object>> getCities(Integer regionId) {

		return citiesDao.getCity(regionId);
	}

	@Override
	public List<Map<String, Object>> getCitiesNew(String regionId) {

		return citiesDao.getCityNew(regionId);
	}

	@Override
	public List<Map<String, Object>> getDistinctSchemes() {

		return portalDao.getDistinctSchemes();
	}

	@Override
	public void deleteDsrDayEnd(Integer dsrId) throws Exception {
		// incase of leave
		portalDao.deleteDsrLeave(dsrId);
		// incase if he has day end at similar day
		int res = portalDao.deleteDsrDayEnd(dsrId);
		if (res > 0) {
			portalDao.deleteStockReturn(dsrId);
			dsrDao.updateDsrStock(dsrId);
		}
	}

	@Override
	public Map<String, Object> updateEmployeeName(String employeeName,
			long phone, long cnic, Integer basicSalary, Integer dailyAllownce,
			Integer fuelAllownce, Integer maintenanceAllownce,
			Integer employeeId, Integer userId) {
		HashMap<String, Object> resp = new HashMap<String, Object>();
		try {
			int result = portalDao.updateEmployeeName(employeeName, phone,
					cnic, basicSalary, dailyAllownce, fuelAllownce,
					maintenanceAllownce, employeeId, userId);

			if (result > 0) {
				resp.put("status", true);
				resp.put("message", "Employee Details Successfully Updated...!");
			} else {
				resp.put("status", false);
				resp.put("message",
						"Somthing Went Wrong Updating Employee Details ...!");
			}
		} catch (Exception e) {
			logger.error(e, e);
			resp.put("status", false);
			resp.put("message",
					"Somthing Went Wrong Updating Employee Details ...!");
		}

		return resp;
	}

	@Override
	public List<Map<String, Object>> getBrandPriceDetails(Integer brandId,
			Integer brandPriceId) throws Exception {
		return familiesDao.getBrandPriceDetails(brandId, brandPriceId);
	}

	@Override
	public int insertBrandPrice(BrandPrice brandPrice) throws Exception {
		int brandPriceId = portalDao.insertBrandPrice(brandPrice);
		if (brandPrice.getDuplicateBrandPriceId() == -1) {
			portalDao.insertFamilyWiseBrandPriceDetails(brandPriceId);
		} else {
			portalDao.insertDuplicateBrandPriceDetails(brandPriceId,
					brandPrice.getDuplicateBrandPriceId());
		}
		return brandPriceId;
	}

	@Override
	public void updateBrandPriceDetails(BrandPriceDetails brandPriceDetail)
			throws Exception {
		// return portalDao.insertBrandPriceDetails(brandPriceDetail);
		portalDao.updateBrandPriceDetails(brandPriceDetail);
	}

	@Override
	public List<Map<String, Object>> getBrandPrices() throws Exception {
		return familiesDao.getBrandPrices();
	}

	@Override
	public int insertBrandPriceDetail(BrandPriceDetails brandPriceDetail)
			throws Exception {
		return portalDao.insertBrandPriceDetails(brandPriceDetail);
	}

	@Override
	public List<String> getBrandRateAssignedList(Integer brandPriceId) {
		return portalDao.getBrandRateAssignedList(brandPriceId);
	}

	@Override
	public void assignRateList(RateAssignment rateAssignment) throws Exception {
		try {

			portalDao.applyRateList(rateAssignment.getRateList(),
					rateAssignment.getBrandPriceId());
			// log insert for price revision
			// portalDao.logRateRevision(rateAssignment.getUserId(),
			// rateAssignment.getRateList(),
			// rateAssignment.getBrandPriceId());

			portalDao.deActiveBrandPrice(rateAssignment.getBrandPriceId());
			portalDao.updateUpcommingPriceData();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void deleteRateList(Integer brandPriceId, Integer userId)
			throws Exception {
		try {
			portalDao.logRateDeletion(brandPriceId, userId);
			portalDao.deleteRateList(brandPriceId, userId);
			portalDao.disAssignBrandPrice(brandPriceId);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void importShopsFromBlock(Integer exportAreaId, Integer importAreaId)
			throws Exception {
		try {
			portalDao.importShopsFromBlock(exportAreaId, importAreaId);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void blockTransfer(String blockName, Integer blockId,
			Integer townId, Integer dsrId, Integer userId) throws Exception {
		this.transferBlockFromPortal(blockName, blockId, townId, dsrId, userId);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private void transferBlockFromPortal(String blockName, Integer blockId,
			Integer townId, Integer dsrId, Integer userId) throws Exception {

		try {
			portalDao.inActiveAreas(blockId, userId);
			int areaId = portalDao.insertBlockFromProtal(blockName, townId,
					userId);
			if (dsrId != -1) {
				portalDao.insertMappBlockToDsrFromProtal(dsrId, areaId, userId);
			}
			if (blockId != -1) {
				portalDao.importShopsFromBlock(blockId, areaId);
			}
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}

	}

	@Override
	public void updateBrandStatus(String status, Integer brandId)
			throws Exception {

		portalDao.updateBrandStatus(status, brandId);

	}

	@Override
	public List<Map<String, Object>> getTerritoriesRadius() {
		return portalDao.getTerritoriesRadius();
	}

	@Override
	public void updateTerritoryRadius(Integer id, Integer radius, Integer userId)
			throws Exception {

		portalDao.updateTerritoryRadius(id, radius, userId);

	}

	@Override
	public List<Map<String, Object>> getFinanceSetup(String type,
			String territoryId, String regionId) {
		if (type.equalsIgnoreCase("Territory")) {
			return portalDao.getTerritoryFinanceDetails(territoryId);

		} else if (type.equalsIgnoreCase("National")) {

			return portalDao.getNationalFinanceDetails();
		} else if (type.equalsIgnoreCase("Regional")) {

			return portalDao.getRegionalFinanceDetails(regionId);
		} else {
			return portalDao.getTownFinanceDetails(territoryId);
		}
	}

	@Override
	public int updateFinanceDetails(FinanceSetup financeSetup, String type)
			throws Exception {
		try {
			if (financeSetup.getChangeScope().equalsIgnoreCase(
					SysConstants.APPLY_TO_OLD_EMPLOYEES)) {
				if (financeSetup.getDsrIncrement() != 0) {
					portalDao.logDsrFinance(financeSetup);
				}
				if (financeSetup.getDeIncrement() != 0) {
					portalDao.logDEFinance(financeSetup);
				}
				if (financeSetup.getVanDsrIncrement() != 0) {
					portalDao.logVanDsrFinance(financeSetup);
				}
			}
			if (!type.equalsIgnoreCase("Town")) {
				if (type.equalsIgnoreCase("Regional")
						|| type.equalsIgnoreCase("National")) {
					portalDao.updateRegionalFinanceDetails(financeSetup);
				}
				portalDao.updateTerritoryFinanceDetails(financeSetup);
				if (financeSetup.getChangeScope().equalsIgnoreCase(
						SysConstants.APPLY_TO_OLD_EMPLOYEES)) {
					portalDao.updateDEFinanceDetails(financeSetup);
				}
			}
			portalDao.updateTownFinanceDetails(financeSetup);
			if (financeSetup.getChangeScope().equalsIgnoreCase(
					SysConstants.APPLY_TO_OLD_EMPLOYEES)) {
				portalDao.updateDsrFinanceDetails(financeSetup);
				portalDao.updateVanDsrFinanceDetails(financeSetup);
			}
			if (type.equalsIgnoreCase("National")) {
				portalDao.updateNationalFinanceDetails(financeSetup);
				portalDao.updateNationalFinanceDetail(financeSetup);
			}

		} catch (Exception e) {
			logger.error(e, e);
			return -1;
		}
		return 1;
	}

	@Override
	public List<Map<String, Object>> getMonths(String month, Integer id) {
		return portalDao.getMonths(month, id);

	}

	public List<Map<String, Object>> updateprice(Double f1, Double f2, int id,
			String date) {
		if (f1 == null || f1 == 0.0) {
			try {
				return portalDao.payrollUnprocess(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			portalDao.update1(f1, f2, id, date);
			try {
				return portalDao.update2(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public List<Map<String, Object>> getApplicationUsers() {

		return portalDao.getApplicationUsers();
	}

	@Override
	public Map<String, Object> insertFocusBrand(final Integer regionId,
			final Integer visits, final Integer sale, final Integer familyId,
			final String startDate, final String endDate, final Integer userId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {

			if (portalDao.insertFocusBrand(regionId, visits, sale, familyId,
					startDate, endDate, userId) > -1) {
				portalDao.markFocusBrand();

				res.put("status", true);
				res.put("message", "ADDED Successfully");
			} else {
				res.put("status", false);
				res.put("message", "Something went wrong..");
			}

		} catch (DuplicateKeyException dke) {
			logger.error(dke);
			res.put("status", false);
		} catch (Exception e) {

			logger.error(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	public void updateFocusBrandStatus(String status, Integer id)
			throws Exception {

		portalDao.updateFocusBrandStatus(status, id);

	}

	public Map<String, Object> insertAcitivity(final String activityTitle,
			final String activityType, final Integer familyId,
			final String startDate, final String endDate, final Integer userId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {

			if (portalDao.insertAcitivity(activityTitle, activityType,
					familyId, startDate, endDate, userId) > -1) {

				res.put("status", true);
				res.put("message", "ADDED Successfully");
			} else {
				res.put("status", false);
				res.put("message", "Something went wrong..");
			}

		} catch (DuplicateKeyException dke) {
			logger.error(dke);
			res.put("status", false);
		} catch (Exception e) {

			logger.error(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	public List<Map<String, Object>> getActivityData(Integer status,
			Integer brandId) {

		return portalDao.getActivityData(status, brandId);
	}

	public Map<String, Object> insertAcitivityRoutes(final Integer deId,
			final Integer activityId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {

			if (portalDao.insertAcitivityRoutes(deId, activityId) > -1) {

				res.put("status", true);
				res.put("message", "ADDED Successfully");
			} else {
				res.put("status", false);
				res.put("message", "Something went wrong..");
			}

		} catch (DuplicateKeyException dke) {
			logger.error(dke);
			res.put("status", false);
		} catch (Exception e) {

			logger.error(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	public List<Map<String, Object>> getCreditNote(String zoneId,
			String regionId, Integer month, Integer year) {

		List<Map<String, Object>> res = null;
		res = portalDao.getCreditNote(zoneId, regionId, month, year);
		return res;
	}

	@Override
	public void getForgotPassword(String userName, String uniqueKey, String type)
			throws Exception {
		int rowsUpdated = portalDao.insertUniqueKey(userName, uniqueKey, type);

		if (rowsUpdated > 0) {
			emailSendingCron.sendForgotPassword(userName, uniqueKey, type);
		}
	}

	@Override
	public Map<String, Object> updatePasswordFromEmail(String newPassword,
			String uniqueKey, String type) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		try {
			map = (HashMap<String, Object>) this.updatePasswordFromEmails(
					newPassword, uniqueKey, type);
			return map;
		} catch (Exception e) {
			logger.error("Exception Occured while updating password : Exception :"
					+ e);
			return map;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private Map<String, Object> updatePasswordFromEmails(String newPassword,
			String uniqueKey, String type) throws Exception {
		HashMap<String, Object> resp = new HashMap<String, Object>();

		if (portalDao.updatePasswordFromEmail(uniqueKey,
				PasswordStorage.createHash(newPassword), type) > 0) {

			portalDao.emptyKey(uniqueKey, type);

			resp.put("status", true);
			resp.put("message", "Password Successfully Updated...!");
		} else {
			resp.put("status", false);
			resp.put("message",
					"Somthing Went Wrong during update password...!");
		}
		return resp;

	}

	@Override
	public boolean getexpiryTime(String currentTime, String uniqueKey,
			String type) throws Exception {
		String expiryTime = portalDao.getExpiry(uniqueKey, type);

		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		// Parse currentTime and expiryTime strings into Date objects
		Date currentDateTime = dateFormat.parse(currentTime);
		Date expiryDateTime = dateFormat.parse(expiryTime);

		// Calculate time difference in milliseconds
		long timeDifferenceMillis = currentDateTime.getTime()
				- expiryDateTime.getTime();

		// Convert time difference from milliseconds to minutes
		long timeDifferenceMinutes = timeDifferenceMillis / (1000 * 60);

		// Return the time difference in minutes as an integer
		return (timeDifferenceMinutes >= 0 && timeDifferenceMinutes <= 10);
	}

	public List<Map<String, Object>> getFinanceSheeetData(String type,
			String territoryId, String regionId) {
		if (type.equalsIgnoreCase("Territory")) {
			return portalDao.getTerritoryFinanceSheetData(territoryId);

		} else if (type.equalsIgnoreCase("National")) {

			return portalDao.getNationalFinanceDetails();
		} else if (type.equalsIgnoreCase("Regional")) {

			return portalDao.getRegionFinanceSheetData(regionId);
		} else if (type.equalsIgnoreCase("godown")) {
			return portalDao.getGodownFinanceSheetData(territoryId);
		} else {
			return portalDao.getTownFinanceSheetData(territoryId);
		}
	}

	@Override
	public List<Map<String, Object>> getUserType(HttpServletRequest request,
			HttpServletResponse response, Integer userId) {
		List<Map<String, Object>> data = null;
		data = portalDao.getUserType(userId);
		logger.info("data" + data);

		return data;
	}

	@Override
	public List<Map<String, Object>> getregionscities(
			HttpServletRequest request, HttpServletResponse response,
			String regions) {
		List<Map<String, Object>> data = null;
		data = citiesDao.getregionscities(request, response, regions);
		return data;
	}

	@Override
	public List<Map<String, Object>> getUserRegions(String assingedRegions,
			Integer zoneId) {
		List<Map<String, Object>> data = null;
		data = portalDao.getUsersRegionsByZoneId(zoneId, assingedRegions);
		return data;
	}

	public AdminProfile getUserFromSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false); // false to avoid
															// creating a new
															// session if none
															// exists
		if (session != null) {
			return (AdminProfile) session.getAttribute("user");
		}
		return null;
	}

	@Override
	public Boolean validateRequest(String UserId, String fullUrl, String type) {
		int t = Integer.parseInt(type);
		logger.info("fullURl" + fullUrl);
		List<Map<String, Object>> allowedlinks = null;
		allowedlinks = portalDao.getAllowedUrls(t);
		String[] parts = fullUrl.split("/#/");
		String a = parts.length > 1 ? parts[1] : "";
		boolean isMatchFound = false;

		if (allowedlinks != null) {
			for (Map<String, Object> map : allowedlinks) {
				for (Object value : map.values()) {
					if (a.equals(value) || type.equals("4")) {
						// isMatchFound = true;
						// return isMatchFound;
						return true;
					}
					if (value.equals("dashboard/productivity_report") ||value.equals("dashboard/sis-productivity") ) {
						if (a.contains("dashboard/shop_detail")
								|| a.contains("dashboard/evaluation/shop_list/details")) {

							// isMatchFound = true;
							// return isMatchFound;
							return true;

						}
						isMatchFound = false;

					}

					if (value.equals("dashboard/admin/roles")) {
						if (a.contains("dashboard/admin/roles/assign-menu")) {

							// isMatchFound = true;
							//
							// return isMatchFound;

							return true;
						}
						isMatchFound = false;

					}

					if (value.equals("dashboard/admin/scheme")) {
						if (a.contains("dashboard/admin/scheme-stock-assignment")) {

							// isMatchFound = true;
							//
							// return isMatchFound;

							return true;
						}
						isMatchFound = false;

					}
					
					if (value.equals("dashboard/dsr-productivity")) {
						if (a.contains("dashboard/tpsom") || a.contains("dashboard/dsr-morning-image")) {

							// isMatchFound = true;
							//
							// return isMatchFound;

							return true;
						}
						isMatchFound = false;

					}

				}
			}
		}

		if (isMatchFound) {
			logger.info("Match found!");
		} else {
			logger.info("No match found.");
		}

		return isMatchFound;
	}

	@Override
	public void primaryVsAchievement(HttpServletRequest request,
			HttpServletResponse response, Integer month, Integer year) {
		pvaReport.createReport(response, request, month, year);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	private Map<String, Object> updateGodownDetails(String title,
			String address, int rent, String type, int regionId, int id,
			String isNew) throws Exception {
		HashMap<String, Object> resp = new HashMap<String, Object>();

		if (portalDao.upsertGodownSetup(title, address, rent, type, regionId,
				id, isNew) > 0) {

			resp.put("status", true);
			resp.put("message", "SuccessFully Added");
		} else {
			resp.put("status", false);
			resp.put("message", "Somthing Went Wrong during update Details...!");
		}
		return resp;

	}

	@Override
	public Map<String, Object> updateGodown(String title, String address,
			int rent, String type, int regionId, int id, String isNew) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		try {
			map = (HashMap<String, Object>) this.updateGodownDetails(title,
					address, rent, type, regionId, id, isNew);
			return map;
		} catch (Exception e) {
			logger.error("Exception Occured while updating password : Exception :"
					+ e);
			return map;
		}
	}

	@Override
	public List<Map<String, Object>> tpsomData(Integer dsrId, String startDate,
			String endDate) {
		List<Map<String, Object>> data = portalDao.tpsomData(dsrId, startDate,
				endDate);
		return data;
	}
	
	
	@Override
	public List<Map<String, Object>> dsrMorningImageData(Integer dsrId, String startDate,
			String endDate) {
		List<Map<String, Object>> data = portalDao.dsrMorningImageData(dsrId, startDate,
				endDate);
		return data;
	}
	
	@Override
	public Map<String, Object> LoadFilters(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			Gson gson = new Gson();

			// Read the request body
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}

			try {
				// Parse JSON request body
				JSONObject json = new JSONObject(sb.toString());

				// Use Gson with custom settings if needed
				GsonBuilder builder = new GsonBuilder();
				builder.excludeFieldsWithoutExposeAnnotation();
				gson = builder.create();

				int userId = 0;
				int action = json.getInt("act"); // assuming 'act' is a required field

				// Validate userId
				if (json.has("userId") && !json.isNull("userId") && !json.get("userId").equals("")) {
					userId = json.getInt("userId");
				}

				// Retrieve the user profile based on userId
				AdminProfile user = portalDao.findUserById(userId);

				// Log the input JSON for debugging purposes
				logger.info("Input JSON: " + json.toString());

				// Retrieve filters based on reportId and user profile
				Map<String, Object> filters = portalDao.getDashboardQueryList(user, json.getInt("reportId"));

				// Create a list and add filters map to it

				return filters;

			} catch (JSONException e) {
				logger.error("Error parsing JSON: " + e.getMessage(), e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON input.");
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> dashboardDataNew(HttpServletRequest request, HttpServletResponse response) {
		// Ensure the request method is not OPTIONS
		if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
			StringBuilder sb = new StringBuilder();
			try (BufferedReader reader = request.getReader()) {
				String line;
				// Read the request body
				while ((line = reader.readLine()) != null) {
					sb.append(line).append('\n');
				}
			} catch (IOException e) {
				logger.error("Error reading request body: " + e.getMessage(), e);
				try {
					return createErrorResponse(response, "Error reading request body.");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			try {
				// Parse JSON request body
				JSONObject json = new JSONObject(sb.toString());
				int queryId = json.getInt("queryId");
				DashboardReport reportData = reportsDao.getDashboardQueryListById(queryId);

				logger.info("Report Download Request: " + reportData.getTitle() + ", params_received:" + json);

				// Process based on report type
				switch (reportData.getType()) {
				case 1:
					exceRxcel.createReport(response, request, null, null, reportData, json);
					break;

				case 2:
				case 3:
					handleTemplateBaseReport(response, request, reportData, json);
					break;

				// Uncomment and implement the case for type 4 if needed
				// case 4:
				// handleType4Report(response, reportData, json);
				// break;

				default:
					logger.warn("Unknown report type: " + reportData.getType());
					return createErrorResponse(response, "Unknown report type.");
				}
			} catch (JSONException e) {
				logger.error("Error parsing JSON: " + e.getMessage(), e);
				try {
					return createErrorResponse(response, "Invalid JSON input.");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (Exception e) {
				logger.error("Unexpected error: " + e.getMessage(), e);
				try {
					return createErrorResponse(response, "Internal server error.");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return null;
	}

	private void handleTemplateBaseReport(HttpServletResponse response, HttpServletRequest request,
			DashboardReport reportData, JSONObject json) throws Exception {
		JSONObject obj = new JSONObject();
		boolean success = true;
		String folderPath = getFolderPath();
		String key = generateKey(reportData.getTitle());
		String filePath = folderPath + key + ".xlsx";

		if (reportData.getType() == 2) {
			success = pivotBasedReport.createReport(response, request, filePath, reportData, json);
		}

		if (success) {
			obj.put("key", key);
			response.setContentType("application/json");
			response.getWriter().println(obj.toString());
			response.getWriter().flush();
		}
	}

	private Map<String, Object> createErrorResponse(HttpServletResponse response, String message) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
		return null; // Return null as the response was already sent
	}

	public String getFolderPath() {
		String folderPath = servletContext.getRealPath("/") + "/portalGeneratedReports/"
				+ DateTimeUtilities.getCurrentDate() + "/";
		if (!FileUtils.isExistsFolder(folderPath)) {
			FileUtils.createFolders(folderPath);
		}
		return folderPath;
	}

	public String generateKey(String reportName) {
		return reportName + "_" + String.valueOf(System.currentTimeMillis()) + "_" + RandomStringUtils.randomNumeric(8);
	}

	@Override
	public void downloadReport(HttpServletRequest req, HttpServletResponse resp) {
		String key = req.getParameter("key");
		String fileType = req.getParameter("fileType");
		String reportType = req.getParameter("pdf");
		String date = req.getParameter("date");
		String tableau = req.getParameter("tableau");

		// Set the date if tableau is 'Y'
		if ("Y".equalsIgnoreCase(tableau)) {
			date = DateTimeUtilities.getDate();
		}

		String directory = File.separator + "portalGeneratedReports" + File.separator
				+ DateTimeUtilities.getCurrentDate() + File.separator;
		String path = directory + key + fileType;
		String deleteFilePath = servletContext.getRealPath("/") + path;

		try (OutputStream os = resp.getOutputStream()) {
			if (date != null) {
				key = key.contains(".xlsx") ? key : key + ".xlsx";
				prepareResponse(resp, key, "txt/plain");
				writeFileToResponse(path, os);
				return;
			}

			if ("y".equalsIgnoreCase(reportType)) {
				prepareResponse(resp, key, "txt/plain");
				writeFileToResponse(path, os);
				deleteReport(deleteFilePath);
			} else {
				handleFileDownload(fileType, key, deleteFilePath, resp, os);
			}
		} catch (IOException ex) {
			logger.error("Error during file download: ", ex);
		}
	}

	private void prepareResponse(HttpServletResponse resp, String key, String contentType) {
		resp.setContentType(contentType);
		resp.setHeader("Content-Disposition",
				"attachment;filename=" + key.split("_")[0] + getFileExtension(contentType));
	}

	private String getFileExtension(String contentType) {
		switch (contentType) {
		case "application/zip":
			return ".zip";
		case "txt/plain":
		default:
			return ".xlsx"; // Defaulting to .xlsx
		}
	}

	private void handleFileDownload(String fileType, String key, String deleteFilePath, HttpServletResponse resp,
			OutputStream os) throws IOException {
		if ("zip".equals(fileType)) {
			resp.setContentType("application/zip");
			resp.setHeader("Content-Disposition", "attachment;filename=" + key.split("_")[0] + ".zip");

			try (FileInputStream in = new FileInputStream(deleteFilePath);
					ZipOutputStream zos = new ZipOutputStream(os)) {

				ZipEntry ze = new ZipEntry(key.split("_")[0]);
				zos.putNextEntry(ze);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				zos.closeEntry();
			}
		} else {
			prepareResponse(resp, key, "txt/plain");
			writeFileToResponse(deleteFilePath, os);
			deleteReport(deleteFilePath);
		}
	}

	private void writeFileToResponse(String path, OutputStream os) throws IOException {
		try (InputStream is = servletContext.getResourceAsStream(path)) {
			if (is != null) {
				byte[] bytes = new byte[1024];
				int read;
				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
			} else {
				logger.error("File not found at path: " + path);
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
	public void downloadcsvReport(HttpServletRequest req, HttpServletResponse resp) {
		String key = req.getParameter("key");
		String fileType = req.getParameter("fileType");
		String directory = File.separator + "portalGeneratedReports" + File.separator
				+ DateTimeUtilities.getCurrentDate() + File.separator;
		String path = directory + key + ".csv";
		String deleteFilePath = servletContext.getRealPath("/") + path;

		try {
			if ("zip".equalsIgnoreCase(fileType)) {
				downloadAsZip(resp, deleteFilePath, key);
			} else {
				downloadAsCsv(resp, path, key);
			}
		} catch (IOException ex) {
			logger.error("Error during CSV report download: ", ex);
		}
	}

	private void downloadAsZip(HttpServletResponse resp, String deleteFilePath, String key) throws IOException {
		resp.setContentType("application/zip");
		resp.setHeader("Content-Disposition", "attachment;filename=" + key.split("_")[0] + ".zip");

		try (FileInputStream in = new FileInputStream(deleteFilePath);
				ZipOutputStream zos = new ZipOutputStream(resp.getOutputStream())) {

			ZipEntry ze = new ZipEntry("Dashboard.csv");
			zos.putNextEntry(ze);
			byte[] buffer = new byte[1024];
			int len;

			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			zos.closeEntry();
		} finally {
			deleteReport(deleteFilePath);
		}
	}

	private void downloadAsCsv(HttpServletResponse resp, String path, String key) throws IOException {
		resp.setContentType("text/csv");
		resp.setHeader("Content-Disposition", "attachment;filename=" + key.split("_")[0] + ".csv");

		try (InputStream is = servletContext.getResourceAsStream(path); OutputStream os = resp.getOutputStream()) {

			if (is != null) {
				byte[] bytes = new byte[1024];
				int read;

				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
			} else {
				logger.error("File not found at path: " + path);
			}
		} finally {
			String deleteFilePath = servletContext.getRealPath("/") + path;
			deleteReport(deleteFilePath);
		}
	}

}
