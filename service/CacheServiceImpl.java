package com.marksman.census.service;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.marksman.census.cache.ApplicationCacheService;
import com.marksman.census.constants.CommonConstants;
import com.marksman.census.constants.SysConstants;
import com.marksman.census.dao.AreaDao;
import com.marksman.census.dao.AssetsDao;
import com.marksman.census.dao.BwusDao;
import com.marksman.census.dao.ChannelsDao;
import com.marksman.census.dao.CitiesDao;
import com.marksman.census.dao.DistributionDao;
import com.marksman.census.dao.DsrDao;
import com.marksman.census.dao.FamiliesDao;
import com.marksman.census.dao.IndustriesDao;
import com.marksman.census.dao.ProductsDao;
import com.marksman.census.dao.QuestionsDao;
import com.marksman.census.dao.RegionsDao;
import com.marksman.census.dao.RemarksDao;
import com.marksman.census.dao.ShopCategoryDao;
import com.marksman.census.dao.ShopGroupsDao;
import com.marksman.census.dao.SurveyorDao;
import com.marksman.census.dao.SystemDao;
import com.marksman.census.dao.TsoDao;
import com.marksman.census.dao.ZonesDao;

public class CacheServiceImpl implements CacheService {

	protected final Logger logger = Logger.getLogger(this.getClass());
	private ResourceBundle bundle = ResourceBundle
			.getBundle(CommonConstants.PROPERTY_FILE_NAME);

	@Autowired
	private SystemDao systemDao;

	@Autowired
	private CitiesDao citiesDao;

	@Autowired
	private RegionsDao regionsDao;

	@Autowired
	private ZonesDao zonesDao;

	@Autowired
	private QuestionsDao questionsDao;

	@Autowired
	private RemarksDao remarksDao;

	@Autowired
	private ShopCategoryDao shopCategoryDao;

	@Autowired
	private ShopGroupsDao shopGroupsDao;

	@Autowired
	private IndustriesDao industriesDao;

	@Autowired
	private FamiliesDao familiesDao;

	@Autowired
	private ProductsDao productsDao;

	@Autowired
	private BwusDao bwusDao;

	@Autowired
	private ChannelsDao channelsDao;

	@Autowired
	private AssetsDao assetsDao;

	@Autowired
	private DistributionDao distributionsDao;

	@Autowired
	private SurveyorDao surveyorsDao;

	@Autowired
	private TsoDao tsoDao;

	@Autowired
	private AreaDao areaDao;

	@Autowired
	private DsrDao dsrDao;

	@Autowired
	private ApplicationCacheService applicationCacheService;

	public void buildCache() {

		try {

			buildVersionsCache();
			buildCitiesCache();
			buildRegionsCache();
			buildZonesCache();
			buildQuestionsCache();
			buildRemarksCache();
			buildProductsCache();
			buildIndustriesCache();
			buildSisIndustriesCache();
			buildSisIndustryBwuCache();
            buildBanksCache();
			if (SysConstants.PMI.equalsIgnoreCase(bundle.getString(
					SysConstants.PROJECT).trim())) {

				buildCategoriesCache();
				buildGroupsCache();
				buildRemarkTypesCache();
				buildFamiliesCache();
				buildBwuCache();
				buildIndustryBwuCache();

			} else if (SysConstants.MONDELEZ.equalsIgnoreCase(bundle.getString(
					SysConstants.PROJECT).trim())) {

				buildProductDisplayCache();
				buildProductCategoriesCache();
				buildChannelsCache();
				buildChannelProductsCache();
				buildPosmCache();
				buildAssetsCache();
				buildDistributionCache();
				buildDsrCache();
				buildTsoCache();
				buildAreaCache();
			} else if (SysConstants.NDN.equalsIgnoreCase(bundle.getString(
					SysConstants.PROJECT).trim())
					|| SysConstants.PMI_WWWR.equalsIgnoreCase(bundle.getString(
							SysConstants.PROJECT).trim())) {

				buildGroupsCache();
				buildFamiliesCache();
				buildSisFamiliesCache();
				buildQuestionTypesCache();
				buildOptionsCache();
				buildQuestionOptionsCache();
				buildBwuCache();
				buildIndustryBwuCache();
				buildRegionsCache();
				buildAreaCache();
				buildZonesCache();
				/* buildRegionsCache(); */
				// buildGroupsCache();
				buildDsrRoutesCache();
				buildDsrIndustryBwuCache();
				buildDsrCache();
			}
		} catch (Exception e) {

			logger.error("Error while buidling cache.", e);
		}
	}

