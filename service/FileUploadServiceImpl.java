package com.marksman.census.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.marksman.census.bo.Area;
import com.marksman.census.bo.City;
import com.marksman.census.bo.Dsr;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.bo.Tso;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.client.bo.Distribution;
import com.marksman.census.constants.XlsxConstants;
import com.marksman.census.dao.AreaDao;
import com.marksman.census.dao.ChannelsDao;
import com.marksman.census.dao.CitiesDao;
import com.marksman.census.dao.DistributionDao;
import com.marksman.census.dao.RegionsDao;
import com.marksman.census.dao.ShopGroupsDao;
import com.marksman.census.dao.ShopsDao;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.dao.TsoDao;
import com.marksman.census.portal.bo.UploadShop;
import com.marksman.census.transformer.XlxsTransformer;
import com.marksman.census.util.StringUtils;
import com.marksman.census.util.XlsxUtilities;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.hssf.usermodel.HSSFCell;

import com.marksman.census.service.InvalidHeadingException;
import com.marksman.census.service.CustomException;

public class FileUploadServiceImpl implements FileUploadService {
	@Autowired
	ServletContext servletContext;
	@Autowired
	private SurveyorDao surveyorDao;
	@Autowired
	private ShopsDao shopsDao;
	@Autowired
	private RegionsDao regionsDao;
	@Autowired
	private CitiesDao citiesDao;
	@Autowired
	private ShopGroupsDao groupsDao;
	@Autowired
	private AreaDao areaDao;
	@Autowired
	private ChannelsDao channelsDao;
	@Autowired
	private DistributionDao distributionDao;
	@Autowired
	private TsoDao tsoDao;

	@Autowired
	private CacheService cacheService;

	@Autowired
	XlxsTransformer xlxsTransformer;
	@Autowired
	ApplicationCacheService applicationCacheService;

	protected Logger logger = Logger.getLogger(this.getClass());
	private List<String> shopfileColumns = Arrays.asList("", "", "", "",
			"channel", "distribution", "subDistribution", "rsmName", "amName",
			"tsoName", "dsrName", "dsrCode", "shopCode", "shopTitle",
			"address", "shopPhone", "shopArea", "landMark",
			"isChillerALlocated", "assetType1", "assetType2", "assetType3");

	@Override
	public String uploadExcelFile(String fileName, int regionId, String path)
			throws Exception {

		String message = "";
		try {

			logger.info("Uploading excel file : " + fileName);
			message = this.readExcelFile(path, regionId);
			logger.info("Completed uploading excel file : " + fileName);

		} catch (Exception ex) {

			logger.error("Error occurred while saving file " + fileName, ex);
			throw ex;
		}
		return message;
	}

	@Override
	public String updateExcelFile(String fileName, int regionId, String path)
			throws Exception {

		String message = "";
		try {

			logger.info("Uploading excel file : " + fileName);
			message = this.readUpdateExcelFile(path, regionId);
			logger.info("Completed uploading excel file : " + fileName);

		} catch (Exception ex) {

			logger.error("Error occurred while saving file " + fileName, ex);
			throw ex;
		}
		return message;
	}

