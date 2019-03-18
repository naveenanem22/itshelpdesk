package com.itshelpdesk.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pc.deserializers.JsonDateDeserializer;
import com.pc.model.Department;
import com.pc.model.User;
import com.pc.serializers.JsonDateSerializer;

public class Ticket {

	@JsonProperty("id")
	private int id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("description")
	private String description;

	@JsonProperty("department")
	private Department department;

	@JsonProperty("priority")
	private String priority;

	@JsonProperty("serviceCategory")
	private String serviceCategory;

	@JsonProperty("officeLocation")
	private String officeLocation;

	@JsonProperty("deskNumber")
	private String deskNumber;

	@JsonProperty("ticketType")
	private String type;

	@JsonProperty("status")
	private String status;

	@JsonProperty("assignedTo")
	private User assignedTo;

	@JsonProperty("ticketHistory")
	private List<TicketHistory> ticketHistoryList;

	@JsonProperty(value = "createdDate")
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private LocalDateTime createdDate;

	@JsonProperty(value = "updatedDate")
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private LocalDateTime updatedDate;

	public Ticket() {

	}

	public User getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(User assignedTo) {
		this.assignedTo = assignedTo;
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

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getServiceCategory() {
		return serviceCategory;
	}

	public void setServiceCategory(String serviceCategory) {
		this.serviceCategory = serviceCategory;
	}

	public String getOfficeLocation() {
		return officeLocation;
	}

	public void setOfficeLocation(String officeLocation) {
		this.officeLocation = officeLocation;
	}

	public String getDeskNumber() {
		return deskNumber;
	}

	public void setDeskNumber(String deskNumber) {
		this.deskNumber = deskNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<TicketHistory> getTicketHistoryList() {
		return ticketHistoryList;
	}

	public void setTicketHistoryList(List<TicketHistory> ticketHistoryList) {
		this.ticketHistoryList = ticketHistoryList;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public String toString() {
		return "Ticket [id=" + id + ", title=" + title + ", description=" + description + ", department=" + department
				+ ", priority=" + priority + ", serviceCategory=" + serviceCategory + ", officeLocation="
				+ officeLocation + ", deskNumber=" + deskNumber + ", type=" + type + ", status=" + status
				+ ", assignedTo=" + assignedTo + ", ticketHistoryList=" + ticketHistoryList + ", createdDate="
				+ createdDate + ", updatedDate=" + updatedDate + "]";
	}

}
