package com.marksman.census.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.marksman.census.bo.AdminProfile;
import com.marksman.census.bo.Area;
import com.marksman.census.bo.BrandAssignment;
import com.marksman.census.bo.BrandPrice;
import com.marksman.census.bo.BrandPriceDetails;
import com.marksman.census.bo.City;
import com.marksman.census.bo.CityStockAssignment;
import com.marksman.census.bo.ExportShop;
import com.marksman.census.bo.Factory;
import com.marksman.census.bo.Family;
import com.marksman.census.bo.Industry;
import com.marksman.census.bo.RateAssignment;
import com.marksman.census.bo.Region;
import com.marksman.census.bo.Shop;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.client.bo.Attendance;
import com.marksman.census.client.bo.ShopAssetImage;
import com.marksman.census.client.bo.StockTransfer;
import com.marksman.census.client.bo.TransferCodeDetails;
import com.marksman.census.portal.bo.DuplicateClusters;
import com.marksman.census.portal.bo.FinanceSetup;

public interface PortalService {

	public ModelAndView createLogin(String userName, String password,
			Integer typeId, HttpServletResponse response);

	public ModelAndView updatePassword(String newPassword,
			HttpServletResponse response);

	public ModelAndView loginPage();

	public ModelAndView logoutPage(HttpServletRequest request);

	public ModelAndView portalLogin(String userName);

	public ModelAndView loadPendingShops(int surveyorId, int categoryId);

	public ModelAndView loadShopsToAssign(int surveyorId);

	public void saveAssignedShops(Integer surveyorId, String shops,
			String merchandiserShopId);

	public ModelAndView basicScreening(int shopId, int merchandiserShopId,
			int surveyorId, String buildVersion, String shopCLosed);

	public ModelAndView loadBwuImages(int merchandiserShopId, int shopId,
			int industryId, int bwuId);

	public ModelAndView visibility(int shopId, int merchandiserShopId,
			int surveyorId, Integer categoryId, String buildVersion,
			String chillerType);

	public ModelAndView hotZone(Integer merchandiserShopId, Integer surveyorId,
			Integer shopId, Integer displayId, Integer displayNumber,
			String buildVersion, HttpServletRequest request,
			HttpServletResponse response);

	public ModelAndView primaryShelf(Integer merchandiserShopId,
			Integer surveyorId, Integer shopId, Integer displayId,
			Integer displayNumber, String buildVersion,
			HttpServletRequest request, HttpServletResponse response);

	public void validateShop(Integer shopId, Integer merchandiserShopId,
			String updatedValue, String oldValue, String validationAction,
			String validationStatus, Integer bwuId, Integer industryId,
			Integer surveyorId);

	public void validateShopCategories(Integer surveyorId, Integer shopId,
			Integer merchandiserShopId, Integer categoryId, Integer displayId,
			Integer displayNumber, String oldTotalFacings,
			String oldMndlzFacings, String oldGoldMndlzFacings,
			String newTotalFacings, String newMndlzFacings,
			String newGoldMndlzFacings, String validationAction,
			String validationStatus, HttpServletRequest request,
			HttpServletResponse response);

	public void updateDisplayAuditProductFacing(Integer surveyorId,
			Integer shopId, Integer merchandiserShopId, Integer categoryId,
			Integer productId, Integer displayId, Integer displayNumber,
			String oldFacing, String newFacing, String validationAction,
			String validationStatus, HttpServletRequest request,
			HttpServletResponse response);

	public void updateDisplayRemark(Integer surveyorId, Integer shopId,
			Integer merchandiserShopId, Integer categoryId, Integer displayId,
			Integer displayNumber, Integer displayRemark,
			HttpServletRequest request, HttpServletResponse response);

	public ModelAndView volumeCheck(int shopId, int merchandiserShopId,
			int surveyorId, String buildVersion);

	public ModelAndView locationValidation(Integer shopId,
			Integer merchandiserShopId, Integer surveyorId, String buildVersion);

	public Map<Integer, ExportShop> viewShopsTag(Integer zoneId,
			Integer regionId, Integer townId, Integer areaId, String startDate,
			String endDate);

