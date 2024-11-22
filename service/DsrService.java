package com.marksman.census.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.marksman.census.bo.Area;
import com.marksman.census.bo.BrandPriceDetails;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.client.bo.ShopRoute;
import com.marksman.census.client.bo.SummaryTag;
import com.marksman.census.constants.SurveyorType;

public interface DsrService
{

	public Map<String, Object> login(String imei, String dCode,String version, HttpServletRequest request,
			HttpServletResponse response);

	public List<ShopRoute> dsrShopList(Integer routeId, Integer dsrId, String version, HttpServletResponse response);

	public List<Map<String, Object>> getDsrList(Integer deId);

	public Map<String, Object> syncVisit(String version, HttpServletRequest request,
			HttpServletResponse response);

	public Map<String, Object> syncCheckInData(String version, HttpServletRequest request,
			HttpServletResponse response);

	public Map<String, Object> checkUpdates(SurveyorType appType, HttpServletRequest request,
			HttpServletResponse response);

	public ArrayList<SummaryTag> dsrSaleSummary(HttpServletRequest request, HttpServletResponse response,
			Integer dsrId, String date, String version);
	
	public Map<String, Object> dsrStock(Integer dsrId, String version, HttpServletRequest request,
			HttpServletResponse response);
	
	public Map<String, Object> stockList( HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, Integer cityId, String version, String appType);
	
	public Map<String, Object> syncDayEndData(String version, HttpServletRequest request,
			HttpServletResponse response);
	
	public List<Map<String, Object>> getShopPreviousThreeVisits(HttpServletRequest request,
			HttpServletResponse response, Integer shopId);
	
	public ArrayList<SummaryTag> getDsrSummary(HttpServletRequest request, HttpServletResponse response,
			Integer dsrId, String date, String type);

	public void updateForceLoginStatus(String dCode);
	
	public List<Map<String, Object>> promotionList(HttpServletRequest request,
			HttpServletResponse response,Integer dsrId, Integer shopId);
	
	public ArrayList<SummaryTag> dsrBrandWiseSaleSummary(HttpServletRequest request, HttpServletResponse response,
			Integer dsrId, String brandName, String version);
	
	public int updateProfile(Surveyor updateProfile)
			throws Exception;
	public List<Area> getDsrAreas( Integer dsrId, HttpServletResponse response);
	public ArrayList<SummaryTag> getTgtVsAch(int dsrId, String type);
	
	public Map<String, Object> mvFiles(HttpServletRequest request,
			HttpServletResponse response) ;
	
	

}
