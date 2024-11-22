package com.marksman.census.controller;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.client.bo.SyncData;
import com.marksman.census.constants.CommonConstants;
import com.marksman.census.constants.DateTimeConstants;
import com.marksman.census.dao.CitiesDao;
import com.marksman.census.dao.PortalDao;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.message.MessageType;
import com.marksman.census.service.ClientLoggingServiceImpl;
import com.marksman.census.service.FileUploadService;
import com.marksman.census.service.ValidationService;
import com.marksman.census.util.CommonUtil;
import com.marksman.census.util.DateTimeUtilities;
import com.marksman.census.util.FileUtils;
import com.marksman.census.util.SpringApplicationContext;
import com.marksman.census.util.StringUtils;

@Controller
public class FileUploadControllerImpl implements FileUploadController {
	@Autowired
	ServletContext servletContext;
	@Autowired
	FileUploadService fileUploadService;

	@Autowired
	ValidationService validationService;

	@Autowired
	CitiesDao cityDao;

	@Autowired
	SurveyorDao surveyorDao;

	@Autowired
	PortalDao portalDao;

	@Autowired
	ApplicationCacheService applicationCacheService;
	
	@Autowired
	ClientLoggingServiceImpl clientLoggingService;

	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public @ResponseBody
	void uploadDbFile(HttpServletRequest request, HttpServletResponse response) {
		
		ServletFileUpload upload = new ServletFileUpload();
		String fileName = "";
		clientLoggingService.insertActivityLog(request, response, "DB_UPLOAD");
		try
		{
			FileItemIterator iterator = upload.getItemIterator(request);
			String surveyorId = request.getHeader("surveyorId");
			while (iterator.hasNext())
			{
				FileItemStream item = iterator.next();

				if (!item.isFormField())
				{
					String curruntDate = DateTimeUtilities
							.getCurrentDate(DateTimeConstants.DATE_FORMAT4);
					String fileDir = servletContext.getRealPath("/");
					fileDir = fileDir + "Db" + File.separator + surveyorId + File.separator
							+ curruntDate + File.separator;
					if (!FileUtils.isExistsFolder(fileDir))
					{
						FileUtils.createFolders(fileDir);
					}
					fileName = item.getName();
					logger.info("uploading file : " + item.getName());
					FileUtils.storeFile(fileDir, fileName, item);
				}
			}
		}
		catch (Exception ex)
		{

			logger.error("Error while uploading db file", ex);
			CommonUtil.writeErrorMessage(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.ERROR_SERVER, response);
		}
		CommonUtil.writeErrorMessage(HttpServletResponse.SC_OK, MessageType.SUCCESS, response);
	}