	public List<DuplicateClusters> getDuplicateClusters(Integer zoneId,
			Integer regionId, Integer townId, Integer areaId);

	public ModelAndView shopsTag();

	public ModelAndView duplicateClusters();

	public ModelAndView duplicateShops(Integer zoneId, Integer regionId,
			Integer townId, Integer areaId, String longitude, String latitude);

	public ModelAndView getShopRemarks(Integer shopId, String requestType,
			Integer surveyorId, Integer merchandiserShopId, Integer remarkId);

	public ModelAndView getShopAssetRemarks(Integer shopId, String requestType,
			Integer surveyorId, Integer merchandiserShopId, Integer remarkId,
			Integer merchandiserAssetId);

	public void reEvaluateShop(Integer shopId);

	public List<Map<String, Object>> getMerchandiserListMap(String visitDate);

	public List<Map<String, Object>> getMerchandiserShopListMap(
			Integer surveyorId, String startDate, String endDate);

	public Map<String, Object> getSectionListMap(Integer merchandiserShopId,
			Integer userTypeId, String visitType, String surveyorType);

	public Map<String, Object> getAuthForPortal(HttpServletRequest request,
			HttpServletResponse response, String userName, String password);

	// By Muhammad Hamed
	public List<Map<String, Object>> getMerchandiserShopListNDN(
			Integer regionId, Integer zoneId, Integer cityId,
			Integer distributionId, String storeType, Integer channelId,
			String startDate, String endDate, Integer rteId,
			Integer surveyorId, String nonEvaluated, String zones,
			String regions);

	// By Muhammad Hamed
	public ArrayList<ShopAssetImage> getCompletedShopListNDN(Integer zoneId,
			Integer regionId, String startDate, String endDate,
			Integer merchandiserId, Integer remarksId, String visitType);

	// By Muhammad Hamed
	public ArrayList<ShopAssetImage> getCompletedVisitShopListNDN(
			Integer zoneId, Integer regionId, String startDate, String endDate,
			Integer merchandiserId, Integer remarksId, String visitType,
			String evaluated);

	// By Muhammad Hamed
	public List<Region> getRegions(Integer userId);

	// By Muhammad Hamed
	public List<Map<String, Object>> getDesAndDsrs(Integer regionId);

	// By Muhammad Hamed
	public List<Map<String, Object>> getDsrsDetails(Integer surveyorId,
			Integer regionId, String status);

	// By Muhammad Hamed
	public List<Map<String, Object>> getDsrAreasDetails(Integer dsrId,
			Integer regionId);

	// By Muhammad Hamed
	public Map<String, Object> updateDeName(Integer surveyorId, String deName,
			Integer userId, String deStatus, Integer employeeId,
			float basicSalary, float dailyAllownce, float fuelAllownce,
			float maintainanceAllownce, float otherAllowances);

	// By Muhammad Hamed
	public Map<String, Object> updateDsrDetails(Integer surveyorId,
			String dsrName, Integer dsrId, String dsrStatus, Integer userId,
			String dsrType, Integer dsrEmployee, float basicSalary,
			float dailyAllownce, float fuelAllownce,
			float maintainanceAllownce, float otherAllowances, float vanSalary);

	// By Muhammad Hamed
	public Map<String, Object> updateDsrAreasDetails(Integer dsrId,
			String blockName, Integer blockId, Integer userId,
			String blockStatus, String dsrType, Integer dsrVanId, Integer townId);

	// By Hamed
	public void addDsrFromPortal(Integer surveyorId, String dsrName,
			Integer userId, String dsrType, Integer dsrEmployee)
			throws Exception;

	// By Hamed
	public void addBlockFromPortal(String blockName, Integer townId,
			Integer dsrId, Integer userId, Integer dsrVanId,
			Integer exportedAreaId) throws Exception;

	// By Hamed
	public List<Map<String, Object>> getTowns(Integer regionId);

	// By Hamed
	public List<Map<String, Object>> getRegionsByZoneId(Integer zoneId);

	public List<Map<String, Object>> getRegionsByZoneIdNew(String zoneId);

	// By Hamed
	public void submitTransferedStock(StockTransfer stockTransfer)
			throws Exception;

