package com.marksman.census.portal.bo;

import com.marksman.census.bo.Shop;

public class EvaluatedShop extends Shop
{

	private String visitDate;
	private String regionName;
	private String cityName;
	private String surveyorName;
	private String asmName;
	private String areaType;
	private String evaluatedDate;
	private String evaluatedTime;
	private String evaluator;

	public String getEvaluator()
	{
		return evaluator;
	}

	public void setEvaluator(String evaluator)
	{
		this.evaluator = evaluator;
	}

	public String getVisitDate()
	{
		return visitDate;
	}

	public void setVisitDate(String visitDate)
	{
		this.visitDate = visitDate;
	}

	public String getRegionName()
	{
		return regionName;
	}

	public void setRegionName(String regionName)
	{
		this.regionName = regionName;
	}

	public String getCityName()
	{
		return cityName;
	}

	public void setCityName(String cityName)
	{
		this.cityName = cityName;
	}

	public String getSurveyorName()
	{
		return surveyorName;
	}

	public void setSurveyorName(String surveyorName)
	{
		this.surveyorName = surveyorName;
	}

	public String getAsmName()
	{
		return asmName;
	}

	public void setAsmName(String asmName)
	{
		this.asmName = asmName;
	}

	public String getAreaType()
	{
		return areaType;
	}

	public void setAreaType(String areaType)
	{
		this.areaType = areaType;
	}

	public String getEvaluatedDate()
	{
		return evaluatedDate;
	}

	public void setEvaluatedDate(String evaluatedDate)
	{
		this.evaluatedDate = evaluatedDate;
	}

	public String getEvaluatedTime()
	{
		return evaluatedTime;
	}

	public void setEvaluatedTime(String evaluatedTime)
	{
		this.evaluatedTime = evaluatedTime;
	}

}
