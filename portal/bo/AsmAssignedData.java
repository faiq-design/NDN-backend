package com.marksman.census.portal.bo;

import com.marksman.census.bo.Shop;

public class AsmAssignedData extends Shop
{
	private String categoryName;
	private String surveyorName;
	private String asmName;
	private String regionName;
	private String verified;
	private String asmVisitDate;

	public String getCategoryName()
	{
		return categoryName;
	}

	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
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

	public String getRegionName()
	{
		return regionName;
	}

	public void setRegionName(String regionName)
	{
		this.regionName = regionName;
	}

	public String getVerified()
	{
		return verified.equals("0") ? "No" : "Yes";
	}

	public void setVerified(String verified)
	{
		this.verified = verified;
	}

	public String getAsmVisitDate()
	{
		return asmVisitDate;
	}

	public void setAsmVisitDate(String asmVisitDate)
	{
		this.asmVisitDate = asmVisitDate;
	}

}