	// By Hamed
	public void submitReceivedStock(StockTransfer stockTransfer)
			throws Exception;

	// By Hamed
	public List<Map<String, Object>> getImeisforPortal();

	// By Sajid
	public List<Map<String, Object>> getDsrSaleTargetforPortal(Integer month,
			Integer year, Integer regionId, Integer townId, Integer zoneId,
			Integer type);

	// By Hamed
	public Map<String, Object> updateImeiFromPortal(String imei,
			String imeiStatus);

	// By Hamed
	public List<Map<String, Object>> getFamiliesForPortal();

	// By Hamed
	public List<Map<String, Object>> getTerritoryFamiliesForPortal(
			Integer territoryId);

	// By Hamed
	public List<Map<String, Object>> getTownFamiliesForPortal(Integer townId);

	// By Hamed
	public List<Map<String, Object>> getTerritoryReceivedStock(
			String transferType, Integer territoryId, String transferCode);

	// By Hamed
	public List<Map<String, Object>> getTerritoryReceivedStockTransferCodes(
			String transferType, Integer territoryId);

	// By Hamed
	public Map<String, Object> checkTransferCodeAvailability(String transferCode);

	// By Hamed
	public List<Map<String, Object>> getReceivedStockCodes(
			HttpServletRequest request);

	// By Hamed
	public List<Map<String, Object>> getReceivedStockCodesForPortal(
			HttpServletRequest request);

	// By Hamed
	public TransferCodeDetails getTransitDetails(String transitCode);

	// By Hamed
	public List<Map<String, Object>> getReceivedStock(Integer transferId);

	// By hamed
	public List<Map<String, Object>> getFactoryStockFamilies(Integer factoryId);

	// M Awais
	public List<Map<String, Object>> getDistributionList(Integer regionId,
			Integer cityId);

	public Map<String, Object> updateDistribution(Integer distributionId,
			double distLat, double distLong, String distName, Integer totalDE,
			Integer totalDSR, Integer investment, String distType,
			Integer regionId, Integer cityId);

	public List<Map<String, Object>> getBlockWiseShopList(Integer blockId,
			Integer regionId, Integer surveyorId, Integer dsrId);

	public List<Map<String, Object>> getSurveyorList();

	// By Hamed
	public List<Factory> getFactoryList(String type);

	public List<Map<String, Object>> getDeAttendanceList(Integer regionId,
			String startDate, String endDate, String attendanceType,
			Integer zoneId);

	public List<Map<String, Object>> getRemarks(String typeId);

	public Map<String, Object> updateWorkType(Integer id, Integer workTypeId,
			Integer userId);

	// By Muhammad Hamed
	public List<Map<String, Object>> getTmProductivityNDN(Integer regionId,
			Integer zoneId, Integer cityId, Integer distributionId,
			String storeType, Integer channelId, String startDate,
			String endDate, Integer rteId, Integer surveyorId, String zones,
			String regions);

	public Map<String, Object> insertEvaluationScore(JSONObject json);

	// By Muhammad Hamed
	public List<Map<String, Object>> getDsrs(Integer surveyorId, String dsrType);

	public List<Map<String, Object>> getDsrsByRegionId(Integer regionId);

	public List<Map<String, Object>> getEmployeeList(Integer regionId);

	// Muhammad Hamed
	public Map<String, Object> transferBlocks(String blocks, Integer dsrId,
			Integer userId);

	public Map<String, Object> transferShops(String shops, Integer areaId,
			Integer userId);

	public List<Map<String, Object>> getQueryTypeList();

	public List<Map<String, Object>> getMerchandiserDsrSalesList(
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer familyId);

	public List<Map<String, Object>> getMerchandiserSpotSellingList(
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer familyId);

	public List<Map<String, Object>> getMerchandiserDsrShopSalesList(
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, Integer familyId,
			String promotionType, String schemeTitle);

	public Map<String, Object> updateDsrSales(Integer dsrSaleId,
			float retailValue, float wholeSaleValue, Integer userId);

	public Map<String, Object> updateSpotSellingValue(Integer spotsellingId,
			float value, Integer userId);

	public Map<String, Object> updateDsrShopSales(Integer dsrSaleId,
			float saleValue, Integer userId, String promoType,
			float discountValue);

