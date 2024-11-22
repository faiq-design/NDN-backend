package com.marksman.census.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SyncService
{

	public Map<String, Object> syncData(String version, HttpServletRequest request,
			HttpServletResponse response);

	public Map<String, Object> syncCheckInData(String version, HttpServletRequest request,
			HttpServletResponse response);

	public Map<String, Object> syncEndDayData(String version, HttpServletRequest request,
			HttpServletResponse response);
	
	public Map<String, Object> syncAsmData(String version, HttpServletRequest request,
			HttpServletResponse response);

	public Map<String, Object> syncVisit(String version, HttpServletRequest request,
			HttpServletResponse response);

	public Map<String, Object> endActivity(String version, HttpServletRequest request,
			HttpServletResponse response);

	public Map<String, Object> syncSisVisit(String version, HttpServletRequest request,
			HttpServletResponse response);

	public Map<String, Object> surveyJsonUploader(HttpServletRequest request,
			HttpServletResponse response);

	public Map<String, Object> syncWsSurvey(String version, HttpServletRequest request,
			HttpServletResponse response);

	public Map<String, Object> updateSisRaData(HttpServletRequest request,
			HttpServletResponse response);
	public Map<String, Object> mvFiles(HttpServletRequest request,
			HttpServletResponse response) ;

}
