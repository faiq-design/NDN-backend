package com.marksman.census.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.marksman.census.bo.AdminProfile;
import com.marksman.census.bo.Factory;
import com.marksman.census.bo.Family;
import com.marksman.census.bo.Industry;
import com.marksman.census.bo.Region;
import com.marksman.census.bo.Shop;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.client.bo.ShopAssetImage;
import com.marksman.census.client.bo.TransferCodeDetails;

public interface PortalController {

	@RequestMapping(value = "/create/login")
	public ModelAndView createLogin(String userName, String password,
			Integer typeId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/updatePassword")
	public ModelAndView updatePassword(String newPassword,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/")
	public ModelAndView loginPage(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/login")
	public ModelAndView portalLogin(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/logout")
	public ModelAndView logoutPage(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/assignShops")
	public ModelAndView loadShopsToAssign(int surveyorId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/shopsTag")
	public ModelAndView shopsTag(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/duplicateClusters")
	public ModelAndView duplicateClusters(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/duplicateShops")
	public ModelAndView duplicateShops(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, String longitude, String latitude,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/viewShopsTag-ajax")
	public Map<String, Object> viewShopsTag(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, String startDate, String endDate,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/viewDuplicateClusters-ajax")
	public Map<String, Object> getDuplicateClusters(Integer zoneId,
			Integer regionId, Integer townId, Integer areaId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/saveAssignedShops")
	public void saveAssignedShops(Integer surveyorId, String shops,
			String merchandiserShopId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/pendingShops")
	public ModelAndView loadPendingShops(int surveyorId, int categoryId,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/Basic-Screening")
	public ModelAndView basicScreening(int shopId, int merchandiserShopId,
			int surveyorId, String buildVersion, String shopCLosed,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/Volume-check")
	public ModelAndView volumeCheck(int shopId, int merchandiserShopId,
			int surveyorId, String buildVersion, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/Visibility")
	public ModelAndView visibility(int shopId, int merchandiserShopId,
			int surveyorId, Integer categoryId, String buildVersion,
			String chillerType, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/hotZone")
	public ModelAndView hotZone(Integer merchandiserShopId, Integer surveyorId,
			Integer shopId, Integer displayId, Integer displayNumber,
			String buildVersion, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/primaryShelf")
	public ModelAndView primaryShelf(Integer merchandiserShopId,
			Integer surveyorId, Integer shopId, Integer displayId,
			Integer displayNumber, String buildVersion,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/Validate-Shop")
	public void validateShop(Integer shopId, Integer merchandiserShopId,
			String updatedValue, String oldValue, String validationAction,
			String validationStatus, Integer bwuId, Integer industryId,
			Integer surveyorId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/Validate-Shop-Categories")
	public void validateShopCategories(Integer surveyorId, Integer shopId,
			Integer merchandiserShopId, Integer categoryId, Integer displayId,
			Integer displayNumber, String oldTotalFacings,
			String oldMndlzFacings, String oldGoldMndlzFacings,
			String newTotalFacings, String newMndlzFacings,
			String newGoldMndlzFacings, String validationAction,
			String validationStatus, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/update-displayAudit-product-facing")
	public void updateDisplayAuditProductFacing(Integer surveyorId,
			Integer shopId, Integer merchandiserShopId, Integer categoryId,
			Integer productId, Integer displayId, Integer displayNumber,
			String oldFacing, String newFacing, String validationAction,
			String validationStatus, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/update-displayRemark")
	public void updateDisplayRemark(Integer surveyorId, Integer shopId,
			Integer merchandiserShopId, Integer categoryId, Integer displayId,
			Integer displayNumber, Integer displayRemark,
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/location")
	public ModelAndView loadLocation(Integer shopId,
			Integer merchandiserShopId, Integer surveyorId,
			String buildVersion, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/bwuImages")
	public ModelAndView loadBwuImages(int merchandiserShopId, int shopId,
			int industryId, int bwuId, HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/multiple/locations")
	public ModelAndView loadLocations(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/popup")
	public ModelAndView getPopup(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/shopRemarks")
	public ModelAndView getShopRemarks(HttpServletRequest request,
			HttpServletResponse response, Integer shopId, Integer surveyorId,
			Integer merchandiserShopId, Integer remarkId, String requestType);

	@RequestMapping(value = "/portal/shopAssetRemarks")
	public ModelAndView getShopAssetRemarks(HttpServletRequest request,
			HttpServletResponse response, Integer shopId, Integer surveyorId,
			Integer merchandiserShopId, Integer remarkId, String requestType,
			Integer merchandiserAssetId);

	@RequestMapping(value = "/portal/reEvaluate")
	public void reEvaluateShop(HttpServletRequest request,
			HttpServletResponse response, Integer shopId);

	@RequestMapping(value = "/merchandiserList")
	public List<Map<String, Object>> getMerchandiserList(
			HttpServletRequest request, HttpServletResponse response,
			String visitDate, Integer userId);

	@RequestMapping(value = "/shopList")
	public List<Map<String, Object>> getMerchandiserShopList(
			HttpServletRequest request, HttpServletResponse response,
			Integer surveyorId, String startDate, String endDate);

	@RequestMapping(value = "/evaluationShop")
	public Map<String, Object> getSectionList(HttpServletRequest request,
			HttpServletResponse response, Integer merchandiserShopId,
			Integer userTypeId, String visitType, Integer userId,
			String surveyorType);

	// By hamed
	@RequestMapping(value = "/portal/ndn/login", method = RequestMethod.POST)
	public Map<String, Object> getAuthForPortal(HttpServletRequest request,
			HttpServletResponse response, String userName, String password);

	// By hamed
	@RequestMapping(value = "portal/ndn/getMerchandiserShopListNDN", method = RequestMethod.POST)
	public List<Map<String, Object>> getMerchandiserShopListNDN(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId, Integer zoneId, Integer cityId,
			Integer distributionId, String storeType, Integer channelId,
			String startDate, String endDate, Integer rteId,
			Integer surveyorId, String nonEvaluated, Integer userId,
			String zones, String regions);

	// By hamed
	@RequestMapping(value = "portal/ndn/completedShopListNDN")
	public ArrayList<ShopAssetImage> getCompletedShopListNDN(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, String startDate, String endDate,
			Integer merchandiserId, Integer remarksId, String visitType,
			Integer userId);

	@RequestMapping(value = "portal/ndn/completedVisitShopListNDN")
	public ArrayList<ShopAssetImage> getCompletedVisitShopListNDN(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, String startDate, String endDate,
			Integer merchandiserId, Integer remarksId, String visitType,
			String evaluated);

	// By hamed
	@RequestMapping(value = "portal/ndn/getRegions")
	public List<Region> getRegions(HttpServletRequest request,
			HttpServletResponse response, Integer userId);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getDesAndDsrs", method = RequestMethod.POST)
	public List<Map<String, Object>> getDesAndDsrs(HttpServletRequest request,
			HttpServletResponse response, Integer regionId);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getDsrsDetails", method = RequestMethod.POST)
	public List<Map<String, Object>> getDsrDetailsForPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer surveyorId, Integer regionId, String status);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getDsrAreasDetails", method = RequestMethod.POST)
	public List<Map<String, Object>> getDsrAreasDetailsForPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer dsrId, Integer regionId);

	// By Hamed
	@RequestMapping(value = "portal/ndn/uploadImeisOnPortal", method = RequestMethod.POST)
	public Map<String, Object> uploadImeisOnPortal(HttpServletRequest request,
			HttpServletResponse response);

	// By Sajid
	@RequestMapping(value = "ndn/uploadDsrSaleTarget", method = RequestMethod.POST)
	public Map<String, Object> uploadDsrSaleTargetOnPortal(
			HttpServletRequest request, HttpServletResponse response);

	// By Hamed
	@RequestMapping(value = "portal/ndn/updateDeName", method = RequestMethod.POST)
	public Map<String, Object> updateDeName(HttpServletRequest request,
			HttpServletResponse response, Integer surveyorId, String deName,
			Integer userId, String deStatus, Integer employeeId,
			float basicSalary, float dailyAllownce, float fuelAllownce,
			float maintainanceAllownce, float otherAllowances);

	// By Hamed
	@RequestMapping(value = "portal/ndn/updateDsrDetails", method = RequestMethod.POST)
	public Map<String, Object> updateDsrDetails(HttpServletRequest request,
			HttpServletResponse response, Integer surveyorId, String dsrName,
			Integer dsrId, String dsrStatus, Integer userId, String dsrType,
			Integer dsrEmployee, float basicSalary, float dailyAllownce,
			float fuelAllownce, float maintainanceAllownce,
			float otherAllowances, float vanSalary);

	// By Hamed
	@RequestMapping(value = "portal/ndn/updateDsrAreasDetails", method = RequestMethod.POST)
	public Map<String, Object> updateDsrAreasDetails(
			HttpServletRequest request, HttpServletResponse response,
			Integer dsrId, String blockName, Integer blockId, Integer userId,
			String blockStatus, String dsrType, Integer dsrVanId, Integer townId);

	// By Hamed
	@RequestMapping(value = "portal/ndn/addDsr", method = RequestMethod.POST)
	public Map<String, Object> addDsrFromPortal(HttpServletRequest request,
			HttpServletResponse response, Integer surveyorId, String dsrName,
			Integer userId, String dsrType, Integer dsrEmployee);

	// By Hamed
	@RequestMapping(value = "portal/ndn/addBlock", method = RequestMethod.POST)
	public Map<String, Object> addBlockFromPortal(HttpServletRequest request,
			HttpServletResponse response, String blockName, Integer townId,
			Integer dsrId, Integer userId, Integer dsrVanId,
			Integer exportedAreaId);

	@RequestMapping(value = "portal/ndn/blockTransfer", method = RequestMethod.POST)
	public Map<String, Object> blockTransfer(HttpServletRequest request,
			HttpServletResponse response, String blockName, Integer blockId,
			Integer townId, Integer dsrId, Integer userId);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getImeis")
	public List<Map<String, Object>> getImeisforPortal(
			HttpServletRequest request, HttpServletResponse response);

	// By Hamed
	@RequestMapping(value = "portal/ndn/updateImei", method = RequestMethod.POST)
	public Map<String, Object> updateImeiFromPortal(HttpServletRequest request,
			HttpServletResponse response, String imei, String imeiStatus);

	// By Sajid
	@RequestMapping(value = "ndn/getSaleTarget", method = RequestMethod.POST)
	public List<Map<String, Object>> getDsrSaleTargetforPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer month, Integer year, Integer regionId, Integer townId,
			Integer zoneId, Integer type);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getDsrs", method = RequestMethod.POST)
	public List<Map<String, Object>> getDsrs(HttpServletRequest request,
			HttpServletResponse response, Integer surveyorId, String dsrType);

	@RequestMapping(value = "portal/ndn/getDsrsByRegionId", method = RequestMethod.POST)
	public List<Map<String, Object>> getDsrsByRegions(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getTowns", method = RequestMethod.POST)
	public List<Map<String, Object>> getTowns(HttpServletRequest request,
			HttpServletResponse response, Integer regionId);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getRegionsByZoneId", method = RequestMethod.POST)
	public List<Map<String, Object>> getRegionsByZoneId(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId);

	@RequestMapping(value = "portal/ndn/getRegionsByZoneIdNew", method = RequestMethod.POST)
	public List<Map<String, Object>> getRegionsByZoneIdNew(
			HttpServletRequest request, HttpServletResponse response,
			String zoneId);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getFamiliesForPortal", method = RequestMethod.GET)
	public List<Map<String, Object>> getFamiliesForPortal(
			HttpServletRequest request, HttpServletResponse response);

	// By Hamed
	@RequestMapping(value = "portal/ndn/submitTransferedStock", method = RequestMethod.POST)
	public Map<String, Object> submitTransferedStock(
			HttpServletRequest request, HttpServletResponse response);

	// By Hamed
	@RequestMapping(value = "portal/ndn/submitReceivedStock", method = RequestMethod.POST)
	public Map<String, Object> submitReceivedStock(HttpServletRequest request,
			HttpServletResponse response);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getTerritoryFamiliesForPortal", method = RequestMethod.POST)
	public List<Map<String, Object>> getTerritoryFamiliesForPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer territoryId);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getTownFamiliesForPortal", method = RequestMethod.POST)
	public List<Map<String, Object>> getTownFamiliesForPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer townId);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getTerritoryReceivedStock", method = RequestMethod.POST)
	public List<Map<String, Object>> getTerritoryReceivedStock(
			HttpServletRequest request, HttpServletResponse response,
			String transferType, Integer territoryId, String transferCode);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getTerritoryReceivedStockTransferCodes", method = RequestMethod.POST)
	public List<Map<String, Object>> getTerritoryReceivedStockTransferCodes(
			HttpServletRequest request, HttpServletResponse response,
			String transferType, Integer territoryId);

	// By Hamed
	@RequestMapping(value = "portal/ndn/checkTransferCode", method = RequestMethod.POST)
	public Map<String, Object> checkTransferCodeAvailability(String transferCode);

	// By hamed
	@RequestMapping(value = "portal/ndn/getReceivedStockTransferCodes")
	public List<Map<String, Object>> getReceivedStockCodes(
			HttpServletRequest request, HttpServletResponse response);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getReceivedStockTransferCodesForPortal")
	public List<Map<String, Object>> getReceivedStockCodesForPortal(
			HttpServletRequest request, HttpServletResponse response);

	// By hamed
	@RequestMapping(value = "portal/ndn/getTransitCodeDetails", method = RequestMethod.POST)
	public TransferCodeDetails getTransitDetails(HttpServletRequest request,
			HttpServletResponse response, String transitCode, Integer userId);

	// By hamed
	@RequestMapping(value = "portal/ndn/getReceivedStock", method = RequestMethod.POST)
	public List<Map<String, Object>> getReceivedStock(
			HttpServletRequest request, HttpServletResponse response,
			Integer transferId, Integer userId);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getFactoryStockFamilies", method = RequestMethod.POST)
	public List<Map<String, Object>> getFactoryStockFamiliesForPortal(
			Integer factoryId);

	// M Awais
	@RequestMapping(value = "portal/ndn/distribution-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getDistributionList(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId, Integer cityId);

	// M Awais
	@RequestMapping(value = "portal/ndn/update-distribution", method = RequestMethod.POST)
	public Map<String, Object> updateDistribution(HttpServletRequest request,
			HttpServletResponse response, Integer distributionId,
			double distLat, double distLong, String distName, Integer totalDE,
			Integer totalDSR, Integer investment, String distType,
			Integer regionId, Integer cityId, Integer userId);

	// M Awais
	@RequestMapping(value = "portal/ndn/block-wise-shop-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getBlockWiseShopList(
			HttpServletRequest request, HttpServletResponse response,
			Integer areaId, Integer regionId, Integer surveyorId, Integer dsrId);

	// M Awais
	@RequestMapping(value = "portal/ndn/surveyor-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getSurveyorList(
			HttpServletRequest request, HttpServletResponse response);

	// M Awais
	@RequestMapping(value = "portal/ndn/transfer-shops", method = RequestMethod.POST)
	public Map<String, Object> checkTransferShops(HttpServletRequest request,
			HttpServletResponse response, String shops, Integer areaId,
			Integer userId);

	// By Muhammad Hamed
	@RequestMapping(value = "portal/ndn/getFactoryList", method = RequestMethod.POST)
	public List<Factory> getFactoryList(HttpServletRequest request,
			HttpServletResponse response, String type);

	// By Sajid
	@RequestMapping(value = "portal/ndn/de-attendance")
	public List<Map<String, Object>> getDeAttendanceList(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId, String startDate, String endDate,
			String attendanceType, Integer zoneId);

	@RequestMapping(value = "portal/ndn/getRemarks")
	List<Map<String, Object>> getRemarksList(HttpServletRequest request,
			HttpServletResponse response, String typeId);

	@RequestMapping(value = "portal/ndn/update-workType", method = RequestMethod.POST)
	public Map<String, Object> updateDeAttendanceWorkType(
			HttpServletRequest request, HttpServletResponse response,
			Integer Id, Integer workTypeId, Integer userId);

	// M Awais
	@RequestMapping(value = "portal/ndn/evaluate-single-shop", method = RequestMethod.POST)
	public Map<String, Object> evaluateSingleShop(HttpServletRequest request,
			HttpServletResponse response);

	// By Hamed
	@RequestMapping(value = "portal/ndn/getTmProductivityNDN", method = RequestMethod.POST)
	public List<Map<String, Object>> getTmProductivityNDN(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId, Integer zoneId, Integer cityId,
			Integer distributionId, String storeType, Integer channelId,
			String startDate, String endDate, Integer rteId,
			Integer surveyorId, Integer userId, String zones, String regions);

	// M Awais
	@RequestMapping(value = "portal/ndn/employee-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getEmployeeList(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId);

	// Muhammad Hamed
	@RequestMapping(value = "portal/ndn/transfer-blocks", method = RequestMethod.POST)
	public Map<String, Object> transferBlocks(HttpServletRequest request,
			HttpServletResponse response, String blocks, Integer dsrId,
			Integer userId);

	// M Awais
	@RequestMapping(value = "portal/ndn/query-type-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getQueryTypeList(
			HttpServletRequest request, HttpServletResponse response,
			Integer userId);

	// M Awais
	@RequestMapping(value = "portal/ndn/merchandiser-dsr-sales-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getMerchandiserDsrSalesList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer userId,
			Integer familyId, String currentDate);

	@RequestMapping(value = "portal/ndn/merchandiser-spot-selling-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getMerchandiserSpotSellingList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer userId,
			Integer familyId, String currentDate);

	@RequestMapping(value = "portal/ndn/merchandiser-dsr-shop-sales-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getMerchandiserDsrShopSalesList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer userId,
			Integer familyId, String currentDate, String promotionType,
			String schemeTitle);

	// M Awais
	@RequestMapping(value = "portal/ndn/update-dsr-sales", method = RequestMethod.POST)
	public Map<String, Object> updateDsrSales(HttpServletRequest request,
			HttpServletResponse response, Integer dsrSaleId, float retailValue,
			float wholeSaleValue, Integer userId);

	@RequestMapping(value = "portal/ndn/update-spot-selling", method = RequestMethod.POST)
	public Map<String, Object> updateSpotSellingValue(
			HttpServletRequest request, HttpServletResponse response,
			Integer spotsellingId, float value, Integer userId);

	@RequestMapping(value = "portal/ndn/update-dsr-shop-sales", method = RequestMethod.POST)
	public Map<String, Object> updateDsrShopSales(HttpServletRequest request,
			HttpServletResponse response, Integer dsrSaleId, float saleValue,
			Integer userId, String promoType, float discountValue);

	// created by muhammad hamed
	@RequestMapping(value = "portal/ndn/getTransactionsData", method = RequestMethod.POST)
	public List<TransferCodeDetails> getTransactionsData(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, String transferType,
			Integer userId);

	// created by muhammad hamed
	@RequestMapping(value = "portal/ndn/getTransactionsStockData", method = RequestMethod.POST)
	public List<Map<String, Object>> getTransactionStockDetails(
			HttpServletRequest request, HttpServletResponse respons,
			Integer transferId, Integer userId);

	@RequestMapping(value = "portal/ndn/getDeListByRegionId", method = RequestMethod.POST)
	public List<Surveyor> getDeListByRegionIdForPortal(
			HttpServletRequest request, HttpServletResponse respons,
			Integer regionId, Integer zoneId, String surveyorType);

	@RequestMapping(value = "portal/ndn/save-dsr-sale", method = RequestMethod.POST)
	public Map<String, Object> saveDSRSale(HttpServletRequest request,
			HttpServletResponse respons, String date, Integer surveyorId,
			Integer dsrId, Integer familyId, float retailValue,
			float wholeSaleValue, Integer userId, Integer cityId);

	// created by muhammad hamed
	@RequestMapping(value = "portal/ndn/reverseTransaction", method = RequestMethod.POST)
	public Map<String, Object> reverseTransaction(HttpServletRequest request,
			HttpServletResponse respons, Integer transactionId, Integer userId);

	@RequestMapping(value = "portal/ndn/dsrs-by-de-id", method = RequestMethod.POST)
	public List<Map<String, Object>> getDSRsByDEId(HttpServletRequest request,
			HttpServletResponse respons, Integer deId);

	// created by muhammad hamed
	@RequestMapping(value = "portal/ndn/getUsersAttendanceForApproval", method = RequestMethod.POST)
	public Map<String, Object> getUsersForAttendanceApprovals(
			HttpServletRequest request, HttpServletResponse respons,
			String userType, String selectedDate, Integer zoneId,
			Integer regionId, Integer userId);

	// created muhammad hamed
	@RequestMapping(value = "portal/ndn/updateAttendanceForApproval", method = RequestMethod.POST)
	public Map<String, Object> UpdateAttendanceForApprovals(
			HttpServletRequest request, HttpServletResponse respons);

	// created muhammad hamed
	@RequestMapping(value = "portal/ndn/getShopsForMapView", method = RequestMethod.POST)
	public List<Map<String, Object>> getShopsForDeTracking(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, Integer surveyorId,
			Integer dsrId, String selectedDate, String mapViewType,
			Integer userId, String startDate, String endDate, String spentTime);

	// created by muhammad hamed
	@RequestMapping(value = "portal/ndn/getDeListByRegionIdForDeTracking", method = RequestMethod.POST)
	public List<Surveyor> getDEListByRegionIdForDeTracking(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, String mapViewType,
			String selectedDate, Integer userId);

	@RequestMapping(value = "portal/ndn/getDsrListBySurveyorIdForDeTracking", method = RequestMethod.POST)
	public List<Map<String, Object>> getDsrListBySurveyorIdForDeTracking(
			HttpServletRequest request, HttpServletResponse response,
			Integer surveyorId, String mapViewType, String selectedDate,
			Integer userId);

	@RequestMapping(value = "portal/ndn/insertCityFromPortal", method = RequestMethod.POST)
	public Map<String, Object> insertCityFromPortal(HttpServletRequest request,
			HttpServletResponse response, Integer regionId, String cityName,
			Integer cityId, Integer userId);

	@RequestMapping(value = "portal/ndn/updateCityName", method = RequestMethod.POST)
	public Map<String, Object> updateCityFromPortal(HttpServletRequest request,
			HttpServletResponse response, Integer cityId, String cityName,
			Integer userId);

	@RequestMapping(value = "portal/ndn/getDeList", method = RequestMethod.POST)
	public List<Map<String, Object>> getDeListByRegionId(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, String regionId, String surveyorType, String status);

	@RequestMapping(value = "portal/ndn/getEmployeeList", method = RequestMethod.POST)
	public List<Map<String, Object>> getEmployeeList(
			HttpServletRequest request, HttpServletResponse response,
			String surveyorType, String mustHave);

	@RequestMapping(value = "portal/ndn/getAvailableEmployeeListForDE", method = RequestMethod.POST)
	public List<Map<String, Object>> getAvailableEmployeeListForDE(
			HttpServletRequest request, HttpServletResponse response,
			String surveyorType);

	@RequestMapping(value = "portal/ndn/insertEmployeeFromPortal", method = RequestMethod.POST)
	public Map<String, Object> insertEmployeeFromPortal(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/insertDeFromPortal", method = RequestMethod.POST)
	public Map<String, Object> insertDeFromPortal(HttpServletRequest request,
			HttpServletResponse response, String regionId, Integer employeeId,
			String deCode, String employeeType, Integer userId,
			float basicSalary, float dailyAllownce, float fuelAllownce,
			Float maintenanceAllownce, Float otherAllowance);

	@RequestMapping(value = "portal/ndn/secondary-sale", method = RequestMethod.POST)
	public Map<String, Object> getSecondarySale(HttpServletRequest request,
			HttpServletResponse response, Integer regionId, Integer cityId,
			Integer brandId, Integer month, Integer year, Integer userId);

	@RequestMapping(value = "portal/ndn/primary-sale", method = RequestMethod.POST)
	public Map<String, Object> getPrimarySale(HttpServletRequest request,
			HttpServletResponse response, Integer regionId, Integer cityId,
			Integer brandId, Integer month, Integer year, Integer userId);

	@RequestMapping(value = "portal/ndn/updatePassword", method = RequestMethod.POST)
	public Map<String, Object> updatePasswordFromPortal(
			HttpServletRequest request, HttpServletResponse response,
			String oldPassword, String newPassword, Integer userId);

	@RequestMapping(value = "portal/ndn/getWebPortalUsers", method = RequestMethod.POST)
	public ArrayList<AdminProfile> getUsersDataForPasswordUpdation(
			HttpServletRequest request, HttpServletResponse response,
			Integer userTypeId);

	@RequestMapping(value = "portal/ndn/updatePasswordByAdmin", method = RequestMethod.POST)
	public Map<String, Object> updatePasswordByAdmin(
			HttpServletRequest request, HttpServletResponse response,
			String newPassword, Integer userId, String userType);

	// need to be move to config file
	@RequestMapping(value = "portal/ndn/transfers-types", method = RequestMethod.POST)
	public List<Map<String, Object>> stockTransfersTypes(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/admin-role", method = RequestMethod.POST)
	public List<Map<String, Object>> getAdminRole(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/tableau-ticket", method = RequestMethod.POST)
	public Map<String, Object> getTableauTicket(HttpServletRequest request,
			HttpServletResponse response, String type);

	@RequestMapping(value = "portal/ndn/save-user-role")
	public Map<String, Object> saveUserRole(HttpServletRequest request,
			HttpServletResponse response, String title);

	@RequestMapping(value = "portal/ndn/update-user-role-status")
	public Map<String, Object> updateUserRoleStatus(HttpServletRequest request,
			HttpServletResponse response, Integer roleId, String status);

	@RequestMapping(value = "portal/ndn/menu-list")
	public Map<String, Object> getMenuList(HttpServletRequest request,
			HttpServletResponse response, Integer roleId);

	@RequestMapping(value = "portal/ndn/update-menu-list")
	public Map<String, Object> updateMenuList(HttpServletRequest request,
			HttpServletResponse response, String menus, Integer roleId,
			Integer act, Integer userId);

	@RequestMapping(value = "portal/sis/surveyor-list")
	public List<Map<String, Object>> sisSurveyorList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer cityId, Integer userId);

	@RequestMapping(value = "portal/ndn/update-stock", method = RequestMethod.POST)
	public Map<String, Object> updateStock(HttpServletRequest request,
			HttpServletResponse response, String stockType, Integer userId,
			String projectName);

	@RequestMapping(value = "portal/dsr/surveyor-list")
	public List<Map<String, Object>> dsrSurveyorList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer cityId, Integer userId, String zones, String regions);

	@RequestMapping(value = "portal/dsr/shop-list", method = RequestMethod.POST)
	public ArrayList<ShopAssetImage> dsrShopList(HttpServletRequest request,
			HttpServletResponse response, Integer zoneId, Integer regionId,
			Integer cityId, String startDate, String endDate,
			Integer merchandiserId, Integer userId, String visitType,
			String newShop);

	@RequestMapping(value = "/portal/ndn/tpsom-data", method = RequestMethod.POST)
	public List<Map<String, Object>> tpsomData(Integer dsrId, String startDate,
			String endDate);

	@RequestMapping(value = "portal/update/surveyor-region")
	public Map<String, Object> updateSurveyorRegion(HttpServletRequest request,
			HttpServletResponse response, String regionId, Integer surveyorId,
			Integer userId);

	@RequestMapping(value = "portal/ndn/industry-list", method = RequestMethod.GET)
	public List<Industry> getIndustryList(HttpServletRequest request,
			HttpServletResponse response, Integer userId);

	@RequestMapping(value = "portal/ndn/brand-list", method = RequestMethod.POST)
	public List<Family> getBrandList(HttpServletRequest request,
			HttpServletResponse response, Integer industryId, Integer userId,
			String type);

	@RequestMapping(value = "portal/ndn/create-brand", method = RequestMethod.POST)
	public Map<String, Object> createBrand(HttpServletRequest request,
			HttpServletResponse response, Integer industryId,
			String brandTitle, Integer brandSticks, Integer userId, String type);

	@RequestMapping(value = "portal/refresh/cache", method = RequestMethod.GET)
	public void refreshCache(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/create-region", method = RequestMethod.POST)
	public Map<String, Object> createRegion(HttpServletRequest request,
			HttpServletResponse response, Integer zoneId, String regionTitle,
			Integer userId);

	@RequestMapping(value = "portal/ndn/update-surveyor-status", method = RequestMethod.POST)
	public Map<String, Object> updateSurveyorStatus(HttpServletRequest request,
			HttpServletResponse response, Integer surveyorId, String status,
			Integer userId, String surveyorType, Integer dsrId);

	@RequestMapping(value = "portal/ndn/update-employee-status", method = RequestMethod.POST)
	public Map<String, Object> updateEmployeeStatus(HttpServletRequest request,
			HttpServletResponse response, Integer employeeId, String status);

	@RequestMapping(value = "portal/ndn/update-status", method = RequestMethod.POST)
	public Map<String, Object> updateStatus(HttpServletRequest request,
			HttpServletResponse response, Integer Id, String Active,
			Integer userId);

	@RequestMapping(value = "portal/ndn/update-date", method = RequestMethod.POST)
	public Map<String, Object> updateInActiveDate(HttpServletRequest request,
			HttpServletResponse response, Integer employeeId,
			String inactiveDate);

	@RequestMapping(value = "portal/ndn/update-scheme-date", method = RequestMethod.POST)
	public Map<String, Object> updateActiveDate(HttpServletRequest request,
			HttpServletResponse response, Integer Id, String Active,
			String inactiveDate, Integer userId);

	@RequestMapping(value = "portal/ndn/delete-sale-target", method = RequestMethod.POST)
	public Map<String, Object> deleteSaleTarget(HttpServletRequest request,
			HttpServletResponse response, Integer targetId, Integer userId);

	@RequestMapping(value = "portal/ndn/test", method = RequestMethod.GET)
	public void portalTest(HttpServletRequest request,
			HttpServletResponse response);

	// Abubakar & Ubaid
	@RequestMapping(value = "ndn/getWSTKSSaleTarget", method = RequestMethod.POST)
	public List<Map<String, Object>> getWSTKSSaleTargetforPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer month, Integer year, Integer regionId, Integer townId,
			Integer zoneId, String shopType);

	@RequestMapping(value = "portal/ndn/delete-ws-tks-sale-target", method = RequestMethod.POST)
	public Map<String, Object> deleteWSTKSSaleTarget(
			HttpServletRequest request, HttpServletResponse response,
			Integer targetId, Integer userId);

	@RequestMapping(value = "ndn/uploadWSTKSSaleTarget", method = RequestMethod.POST)
	public Map<String, Object> uploadWSTKSSaleTargetOnPortal(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/merchandiser-ws-tks-sales-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getMerchandiserWSTKSSalesList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer userId, String type,
			Integer shopId, Integer familyId, String currentDate);

	@RequestMapping(value = "portal/ndn/update-ws-tks-sales", method = RequestMethod.POST)
	public Map<String, Object> updateWSTKSSales(HttpServletRequest request,
			HttpServletResponse response, Integer surveyId, Integer familyId,
			float wholeSaleValue, String type, Integer remarksId,
			String updateType, Integer userId);

	@RequestMapping(value = "portal/ndn/merchandiser-ws-tks-shops-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getMerchandiserWSTKSShopList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer userId, String type,
			Integer shopId);

	@RequestMapping(value = "portal/ndn/add-merchandiser-ws-tks-sale", method = RequestMethod.POST)
	public Map<String, Object> addNewWSTKSSale(HttpServletRequest request,
			HttpServletResponse response, Integer surveyorId, Integer userId,
			String type, Integer shopId, String visitDate, Integer dsrId,
			Integer employeeId, Integer familyId, float value,
			String visitDateTime, Integer remark);

	@RequestMapping(value = "portal/ndn/get-ra-shops", method = RequestMethod.POST)
	public List<Shop> getRAShops(HttpServletRequest request,
			HttpServletResponse response, Integer regionId);

	@RequestMapping(value = "ndn/uploadRAShops", method = RequestMethod.POST)
	public Map<String, Object> uploadRAShopsOnPortal(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "ndn/update-surveyor-employee", method = RequestMethod.POST)
	public Map<String, Object> updateSurveyorEmloyee(
			HttpServletRequest request, HttpServletResponse response,
			Integer surveyorId, Integer employeeId, Integer userId,
			float basicSalary, float dailyAllownce, float fuelAllownce,
			Float maintenanceAllownce, Float otherAllowance, Integer cityId);

	@RequestMapping(value = "ndn/get-areas-by-region", method = RequestMethod.POST)
	public List<Map<String, Object>> getAreasByRegionId(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId);

	@RequestMapping(value = "ndn/create-user-from-portal", method = RequestMethod.POST)
	public Map<String, Object> createUserFromPortal(HttpServletRequest request,
			HttpServletResponse response, String username, String password,
			Integer role, String zone, String region, String area);

	@RequestMapping(value = "ndn/dsr-attendance-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getDsrListForAttendance(
			HttpServletRequest request, HttpServletResponse response,
			Integer surveyorId, String visitDate, Integer territoryId);

	@RequestMapping(value = "portal/ndn/update-dsr-attendance", method = RequestMethod.POST)
	public Map<String, Object> updateDsrAttendance(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, Integer workTypeId,
			Integer userId, Integer surveyorId, String visitDate);

	@RequestMapping(value = "portal/ndn/get-working-days", method = RequestMethod.POST)
	public List<Map<String, Object>> getWorkingDays(HttpServletRequest request,
			HttpServletResponse response, Integer userId, Integer month,
			Integer year);

	@RequestMapping(value = "portal/ndn/update-working-days", method = RequestMethod.POST)
	public Map<String, Object> updateWorkingDays(HttpServletRequest request,
			HttpServletResponse response, Integer userId, Integer month,
			Integer year, String value, String date);

	@RequestMapping(value = "portal/ndn/sis-industry-list", method = RequestMethod.GET)
	public List<Industry> getSisIndustryList(HttpServletRequest request,
			HttpServletResponse response, Integer userId);

	@RequestMapping(value = "portal/ndn/get-new-sis-brands", method = RequestMethod.POST)
	public List<Map<String, Object>> getNewSisBrands(
			HttpServletRequest request, HttpServletResponse response,
			Integer industryId, Integer zoneId, Integer regionId,
			Integer cityId, Integer surveyorId, String startDate, String endDate);

	@RequestMapping(value = "portal/ndn/update-new-brands", method = RequestMethod.POST)
	public Map<String, Object> updateNewBrands(HttpServletRequest request,
			HttpServletResponse response, String newBrandId,
			Integer systemBrandId, Integer userId);

	@RequestMapping(value = "portal/ndn/shop-profile")
	public List<Map<String, Object>> getShopProfile(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/sis-surveyor")
	public List<Map<String, Object>> getSisSurveyor(HttpServletRequest request,
			HttpServletResponse response, Integer regionId, Integer typeId);

	@RequestMapping(value = "portal/ndn/update-town-status", method = RequestMethod.POST)
	public Map<String, Object> updateTownStatus(HttpServletRequest request,
			HttpServletResponse response, Integer cityId, String status,
			String userId);

	@RequestMapping(value = "portal/ndn/get-towns-for-portal", method = RequestMethod.POST)
	public List<Map<String, Object>> getTownsForPortal(
			HttpServletRequest request, HttpServletResponse response,
			Integer regionId);

	@RequestMapping(value = "portal/ndn/sis-brands")
	public List<Map<String, Object>> getSisBrands(HttpServletRequest request,
			HttpServletResponse response, Integer industryId);

	@RequestMapping(value = "portal/ndn/update-old-sis-brand")
	public Map<String, Object> updateSisOldBrands(HttpServletRequest request,
			HttpServletResponse response, Integer industryId, Integer familyId);

	@RequestMapping(value = "portal/ndn/sis-attendance")
	public List<Map<String, Object>> getSisAttendanceList(
			HttpServletRequest request, HttpServletResponse response,
			String startDate, String endDate, Integer surveyorId, Integer typeId);

	@RequestMapping(value = "ndn/get-areas-by-town", method = RequestMethod.POST)
	public List<Map<String, Object>> getAreasByTownId(
			HttpServletRequest request, HttpServletResponse response,
			Integer townId);

	@RequestMapping(value = "portal/ndn/insert-area-from-portal", method = RequestMethod.POST)
	public Map<String, Object> insertAreaFromPortal(HttpServletRequest request,
			HttpServletResponse response, Integer townId, String areaName,
			Integer userId);

	@RequestMapping(value = "portal/ndn/get-sis-shops-for-mapview", method = RequestMethod.POST)
	public List<Map<String, Object>> getShopsForSisTracking(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, Integer cityId,
			Integer surveyorId, String selectedDate, String mapViewType,
			Integer userId, String startDate, String endDate, String shopType);

	@RequestMapping(value = "portal/ndn/get-surveyor-list-by-region-id-for-sis-tracking", method = RequestMethod.POST)
	public List<Surveyor> getSisListByRegionIdForSurveyorTracking(
			HttpServletRequest request, HttpServletResponse response,
			Integer zoneId, Integer regionId, String mapViewType,
			String selectedDate, Integer userId, String surveyorType);

	@RequestMapping(value = "portal/ndn/get-schemes", method = RequestMethod.POST)
	public List<Map<String, Object>> getSchemes(HttpServletRequest request,
			HttpServletResponse response, Integer cityId, Integer shopId);

	@RequestMapping(value = "portal/ndn/get-overall-schemes", method = RequestMethod.POST)
	public List<Map<String, Object>> getOverallSchemes(
			HttpServletRequest request, HttpServletResponse response,
			String promoType, Integer brandId, String status, String type);

	@RequestMapping(value = "portal/ndn/update-scheme-tagging", method = RequestMethod.POST)
	public Map<String, Object> updateSchemeTagging(HttpServletRequest request,
			HttpServletResponse response, String schemeId, Integer townId,
			Integer shopId, Integer userId);

	@RequestMapping(value = "portal/ndn/insert-new-scheme", method = RequestMethod.POST)
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
			final Integer upperLimit);

	@RequestMapping(value = "portal/ndn/update-town-scheme", method = RequestMethod.POST)
	public Map<String, Object> updateTownSchemeStatus(
			HttpServletRequest request, HttpServletResponse response,
			Integer schemeId, String status, String userId);

	@RequestMapping(value = "ndn/upload-dsr-routes", method = RequestMethod.POST)
	public Map<String, Object> uploadDsrRoutes(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/get-tree-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getTreeList(HttpServletRequest request,
			HttpServletResponse response, String type, Integer familyId,
			Integer promoId);

	@RequestMapping(value = "portal/ndn/get-assigned-list", method = RequestMethod.POST)
	public List<String> getPromoAssignedList(HttpServletRequest request,
			HttpServletResponse response, Integer promoId);

	@RequestMapping(value = "portal/ndn/assign-promo", method = RequestMethod.POST)
	public Map<String, Object> assignPromo(HttpServletRequest request,
			HttpServletResponse response, Integer promoId, String citiesList,
			Integer userId, float schemeStock, int familyId, String type);

	@RequestMapping(value = "portal/ndn/get-received-stock-code")
	public List<Map<String, Object>> getEditReceivedStock(
			HttpServletRequest request, HttpServletResponse response,
			String receivedDate, Integer userId, String type);

	@RequestMapping(value = "portal/ndn/get-adjustment-stock", method = RequestMethod.POST)
	public List<Map<String, Object>> getAdjustmentStock(
			HttpServletRequest request, HttpServletResponse response,
			Integer transferId, Integer userId);

	@RequestMapping(value = "portal/ndn/submit-adjusted-stock", method = RequestMethod.POST)
	public Map<String, Object> submitAdjustedStock(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/get-route-tagging", method = RequestMethod.POST)
	public Map<String, Object> getRouteTagging(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, Integer type);

	@RequestMapping(value = "portal/ndn/assign-routes", method = RequestMethod.POST)
	public Map<String, Object> assignDsrRoutes(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, String routeList,
			Integer type, Integer userId);

	@RequestMapping(value = "portal/ndn/get-stock-scheme-city", method = RequestMethod.POST)
	public List<Map<String, Object>> getStockSchemeCity(
			HttpServletRequest request, HttpServletResponse response,
			Integer promoId, Float schemeStock, Float consumedStock);

	@RequestMapping(value = "portal/ndn/assign-city-stock-scheme", method = RequestMethod.POST)
	public Map<String, Object> assignStockCityScheme(
			HttpServletRequest request, HttpServletResponse respons);

	@RequestMapping(value = "portal/ndn/get-assigned-brand-list", method = RequestMethod.POST)
	public List<String> getBrandAssignedList(HttpServletRequest request,
			HttpServletResponse response, Integer familyId);

	@RequestMapping(value = "portal/ndn/assign-brand-list", method = RequestMethod.POST)
	public Map<String, Object> assignBrandList(HttpServletRequest request,
			HttpServletResponse respons);

	@RequestMapping(value = "portal/ndn/get-brand-list", method = RequestMethod.GET)
	public List<Map<String, Object>> getBrandList(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/get-all-brand-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getAllBrandList(
			HttpServletRequest request, HttpServletResponse response,
			Integer industryId, String type);

	@RequestMapping(value = "portal/ndn/get-rate-list", method = RequestMethod.GET)
	public List<Map<String, Object>> getFamilySetList(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/insert-dsr-employee", method = RequestMethod.POST)
	public Map<String, Object> insertDsrEmployee(HttpServletRequest request,
			HttpServletResponse response, String dsrName);

	@RequestMapping(value = "portal/ndn/dsr-employee-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getDsrEmployeeList(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "ndn/update-dsr-employee", method = RequestMethod.POST)
	public Map<String, Object> updateDsrEmloyee(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, Integer employeeId,
			Integer userId, float basicSalary, float dailyAllownce,
			float fuelAllownce, float maintainanceAllownce,
			float otherAllowance, Integer cityId, String registrationNo,
			String makeModel, String vanType, String dsrType);

	@RequestMapping(value = "portal/ndn/insertDsrFromPortal", method = RequestMethod.POST)
	public Map<String, Object> insertDsrFromPortal(HttpServletRequest request,
			HttpServletResponse response, Integer employeeId, String deCode,
			String employeeType, Integer selectedDE, Integer userId,
			Integer rateList, float basicSalary, float dailyAllownce,
			float fuelAllownce, Float maintenanceAllownce,
			Float otherAllowance, Float vanSalary, String registrationNo,
			String makeModel, String vanType);

	@RequestMapping(value = "portal/ndn/towns", method = RequestMethod.POST)
	public List<Map<String, Object>> getCities(HttpServletRequest request,
			HttpServletResponse response, Integer regionId);

	@RequestMapping(value = "portal/ndn/townsNew", method = RequestMethod.POST)
	public List<Map<String, Object>> getCitiesNew(HttpServletRequest request,
			HttpServletResponse response, String regionId);

	@RequestMapping(value = "portal/ndn/get-distinct-schemes", method = RequestMethod.POST)
	public List<Map<String, Object>> getDistinctSchemes(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/delete-dsr-day-end", method = RequestMethod.POST)
	public Map<String, Object> deleteDsrDayEnd(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, Integer userId);

	@RequestMapping(value = "portal/ndn/update-employee-name", method = RequestMethod.POST)
	public Map<String, Object> updateEmployeeName(HttpServletRequest request,
			HttpServletResponse response, String employeeName, long phone,
			long cnic, Integer basicSalary, Integer dailyAllownce,
			Integer fuelAllownce, Integer maintenanceAllownce,
			Integer employeeId, Integer userId);

	@RequestMapping(value = "portal/ndn/get-brand-price-detail", method = RequestMethod.POST)
	public List<Map<String, Object>> getBrandPriceDetails(
			HttpServletRequest request, HttpServletResponse response,
			Integer brandId, Integer brandPriceId);

	@RequestMapping(value = "portal/ndn/insert-brand-price", method = RequestMethod.POST)
	public Map<String, Object> addBrandPrice(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/update-brand-price-detail", method = RequestMethod.POST)
	public Map<String, Object> updateBrandPrice(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/get-brand-prices", method = RequestMethod.GET)
	public List<Map<String, Object>> getBrandPrices(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/insert-brand-price-detail", method = RequestMethod.POST)
	public Map<String, Object> addBrandPriceDetail(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/get-assigned-tree-list", method = RequestMethod.POST)
	public List<Map<String, Object>> getAssignedBrandList(
			HttpServletRequest request, HttpServletResponse response,
			String type, Integer brandPriceId);

	@RequestMapping(value = "portal/ndn/get-rate-assigned-brand-list", method = RequestMethod.POST)
	public List<String> getBrandRateAssignedList(HttpServletRequest request,
			HttpServletResponse response, Integer familyId);

	@RequestMapping(value = "portal/ndn/assign-rate-list", method = RequestMethod.POST)
	public Map<String, Object> assignRateList(HttpServletRequest request,
			HttpServletResponse respons);

	@RequestMapping(value = "portal/ndn/delete-rate-list", method = RequestMethod.POST)
	public void deleteRateList(HttpServletRequest request,
			HttpServletResponse respons, Integer brandPriceId, Integer userId);

	@RequestMapping(value = "portal/ndn/import-shops-from-block", method = RequestMethod.POST)
	public void importShopsFromBlock(HttpServletRequest request,
			HttpServletResponse respons, Integer exportAreaId,
			Integer importAreaId);

	@RequestMapping(value = "portal/ndn/update-brand-status", method = RequestMethod.POST)
	public Map<String, Object> updateBrandStatus(HttpServletRequest request,
			HttpServletResponse response, Integer brandId, String status);

	@RequestMapping(value = "portal/ndn/territory-wise-radius", method = RequestMethod.POST)
	public List<Map<String, Object>> getTerritoriesRadius(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/update-radius", method = RequestMethod.POST)
	public Map<String, Object> updateTerritoryRadius(
			HttpServletRequest request, HttpServletResponse response,
			Integer id, Integer radius, Integer userId);

	@RequestMapping(value = "portal/ndn/get-finance-setup", method = RequestMethod.POST)
	public List<Map<String, Object>> getFinanceSetup(
			HttpServletRequest request, HttpServletResponse response,
			String type, String territoryId, String zoneId);

	@RequestMapping(value = "portal/ndn/update-finance-details", method = RequestMethod.POST)
	public Map<String, Object> updateFinanceDetails(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/sallery-month", method = RequestMethod.POST)
	public List<Map<String, Object>> getMonths(HttpServletRequest request,
			HttpServletResponse response, String month, Integer id);

	@RequestMapping(value = "portal/ndn/update_price", method = RequestMethod.POST)
	public List<Map<String, Object>> updateprice(HttpServletRequest request,
			HttpServletResponse response, Double f1, Double f2, int id,
			String date) throws Exception;

	@RequestMapping(value = "portal/ndn/get-focus-brands", method = RequestMethod.POST)
	public List<Map<String, Object>> getFocusBrands(HttpServletRequest request,
			HttpServletResponse response, Integer status, Integer brandId);

	@RequestMapping(value = "portal/ndn/insert-focus-brand", method = RequestMethod.POST)
	public Map<String, Object> insertFocusBrand(HttpServletRequest request,
			HttpServletResponse response, final Integer regionId,
			final Integer visits, final Integer sale, final Integer familyId,
			final String startDate, final String endDate, final Integer userId);

	@RequestMapping(value = "portal/ndn/update-focus-brand-status", method = RequestMethod.POST)
	public Map<String, Object> updateFocusBrandStatus(
			HttpServletRequest request, HttpServletResponse response,
			Integer id, String status);

	@RequestMapping(value = "portal/ndn/getAppUsers", method = RequestMethod.POST)
	public List<Map<String, Object>> getApplicationUsers(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "portal/ndn/insert-activity", method = RequestMethod.POST)
	public Map<String, Object> insertAcitivity(HttpServletRequest request,
			HttpServletResponse response, final String activityTitle,
			final String activityType, final Integer familyId,
			final String startDate, final String endDate, final Integer userId);

	@RequestMapping(value = "portal/ndn/get-activity", method = RequestMethod.POST)
	public List<Map<String, Object>> getActivityData(
			HttpServletRequest request, HttpServletResponse response,
			Integer status, Integer brandId);

	@RequestMapping(value = "portal/ndn/insert-de-activity", method = RequestMethod.POST)
	public Map<String, Object> insertAcitivityRoutes(
			HttpServletRequest request, HttpServletResponse response,
			final Integer deId, final Integer acitityId);

	@RequestMapping(value = "/ndn/uploadPrimarySaleTarget", method = RequestMethod.POST)
	public Map<String, Object> uploadPrimarySaleTargetOnPortal(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "ndn/getCreditNote", method = RequestMethod.POST)
	public List<Map<String, Object>> getCreditNote(HttpServletRequest request,
			HttpServletResponse response, String zoneId, String regionId,
			Integer month, Integer year);

	@RequestMapping(value = "portal/ndn/getFpiShops", method = RequestMethod.GET)
	public List<Map<String, Object>> getFpiShops(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = "/portal/ndn/forgotPassword", method = RequestMethod.POST)
	public Map<String, Object> getForgotLoginPassword(
			HttpServletRequest request, HttpServletResponse response,
			String userName, String uniqueKey, String type);

	@RequestMapping(value = "/portal/ndn/updatePasswordFromEmail", method = RequestMethod.POST)
	public Map<String, Object> updatePasswordNew(HttpServletRequest request,
			HttpServletResponse response, String newPassword, String uniqueKey,
			String type);

	@RequestMapping(value = "/portal/ndn/expiryTimeCheck", method = RequestMethod.POST)
	public Map<String, Object> expiryTime(HttpServletRequest request,
			HttpServletResponse response, String currentTime, String uniqueKey,
			String type);

	@RequestMapping(value = "/portal/ndn/finance-sheet-data", method = RequestMethod.POST)
	public List<Map<String, Object>> getFinanceSheeetData(
			HttpServletRequest request, HttpServletResponse response,
			String type, String territoryId, String zoneId);

	@RequestMapping(value = "/ndn/getUserType", method = RequestMethod.POST)
	public List<Map<String, Object>> getUserType(HttpServletRequest request,
			HttpServletResponse response, Integer userId);

	@RequestMapping(value = "/ndn/getCitiesOfRegions", method = RequestMethod.POST)
	public List<Map<String, Object>> getregionscities(
			HttpServletRequest request, HttpServletResponse response,
			String regions);

	@RequestMapping(value = "/ndn/getUserRegions", method = RequestMethod.POST)
	public List<Map<String, Object>> getUserRegions(HttpServletRequest request,
			HttpServletResponse response, String assingedRegions, Integer zoneId);

	@RequestMapping("/error401")
	public String handle401Error();

	@RequestMapping(value = "/ndn/primarySaleTargets_vs_Achievements", method = RequestMethod.POST)
	public void primaryVsAchievement(HttpServletRequest request,
			HttpServletResponse response, Integer month, Integer year);

	@RequestMapping(value = "/portal/ndn/updateGodownDetails", method = RequestMethod.POST)
	public Map<String, Object> updateGodown(HttpServletRequest request,
			HttpServletResponse response, String title, String address,
			int rent, String type, int regionId, int id, String isNew);
	
	@RequestMapping(value = "/portal/ndn/dsr-morning-attendance", method = RequestMethod.POST)
	public List<Map<String, Object>> dsrMorningImageData(Integer dsrId, String startDate,
			String endDate);

	@RequestMapping(value = "/loadFilters", method = RequestMethod.POST)
	public Map<String, Object> loadFilters(HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/dashboard-data-new", method = RequestMethod.POST)
	public Map<String, Object> dashboardDataNew(HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/downloadReport", method = RequestMethod.POST)
	public void downloadReport(HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/downloadcsvReport", method = RequestMethod.POST)
	public void downloadcsvReport(HttpServletRequest request, HttpServletResponse response);

}
