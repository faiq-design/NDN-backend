package com.marksman.census.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.marksman.census.constants.SurveyorType;

public interface SystemController {

	@RequestMapping(value = "/chk/updates", method = RequestMethod.POST)
	public Map<String, Object> checkUpdates(SurveyorType appType,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;

	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public void authenticateSurveyor(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	@RequestMapping(value = "/forgot/password")
	public Map<String, Object> forgotPassword(String email,
			HttpServletRequest request);

	@RequestMapping(value = "/mv")
	public Map<String, Object> moveFiles(String email,
			HttpServletRequest request) throws Exception;

}
