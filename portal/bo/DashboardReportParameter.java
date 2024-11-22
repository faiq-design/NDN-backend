package com.marksman.census.portal.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.marksman.census.bo.Area;
import com.marksman.census.bo.City;
import com.marksman.census.bo.Region;
import com.marksman.census.bo.Zone;
import com.marksman.census.client.bo.Distribution;



public class DashboardReportParameter
{
	private int id;
	private int parentId;
	private String title;
	private String type;
	private String populatedFrom;
	private String parameterKey;
	private ArrayList<Cluster> clusterList;
	private ArrayList<Zone> zoneList;
	private ArrayList<Region> regionList;
	private ArrayList<Area> areaList;
	private ArrayList<City> cityList;
	private ArrayList<Distribution> distributionList;
	private List<Map<String, Object>> foldList;
	private ArrayList<ShopChannel> channelList;
	private Map<String, Object> dataObject;
	private String allowedValues;

	// barnd filter
	private ArrayList<Brand> brandList;

	private ArrayList<Brand> criteriaList;

	public int getParentId()
	{
		return parentId;
	}

	public void setParentId(int parentId)
	{
		this.parentId = parentId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getPopulatedFrom()
	{
		return populatedFrom;
	}

	public void setPopulatedFrom(String populatedFrom)
	{
		this.populatedFrom = populatedFrom;
	}

	public ArrayList<Cluster> getClusterList()
	{
		return clusterList;
	}

	public void setClusterList(ArrayList<Cluster> clusterList)
	{
		this.clusterList = clusterList;
	}

	public ArrayList<Zone> getZoneList()
	{
		return zoneList;
	}

	public void setZoneList(ArrayList<Zone> zoneList)
	{
		this.zoneList = zoneList;
	}

	public ArrayList<Region> getRegionList()
	{
		return regionList;
	}

	public void setRegionList(ArrayList<Region> regionList)
	{
		this.regionList = regionList;
	}

	public ArrayList<Area> getAreaList()
	{
		return areaList;
	}

	public void setAreaList(ArrayList<Area> areaList)
	{
		this.areaList = areaList;
	}

	public ArrayList<City> getCityList()
	{
		return cityList;
	}

	public void setCityList(ArrayList<City> cityList)
	{
		this.cityList = cityList;
	}

	public ArrayList<Distribution> getDistributionList()
	{
		return distributionList;
	}

	public void setDistributionList(ArrayList<Distribution> distributionList)
	{
		this.distributionList = distributionList;
	}

	public List<Map<String, Object>> getFoldList()
	{
		return foldList;
	}

	public void setFoldList(List<Map<String, Object>> foldList)
	{
		this.foldList = foldList;
	}

	public ArrayList<ShopChannel> getChannelList()
	{
		return channelList;
	}

	public void setChannelList(ArrayList<ShopChannel> channelList)
	{
		this.channelList = channelList;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	// barnd filter
	public ArrayList<Brand> getBrandList()
	{
		return brandList;
	}

	public void setBrandList(ArrayList<Brand> brandList)
	{
		this.brandList = brandList;
	}

	public ArrayList<Brand> getCriteriaList()
	{
		return criteriaList;
	}

	public void setCriteriaList(ArrayList<Brand> criteriaList)
	{
		this.criteriaList = criteriaList;
	}

	public Map<String, Object> getDataObject()
	{
		return dataObject;
	}

	public void setDataObject(Map<String, Object> dataObject)
	{
		this.dataObject = dataObject;
	}

	public String getAllowedValues()
	{
		return allowedValues;
	}

	public void setAllowedValues(String allowedValues)
	{
		this.allowedValues = allowedValues;
	}

	public String getParameterKey()
	{
		return parameterKey;
	}

	public void setParameterKey(String parameterKey)
	{
		this.parameterKey = parameterKey;
	}

}
