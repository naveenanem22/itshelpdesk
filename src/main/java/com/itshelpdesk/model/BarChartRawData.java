package com.itshelpdesk.model;

public class BarChartRawData {
	String month;
	String status;
	Integer ticketCount;

	public BarChartRawData() {

	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getTicketCount() {
		return ticketCount;
	}

	public void setTicketCount(Integer ticketCount) {
		this.ticketCount = ticketCount;
	}

	@Override
	public String toString() {
		return "BarChartRawData [month=" + month + ", status=" + status + ", ticketCount=" + ticketCount + "]";
	}

}