	// by muhammad hamed
	public List<TransferCodeDetails> getTransactionsData(String startDate,
			String endDate, String transferType);

	// by muhammad hamed
	public List<Map<String, Object>> getTransactionStockDetails(
			Integer transferId);

	// by muhammad hamed

	public List<Surveyor> getDeListByRegionId(Integer regionId, Integer zoneId,
			String surveyorType);

	public Map<String, Object> saveDSRSale(String date, Integer surveyorId,
			Integer dsrId, Integer familyId, float retailValue,
			float wholeSaleValue, Integer cityId);

	// by muhammad hamed
	public void reverseTransaction(Integer transactionId) throws Exception;

	public List<Map<String, Object>> getDSRsByDEId(Integer deId);

	// created by muhammad hamed
	public List<Attendance> getUsersForAttendanceApprovals(String userType,
			String selectedDate, Integer zoneId, Integer regionId);

	// created by muhammad hamed
	public void UpdateAttendanceForApprovals(
			ArrayList<Attendance> approvedAttendance, String selectedDate,
			Integer surveyorId) throws Exception;

	// created by muhammad hamed
	public List<Map<String, Object>> getShopsForDETracking(Integer zoneId,
			Integer regionId, Integer surveyorId, Integer dsrId,
			String selectedDate, String mapViewType, String startDate,
			String endDate, String spentTime);

	// created by muhammad hamed
	public List<Surveyor> getDeListByRegionIdForDETracking(Integer zoneId,
			Integer regionId, String mapViewType, String selectedDate);

	// created by muhammad hamed
	public List<Map<String, Object>> getDsrListBySurveyorIdForDeTracking(
			Integer surveyorId, String mapViewType, String selectedDate);

	// created by muhammad hamed
	public Map<String, Object> insertCityFromPortal(Integer regionId,
			String cityName, Integer cityId);

	// created by muhammad hamed
	public Map<String, Object> updateCityFromPortal(Integer cityId,
			String cityName);

	public AdminProfile findUserById(Integer userId);

	public List<Map<String, Object>> getDeListByRegionIdNew(Integer zoneId,
			String regionId, String surveyorType, String status);

	// created by muhammad hamed
	public List<Map<String, Object>> getEmployeeList(String surveyorType,
			String mustHave);

	// created by muhammad hamed
	public Map<String, Object> insertEmployeeFromPortal(
			HttpServletRequest request, HttpServletResponse response);

	public List<Map<String, Object>> getAvailableEmployeeListForDE(
			String surveyorType);

	public void insertDeFromPortal(String regionId, Integer employeeId,
			String deCode, String employeeType, Integer userId,
			float basicSalary, float dailyAllownce, float fuelAllownce,
			Float maintenanceAllownce, Float otherAllowance) throws Exception;

	public List<Map<String, Object>> getSecondarySale(Integer regionId,
			Integer cityId, Integer brandId, Integer month, Integer year);

	public List<Map<String, Object>> getPrimarySale(Integer regionId,
			Integer cityId, Integer brandId, Integer month, Integer year);

	public Map<String, Object> updatePasswordFromPortal(String oldPassword,
			String newPassword, Integer userId);

	public ArrayList<AdminProfile> getUsersDataForPasswordUpdation(
			Integer userTypeId);

	public Map<String, Object> updatePasswordByAdmin(String newPassword,
			Integer userId, String userType);

	public List<Map<String, Object>> stockTransfersTypes();

	public List<Map<String, Object>> getAdminRole();

	public Map<String, Object> getTableauTicket(HttpServletRequest request,
			HttpServletResponse response, String type);

	public Map<String, Object> saveUserRole(String title,
			HttpServletResponse response);

	public Map<String, Object> updateUserRoleStatus(Integer roleId,
			String status, HttpServletResponse response);

	public Map<String, Object> getMenuList(Integer roleId);

	public Map<String, Object> updateMenuList(String menus, Integer roleId,
			Integer act);

	public List<Map<String, Object>> sisSurveyorList(String startDate,
			String endDate, Integer zoneId, Integer regionId, Integer cityId);

