package com.marksman.census.service;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public interface DataService
{

	public Map<String, Object> refresh(String imei, String mCode, HttpServletResponse response);

}
