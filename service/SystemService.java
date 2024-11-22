package com.marksman.census.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.marksman.census.constants.SurveyorType;

public interface SystemService {

	public Map<String, Object> checkUpdates(SurveyorType appType,
			HttpServletRequest request, HttpServletResponse response);

	public Map<String, Object> forgotPassword(String userName) throws Exception;

	public Map<String, Object> mvFiles() throws Exception;

}
