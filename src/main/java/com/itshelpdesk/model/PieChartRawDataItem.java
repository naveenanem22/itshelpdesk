package com.itshelpdesk.model;

public class PieChartRawDataItem {

	private String departmentName;
	private Integer ticketCount;

	public PieChartRawDataItem() {

	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Integer getTicketCount() {
		return ticketCount;
	}

	public void setTicketCount(Integer ticketCount) {
		this.ticketCount = ticketCount;
	}

	@Override
	public String toString() {
		return "PieChartRawDataItem [departmentName=" + departmentName + ", ticketCount=" + ticketCount + "]";
	}

}
