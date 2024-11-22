package com.marksman.census.portal.bo;

public class FinanceSetup {

	private int id;
	private int regionId;
	private int zoneId;
	private int townId;
	private int approvedDsrCount;
	private int approvedDECount;
	private float dsrBasicSalary;
	private float dsrFuelAllowances;
	private float dsrDailyAllowances;
	private float dsrMaintainanceAllowances;
	private float deBasicSalary;
	private float deFuelAllowances;
	private float deDailyAllowances;
	private float deMaintainanceAllowances;
	private float dsrIncrement;
	private float deIncrement;
	private int approvedVanDsrCount;
	private float vanDsrBasicSalary;
	private float vanDsrIncrement;
	private float deOtherAllowances;
	private float dsrOtherAllowances;
	private String changeScope;
	private String deIncrementUnit;
	private String dsrIncrementUnit;
	private String vanDsrIncrementUnit;
	private float loadingUnloading;
	private float soleToSub;
	private float godownRent;
	private float vanSalary;
	private float additionalDrivers;
	private float additionalDriverSalary;
	private float othersSalary;
	private float officeRent;
	private float legalExpenses;
	private float additionalExpenses;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTownId() {
		return townId;
	}

	public void setTownId(int townId) {
		this.townId = townId;
	}

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public int getApprovedDsrCount() {
		return approvedDsrCount;
	}

	public void setApprovedDsrCount(int approvedDsrCount) {
		this.approvedDsrCount = approvedDsrCount;
	}

	public int getApprovedDECount() {
		return approvedDECount;
	}

	public void setApprovedDECount(int approvedDECount) {
		this.approvedDECount = approvedDECount;
	}

	public float getDsrBasicSalary() {
		return dsrBasicSalary;
	}

	public void setDsrBasicSalary(float dsrBasicSalary) {
		this.dsrBasicSalary = dsrBasicSalary;
	}

	public float getDsrFuelAllowances() {
		return dsrFuelAllowances;
	}

	public void setDsrFuelAllowances(float dsrFuelAllowances) {
		this.dsrFuelAllowances = dsrFuelAllowances;
	}

	public float getDsrDailyAllowances() {
		return dsrDailyAllowances;
	}

	public void setDsrDailyAllowances(float dsrDailyAllowances) {
		this.dsrDailyAllowances = dsrDailyAllowances;
	}

	public float getDsrMaintainanceAllowances() {
		return dsrMaintainanceAllowances;
	}

	public void setDsrMaintainanceAllowances(float dsrMaintainanceAllowances) {
		this.dsrMaintainanceAllowances = dsrMaintainanceAllowances;
	}

	public float getDeBasicSalary() {
		return deBasicSalary;
	}

	public void setDeBasicSalary(float deBasicSalary) {
		this.deBasicSalary = deBasicSalary;
	}

	public float getDeFuelAllowances() {
		return deFuelAllowances;
	}

	public void setDeFuelAllowances(float deFuelAllowances) {
		this.deFuelAllowances = deFuelAllowances;
	}

	public float getDeDailyAllowances() {
		return deDailyAllowances;
	}

	public void setDeDailyAllowances(float deDailyAllowances) {
		this.deDailyAllowances = deDailyAllowances;
	}

	public float getDeMaintainanceAllowances() {
		return deMaintainanceAllowances;
	}

	public void setDeMaintainanceAllowances(float deMaintainanceAllowances) {
		this.deMaintainanceAllowances = deMaintainanceAllowances;
	}

	public float getDsrIncrement() {
		return dsrIncrement;
	}

	public void setDsrIncrement(float dsrIncrement) {
		this.dsrIncrement = dsrIncrement;
	}

	public float getDeIncrement() {
		return deIncrement;
	}

	public void setDeIncrement(float deIncrement) {
		this.deIncrement = deIncrement;
	}

	public int getZoneId() {
		return zoneId;
	}

	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}

	public int getApprovedVanDsrCount() {
		return approvedVanDsrCount;
	}

	public void setApprovedVanDsrCount(int approvedVanDsrCount) {
		this.approvedVanDsrCount = approvedVanDsrCount;
	}

	public float getVanDsrBasicSalary() {
		return vanDsrBasicSalary;
	}

	public void setVanDsrBasicSalary(float vanDsrBasicSalary) {
		this.vanDsrBasicSalary = vanDsrBasicSalary;
	}

	public float getVanDsrIncrement() {
		return vanDsrIncrement;
	}

	public void setVanDsrIncrement(float vanDsrIncrement) {
		this.vanDsrIncrement = vanDsrIncrement;
	}

	public float getDeOtherAllowances() {
		return deOtherAllowances;
	}

	public void setDeOtherAllowances(float deOtherAllowances) {
		this.deOtherAllowances = deOtherAllowances;
	}

	public float getDsrOtherAllowances() {
		return dsrOtherAllowances;
	}

	public void setDsrOtherAllowances(float dsrOtherAllowances) {
		this.dsrOtherAllowances = dsrOtherAllowances;
	}

	public String getChangeScope() {
		return changeScope;
	}

	public void setChangeScope(String changeScope) {
		this.changeScope = changeScope;
	}

	public String getDeIncrementUnit() {
		return deIncrementUnit;
	}

	public void setDeIncrementUnit(String deIncrementUnit) {
		this.deIncrementUnit = deIncrementUnit;
	}

	public String getDsrIncrementUnit() {
		return dsrIncrementUnit;
	}

	public void setDsrIncrementUnit(String dsrIncrementUnit) {
		this.dsrIncrementUnit = dsrIncrementUnit;
	}

	public String getVanDsrIncrementUnit() {
		return vanDsrIncrementUnit;
	}

	public void setVanDsrIncrementUnit(String vanDsrIncrementUnit) {
		this.vanDsrIncrementUnit = vanDsrIncrementUnit;
	}

	public float getLoadingUnloading() {
		return loadingUnloading;
	}

	public void setLoadingUnloading(float loadingUnloading) {
		this.loadingUnloading = loadingUnloading;
	}

	public float getSoleToSub() {
		return soleToSub;
	}

	public void setSoleToSub(float soleToSub) {
		this.soleToSub = soleToSub;
	}

	public float getGodownRent() {
		return godownRent;
	}

	public void setGodownRent(float godownRent) {
		this.godownRent = godownRent;
	}

	public float getVanSalary() {
		return vanSalary;
	}

	public void setVanSalary(float vanSalary) {
		this.vanSalary = vanSalary;
	}

	public float getAdditionalDrivers() {
		return additionalDrivers;
	}

	public void setAdditionalDrivers(float additionalDrivers) {
		this.additionalDrivers = additionalDrivers;
	}

	public float getAdditionalDriverSalary() {
		return additionalDriverSalary;
	}

	public void setAdditionalDriverSalary(float additionalDriverSalary) {
		this.additionalDriverSalary = additionalDriverSalary;
	}

	public float getOthersSalary() {
		return othersSalary;
	}

	public void setOthersSalary(float othersSalary) {
		this.othersSalary = othersSalary;
	}

	public float getOfficeRent() {
		return officeRent;
	}

	public void setOfficeRent(float officeRent) {
		this.officeRent = officeRent;
	}

	public float getLegalExpenses() {
		return legalExpenses;
	}

	public void setLegalExpenses(float legalExpenses) {
		this.legalExpenses = legalExpenses;
	}

	public float getAdditionalExpenses() {
		return additionalExpenses;
	}

	public void setAdditionalExpenses(float additionalExpenses) {
		this.additionalExpenses = additionalExpenses;
	}

}
