package com.marksman.census.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface ManagementAppController {

	@RequestMapping(value = { "/basic-data" }, method = { RequestMethod.POST })
	Map<String, Object> getbasicData(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = { "/instoLogin" }, method = { RequestMethod.POST })
	Map<String, Object> instoLogin(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = { "chiller-oos" }, method = { RequestMethod.POST })
	List<Map<String, Object>> getPrimarySalesMonthWise(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = { "/trend-chiller-oos" }, method = { RequestMethod.POST })
	List<Map<String, Object>> getPrimaryAchievementTrends(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = { "/trend-ps" }, method = { RequestMethod.POST })
	List<Map<String, Object>> getPrimarySalesTrends(HttpServletRequest request,
			HttpServletResponse response);

	@RequestMapping(value = { "/oos" }, method = { RequestMethod.POST })
	List<Map<String, Object>> getSecondarySalesMonthWise(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = { "/trend-oos" }, method = { RequestMethod.POST })
	List<Map<String, Object>> getSecondaryAchievementTrends(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = { "/trend-ss" }, method = { RequestMethod.POST })
	List<Map<String, Object>> getSecondarySalesTrends(
			HttpServletRequest request, HttpServletResponse response);

	@RequestMapping(value = { "/dsr-monthwise-productivity" }, method = { RequestMethod.POST })
	List<Map<String, Object>> getDsrMonthwiseproductivity(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException;

	@RequestMapping(value = { "/de-monthwise-productivity" }, method = { RequestMethod.POST })
	List<Map<String, Object>> getDeMonthwiseproductivity(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException;

	@RequestMapping(value = { "/tm-monthwise-productivity" }, method = { RequestMethod.POST })
	List<Map<String, Object>> getTmMonthwiseproductivity(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException;

	@RequestMapping(value = { "/trend-Dsr-productivity" }, method = { RequestMethod.POST })
	List<Map<String, Object>> getDsrProductivityTrends(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException;

	@RequestMapping(value = { "/trend-De-productivity" }, method = { RequestMethod.POST })
	List<Map<String, Object>> getDeProductivityTrends(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException;

	@RequestMapping(value = { "/trend-Tm-productivity" }, method = { RequestMethod.POST })
	List<Map<String, Object>> getTmProductivityTrends(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException;

}
