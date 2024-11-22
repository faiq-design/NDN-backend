package com.marksman.census.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.marksman.census.bo.Area;
import com.marksman.census.bo.Build;
import com.marksman.census.bo.Region;
import com.marksman.census.bo.Shop;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.bo.Zone;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.client.bo.BrandStock;
import com.marksman.census.client.bo.DeDetails;
import com.marksman.census.client.bo.Family;
import com.marksman.census.client.bo.SummaryData;
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
import com.marksman.census.constants.CommonConstants;
import com.marksman.census.constants.DateTimeConstants;
import com.marksman.census.constants.SurveyorType;
import com.marksman.census.constants.SysConstants;
import com.marksman.census.dao.AreaDao;
import com.marksman.census.dao.BwusDao;
import com.marksman.census.dao.CitiesDao;
import com.marksman.census.dao.DsrDao;
import com.marksman.census.dao.FamiliesDao;
import com.marksman.census.dao.IndustriesDao;
import com.marksman.census.dao.ProductsDao;
import com.marksman.census.dao.QuestionsDao;
import com.marksman.census.dao.RegionsDao;
import com.marksman.census.dao.ShopsDao;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.message.MessageType;
import com.marksman.census.util.CommonUtil;
import com.marksman.census.util.DateTimeUtilities;

public class SurveyorServiceImpl implements SurveyorService {

	@Autowired
	private SurveyorDao surveyorDao;
	@Autowired
	private ShopsDao shopsDao;
	@Autowired
	private ApplicationCacheService applicationCacheService;
	@Autowired
	private AreaDao areaDao;
	@Autowired
	private CitiesDao citiesDao;
	@Autowired
	private QuestionsDao questionsDao;
	@Autowired
	private IndustriesDao industriesDao;
	@Autowired
	private FamiliesDao familiesDao;
	@Autowired
	private ProductsDao productsDao;
	@Autowired
	private BwusDao bwusDao;
	@Autowired
	private RegionsDao regionDao;
	@Autowired
	private DsrDao dsrDao;

	protected Logger logger = Logger.getLogger(this.getClass());
	private ResourceBundle bundle = ResourceBundle
			.getBundle(CommonConstants.PROPERTY_FILE_NAME);

	@Override
	public Map<String, Object> login(String imei, String mCode,
			HttpServletRequest request, HttpServletResponse response) {

		if (SysConstants.PMI.equalsIgnoreCase(bundle.getString(
				SysConstants.PROJECT).trim())) {

			return pmiLogin(imei, mCode, response);
		} else if (SysConstants.NDN.equalsIgnoreCase(bundle.getString(
				SysConstants.PROJECT).trim())) {
			Build build = applicationCacheService.getBuildsMap().get("DE");
			logger.info(build+"build");
			String version = request.getHeader("version");
			if (version.contains("_")) {
				version = version.split("_")[0];
			}
			logger.info(version+"version");
			logger.info(" request version : " + version
					+ ", current version : " + build.getVersion()
					+ " , DE Code : " + mCode + ", imei: " + imei);
			if (Boolean.parseBoolean(bundle.getString(
					CommonConstants.VERSION_ALERT).trim())
					&& !build.getVersion().equalsIgnoreCase(version)) {
				response.setHeader("url", build.getDownloadUrl());
				response.setHeader("version", build.getVersion());
				CommonUtil.writeErrorMessage(
						HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED,
						MessageType.ERROR_VERSION, response);
				return null;
			} else {
				return ndnLogin(imei, mCode, response);
			}
		} else if (SysConstants.MONDELEZ.equalsIgnoreCase(bundle.getString(
				SysConstants.PROJECT).trim())) {

			return mondelezLogin(imei, mCode, response);
		}
		return null;
	}