	@Override
	public String readExcelFile(String path, int regionId) {
		StringBuilder message = new StringBuilder();
		XSSFWorkbook myWorkBook = null;
		try {
			File file = new File(path);

			logger.info("Reading in workbook Started");
			myWorkBook = new XSSFWorkbook(FileUtils.openInputStream(file));
			logger.info("Reading in workbook Finish");

			// Region region = applicationCacheService.getRegionById(regionId);
			int numberOfSheets = myWorkBook.getNumberOfSheets();
			int count = 0;
			for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
				XSSFSheet mySheet = myWorkBook.getSheetAt(sheetIndex);
				Surveyor surveyor = surveyorDao.getSurveyorByCode(myWorkBook
						.getSheetName(sheetIndex).trim());
				if (surveyor == null || surveyor.getId() <= 0) {
					message.append(": Sheet No : "
							+ sheetIndex
							+ "Error Surveyor Not Found in System. This sheet  will not be uploaded in system"
							+ "\n");
					break;
				}

				Iterator<Row> rowIter = mySheet.rowIterator();
				while (rowIter.hasNext()) {
					XSSFRow myRow = (XSSFRow) rowIter.next();
					String title = XlsxUtilities.getStringCellValue(myRow
							.getCell(0));

					if (title.equalsIgnoreCase("S.No")
							|| title.trim().equals("")) {
						continue;
					}

					UploadShop uploadShop = xlxsTransformer
							.makeUploadShopObject(myRow, shopfileColumns);
					message.append(XlsxUtilities.validateRowValues(myRow,
							uploadShop));

					Area area = applicationCacheService
							.getAreasByName(uploadShop.getShopArea().trim());

					if (area == null || area.getId() <= 0) {

						area = new Area();
						area.setTitle(uploadShop.getShopArea());
						area.setRegion(applicationCacheService
								.getRegionById(regionId));
						area.setAsmCode(uploadShop.getAreaCode());
						area.setAsmName(uploadShop.getAreaManager());
						area = areaDao.insertArea(area);
						// Updating area cache
						cacheService.buildAreaCache();
					}
					/*
					 * Channel channel =
					 * applicationCacheService.getChannelByTitle
					 * (uploadShop.getChannel()); if(channel == null ||
					 * channel.getId() <= 0) {
					 * message.append("SHEET NUMBER  : "+
					 * sheetIndex+":  Row no : " + myRow.getRowNum() +
					 * "Error Channel Not Found in System. This shop will not be uploaded in system"
					 * + uploadShop.getShopTitle()+"      " +
					 * uploadShop.getShopCode() + "\n"); break; }
					 * uploadShop.setChannelId(channel.getId());
					 */
					Distribution distribution = applicationCacheService
							.getDistributionByName(uploadShop.getDistribution());
					if (distribution == null || distribution.getId() <= 0) {

						distribution = new Distribution();
						distribution.setTitle(uploadShop.getDistribution());
						distribution.setRegionId(regionId);
						distribution = distributionDao
								.insertDistribution(distribution);
						// Updating Distribution cache
						cacheService.buildDistributionCache();
					}
					uploadShop.setDistributionId(distribution.getId());
					Tso tso = applicationCacheService.getTsoByTitle(uploadShop
							.getTsoName());
					if (tso == null || tso.getId() <= 0) {

						tso = new Tso();
						tso.setTitle(uploadShop.getTsoName());
						tso.setDisributionId(uploadShop.getDistributionId());
						tso = tsoDao.insertTso(tso);
						// Updating TSO cache
						cacheService.buildTsoCache();
					}
					uploadShop.setTsoId(tso.getId());
					Dsr dsr = applicationCacheService.getDsrByCode(uploadShop
							.getDsrCode());
					if (dsr == null || dsr.getId() <= 0) {

						dsr = new Dsr();
						dsr.setFullName(uploadShop.getDsrName());
						dsr.setCode(uploadShop.getDsrCode());
						dsr.setSurveyor(surveyor);
						dsr = surveyorDao.insertSurveyorDsr(dsr);
						surveyorDao.insertSurveyorDsrs(dsr.getId(),
								surveyor.getId());
						cacheService.buildDsrCache();
					}

					/*
					 * City city =
					 * applicationCacheService.getCityByName(uploadShop
					 * .getCityName());
					 * 
					 * if (city == null) { message.append(": Row no : " +
					 * myRow.getRowNum() +
					 * "Error Town Not Found in System. This shop will not be uploaded in system"
					 * + uploadShop.getShopTitle()+"      " +
					 * uploadShop.getShopCode() + "\n"); continue; }
					 */

					uploadShop.setSurveyorId(surveyor.getId());
					uploadShop.setDsrId(dsr.getId());
					uploadShop.setAreaId(area.getId());

					if (shopsDao.isDuplicateShop(uploadShop.getShopCode(),
							uploadShop.getShopTitle(), uploadShop.getAreaId(),
							uploadShop.getDistributionId())) {
						count++;

						/*
						 * message.append(": Row no:" + myRow.getRowNum() +
						 * " error Duplicate Shop" + uploadShop.getShopTitle() +
						 * uploadShop.getShopCode() + "\n");
						 */
					} else {
						if (shopsDao.insertShop(uploadShop) == -1) {

							message.append(": Row no:" + myRow.getRowNum()
									+ " error in  Shop insertion"
									+ uploadShop.getShopTitle()
									+ uploadShop.getShopCode() + "\n");
							break;
						}
					}
				}
			}
			message.append(count);
			myWorkBook.close();
		} catch (Exception e) {

			logger.error(e, e);
		}