	public void buildAreaCache() throws Exception {
		try {
			applicationCacheService.setAreasMap(areaDao.loadAreas());
		} catch (Exception e) {

			logger.error("Error while buidling builds cache.", e);
			e.printStackTrace();
		}
	}

	public void buildDistributionCache() throws Exception {
		try {
			applicationCacheService.setDistributionMap(distributionsDao
					.getDistributions());
		} catch (Exception e) {

			logger.error("Error while buidling builds cache.", e);
			e.printStackTrace();
		}
	}

	public void buildDsrCache() throws Exception {
		try {
			applicationCacheService.setDsrMap(surveyorsDao.getDsrs());
		} catch (Exception e) {

			logger.error("Error while buidling builds cache.", e);
			e.printStackTrace();
		}
	}

	public void buildTsoCache() throws Exception {
		try {
			applicationCacheService.setTsoMap(tsoDao.getTso());
		} catch (Exception e) {

			logger.error("Error while buidling builds cache.", e);
			e.printStackTrace();
		}
	}

	@Override
	public void buildVersionsCache() throws Exception {

		try {
			applicationCacheService.setBuildsMap(systemDao.getBuilds());
		} catch (Exception e) {

			logger.error("Error while buidling builds cache.", e);
		}
	}

	@Override
	public void buildCitiesCache() throws Exception {

		try {
			applicationCacheService.setCitiesMap(citiesDao.getCities());
		} catch (Exception e) {

			logger.error("Error while buidling cities cache.", e);
		}
	}

	@Override
	public void buildRegionsCache() throws Exception {

		try {
			applicationCacheService.setRegionsMap(regionsDao.getRegions());
		} catch (Exception e) {

			logger.error("Error while buidling regions cache.", e);
		}
	}

	@Override
	public void buildZonesCache() throws Exception {

		try {
			applicationCacheService.setZonesMap(zonesDao.getZones());
		} catch (Exception e) {

			logger.error("Error while buidling zones cache.", e);
		}
	}

	@Override
	public void buildCategoriesCache() throws Exception {

		try {
			applicationCacheService.setCategoriesMap(shopCategoryDao
					.getShopsCategories());
		} catch (Exception e) {

			logger.error("Error while buidling category cache.", e);
		}
	}

	@Override
	public void buildGroupsCache() throws Exception {

		try {
			applicationCacheService
					.setGroupsMap(shopGroupsDao.getShopsGroups());
		} catch (Exception e) {

			logger.error("Error while buidling groups cache.", e);
		}
	}

	@Override
	public void buildQuestionTypesCache() throws Exception {

		try {
			applicationCacheService.setQuestionTypesMap(questionsDao
					.getQuestionTypes());
		} catch (Exception e) {

			logger.error("Error while buidling questions cache.", e);
		}
	}

	@Override
	public void buildQuestionsCache() throws Exception {

		try {
			applicationCacheService.setQuestionsMap(SysConstants.PMI_WWWR
					.equalsIgnoreCase(bundle.getString(SysConstants.PROJECT)
							.trim()) ? questionsDao.getQuestionsMap()
					: questionsDao.getQuestions());
		} catch (Exception e) {

			logger.error("Error while buidling questions cache.", e);
		}
	}

	@Override
	public void buildOptionsCache() throws Exception {

		try {
			applicationCacheService.setOptionsMap(questionsDao.getOptions());
		} catch (Exception e) {

			logger.error("Error while buidling options cache.", e);
		}
	}

	@Override
	public void buildQuestionOptionsCache() throws Exception {

		try {
			applicationCacheService.setQuestionOptionsMap(questionsDao
					.getQuestionOptions());
		} catch (Exception e) {

			logger.error("Error while buidling question options cache.", e);
		}
	}

	@Override
	public void buildRemarksCache() throws Exception {

		try {
			applicationCacheService.setRemarksMap(remarksDao.getRemarks());
		} catch (Exception e) {

			logger.error("Error while buidling remarks cache.", e);
		}
	}

	@Override
	public void buildRemarkTypesCache() throws Exception {

		try {
			applicationCacheService.setRemarkTypesMap(remarksDao
					.getRemarksTypes());
		} catch (Exception e) {

			logger.error("Error while buidling remarks type cache.", e);
		}
	}