	public Map<String, Object> updateStock(HttpServletRequest request,
			HttpServletResponse response, String stockType, String projectName);

	public List<Map<String, Object>> dsrSurveyorList(String startDate,
			String endDate, Integer zoneId, Integer regionId, Integer cityId,
			String zones, String regions);

	public ArrayList<ShopAssetImage> dsrShopList(Integer zoneId,
			Integer regionId, Integer cityId, String startDate, String endDate,
			Integer surveyorId, String visitType, String newShop);

	public void updateSurveyorRegion(String regionId, Integer surveyorId)
			throws Exception;

	public List<Industry> getIndustryList();

	public List<Family> getBrandList(Integer industryId);

	public void createBrand(Integer industryId, String brandTitle,
			Integer brandSticks) throws Exception;

	public void createRegion(Integer zoneId, String regionTitle)
			throws Exception;

	public void updateSurveyorStatus(String status, Integer surveyorId,
			String surveyorType, Integer dsrId) throws Exception;

	public void deleteSaleTarget(Integer targetId) throws Exception;

	public List<Map<String, Object>> getWSTKSSaleTarget(Integer month,
			Integer year, Integer regionId, Integer townId, Integer zoneId,
			String shopType);

	void deleteWSTKSSaleTarget(Integer targetId) throws Exception;

	public List<Map<String, Object>> getMerchandiserWSTKSSalesList(
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, String type, Integer shopId,
			Integer familyId);

	public Map<String, Object> updateWSTKSSales(Integer surveyId, int familyId,
			float wholeSaleValue, String type, Integer remarksId,
			String updateType, int userId);

	public List<Map<String, Object>> getMerchandiserWSTKSShopList(
			String startDate, String endDate, Integer zoneId, Integer regionId,
			Integer surveyorId, Integer dsrId, String type, Integer shopId);

	public Map<String, Object> addNewWSTKSSale(Integer surveyorId,
			Integer userId, String type, Integer shopId, String visitDate,
			Integer dsrId, Integer employeeId, Integer familyId, float value,
			String visitDateTime, Integer remark);

	public List<Shop> getRAShopList(Integer regionId);

	public void updateSurveyorEmloyee(Integer surveyorId, Integer employeeId,
			Integer userId, float basicSalary, float dailyAllownce,
			float fuelAllownce, Float maintenanceAllownce,
			Float otherAllowance, Integer cityId) throws Exception;

	public List<Map<String, Object>> getAreasByRegionId(Integer regionId);

	public void createUserFromPortal(String username, String password,
			Integer role, String zone, String region, String area)
			throws Exception;

	public List<Map<String, Object>> getDsrListForAttendance(
			Integer surveyorId, String visitDate, Integer territoryId);

	public Map<String, Object> updateDsrAttendance(Integer id,
			Integer workTypeId, Integer userId, Integer surveyorId,
			String visitDate);

	public List<Map<String, Object>> getWorkingDays(Integer userId,
			Integer month, Integer year);

	public Map<String, Object> updateWorkingDays(Integer userId, Integer month,
			Integer year, String value, String date);

	public List<Industry> getSisIndustryList();

	public void createSisBrand(Integer industryId, String brandTitle,
			Integer userId) throws Exception;

	public List<Family> getSisBrandList(Integer industryId);

	public List<Map<String, Object>> getNewSisBrands(Integer industryId,
			Integer zoneId, Integer regionId, Integer cityId,
			Integer surveyorId, String startDate, String endDate);

	public Map<String, Object> updateBrands(String newBrandId,
			Integer systemBrandId, Integer userId);

	public List<Map<String, Object>> getShopProfile();

	public List<Map<String, Object>> getSisSurveyor(Integer regionId,
			Integer typeId);

	public void updateTownStatus(String status, Integer cityId, String userId)
			throws Exception;

	public void updateSisOldBrands(Integer industryId, Integer familyId)
			throws Exception;

	public List<Map<String, Object>> getSisAttendanceList(String startDate,
			String endDate, Integer surveyorId, Integer typeId);

	public List<Map<String, Object>> getAreasByTownId(Integer townId);

	public Map<String, Object> insertAreaFromPortal(Integer townId,
			String areaName, Integer userId);

