package com.marksman.census.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.marksman.census.bo.AdminProfile;
import com.marksman.census.bo.Area;
import com.marksman.census.bo.BrandAssignment;
import com.marksman.census.bo.BrandPrice;
import com.marksman.census.bo.BrandPriceDetails;
import com.marksman.census.bo.City;
import com.marksman.census.bo.CityStockAssignment;
import com.marksman.census.bo.Employee;
import com.marksman.census.bo.Factory;
import com.marksman.census.bo.Family;
import com.marksman.census.bo.Industry;
import com.marksman.census.bo.RateAssignment;
import com.marksman.census.bo.Region;
import com.marksman.census.bo.Shop;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.client.bo.Attendance;
import com.marksman.census.client.bo.AttendanceForApproval;
import com.marksman.census.client.bo.ShopAssetImage;
import com.marksman.census.client.bo.StockTransfer;
import com.marksman.census.client.bo.TransferCodeDetails;
import com.marksman.census.constants.CommonConstants;
import com.marksman.census.constants.Roles;
import com.marksman.census.constants.SysConstants;
import com.marksman.census.dao.FamiliesDao;
import com.marksman.census.dao.PortalDao;
import com.marksman.census.message.MessageType;
import com.marksman.census.portal.bo.FinanceSetup;
import com.marksman.census.service.CacheService;
import com.marksman.census.service.CustomException;
import com.marksman.census.service.FileUploadService;
import com.marksman.census.service.InvalidHeadingException;
import com.marksman.census.service.PortalLoggingServiceImpl;
import com.marksman.census.service.PortalService;
import com.marksman.census.util.CommonUtil;
import com.marksman.census.util.DateTimeUtilities;
import com.marksman.census.util.FileUtils;
import com.marksman.census.util.SpringApplicationContext;

@Controller
public class PortalControllerImpl implements PortalController {
	protected Logger logger = Logger.getLogger(PortalControllerImpl.class);

	@Autowired
	PortalService portalService;
	@Autowired
	PortalDao portalDao;
	@Autowired
	ServletContext servletContext;
	@Autowired
	FileUploadService fileUploadService;
	@Autowired
	FileUtils fileUtils;
	@Autowired
	FamiliesDao familiesDao;
	@Autowired
	PortalLoggingServiceImpl portalLoggingService;
	@Autowired
	ApplicationCacheService applicationCacheService;
	@Autowired
	CacheService cacheService;

	@Override
	public @ResponseBody
	ModelAndView createLogin(String userName, String password, Integer typeId,
			HttpServletRequest request, HttpServletResponse response) {
		return portalService.createLogin(userName, password, typeId, response);
	}

	@Override
	public ModelAndView loginPage(HttpServletRequest request,
			HttpServletResponse response) {
		return portalService.loginPage();
	}

	@Override
	public ModelAndView logoutPage(HttpServletRequest request,
			HttpServletResponse response) {
		return portalService.logoutPage(request);
	}

	@Override
	public @ResponseBody
	ModelAndView portalLogin(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		if ("Y".equalsIgnoreCase(SpringApplicationContext.getAdmionProfile()
				.getIsNewPassword())) {
			return new ModelAndView("redirect:" + "/updatePassword");
		} else {
			switch (Roles.valueOf(SpringApplicationContext.getAuthority())) {
			case Routes_Manager:

				return new ModelAndView("redirect:" + "/portal/upload-file");
				/*
				 * case Data_Exporter:
				 * 
				 * return new ModelAndView("redirect:" + "/portal/export");
				 */
			default:

				return portalService.portalLogin(SpringApplicationContext
						.getAuthentication().getName());
			}
		}
	}

	@Override
	public ModelAndView loadShopsToAssign(int surveyorId,
			HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.loadShopsToAssign(surveyorId);
	}

	@Override
	public ModelAndView loadPendingShops(int surveyorId, int categoryId,
			HttpServletRequest request, HttpServletResponse response) {

		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.loadPendingShops(surveyorId, categoryId);
	}

	public ModelAndView basicScreening(int shopId, int merchandiserShopId,
			int surveyorId, String buildVersion, String shopClosed,
			HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.basicScreening(shopId, merchandiserShopId,
				surveyorId, buildVersion, shopClosed);
	}

	public ModelAndView loadLocation(Integer shopId,
			Integer merchandiserShopId, Integer surveyorId,
			String buildVersion, HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.locationValidation(shopId, merchandiserShopId,
				surveyorId, buildVersion);
	}

	public ModelAndView loadLocations(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return new ModelAndView("WEB-INF/jsp/multipleLocations");
	}

	public ModelAndView getPopup(HttpServletRequest request,
			HttpServletResponse response) {
		return new ModelAndView("WEB-INF/jsp/popup");
	}

	@Override
	public void validateShop(Integer shopId, Integer merchandiserShopId,
			String updatedValue, String oldValue, String validationAction,
			String validationStatus, Integer bwuId, Integer industryId,
			Integer surveyorId, HttpServletRequest request,
			HttpServletResponse response) {

		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		portalService.validateShop(shopId, merchandiserShopId, updatedValue,
				oldValue, validationAction, validationStatus, bwuId,
				industryId, surveyorId);
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
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		portalService.validateShopCategories(surveyorId, shopId,
				merchandiserShopId, categoryId, displayId, displayNumber,
				oldTotalFacings, oldMndlzFacings, oldGoldMndlzFacings,
				newTotalFacings, newMndlzFacings, newGoldMndlzFacings,
				validationAction, validationStatus, request, response);
	}

	@Override
	public void updateDisplayAuditProductFacing(Integer surveyorId,
			Integer shopId, Integer merchandiserShopId, Integer categoryId,
			Integer productId, Integer displayId, Integer displayNumber,
			String oldFacing, String newFacing, String validationAction,
			String validationStatus, HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		portalService.updateDisplayAuditProductFacing(surveyorId, shopId,
				merchandiserShopId, categoryId, productId, displayId,
				displayNumber, oldFacing, newFacing, validationAction,
				validationStatus, request, response);
	}

	@Override
	public void updateDisplayRemark(Integer surveyorId, Integer shopId,
			Integer merchandiserShopId, Integer categoryId, Integer displayId,
			Integer displayNumber, Integer displayRemark,
			HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		portalService.updateDisplayRemark(surveyorId, shopId,
				merchandiserShopId, categoryId, displayId, displayNumber,
				displayRemark, request, response);
	}

	@Override
	public ModelAndView visibility(int shopId, int merchandiserShopId,
			int surveyorId, Integer categoryId, String buildVersion,
			String chillerType, HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.visibility(shopId, merchandiserShopId, surveyorId,
				categoryId, buildVersion, chillerType);
	}

	@Override
	public ModelAndView hotZone(Integer merchandiserShopId, Integer surveyorId,
			Integer shopId, Integer displayId, Integer displayNumber,
			String buildVersion, HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.hotZone(merchandiserShopId, surveyorId, shopId,
				displayId, displayNumber, buildVersion, request, response);
	}

	@Override
	public ModelAndView primaryShelf(Integer merchandiserShopId,
			Integer surveyorId, Integer shopId, Integer displayId,
			Integer displayNumber, String buildVersion,
			HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.primaryShelf(merchandiserShopId, surveyorId,
				shopId, displayId, displayNumber, buildVersion, request,
				response);
	}

	@Override
	public ModelAndView volumeCheck(int shopId, int merchandiserShopId,
			int surveyorId, String buildVersion, HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.volumeCheck(shopId, merchandiserShopId,
				surveyorId, buildVersion);
	}

	@Override
	public @ResponseBody
	void saveAssignedShops(Integer surveyorId, String shops,
			String merchandiserShopId, HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		portalService.saveAssignedShops(surveyorId, shops, merchandiserShopId);
	}

	@Override
	public ModelAndView updatePassword(String newPassword,
			HttpServletRequest request, HttpServletResponse response) {
		return portalService.updatePassword(newPassword, response);
	}

	@Override
	public ModelAndView loadBwuImages(int merchandiserShopId, int shopId,
			int industryId, int bwuId, HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.loadBwuImages(merchandiserShopId, shopId,
				industryId, bwuId);
	}

	@Override
	public ModelAndView shopsTag(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.shopsTag();
	}

	@Override
	public ModelAndView duplicateClusters(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.duplicateClusters();
	}

	@Override
	public ModelAndView duplicateShops(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, String longitude, String latitude,
			HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		return portalService.duplicateShops(zoneId, regionId, townId, areaId,
				longitude, latitude);
	}

	@Override
	public @ResponseBody
	Map<String, Object> viewShopsTag(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, String startDate, String endDate,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			responseMap.put("shopsData", portalService.viewShopsTag(zoneId,
					regionId, townId, areaId, startDate, endDate));
		} catch (Exception ex) {

			logger.error("Error while view shops Tag", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return responseMap;
	}

	@Override
	public @ResponseBody
	Map<String, Object> getDuplicateClusters(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			responseMap.put("clusterData", portalService.getDuplicateClusters(
					zoneId, regionId, townId, areaId));
		} catch (Exception ex) {

			logger.error("Error while view shops Tag", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return responseMap;
	}

	@Override
	public ModelAndView getShopRemarks(HttpServletRequest request,
			HttpServletResponse response, Integer shopId, Integer surveyorId,
			Integer merchandiserShopId, Integer remarkId, String requestType) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.getShopRemarks(shopId, requestType, surveyorId,
				merchandiserShopId, remarkId);
	}

	@Override
	public ModelAndView getShopAssetRemarks(HttpServletRequest request,
			HttpServletResponse response, Integer shopId, Integer surveyorId,
			Integer merchandiserShopId, Integer remarkId, String requestType,
			Integer merchandiserAssetId) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		return portalService.getShopAssetRemarks(shopId, requestType,
				surveyorId, merchandiserShopId, remarkId, merchandiserAssetId);
	}

	@Override
	public void reEvaluateShop(HttpServletRequest request,
			HttpServletResponse response, Integer shopId) {
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		portalService.reEvaluateShop(shopId);
	}