	@Override
	public @ResponseBody
	Map<String, Object> uploadFile(HttpServletRequest request,
			HttpServletResponse response) {
		ServletFileUpload upload = new ServletFileUpload();
		Map<String, Object> map = null;
		String fileName = "";
		String fileUrl = null;
		String destFileName = null;
		String path = "";
		try {
			FileItemIterator iterator = upload.getItemIterator(request);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();

				if (!item.isFormField()) {
					String curruntDate = DateTimeUtilities.getDate();
					String fileDir = servletContext.getRealPath("/");
					fileDir = fileDir + "Route/" + curruntDate;
					if (!FileUtils.isExistsFolder(fileDir)) {
						FileUtils.createFolders(fileDir);
					}
					fileName = item.getName();

					fileUrl = fileDir + "/" + fileName;
					int destFileID = portalDao.getFileId(
							fileDir,
							fileName,
							fileUrl,
							StringUtils.parseInt(
									request.getParameter("regionId"), -1));
					destFileName = destFileID + ".xlsx";
					logger.info("uploading file : " + item.getName());
					path = FileUtils.storeFile(fileDir + "/", destFileName,
							item);
				}
			}
			String message = "";
			String extension = FilenameUtils.getExtension(fileName);
			if (CommonConstants.XLSX_FILE.equalsIgnoreCase(extension)) {
				message = fileUploadService.uploadExcelFile(fileName,
						StringUtils.parseInt(request.getParameter("regionId"),
								-1), path);
			} else {
				message = "file type not supported";
			}
			map = new HashMap<String, Object>();
			map.put("message", message);

		} catch (Exception ex) {

			logger.error("Error while uploading excel file", ex);
		}
		return map;
	}
	
	//By Hamed 
	@Override
	public @ResponseBody
	Map<String, Object> uploadImeisExcelFile(HttpServletRequest request,
			HttpServletResponse response) {
		/*// TODO Auto-generated method stub
		return null;*/
		Map <String, Object> map = new HashMap<String, Object>();
		Gson gson = new Gson();
		try{
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
					fileDir = fileDir + "Route/" + curruntDate;
					if (!FileUtils.isExistsFolder(fileDir)) {
						FileUtils.createFolders(fileDir);
					}
					fileName = item.getName();

					fileUrl = fileDir + "/" + fileName;
					int destFileID = portalDao.getImeisExcelFileId(
							fileDir,
							fileName,
							fileUrl);
					destFileName = destFileID + ".xlsx";
					logger.info("uploading file : " + item.getName());
					path = FileUtils.storeFile(fileDir + "/", destFileName,
							item);
					InputStream stream = item.openStream();
					String reader = Streams.asString(stream);
					logger.info("Build Version "
							+ request.getHeader("version")
							+ "\n validated Shop : " + reader);
					singleImei = gson.fromJson(reader, String.class);
					imeiList.add(singleImei);
				}
				
			}
			String message = "";
			String extension = FilenameUtils.getExtension(fileName);
			if (CommonConstants.XLSX_FILE.equalsIgnoreCase(extension)) {
				imeiList = fileUploadService.readExcelFileForIMES(fileName);
			} 
			
			portalDao.insertImeis(imeiList);
			map.put("message", "Imeis Added into System.");

		} catch (Exception ex) {

			logger.error("Error while uploading excel file", ex);
		}
		}catch (Exception e) {
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
	public @ResponseBody
	ModelAndView uploadFileGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/uploadExcelFile");
			modelAndView.addObject("regionList",
					applicationCacheService.getRegions());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
		} catch (Exception ex) {

			logger.error("Error while uploading parental file", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	@Override
	public @ResponseBody
	Map<String, Object> loadRouteInfo(HttpServletRequest request,
			HttpServletResponse response, Integer regionId) {
		Map<String, Object> responseMap = null;
		try {
			responseMap = new HashMap<String, Object>();
			if (regionId != null) {
				List<Surveyor> surveyorList = surveyorDao
						.getSurveyorList(regionId);
				responseMap.put("surveyorList", surveyorList);
			}
		} catch (Exception ex) {

			logger.error("Error while Getting Region Against Zone", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return responseMap;
	}

	@Override
	public Map<String, Object> shopUpdate(HttpServletRequest request,
			HttpServletResponse response) {
		ServletFileUpload upload = new ServletFileUpload();
		Map<String, Object> map = null;
		String fileName = "";
		String fileUrl = null;
		String destFileName = null;
		String path = "";
		try {
			FileItemIterator iterator = upload.getItemIterator(request);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();

				if (!item.isFormField()) {
					String curruntDate = DateTimeUtilities.getDate();
					String fileDir = servletContext.getRealPath("/");
					fileDir = fileDir + "Route/Update/" + curruntDate;
					if (!FileUtils.isExistsFolder(fileDir)) {
						FileUtils.createFolders(fileDir);
					}
					fileName = item.getName();

					fileUrl = fileDir + "/" + fileName;
					int destFileID = portalDao.getFileId(
							fileDir,
							fileName,
							fileUrl,
							StringUtils.parseInt(
									request.getParameter("regionId"), -1));
					destFileName = destFileID + ".xlsx";
					logger.info("uploading file : " + item.getName());
					path = FileUtils.storeFile(fileDir + "/", destFileName,
							item);
				}
			}
			String message = "";
			String extension = FilenameUtils.getExtension(fileName);
			if (CommonConstants.XLSX_FILE.equalsIgnoreCase(extension)) {
				message = fileUploadService.updateExcelFile(fileName,
						StringUtils.parseInt(request.getParameter("regionId"),
								-1), path);
			} else {
				message = "file type not supported";
			}
			map = new HashMap<String, Object>();
			map.put("message", message);

		} catch (Exception ex) {

			logger.error("Error while uploading excel file", ex);
		}
		return map;
	}

	@Override
	public ModelAndView shopUpdateGet(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = new ModelAndView("WEB-INF/jsp/updateExcelFile");
			modelAndView.addObject("regionList",
					applicationCacheService.getRegions());
			modelAndView.addObject("time", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.TIME_FORMAT));
			modelAndView.addObject("date", DateTimeUtilities
					.getCurrentDate(DateTimeConstants.DATE_FORMAT2));
			modelAndView.addObject("role",
					SpringApplicationContext.getAuthority());
			modelAndView.addObject("userName", SpringApplicationContext
					.getAuthentication().getName());
		} catch (Exception ex) {

			logger.error("Error while uploading parental file", ex);
		}
		response.setHeader("Cache-Control", "no-cache");
		return modelAndView;
	}

	

}
