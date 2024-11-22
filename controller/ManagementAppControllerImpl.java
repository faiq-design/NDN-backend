package com.marksman.census.controller;

import com.marksman.census.service.ManagementAppService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ManagementAppControllerImpl implements ManagementAppController {
	protected Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	ManagementAppService managementappservice;

	@ResponseBody
	public Map<String, Object> getbasicData(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> jsonMap = null;
		jsonMap = managementappservice.getbasicData(request, response);
		return jsonMap;
	}

	@ResponseBody
	public Map<String, Object> instoLogin(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> data = null;

		try {
			data = managementappservice.instoLogin(request, response);
		} catch (IOException e) {
			this.logger.error(e, e);
		}

		return data;
	}

	@ResponseBody
	public List<Map<String, Object>> getPrimarySalesMonthWise(
			HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, Object>> data = null;

		try {
			data = managementappservice.getPrimarySalesMonthWise(request,
					response);
		} catch (IOException e) {
			this.logger.error(e, e);
		}

		return data;
	}

	@ResponseBody
	public List<Map<String, Object>> getSecondarySalesMonthWise(
			HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, Object>> data = null;

		try {
			data = managementappservice.getSecondarySalesMonthWise(request,
					response);
		} catch (IOException e) {
			this.logger.error(e, e);
		}

		return data;
	}

	@ResponseBody
	public List<Map<String, Object>> getPrimaryAchievementTrends(
			HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, Object>> data = null;

		try {
			data = managementappservice.getPrimaryAchievementTrends(request,
					response);
		} catch (IOException e) {
			this.logger.error(e, e);
		}

		return data;
	}

	@ResponseBody
	public List<Map<String, Object>> getPrimarySalesTrends(
			HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, Object>> data = null;

		try {
			data = managementappservice
					.getPrimarySalesTrends(request, response);
		} catch (IOException e) {
			this.logger.error(e, e);
		}

		return data;
	}

	@ResponseBody
	public List<Map<String, Object>> getSecondarySalesTrends(
			HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, Object>> data = null;

		try {
			data = this.managementappservice.getSecondarySalesTrends(request,
					response);
		} catch (IOException e) {
			this.logger.error(e, e);
		}

		return data;
	}

	@ResponseBody
	public List<Map<String, Object>> getSecondaryAchievementTrends(
			HttpServletRequest request, HttpServletResponse response) {
		List<Map<String, Object>> data = null;

		try {
			data = this.managementappservice.getSecondaryAchievementTrends(
					request, response);
		} catch (IOException e) {
			this.logger.error(e, e);
		}

		return data;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDsrMonthwiseproductivity(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		List<Map<String, Object>> data = null;
		data = this.managementappservice.getDsrMonthwiseproductivity(request,
				response);
		return data;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDeMonthwiseproductivity(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		List<Map<String, Object>> data = null;
		data = this.managementappservice.getDeMonthwiseproductivity(request,
				response);
		return data;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getTmMonthwiseproductivity(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		List<Map<String, Object>> data = null;
		data = managementappservice.getTmMonthwiseproductivity(request,
				response);
		return data;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDsrProductivityTrends(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		List<Map<String, Object>> data = null;
		data = managementappservice.getDsrProductivityTrends(request, response);
		return data;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDeProductivityTrends(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		List<Map<String, Object>> data = null;
		data = managementappservice.getDeProductivityTrends(request, response);
		return data;
	}
	@Override
	@ResponseBody
	public List<Map<String, Object>> getTmProductivityTrends(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		List<Map<String, Object>> data = null;
		data = managementappservice.getTmProductivityTrends(request, response);
		return data;
	}
}