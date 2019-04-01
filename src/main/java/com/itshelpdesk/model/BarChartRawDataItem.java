package com.itshelpdesk.model;

import java.time.LocalDateTime;
import java.time.Year;

public class BarChartRawDataItem {
	Integer month;
	String status;
	Integer ticketCount;
	Integer year;
	LocalDateTime lastDayOfTicketCreatedMonth;

	public BarChartRawDataItem() {

	}

	public LocalDateTime getLastDayOfTicketCreatedMonth() {
		return lastDayOfTicketCreatedMonth;
	}

	public void setLastDayOfTicketCreatedMonth(LocalDateTime lastDayOfTicketCreatedMonth) {
		this.lastDayOfTicketCreatedMonth = lastDayOfTicketCreatedMonth;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
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
		return "BarChartRawDataItem [month=" + month + ", status=" + status + ", ticketCount=" + ticketCount + ", year="
				+ year + ", lastDayOfTicketCreatedMonth=" + lastDayOfTicketCreatedMonth + "]";
	}

}
