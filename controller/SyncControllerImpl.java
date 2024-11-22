package com.marksman.census.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.marksman.census.service.ClientLoggingServiceImpl;
import com.marksman.census.service.SyncService;

@Controller
public class SyncControllerImpl implements SyncController
{

	@Autowired
	SyncService syncService;
	@Autowired
	ClientLoggingServiceImpl clientLoggingService;

	@Override
	public @ResponseBody
	Map<String, Object> syncData(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		clientLoggingService.insertActivityLog(request, response, "SyncData");
		Map<String, Object> jsonMap = syncService.syncData(version, request, response);
		return jsonMap;
	}

	@Override
	public @ResponseBody
	Map<String, Object> syncCheckInData(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		clientLoggingService.insertActivityLog(request, response, "SyncCheckInData");
		Map<String, Object> jsonMap = syncService.syncCheckInData(version, request, response);
		return jsonMap;
	}
	
	@Override
	public @ResponseBody
	Map<String, Object> syncEndDayData(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		clientLoggingService.insertActivityLog(request, response, "syncEndDayData");
		Map<String, Object> jsonMap = syncService.syncEndDayData(version, request, response);
		return jsonMap;
	}

	@Override
	public @ResponseBody
	Map<String, Object> syncAsmData(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		clientLoggingService.insertActivityLog(request, response, "SyncAsmData");
		Map<String, Object> jsonMap = syncService.syncAsmData(version, request, response);
		return jsonMap;
	}

	@Override
	public @ResponseBody
	Map<String, Object> syncVisit(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		clientLoggingService.insertActivityLog(request, response, "SyncVisit");
		Map<String, Object> jsonMap = syncService.syncVisit(version, request, response);
		return jsonMap;
	}

	@Override
	@ResponseBody
	public Map<String, Object> engActivity(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		clientLoggingService.insertActivityLog(request, response, "EndActivity");
		Map<String, Object> jsonMap = syncService.endActivity(version, request, response);
		return jsonMap;
	}

	@Override
	@ResponseBody
	public Map<String, Object> syncSisVisit(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		clientLoggingService.insertActivityLog(request, response, "SyncSisVisit");
		Map<String, Object> jsonMap = syncService.syncSisVisit(version, request, response);
		return jsonMap;
	}

	@Override
	@ResponseBody
	public Map<String, Object> surveyJsonUploader(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		clientLoggingService.insertActivityLog(request, response, "SurveyJsonUploader");
		Map<String, Object> jsonMap = syncService.surveyJsonUploader(request, response);
		return jsonMap;
	}

	@Override
	@ResponseBody
	public Map<String, Object> syncWsSurvey(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		clientLoggingService.insertActivityLog(request, response, "SyncWsSurvey");
		Map<String, Object> jsonMap = syncService.syncWsSurvey(version, request, response);
		return jsonMap;
	}
	
	@Override
	public @ResponseBody
	Map<String, Object> updateSisRa(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		clientLoggingService.insertActivityLog(request, response, "UpdateSisRaData");
		Map<String, Object> jsonMap = syncService.updateSisRaData(request, response);
		return jsonMap;
	}
	
	@Override
	@ResponseBody
	public Map<String, Object> moveFiles(HttpServletRequest request,
			HttpServletResponse response) throws  ServletException, IOException {
		Map<String, Object> jsonMap = syncService.mvFiles(request, response);
		return jsonMap;
	}
	
}
