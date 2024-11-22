package com.marksman.census.portal.bo;

import com.marksman.census.bo.Industry;

public class IndustryVolume extends Industry
{
	private Float industryVolume;
	private Float familyVolume;
	private Float variance;
	private String unit;

	public String getUnit()
	{
		return unit;
	}

	public void setUnit(String unit)
	{
		this.unit = unit;
	}

	public Float getIndustryVolume()
	{
		return industryVolume;
	}

	public void setIndustryVolume(Float industryVolume)
	{
		this.industryVolume = industryVolume;
	}

	public Float getFamilyVolume()
	{
		return familyVolume;
	}

	public void setFamilyVolume(Float familyVolume)
	{
		this.familyVolume = familyVolume;
	}

	public Float getVariance()
	{
		return variance;
	}

	public void setVariance(Float variance)
	{
		this.variance = variance;
	}

}
