package com.marksman.census.portal.bo;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.annotations.Expose;

public class ShopChannel extends ListItem implements Comparator<ShopChannel>
{
	@Expose
	protected String description;
	@Expose
	private int timeSpentRequired;
	@Expose
	private int shopsCount;
	@Expose
	private double pictureScore;
	@Expose
	private int categoryScore;
	@Expose
	private int groupId;
	@Expose
	private int parnetChannelId;
	@Expose
	private int orderId;
	@Expose
	private String channelType;
	@Expose
	private String superChannel;
	
	public int getOrderId()
	{
		return orderId;
	}

	public void setOrderId(int orderId)
	{
		this.orderId = orderId;
	}

	public int getShopsCount()
	{
		return shopsCount;
	}

	public void setShopsCount(int shopsCount)
	{
		this.shopsCount = shopsCount;
	}

	public int getChannelId()
	{
		return id;
	}

	public void setChannelId(int channelId)
	{
		setId(channelId);
	}

	public String getChannelTitle()
	{
		return title;
	}

	public void setChannelTitle(String channelTitle)
	{
		setTitle(channelTitle);

	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the timeSpentRequired
	 */
	public int getTimeSpentRequired()
	{
		return timeSpentRequired;
	}

	/**
	 * @param timeSpentRequired the timeSpentRequired to set
	 */
	public void setTimeSpentRequired(int timeSpentRequired)
	{
		this.timeSpentRequired = timeSpentRequired;
	}

	/**
	 * @return the pictureScore
	 */
	public double getPictureScore()
	{
		return pictureScore;
	}

	/**
	 * @param pictureScore the pictureScore to set
	 */
	public void setPictureScore(double pictureScore)
	{
		this.pictureScore = pictureScore;
	}

	/**
	 * @return the groupId
	 */
	public int getGroupId()
	{
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(int groupId)
	{
		this.groupId = groupId;
	}

	/**
	 * @return the categoryScore
	 */
	public int getCategoryScore()
	{
		return categoryScore;
	}

	/**
	 * @param categoryScore the categoryScore to set
	 */
	public void setCategoryScore(int categoryScore)
	{
		this.categoryScore = categoryScore;
	}

	
	public int getParnetChannelId()
	{
		return parnetChannelId;
	}

	public void setParnetChannelId(int parnetChannelId)
	{
		this.parnetChannelId = parnetChannelId;
	}

	public String toString()
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("id", id);
			obj.put("title", title);
			obj.put("orderId", orderId);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return obj.toString();
	}

	@Override
	public int compare(ShopChannel o1, ShopChannel o2)
	{
		 if(o1.getOrderId() < o2.getOrderId())
        	 return -1;
        else if(o1.getOrderId() > o2.getOrderId())
            return 1;
        else
            return 0;   
	}

	public String getChannelType()
	{
		return channelType;
	}

	public void setChannelType(String channelType)
	{
		this.channelType = channelType;
	}

	
} 
