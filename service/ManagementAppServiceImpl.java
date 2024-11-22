package com.marksman.census.service;

import com.marksman.census.bo.AdminProfile;
import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.dao.ChannelsDao;
import com.marksman.census.dao.ManagementAppDao;
import com.marksman.census.dao.PortalDao;
import com.marksman.census.message.MessageType;
import com.marksman.census.util.CommonUtil;
import com.marksman.census.util.PasswordStorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class ManagementAppServiceImpl implements ManagementAppService {
	protected Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	ManagementAppDao managementappDao;
	@Autowired
	ApplicationCacheService applicationCacheService;
	@Autowired
	ChannelsDao channelsDao;
	@Autowired
	PortalDao portalDao;
	@Autowired
	PortalService portalService;
	ClientLoggingServiceImpl clientLoggingService;
	@Autowired
	private ValidationService validationService;

	public Map<String, Object> getbasicData(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> data = new HashMap<String, Object>();

		try {
			data.put("zones", this.managementappDao.getZone());
			data.put("regions", this.managementappDao.getRegion());
			data.put("asmList", this.managementappDao.getCities());
			data.put("channels", this.managementappDao.getChannels());
			data.put("categoryList", this.managementappDao.getFamiies());
			return data;
		} catch (Exception e) {

			this.logger.error(e, e);
			CommonUtil.writeErrorMessage(500, MessageType.ERROR_SERVER,
					response);
			return null;
		}
	}

	public Map<String, Object> instoLogin(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		StringBuilder sb = new StringBuilder();

		try {
			try {
				BufferedReader reader = request.getReader();

				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append('\n');
				}

				JSONObject json = new JSONObject(sb.toString());
				String username = json.getString("userName");
				String password = json.getString("password");

				try {
					AdminProfile adminProfile = this.portalDao
							.getPasswordHashByUserName(username, 0);
					String passwordHash = adminProfile.getPassword();
					if (adminProfile != null
							&& adminProfile.getStatus().equalsIgnoreCase("N")) {
						if (PasswordStorage.verifyPassword(password,
								passwordHash)) {
							CommonUtil.writeErrorMessage(500,
									MessageType.USER_DISABLED, response);
						} else {
							CommonUtil.writeErrorMessage(500,
									MessageType.INVALID_CREDENTIALS, response);
						}
					} else if (!passwordHash.isEmpty() && passwordHash != null) {
						try {
							if (PasswordStorage.verifyPassword(password,
									passwordHash)) {
								res = managementappDao
										.getAdminProfileInfo(username);
							} else {
								CommonUtil.writeErrorMessage(500,
										MessageType.INVALID_CREDENTIALS,
										response);
							}
						} catch (Exception e) {
							logger.error(e, e);
						}
					} else {
						CommonUtil.writeErrorMessage(500,
								MessageType.INVALID_CREDENTIALS, response);
					}
				} catch (Exception e) {
					this.logger.error("Error occurred during login: ", e);
					CommonUtil.writeErrorMessage(500,
							MessageType.INVALID_CREDENTIALS, response);
				}

				return res.get(0);
			} catch (JSONException e) {
				logger.error(e, e);
				CommonUtil.writeErrorMessage(500,
						MessageType.INVALID_CREDENTIALS, response);
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		return res.get(0);
	}

	public List<Map<String, Object>> getPrimarySalesMonthWise(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException {

		StringBuilder sb = new StringBuilder();
		int zoneId = -1;
		int regionId = -1;
		int month = -1;
		int year = -1;
		String type = null;

		try {
			BufferedReader reader = request.getReader();

			while (true) {
				String line;
				if ((line = reader.readLine()) == null) {
					JSONObject json = new JSONObject(sb.toString());
					logger.info(json);
					if (json.has("zoneId")) {
						zoneId = json.getInt("zoneId");
					} else {
						logger.warn("JSON object does not contain 'zoneId'");
					}

					if (json.has("regionId")) {
						regionId = json.getInt("regionId");
					} else {
						logger.warn("JSON object does not contain 'regionId'");
					}

					if (json.has("month")) {
						month = json.getInt("month");
					} else {
						logger.warn("JSON object does not contain 'month'");
					}

					if (json.has("year")) {
						year = json.getInt("year");
					} else {
						logger.warn("JSON object does not contain 'year'");
					}

					if (json.has("type")) {
						type = json.getString("type");
					} else {
						logger.warn("JSON object does not contain 'type'");
					}

					break;
				}

				sb.append(line).append('\n');
			}
		} catch (JSONException e) {
			logger.error(e, e);
		}
		List<Map<String, Object>> ap = this.managementappDao
				.getPrimarySalesMonthWise(zoneId, regionId, month, year,type);
		return ap;
	}

	public List<Map<String, Object>> getSecondarySalesMonthWise(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException {

		StringBuilder sb = new StringBuilder();
		int zoneId = -1;
		int regionId = -1;
		int month = -1;
		int year = -1;
		String type = null;

		try {
			BufferedReader reader = request.getReader();

			while (true) {
				String line;
				if ((line = reader.readLine()) == null) {
					JSONObject json = new JSONObject(sb.toString());
					logger.info(json);
					if (json.has("zoneId")) {
						zoneId = json.getInt("zoneId");
					} else {
						logger.warn("JSON object does not contain 'zoneId'");
					}

					if (json.has("regionId")) {
						regionId = json.getInt("regionId");
					} else {
						logger.warn("JSON object does not contain 'regionId'");
					}

					if (json.has("month")) {
						month = json.getInt("month");
					} else {
						logger.warn("JSON object does not contain 'month'");
					}

					if (json.has("year")) {
						year = json.getInt("year");
					} else {
						logger.warn("JSON object does not contain 'year'");
					}

					if (json.has("type")) {
						type = json.getString("type");
					} else {
						logger.warn("JSON object does not contain 'type'");
					}

					break;
				}

				sb.append(line).append('\n');
			}
		} catch (JSONException e) {
			logger.error(e, e);
		}

		List<Map<String, Object>> ap = this.managementappDao
				.getSecondarySalesMonthWise(zoneId, regionId, month, year,type);
		return ap;
	}

	public List<Map<String, Object>> getPrimarySalesTrends(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException {

		StringBuilder sb = new StringBuilder();
		int zoneId = -1;
		int regionId = -1;

		try {
			BufferedReader reader = request.getReader();

			while (true) {
				String line;
				if ((line = reader.readLine()) == null) {
					JSONObject json = new JSONObject(sb.toString());
					logger.info(json);
					if (json.has("zoneId")) {
						zoneId = json.getInt("zoneId");
					}
					if (json.has("regionId")) {
						zoneId = json.getInt("regionId");
					}
					break;
				}

				sb.append(line).append('\n');
			}
		} catch (JSONException e) {
			logger.error(e, e);
		}

		List<Map<String, Object>> ap = this.managementappDao
				.getPrimarySalesTrends(zoneId, regionId);
		return ap;
	}

	public List<Map<String, Object>> getSecondarySalesTrends(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException {

		StringBuilder sb = new StringBuilder();
		int zoneId = -1;
		int regionId = -1;

		try {
			BufferedReader reader = request.getReader();

			while (true) {
				String line;
				if ((line = reader.readLine()) == null) {
					JSONObject json = new JSONObject(sb.toString());
					logger.info(json);
					if (json.has("zoneId")) {
						zoneId = json.getInt("zoneId");
					}
					if (json.has("regionId")) {
						zoneId = json.getInt("regionId");
					}
					break;
				}

				sb.append(line).append('\n');
			}
		} catch (JSONException e) {
			logger.error(e, e);
		}

		List<Map<String, Object>> ap = this.managementappDao
				.getSecondarySalesTrends(zoneId, regionId);
		return ap;
	}

	public List<Map<String, Object>> getPrimaryAchievementTrends(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException {

		StringBuilder sb = new StringBuilder();
		int zoneId = -1;
		int regionId = -1;

		try {
			BufferedReader reader = request.getReader();

			while (true) {
				String line;
				if ((line = reader.readLine()) == null) {
					JSONObject json = new JSONObject(sb.toString());
					logger.info(json);
					if (json.has("zoneId")) {
						zoneId = json.getInt("zoneId");
					}
					if (json.has("regionId")) {
						zoneId = json.getInt("regionId");
					}
					break;
				}

				sb.append(line).append('\n');
			}
		} catch (JSONException e) {
			logger.error(e, e);
		}

		List<Map<String, Object>> ap = this.managementappDao
				.getPrimaryAchievementTrends(zoneId, regionId);
		return ap;
	}

	public List<Map<String, Object>> getSecondaryAchievementTrends(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException {

		StringBuilder sb = new StringBuilder();
		int zoneId = -1;
		int regionId = -1;

		try {
			BufferedReader reader = request.getReader();

			while (true) {
				String line;
				if ((line = reader.readLine()) == null) {
					JSONObject json = new JSONObject(sb.toString());
					logger.info(json);
					if (json.has("zoneId")) {
						zoneId = json.getInt("zoneId");
					}
					if (json.has("regionId")) {
						zoneId = json.getInt("regionId");
					}
					break;
				}

				sb.append(line).append('\n');
			}
		} catch (JSONException e) {
			logger.error(e, e);
		}

		List<Map<String, Object>> ap = this.managementappDao
				.getSecondaryAchievementTrends(zoneId, regionId);
		return ap;
	}

	@Override
	public List<Map<String, Object>> getDsrMonthwiseproductivity(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException {
		StringBuilder sb = new StringBuilder();
		int zoneId = -1;
		int regionId = -1;
		int month = -1;
		int year = -1;
		String type = null;

		try {
			BufferedReader reader = request.getReader();

			while (true) {
				String line;
				if ((line = reader.readLine()) == null) {
					JSONObject json = new JSONObject(sb.toString());
					logger.info(json);
					if (json.has("zoneId")) {
						zoneId = json.getInt("zoneId");
					} else {
						logger.warn("JSON object does not contain 'zoneId'");
					}

					if (json.has("regionId")) {
						regionId = json.getInt("regionId");
					} else {
						logger.warn("JSON object does not contain 'regionId'");
					}

					if (json.has("month")) {
						month = json.getInt("month");
					} else {
						logger.warn("JSON object does not contain 'month'");
					}

					if (json.has("year")) {
						year = json.getInt("year");
					} else {
						logger.warn("JSON object does not contain 'year'");
					}

					if (json.has("type")) {
						type = json.getString("type");
					} else {
						logger.warn("JSON object does not contain 'type'");
					}

					break;
				}

				sb.append(line).append('\n');
			}
		} catch (JSONException e) {
			logger.error(e, e);
		}
		List<Map<String, Object>> data = null;
		data = managementappDao.getMonthwiseDsrproductivity(zoneId, regionId,
				month, year, type);

		return data;
	}

	@Override
	public List<Map<String, Object>> getDeMonthwiseproductivity(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException {
		StringBuilder sb = new StringBuilder();
		int zoneId = -1;
		int regionId = -1;
		int month = -1;
		int year = -1;
		String type = null;

		try {
			BufferedReader reader = request.getReader();

			while (true) {
				String line;
				if ((line = reader.readLine()) == null) {
					JSONObject json = new JSONObject(sb.toString());
					logger.info(json);
					if (json.has("zoneId")) {
						zoneId = json.getInt("zoneId");
					} else {
						logger.warn("JSON object does not contain 'zoneId'");
					}

					if (json.has("regionId")) {
						regionId = json.getInt("regionId");
					} else {
						logger.warn("JSON object does not contain 'regionId'");
					}

					if (json.has("month")) {
						month = json.getInt("month");
					} else {
						logger.warn("JSON object does not contain 'month'");
					}

					if (json.has("year")) {
						year = json.getInt("year");
					} else {
						logger.warn("JSON object does not contain 'year'");
					}

					if (json.has("type")) {
						type = json.getString("type");
					} else {
						logger.warn("JSON object does not contain 'type'");
					}

					break;
				}

				sb.append(line).append('\n');
			}
		} catch (JSONException e) {
			logger.error(e, e);
		}
		List<Map<String, Object>> data = null;
		data = managementappDao.getDeMonthwiseproductivity(zoneId, regionId,
				month, year, type);
		return data;
	}

	@Override
	public List<Map<String, Object>> getTmMonthwiseproductivity(
			HttpServletRequest request, HttpServletResponse response)
					throws IOException {
		StringBuilder sb = new StringBuilder();
		int zoneId = -1;
		// int regionId = -1;
		int month = -1;
		int year = -1;
		String type = null;

		try {
			BufferedReader reader = request.getReader();

			while (true) {
				String line;
				if ((line = reader.readLine()) == null) {
					JSONObject json = new JSONObject(sb.toString());
					logger.info(json);
					if (json.has("zoneId")) {
						zoneId = json.getInt("zoneId");
					} else {
						logger.warn("JSON object does not contain 'zoneId'");
					}
					// if (json.has("regionId")) {
					// regionId = json.getInt("regionId");
					// }
					if (json.has("month")) {
						month = json.getInt("month");
					} else {
						logger.warn("JSON object does not contain 'month'");
					}

					if (json.has("year")) {
						year = json.getInt("year");
					} else {
						logger.warn("JSON object does not contain 'year'");
					}

					if (json.has("type")) {
						type = json.getString("type");
					} else {
						logger.warn("JSON object does not contain 'type'");
					}

					break;
				}

				sb.append(line).append('\n');
			}
		} catch (JSONException e) {
			logger.error(e, e);
		}
		List<Map<String, Object>> data = null;
		data = managementappDao.getTmMonthwiseproductivity(zoneId, month, year,
				type);
		return data;
	}

	@Override
	public List<Map<String, Object>> getDsrProductivityTrends(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int zoneId = -1;
	    int regionId = -1;

	    try (BufferedReader reader = request.getReader()) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line).append('\n');
	        }

	        String jsonString = sb.toString().trim();
	        logger.info("Received JSON: {}"+ jsonString);

	        JSONObject json = new JSONObject(jsonString);

	        if (json.has("zoneId")) {
	            zoneId = json.getInt("zoneId");
	        } else {
	            logger.warn("JSON object does not contain 'zoneId'");
	        }

	        if (json.has("regionId")) {
	            regionId = json.getInt("regionId");
	        } else {
	            logger.warn("JSON object does not contain 'regionId'");
	        }
	    } catch (JSONException e) {
	        logger.error("JSON Parsing error", e);
	    }

	    return managementappDao.getDsrProductivityTrends(zoneId, regionId);
	}


	@Override
	public List<Map<String, Object>> getDeProductivityTrends(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int zoneId = -1;
	    int regionId = -1;

	    try (BufferedReader reader = request.getReader()) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line).append('\n');
	        }

	        String jsonString = sb.toString().trim();
	        logger.info("Received JSON: {}"+ jsonString);

	        JSONObject json = new JSONObject(jsonString);

	        if (json.has("zoneId")) {
	            zoneId = json.getInt("zoneId");
	        } else {
	            logger.warn("JSON object does not contain 'zoneId'");
	        }

	        if (json.has("regionId")) {
	            regionId = json.getInt("regionId");
	        } else {
	            logger.warn("JSON object does not contain 'regionId'");
	        }
	    } catch (JSONException e) {
	        logger.error("JSON Parsing error", e);
	    }

	    return managementappDao.getDeProductivityTrends(zoneId, regionId);
	}


	@Override
	public List<Map<String, Object>> getTmProductivityTrends(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int zoneId = -1;

	    try (BufferedReader reader = request.getReader()) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line).append('\n');
	        }

	        String jsonString = sb.toString().trim();
	        logger.info("Received JSON: {}"+ jsonString);

	        JSONObject json = new JSONObject(jsonString);

	        if (json.has("zoneId")) {
	            zoneId = json.getInt("zoneId");
	        } else {
	            logger.warn("JSON object does not contain 'zoneId'");
	        }
	    } catch (JSONException e) {
	        logger.error("JSON Parsing error", e);
	    }

	    return managementappDao.getTmProductivityTrends(zoneId);
	}


}