package com.marksman.census.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marksman.census.service.FileDownloadService;

@Controller
public class FileDownloadControllerImpl implements FileDownloadController
{
	@Autowired
	FileDownloadService fileDownloadService;

	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public @ResponseBody
	void downloadImage(HttpServletRequest request, HttpServletResponse response)
	{
		fileDownloadService.downloadImage(request.getPathInfo(), response);
	}

	@Override
	public @ResponseBody
	void downloadDssBuild(HttpServletRequest request, HttpServletResponse response)
	{
		fileDownloadService.downloadDssBuild(response);
	}
	
	@Override
	public @ResponseBody
	void downloadSisBuild(HttpServletRequest request, HttpServletResponse response)
	{
		fileDownloadService.downloadSisBuild(response);
	}

	@Override
	public @ResponseBody
	void downloadAsmBuild(HttpServletRequest request, HttpServletResponse response)
	{
		fileDownloadService.downloadAsmBuild(response);
	}

	@Override
	public void downloadTmBuild(HttpServletRequest request,
			HttpServletResponse response) {
		fileDownloadService.downloadTmBuild(response);
		
	}
}
