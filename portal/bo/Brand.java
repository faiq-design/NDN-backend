package com.marksman.census.portal.bo;


import java.util.Comparator;

public class Brand implements Comparator<Brand>
{
	public static String label;
	
	private int id;
	private String title;
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
	
	@Override
	public int compare(Brand o1, Brand o2)
	{
		return o1.getId() - o2.getId();
	}

	
	
}