	@Override
	public Map<String, Object> refreshData(String imei, Surveyor surveyor,
			String surveyorType, String version, HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			// Surveyor surveyor = surveyorDao.getSurveyorById(surveyorId);
			if (SurveyorType.DE.toString().equals(surveyorType)) {

				surveyor.setDsrList(surveyorDao.getDsrList(surveyor.getId()));
				
					
					data.put("shops", shopsDao.getShopsDetailNew(surveyor.getId(), -1,
							-1, -1.0, -1.0, -1, version));
				
				data.put("areas", areaDao.getSurveyorAreas(surveyor.getId()));
				data.put("remarks", applicationCacheService.getRemarks());
				data.put(
						"wholesaleChannel",
						new ArrayList<String>(Arrays.asList(bundle
								.getString(SysConstants.WHOLE_SALE_CHANNEL)
								.trim().split(","))));
				data.put("questions", applicationCacheService.getQuestions());
				data.put("options", applicationCacheService.getOptions());
				data.put("questionOptions",
						applicationCacheService.getQuestionOptions());
				data.put(
						"profiles",
						new ArrayList<String>(Arrays.asList(bundle
								.getString(SysConstants.PROFILE).trim()
								.split(","))));
				data.put("interceptionShops", shopsDao.interceptionShopList());
			} else if (surveyor.getSurveyorType().equals(SurveyorType.TM)) {

				surveyor.setDsrList(surveyorDao.getDsrListByTMId(surveyor
						.getId()));
				data.put("deList",
						surveyorDao.getSupervisorList(surveyor.getId(), "DE"));
				/*
				 * data.put("shops",
				 * shopsDao.getShopsDetailForTM(surveyor.getId()));
				 * data.put("areas", areaDao.getTmAreas(surveyor.getId()));
				 */
				data.put("shops", new ArrayList<Shop>());
				data.put("areas", new ArrayList<Area>());
			}
			data.put("surveyor", surveyor);
			data.put("remarks", applicationCacheService.getRemarks());
			data.put("remarksTypes", applicationCacheService.getRemarksTypes());
			data.put("checkInInfo", surveyorDao
					.getSurveyorAttendanceAndDsrAttendance(surveyor.getId()));
			data.put("shopCategories",
					applicationCacheService.getShopsCategories());
			data.put("shopGroups", applicationCacheService.getShopsGroups());
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
			data.put("cities", citiesDao.getCities(surveyor.getId()));
			data.put("questions", applicationCacheService.getQuestions());
			data.put("options", applicationCacheService.getOptions());
			data.put("questionOptions",
					applicationCacheService.getQuestionOptions());
			data.put("industries", applicationCacheService.getIndustries());
			data.put("families", applicationCacheService.getFamilies());
			data.put("products", applicationCacheService.getProducts());
			data.put("bwus", applicationCacheService.getBwu());
			data.put("industryBwus", applicationCacheService.getIndustryBwu());
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
	public Map<String, Object> shopFascia(int shopId,
			HttpServletResponse response, String appType) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		if (appType != null && appType.equalsIgnoreCase("DSR")) {
			jsonMap.put("url", dsrDao.getLastVisitShopImageUrl(shopId));
		} else {
			jsonMap.put("url",
					shopsDao.getLastVisitShopImageUrl(shopId, appType));
		}

		return jsonMap;
	}

