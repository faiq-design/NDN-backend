/**
 * 
 */
package com.marksman.census.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Muhammad Awais
 * 
 */
public interface SisService
{
	public Map<String, Object> login(String imei, String mCode, HttpServletRequest request,
			HttpServletResponse response);
}
