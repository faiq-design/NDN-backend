package com.marksman.census.service;

public interface CacheService
{

	public void buildCache() throws Exception;

	public void buildAreaCache() throws Exception;

	public void buildVersionsCache() throws Exception;

	public void buildCitiesCache() throws Exception;

	public void buildRegionsCache() throws Exception;

	public void buildZonesCache() throws Exception;

	public void buildCategoriesCache() throws Exception;

	public void buildGroupsCache() throws Exception;

	public void buildQuestionTypesCache() throws Exception;

	public void buildQuestionsCache() throws Exception;

	public void buildOptionsCache() throws Exception;

	public void buildQuestionOptionsCache() throws Exception;

	public void buildRemarksCache() throws Exception;

	public void buildRemarkTypesCache() throws Exception;

	public void buildIndustriesCache() throws Exception;

	public void buildFamiliesCache() throws Exception;

	public void buildChannelsCache() throws Exception;

	public void buildChannelProductsCache() throws Exception;

	public void buildAssetsCache() throws Exception;

	public void buildProductDisplayCache() throws Exception;

	public void buildProductCategoriesCache() throws Exception;

	public void buildProductsCache() throws Exception;

	public void buildBwuCache() throws Exception;

	public void buildPosmCache() throws Exception;

	public void buildIndustryBwuCache() throws Exception;
	
	public void buildSisIndustryBwuCache() throws Exception;

	public void buildDistributionCache() throws Exception;

	public void buildDsrCache() throws Exception;

	public void buildTsoCache() throws Exception;

	public void buildSisFamiliesCache() throws Exception;

	public void buildSisIndustriesCache() throws Exception;
	
	public void buildDsrRoutesCache() throws Exception;
	
	public void buildDsrIndustryBwuCache() throws Exception;

	public void buildBanksCache() throws Exception;

}
