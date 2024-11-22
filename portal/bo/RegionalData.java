package com.marksman.census.portal.bo;

import com.marksman.census.constants.DateTimeConstants;
import com.marksman.census.util.CommonUtil;
import com.marksman.census.util.DateTimeUtilities;

public class RegionalData
{
	private String region;
	private int dssCount;
	private String activityStartDateRetail;
	private String activityStartDateWS;

	private int wsDailyTarget;
	private int retailDailyTarget;

	private int planned;
	private int wsPlanned;
	private int retailPlanned;

	private int validated;
	private int newValidated;
	private int retailValidated;
	private int wsValidated;

	private int pendingShops;

	private int newShops;
	private int wsNewShops;
	private int retailNewShops;

	private int outOfScope;
	private int wsOutOfScope;
	private int retailOutOfScope;

	private int redFlagShops;
	private int newRedFlagShops;

	private int retailConverted;
	private int wsConverted;

	private int retailQcEvaluated;
	private int wsQcEvaluated;
	private int retailQcPending;
	private int wsQcPending;

	private int retailAmPending;
	private int wsAMPending;
	private int tillDateTarget;

	public int getDssCount()
	{
		return dssCount;
	}

	public void setDssCount(int dssCount)
	{
		this.dssCount = dssCount;
	}

	public String getActivityStartDateRetail()
	{
		return activityStartDateRetail;
	}

	public void setActivityStartDateRetail(String activityStartDateRetail)
	{
		this.activityStartDateRetail = activityStartDateRetail;
	}

	public String getActivityStartDateWS()
	{
		return activityStartDateWS;
	}

	public void setActivityStartDateWS(String activityStartDateWS)
	{
		this.activityStartDateWS = activityStartDateWS;
	}

	public int getWsDailyTarget()
	{
		return wsDailyTarget;
	}

	public void setWsDailyTarget(int wsDailyTarget)
	{
		this.wsDailyTarget = wsDailyTarget;
	}

	public int getRetailDailyTarget()
	{
		return retailDailyTarget;
	}

	public void setRetailDailyTarget(int retailDailyTarget)
	{
		this.retailDailyTarget = retailDailyTarget;
	}

	public int getWsDaysPassed()
	{

		return (int) (DateTimeUtilities.daysBetween2Dates(DateTimeUtilities.stringToDate(
				getActivityStartDateWS(), DateTimeConstants.DATE_FORMAT4), DateTimeUtilities
				.stringToDate("2016-09-30", DateTimeConstants.DATE_FORMAT4)) - 3);
	}

	public int getRetailDaysPassed()
	{
		return (int) DateTimeUtilities.daysBetween2Dates(DateTimeUtilities.stringToDate(
				getActivityStartDateRetail(), DateTimeConstants.DATE_FORMAT4), DateTimeUtilities
				.getCurrentDateInDate(DateTimeConstants.DATE_FORMAT4));
	}

	public int getPlanned()
	{
		return planned;
	}

	public void setPlanned(int planned)
	{
		this.planned = planned;
	}

	public int getWsPlanned()
	{
		return wsPlanned;
	}

	public void setWsPlanned(int wsPlanned)
	{
		this.wsPlanned = wsPlanned;
	}

	public int getRetailPlanned()
	{
		return retailPlanned;
	}

	public void setRetailPlanned(int retailPlanned)
	{
		this.retailPlanned = retailPlanned;
	}

	public int getValidated()
	{
		return validated;
	}

	public void setValidated(int validated)
	{
		this.validated = validated;
	}

	public int getNewValidated()
	{
		return newValidated;
	}

	public void setNewValidated(int newValidated)
	{
		this.newValidated = newValidated;
	}

	public int getRetailValidated()
	{
		return retailValidated;
	}

	public void setRetailValidated(int retailValidated)
	{
		this.retailValidated = retailValidated;
	}

	public int getWsValidated()
	{
		return wsValidated;
	}

	public void setWsValidated(int wsValidated)
	{
		this.wsValidated = wsValidated;
	}

	public int getWsConverted()
	{
		return wsConverted;
	}

	public void setWsConverted(int wsConverted)
	{
		this.wsConverted = wsConverted;
	}

	public int getRedFlagShops()
	{
		return redFlagShops;
	}

	public void setRedFlagShops(int redFlagShops)
	{
		this.redFlagShops = redFlagShops;
	}

	public int getPendingShops()
	{
		return pendingShops;
	}

	public void setPendingShops(int pendingShops)
	{
		this.pendingShops = pendingShops;
	}

	public String getRegion()
	{
		return region;
	}

