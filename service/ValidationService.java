package com.marksman.census.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.marksman.census.constants.SurveyorType;

public interface ValidationService {
	// public String getAllowedVariance();

	public boolean validateSurveyor(String mCode, SurveyorType surveyorType,
			String imei, HttpServletResponse response);

	public int isValidCode(String mCode, SurveyorType surveyorType,
			HttpServletResponse response);

	public boolean isValidImei(String imei, HttpServletResponse response);

	public boolean validateSurveyor(String mCode, String password,
			SurveyorType de, String imei, HttpServletResponse response);

	public Map<String, Object> updatePassword(HttpServletRequest request,
			HttpServletResponse respons, String mCode, String oldPassword,
			String newPassword);

	public boolean validateDsr(String mCode, String password,
			SurveyorType surveyorType, String imei, HttpServletResponse response);

}
