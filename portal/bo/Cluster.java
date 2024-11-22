package com.marksman.census.portal.bo;

import java.util.Comparator;

import com.google.gson.annotations.Expose;

public class Cluster implements Comparator<Cluster>
{
	public static String label;
	
	private int id;
	private String title;
	private String manager;
	@Expose
	private String active;
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
	public String getManager()
	{
		return manager;
	}
	public void setManager(String manager)
	{
		this.manager = manager;
	}
	
	@Override
	public int compare(Cluster o1, Cluster o2)
	{
		return o1.getId() - o2.getId();
	}
	
	public void setActive(String active)
	{
		this.active = active;
	}

	
	
}
