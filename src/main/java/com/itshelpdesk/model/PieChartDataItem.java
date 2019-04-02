package com.itshelpdesk.model;

public class PieChartDataItem {

	private String departmentName;
	private Integer payload;

	public PieChartDataItem() {

	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Integer getPayload() {
		return payload;
	}

	public void setPayload(Integer payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return "PieChartDataItem [departmentName=" + departmentName + ", payload=" + payload + "]";
	}

}