	public void setRegion(String region)
	{
		this.region = region;
	}

	public int getNewShops()
	{
		return newShops;
	}

	public void setNewShops(int newShops)
	{
		this.newShops = newShops;
	}

	public int getOutOfScope()
	{
		return outOfScope;
	}

	public void setOutOfScope(int outOfScope)
	{
		this.outOfScope = outOfScope;
	}

	public void setTillDateTarget()
	{
		this.tillDateTarget = Math
				.round((this.getDssCount() * getWsDaysPassed() * getWsDailyTarget())
						+ (this.getDssCount() * getRetailDaysPassed() * getRetailDailyTarget())
						/ (float) 1.16);
	}

	public int getTillDateTarget()
	{

		return tillDateTarget;
	}

	public int getRetailConverted()
	{
		return retailConverted;
	}

	public void setRetailConverted(int retailConverted)
	{
		this.retailConverted = retailConverted;
	}

	public int getWsOutOfScope()
	{
		return wsOutOfScope;
	}

	public void setWsOutOfScope(int wsOutOfScope)
	{
		this.wsOutOfScope = wsOutOfScope;
	}

	public int getRetailOutOfScope()
	{
		return retailOutOfScope;
	}

	public void setRetailOutOfScope(int retailOutOfScope)
	{
		this.retailOutOfScope = retailOutOfScope;
	}

	public int getNewRedFlagShops()
	{
		return newRedFlagShops;
	}

	public void setNewRedFlagShops(int newRedFlagShops)
	{
		this.newRedFlagShops = newRedFlagShops;
	}

	public int getWsNewShops()
	{
		return wsNewShops;
	}

	public void setWsNewShops(int wsNewShops)
	{
		this.wsNewShops = wsNewShops;
	}

	public int getRetailNewShops()
	{
		return retailNewShops;
	}

	public void setRetailNewShops(int retailNewShops)
	{
		this.retailNewShops = retailNewShops;
	}

	public int getRetailQcEvaluated()
	{
		return retailQcEvaluated;
	}

	public void setRetailQcEvaluated(int retailQcEvaluated)
	{
		this.retailQcEvaluated = retailQcEvaluated;
	}

	public int getWsQcEvaluated()
	{
		return wsQcEvaluated;
	}

	public void setWsQcEvaluated(int wsQcEvaluated)
	{
		this.wsQcEvaluated = wsQcEvaluated;
	}

	public int getRetailQcPending()
	{
		return retailQcPending;
	}

	public void setRetailQcPending(int retailQcPending)
	{
		this.retailQcPending = retailQcPending;
	}

	public int getWsQcPending()
	{
		return wsQcPending;
	}

	public void setWsQcPending(int wsQcPending)
	{
		this.wsQcPending = wsQcPending;
	}

	public int getRetailAmPending()
	{
		return retailAmPending;
	}

	public void setRetailAmPending(int retailAmPending)
	{
		this.retailAmPending = retailAmPending;
	}

	public int getWsAMPending()
	{
		return wsAMPending;
	}

	public void setWsAMPending(int wsAMPending)
	{
		this.wsAMPending = wsAMPending;
	}

	public float getPercentageTillDateTarget()
	{
		return (float) CommonUtil.round((getValidated() / (double) getTillDateTarget()), 2);
	}

	public float getPercentageRetailTillDateTarget()
	{
		return (float) CommonUtil.round((getRetailValidated() + getRetailNewShops()
				+ getRetailOutOfScope() + getRetailConverted() - getWsConverted())
				/ (double) (getRetailPlanned() + getRetailNewShops()), 2);
	}

	public float getPercentageWsTillDateTarget()
	{
		return (float) CommonUtil.round((getWsValidated() + getWsNewShops() + getWsOutOfScope()
				+ getWsConverted() - getRetailConverted())
				/ (double) (getWsPlanned() + getWsNewShops()), 2);
	}

	public float getPercentageOutOfScope()
	{
		return (float) CommonUtil.round((getOutOfScope() / (double) getValidated()), 2);
	}

	public float getPercentageWsOutOfScope()
	{
		return (float) CommonUtil.round((getWsOutOfScope() / (double) getWsPlanned()), 2);
	}

	public float getCurrentRate()
	{
		return getValidated()
				/ (float) ((getWsDaysPassed() + getRetailDaysPassed()) * getDssCount());
	}

	public float getRequriedRate()
	{
		return (getPlanned() - getValidated())
				/ (float) ((51 - getRetailDaysPassed()) * getDssCount()) == 0 ? 1
				: (51 - getRetailDaysPassed()) * getDssCount();
	}

}
