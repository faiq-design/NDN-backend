package com.marksman.census.portal.bo;
import java.util.ArrayList;

public class DashboardReport
{
	private int id;
	private String title;
	private int type;
	private String templateUrl;
	private String sheetName;
	private String query;
	private ArrayList<DashboardReportParameter> parameterList = new ArrayList<DashboardReportParameter>();

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public String getTemplateUrl()
	{
		return templateUrl;
	}

	public void setTemplateUrl(String templateUrl)
	{
		this.templateUrl = templateUrl;
	}

	public String getSheetName()
	{
		return sheetName;
	}

	public void setSheetName(String sheetName)
	{
		this.sheetName = sheetName;
	}

	public String getQuery()
	{
		return query;
	}

	public void setQuery(String query)
	{
		this.query = query;
	}

	public ArrayList<DashboardReportParameter> getParameterList()
	{
		return parameterList;
	}

	public void setParameterList(ArrayList<DashboardReportParameter> parameterList)
	{
		this.parameterList = parameterList;
	}

}
