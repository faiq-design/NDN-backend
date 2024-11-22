package com.marksman.census.portal.bo;

public class ProductsDisplayDetail
{
	private int categoryId;
	private String category;
	private String imageUrl;
	private int totalFacings;
	private int mondelezFacings;
	private int mondelezGoldFacings;
	private int displayRemarkId;

	public int getCategoryId()
	{
		return categoryId;
	}

	public void setCategoryId(int categoryId)
	{
		this.categoryId = categoryId;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	public int getTotalFacings()
	{
		return totalFacings;
	}

	public void setTotalFacings(int totalFacings)
	{
		this.totalFacings = totalFacings;
	}

	public int getMondelezFacings()
	{
		return mondelezFacings;
	}

	public void setMondelezFacings(int mondelezFacings)
	{
		this.mondelezFacings = mondelezFacings;
	}

	public int getMondelezGoldFacings()
	{
		return mondelezGoldFacings;
	}

	public void setMondelezGoldFacings(int mondelezGoldFacings)
	{
		this.mondelezGoldFacings = mondelezGoldFacings;
	}

	public int getDisplayRemarkId()
	{
		return displayRemarkId;
	}

	public void setDisplayRemarkId(int displayRemarkId)
	{
		this.displayRemarkId = displayRemarkId;
	}

}
