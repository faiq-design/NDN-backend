/**
 * 
 */
package com.marksman.census.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Muhammad Awais
 * 
 */
public interface LoggingService
{

	void insertActivityLog(HttpServletRequest request, HttpServletResponse response,
			String requestType);
}
