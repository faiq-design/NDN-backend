package com.marksman.census.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface SyncController
{

	@RequestMapping(value = "/client/sync", method = RequestMethod.POST)
	public Map<String, Object> syncData(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	@RequestMapping(value = "/client/checkIn", method = RequestMethod.POST)
	public Map<String, Object> syncCheckInData(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
	
	@RequestMapping(value = "/client/sis/endDay", method = RequestMethod.POST)
	public Map<String, Object> syncEndDayData(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	@RequestMapping(value = "/asm/sync", method = RequestMethod.POST)
	public Map<String, Object> syncAsmData(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	@RequestMapping(value = "/client/syncVisit", method = RequestMethod.POST)
	public Map<String, Object> syncVisit(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
	

	@RequestMapping(value = "/client/endActivity", method = RequestMethod.POST)
	public Map<String, Object> engActivity(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	@RequestMapping(value = "/sis/syncVisit", method = RequestMethod.POST)
	public Map<String, Object> syncSisVisit(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	@RequestMapping(value = "/client/survey-json-uploader", method = RequestMethod.POST)
	public Map<String, Object> surveyJsonUploader(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	@RequestMapping(value = "/client/ws/syncSurvey", method = RequestMethod.POST)
	public Map<String, Object> syncWsSurvey(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
	
	@RequestMapping(value = "/sis/update-shop", method = RequestMethod.POST)
	public Map<String, Object> updateSisRa(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
	
	
	@RequestMapping(value = "/file" , method = RequestMethod.POST)
	public Map<String, Object> moveFiles(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

}
