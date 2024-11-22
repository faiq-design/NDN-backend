package com.marksman.census.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

public interface FileUploadController
{
	@RequestMapping(value = "/db-upload", method = RequestMethod.POST)
	public void uploadDbFile(HttpServletRequest request, HttpServletResponse response);
	
	@RequestMapping(value = "/portal/dump-file", method = RequestMethod.POST)
	public Map<String, Object> uploadFile(HttpServletRequest request, HttpServletResponse response);
	
	@RequestMapping( value = "/portal/upload-imeis" , method= RequestMethod.POST)
	public Map<String, Object> uploadImeisExcelFile(HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/upload-file")
	public ModelAndView uploadFileGet(HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/loadRouteInfo", method = RequestMethod.POST)
	public Map<String, Object> loadRouteInfo(HttpServletRequest request,
			HttpServletResponse response, Integer regionId);
	
	
	@RequestMapping(value = "/portal/dump-update-file", method = RequestMethod.POST)
	public Map<String, Object> shopUpdate(HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = "/portal/update-file")
	public ModelAndView shopUpdateGet(HttpServletRequest request, HttpServletResponse response);

}
