package com.marksman.census.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;

public interface DataController
{

	@RequestMapping(value = "/refresh")
	public Map<String, Object> refresh(String imei, String mCode, HttpServletRequest request,
			HttpServletResponse response);

}
