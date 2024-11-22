package com.marksman.census.portal.bo;

import java.util.ArrayList;
import java.util.List;

import com.marksman.census.bo.ExportShop;

public class BillImagesShop extends ExportShop {

	private List<String> billImages = new ArrayList<String>();

	public List<String> getBillImages() {
		return billImages;
	}

	public void setBillImages(List<String> billImages) {
		this.billImages = billImages;
	}

}
