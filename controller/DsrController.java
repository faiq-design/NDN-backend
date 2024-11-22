package com.marksman.census.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.marksman.census.bo.Area;
import com.marksman.census.client.bo.ShopRoute;
import com.marksman.census.client.bo.SummaryTag;
import com.marksman.census.constants.SurveyorType;

public interface DsrController
{

	@RequestMapping(value = "/dsr/login", method = RequestMethod.POST)
	public Map<String, Object> dsrLogin(String imei, String dCode, String password,
			HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException;

	@RequestMapping(value = "/dsr/shop-list", method = RequestMethod.GET)
	public List<ShopRoute> dsrShopList(Integer routeId, Integer dsrId, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	@RequestMapping(value = "/dsr/dsr-list", method = RequestMethod.GET)
	public List<Map<String, Object>> getDsrList(HttpServletRequest request,
			HttpServletResponse response, Integer deId);

	@RequestMapping(value = "/dsr/checkIn", method = RequestMethod.POST)
	public Map<String, Object> syncCheckInData(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	@RequestMapping(value = "/dsr/syncVisit", method = RequestMethod.POST)
	public Map<String, Object> syncVisit(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	@RequestMapping(value = "/dsr/chk/updates", method = RequestMethod.POST)
	public Map<String, Object> checkUpdates(SurveyorType appType, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	@RequestMapping(value = "/dsr/sale-summary", method = RequestMethod.GET)
	public ArrayList<SummaryTag> dsrSaleSummary(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, String date) throws ServletException,
			IOException;
	
	@RequestMapping(value = "/dsr/stock", method = RequestMethod.POST)
	public Map<String, Object> dsrStock(Integer dsrId,
			HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException;
	
	@RequestMapping(value = "/dsr/day-end", method = RequestMethod.POST)
	public Map<String, Object> syncDayEndData(String version, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
	
	@RequestMapping(value = "/dsr/previous-three-visits", method = RequestMethod.POST)
	public List<Map<String, Object>> getShopPreviousThreeVisits(HttpServletRequest request,
			HttpServletResponse response, Integer shopId) throws ServletException, IOException;
	
	@RequestMapping(value = "/dsr/summary", method = RequestMethod.GET)
	public ArrayList<SummaryTag> getDsrSummary(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, String date, String type) throws ServletException,
			IOException;

	@RequestMapping(value = "/dsr/promotions", method = RequestMethod.GET)
	public List<Map<String, Object>> getPromotionlist(HttpServletRequest request,
			HttpServletResponse response,Integer dsrId, Integer shopId) throws ServletException, IOException;
	
	@RequestMapping(value = "/dsr/stockList", method = RequestMethod.POST)
	public Map<String, Object> getStockList(HttpServletRequest request,
			HttpServletResponse response,Integer dsrId,Integer cityId) throws ServletException, IOException;
	
	@RequestMapping(value = "/dsr/brand-wise-sale-summary", method = RequestMethod.GET)
	public ArrayList<SummaryTag> dsrBrandWiseSaleSummary(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, String brandName) throws ServletException,
			IOException;
	
	@RequestMapping(value = "/app/update-profile", method = RequestMethod.POST)
	public Map<String, Object> updateProfile(HttpServletRequest request,
			HttpServletResponse response);
	
	@RequestMapping(value = "/dsr/areas-list", method = RequestMethod.GET)
	public List<Area> getDsrAreas(Integer dsrId, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
	
	@RequestMapping(value = "/dsr/tgt-vs-ach", method = RequestMethod.GET)
	public ArrayList<SummaryTag> getTgtVsAch(HttpServletRequest request,
			HttpServletResponse respons, Integer dsrId, String type);
	
	@RequestMapping(value = "/dsr-file" , method = RequestMethod.POST)
	public Map<String, Object> moveFiles(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
}
