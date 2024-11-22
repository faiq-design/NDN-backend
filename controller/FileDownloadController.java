package com.marksman.census.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface FileDownloadController
{

	@RequestMapping(value = {"/censusImages/**","/censusImages1/**"}, method = RequestMethod.GET)
	public void downloadImage(HttpServletRequest request, HttpServletResponse response);
	
	@RequestMapping(value = "/app/de", method = RequestMethod.GET)
	public void downloadDssBuild(HttpServletRequest request, HttpServletResponse response);
	
	@RequestMapping(value = "/app/tm", method = RequestMethod.GET)
	public void downloadTmBuild(HttpServletRequest request, HttpServletResponse response);
	
	@RequestMapping(value = "/app/sis", method = RequestMethod.GET)
	public void downloadSisBuild(HttpServletRequest request, HttpServletResponse response);
	
	@RequestMapping(value = "/app/asm", method = RequestMethod.GET)
	public void downloadAsmBuild(HttpServletRequest request, HttpServletResponse response);

}