	public List<Map<String, Object>> getShopsForSisTracking(Integer zoneId,
			Integer regionId, Integer cityId, Integer surveyorId,
			String selectedDate, String mapViewType, String startDate,
			String endDate, String shopType);

	public List<Surveyor> getSisListByRegionIdForSurveyorTracking(
			Integer zoneId, Integer regionId, String mapViewType,
			String selectedDate, String surveyorType);

	public List<Map<String, Object>> getSchemes(Integer cityId, Integer shopId);

	public List<Map<String, Object>> getOverallSchemes(String promoType,
			Integer brandId, String status, String type);

	public List<Map<String, Object>> getFocusBrands(Integer status,
			Integer brandId);

	public Map<String, Object> updateSchemeTagging(String schemeId,
			Integer townId, Integer shopId, Integer userId);

	public Map<String, Object> insertNewSchemeFromPortal(String schemeTitle,
			Integer familyId, String promotionType, String schemeType,
			Integer minimumQuantity, float discountQunatity,
			float discountPercent, float discountValue, float stockAssignment,
			String startDate, String endDate, boolean isCreate, Integer userId,
			String type, float productiveShop, float minimumSale,
			String shopProfile, float schemeDuration,
			float productiveBrandFrom, float productiveBrandTo,
			float maximumBudget, final String calculatioType,
			final Integer upperLimit);

	public void updateTownSchemeStatus(String status, Integer schemeId)
			throws Exception;

	public List<String> getPromoAssignedList(Integer promoId);

	public Map<String, Object> assignPromo(Integer promoId, String cities,
			float schemeStock, int familyId, String type);

	public List<Map<String, Object>> getEditReceivedStock(
			HttpServletRequest request, String receivedDate, Integer userId,
			String type);

	public List<Map<String, Object>> getAdjustmentStock(Integer transferId);

	public void submitAdjustedStock(StockTransfer stockTransfer)
			throws Exception;

	public Map<String, Object> getRouteTagging(Integer dsrId, Integer type);

	public Map<String, Object> assignDsrRoutes(Integer dsrId,
			String routesList, Integer type);

	public List<Map<String, Object>> getStockSchemeCity(Integer promoId,
			Float schemeStock, Float consumedStock);

	public void assignStockCityScheme(CityStockAssignment cityStockAssignment)
			throws Exception;

	public List<String> getBrandAssignedList(Integer familyId);

	public void assignBrandList(BrandAssignment brandAssignment)
			throws Exception;

	public List<Map<String, Object>> getBrandList();

	public List<Map<String, Object>> getAllBrandList(Integer industryId,
			String type);

	public List<Map<String, Object>> getFamilySetList();

	public void insertDsrIntoEmployee(String dsrName) throws Exception;

	public List<Map<String, Object>> getDsrEmployeeList();

	public void updateDsrEmloyee(Integer dsrId, Integer employeeId,
			Integer userId, float basicSalary, float dailyAllownce,
			float fuelAllownce, float maintainanceAllownce,
			float otherAllowance, Integer cityId, String registrationNo,
			String makeModel, String vanType, String dsrType) throws Exception;

	public void insertDsrFromPortal(Integer employeeId, String deCode,
			String employeeType, Integer selectedDE, Integer userId,
			Integer rateList, float basicSalary, float dailyAllownce,
			float fuelAllownce, Float maintenanceAllownce,
			Float otherAllowance, Float vanSalary, String registrationNo,
			String makeModel, String vanType) throws Exception;

	public List<Map<String, Object>> getCities(Integer regionId);

	public List<Map<String, Object>> getCitiesNew(String regionId);

	public List<Map<String, Object>> getDistinctSchemes();

	public void deleteDsrDayEnd(Integer dsrId) throws Exception;

	public Map<String, Object> updateEmployeeName(String employeeName,
			long phone, long cnic, Integer basicSalary, Integer dailyAllownce,
			Integer fuelAllownce, Integer maintenanceAllownce,
			Integer employeeId, Integer userId);

	public void updateEmpStatus(String status, Integer employeeId)
			throws Exception;

	public void updateStatus(String Active, Integer Id, Integer userId)
			throws Exception;

