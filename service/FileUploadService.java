package com.marksman.census.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public interface FileUploadService
{
	public String uploadExcelFile(String fileName, int regionId, String path) throws Exception;

	public String readExcelFile(String path, int regionId);
	
	public List<String> readExcelFileForIMES(String path);
	
	public Map<Integer, Map<String, String>> readExcelFileForDsrSaleTarget(String path, ArrayList<String> validatorArray) throws Exception,InvalidHeadingException,CustomException;
	
	public String updateExcelFile(String fileName, int regionId, String path) throws Exception;

	public String readUpdateExcelFile(String path, int regionId);

	Map<Integer, Map<String, String>> readExcelFileForWSTKSSaleTarget(
			String path) throws Exception;
	
	public Map<Integer, Map<String, String>> readExcelFileForRAShops(String path) throws Exception;
	
	public Map<Integer, Map<String, String>> readExcelFileForDSRRoutes(String path) throws Exception;

	public Map<Integer, Map<String, String>> readExcelFileForPrimarySaleTarget(
			String path, ArrayList<String> validatorArray) throws Exception;
}
