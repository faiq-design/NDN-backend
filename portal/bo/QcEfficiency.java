package com.marksman.census.portal.bo;

public class QcEfficiency {
	private String region;
	private int validated;
	private int evaluated;

	private int curRedFlag;
	private int curShopFascia;
	private int curChannel;
	private int curAssetVisibility;
	private int curHotZone;
	private int curPrimaryShelf;
	private int curLocationCheck;

	private int totalRedFlag;
	private int totalShopFascia;
	private int totalChannel;
	private int totalAssetVisibility;
	private int totalHotZone;
	private int totalPrimaryShelf;
	private int totalLocationCheck;

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getValidated() {
		return validated;
	}

	public void setValidated(int validated) {
		this.validated = validated;
	}

	public int getEvaluated() {
		return evaluated;
	}

	public void setEvaluated(int evaluated) {
		this.evaluated = evaluated;
	}

	public float getEfficiency() {
		return (float) (getEvaluated() / (double) getValidated());
	}

	public int getBalance() {
		return getValidated() - getEvaluated();
	}

	public int getCurRedFlag() {
		return curRedFlag;
	}

	public void setCurRedFlag(int curRedFlag) {
		this.curRedFlag = curRedFlag;
	}

	public int getCurShopFascia() {
		return curShopFascia;
	}

	public void setCurShopFascia(int curShopFascia) {
		this.curShopFascia = curShopFascia;
	}

	public int getCurChannel() {
		return curChannel;
	}

	public void setCurChannel(int curChannel) {
		this.curChannel = curChannel;
	}

	public int getCurAssetVisibility() {
		return curAssetVisibility;
	}

	public void setCurAssetVisibility(int curAssetVisibility) {
		this.curAssetVisibility = curAssetVisibility;
	}

	public int getCurHotZone() {
		return curHotZone;
	}

	public void setCurHotZone(int curHotZone) {
		this.curHotZone = curHotZone;
	}

	public int getCurPrimaryShelf() {
		return curPrimaryShelf;
	}

	public void setCurPrimaryShelf(int curPrimaryShelf) {
		this.curPrimaryShelf = curPrimaryShelf;
	}

	public int getCurLocationCheck() {
		return curLocationCheck;
	}

	public void setCurLocationCheck(int curLocationCheck) {
		this.curLocationCheck = curLocationCheck;
	}

	public int getTotalRedFlag() {
		return totalRedFlag;
	}

	public void setTotalRedFlag(int totalRedFlag) {
		this.totalRedFlag = totalRedFlag;
	}

	public int getTotalShopFascia() {
		return totalShopFascia;
	}

	public void setTotalShopFascia(int totalShopFascia) {
		this.totalShopFascia = totalShopFascia;
	}

	public int getTotalChannel() {
		return totalChannel;
	}

	public void setTotalChannel(int totalChannel) {
		this.totalChannel = totalChannel;
	}

	public int getTotalAssetVisibility() {
		return totalAssetVisibility;
	}

	public void setTotalAssetVisibility(int totalAssetVisibility) {
		this.totalAssetVisibility = totalAssetVisibility;
	}

	public int getTotalHotZone() {
		return totalHotZone;
	}

	public void setTotalHotZone(int totalHotZone) {
		this.totalHotZone = totalHotZone;
	}

	public int getTotalPrimaryShelf() {
		return totalPrimaryShelf;
	}

	public void setTotalPrimaryShelf(int totalPrimaryShelf) {
		this.totalPrimaryShelf = totalPrimaryShelf;
	}

	public int getTotalLocationCheck() {
		return totalLocationCheck;
	}

	public void setTotalLocationCheck(int totalLocationCheck) {
		this.totalLocationCheck = totalLocationCheck;
	}

}