	public void updateDate(String inactiveDate, Integer employeeId)
			throws Exception;

	public void updateschemeDate(Integer Id, String Active,
			String inactiveDate, Integer userId) throws Exception;

	public List<Map<String, Object>> getBrandPriceDetails(Integer brandId,
			Integer brandPriceId) throws Exception;

	public int insertBrandPrice(BrandPrice brandPrice) throws Exception;

	public void updateBrandPriceDetails(BrandPriceDetails brandPriceDetail)
			throws Exception;

	public List<Map<String, Object>> getBrandPrices() throws Exception;

	public int insertBrandPriceDetail(BrandPriceDetails brandPriceDetail)
			throws Exception;

	public List<String> getBrandRateAssignedList(Integer brandPriceId);

	public void assignRateList(RateAssignment rateAssignment) throws Exception;

	public void deleteRateList(Integer brandPriceId, Integer userId)
			throws Exception;

	public void importShopsFromBlock(Integer exportAreaId, Integer importAreaId)
			throws Exception;

	public void blockTransfer(String blockName, Integer blockId,
			Integer townId, Integer dsrId, Integer userId) throws Exception;

	public void updateBrandStatus(String status, Integer brandId)
			throws Exception;

	public List<Map<String, Object>> getTerritoriesRadius();

	public void updateTerritoryRadius(Integer id, Integer radius, Integer userId)
			throws Exception;

	public List<Map<String, Object>> getFinanceSetup(String type,
			String territoryId, String regionId) throws Exception;

	public int updateFinanceDetails(FinanceSetup financeSetup, String type)
			throws Exception;

	public List<Map<String, Object>> getMonths(String month, Integer id)
			throws Exception;

	public List<Map<String, Object>> updateprice(Double f1, Double f2, int id,
			String date);

	public Map<String, Object> insertFocusBrand(final Integer regionId,
			final Integer visits, final Integer sale, final Integer familyId,
			final String startDate, final String endDate, final Integer userId);

	public void updateFocusBrandStatus(String status, Integer id)
			throws Exception;

	public List<Map<String, Object>> getApplicationUsers();

	public Map<String, Object> insertAcitivity(final String activityTitle,
			final String activityType, final Integer familyId,
			final String startDate, final String endDate, final Integer userId);

	public List<Map<String, Object>> getActivityData(Integer status,
			Integer brandId);

	public Map<String, Object> insertAcitivityRoutes(final Integer deId,
			final Integer activityId);

	public List<Map<String, Object>> getCreditNote(String zoneId,
			String regionId, Integer month, Integer year);

	public void getForgotPassword(String uerName, String uniqueKey, String type)
			throws Exception;

	public Map<String, Object> updatePasswordFromEmail(String newPassword,
			String userName, String type);

	public boolean getexpiryTime(String currentTime, String uniqueKey,
			String type) throws Exception;

	public List<Map<String, Object>> getFinanceSheeetData(String type,
			String territoryId, String zoneId);

	public List<Map<String, Object>> getUserType(HttpServletRequest request,
			HttpServletResponse response, Integer userId);

	public List<Map<String, Object>> getregionscities(
			HttpServletRequest request, HttpServletResponse response,
			String regions);

	public List<Map<String, Object>> getUserRegions(String assingedRegions,
			Integer zoneId);

	public Boolean validateRequest(String UserId, String fullUrl, String type);

	public void primaryVsAchievement(HttpServletRequest request,
			HttpServletResponse response, Integer month, Integer year);

	public Map<String, Object> updateGodown(String title, String address,
			int rent, String type, int regionId, int id, String isNew);

	public List<Map<String, Object>> tpsomData(Integer dsrId, String startDate,
			String endDate);
	
	public List<Map<String, Object>> dsrMorningImageData(Integer dsrId, String startDate,
			String endDate);

	public Map<String, Object> LoadFilters(HttpServletRequest request, HttpServletResponse response) throws IOException;

	public Map<String, Object> dashboardDataNew(HttpServletRequest request, HttpServletResponse response);

	public void downloadReport(HttpServletRequest request, HttpServletResponse response);

	public void downloadcsvReport(HttpServletRequest request, HttpServletResponse response);
}
