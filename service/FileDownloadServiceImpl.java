package com.marksman.census.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.marksman.census.constants.SurveyorType;
import com.marksman.census.dao.SystemDao;

public class FileDownloadServiceImpl implements FileDownloadService {
	@Autowired
	ServletContext servletContext;
	@Autowired
	private SystemDao systemDao;
	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void downloadImage(String imagePath, HttpServletResponse response) {
		InputStream inputStream = null;
		try {

			logger.info("Getting image from path: " + imagePath);
			File imgFile = new File(servletContext.getRealPath("/")
					+ File.separator + imagePath);
			if (imgFile.exists()) {

				inputStream = new FileInputStream(imgFile);
				byte[] buffer = new byte[4096];
				int size = 0;
				while ((size = inputStream.read(buffer)) > 0) {

					response.getOutputStream().write(buffer, 0, size);
				}
			}
		} catch (Exception ex) {

			logger.error("Error while downloading image : " + imagePath, ex);
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				logger.error("Error while closing stream ", e);
			}

		}
	}

	@Override
	public void downloadDssBuild(HttpServletResponse response) {
		String buildPath = "";
		try {

			logger.info("Downlaoding DE build ");
			Map<String, Object> buildInfo = systemDao
					.getBuildUrl(SurveyorType.DE);
			if (buildInfo != null) {
				this.downloadBuild(buildInfo, response);
			}
		} catch (Exception ex) {

			logger.error("Error while downloading DE build : " + buildPath, ex);
		}
	}
	
	@Override
	public void downloadTmBuild(HttpServletResponse response) {
		String buildPath = "";
		try {

			logger.info("Downlaoding TM build ");
			Map<String, Object> buildInfo = systemDao
					.getBuildUrl(SurveyorType.TM);
			if (buildInfo != null) {
				this.downloadBuild(buildInfo, response);
			}
		} catch (Exception ex) {

			logger.error("Error while downloading DE build : " + buildPath, ex);
		}
	}

	@Override
	public void downloadSisBuild(HttpServletResponse response) {
		String buildPath = "";
		try {

			logger.info("Downlaoding SIS build ");
			Map<String, Object> buildInfo = systemDao
					.getBuildUrl(SurveyorType.SIS);
			if (buildInfo != null) {
				this.downloadBuild(buildInfo, response);
			}
		} catch (Exception ex) {
			logger.error("Error while downloading SIS build : " + buildPath, ex);
		}
	}

	@Override
	public void downloadAsmBuild(HttpServletResponse response) {
		String buildPath = "";
		try {

			logger.info("Downlaoding Asm build ");
			Map<String, Object> buildInfo = systemDao
					.getBuildUrl(SurveyorType.ASM);
			if (buildInfo != null) {
				this.downloadBuild(buildInfo, response);
			}
		} catch (Exception ex) {

			logger.error("Error while downloading Asm build : " + buildPath, ex);
		}

	}

	void downloadBuild(Map<String, Object> buildInfo,
			HttpServletResponse response) throws Exception {

		InputStream inputStream = null;
		try {
			File buildFile = new File(servletContext.getRealPath("/")
					+ File.separator + buildInfo.get("url").toString());
			response.setHeader("Content-disposition", "attachment; filename="
					+ buildInfo.get("title").toString());
			logger.info("app download url --->  " + buildFile);
			if (buildFile.exists()) {

				inputStream = new FileInputStream(buildFile);
				byte[] buffer = new byte[4096];
				int size = 0;
				while ((size = inputStream.read(buffer)) > 0) {

					response.getOutputStream().write(buffer, 0, size);
				}
			}
		} catch (Exception ex) {

			logger.error(ex, ex);
			throw ex;
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				logger.error("Error while closing stream ", e);
			}

		}
	}

	
}
