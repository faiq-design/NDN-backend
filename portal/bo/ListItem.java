package com.marksman.census.portal.bo;

import org.json.JSONObject;

import com.google.gson.annotations.Expose;

public class ListItem implements Comparable<ListItem> {
	@Expose
	protected int id;
	@Expose
	protected String title;
	protected String selected;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	// test
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the selected
	 */
	public String getSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(String selected) {
		this.selected = selected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ListItem other = (ListItem) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(ListItem o) {
		if (this.getTitle().compareTo(o.getTitle()) > 0) {
			return 1;
		} else if (this.getTitle().compareTo(o.getTitle()) < 0) {
			return -1;
		}
		return 0;
	}

	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("id", id);
			obj.put("title", title);
			obj.put("selected", selected);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}
