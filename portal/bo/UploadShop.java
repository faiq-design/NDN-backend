package com.marksman.census.portal.bo;

import com.marksman.census.bo.Shop;

public class UploadShop extends Shop
{

	private String areaPMPKL;
	private String areaManager;
	private String areaCode;
	private String areaType;
	private String businessType;
	private String dsrCode;
	private String dsrName;
	private String surveyorCode;
	private String surveyorName;
	private String cityName;
	

	public String getAreaPMPKL()
	{
		return areaPMPKL;
	}

	public void setAreaPMPKL(String areaPMPKL)
	{
		this.areaPMPKL = areaPMPKL;
	}

	public String getAreaManager()
	{
		return areaManager;
	}

	public void setAreaManager(String areaManager)
	{
		this.areaManager = areaManager;
	}

	public String getAreaType()
	{
		return areaType;
	}

	public void setAreaType(String areaType)
	{
		this.areaType = areaType;
	}

	public String getBusinessType()
	{
		return businessType;
	}

	public void setBusinessType(String businessType)
	{
		this.businessType = businessType;
	}

	public String getDsrCode()
	{
		return dsrCode;
	}

	public void setDsrCode(String dsrCode)
	{
		this.dsrCode = dsrCode;
	}

	public String getDsrName()
	{
		return dsrName;
	}

	public void setDsrName(String dsrName)
	{
		this.dsrName = dsrName;
	}

	public String getSurveyorCode()
	{
		return surveyorCode;
	}

	public void setSurveyorCode(String surveyorCode)
	{
		this.surveyorCode = surveyorCode;
	}

	public String getSurveyorName()
	{
		return surveyorName;
	}

	public void setSurveyorName(String surveyorName)
	{
		this.surveyorName = surveyorName;
	}

	public String getAreaCode()
	{
		return areaCode;
	}

	public void setAreaCode(String areaCode)
	{
		this.areaCode = areaCode;
	}

	public String getCityName()
	{
		return cityName;
	}

	public void setCityName(String cityName)
	{
		this.cityName = cityName;
	}

}