	@Override
	public void buildIndustriesCache() throws Exception {

		try {
			applicationCacheService.setIndustriesMap(industriesDao
					.getIndustries());
		} catch (Exception e) {

			logger.error("Error while buidling industries cache.", e);
		}
	}

	@Override
	public void buildSisIndustriesCache() throws Exception {

		try {
			applicationCacheService.setSisIndustriesMap(industriesDao
					.getSisIndustries());
		} catch (Exception e) {

			logger.error("Error while buidling industries cache.", e);
		}
	}

	@Override
	public void buildFamiliesCache() throws Exception {

		try {
			applicationCacheService.setFamiliesMap(familiesDao.getFamilies());
		} catch (Exception e) {

			logger.error("Error while buidling families cache.", e);
		}
	}

	@Override
	public void buildSisFamiliesCache() throws Exception {

		try {
			applicationCacheService.setSisFamiliesMap(familiesDao
					.getSisFamilies());
		} catch (Exception e) {

			logger.error("Error while buidling families cache.", e);
		}
	}

	@Override
	public void buildChannelsCache() throws Exception {

		try {
			applicationCacheService.setChannelsMap(channelsDao.getChannels());
		} catch (Exception e) {

			logger.error("Error while buidling channels cache.", e);
		}
	}

	@Override
	public void buildAssetsCache() throws Exception {

		try {
			applicationCacheService.setAssetsMap(assetsDao.getAssets());
		} catch (Exception e) {

			logger.error("Error while buidling assets cache.", e);
		}
	}

	@Override
	public void buildChannelProductsCache() throws Exception {

		try {
			applicationCacheService.setChannelProductsMap(channelsDao
					.getChannelProducts());
		} catch (Exception e) {

			logger.error("Error while buidling channel product cache.", e);
		}
	}

	@Override
	public void buildProductCategoriesCache() throws Exception {

		try {
			applicationCacheService.setProductCategroiesMap(productsDao
					.getProductCategories());
		} catch (Exception e) {

			logger.error("Error while buidling product categories cache.", e);
		}
	}

	@Override
	public void buildProductDisplayCache() throws Exception {

		try {
			applicationCacheService.setProductDisplayMap(productsDao
					.getProductDisplay());
		} catch (Exception e) {

			logger.error("Error while buidling product display cache.", e);
		}
	}

	@Override
	public void buildProductsCache() throws Exception {

		try {
			applicationCacheService.setProductsMap(productsDao.getProducts());
			applicationCacheService.setCategoryWiseProductsMap(productsDao
					.getProductsByCategoryId());
		} catch (Exception e) {

			logger.error("Error while buidling products cache.", e);
		}
	}

	@Override
	public void buildBwuCache() throws Exception {

		try {
			applicationCacheService.setBwusMap(bwusDao.getBwus());
		} catch (Exception e) {

			logger.error("Error while buidling bwu cache.", e);
		}
	}

	@Override
	public void buildPosmCache() throws Exception {

		try {
			applicationCacheService.setPosmMap(bwusDao.getPosm());
		} catch (Exception e) {

			logger.error("Error while buidling POSM cache.", e);
		}
	}

	@Override
	public void buildIndustryBwuCache() throws Exception {

		try {
			applicationCacheService.setIndustryBwusMap(bwusDao
					.getIndustryBwus("DE"));
		} catch (Exception e) {

			logger.error("Error while buidling industry bwu cache.", e);
		}
	}

	@Override
	public void buildSisIndustryBwuCache() throws Exception {

		try {
			applicationCacheService.setSisIndustryBwusMap(bwusDao
					.getIndustryBwus("SIS"));
		} catch (Exception e) {

			logger.error("Error while buidling industry bwu cache.", e);
		}
	}

	@Override
	public void buildDsrRoutesCache() throws Exception {

		try {
			applicationCacheService.setRoutesMap(dsrDao.getDsrRoutes());
		} catch (Exception e) {

			logger.error("Error while buidling industries cache.", e);
		}

	}

	@Override
	public void buildDsrIndustryBwuCache() throws Exception {

		try {
			applicationCacheService.setDsrIndustryBwusMap(bwusDao
					.getIndustryBwus("DSR"));
		} catch (Exception e) {

			logger.error("Error while buidling industry bwu cache.", e);
		}
	}
	
	@Override
	public void buildBanksCache() throws Exception {

		try {
			applicationCacheService.setBanksMap(dsrDao.getBanks());
		} catch (Exception e) {

			logger.error("Error while buidling questions cache.", e);
		}
	}
}
