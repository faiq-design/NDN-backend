package com.marksman.census.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ManagementAppService {

	public Map<String, Object> getbasicData(HttpServletRequest request,
			HttpServletResponse response);

	public Map<String, Object> instoLogin(HttpServletRequest request,
			HttpServletResponse response) throws IOException;

	public List<Map<String, Object>> getPrimarySalesMonthWise(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException;

	public List<Map<String, Object>> getSecondarySalesMonthWise(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException;

	public List<Map<String, Object>> getPrimaryAchievementTrends(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException;

	public List<Map<String, Object>> getSecondaryAchievementTrends(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException;

	public List<Map<String, Object>> getPrimarySalesTrends(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException;

	public List<Map<String, Object>> getSecondarySalesTrends(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException;

	public List<Map<String, Object>> getDsrMonthwiseproductivity(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException;

	public List<Map<String, Object>> getDeMonthwiseproductivity(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException;

	public List<Map<String, Object>> getTmMonthwiseproductivity(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException;

	public List<Map<String, Object>> getDsrProductivityTrends(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException;

	public List<Map<String, Object>> getDeProductivityTrends(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException;

	public List<Map<String, Object>> getTmProductivityTrends(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException;
}