	@Override
	public @ResponseBody
	List<Map<String, Object>> getMerchandiserList(HttpServletRequest request,
			HttpServletResponse response, String visitDate, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"MERCHANDISER_LIST");
		return portalService.getMerchandiserListMap(visitDate);

	}

	@Override
	public @ResponseBody
	List<Map<String, Object>> getMerchandiserShopList(
			HttpServletRequest request, HttpServletResponse response,
			Integer surveyorId, String startDate, String endDate) {
		return portalService.getMerchandiserShopListMap(surveyorId, startDate,
				endDate);
	}

	@Override
	public @ResponseBody
	Map<String, Object> getSectionList(HttpServletRequest request,
			HttpServletResponse response, Integer merchandiserShopId,
			Integer userTypeId, String visitType, Integer userId,
			String surveyorType) {
		portalLoggingService.insertActivityLog(request, response,
				"SECTION_LIST");
		return portalService.getSectionListMap(merchandiserShopId, userTypeId,
				visitType, surveyorType);
	}

	// By Muhammad Hamed Kamal.
	@Override
	public @ResponseBody
	Map<String, Object> getAuthForPortal(HttpServletRequest request,
			HttpServletResponse response, String userName, String password) {
		logger.info("Login PMI");
		Map<String, Object> map = new HashMap<String, Object>();
		map = portalService.getAuthForPortal(request, response, userName,
				password);
		portalLoggingService.insertActivityLog(request, response,
				"PORTAL_LOGIN_AUTHENTICATION");
		return map;

	}

	// By Muhammad Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getMerchandiserShopListNDN(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId, Integer zoneId, Integer cityId,
			Integer distributionId, String storeType, Integer channelId,
			String startDate, String endDate, Integer rteId,
			Integer surveyorId, String nonEvaluated, Integer userId,
			String zones, String regions) {
		portalLoggingService.insertActivityLog(request, response,
				"MERCHANDISER_SHOP_LIST");
		return portalService.getMerchandiserShopListNDN(regionId, zoneId,
				cityId, distributionId, storeType, channelId, startDate,
				endDate, rteId, surveyorId, nonEvaluated, zones, regions);
	}

	// By Muhammad Hamed
	@Override
	@ResponseBody
	public ArrayList<ShopAssetImage> getCompletedShopListNDN(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, String startDate, String endDate,
			Integer merchandiserId, Integer remarksId, String visitType,
			Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"COMPLETED_SHOP_LIST");
		return portalService.getCompletedShopListNDN(zoneId, regionId,
				startDate, endDate, merchandiserId, remarksId, visitType);
	}

	@Override
	@ResponseBody
	public ArrayList<ShopAssetImage> getCompletedVisitShopListNDN(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, String startDate, String endDate,
			Integer merchandiserId, Integer remarksId, String visitType,
			String evaluated) {
		return portalService.getCompletedVisitShopListNDN(zoneId, regionId,
				startDate, endDate, merchandiserId, remarksId, visitType,
				evaluated);
	}

	@Override
	@ResponseBody
	public List<Region> getRegions(HttpServletRequest request,
			HttpServletResponse response, Integer userId) {
		portalLoggingService
				.insertActivityLog(request, response, "REGION_LIST");
		return portalService.getRegions(userId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDesAndDsrs(HttpServletRequest request,
			HttpServletResponse response, Integer regionId) {
		return portalService.getDesAndDsrs(regionId);
	}

	// By Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getDsrDetailsForPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer surveyorId, Integer regionId, String status) {
		// TODO Auto-generated method stub
		return portalService.getDsrsDetails(surveyorId, regionId, status);
	}

	// By Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getDsrAreasDetailsForPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer dsrId, Integer regionId) {
		return portalService.getDsrAreasDetails(dsrId, regionId);
	}

	// By Hamed
	@Override
	@ResponseBody
	public Map<String, Object> uploadImeisOnPortal(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		Gson gson = new Gson();
		try {
			ServletFileUpload upload = new ServletFileUpload();
			String fileName = "";
			String fileUrl = null;
			String destFileName = null;
			String path = "";
			List<String> imeiList = new ArrayList<String>();
			try {
				FileItemIterator iterator = upload.getItemIterator(request);
				String singleImei = "";
				while (iterator.hasNext()) {
					FileItemStream item = iterator.next();

					if (!item.isFormField()) {
						String curruntDate = DateTimeUtilities.getDate();
						String fileDir = servletContext.getRealPath("/");
						fileDir = fileDir + "Imeis/" + curruntDate;
						if (!FileUtils.isExistsFolder(fileDir)) {
							FileUtils.createFolders(fileDir);
						}
						fileName = item.getName();

						fileUrl = fileDir + "/" + fileName;
						int destFileID = portalDao.getImeisExcelFileId(fileDir,
								fileName, fileUrl);
						destFileName = destFileID + ".xlsx";
						logger.info("Imei Number : " + item.getName());
						path = FileUtils.storeFile(fileDir + "/", fileName,
								item);
					} else {
						InputStream stream = item.openStream();
						String reader = Streams.asString(stream);
						/*
						 * logger.info("Build Version " +
						 * request.getHeader("version") + "\n validated Shop : "
						 * + reader);
						 */
						singleImei = gson.fromJson(reader, String.class);
						if (singleImei != null) {
							imeiList.add(singleImei);
						}
					}

				}
				String message = "";
				String extension = FilenameUtils.getExtension(fileName);
				if (CommonConstants.XLSX_FILE.equalsIgnoreCase(extension)) {
					imeiList = fileUploadService.readExcelFileForIMES(path);
				}

				portalDao.insertImeis(imeiList);
				map.put("message", "Imeis Added into System.");

			} catch (Exception ex) {

				logger.error("Error while uploading excel file", ex);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.FILE_SAVING_SERVER, response);
			return null;
		}
		return map;
	}

	// By Hamed
	@Override
	@ResponseBody
	public Map<String, Object> updateDeName(HttpServletRequest request,
			HttpServletResponse response, Integer surveyorId, String deName,
			Integer userId, String deStatus, Integer employeeId,
			float basicSalary, float dailyAllownce, float fuelAllownce,
			float maintainanceAllownce, float otherAllowances) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_DE_NAME");
		return portalService.updateDeName(surveyorId, deName, userId, deStatus,
				employeeId, basicSalary, dailyAllownce, fuelAllownce,
				maintainanceAllownce, otherAllowances);
	}

	// By Hamed
	@Override
	@ResponseBody
	public Map<String, Object> updateDsrDetails(HttpServletRequest request,
			HttpServletResponse response, Integer surveyorId, String dsrName,
			Integer dsrId, String dsrStatus, Integer userId, String dsrType,
			Integer dsrEmployee, float basicSalary, float dailyAllownce,
			float fuelAllownce, float maintainanceAllownce,
			float otherAllowances, float vanSalary) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_DSR_DETAIL");
		return portalService.updateDsrDetails(surveyorId, dsrName, dsrId,
				dsrStatus, userId, dsrType, dsrEmployee, basicSalary,
				dailyAllownce, fuelAllownce, maintainanceAllownce,
				otherAllowances, vanSalary);
	}

	// By Hamed
	@Override
	@ResponseBody
	public Map<String, Object> updateDsrAreasDetails(
			HttpServletRequest request, HttpServletResponse response,
			Integer dsrId, String blockName, Integer blockId, Integer userId,
			String blockStatus, String dsrType, Integer dsrVanId, Integer townId) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_DSR_AREAS_DETAIL");
		return portalService.updateDsrAreasDetails(dsrId, blockName, blockId,
				userId, blockStatus, dsrType, dsrVanId, townId);
	}

	// By Hamed
	@Override
	@ResponseBody
	public Map<String, Object> addDsrFromPortal(HttpServletRequest request,
			HttpServletResponse response, Integer surveyorId, String dsrName,
			Integer userId, String dsrType, Integer dsrEmployee) {
		portalLoggingService
				.insertActivityLog(request, response, "ADD_NEW_DSR");
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			logger.debug(" Add Dsr From Portal Service");
			portalService.addDsrFromPortal(surveyorId, dsrName, userId,
					dsrType, dsrEmployee);
			map.put("status", true);
			map.put("message", "dsr added into system.");
			return map;

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
			map.put("status", true);
			map.put("message", "dsr added into system.");
			return map;
		}
	}

	// By Hamed
	@Override
	@ResponseBody
	public Map<String, Object> addBlockFromPortal(HttpServletRequest request,
			HttpServletResponse response, String blockName, Integer townId,
			Integer dsrId, Integer userId, Integer dsrVanId,
			Integer exportedAreaId) {
		portalLoggingService.insertActivityLog(request, response,
				"ADD_NEW_BLOCK");
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			logger.debug(" Add Dsr From Portal Service");
			portalService.addBlockFromPortal(blockName, townId, dsrId, userId,
					dsrVanId, exportedAreaId);
			map.put("status", true);
			map.put("message", "Block added into system.");
			return map;

		} catch (Exception ex) {

			logger.error(ex);
			map.put("status", false);
			map.put("message", "Block not added into system.");
			return map;
		}
	}

	// By hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getDsrs(HttpServletRequest request,
			HttpServletResponse response, Integer surveyorId, String dsrType) {
		return portalService.getDsrs(surveyorId, dsrType);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDsrsByRegions(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId) {
		return portalService.getDsrsByRegionId(regionId);
	}

	// By Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getTowns(HttpServletRequest request,
			HttpServletResponse response, Integer regionId) {
		// applicationCacheService.getCitiesByRegionId(regionId);
		return portalService.getTowns(regionId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getRegionsByZoneId(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId) {

		// check added if hit from tm build to send all region for stock
		// transfer
		if (request.getHeader("surveyorType") != null
				&& request.getHeader("surveyorType").equalsIgnoreCase("TM")) {
			return portalService.getRegionsByZoneId(-1);
		} else {
			return portalService.getRegionsByZoneId(zoneId);
		}
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getRegionsByZoneIdNew(
			HttpServletRequest request, HttpServletResponse response,
			String zoneId) {

		return portalService.getRegionsByZoneIdNew(zoneId);
	}

	// By Sajid
	@Override
	@ResponseBody
	public List<Map<String, Object>> getRemarksList(HttpServletRequest request,
			HttpServletResponse response, String typeId) {
		return portalService.getRemarks(typeId);
	}

	// By Hamed
	@Override
	@ResponseBody
	public Map<String, Object> submitTransferedStock(
			HttpServletRequest request, HttpServletResponse response) {

		/* return portalService.submitTransferedStock(stockTransfered); */

		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			// ArrayList<BwuImage> bwuImages = new ArrayList<BwuImage>();
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			/* SyncData syncData = null; */
			StockTransfer stockTransfer = null;
			if (isMultiPart) {
				ServletFileUpload upload = new ServletFileUpload();
				try {

					FileItemIterator itr = upload.getItemIterator(request);
					while (itr.hasNext()) {
						FileItemStream item = itr.next();
						if (item.isFormField()) {
							InputStream stream = item.openStream();
							String reader = Streams.asString(stream);
							/*
							 * logger.info("Build Version " +
							 * request.getHeader("version") +
							 * "\n validated Shop : " + reader);
							 */
							stockTransfer = gson.fromJson(reader,
									StockTransfer.class);
						}
					}
				} catch (Exception ex) {
					logger.error("Error while saving Stock Transfer Data  : ",
							ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					logger.info("Error :", ex);
					return null;
				}
				// logger.info("Stock Submit Data: " + stockTransfer);
				if (stockTransfer != null) {

					try {

						portalService.submitTransferedStock(stockTransfer);
						responMap.put("status", true);
						if (stockTransfer.getTransferStatus().equalsIgnoreCase(
								"SENT")) {
							responMap.put("description",
									"Stock Transferred Successfully..!!");
						} else {
							responMap.put("description",
									"Stock Received Successfully..!!");
						}
					} catch (IllegalStateException ex) {

						logger.error(ex, ex);
						responMap.put("status", false);
						responMap.put("description",
								"Stcok Family data not provided.");
					} catch (Exception e) {
						logger.error(e, e);
						responMap.put("status", false);
						responMap.put("description",
								"Stock Not Transferred Successfully..!!");
					}

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

		return responMap;
	}

	// By Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getImeisforPortal(
			HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return portalService.getImeisforPortal();
	}

	// By Sajid
	@Override
	@ResponseBody
	public List<Map<String, Object>> getDsrSaleTargetforPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer month, Integer year, Integer regionId, Integer townId,
			Integer zoneId, Integer type) {
		showParameters(request);
		return portalService.getDsrSaleTargetforPortal(month, year, regionId,
				townId, zoneId, type);
	}

	// By Hamed
	@Override
	@ResponseBody
	public Map<String, Object> updateImeiFromPortal(HttpServletRequest request,
			HttpServletResponse response, String imei, String imeiStatus) {
		// TODO Auto-generated method stub
		return portalService.updateImeiFromPortal(imei, imeiStatus);
	}

	// By Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getFamiliesForPortal(
			HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return familiesDao.getFamiliesForPortal();
	}

	// By Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getTerritoryFamiliesForPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer territoryId) {
		// TODO Auto-generated method stub
		return familiesDao.getTerritoryFamiliesForPortal(territoryId);
	}

	// By Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getTownFamiliesForPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer townId) {
		// TODO Auto-generated method stub
		/*
		 * Map<String, Object> map = new HashMap<String, Object>();
		 * map.put("townFamilies",
		 * familiesDao.getTownFamiliesForPortal(townId));
		 */
		return familiesDao.getTownFamiliesForPortal(townId);
	}

	// By Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getTerritoryReceivedStock(
			HttpServletRequest request, HttpServletResponse response,
			String transferType, Integer territoryId, String transferCode) {
		// TODO Auto-generated method stub
		return portalService.getTerritoryReceivedStock(transferType,
				territoryId, transferCode);
	}

	// By Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getTerritoryReceivedStockTransferCodes(
			HttpServletRequest request, HttpServletResponse response,
			String transferType, Integer territoryId) {
		// TODO Auto-generated method stub
		return portalDao.getTerritoryReceivedStockTransferCodes(transferType,
				territoryId);
	}

	// By Hamed
	@Override
	@ResponseBody
	public Map<String, Object> submitReceivedStock(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			// ArrayList<BwuImage> bwuImages = new ArrayList<BwuImage>();
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			StockTransfer stockTransfer = null;
			if (isMultiPart) {
				ServletFileUpload upload = new ServletFileUpload();
				try {

					FileItemIterator itr = upload.getItemIterator(request);
					while (itr.hasNext()) {
						FileItemStream item = itr.next();
						if (item.isFormField()) {
							InputStream stream = item.openStream();
							String reader = Streams.asString(stream);
							stockTransfer = gson.fromJson(reader,
									StockTransfer.class);
						}
					}
				} catch (Exception ex) {
					logger.error("Error while saving Stock Transfer Data  : ",
							ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					logger.info("Error :", ex);
					return null;
				}

				if (stockTransfer != null) {

					try {

						portalService.submitReceivedStock(stockTransfer);
						responMap.put("status", true);
						responMap.put("message",
								"Stock Received Successfully..!!");
					} catch (IllegalStateException ex) {
						ex.printStackTrace();
						logger.error("Error", ex);
						responMap.put("status", false);
						responMap.put("message",
								"Stcok Family data not provided.");
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("Error", e);
						responMap.put("status", false);
						responMap.put("message",
								"Stock NOT Received Successfully..!!");
					}

					/*
					 * logger.info("Sending Shop id in response " +
					 * " shop id : " +syncData.getVisit().getShopId());
					 */
					// responMap.put("syncResponse",CommonUtil.getResponseObj(syncData.getVisit().getShopId(),
					// -1));
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
			e.printStackTrace();
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.FILE_SAVING_SERVER, response);
			return null;
		}

		return responMap;
	}

	@Override
	@ResponseBody
	public Map<String, Object> checkTransferCodeAvailability(String transferCode) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("valid", portalDao.isValidTransferCode(transferCode));
		return map;
	}

	// BY Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getReceivedStockCodes(
			HttpServletRequest request, HttpServletResponse response) {
		return portalService.getReceivedStockCodes(request);
	}

	// By Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getReceivedStockCodesForPortal(
			HttpServletRequest request, HttpServletResponse response) {
		return portalService.getReceivedStockCodesForPortal(request);
	}

	@Override
	@ResponseBody
	public TransferCodeDetails getTransitDetails(HttpServletRequest request,
			HttpServletResponse response, String transitCode, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"Transit_Details");
		return portalService.getTransitDetails(transitCode);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getReceivedStock(
			HttpServletRequest request, HttpServletResponse response,
			Integer transferId, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"RECEVIED_STOCK");
		return portalService.getReceivedStock(transferId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getFactoryStockFamiliesForPortal(
			Integer factoryId) {
		// TODO Auto-generated method stub
		return portalService.getFactoryStockFamilies(factoryId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDistributionList(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId, Integer cityId) {

		return portalService.getDistributionList(regionId, cityId);
	}

	// public Map<String, Object> updateDistributionLatLong(
	// HttpServletRequest request, HttpServletResponse response,
	// Integer distributionId, double distLat, double distLong) {
	// return portalService.updateDistributionLatLong(distributionId, distLat,
	// distLong);
	// }

	// By Sajid
	@Override
	@ResponseBody
	public Map<String, Object> uploadDsrSaleTargetOnPortal(
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<String> validatorArray = new ArrayList<String>();
		Gson gson = new Gson();
		try {
			ServletFileUpload upload = new ServletFileUpload();
			String fileName = "";
			String fileUrl = null;
			String destFileName = null;
			String path = "";
			Map<Integer, Map<String, String>> DsrSaleTargetList = new HashMap<Integer, Map<String, String>>();
			try {
				FileItemIterator iterator = upload.getItemIterator(request);
				while (iterator.hasNext()) {
					FileItemStream item = iterator.next();

					if (!item.isFormField()) {
						String curruntDate = DateTimeUtilities.getDate();
						String fileDir = servletContext.getRealPath("/");
						fileDir = fileDir + "SaleTarget/" + curruntDate;
						if (!FileUtils.isExistsFolder(fileDir)) {
							FileUtils.createFolders(fileDir);
						}
						fileName = item.getName();

						fileUrl = fileDir + "/" + fileName;
						int destFileID = portalDao.getImeisExcelFileId(fileDir,
								fileName, fileUrl);
						destFileName = destFileID + ".xlsx";
						logger.info("DSR Sale Target : " + item.getName());
						path = FileUtils.storeFile(fileDir + "/", fileName,
								item);
					} else {

					}

				}
				String message = "";
				String extension = FilenameUtils.getExtension(fileName);
				if (CommonConstants.XLSX_FILE.equalsIgnoreCase(extension)) {
					DsrSaleTargetList = fileUploadService
							.readExcelFileForDsrSaleTarget(path, validatorArray);
				}
				portalDao.insertSaleTargetData(DsrSaleTargetList);
				map.put("success", true);
				map.put("message", "DSR sale target data uploaded");

			} catch (InvalidHeadingException ex) {
				logger.error(ex, ex);

				map.put("success", false);
				map.put("message", ex);
			} catch (CustomException ex) {
				logger.error(ex, ex);

				map.put("success", false);
				map.put("message", ex);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.FILE_SAVING_SERVER, response);
			return null;
		}
		return map;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getBlockWiseShopList(
			HttpServletRequest request, HttpServletResponse response,
			Integer areaId, Integer regionId, Integer surveyorId, Integer dsrId) {

		return portalService.getBlockWiseShopList(areaId, regionId, surveyorId,
				dsrId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> checkTransferShops(HttpServletRequest request,
			HttpServletResponse response, String shops, Integer areaId,
			Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"TRANSFER_SHOP");
		return portalService.transferShops(shops, areaId, userId);

	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getSurveyorList(
			HttpServletRequest request, HttpServletResponse response) {
		return portalService.getSurveyorList();
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateDistribution(HttpServletRequest request,
			HttpServletResponse response, Integer distributionId,
			double distLat, double distLong, String distName, Integer totalDE,
			Integer totalDSR, Integer investment, String distType,
			Integer regionId, Integer cityId, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_DISTRIBUTION");
		return portalService.updateDistribution(distributionId, distLat,
				distLong, distName, totalDE, totalDSR, investment, distType,
				regionId, cityId);
	}

	@Override
	@ResponseBody
	public List<Factory> getFactoryList(HttpServletRequest request,
			HttpServletResponse response, String type) {
		// TODO Auto-generated method stub
		return portalService.getFactoryList(type);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDeAttendanceList(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId, String startDate, String endDate,
			String attendanceType, Integer zoneId) {

		return portalService.getDeAttendanceList(regionId, startDate, endDate,
				attendanceType, zoneId);

	}

	@Override
	@ResponseBody
	public Map<String, Object> updateDeAttendanceWorkType(
			HttpServletRequest request, HttpServletResponse response,
			Integer Id, Integer workTypeId, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_DE_ATTENDANCE_WORK_TYPE");
		return portalService.updateWorkType(Id, workTypeId, userId);
	}

	// By Hamed
	@Override
	@ResponseBody
	public List<Map<String, Object>> getTmProductivityNDN(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId, Integer zoneId, Integer cityId,
			Integer distributionId, String storeType, Integer channelId,
			String startDate, String endDate, Integer rteId,
			Integer surveyorId, Integer userId, String zones, String regions) {
		portalLoggingService.insertActivityLog(request, response,
				"TM_PRODUCTIVITY");
		return portalService.getTmProductivityNDN(regionId, zoneId, cityId,
				distributionId, storeType, channelId, startDate, endDate,
				rteId, surveyorId, zones, regions);
	}

	@Override
	@ResponseBody
	public Map<String, Object> evaluateSingleShop(HttpServletRequest request,
			HttpServletResponse response) {

		JSONObject json = extractJsonObject(request, response);
		portalLoggingService.insertActivityLog(request, response,
				json.toString());
		if (json.getString("isEvaluated").equalsIgnoreCase("Y")) {
			portalDao.removeExistingScore(json.getInt("surveyId"));
		}
		return portalService.insertEvaluationScore(json);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getEmployeeList(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId) {
		return portalService.getEmployeeList(regionId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> transferBlocks(HttpServletRequest request,
			HttpServletResponse response, String blocks, Integer dsrId,
			Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"TRANSFER_BLOCK");
		return portalService.transferBlocks(blocks, dsrId, userId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getQueryTypeList(
			HttpServletRequest request, HttpServletResponse response,
			Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"QUERY_TYPE_LIST");
		return portalService.getQueryTypeList();
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getMerchandiserDsrSalesList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer userId,
			Integer familyId, String currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		logger.info("current date " + formatter.format(date));
		if (formatter.format(date).toString().equalsIgnoreCase(currentDate)) {
			portalLoggingService.insertActivityLog(request, response,
					"MERCHANDISER_DSR_SALE_LIST");
			return portalService.getMerchandiserDsrSalesList(startDate,
					endDate, zoneId, regionId, surveyorId, dsrId, familyId);
		} else {
			return null;
		}
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getMerchandiserSpotSellingList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer userId,
			Integer familyId, String currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		logger.info("current date " + formatter.format(date));
		if (formatter.format(date).toString().equalsIgnoreCase(currentDate)) {
			portalLoggingService.insertActivityLog(request, response,
					"MERCHANDISER_SPOT_SELLING_LIST");
			return portalService.getMerchandiserSpotSellingList(startDate,
					endDate, zoneId, regionId, surveyorId, dsrId, familyId);
		} else {
			return null;
		}
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateDsrSales(HttpServletRequest request,
			HttpServletResponse response, Integer dsrSaleId, float retailValue,
			float wholeSaleValue, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_DSR_SALE");
		return portalService.updateDsrSales(dsrSaleId, retailValue,
				wholeSaleValue, userId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateSpotSellingValue(
			HttpServletRequest request, HttpServletResponse response,
			Integer spotsellingId, float value, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_DSR_SALE");
		return portalService.updateSpotSellingValue(spotsellingId, value,
				userId);
	}

	@Override
	@ResponseBody
	public List<TransferCodeDetails> getTransactionsData(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, String transferType,
			Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"TRANSACTION_DATA");
		return portalService.getTransactionsData(startDate, endDate,
				transferType);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getTransactionStockDetails(
			HttpServletRequest request, HttpServletResponse respons,
			Integer transferId, Integer userId) {
		portalLoggingService.insertActivityLog(request, respons,
				"TRANSACTION_STOCK_DETAIL");
		return portalService.getTransactionStockDetails(transferId);
	}

	@Override
	@ResponseBody
	public List<Surveyor> getDeListByRegionIdForPortal(
			HttpServletRequest request, HttpServletResponse respons,
			Integer regionId, Integer zoneId, String surveyorType) {
		return portalService
				.getDeListByRegionId(regionId, zoneId, surveyorType);
	}

	@Override
	@ResponseBody
	public Map<String, Object> saveDSRSale(HttpServletRequest request,
			HttpServletResponse respons, String date, Integer surveyorId,
			Integer dsrId, Integer familyId, float retailValue,
			float wholeSaleValue, Integer userId, Integer cityId) {
		portalLoggingService.insertActivityLog(request, respons,
				"SAVE_DSR_SALE");
		return portalService.saveDSRSale(date, surveyorId, dsrId, familyId,
				retailValue, wholeSaleValue, cityId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> reverseTransaction(HttpServletRequest request,
			HttpServletResponse respons, Integer transactionId, Integer userId) {

		portalLoggingService.insertActivityLog(request, respons,
				"REVERSE_TRANSACTION");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			portalService.reverseTransaction(transactionId);
			map.put("transactionReversed", true);
			map.put("message", "Transaction has been reveresed.");
		} catch (Exception e) {
			logger.debug("Exception in Reverse Transaction " + e);
			map.put("transactionReversed", false);
			map.put("message", "Transaction has not been reveresed.");
		}
		return map;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDSRsByDEId(HttpServletRequest request,
			HttpServletResponse respons, Integer deId) {
		return portalService.getDSRsByDEId(deId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> getUsersForAttendanceApprovals(
			HttpServletRequest request, HttpServletResponse respons,
			String userType, String selectedDate, Integer zoneId,
			Integer regionId, Integer userId) {
		portalLoggingService.insertActivityLog(request, respons,
				"ATTENDANCE_APPROVAL");
		List<Attendance> attendanceList;
		Map<String, Object> responseMap = new HashMap<String, Object>();
		attendanceList = portalService.getUsersForAttendanceApprovals(userType,
				selectedDate, zoneId, regionId);
		if (attendanceList.size() > 0) {
			responseMap.put("status", true);
			responseMap.put("description", "Data Found...!");
			responseMap.put("data", attendanceList);
		} else {
			responseMap.put("status", false);
			responseMap.put("description", "No Data Found....!");
			responseMap.put("data", attendanceList);
		}
		return responseMap;
	}

	@Override
	@ResponseBody
	public Map<String, Object> UpdateAttendanceForApprovals(
			HttpServletRequest request, HttpServletResponse response) {
		// showParameters(request, surveyorId);
		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			AttendanceForApproval attendanceForApproval = null;
			if (isMultiPart) {
				ServletFileUpload upload = new ServletFileUpload();
				try {

					FileItemIterator itr = upload.getItemIterator(request);
					while (itr.hasNext()) {
						FileItemStream item = itr.next();
						if (item.isFormField()) {
							InputStream stream = item.openStream();
							String reader = Streams.asString(stream);
							attendanceForApproval = gson.fromJson(reader,
									AttendanceForApproval.class);
						}
					}
				} catch (Exception ex) {
					logger.error(
							"Error while saving DE Attendance Approval Data  : ",
							ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					logger.info("Error :", ex);
					return null;
				}

				if (attendanceForApproval != null) {
					try {
						portalService.UpdateAttendanceForApprovals(
								attendanceForApproval.getApprovedAttendance(),
								attendanceForApproval.getChooseDate(),
								attendanceForApproval.getSurveyorId());
						responMap.put("status", true);
						responMap.put("description",
								"Attendance has been approved....");
					} catch (IllegalStateException ex) {
						logger.error("Error", ex);
						responMap.put("status", false);
						responMap
								.put("description", "Something Went Wrong....");
					} catch (Exception e) {
						logger.error("Error", e);
						responMap.put("status", false);
						responMap.put("description",
								"Something Went Wrong....!!");
					}

					/*
					 * logger.info("Sending Shop id in response " +
					 * " shop id : " +syncData.getVisit().getShopId());
					 */
					// responMap.put("syncResponse",CommonUtil.getResponseObj(syncData.getVisit().getShopId(),
					// -1));
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
			e.printStackTrace();
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.FILE_SAVING_SERVER, response);
			return null;
		}

		return responMap;

		/*
		 * JSONObject json = extractJsonObject(request, respons); JSONArray
		 * jsonArr = json.getJSONArray("approvedAttendance");
		 * ArrayList<Attendance> approvedAttendace = new
		 * ArrayList<Attendance>(); if(jsonArr.isEmpty()){ map.put("status",
		 * false); return map; } else {
		 * 
		 * for(int i = 0 ; i< jsonArr.length(); i++){
		 * approvedAttendace.add((Attendance) jsonArr.get(i)); } } try{
		 * portalService.UpdateAttendanceForApprovals( approvedAttendace ,
		 * selectedDate, surveyorId); map.put("status", true); } catch
		 * (Exception e) { logger.debug(
		 * "Exception occured while updating attendance approval status" + e);
		 * map.put("status", false); }
		 */
		// return map;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getShopsForDeTracking(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, Integer surveyorId,
			Integer dsrId, String selectedDate, String mapViewType,
			Integer userId, String startDate, String endDate, String spentTime) {
		portalLoggingService.insertActivityLog(request, response,
				"SHOPS_FOR_DE_TRACKING");
		return portalService.getShopsForDETracking(zoneId, regionId,
				surveyorId, dsrId, selectedDate, mapViewType, startDate,
				endDate, spentTime);
	}

	@Override
	@ResponseBody
	public List<Surveyor> getDEListByRegionIdForDeTracking(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, String mapViewType,
			String selectedDate, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"SHOPS_FOR_DE_TRACKING_BY_REGION");
		return portalService.getDeListByRegionIdForDETracking(zoneId, regionId,
				mapViewType, selectedDate);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDsrListBySurveyorIdForDeTracking(
			HttpServletRequest request, HttpServletResponse response,
			Integer surveyorId, String mapViewType, String selectedDate,
			Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"DSRs_FOR_DE_TRACKING_BY_SURVEYOR");
		return portalService.getDsrListBySurveyorIdForDeTracking(surveyorId,
				mapViewType, selectedDate);
	}

	@Override
	@ResponseBody
	public Map<String, Object> insertCityFromPortal(HttpServletRequest request,
			HttpServletResponse response, Integer regionId, String cityName,
			Integer cityId, Integer userId) {
		portalLoggingService
				.insertActivityLog(request, response, "INSERT_CITY");
		return portalService.insertCityFromPortal(regionId, cityName, cityId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateCityFromPortal(HttpServletRequest request,
			HttpServletResponse response, Integer cityId, String cityName,
			Integer userId) {
		portalLoggingService
				.insertActivityLog(request, response, "UPDATE_CITY");
		return portalService.updateCityFromPortal(cityId, cityName);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDeListByRegionId(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, String regionId, String surveyorType, String status) {
		return portalService.getDeListByRegionIdNew(zoneId, regionId,
				surveyorType, status);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getEmployeeList(
			HttpServletRequest request, HttpServletResponse response,
			String surveyorType, String mustHave) {
		return portalService.getEmployeeList(surveyorType, mustHave);
	}

	@Override
	@ResponseBody
	public Map<String, Object> insertEmployeeFromPortal(
			HttpServletRequest request, HttpServletResponse response) {

		return portalService.insertEmployeeFromPortal(request, response);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getAvailableEmployeeListForDE(
			HttpServletRequest request, HttpServletResponse response,
			String surveyorType) {
		return portalService.getAvailableEmployeeListForDE(surveyorType);
	}

	@Override
	@ResponseBody
	public Map<String, Object> insertDeFromPortal(HttpServletRequest request,
			HttpServletResponse response, String regionId, Integer employeeId,
			String deCode, String employeeType, Integer userId,
			float basicSalary, float dailyAllownce, float fuelAllownce,
			Float maintenanceAllownce, Float otherAllowance) {

		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalService.insertDeFromPortal(regionId, employeeId, deCode,
					employeeType, userId, basicSalary, dailyAllownce,
					fuelAllownce, maintenanceAllownce, otherAllowance);
			res.put("status", true);
			res.put("message", "Surveyor created successfully!");

		} catch (DuplicateKeyException dke) {
			logger.error(dke);
			res.put("status", false);
			res.put("message", deCode + " already exists in the system");
		} catch (Exception e) {

			logger.error(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;

	}

	public JSONObject extractJsonObject(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods",
				"POST, PUT, GET, OPTIONS, DELETE");
		response.setHeader(
				"Access-Control-Allow-Headers",
				"Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, x-auth-token");
		response.setHeader("Access-Control-Max-Age", "3600");
		JSONObject json = null;
		// Stop here if its Preflighted OPTIONS request
		if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {

			try {
				StringBuilder sb = new StringBuilder();
				BufferedReader reader = request.getReader();

				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append('\n');
				}

				json = new JSONObject(sb.toString());
				// logger.info(json);

			} catch (Exception e) {

				logger.error(e);
			}
		}
		return json;
	}

	@Override
	public Map<String, Object> getSecondarySale(HttpServletRequest request,
			HttpServletResponse response, Integer regionId, Integer cityId,
			Integer brandId, Integer month, Integer year, Integer userId) {
		try {

			// showParameters(request);
			// Map<String, Object> obj = new HashMap<String, Object>();
			Gson gson = new Gson();

			List<Map<String, Object>> secondarySaleList = portalService
					.getSecondarySale(regionId, cityId, brandId, month, year);
			// obj.put("saleAchievementList", saleAchievementList);
			logger.info("Secondary Sale list :: " + secondarySaleList.size());
			response.setContentType("text/json");
			response.getWriter().println(gson.toJson(secondarySaleList));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			logger.error(e, e);
		}
		return null;
	}

	@Override
	public Map<String, Object> getPrimarySale(HttpServletRequest request,
			HttpServletResponse response, Integer regionId, Integer cityId,
			Integer brandId, Integer month, Integer year, Integer userId) {
		try {

			// showParameters(request);
			// Map<String, Object> obj = new HashMap<String, Object>();
			Gson gson = new Gson();

			List<Map<String, Object>> primarySaleList = portalService
					.getPrimarySale(regionId, cityId, brandId, month, year);
			// obj.put("saleAchievementList", saleAchievementList);
			logger.info("Secondary Sale list :: " + primarySaleList.size());
			response.setContentType("text/json");
			response.getWriter().println(gson.toJson(primarySaleList));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			logger.error(e, e);
		}
		return null;
	}

	@Override
	@ResponseBody
	public Map<String, Object> updatePasswordFromPortal(
			HttpServletRequest request, HttpServletResponse response,
			String oldPassword, String newPassword, Integer userId) {

		return portalService.updatePasswordFromPortal(oldPassword, newPassword,
				userId);
	}

	@Override
	@ResponseBody
	public ArrayList<AdminProfile> getUsersDataForPasswordUpdation(
			HttpServletRequest request, HttpServletResponse response,
			Integer userTypeId) {
		return portalService.getUsersDataForPasswordUpdation(userTypeId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updatePasswordByAdmin(
			HttpServletRequest request, HttpServletResponse response,
			String newPassword, Integer userId, String userType) {
		return portalService.updatePasswordByAdmin(newPassword, userId,
				userType);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> stockTransfersTypes(
			HttpServletRequest request, HttpServletResponse response) {

		return portalService.stockTransfersTypes();
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getAdminRole(HttpServletRequest request,
			HttpServletResponse response) {

		return portalService.getAdminRole();
	}

	@Override
	@ResponseBody
	public Map<String, Object> getTableauTicket(HttpServletRequest request,
			HttpServletResponse response, String type) {
		return portalService.getTableauTicket(request, response, type);
	}

	@Override
	@ResponseBody
	public Map<String, Object> saveUserRole(HttpServletRequest request,
			HttpServletResponse response, String title) {
		return portalService.saveUserRole(title, response);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateUserRoleStatus(HttpServletRequest request,
			HttpServletResponse response, Integer roleId, String status) {
		return portalService.updateUserRoleStatus(roleId, status, response);
	}

	@Override
	@ResponseBody
	public Map<String, Object> getMenuList(HttpServletRequest request,
			HttpServletResponse response, Integer roleId) {

		return portalService.getMenuList(roleId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateMenuList(HttpServletRequest request,
			HttpServletResponse response, String menus, Integer roleId,
			Integer act, Integer userId) {
		if (act == 1) {
			portalLoggingService.insertActivityLog(request, response,
					"ASSIGN_MENU");
		} else {
			portalLoggingService.insertActivityLog(request, response,
					"UNASSIGN_MENU");
		}
		return portalService.updateMenuList(menus, roleId, act);
	}

	public void showParameters(HttpServletRequest request) {

		logger.debug("\n\n[[[=========== Requester Arrived ===========]]]");
		logger.debug("\n===========getRequestURI():  "
				+ request.getRequestURI());
		logger.debug("\n===========getRemoteAddr  :  "
				+ request.getRemoteAddr());

		StringBuilder header = new StringBuilder();
		Enumeration<?> en = request.getHeaderNames();
		while (en.hasMoreElements()) {
			String str = (String) en.nextElement();
			header.append(str).append(":").append(request.getParameter(str))
					.append("; ");
		}
		logger.debug("\n\n=========== Printing Request Header ===========");
		logger.debug(header);

		en = request.getParameterNames();
		StringBuilder params = new StringBuilder();

		while (en.hasMoreElements()) {
			String str = (String) en.nextElement();
			params.append(str).append(":").append(request.getParameter(str))
					.append("; ");

		}
		logger.debug("\n\n=========== Printing Request Parameters ===========");
		logger.debug(params);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> sisSurveyorList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer cityId, Integer userId) {
		return portalService.sisSurveyorList(startDate, endDate, zoneId,
				regionId, cityId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateStock(HttpServletRequest request,
			HttpServletResponse response, String stockType, Integer userId,
			String projectName) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_STOCK");
		return portalService.updateStock(request, response, stockType,
				projectName);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> dsrSurveyorList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer cityId, Integer userId, String zones, String regions) {
		return portalService.dsrSurveyorList(startDate, endDate, zoneId,
				regionId, cityId, zones, regions);
	}

	@Override
	@ResponseBody
	public ArrayList<ShopAssetImage> dsrShopList(HttpServletRequest request,
			HttpServletResponse response, Integer zoneId, Integer regionId,
			Integer cityId, String startDate, String endDate,
			Integer merchandiserId, Integer userId, String visitType,
			String newShop) {

		return portalService.dsrShopList(zoneId, regionId, cityId, startDate,
				endDate, merchandiserId, visitType, newShop);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateSurveyorRegion(HttpServletRequest request,
			HttpServletResponse response, String regionId, Integer surveyorId,
			Integer userId) {

		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"INSERT_SURVEYOR_REGION");
			portalService.updateSurveyorRegion(regionId, surveyorId);
			res.put("status", true);
			res.put("message", "Tm region created successfully!");

		} catch (DuplicateKeyException dke) {
			logger.info(dke);
			res.put("status", false);
			res.put("message", "Territory " + regionId
					+ " already exists in the system against TM " + surveyorId);
		} catch (Exception e) {

			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;

	}

	@Override
	@ResponseBody
	public List<Industry> getIndustryList(HttpServletRequest request,
			HttpServletResponse response, Integer userId) {
		return portalService.getIndustryList();
	}

	@Override
	@ResponseBody
	public List<Family> getBrandList(HttpServletRequest request,
			HttpServletResponse response, Integer industryId, Integer userId,
			String type) {
		if (type.equalsIgnoreCase(CommonConstants.SIS)) {
			return portalService.getSisBrandList(industryId);
		} else {
			return portalService.getBrandList(industryId);
		}
	}

	@Override
	@ResponseBody
	public Map<String, Object> createBrand(HttpServletRequest request,
			HttpServletResponse response, Integer industryId,
			String brandTitle, Integer brandSticks, Integer userId, String type) {

		Map<String, Object> res = new HashMap<String, Object>();
		try {
			if (type.equalsIgnoreCase(CommonConstants.SIS)) {
				portalLoggingService.insertActivityLog(request, response,
						"INSERT_SIS_FAMILY");
				portalService.createSisBrand(industryId, brandTitle, userId);
				cacheService.buildSisFamiliesCache();
			} else {
				portalLoggingService.insertActivityLog(request, response,
						"INSERT_FAMILY");
				portalService.createBrand(industryId, brandTitle, brandSticks);
				cacheService.buildFamiliesCache();
			}
			res.put("status", true);
			res.put("message", "Brand added successfully!");

		} catch (Exception e) {

			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;

	}

	@Override
	public void refreshCache(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			cacheService.buildCache();

			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write("Cache refresh successfully!");
			response.getWriter().flush();
		} catch (Exception e) {
			logger.error(e, e);

		}
	}

	@Override
	@ResponseBody
	public Map<String, Object> createRegion(HttpServletRequest request,
			HttpServletResponse response, Integer zoneId, String regionTitle,
			Integer userId) {

		Map<String, Object> res = new HashMap<String, Object>();
		try {
			// portalLoggingService.insertActivityLog(request, response,
			// "INSERT_FAMILY");
			portalService.createRegion(zoneId, regionTitle);
			res.put("status", true);
			res.put("message", "Territory added successfully!");
			cacheService.buildFamiliesCache();

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;

	}

	@Override
	@ResponseBody
	public Map<String, Object> updateSurveyorStatus(HttpServletRequest request,
			HttpServletResponse response, Integer surveyorId, String status,
			Integer userId, String surveyorType, Integer dsrId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"UPDATE_SURVEYOR_STATUS");
			portalService.updateSurveyorStatus(status, surveyorId,
					surveyorType, dsrId);
			res.put("status", true);
			res.put("message", "Status updated successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateEmployeeStatus(HttpServletRequest request,
			HttpServletResponse response, Integer employeeId, String status) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"UPDATE_EMPLOYEE_STATUS");
			portalService.updateEmpStatus(status, employeeId);
			res.put("status", true);
			res.put("message", "Status updated successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateStatus(HttpServletRequest request,
			HttpServletResponse response, Integer Id, String Active,
			Integer userId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"UPDATE_STATUS");
			portalService.updateStatus(Active, Id, userId);
			res.put("status", true);
			res.put("message", "Status updated successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateInActiveDate(HttpServletRequest request,
			HttpServletResponse response, Integer employeeId,
			String inactiveDate) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"UPDATE_EMPLOYEE_STATUS");
			portalService.updateDate(inactiveDate, employeeId);
			res.put("status", true);
			res.put("message", "Date Added successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateActiveDate(HttpServletRequest request,
			HttpServletResponse response, Integer Id, String Active,
			String inactiveDate, Integer userId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"UPDATE_SCHEME_INACTIVE_DATE");
			portalService.updateschemeDate(Id, Active, inactiveDate, userId);
			res.put("status", true);
			res.put("message", "Date Added successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public Map<String, Object> deleteSaleTarget(HttpServletRequest request,
			HttpServletResponse response, Integer targetId, Integer userId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"DELETE_SALE_TARGET");
			portalService.deleteSaleTarget(targetId);
			res.put("status", true);
			res.put("message", "Sale target deleted successfully!");
			cacheService.buildFamiliesCache();

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public Map<String, Object> deleteWSTKSSaleTarget(
			HttpServletRequest request, HttpServletResponse response,
			Integer targetId, Integer userId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"DELETE_WS_TKS_SALE_TARGET");
			portalService.deleteWSTKSSaleTarget(targetId);
			res.put("status", true);
			res.put("message", "Sale target deleted successfully!");
			cacheService.buildFamiliesCache();

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	public void portalTest(HttpServletRequest request,
			HttpServletResponse response) {
		CommonUtil.writeErrorMessage(HttpServletResponse.SC_OK,
				MessageType.SUCCESS, response);
	}

	// Abubakar & Ubaid
	@Override
	@ResponseBody
	public List<Map<String, Object>> getWSTKSSaleTargetforPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer month, Integer year, Integer regionId, Integer townId,
			Integer zoneId, String shopType) {
		showParameters(request);
		return portalService.getWSTKSSaleTarget(month, year, regionId, townId,
				zoneId, shopType);
	}

	@Override
	@ResponseBody
	public Map<String, Object> uploadWSTKSSaleTargetOnPortal(
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> map = new HashMap<String, Object>();
		Gson gson = new Gson();
		try {
			ServletFileUpload upload = new ServletFileUpload();
			String fileName = "";
			String fileUrl = null;
			String destFileName = null;
			String path = "";
			Map<Integer, Map<String, String>> WSTKSSaleTargetList = new HashMap<Integer, Map<String, String>>();
			try {
				FileItemIterator iterator = upload.getItemIterator(request);
				while (iterator.hasNext()) {
					FileItemStream item = iterator.next();

					if (!item.isFormField()) {
						String curruntDate = DateTimeUtilities.getDate();
						String fileDir = servletContext.getRealPath("/");
						fileDir = fileDir + "SaleTarget/" + curruntDate;
						if (!FileUtils.isExistsFolder(fileDir)) {
							FileUtils.createFolders(fileDir);
						}
						fileName = item.getName();

						fileUrl = fileDir + "/" + fileName;
						int destFileID = portalDao.getImeisExcelFileId(fileDir,
								fileName, fileUrl);
						destFileName = destFileID + ".xlsx";
						logger.info("WS/TKS Sale Target : " + item.getName());
						path = FileUtils.storeFile(fileDir + "/", fileName,
								item);
					} else {

					}

				}
				String message = "";
				String extension = FilenameUtils.getExtension(fileName);
				if (CommonConstants.XLSX_FILE.equalsIgnoreCase(extension)) {
					WSTKSSaleTargetList = fileUploadService
							.readExcelFileForWSTKSSaleTarget(path);
				}
				portalDao.insertSaleTargetDataForWSTKS(WSTKSSaleTargetList);
				map.put("message", "WS/TKS sale target data uploaded");

			} catch (Exception ex) {

				map.put("message", "DSR sale target data not uploaded");
				logger.error("Error while uploading excel file", ex);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.FILE_SAVING_SERVER, response);
			return null;
		}
		return map;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getMerchandiserWSTKSSalesList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer userId, String type,
			Integer shopId, Integer familyId, String currentDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		logger.info("current date " + formatter.format(date));
		if (formatter.format(date).toString().equalsIgnoreCase(currentDate)) {
			portalLoggingService.insertActivityLog(request, response,
					"MERCHANDISER_WS_TKS_SALE_LIST");
			return portalService.getMerchandiserWSTKSSalesList(startDate,
					endDate, zoneId, regionId, surveyorId, dsrId, type, shopId,
					familyId);
		} else {
			return null;
		}
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateWSTKSSales(HttpServletRequest request,
			HttpServletResponse response, Integer surveyId, Integer familyId,
			float wholeSaleValue, String type, Integer remarksId,
			String updateType, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_WS_TKS_SALE");
		return portalService.updateWSTKSSales(surveyId, familyId,
				wholeSaleValue, type, remarksId, updateType, userId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getMerchandiserWSTKSShopList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer userId, String type,
			Integer shopId) {
		portalLoggingService.insertActivityLog(request, response,
				"GET_SHOPS_WITH_NO_SALE");
		return portalService.getMerchandiserWSTKSShopList(startDate, endDate,
				zoneId, regionId, surveyorId, dsrId, type, shopId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> addNewWSTKSSale(HttpServletRequest request,
			HttpServletResponse response, Integer surveyorId, Integer userId,
			String type, Integer shopId, String visitDate, Integer dsrId,
			Integer employeeId, Integer familyId, float value,
			String visitDateTime, Integer remark) {
		portalLoggingService.insertActivityLog(request, response,
				"ADD_NEW_WS_TKS_SALE");
		return portalService.addNewWSTKSSale(surveyorId, userId, type, shopId,
				visitDate, dsrId, employeeId, familyId, value, visitDateTime,
				remark);
	}

	@Override
	@ResponseBody
	public List<Shop> getRAShops(HttpServletRequest request,
			HttpServletResponse response, Integer regionId) {
		logger.info("Getting shop list of RA");
		return portalService.getRAShopList(regionId);

	}

	@Override
	@ResponseBody
	public Map<String, Object> uploadRAShopsOnPortal(
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> map = new HashMap<String, Object>();
		Gson gson = new Gson();
		try {
			ServletFileUpload upload = new ServletFileUpload();
			String fileName = "";
			String fileUrl = null;
			String destFileName = null;
			String path = "";
			Map<Integer, Map<String, String>> RAShopsList = new HashMap<Integer, Map<String, String>>();
			try {
				FileItemIterator iterator = upload.getItemIterator(request);
				while (iterator.hasNext()) {
					FileItemStream item = iterator.next();

					if (!item.isFormField()) {
						String curruntDate = DateTimeUtilities.getDate();
						String fileDir = servletContext.getRealPath("/");
						fileDir = fileDir + "RAShops/" + curruntDate;
						if (!FileUtils.isExistsFolder(fileDir)) {
							FileUtils.createFolders(fileDir);
						}
						fileName = item.getName();

						fileUrl = fileDir + "/" + fileName;
						int destFileID = portalDao.getImeisExcelFileId(fileDir,
								fileName, fileUrl);
						destFileName = destFileID + ".xlsx";
						logger.info("RA Shops list: " + item.getName());
						path = FileUtils.storeFile(fileDir + "/", fileName,
								item);
					} else {

					}

				}
				String message = "";
				String extension = FilenameUtils.getExtension(fileName);
				if (CommonConstants.XLSX_FILE.equalsIgnoreCase(extension)) {
					RAShopsList = fileUploadService
							.readExcelFileForRAShops(path);
				}
				portalDao.insertRAShopsList(RAShopsList);
				map.put("message", "RA Shops list uploaded.");

			} catch (Exception ex) {

				map.put("message", "RA Shops list not uploaded.");
				logger.error("Error while uploading excel file", ex);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.FILE_SAVING_SERVER, response);
			return null;
		}
		return map;
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateSurveyorEmloyee(
			HttpServletRequest request, HttpServletResponse response,
			Integer surveyorId, Integer employeeId, Integer userId,
			float basicSalary, float dailyAllownce, float fuelAllownce,
			Float maintenanceAllownce, Float otherAllowance, Integer cityId) {
		logger.info("updating surveyor employee");
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalService.updateSurveyorEmloyee(surveyorId, employeeId, userId,
					basicSalary, dailyAllownce, fuelAllownce,
					maintenanceAllownce, otherAllowance, cityId);
			res.put("status", true);
			res.put("message", "Surveyor Employee Updated Successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;

	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getAreasByRegionId(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId) {
		logger.info("areas by region ID");
		return portalService.getAreasByRegionId(regionId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> createUserFromPortal(HttpServletRequest request,
			HttpServletResponse response, String username, String password,
			Integer role, String zone, String region, String area) {
		logger.info("Creating Web User");
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalService.createUserFromPortal(username, password, role, zone,
					region, area);
			res.put("status", true);
			res.put("message", "Web User Created Successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;

	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDsrListForAttendance(
			HttpServletRequest request, HttpServletResponse response,
			Integer surveyorId, String visitDate, Integer territoryId) {
		logger.info("Get Dsr List For Attendance");
		return portalService.getDsrListForAttendance(surveyorId, visitDate,
				territoryId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateDsrAttendance(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, Integer workTypeId,
			Integer userId, Integer surveyorId, String visitDate) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_DSR_ATTENDANCE");
		return portalService.updateDsrAttendance(dsrId, workTypeId, userId,
				surveyorId, visitDate);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getWorkingDays(HttpServletRequest request,
			HttpServletResponse response, Integer userId, Integer month,
			Integer year) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_WORKING_DAYS");
		return portalService.getWorkingDays(userId, month, year);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateWorkingDays(HttpServletRequest request,
			HttpServletResponse response, Integer userId, Integer month,
			Integer year, String value, String date) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_WORKING_DAYS");
		return portalService
				.updateWorkingDays(userId, month, year, value, date);
	}

	@Override
	@ResponseBody
	public List<Industry> getSisIndustryList(HttpServletRequest request,
			HttpServletResponse response, Integer userId) {
		return portalService.getSisIndustryList();
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getNewSisBrands(
			HttpServletRequest request, HttpServletResponse response,
			Integer industryId, Integer zoneId, Integer regionId,
			Integer cityId, Integer surveyorId, String startDate, String endDate) {
		portalLoggingService.insertActivityLog(request, response,
				"ADD-SIS-BRAND-PAGE");
		return portalService.getNewSisBrands(industryId, zoneId, regionId,
				cityId, surveyorId, startDate, endDate);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateNewBrands(HttpServletRequest request,
			HttpServletResponse response, String newBrandId,
			Integer systemBrandId, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_NEW_SIS_BRANDS");
		return portalService.updateBrands(newBrandId, systemBrandId, userId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getShopProfile(HttpServletRequest request,
			HttpServletResponse response) {
		return portalService.getShopProfile();
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getSisSurveyor(HttpServletRequest request,
			HttpServletResponse response, Integer regionId, Integer typeId) {
		return portalService.getSisSurveyor(regionId, typeId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateTownStatus(HttpServletRequest request,
			HttpServletResponse response, Integer cityId, String status,
			String userId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"UPDATE_TOWN_STATUS");
			portalService.updateTownStatus(status, cityId, userId);
			res.put("status", true);
			res.put("message", "City status updated successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getTownsForPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId) {
		// applicationCacheService.getCitiesByRegionId(regionId);
		List<Map<String, Object>> data = new ArrayList<>();
		data = portalDao.getCitiesForPortal(regionId);
		logger.info("towns inside region " + regionId + " are " + data);
		logger.debug("towns inside region " + regionId + " are " + data);

		return data;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getSisBrands(HttpServletRequest request,
			HttpServletResponse response, Integer industryId) {
		return portalDao.getSisBrands(industryId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateSisOldBrands(HttpServletRequest request,
			HttpServletResponse response, Integer industryId, Integer familyId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"UPDATE_BRAND_INDUSTRY");
			portalService.updateSisOldBrands(industryId, familyId);
			res.put("status", true);
			res.put("message", "Industry of brand updated successfully!");
			cacheService.buildFamiliesCache();

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getSisAttendanceList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer surveyorId, Integer typeId) {

		return portalService.getSisAttendanceList(startDate, endDate,
				surveyorId, typeId);

	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getAreasByTownId(
			HttpServletRequest request, HttpServletResponse response,
			Integer townId) {
		return portalService.getAreasByTownId(townId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> insertAreaFromPortal(HttpServletRequest request,
			HttpServletResponse response, Integer townId, String areaName,
			Integer userId) {
		portalLoggingService
				.insertActivityLog(request, response, "INSERT_AREA");
		return portalService.insertAreaFromPortal(townId, areaName, userId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getShopsForSisTracking(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, Integer cityId,
			Integer surveyorId, String selectedDate, String mapViewType,
			Integer userId, String startDate, String endDate, String shopType) {
		portalLoggingService.insertActivityLog(request, response,
				"SHOPS_FOR_SIS_TRACKING");
		return portalService.getShopsForSisTracking(zoneId, regionId, cityId,
				surveyorId, selectedDate, mapViewType, startDate, endDate,
				shopType);
	}

	@Override
	@ResponseBody
	public List<Surveyor> getSisListByRegionIdForSurveyorTracking(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, String mapViewType,
			String selectedDate, Integer userId, String surveyorType) {
		portalLoggingService.insertActivityLog(request, response,
				"SHOPS_FOR_SURVEYOR_TRACKING_BY_REGION");
		return portalService.getSisListByRegionIdForSurveyorTracking(zoneId,
				regionId, mapViewType, selectedDate, surveyorType);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getSchemes(HttpServletRequest request,
			HttpServletResponse response, Integer cityId, Integer shopId) {
		portalLoggingService.insertActivityLog(request, response,
				"FETCHING_SCHEMES");
		return portalService.getSchemes(cityId, shopId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getOverallSchemes(
			HttpServletRequest request, HttpServletResponse response,
			String promoType, Integer brandId, String status, String type) {
		return portalService
				.getOverallSchemes(promoType, brandId, status, type);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getFocusBrands(HttpServletRequest request,
			HttpServletResponse response, Integer status, Integer brandId) {
		return portalService.getFocusBrands(status, brandId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateSchemeTagging(HttpServletRequest request,
			HttpServletResponse response, String schemeId, Integer townId,
			Integer shopId, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"SCHEME_TAGGING");
		return portalService.updateSchemeTagging(schemeId, townId, shopId,
				userId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> insertNewSchemeFromPortal(
			HttpServletRequest request, HttpServletResponse response,
			String schemeTitle, Integer familyId, String promotionType,
			String schemeType, Integer minimumQuantity, float discountQunatity,
			float discountPercent, float discountValue, float stockAssignment,
			String startDate, String endDate, boolean isCreate, Integer userId,
			String type, float productiveShop, float minimumSale,
			String shopProfile, float schemeDuration,
			float productiveBrandFrom, float productiveBrandTo,
			float maximumBudget, final String calculatioType,
			final Integer upperLimit) {
		portalLoggingService.insertActivityLog(request, response,
				"INSERT_NEW_SCHEME");
		return portalService.insertNewSchemeFromPortal(schemeTitle, familyId,
				promotionType, schemeType, minimumQuantity, discountQunatity,
				discountPercent, discountValue, stockAssignment, startDate,
				endDate, isCreate, userId, type, productiveShop, minimumSale,
				shopProfile, schemeDuration, productiveBrandFrom,
				productiveBrandTo, maximumBudget, calculatioType, upperLimit);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateTownSchemeStatus(
			HttpServletRequest request, HttpServletResponse response,
			Integer schemeId, String status, String userId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"UPDATE_TOWN_SCHEME_STATUS");
			portalService.updateTownSchemeStatus(status, schemeId);
			res.put("status", true);
			res.put("message", "Scheme status updated successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public Map<String, Object> uploadDsrRoutes(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> map = new HashMap<String, Object>();
		Gson gson = new Gson();
		try {
			ServletFileUpload upload = new ServletFileUpload();
			String fileName = "";
			String fileUrl = null;
			String destFileName = null;
			String path = "";
			Map<Integer, Map<String, String>> dsrRoute = new HashMap<Integer, Map<String, String>>();
			try {
				FileItemIterator iterator = upload.getItemIterator(request);
				while (iterator.hasNext()) {
					FileItemStream item = iterator.next();

					if (!item.isFormField()) {
						String curruntDate = DateTimeUtilities.getDate();
						String fileDir = servletContext.getRealPath("/");
						fileDir = fileDir + "DsrRoutes/" + curruntDate;
						if (!FileUtils.isExistsFolder(fileDir)) {
							FileUtils.createFolders(fileDir);
						}
						fileName = item.getName();

						fileUrl = fileDir + "/" + fileName;
						int destFileID = portalDao.getImeisExcelFileId(fileDir,
								fileName, fileUrl);
						destFileName = destFileID + ".xlsx";
						logger.info("DsrRoutes : " + item.getName());
						path = FileUtils.storeFile(fileDir + "/", fileName,
								item);
					} else {

					}

				}
				String message = "";
				String extension = FilenameUtils.getExtension(fileName);
				if (CommonConstants.XLSX_FILE.equalsIgnoreCase(extension)) {
					dsrRoute = fileUploadService
							.readExcelFileForDSRRoutes(path);
				}
				// inserting routes into db
				// portalDao.insertSaleTargetDataForWSTKS(dsrRoute);
				map.put("message", "DSR Routes uploaded");

			} catch (Exception ex) {

				map.put("message", "Error while uploading DSR Routes");
				logger.error("Error while uploading excel file", ex);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.FILE_SAVING_SERVER, response);
			return null;
		}
		return map;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getTreeList(HttpServletRequest request,
			HttpServletResponse response, String type, Integer familyId,
			Integer promoId) {
		// TODO Auto-generated method stub
		return portalDao.getTreeList(type, familyId, promoId);
	}

	@Override
	@ResponseBody
	public List<String> getPromoAssignedList(HttpServletRequest request,
			HttpServletResponse response, Integer promoId) {

		return portalService.getPromoAssignedList(promoId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> assignPromo(HttpServletRequest request,
			HttpServletResponse response, Integer promoId, String citiesList,
			Integer userId, float schemeStock, int familyId, String type) {
		portalLoggingService.insertActivityLog(request, response,
				"ASSIGN_PROMO");
		return portalService.assignPromo(promoId, citiesList, schemeStock,
				familyId, type);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getEditReceivedStock(
			HttpServletRequest request, HttpServletResponse response,
			String receivedDate, Integer userId, String type) {
		return portalService.getEditReceivedStock(request, receivedDate,
				userId, type);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getAdjustmentStock(
			HttpServletRequest request, HttpServletResponse response,
			Integer transferId, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"GET_ADJUSTMENT_STOCK");
		return portalService.getAdjustmentStock(transferId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> submitAdjustedStock(HttpServletRequest request,
			HttpServletResponse response) {

		// TODO Auto-generated method stub
		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			// ArrayList<BwuImage> bwuImages = new ArrayList<BwuImage>();
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			StockTransfer stockTransfer = null;
			if (isMultiPart) {
				ServletFileUpload upload = new ServletFileUpload();
				try {

					FileItemIterator itr = upload.getItemIterator(request);
					while (itr.hasNext()) {
						FileItemStream item = itr.next();
						if (item.isFormField()) {
							InputStream stream = item.openStream();
							String reader = Streams.asString(stream);
							stockTransfer = gson.fromJson(reader,
									StockTransfer.class);
						}
					}
				} catch (Exception ex) {
					logger.error("Error while saving Stock Adjusted Data  : ",
							ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					logger.info("Error :", ex);
					return null;
				}

				if (stockTransfer != null) {

					try {

						portalService.submitAdjustedStock(stockTransfer);
						responMap.put("status", true);
						responMap.put("message",
								"Stock Adjusted Successfully..!!");
					} catch (IllegalStateException ex) {
						ex.printStackTrace();
						logger.error("Error", ex);
						responMap.put("status", false);
						responMap.put("message",
								"Stock Family data not provided.");
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("Error", e);
						responMap.put("status", false);
						responMap.put("message",
								"Stock NOT Adjusted Successfully..!!");
					}

					/*
					 * logger.info("Sending Shop id in response " +
					 * " shop id : " +syncData.getVisit().getShopId());
					 */
					// responMap.put("syncResponse",CommonUtil.getResponseObj(syncData.getVisit().getShopId(),
					// -1));
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
			e.printStackTrace();
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.FILE_SAVING_SERVER, response);
			return null;
		}

		return responMap;
	}

	@Override
	@ResponseBody
	public Map<String, Object> getRouteTagging(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, Integer type) {
		return portalService.getRouteTagging(dsrId, type);
	}

	@Override
	@ResponseBody
	public Map<String, Object> assignDsrRoutes(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, String routeList,
			Integer type, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"ASSIGN_DSR_ROUTES");
		return portalService.assignDsrRoutes(dsrId, routeList, type);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getStockSchemeCity(
			HttpServletRequest request, HttpServletResponse response,
			Integer promoId, Float schemeStock, Float consumedStock) {
		return portalService.getStockSchemeCity(promoId, schemeStock,
				consumedStock);
	}

	@Override
	@ResponseBody
	public Map<String, Object> assignStockCityScheme(
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			CityStockAssignment city = null;
			if (isMultiPart) {
				ServletFileUpload upload = new ServletFileUpload();
				try {

					FileItemIterator itr = upload.getItemIterator(request);
					while (itr.hasNext()) {
						FileItemStream item = itr.next();
						if (item.isFormField()) {
							InputStream stream = item.openStream();
							String reader = Streams.asString(stream);
							city = gson.fromJson(reader,
									CityStockAssignment.class);
						}
					}
				} catch (Exception ex) {
					logger.error("Error while assigning stock city wise  : ",
							ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					logger.info("Error :", ex);
					return null;
				}

				if (city != null) {
					try {
						portalService.assignStockCityScheme(city);
						responMap.put("status", true);
						responMap.put("description", "Stock Assigned");
					} catch (IllegalStateException ex) {
						logger.error("Error", ex);
						responMap.put("status", false);
						responMap
								.put("description", "Something Went Wrong....");
					} catch (Exception e) {
						logger.error("Error", e);
						responMap.put("status", false);
						responMap.put("description",
								"Something Went Wrong....!!");
					}

					/*
					 * logger.info("Sending Shop id in response " +
					 * " shop id : " +syncData.getVisit().getShopId());
					 */
					// responMap.put("syncResponse",CommonUtil.getResponseObj(syncData.getVisit().getShopId(),
					// -1));
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
			e.printStackTrace();
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.FILE_SAVING_SERVER, response);
			return null;
		}

		return responMap;

		/*
		 * JSONObject json = extractJsonObject(request, respons); JSONArray
		 * jsonArr = json.getJSONArray("approvedAttendance");
		 * ArrayList<Attendance> approvedAttendace = new
		 * ArrayList<Attendance>(); if(jsonArr.isEmpty()){ map.put("status",
		 * false); return map; } else {
		 * 
		 * for(int i = 0 ; i< jsonArr.length(); i++){
		 * approvedAttendace.add((Attendance) jsonArr.get(i)); } } try{
		 * portalService.UpdateAttendanceForApprovals( approvedAttendace ,
		 * selectedDate, surveyorId); map.put("status", true); } catch
		 * (Exception e) { logger.debug(
		 * "Exception occured while updating attendance approval status" + e);
		 * map.put("status", false); }
		 */
		// return map;
	}

	@Override
	@ResponseBody
	public List<String> getBrandAssignedList(HttpServletRequest request,
			HttpServletResponse response, Integer familyId) {

		return portalService.getBrandAssignedList(familyId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> assignBrandList(HttpServletRequest request,
			HttpServletResponse response) {
		// portalLoggingService.insertActivityLog(request, response,
		// "ASSIGN_BRANDS");

		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			BrandAssignment brand = null;
			if (isMultiPart) {
				ServletFileUpload upload = new ServletFileUpload();
				try {

					FileItemIterator itr = upload.getItemIterator(request);
					while (itr.hasNext()) {
						FileItemStream item = itr.next();
						if (item.isFormField()) {
							InputStream stream = item.openStream();
							String reader = Streams.asString(stream);
							brand = gson
									.fromJson(reader, BrandAssignment.class);
						}
					}
				} catch (Exception ex) {
					logger.error("Error while assigning brand list  : ", ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					logger.info("Error :", ex);
					return null;
				}

				if (brand != null) {
					try {
						portalService.assignBrandList(brand);
						responMap.put("status", true);
						responMap.put("description", "Brand Assigned");
					} catch (IllegalStateException ex) {
						logger.error("Error", ex);
						responMap.put("status", false);
						responMap
								.put("description", "Something Went Wrong....");
					} catch (Exception e) {
						logger.error("Error", e);
						responMap.put("status", false);
						responMap.put("description",
								"Something Went Wrong....!!");
					}

					/*
					 * logger.info("Sending Shop id in response " +
					 * " shop id : " +syncData.getVisit().getShopId());
					 */
					// responMap.put("syncResponse",CommonUtil.getResponseObj(syncData.getVisit().getShopId(),
					// -1));
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
			e.printStackTrace();
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.FILE_SAVING_SERVER, response);
			return null;
		}

		return responMap;

		/*
		 * JSONObject json = extractJsonObject(request, respons); JSONArray
		 * jsonArr = json.getJSONArray("approvedAttendance");
		 * ArrayList<Attendance> approvedAttendace = new
		 * ArrayList<Attendance>(); if(jsonArr.isEmpty()){ map.put("status",
		 * false); return map; } else {
		 * 
		 * for(int i = 0 ; i< jsonArr.length(); i++){
		 * approvedAttendace.add((Attendance) jsonArr.get(i)); } } try{
		 * portalService.UpdateAttendanceForApprovals( approvedAttendace ,
		 * selectedDate, surveyorId); map.put("status", true); } catch
		 * (Exception e) { logger.debug(
		 * "Exception occured while updating attendance approval status" + e);
		 * map.put("status", false); }
		 */
		// return map;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getBrandList(HttpServletRequest request,
			HttpServletResponse response) {
		return portalService.getBrandList();
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getAllBrandList(
			HttpServletRequest request, HttpServletResponse response,
			Integer industryId, String type) {
		return portalService.getAllBrandList(industryId, type);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getFamilySetList(
			HttpServletRequest request, HttpServletResponse response) {
		return portalService.getFamilySetList();
	}

	@Override
	@ResponseBody
	public Map<String, Object> insertDsrEmployee(HttpServletRequest request,
			HttpServletResponse response, String dsrName) {
		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			portalService.insertDsrIntoEmployee(dsrName);
			responMap.put("status", true);
			responMap.put("description", "Dsr Employee Added");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responMap.put("status", false);
			responMap.put("description", "Something Went Wrong....!!");
		}

		return responMap;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDsrEmployeeList(
			HttpServletRequest request, HttpServletResponse response) {
		return portalService.getDsrEmployeeList();
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateDsrEmloyee(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, Integer employeeId,
			Integer userId, float basicSalary, float dailyAllownce,
			float fuelAllownce, float maintainanceAllownce,
			float otherAllowance, Integer cityId, String registrationNo,
			String makeModel, String vanType, String dsrType) {
		logger.info("updating dsr employee");
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalService.updateDsrEmloyee(dsrId, employeeId, userId,
					basicSalary, dailyAllownce, fuelAllownce,
					maintainanceAllownce, otherAllowance, cityId,
					registrationNo, makeModel, vanType, dsrType);
			res.put("status", true);
			res.put("message", "Dsr Employee Updated Successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public Map<String, Object> insertDsrFromPortal(HttpServletRequest request,
			HttpServletResponse response, Integer employeeId, String deCode,
			String employeeType, Integer selectedDE, Integer userId,
			Integer rateList, float basicSalary, float dailyAllownce,
			float fuelAllownce, Float maintenanceAllownce,
			Float otherAllowance, Float vanSalary, String registrationNo,
			String makeModel, String vanType) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalService.insertDsrFromPortal(employeeId, deCode, employeeType,
					selectedDE, userId, rateList, basicSalary, dailyAllownce,
					fuelAllownce, maintenanceAllownce, otherAllowance,
					vanSalary, registrationNo, makeModel, vanType);
			res.put("status", true);
			res.put("message", "Dsr created successfully!");

		} catch (DuplicateKeyException dke) {
			logger.error(dke);
			res.put("status", false);
			res.put("message", deCode + " already exists in the system");
		} catch (Exception e) {

			logger.error(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;

	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getCities(HttpServletRequest request,
			HttpServletResponse response, Integer regionId) {
		return portalService.getCities(regionId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getCitiesNew(HttpServletRequest request,
			HttpServletResponse response, String regionId) {
		return portalService.getCitiesNew(regionId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDistinctSchemes(
			HttpServletRequest request, HttpServletResponse response) {
		return portalService.getDistinctSchemes();
	}

	@Override
	@ResponseBody
	public Map<String, Object> deleteDsrDayEnd(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, Integer userId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"DELETE_DSR_DAY_END");
			portalService.deleteDsrDayEnd(dsrId);
			res.put("status", true);
			res.put("message", "Day End Deleted Successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateEmployeeName(HttpServletRequest request,
			HttpServletResponse response, String employeeName, long phone,
			long cnic, Integer basicSalary, Integer dailyAllownce,
			Integer fuelAllownce, Integer maintenanceAllownce,
			Integer employeeId, Integer userId) {
		return portalService.updateEmployeeName(employeeName, phone, cnic,
				basicSalary, dailyAllownce, fuelAllownce, maintenanceAllownce,
				employeeId, userId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getBrandPriceDetails(
			HttpServletRequest request, HttpServletResponse response,
			Integer brandId, Integer brandPriceId) {
		List<Map<String, Object>> brandPriceList = null;
		try {
			// portalLoggingService.insertActivityLog(request, response,
			// "GET_BRAND_PRICES");
			brandPriceList = portalService.getBrandPriceDetails(brandId,
					brandPriceId);
		} catch (Exception e) {
			logger.info(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
		}
		return brandPriceList;
	}

	@Override
	@ResponseBody
	public Map<String, Object> addBrandPrice(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			BrandPrice brandPrice = null;
			if (isMultiPart) {
				ServletFileUpload upload = new ServletFileUpload();
				try {

					FileItemIterator itr = upload.getItemIterator(request);
					while (itr.hasNext()) {
						FileItemStream item = itr.next();
						if (item.isFormField()) {
							InputStream stream = item.openStream();
							String reader = Streams.asString(stream);
							brandPrice = gson
									.fromJson(reader, BrandPrice.class);
						}
					}
				} catch (Exception ex) {
					logger.error("Error while adding brand Price  : ", ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					logger.info("Error :", ex);
					return null;
				}

				if (brandPrice != null) {
					try {
						// portalLoggingService.insertActivityLog(request,
						// response, "INSERT_BRAND_PRICE");
						int brandPriceId = portalService
								.insertBrandPrice(brandPrice);
						responMap.put("status", true);
						responMap.put("data", brandPriceId);
						responMap.put("description", "Brand Price added");
					} catch (IllegalStateException ex) {
						logger.error("Error", ex);
						responMap.put("status", false);
						responMap
								.put("description", "Something Went Wrong....");
					} catch (Exception e) {
						logger.error("Error", e);
						responMap.put("status", false);
						responMap.put("description",
								"Something Went Wrong....!!");
					}

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

		return responMap;
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateBrandPrice(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			BrandPriceDetails brandPriceDetails = null;
			if (isMultiPart) {
				ServletFileUpload upload = new ServletFileUpload();
				try {

					FileItemIterator itr = upload.getItemIterator(request);
					while (itr.hasNext()) {
						FileItemStream item = itr.next();
						if (item.isFormField()) {
							InputStream stream = item.openStream();
							String reader = Streams.asString(stream);
							brandPriceDetails = gson.fromJson(reader,
									BrandPriceDetails.class);
						}
					}
				} catch (Exception ex) {
					logger.error("Error while adding brand Price  : ", ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					logger.info("Error :", ex);
					return null;
				}

				if (brandPriceDetails != null) {
					try {
						// portalLoggingService.insertActivityLog(request,
						// response, "INSERT_BRAND_PRICE");
						portalService
								.updateBrandPriceDetails(brandPriceDetails);
						responMap.put("status", true);
						responMap.put("description", "Brand Price added");
					} catch (IllegalStateException ex) {
						logger.error("Error", ex);
						responMap.put("status", false);
						responMap
								.put("description", "Something Went Wrong....");
					} catch (Exception e) {
						logger.error("Error", e);
						responMap.put("status", false);
						responMap.put("description",
								"Something Went Wrong....!!");
					}

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

		return responMap;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getBrandPrices(HttpServletRequest request,
			HttpServletResponse response) {
		List<Map<String, Object>> brandPriceList = null;
		try {
			/*
			 * portalLoggingService.insertActivityLog(request, response,
			 * "GET_BRAND_PRICES");
			 */
			brandPriceList = portalService.getBrandPrices();
		} catch (Exception e) {
			logger.info(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
		}
		return brandPriceList;
	}

	@Override
	@ResponseBody
	public Map<String, Object> addBrandPriceDetail(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			BrandPriceDetails brandPriceDetail = null;
			if (isMultiPart) {
				ServletFileUpload upload = new ServletFileUpload();
				try {

					FileItemIterator itr = upload.getItemIterator(request);
					while (itr.hasNext()) {
						FileItemStream item = itr.next();
						if (item.isFormField()) {
							InputStream stream = item.openStream();
							String reader = Streams.asString(stream);
							brandPriceDetail = gson.fromJson(reader,
									BrandPriceDetails.class);
						}
					}
				} catch (Exception ex) {
					logger.error("Error while adding brand Price detail  : ",
							ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					logger.info("Error :", ex);
					return null;
				}

				if (brandPriceDetail != null) {
					try {
						// portalLoggingService.insertActivityLog(request,
						// response, "INSERT_BRAND_PRICE");
						int brandPriceDetailId = portalService
								.insertBrandPriceDetail(brandPriceDetail);
						responMap.put("status", true);
						responMap.put("data", brandPriceDetailId);
						responMap.put("description",
								"Brand Price detail updated");
					} catch (IllegalStateException ex) {
						logger.error("Error", ex);
						responMap.put("status", false);
						responMap
								.put("description", "Something Went Wrong....");
					} catch (Exception e) {
						logger.error("Error", e);
						responMap.put("status", false);
						responMap.put("description",
								"Something Went Wrong....!!");
					}

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

		return responMap;
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateDsrShopSales(HttpServletRequest request,
			HttpServletResponse response, Integer dsrSaleId, float saleValue,
			Integer userId, String promoType, float discountValue) {
		portalLoggingService.insertActivityLog(request, response,
				"UPDATE_DSR_Shop_SALE");
		return portalService.updateDsrShopSales(dsrSaleId, saleValue, userId,
				promoType, discountValue);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getMerchandiserDsrShopSalesList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer userId,
			Integer familyId, String currentDate, String promotionType,
			String schemeTitle) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		logger.info("current date " + formatter.format(date));
		if (formatter.format(date).toString().equalsIgnoreCase(currentDate)) {
			portalLoggingService.insertActivityLog(request, response,
					"MERCHANDISER_DSR_SHOP_SALE_LIST");
			return portalService.getMerchandiserDsrShopSalesList(startDate,
					endDate, zoneId, regionId, surveyorId, dsrId, familyId,
					promotionType, schemeTitle);
		} else {
			return null;
		}
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getAssignedBrandList(
			HttpServletRequest request, HttpServletResponse response,
			String type, Integer brandPriceId) {
		// TODO Auto-generated method stub
		return portalDao.getAssignedBrandList(type, brandPriceId);
	}

	@Override
	@ResponseBody
	public List<String> getBrandRateAssignedList(HttpServletRequest request,
			HttpServletResponse response, Integer brandPriceId) {

		return portalService.getBrandRateAssignedList(brandPriceId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> assignRateList(HttpServletRequest request,
			HttpServletResponse response) {
		// portalLoggingService.insertActivityLog(request, response,
		// "ASSIGN_BRANDS");

		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			RateAssignment rate = null;
			if (isMultiPart) {
				ServletFileUpload upload = new ServletFileUpload();
				try {

					FileItemIterator itr = upload.getItemIterator(request);
					while (itr.hasNext()) {
						FileItemStream item = itr.next();
						if (item.isFormField()) {
							InputStream stream = item.openStream();
							String reader = Streams.asString(stream);
							rate = gson.fromJson(reader, RateAssignment.class);
						}
					}
				} catch (Exception ex) {
					logger.error("Error while assigning rate list  : ", ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					logger.info("Error :", ex);
					return null;
				}

				if (rate != null) {
					try {
						portalService.assignRateList(rate);
						responMap.put("status", true);
						responMap.put("description", "Brand Assigned");
					} catch (IllegalStateException ex) {
						logger.error("Error", ex);
						responMap.put("status", false);
						responMap
								.put("description", "Something Went Wrong....");
					} catch (Exception e) {
						logger.error("Error", e);
						responMap.put("status", false);
						responMap.put("description",
								"Something Went Wrong....!!");
					}

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

		return responMap;

	}

	@Override
	@ResponseBody
	public void deleteRateList(HttpServletRequest request,
			HttpServletResponse response, Integer brandPriceId, Integer userId) {
		try {
			// portalLoggingService.insertActivityLog(request, response,
			// "delete_rate_list");
			portalService.deleteRateList(brandPriceId, userId);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_OK,
					MessageType.SUCCESS, response);
		} catch (Exception e) {
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_FORMAT_DATA, response);
		}
	}

	@Override
	@ResponseBody
	public void importShopsFromBlock(HttpServletRequest request,
			HttpServletResponse response, Integer exportAreaId,
			Integer importAreaId) {
		try {
			portalLoggingService.insertActivityLog(request, response,
					"IMPORT_SHOPS_FROM_BLOCK");
			portalService.importShopsFromBlock(exportAreaId, importAreaId);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_OK,
					MessageType.SUCCESS, response);
		} catch (Exception e) {
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_FORMAT_DATA, response);
		}
	}

	@Override
	@ResponseBody
	public Map<String, Object> blockTransfer(HttpServletRequest request,
			HttpServletResponse response, String blockName, Integer blockId,
			Integer townId, Integer dsrId, Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"TRANSFER_NEW_BLOCK");
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			portalService.blockTransfer(blockName, blockId, townId, dsrId,
					userId);
			map.put("status", true);
			map.put("message", "Block added into system.");
			return map;

		} catch (Exception ex) {

			logger.error(ex);
			map.put("status", false);
			map.put("message", "Block not added into system.");
			return map;
		}
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateBrandStatus(HttpServletRequest request,
			HttpServletResponse response, Integer brandId, String status) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"UPDATE_BRAND_STATUS");
			portalService.updateBrandStatus(status, brandId);
			res.put("status", true);
			res.put("message", "Status updated successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getTerritoriesRadius(
			HttpServletRequest request, HttpServletResponse response) {
		return portalService.getTerritoriesRadius();
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateTerritoryRadius(
			HttpServletRequest request, HttpServletResponse response,
			Integer id, Integer radius, Integer userId) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"UPDATE_Radius");
			portalService.updateTerritoryRadius(id, radius, userId);
			res.put("status", true);
			res.put("message", "Radius updated successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getFinanceSetup(
			HttpServletRequest request, HttpServletResponse response,
			String type, String territoryId, String regionId) {
		List<Map<String, Object>> financeData = null;
		try {
			financeData = portalService.getFinanceSetup(type, territoryId,
					regionId);
		} catch (Exception e) {
			logger.info(e);
		}
		return financeData;
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateFinanceDetails(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> responMap = new HashMap<String, Object>();
		try {
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			FinanceSetup financeSetup = null;
			if (isMultiPart) {
				ServletFileUpload upload = new ServletFileUpload();
				try {

					FileItemIterator itr = upload.getItemIterator(request);
					while (itr.hasNext()) {
						FileItemStream item = itr.next();
						if (item.isFormField()) {
							InputStream stream = item.openStream();
							String reader = Streams.asString(stream);
							financeSetup = gson.fromJson(reader,
									FinanceSetup.class);
						}
					}
				} catch (Exception ex) {
					logger.error("Error while addind finance Details  : ", ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					logger.info("Error :", ex);
					return null;
				}

				if (financeSetup != null) {
					try {
						String type = request.getParameter("type");
						// portalLoggingService.insertActivityLog(request,
						// response, "UPDATE_FINANCE_DETAILS");
						int id = portalService.updateFinanceDetails(
								financeSetup, type);
						responMap.put("status", true);
						responMap.put("data", id);
						responMap.put("description",
								"Data is updated successfully");
					} catch (IllegalStateException ex) {
						logger.error("Error", ex);
						responMap.put("status", false);
						responMap
								.put("description", "Something Went Wrong....");
					} catch (Exception e) {
						logger.error("Error", e);
						responMap.put("status", false);
						responMap.put("description",
								"Something Went Wrong....!!");
					}

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

		return responMap;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getMonths(HttpServletRequest request,
			HttpServletResponse response, String month, Integer id) {
		List<Map<String, Object>> monthList = null;
		try {
			/*
			 * portalLoggingService.insertActivityLog(request, response,
			 * "GET_BRAND_PRICES");
			 */
			monthList = portalService.getMonths(month, id);
		} catch (Exception e) {
			logger.info(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
		}
		return monthList;

	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> updateprice(HttpServletRequest request,
			HttpServletResponse response, Double f1, Double f2, int id,
			String date) throws Exception {
		return portalService.updateprice(f1, f2, id, date);
	}

	@Override
	@ResponseBody
	public Map<String, Object> insertFocusBrand(HttpServletRequest request,
			HttpServletResponse response, final Integer regionId,
			final Integer visits, final Integer sale, final Integer familyId,
			final String startDate, final String endDate, final Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"INSERT_FOCUS_BRAND");
		return portalService.insertFocusBrand(regionId, visits, sale, familyId,
				startDate, endDate, userId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> updateFocusBrandStatus(
			HttpServletRequest request, HttpServletResponse response,
			Integer id, String status) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"UPDATE_FOCUS_BRAND_STATUS");
			portalService.updateFocusBrandStatus(status, id);
			res.put("status", true);
			res.put("message", "Status updated successfully!");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getApplicationUsers(
			HttpServletRequest request, HttpServletResponse response) {
		return portalService.getApplicationUsers();
	}

	@ResponseBody
	public Map<String, Object> insertAcitivity(HttpServletRequest request,
			HttpServletResponse response, final String activityTitle,
			final String activityType, final Integer familyId,
			final String startDate, final String endDate, final Integer userId) {
		portalLoggingService.insertActivityLog(request, response,
				"INSERT_DE_ACTIVITY");
		return portalService.insertAcitivity(activityTitle, activityType,
				familyId, startDate, endDate, userId);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getActivityData(
			HttpServletRequest request, HttpServletResponse response,
			Integer status, Integer brandId) {
		return portalService.getActivityData(status, brandId);
	}

	@ResponseBody
	public Map<String, Object> insertAcitivityRoutes(
			HttpServletRequest request, HttpServletResponse response,
			final Integer deId, final Integer activityId) {
		portalLoggingService.insertActivityLog(request, response,
				"INSERT_DE_ACTIVITY");
		return portalService.insertAcitivityRoutes(deId, activityId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> uploadPrimarySaleTargetOnPortal(
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<String> validatorArray = new ArrayList<String>();

		Gson gson = new Gson();
		try {
			ServletFileUpload upload = new ServletFileUpload();
			String fileName = "";
			String fileUrl = null;
			String destFileName = null;
			String path = "";
			Map<Integer, Map<String, String>> DsrSaleTargetList = new HashMap<Integer, Map<String, String>>();
			try {
				FileItemIterator iterator = upload.getItemIterator(request);
				while (iterator.hasNext()) {
					FileItemStream item = iterator.next();
					if (!item.isFormField()) {
						String curruntDate = DateTimeUtilities.getDate();
						String fileDir = servletContext.getRealPath("/");
						fileDir = fileDir + "PrimarySaleTarget/" + curruntDate;
						if (!FileUtils.isExistsFolder(fileDir)) {
							FileUtils.createFolders(fileDir);
						}
						fileName = item.getName();

						fileUrl = fileDir + "/" + fileName;
						int destFileID = portalDao.getImeisExcelFileId(fileDir,
								fileName, fileUrl);
						destFileName = destFileID + ".xlsx";
						logger.info("Primary Sale Target : " + item.getName());
						path = FileUtils.storeFile(fileDir + "/", fileName,
								item);
					} else {

					}

				}
				String message = "";
				String extension = FilenameUtils.getExtension(fileName);
				logger.info("sasa" + DsrSaleTargetList);
				if (CommonConstants.XLSX_FILE.equalsIgnoreCase(extension)) {
					DsrSaleTargetList = fileUploadService
							.readExcelFileForPrimarySaleTarget(path,
									validatorArray);
				}
				logger.info("sasa" + DsrSaleTargetList);
				portalDao.insertPrimaryTargetData(DsrSaleTargetList);
				map.put("success", true);
				map.put("message", "Primary sale target data uploaded");

			} catch (InvalidHeadingException ex) {
				logger.error(ex, ex);

				map.put("success", false);
				map.put("message", ex);
			} catch (CustomException ex) {
				logger.error(ex, ex);

				map.put("success", false);
				map.put("message", ex);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.FILE_SAVING_SERVER, response);
			return null;
		}
		return map;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getCreditNote(HttpServletRequest request,
			HttpServletResponse response, String zoneId, String regionId,
			Integer month, Integer year) {
		return portalService.getCreditNote(zoneId, regionId, month, year);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getFpiShops(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		return familiesDao.getFpiShops();
	}

	@Override
	@ResponseBody
	public Map<String, Object> getForgotLoginPassword(
			HttpServletRequest request, HttpServletResponse response,
			String userName, String uniqueKey, String type) {
		Map<String, Object> res = new HashMap<String, Object>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"Forgot_Password_Email");
			portalService.getForgotPassword(userName, uniqueKey, type);
			res.put("status", true);
			res.put("message",
					"Please check your email to update your password.");

		} catch (Exception e) {
			logger.info(e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public Map<String, Object> updatePasswordNew(HttpServletRequest request,
			HttpServletResponse response, String newPassword, String uniqueKey,
			String type) {

		return portalService.updatePasswordFromEmail(newPassword, uniqueKey,
				type);
	}

	@Override
	@ResponseBody
	public Map<String, Object> expiryTime(HttpServletRequest request,
			HttpServletResponse response, String currentTime, String uniqueKey,
			String type) {
		Map<String, Object> res = new HashMap<>();
		try {
			portalLoggingService.insertActivityLog(request, response,
					"EXPIRY_TIME_CHECK");

			// Get the time difference in minutes and check if it's exactly 10
			// minutes
			boolean isTenMinutesDifference = portalService.getexpiryTime(
					currentTime, uniqueKey, type);

			if (isTenMinutesDifference) {
				res.put("status", true);
				res.put("message", "Enter New Password");
			} else {
				res.put("status", false);
				res.put("message", "Link Is Expired.");
			}

		} catch (Exception e) {
			logger.error("Error occurred during expiryTime processing", e);
			res.put("status", false);
			res.put("message", "Something went wrong..");
		}
		return res;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getFinanceSheeetData(
			HttpServletRequest request, HttpServletResponse response,
			String type, String territoryId, String zoneId) {
		List<Map<String, Object>> data = portalService.getFinanceSheeetData(
				type, territoryId, zoneId);
		return data;

	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getUserType(HttpServletRequest request,
			HttpServletResponse response, Integer userId) {
		List<Map<String, Object>> res = null;
		res = portalService.getUserType(request, response, userId);
		return res;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getregionscities(
			HttpServletRequest request, HttpServletResponse response,
			String regions) {
		List<Map<String, Object>> data = null;
		data = portalService.getregionscities(request, response, regions);
		return data;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getUserRegions(HttpServletRequest request,
			HttpServletResponse response, String assingedRegions, Integer zoneId) {
		List<Map<String, Object>> data = null;
		data = portalService.getUserRegions(assingedRegions, zoneId);
		logger.info("data" + data);
		return data;
	}

	@Override
	@ResponseBody
	public String handle401Error() {
		// TODO Auto-generated method stub
		return "forward:/WEB-INF/jsp/MyHtml.html";
	}

	@ResponseBody
	public void primaryVsAchievement(HttpServletRequest request,
			HttpServletResponse response, Integer month, Integer year) {

		portalService.primaryVsAchievement(request, response, month, year);

	}

	@Override
	@ResponseBody
	public Map<String, Object> updateGodown(HttpServletRequest request,
			HttpServletResponse response, String title, String address,
			int rent, String type, int regionId, int id, String isNew) {

		return portalService.updateGodown(title, address, rent, type, regionId,
				id, isNew);
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> tpsomData(Integer dsrId, String startDate,
			String endDate) {
		List<Map<String, Object>> data = portalService.tpsomData(dsrId,
				startDate, endDate);
		return data;
	}
	
	@Override
	@ResponseBody
	public List<Map<String, Object>> dsrMorningImageData(Integer dsrId, String startDate,
			String endDate) {
		List<Map<String, Object>> data = portalService.dsrMorningImageData(dsrId,
				startDate, endDate);
		return data;
	}
	
	@Override
	@ResponseBody
	public Map<String, Object> loadFilters(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = null;
		try {
			data = portalService.LoadFilters(request, response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	@Override
	@ResponseBody
	public Map<String, Object> dashboardDataNew(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = portalService.dashboardDataNew(request, response);
		return data;
	}

	@Override
	@ResponseBody
	public void downloadReport(HttpServletRequest request, HttpServletResponse response) {
		portalService.downloadReport(request, response);

	}

	@Override
	@ResponseBody
	public void downloadcsvReport(HttpServletRequest request, HttpServletResponse response) {
		portalService.downloadcsvReport(request, response);

	}


}
