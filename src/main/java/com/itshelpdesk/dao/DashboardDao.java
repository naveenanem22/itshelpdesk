package com.itshelpdesk.dao;

import java.util.List;

import com.itshelpdesk.model.BarChartDataItem;

public interface DashboardDao {
	Integer fetchCountOfTicketsInLastHourByStatus(String ticketStatus);
	List<BarChartDataItem> fetchTicketCountStatusAndMonthWise();
	

}
