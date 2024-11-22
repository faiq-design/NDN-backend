package com.marksman.census.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.marksman.census.bo.Area;
import com.marksman.census.bo.BrandPriceDetails;
import com.marksman.census.bo.Surveyor;
import com.marksman.census.client.bo.ShopRoute;
import com.marksman.census.client.bo.SummaryTag;
import com.marksman.census.constants.SurveyorType;
import com.marksman.census.constants.SysConstants;
import com.marksman.census.message.MessageType;
import com.marksman.census.service.ClientLoggingServiceImpl;
import com.marksman.census.service.DsrService;
import com.marksman.census.service.ValidationService;
import com.marksman.census.util.CommonUtil;

@Controller
public class DsrControllerImpl implements DsrController {

	@Autowired
	DsrService dsrService;
	@Autowired
	ClientLoggingServiceImpl clientLoggingService;
	@Autowired
	private ValidationService validationService;

	protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	@ResponseBody
	public Map<String, Object> dsrLogin(String imei, String dCode,
			String password, HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> jsonMap = null;
		String version = request.getHeader(SysConstants.VERSION);
		logger.info("login request against imei : " + imei + ", dsr code : "
				+ dCode + " dsr app version " + version);
		clientLoggingService
				.insertActivityLog(request, response, "DsrAppLogin");

		if (validationService.validateDsr(dCode, password, SurveyorType.DSR,
				imei, response)) {
			dsrService.updateForceLoginStatus(dCode);
			jsonMap = dsrService.login(imei, dCode, version, request,  response);
		}

		return jsonMap;

	}

	@Override
	@ResponseBody
	public List<ShopRoute> dsrShopList(Integer routeId, Integer dsrId,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		clientLoggingService
				.insertActivityLog(request, response, "DsrShopList");
		String version = request.getHeader(SysConstants.VERSION);
		logger.info("shopList Version: "+version);
		return dsrService.dsrShopList(routeId, dsrId, version, response);

	}
	