		return message.toString();

	}

	@Override
	public String readUpdateExcelFile(String path, int regionId) {
		StringBuilder message = new StringBuilder();
		XSSFWorkbook myWorkBook = null;
		try {
			File file = new File(path);

			logger.info("Reading in workbook Started");
			myWorkBook = new XSSFWorkbook(FileUtils.openInputStream(file));
			logger.info("Reading in workbook Finish");

			int numberOfSheets = myWorkBook.getNumberOfSheets();

			for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
				XSSFSheet mySheet = myWorkBook.getSheetAt(sheetIndex);

				Iterator<Row> rowIter = mySheet.rowIterator();
				while (rowIter.hasNext()) {
					XSSFRow myRow = (XSSFRow) rowIter.next();
					String title = XlsxUtilities.getStringCellValue(myRow
							.getCell(0));

					if (title.equalsIgnoreCase("ID") || title.trim().equals("")) {
						continue;
					}

					String shopCode = XlsxUtilities.getStringCellValue(
							myRow.getCell(4)).trim();
					String shopsName = XlsxUtilities.getStringCellValue(
							myRow.getCell(7)).trim();
					String cityName = XlsxUtilities.getStringCellValue(
							myRow.getCell(3)).trim();
					String bussinessType = XlsxUtilities.getStringCellValue(
							myRow.getCell(17)).trim();

					int categoryId = 1;
					if ("WS".equalsIgnoreCase(bussinessType)
							|| XlsxConstants.WHOLESALER
									.equalsIgnoreCase(bussinessType)
							|| "wholesale".equalsIgnoreCase(bussinessType)) {
						categoryId = 2;
					} else {
						categoryId = 1;
					}

					City city = applicationCacheService.getCityByName(cityName);

					if (city == null) {
						message.append(": Row no : "
								+ myRow.getRowNum()
								+ "Error Town Not Found in System. This shop will not be uploaded in system"
								+ shopsName + shopCode + "\n");
						continue;
					}

					int shopId = shopsDao.findShop(shopCode, shopsName,
							city.getId());

					if (shopId > 0) {
						shopsDao.updateShopCategory(shopId, categoryId);
					} else {
						message.append(": Row no : "
								+ myRow.getRowNum()
								+ "Error Shop Not Found in System. This shop will not be uploaded in system"
								+ shopsName + shopCode + "\n");
					}
				}
			}

			myWorkBook.close();
		} catch (Exception e) {
			logger.error(e, e);
		}

		return message.toString();
	}

	// By Hamed
	@Override
	public List<String> readExcelFileForIMES(String path) {
		StringBuilder message = new StringBuilder();
		XSSFWorkbook myWorkBook = null;
		List<String> list = new ArrayList<String>();
		try {
			File file = new File(path);

			logger.info("Reading in workbook Started");
			myWorkBook = new XSSFWorkbook(FileUtils.openInputStream(file));
			logger.info("Reading in workbook Finish");

			// Region region = applicationCacheService.getRegionById(regionId);
			int numberOfSheets = myWorkBook.getNumberOfSheets();
			int count = 0;
			for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
				XSSFSheet mySheet = myWorkBook.getSheetAt(sheetIndex);
				Iterator<Row> rowIter = mySheet.rowIterator();
				while (rowIter.hasNext()) {
					XSSFRow myRow = (XSSFRow) rowIter.next();
					String imei = XlsxUtilities.getStringCellValue(myRow
							.getCell(0));
					list.add(imei);
				}
			}
			message.append(count);
			myWorkBook.close();
		} catch (Exception e) {

			logger.error(e, e);
		}

		return list;

	}

	@SuppressWarnings("resource")
	public Map<Integer, Map<String, String>> readExcelFileForDsrSaleTarget(
			String path ,ArrayList<String> validatorArray) throws Exception {
		StringBuilder message = new StringBuilder();
		XSSFWorkbook myWorkBook = null;
		Map<Integer, Map<String, String>> allDataMap = new HashMap<Integer, Map<String, String>>();
		Map<String, String> columnMap = null;
		int rowNumber = 0;
		File file = new File(path);
		//validatorArray = loadValidHeadingsList();
		logger.info("Reading in workbook Started");
		myWorkBook = new XSSFWorkbook(FileUtils.openInputStream(file));
		logger.debug("book"+myWorkBook);
		logger.info("Reading in workbook Finish");

		int numberOfSheets = myWorkBook.getNumberOfSheets();
		int count = 0;
		for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
			XSSFSheet mySheet = myWorkBook.getSheetAt(sheetIndex);
		//	ArrayList<String> headings = new ArrayList<String>();
			Iterator<?> rowIter = mySheet.rowIterator();
			Vector<XSSFCell> cellStoreVector = new Vector<XSSFCell>();
			Vector<String> cellVector = new Vector<String>();
			while (rowIter.hasNext()) {
				XSSFRow myRow = (XSSFRow) rowIter.next();
				Iterator<?> cellIter = myRow.cellIterator();
				DataFormatter formatter = new DataFormatter();
				columnMap = new HashMap<String, String>();
				if (myRow.getRowNum() == 0) {
					
					while (cellIter.hasNext())
					{
						XSSFCell myCell = (XSSFCell) cellIter.next();
						String cellValue = formatter.formatCellValue(myCell);
						cellVector.addElement(cellValue);
						cellStoreVector.addElement(myCell);
					}

					checkHeadings(cellVector, myRow);
					continue;
					
				}
				rowNumber = myRow.getRowNum();
				logger.debug("row:" + rowNumber);
				rowNumber++;
                int sr = Double.valueOf(
                        XlsxUtilities.getStringCellValue(myRow.getCell(cellVector.indexOf("Sr,"))))
                        .intValue();
                String regionId =getCellValue(
    					myRow.getCell(cellVector.indexOf("region_id")),rowNumber, cellStoreVector.elementAt(cellVector.indexOf("region_id")).toString()).trim();
    			
    				columnMap.put("regionId", regionId);
    			String region =getCellValue(
        					myRow.getCell(cellVector.indexOf("region")),rowNumber, cellStoreVector.elementAt(cellVector.indexOf("region")).toString()).trim();
        			
        				columnMap.put("region", region);
        		String territoryId =getCellValue(
            					myRow.getCell(cellVector.indexOf("territory_id")),rowNumber, cellStoreVector.elementAt(cellVector.indexOf("territory_id")).toString()).trim();
            			
            				columnMap.put("territoryId", territoryId);
            				
            	String territory =getCellValue(
                					myRow.getCell(cellVector.indexOf("territory")),rowNumber, cellStoreVector.elementAt(cellVector.indexOf("territory")).toString()).trim();
                			
                				columnMap.put("territory", territory);		
               
    				
				String cityId =getCellValue(
					myRow.getCell(cellVector.indexOf("town_id")),rowNumber, cellStoreVector.elementAt(cellVector.indexOf("town_id")).toString()).trim();
			
				columnMap.put("cityId", cityId);
				
				 String town =getCellValue(
     					myRow.getCell(cellVector.indexOf("town")),rowNumber, cellStoreVector.elementAt(cellVector.indexOf("town")).toString()).trim();
     			
     				columnMap.put("town", town);	
				
				

				String employeeId = getCellValue(
						myRow.getCell(cellVector.indexOf("employee_id")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("employee_id")).toString()).trim();

				columnMap.put("employeeId", employeeId);

				String deId = getCellValue(
						myRow.getCell(cellVector.indexOf("DE_id")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("DE_id")).toString()).trim();
				columnMap.put("deId", deId);
				
				String deCode = getCellValue(
						myRow.getCell(cellVector.indexOf("DE_Code")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("DE_Code")).toString()).trim();
				columnMap.put("deCode", deCode);
				String deName = getCellValue(
						myRow.getCell(cellVector.indexOf("DE_Name")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("DE_Name")).toString()).trim();
				columnMap.put("deName", deName);

				String dsrId = getCellValue(
						myRow.getCell(cellVector.indexOf("DSR_ID")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("DSR_ID")).toString()).trim();
				columnMap.put("dsrId", dsrId);
				String dsrName = getCellValue(
						myRow.getCell(cellVector.indexOf("DSR_Name")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("DSR_Name")).toString()).trim();
				columnMap.put("dsrName", dsrName);
				String familyId = getCellValue(
						myRow.getCell(cellVector.indexOf("Brand_ID")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("Brand_ID")).toString()).trim();
				columnMap.put("familyId", familyId);
				String familyName = getCellValue(
						myRow.getCell(cellVector.indexOf("Brand_name")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("Brand_name")).toString()).trim();
				columnMap.put("familyName", familyName);
				String wholesaleTarget =getCellValue(
						myRow.getCell(cellVector.indexOf("target_wholesale")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("target_wholesale")).toString()).trim();
				columnMap.put("wholesaleTarget", wholesaleTarget);

				String retailTarget = getCellValue(
						myRow.getCell(cellVector.indexOf("target_retail")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("target_retail")).toString()).trim();
				columnMap.put("retailTarget", retailTarget);

				String year =getCellValue(
						myRow.getCell(cellVector.indexOf("target_year")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("target_year")).toString()).trim();
				columnMap.put("year", year);

				String month = getCellValue(
						myRow.getCell(cellVector.indexOf("target_month")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("target_month")).toString()).trim();
				columnMap.put("month", month);
				String dsrCode = getCellValue(
						myRow.getCell(cellVector.indexOf("dsr_code")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("dsr_code")).toString()).trim();
				columnMap.put("dsrCode", dsrCode);
				String dsremployeeId = getCellValue(
						myRow.getCell(cellVector.indexOf("dsr_employee_id")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("dsr_employee_id")).toString()).trim();
				columnMap.put("dsremployeeid", dsremployeeId);
				String dsrtype = getCellValue(
						myRow.getCell(cellVector.indexOf("dsr_type")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("dsr_type")).toString()).trim();
				columnMap.put("dsrtype", dsrtype);
				
					allDataMap.put(sr, columnMap);
				

			}
		}
		message.append(count);
		myWorkBook.close();

		return allDataMap;
	}
	
	@SuppressWarnings("resource")
	public Map<Integer, Map<String, String>> readExcelFileForPrimarySaleTarget(
			String path, ArrayList<String> validatorArray) throws Exception{
		StringBuilder message = new StringBuilder();
		XSSFWorkbook myWorkBook = null;
		Map<Integer, Map<String, String>> allDataMap = new HashMap<Integer, Map<String, String>>();
		Map<String, String> columnMap = null;
		int rowNumber = 0;
		File file = new File(path);
		//validatorArray = loadValidHeadingsList();
		logger.info("Reading in workbook Started");
		myWorkBook = new XSSFWorkbook(FileUtils.openInputStream(file));
		logger.debug("book"+myWorkBook);
		logger.info("Reading in workbook Finish");

		int numberOfSheets = myWorkBook.getNumberOfSheets();
		int count = 0;
		for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
			XSSFSheet mySheet = myWorkBook.getSheetAt(sheetIndex);
			//	ArrayList<String> headings = new ArrayList<String>();
			Iterator<?> rowIter = mySheet.rowIterator();
			Vector<XSSFCell> cellStoreVector = new Vector<XSSFCell>();
			Vector<String> cellVector = new Vector<String>();
			while (rowIter.hasNext()) {
				XSSFRow myRow = (XSSFRow) rowIter.next();
				Iterator<?> cellIter = myRow.cellIterator();
				DataFormatter formatter = new DataFormatter();
				columnMap = new HashMap<String, String>();
				if (myRow.getRowNum() == 0) {

					while (cellIter.hasNext())
					{
						XSSFCell myCell = (XSSFCell) cellIter.next();
						String cellValue = formatter.formatCellValue(myCell);
						cellVector.addElement(cellValue);
						cellStoreVector.addElement(myCell);
					}

				checkPrimaryHeadings(cellVector, myRow);
					continue;

				}
				rowNumber = myRow.getRowNum();
				logger.debug("row:" + rowNumber);
				rowNumber++;
				String srString = XlsxUtilities.getStringCellValue(myRow.getCell(cellVector.indexOf("SR.")));
				int sr = 0; // Default value or whatever appropriate default value for sr
				try {
				    sr = Double.valueOf(srString).intValue();
				} catch (NumberFormatException e) {
				    // Handle the case where the string is not a valid number
				    // Log a message or take appropriate action
				    // For example, you might set a default value or skip this entry
				    System.out.println("Unable to parse SR value: " + srString);
				    // You might also throw an exception or return an error code, depending on your application's logic
				}
				String regionId =getCellValue(
						myRow.getCell(cellVector.indexOf("Region_ID")),rowNumber, cellStoreVector.elementAt(cellVector.indexOf("Region_ID")).toString()).trim();

				columnMap.put("regionId", regionId);
				String territoryId =getCellValue(
						myRow.getCell(cellVector.indexOf("Territory_ID")),rowNumber, cellStoreVector.elementAt(cellVector.indexOf("Territory_ID")).toString()).trim();

				columnMap.put("territoryId", territoryId);
				String territory =getCellValue(
						myRow.getCell(cellVector.indexOf("Territory")),rowNumber, cellStoreVector.elementAt(cellVector.indexOf("Territory")).toString()).trim();

				columnMap.put("territory", territory);

				String brandId =getCellValue(
						myRow.getCell(cellVector.indexOf("Brand_ID")),rowNumber, cellStoreVector.elementAt(cellVector.indexOf("Brand_ID")).toString()).trim();

				columnMap.put("brandId", brandId);		


				String brandName =getCellValue(
						myRow.getCell(cellVector.indexOf("Brand_Name")),rowNumber, cellStoreVector.elementAt(cellVector.indexOf("Brand_Name")).toString()).trim();

				columnMap.put("brandName", brandName);

				String target =getCellValue(
						myRow.getCell(cellVector.indexOf("Target")),rowNumber, cellStoreVector.elementAt(cellVector.indexOf("Target")).toString()).trim();

				columnMap.put("target", target);	



				String targetYear = getCellValue(
						myRow.getCell(cellVector.indexOf("Target_Year")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("Target_Year")).toString()).trim();

				columnMap.put("targetYear", targetYear);



				String targetMonth = getCellValue(
						myRow.getCell(cellVector.indexOf("Target_Month")),rowNumber,cellStoreVector.elementAt(cellVector.indexOf("Target_Month")).toString()).trim();
				columnMap.put("targetMonth", targetMonth);
				allDataMap.put(sr, columnMap);


			}
		}
		message.append(count);
		myWorkBook.close();

		return allDataMap;
	}
	

	public String getCellValue(XSSFCell xssfCell,int rowNumber, String headingTitle) throws CustomException
{
		
		String cellStringValue=null;
		
		if ( !(xssfCell == null)  )
		{
			
			xssfCell.setCellType(Cell.CELL_TYPE_STRING);
			cellStringValue = xssfCell.getStringCellValue().trim();
		}
		else
		{
			throw new CustomException("Column "+headingTitle+"  Empty at Row no: "+rowNumber);
		}
		return cellStringValue;
}
	private void checkHeadings(Vector<String> cellStoreVector,
			XSSFRow myRow) throws InvalidHeadingException
	{
		
		ArrayList<String> headings = new ArrayList<String>();
		headings=loadValidHeadingsList();
		ArrayList<String> vectorList = new ArrayList<String>(cellStoreVector);
		headings.removeAll(vectorList);
		if (!headings.isEmpty())
		{
			throw new InvalidHeadingException("Invalid Heading: " + headings);
		}

	}
	private void checkPrimaryHeadings(Vector<String> cellStoreVector,
			XSSFRow myRow) throws InvalidHeadingException
	{
		
		ArrayList<String> headings = new ArrayList<String>();
		headings=loadndValidHeadingsList();
		logger.info("headingggg"+headings);
		ArrayList<String> vectorList = new ArrayList<String>(cellStoreVector);
		logger.info(vectorList);
		headings.removeAll(vectorList);
		if (!headings.isEmpty())
		{
			throw new InvalidHeadingException("Invalid Heading: " + headings);
		}

	}
	public ArrayList<String> loadValidHeadingsList()
	{
		ArrayList<String> headings = new ArrayList<String>();
		headings.add("Sr,");
		headings.add("region_id");
		headings.add("region");
		headings.add("territory_id");
		headings.add("territory");
		headings.add("town_id");
		headings.add("town");
		headings.add("employee_id");
		headings.add("DE_id");
		headings.add("DE_Code");
		headings.add("DE_Name");
		headings.add("DSR_ID");
		headings.add("DSR_Name");
		headings.add("Brand_ID");
		headings.add("Brand_name");
		headings.add("target_wholesale");
		headings.add("target_retail");
		headings.add("target_year");
		headings.add("target_month");
		headings.add("dsr_code");
		headings.add("dsr_employee_id");
		headings.add("dsr_type");
		System.out.print(headings.toString() + " Size =  " + headings.size());
		return headings;

	}
	public ArrayList<String> loadndValidHeadingsList()
	{
	    ArrayList<String> headings = new ArrayList<String>();
	    headings.add("SR.");
	    headings.add("Region_ID");
	    headings.add("Region");
	    headings.add("Territory_ID");
	    headings.add("Territory");
	    headings.add("Brand_ID");
	    headings.add("Brand_Name");
	    headings.add("Target");
	    headings.add("Target_Year");
	    headings.add("Target_Month");

	    System.out.print(headings.toString() + " Size =  " + headings.size());
	    return headings;
	}

	@Override
	public Map<Integer, Map<String, String>> readExcelFileForWSTKSSaleTarget(
			String path) throws Exception {
		StringBuilder message = new StringBuilder();
		XSSFWorkbook myWorkBook = null;
		Map<Integer, Map<String, String>> allDataMap = new HashMap<Integer, Map<String, String>>();
		Map<String, String> columnMap = null;

		File file = new File(path);

		logger.info("Reading in workbook Started");
		myWorkBook = new XSSFWorkbook(FileUtils.openInputStream(file));
		logger.info("Reading in workbook Finish");

		int numberOfSheets = myWorkBook.getNumberOfSheets();
		int count = 0;
		for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
			XSSFSheet mySheet = myWorkBook.getSheetAt(sheetIndex);
			Iterator<Row> rowIter = mySheet.rowIterator();
			while (rowIter.hasNext()) {
				XSSFRow myRow = (XSSFRow) rowIter.next();
				columnMap = new HashMap<String, String>();

				if (myRow.getRowNum() > 0) {
					int sr = Double.valueOf(
							XlsxUtilities.getStringCellValue(myRow.getCell(0)))
							.intValue();

					String shopId = XlsxUtilities.getStringCellValue(myRow
							.getCell(7));
					columnMap.put("shopId", shopId);
					
					String shopType = XlsxUtilities
							.getStringCellValue(myRow.getCell(9));
					columnMap.put("shopType", shopType);

					String familyId = XlsxUtilities.getStringCellValue(myRow
							.getCell(10));
					columnMap.put("familyId", familyId);

					String saleTarget = XlsxUtilities.getStringCellValue(myRow
							.getCell(12));
					columnMap.put("saleTarget", saleTarget);

					String year = XlsxUtilities.getStringCellValue(myRow
							.getCell(13));
					columnMap.put("year", year);

					String month = XlsxUtilities
							.getStringCellValue(myRow.getCell(14));
					columnMap.put("month", month);

					allDataMap.put(sr, columnMap);
				}

			}
		}
		message.append(count);
		myWorkBook.close();

		return allDataMap;
	}

	@Override
	public Map<Integer, Map<String, String>> readExcelFileForRAShops(
			String path) throws Exception {
		StringBuilder message = new StringBuilder();
		XSSFWorkbook myWorkBook = null;
		Map<Integer, Map<String, String>> allDataMap = new HashMap<Integer, Map<String, String>>();
		Map<String, String> columnMap = null;

		File file = new File(path);

		logger.info("Reading in workbook Started");
		myWorkBook = new XSSFWorkbook(FileUtils.openInputStream(file));
		logger.info("Reading in workbook Finish");

		int numberOfSheets = myWorkBook.getNumberOfSheets();
		int count = 0;
		for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
			XSSFSheet mySheet = myWorkBook.getSheetAt(sheetIndex);
			Iterator<Row> rowIter = mySheet.rowIterator();
			while (rowIter.hasNext()) {
				XSSFRow myRow = (XSSFRow) rowIter.next();
				columnMap = new HashMap<String, String>();

				if (myRow.getRowNum() > 0) {
					int sr = Double.valueOf(
							XlsxUtilities.getStringCellValue(myRow.getCell(0)))
							.intValue();

					String territory = XlsxUtilities.getStringCellValue(myRow
							.getCell(1));
					columnMap.put("territory", territory);

					String shopTitle = XlsxUtilities.getStringCellValue(myRow
							.getCell(2));
					columnMap.put("shopTitle", shopTitle);

					String channel = XlsxUtilities.getStringCellValue(myRow
							.getCell(3));
					columnMap.put("channel", channel);

					String shopAddress = XlsxUtilities.getStringCellValue(myRow
							.getCell(4));
					columnMap.put("shopAddress", shopAddress);

					String landmark = XlsxUtilities.getStringCellValue(myRow
							.getCell(5));
					columnMap.put("landmark", landmark);

					allDataMap.put(sr, columnMap);
				}

			}
		}
		message.append(count);
		myWorkBook.close();

		return allDataMap;
	}
	
	@Override
	public Map<Integer, Map<String, String>> readExcelFileForDSRRoutes(
			String path) throws Exception {
		StringBuilder message = new StringBuilder();
		XSSFWorkbook myWorkBook = null;
		Map<Integer, Map<String, String>> allDataMap = new HashMap<Integer, Map<String, String>>();
		Map<String, String> columnMap = null;

		File file = new File(path);

		logger.info("Reading in workbook Started");
		myWorkBook = new XSSFWorkbook(FileUtils.openInputStream(file));
		logger.info("Reading in workbook Finish");

		int numberOfSheets = myWorkBook.getNumberOfSheets();
		int count = 0;
		for (int sheetIndex = 0; sheetIndex < numberOfSheets; sheetIndex++) {
			XSSFSheet mySheet = myWorkBook.getSheetAt(sheetIndex);
			Iterator<Row> rowIter = mySheet.rowIterator();
			while (rowIter.hasNext()) {
				XSSFRow myRow = (XSSFRow) rowIter.next();
				columnMap = new HashMap<String, String>();

				if (myRow.getRowNum() > 0) {
					int sr = Double.valueOf(
							XlsxUtilities.getStringCellValue(myRow.getCell(0)))
							.intValue();

					String shopId = XlsxUtilities.getStringCellValue(myRow
							.getCell(1));
					columnMap.put("shopId", shopId);
					
					String shopTitle = XlsxUtilities
							.getStringCellValue(myRow.getCell(2));
					columnMap.put("shopTitle", shopTitle);

					String dsrId = XlsxUtilities.getStringCellValue(myRow
							.getCell(3));
					columnMap.put("dsrId", dsrId);

					String dsrName = XlsxUtilities.getStringCellValue(myRow
							.getCell(4));
					columnMap.put("dsrName", dsrName);

					String day = XlsxUtilities.getStringCellValue(myRow
							.getCell(5));
					columnMap.put("day", day);

					allDataMap.put(sr, columnMap);
				}

			}
		}
		message.append(count);
		myWorkBook.close();

		return allDataMap;
	}
	
}
