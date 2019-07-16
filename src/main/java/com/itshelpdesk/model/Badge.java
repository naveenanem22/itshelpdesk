package com.itshelpdesk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Badge {

	@JsonProperty("id")
	private int id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("description")
	private String description;

	public Badge() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Badge [id=" + id + ", title=" + title + ", description=" + description + "]";
	}

}