	@Override
	@ResponseBody
	public List<Area> getDsrAreas( Integer dsrId,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		clientLoggingService
				.insertActivityLog(request, response, "AreasList");
		return dsrService.getDsrAreas( dsrId, response);

	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getDsrList(HttpServletRequest request,
			HttpServletResponse response, Integer deId) {
		clientLoggingService.insertActivityLog(request, response, "DsrList");
		return dsrService.getDsrList(deId);
	}

	@Override
	@ResponseBody
	public Map<String, Object> syncVisit(String version,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		clientLoggingService.insertActivityLog(request, response,
				"SyncDsrVisit");
		Map<String, Object> jsonMap = dsrService.syncVisit(version, request,
				response);
		return jsonMap;
	}

	@Override
	@ResponseBody
	public Map<String, Object> syncCheckInData(String version,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		clientLoggingService.insertActivityLog(request, response,
				"SyncDsrCheckInData");
		Map<String, Object> jsonMap = dsrService.syncCheckInData(version,
				request, response);
		return jsonMap;
	}

	@Override
	@ResponseBody
	public Map<String, Object> checkUpdates(SurveyorType appType,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		return dsrService.checkUpdates(appType, request, response);
	}

	@Override
	@ResponseBody
	public ArrayList<SummaryTag> dsrSaleSummary(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, String date)
			throws ServletException, IOException {
		String version = request.getHeader(SysConstants.VERSION);
		logger.info("Summary Version: "+version);
		return dsrService.dsrSaleSummary(request, response, dsrId, date, version);
	}

	@Override
	@ResponseBody
	public Map<String, Object> dsrStock(Integer dsrId,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> jsonMap = null;
		String version = request.getHeader(SysConstants.VERSION);
		jsonMap = dsrService.dsrStock(dsrId, version, request, response);

		return jsonMap;

	}

	@Override
	@ResponseBody
	public Map<String, Object> syncDayEndData(String version,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		clientLoggingService.insertActivityLog(request, response,
				"SyncDsrDayEndData");
		Map<String, Object> jsonMap = dsrService.syncDayEndData(version,
				request, response);
		return jsonMap;
	}

	@Override
	@ResponseBody
	public List<Map<String, Object>> getShopPreviousThreeVisits(
			HttpServletRequest request, HttpServletResponse response,
			Integer shopId) throws ServletException, IOException {
		clientLoggingService.insertActivityLog(request, response,
				"PREVIOUS_THREE_VISITS");
		List<Map<String, Object>> jsonMap = dsrService
				.getShopPreviousThreeVisits(request, response, shopId);
		return jsonMap;
	}

	@Override
	@ResponseBody
	public ArrayList<SummaryTag> getDsrSummary(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, String date,
			String type) throws ServletException, IOException {
		return dsrService.getDsrSummary(request, response, dsrId, date, type);
	}
	
	@Override
	@ResponseBody
	public List<Map<String, Object>> getPromotionlist(HttpServletRequest request, HttpServletResponse response, Integer dsrId, Integer shopId)
			throws ServletException, IOException {
		return dsrService.promotionList(request, response, dsrId, shopId);
	}
	
	@Override
	@ResponseBody
	public Map<String, Object> getStockList(
			HttpServletRequest request, HttpServletResponse response,Integer dsrId,Integer cityId) {

		Map<String, Object> jsonMap = null;
		String version = request.getHeader(SysConstants.VERSION);
		String appType = request.getHeader("appType");
		jsonMap = dsrService.stockList( request, response,dsrId,cityId,version,appType);

		return jsonMap;

	}
	
	@Override
	@ResponseBody
	public ArrayList<SummaryTag> dsrBrandWiseSaleSummary(HttpServletRequest request,
			HttpServletResponse response, Integer dsrId, String brandName )
			throws ServletException, IOException {
		String version = request.getHeader(SysConstants.VERSION);
		logger.info("Extend Summary: "+version);
		return dsrService.dsrBrandWiseSaleSummary(request, response, dsrId, brandName, version);
	}
	
	@Override
	@ResponseBody
	public Map<String, Object> updateProfile(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> responMap = new HashMap<String, Object>();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
			Gson gson = new Gson();
			Surveyor updateProfile = null;
			if (isMultiPart) {
				ServletFileUpload upload = new ServletFileUpload();
				try {

					FileItemIterator itr = upload.getItemIterator(request);
					while (itr.hasNext()) {
						FileItemStream item = itr.next();
						if (item.isFormField()) {
							InputStream stream = item.openStream();
							String reader = Streams.asString(stream);
							updateProfile = gson.fromJson(reader,
									Surveyor.class);
						}
					}
				} catch (Exception ex) {
					logger.error("Error while adding brand Price detail  : ",
							ex);
					CommonUtil.writeErrorMessage(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							MessageType.ERROR_SERVER, response);
					logger.info("Error :", ex);
					return null;
				}

				if (updateProfile != null) {
					try {
						// portalLoggingService.insertActivityLog(request,
						// response, "INSERT_BRAND_PRICE");
						int updateProfileDetail = dsrService
								.updateProfile(updateProfile);
						data.put("surveyor", updateProfile);
						responMap.put("data", data);
					} catch (IllegalStateException ex) {
						logger.error("Error", ex);
						responMap.put("status", false);
						responMap
								.put("description", "Something Went Wrong....");
					} catch (Exception e) {
						logger.error("Error", e);
						responMap.put("status", false);
						responMap.put("description",
								"Something Went Wrong....!!");
					}

				}

			} else {

				logger.error("Data is not multi part ");
				CommonUtil.writeErrorMessage(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						MessageType.ERROR_FORMAT_DATA, response);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e, e);
			CommonUtil.writeErrorMessage(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					MessageType.FILE_SAVING_SERVER, response);
			return null;
		}

		return responMap;
	}
	
	@Override
	@ResponseBody
	public ArrayList<SummaryTag> getTgtVsAch(HttpServletRequest request,
			HttpServletResponse respons, Integer dsrId, String type) {

		return dsrService.getTgtVsAch(dsrId, type);

	}
	
	@Override
	@ResponseBody
	public Map<String, Object> moveFiles(HttpServletRequest request,
			HttpServletResponse response) throws  ServletException, IOException {
		Map<String, Object> jsonMap = dsrService.mvFiles(request, response);
		return jsonMap;
	}

}
