package com.marksman.census.portal.bo;

public class UserActivity
{
	private String userName;
	private String designation;
	private String region;
	private int loggedIn;
	private String lastLoggedIn;
	private int exportData;
	private int dssSummary;
	private int redFlag;
	private int asmSummary;
	private int asmAssigned;
	private int outOfScope;

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getDesignation()
	{
		return designation;
	}

	public void setDesignation(String designation)
	{
		this.designation = designation;
	}

	public String getRegion()
	{
		return region;
	}

	public void setRegion(String region)
	{
		this.region = region;
	}

	public int getLoggedIn()
	{
		return loggedIn;
	}

	public void setLoggedIn(int loggedIn)
	{
		this.loggedIn = loggedIn;
	}

	public String getLastLoggedIn()
	{
		return lastLoggedIn;
	}

	public void setLastLoggedIn(String lastLoggedIn)
	{
		this.lastLoggedIn = lastLoggedIn;
	}

	public int getExportData()
	{
		return exportData;
	}

	public void setExportData(int exportData)
	{
		this.exportData = exportData;
	}

	public int getDssSummary()
	{
		return dssSummary;
	}

	public void setDssSummary(int dssSummary)
	{
		this.dssSummary = dssSummary;
	}

	public int getRedFlag()
	{
		return redFlag;
	}

	public void setRedFlag(int redFlag)
	{
		this.redFlag = redFlag;
	}

	public int getAsmSummary()
	{
		return asmSummary;
	}

	public void setAsmSummary(int asmSummary)
	{
		this.asmSummary = asmSummary;
	}

	public int getAsmAssigned()
	{
		return asmAssigned;
	}

	public void setAsmAssigned(int asmAssigned)
	{
		this.asmAssigned = asmAssigned;
	}

	public int getOutOfScope()
	{
		return outOfScope;
	}

	public void setOutOfScope(int outOfScope)
	{
		this.outOfScope = outOfScope;
	}

}