	private Map<String, Object> pmiLogin(String imei, String mCode,
			HttpServletResponse response) {

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			Surveyor surveyor = surveyorDao.getSurveyorDetail(mCode);
			surveyor.setDsrList(surveyorDao.getDsrList(surveyor.getId()));
			data.put("surveyor", surveyor);
			data.put("shops", shopsDao.getShopsDetail(surveyor.getId(), 0, 0,
					-1.0, -1.0, -1));
			data.put("remarks", applicationCacheService.getRemarks());
			data.put("remarksTypes", applicationCacheService.getRemarksTypes());
			data.put("shopCategories",
					applicationCacheService.getShopsCategories());
			data.put("shopGroups", applicationCacheService.getShopsGroups());
			data.put("areas", areaDao.getAreas());
			data.put("cities", citiesDao.getCities(surveyor.getId()));
			data.put("questions", applicationCacheService.getQuestions());
			data.put("industries", applicationCacheService.getIndustries());
			data.put("families", applicationCacheService.getFamilies());
			data.put("products", applicationCacheService.getProducts());
			data.put("bwus", applicationCacheService.getBwu());
			data.put("industryBwus", applicationCacheService.getIndustryBwu());
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

	private Map<String, Object> ndnLogin(String imei, String mCode,
			HttpServletResponse response) {

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			Surveyor surveyor = surveyorDao.getSurveyorDetail(mCode);

			if (surveyor.getSurveyorType().equals(SurveyorType.TM)) {
				List<Shop> shops = new ArrayList<Shop>();
				surveyor.setDsrList(surveyorDao.getDsrListByTMId(surveyor
						.getId()));
				// live request for de list build on selection of tm
				// Referance :::: /client/me/getDeList
				// data.put("deList",
				// surveyorDao.getSupervisorList(surveyor.getId()));

				/*
				 * data.put("shops",
				 * shopsDao.getShopsDetailForTM(surveyor.getId()));
				 */
				// shops data excluded from live build
				data.put("regions",
						regionDao.getTmRegions(surveyor.getId(), -1));
				data.put("shops", shops);
				data.put("areas", areaDao.getTmAreas(surveyor.getId()));
				data.put("visits",
						shopsDao.getShopsForTMAndME(surveyor.getId()));
				data.put("families", applicationCacheService.getFamilies());
			} else if (surveyor.getSurveyorType().equals(SurveyorType.DE) || surveyor.getSurveyorType().equals(SurveyorType.DE_TP)) {
				surveyor.setDsrList(surveyorDao.getDsrList(surveyor.getId()));
				data.put("shops", shopsDao.getShopsDetail(surveyor.getId(), -1,
						-1, -1.0, -1.0, -1));
				data.put("areas", areaDao.getSurveyorAreas(surveyor.getId()));
				data.put("visits", shopsDao.getShopVisits(surveyor.getId()));
				data.put(
						"wholesaleChannel",
						new ArrayList<String>(Arrays.asList(bundle
								.getString(SysConstants.WHOLE_SALE_CHANNEL)
								.trim().split(","))));
				data.put("families",
						surveyorDao.getFamiliesByRegion(surveyor.getId()));
				data.put("routes", shopsDao.getRoutes(surveyor.getId()));
				data.put("interceptionShops", shopsDao.interceptionShopList());
				data.put("promotionStockList", surveyorDao.getDEPromotionList(mCode, "-1"));
				data.put("activity", shopsDao.getDEActivity(surveyor.getId()));
				
			} else if (surveyor.getSurveyorType().equals(SurveyorType.ME)) {
				data.put("visits", shopsDao.getShopVisits(surveyor.getId()));
				data.put("families", applicationCacheService.getFamilies());
			}
			data.put("surveyor", surveyor);

			data.put("remarks", applicationCacheService.getRemarks());
			logger.info("remarks"+applicationCacheService.getRemarks().size());
			data.put("remarksTypes", applicationCacheService.getRemarksTypes());
			data.put("checkInInfo", surveyorDao
					.getSurveyorAttendanceAndDsrAttendance(surveyor.getId()));
			data.put("shopCategories",
					applicationCacheService.getShopsCategories());
			data.put("shopGroups", applicationCacheService.getShopsGroups());
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
			data.put("cities", citiesDao.getCities(surveyor.getId()));
			data.put("questions", applicationCacheService.getQuestions());
			data.put("options", applicationCacheService.getOptions());
			data.put("questionOptions",
					applicationCacheService.getQuestionOptions());
			data.put("industries", applicationCacheService.getIndustries());
			data.put("products", applicationCacheService.getProducts());
			data.put("bwus", applicationCacheService.getBwu());
			data.put("industryBwus", applicationCacheService.getIndustryBwu());
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

	private Map<String, Object> mondelezLogin(String imei, String mCode,
			HttpServletResponse response) {

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			Surveyor surveyor = surveyorDao.getSurveyorDetail(mCode);
			surveyor.setDsrList(surveyorDao.getDsrList(surveyor.getId()));
			data.put("surveyor", surveyor);
			data.put("shops", shopsDao.getShopsDetail(surveyor.getId(), 0, 0,
					-1.0, -1.0, -1));
			data.put("remarks", applicationCacheService.getActiveRemarks());
			data.put("productCategories",
					applicationCacheService.getProductCategories());
			data.put("productDisplay",
					applicationCacheService.getProductDisplay());
			data.put("areas", areaDao.getAreas());
			data.put("cities", citiesDao.getCities(surveyor.getId()));
			data.put("questions", applicationCacheService.getQuestions());
			data.put("industries", applicationCacheService.getIndustries());
			data.put("channels", applicationCacheService.getChannels());
			data.put("channelProducts",
					applicationCacheService.getChannelProducts());
			data.put("posm", applicationCacheService.getPosm());
			data.put("products", applicationCacheService.getProducts());
			data.put("assets", applicationCacheService.getAssets());

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
	public Map<String, Object> asmLogin(String imei, String mCode,
			HttpServletResponse response) {

		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			Surveyor surveyor = surveyorDao.getSurveyorDetail(mCode);
			surveyor.setDssList(surveyorDao.getDssList(surveyor.getAreaId()));
			data.put("surveyor", surveyor);
			data.put("remarks", applicationCacheService.getRemarks());
			data.put("remarksTypes", applicationCacheService.getRemarksTypes());
			data.put("shopCategories",
					applicationCacheService.getShopsCategories());
			data.put("shopGroups", applicationCacheService.getShopsGroups());
			data.put("areas", areaDao.getAreas());
			data.put("questions", applicationCacheService.getQuestions());
			data.put("industries", applicationCacheService.getIndustries());
			data.put("families", applicationCacheService.getFamilies());
			data.put("products", applicationCacheService.getProducts());
			data.put("bwus", applicationCacheService.getBwu());
			data.put("industryBwus", applicationCacheService.getIndustryBwu());
			jsonMap.put("data", data);
		} catch (Exception ex) {

			logger.error("Exception occured while asm login against imei "
					+ imei, ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return jsonMap;
	}

	@Override
	public Map<String, Object> getAsmShopDetail(int shopId,
			int merchandiserShopId, HttpServletResponse response) {

		Map<String, Object> shopData = new HashMap<String, Object>();
		try {
			shopData.put("syncShop",
					shopsDao.getAsmShopDetail(merchandiserShopId));
		} catch (Exception ex) {

			logger.error(
					"Exception occured while getting shop data against shop id  "
							+ shopId, ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return shopData;
	}

	@Override
	public Map<String, Object> getAsmShopData(int shopId,
			int merchandiserShopId, HttpServletResponse response) {

		Map<String, Object> shopData = new HashMap<String, Object>();
		try {
			shopData.put("industryData",
					industriesDao.getIndustryData(merchandiserShopId));
			shopData.put("familyData",
					familiesDao.getFamilyData(merchandiserShopId));
			shopData.put("productData",
					productsDao.getProductData(merchandiserShopId));
			shopData.put("pposmData", bwusDao.getPposmData(merchandiserShopId));
			shopData.put("questionData",
					questionsDao.getQuestionsData(merchandiserShopId));
		} catch (Exception ex) {

			logger.error(
					"Exception occured while getting shop data against shop id  "
							+ shopId, ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return shopData;
	}

	@Override
	public Map<String, Object> getAsmShopSummary(int areaId,
			HttpServletResponse response) {

		try {
			return shopsDao.getAsmShopSummary(areaId);
		} catch (Exception ex) {

			logger.error(
					"Exception occured while getting  shop summary against area id  "
							+ areaId, ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
	}

	@Override
	public Map<String, Object> getAsmDssShopSummary(int surveyorId,
			HttpServletResponse response) {

		try {
			return shopsDao.getAsmDssShopSummary(surveyorId);
		} catch (Exception ex) {

			logger.error(
					"Exception occured while getting  shop summary against surveyor id  "
							+ surveyorId, ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
	}

	@Override
	public Map<String, Object> getAsmShopStatusSummary(int surveyorId,
			AsmSummaryType asmSummaryType, HttpServletResponse response) {

		Map<String, Object> responseMap = null;
		try {
			switch (asmSummaryType) {

			case VERIFICATION:

				responseMap = shopsDao.getAsmVerficationStatus(surveyorId);
				break;
			case VIEWANDEDIT:

				responseMap = shopsDao.getAsmViewAndEditStatus(surveyorId);
				break;
			}
		} catch (Exception ex) {

			logger.error(
					"Exception occured while getting  shop ASM shop status  against surveyor id  "
							+ surveyorId, ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return responseMap;
	}

	@Override
	public Map<String, Object> getAsmShopList(int surveyorId,
			AsmSummaryType asmSummaryType, AsmShopStatus asmShopStatus,
			HttpServletResponse response) {

		Map<String, Object> shopData = new HashMap<String, Object>();
		try {
			switch (asmSummaryType) {

			case VERIFICATION:
				shopData.put("shops", shopsDao.getAsmVerificationShops(
						surveyorId, asmShopStatus));
				break;
			case VIEWANDEDIT:

				shopData.put("shops", shopsDao.getAsmViewAndEditShops(
						surveyorId, asmShopStatus));
				break;
			default:
				break;
			}
		} catch (Exception ex) {

			logger.error(
					"Exception occured while getting assigned shop data against surveyor id  "
							+ surveyorId, ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return shopData;
	}

	@Override
	public Map<String, Object> getSurveyorErrorList(int surveyorId,
			HttpServletResponse response) {

		Map<String, Object> errorData = new HashMap<String, Object>();
		try {
			errorData.put("surveyorError",
					surveyorDao.getSurveyorErrorList(surveyorId));
		} catch (Exception ex) {

			logger.error(
					"Exception occured while getting surveyor error list against surveyor id  "
							+ surveyorId, ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return errorData;
	}

	// By Hamed
	@Override
	public List<Region> getRegions(HttpServletResponse response) {
		// Map<String, Object> regionsData = new HashMap<String, Object>();
		List<Region> list = new ArrayList<Region>();
		try {
			list = applicationCacheService.getRegions();
		} catch (Exception ex) {

			logger.error(
					"Exception occured while getting List of regions from Application Cache Service.",
					ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return list;
	}

	@Override
	public List<Area> getAreas(int regionId, HttpServletResponse response) {
		/* Map<String, Object> areasBOData = new HashMap<String, Object>(); */
		List<Area> list = new ArrayList<Area>();
		try {
			/*
			 * areasBOData.put("areasBOData",applicationCacheService.
			 * getAreasByregionId(regionId));
			 */
			// areasBOData.put("areasBOData",applicationCacheService.getAreasByregionId(regionId));
			list = applicationCacheService.getAreasByregionId(regionId);
		} catch (Exception ex) {

			logger.error(
					"Exception occured while getting List of Areas BO from Application Cache Service.",
					ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return list;
	}

	@Override
	public List<Shop> getShops(int areaId, HttpServletResponse response) {
		List<Shop> list = new ArrayList<Shop>();

		try {
			list = shopsDao.getShopsDetail(-1, areaId, -1, -1.0, -1.0, -1);
		} catch (Exception ex) {
			logger.error(
					"Exception occured while getting List of Shops BO from Dao Layer.",
					ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return list;
	}

	@Override
	public Map<String, Object> tmLogin(String imei, String mCode,
			HttpServletRequest request, HttpServletResponse response) {

		if (SysConstants.PMI.equalsIgnoreCase(bundle.getString(
				SysConstants.PROJECT).trim())) {

			return pmiLogin(imei, mCode, response);
		} else if (SysConstants.NDN.equalsIgnoreCase(bundle.getString(
				SysConstants.PROJECT).trim())) {
			Build build = applicationCacheService.getBuildsMap().get("TM");
			logger.info("Applicaiton version : " + request.getHeader("version"));
			String version = request.getHeader("version");
			if (version.contains("_")) {
				version = version.split("_")[0];
			}
			if (Boolean.parseBoolean(bundle.getString(
					CommonConstants.VERSION_ALERT).trim())
					&& !build.getVersion().equalsIgnoreCase(version)) {
				response.setHeader("url", build.getDownloadUrl());
				response.setHeader("version", build.getVersion());
				CommonUtil.writeErrorMessage(
						HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED,
						MessageType.ERROR_VERSION, response);
				return null;
			} else {
				return ndnLogin(imei, mCode, response);
			}
		} else if (SysConstants.MONDELEZ.equalsIgnoreCase(bundle.getString(
				SysConstants.PROJECT).trim())) {

			return mondelezLogin(imei, mCode, response);
		}
		return null;
	}

	@Override
	public Map<String, Object> meLogin(String imei, String mCode,
			HttpServletRequest request, HttpServletResponse response) {

		if (SysConstants.PMI.equalsIgnoreCase(bundle.getString(
				SysConstants.PROJECT).trim())) {

			return pmiLogin(imei, mCode, response);
		} else if (SysConstants.NDN.equalsIgnoreCase(bundle.getString(
				SysConstants.PROJECT).trim())) {
			Build build = applicationCacheService.getBuildsMap().get("ME");
			logger.info("Applicaiton version : " + request.getHeader("version"));
			String version = request.getHeader("version");
			if (version.contains("_")) {
				version = version.split("_")[0];
			}
			if (!build.getVersion().equalsIgnoreCase(version)) {
				response.setHeader("url", build.getDownloadUrl());
				response.setHeader("version", build.getVersion());
				CommonUtil.writeErrorMessage(
						HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED,
						MessageType.ERROR_VERSION, response);
				return null;
			} else {
				return ndnLogin(imei, mCode, response);
			}
		} else if (SysConstants.MONDELEZ.equalsIgnoreCase(bundle.getString(
				SysConstants.PROJECT).trim())) {

			return mondelezLogin(imei, mCode, response);
		}
		return null;
	}

	@Override
	public Map<String, Object> getDesAndDsrsByActivityId(Integer activityId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("deList", surveyorDao.getDeListByActivityId(activityId));
		map.put("dsrList", surveyorDao.getDsrListByActivityId(activityId));
		return map;
	}

	@Override
	public Object getActivity(Integer tmId) {

		int activityId = 0;
		activityId = surveyorDao.getCurrentActivityByTmId(tmId);
		if (activityId == 0) {
			activityId = surveyorDao.insertActivity(tmId);
		}
		return surveyorDao.getActivityById(activityId);
	}

	@Override
	public Object getdeDetails(Integer deId) {
		DeDetails deDetails = new DeDetails();
		deDetails.setAreas(surveyorDao.getAreasBySurveyorId(deId));
		deDetails.setDsrs(surveyorDao.getDsrsBySurveyorId(deId));
		return deDetails;
	}

	@Override
	public List<Shop> getShopsForME(int areaId, int dsrId,
			HttpServletResponse response, Double latitude, Double longitude,
			int tmId) {

		List<Shop> list = new ArrayList<Shop>();
		try {
			// no need to send tmId for shoplist
			list = shopsDao.getShopsDetail(-1, areaId, dsrId, latitude,
					longitude, -1);
		} catch (Exception ex) {
			logger.error(
					"Exception occured while getting List of Shops for M.E Build.",
					ex);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
			return null;
		}
		return list;
	}

	@Override
	public List<Surveyor> getDeListByRegionId(int regionId) {

		return surveyorDao.getDeListByRegionId(regionId, -1, null);
	}

	@Override
	public List<Zone> getZones() {
		List<Zone> zones = new ArrayList<Zone>();
		zones = applicationCacheService.getZones();
		return zones;
	}

	@Override
	public List<Region> getRegionsByZoneId(int zoneId) {
		return surveyorDao.getRegionsByZoneId(zoneId);
	}

	@Override
	public ArrayList<SummaryTag> getSummaryForTM(int surveyorId, String type,
			String surveyorType) {
		SummaryData summaryData = new SummaryData();
		if (surveyorType.equalsIgnoreCase(SurveyorType.TM.toString())) {
			summaryData = surveyorDao.getSummaryForTM(surveyorId, type);
		} else {

			summaryData = surveyorDao.getSummaryForDE(surveyorId, type);
		}
		return summaryData.getSummarData();

	}

	@Override
	public List<Surveyor> getDeListByTmId(Integer tmId) {
		return surveyorDao.getSupervisorList(tmId, "DE");

	}

	@Override
	public String getServerTime() {
		return DateTimeUtilities
				.getCurrentDate(DateTimeConstants.DATE_TIME_FORMAT);
	}

	@Override
	public List<Map<String, Object>> getAttendanceSummary(String startDate,
			String endDate, Integer zoneId, Integer regionId) {
		return surveyorDao.getAttendanceSummary(startDate, endDate, zoneId,
				regionId);
	}

	@Override
	public List<Map<String, Object>> getSaleSummary(String startDate,
			String endDate, Integer deId, Integer regionId) {
		return surveyorDao.getSaleSummary(startDate, endDate, deId, regionId);
	}

	@Override
	public SyncShop getSyncShopData(Integer shopId) {
		return shopsDao.getSyncShopData(shopId);
	}

	@Override
	public ArrayList<SyncQuestion> getShopQuestionData(Integer shopId,
			Integer msId) {

		return shopsDao.getShopQuestionData(shopId, msId);
	}

	@Override
	public ArrayList<SyncFamily> getShopFamilyData(Integer shopId, Integer msId) {
		return shopsDao.getShopFamilyData(shopId, msId);

	}

	@Override
	public ArrayList<SyncIndustry> getShopIndustryData(Integer shopId,
			Integer msId) {
		return shopsDao.getShopIndustryData(shopId, msId);
	}

	@Override
	public ArrayList<SummaryTag> getDETimeSummary(Integer surveyorId,
			String type) {
		SummaryData summaryData = new SummaryData();
		summaryData = surveyorDao.getDETimeSummary(surveyorId, type);
		return summaryData.getSummarData();
	}

	@Override
	public ArrayList<SyncBwu> getShopBwuData(Integer shopId, Integer msId) {
		return shopsDao.getShopSyncBwuData(shopId, msId);
	}

	@Override
	public List<Region> getSisRegions(Integer userId, Integer zoneId,
			String type, float version) {
		return regionDao.getRegions(userId, zoneId, type, version);
	}

	@Override
	public List<Area> getSisAreas(Integer townId) {
		return applicationCacheService.getAreasByCityId(townId);
	}

	@Override
	public ArrayList<SyncFamily> getOffTakeLastVisit(Integer shopId) {
		return familiesDao.getOffTakeLastVisit(shopId);
	}

	@Override
	public List<Shop> getShops(int areaId, Integer townId, Integer regionId,
			String appType, HttpServletResponse response, Integer activityId,
			String type, String userType) {
		return shopsDao.getSisShopDetail(areaId, townId, regionId, appType,
				activityId, type, userType);
	}

	// this service will not be in use after sis build version 2.1
	@Override
	public List<BrandStock> getPreviousPurchaseStock(Integer shopId) {
		return familiesDao.getPreviousPurchaseStock(shopId);
	}

	@Override
	public void updateForceLoginStatus(String mCode) {
		surveyorDao.updateForceLoginStatus(mCode);
	}

	@Override
	public int insertSisActivity(int surveyorId, Integer townId,
			Integer shopsTarget, String title, String visitType,
			Integer regionId) throws Exception {
		return surveyorDao.insertSisActivity(surveyorId, townId, shopsTarget,
				title, visitType, regionId);

	}

	@Override
	public List<WorkActivity> getSisActivities(Integer townId, int surveyorId,
			String appType, String visitType, Integer regionId, float version) {
		return surveyorDao.getSisActivities(townId, surveyorId, appType,
				visitType, regionId, version);
	}

	@Override
	public void sisJoinActivityRequest(Integer activityId, Integer surveyorId,
			String isOwner, String requestStatus) throws Exception {
		surveyorDao.insertSisActivityRequest(surveyorId, isOwner, activityId,
				requestStatus);
	}

	@Override
	public List<WorkActivity> getSisActivityRequestList(Integer surveyorId) {
		return surveyorDao.getSisActivityRequestList(surveyorId);
	}

	@Override
	public void updateRequestStatus(int requestId, String requestStatus)
			throws Exception {
		surveyorDao.updateRequestStatus(requestId, requestStatus);
	}

	@Override
	public SyncData getSisShopData(HttpServletRequest request,
			HttpServletResponse response, Integer shopId) {
		SyncData syncData = new SyncData();

		try {
			surveyorDao.getSisVisitData(shopId, syncData);
			syncData.getVisit()
					.setQuestionData(
							surveyorDao.getSisQuestionData(syncData.getVisit()
									.getId()));
			syncData.getVisit().setFamilyData(
					surveyorDao.getSisFamilyData(syncData.getVisit().getId()));
		} catch (Exception e) {
			logger.error(e, e);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_FORBIDDEN,
					MessageType.ERROR_SERVER, response);
		}
		return syncData;
	}

	@Override
	public void endSisActivity(Integer activityId) throws Exception {
		surveyorDao.endSisActivity(activityId);

	}

	@Override
	public List<Surveyor> getSisSupervisors(Integer zoneId, Integer regionId) {
		return surveyorDao.getSisSupervisors(zoneId, regionId);
	}

	@Override
	public List<Family> getWSTKSSaleDetails(Integer shopId, String shopType) {
		return surveyorDao.getWSTKSSalesDeatils(shopId, shopType);
	}

	@Override
	public ArrayList<SummaryTag> getSummaryForSIS(Integer activity_id,
			String type, float version) {
		SummaryData summaryData = new SummaryData();
		summaryData = surveyorDao.getSummaryForSIS(activity_id, type, version);
		return summaryData.getSummarData();

	}

	@Override
	public void updateUserToken(int surveyorId, String userToken) {
		surveyorDao.updatUserToken(surveyorId, userToken);

	}

	@Override
	public List<BrandStock> getSisPreviousPurchaseStock(Integer regionId) {
		return familiesDao.getSisPreviousPurchaseStock(regionId);
	}

	@Override
	public List<Map<String, Object>> getDsrAttendanceForDe(String visitDate,
			Integer deId) {
		return surveyorDao.getDsrAttendanceForDe(visitDate, deId);
	}

	@Override
	public ArrayList<SummaryTag> getSummaryForDsr(int id, String type) {
		return surveyorDao.dsrSummary(id, type);

	}

	@Override
	public List<Shop> getDsrNewPendingShops(int deId, int dsrId,
			HttpServletResponse response) throws Exception {
		return shopsDao.getDsrPendingShops(deId, dsrId);
	}

	@Override
	public void updateDsrNewShopStatus(int shopId, String shopStatus,
			HttpServletResponse response) throws Exception {
		surveyorDao.updateDsrNewShopStatus(shopId, shopStatus);
	}
	
	
}
