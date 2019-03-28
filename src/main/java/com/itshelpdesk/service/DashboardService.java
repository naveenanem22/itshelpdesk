package com.itshelpdesk.service;

import java.util.List;
import java.util.Map;

import com.itshelpdesk.model.BarChartDataItem;

public interface DashboardService {
	Integer fetchCountOfTicketsInLastHourByStatus(String ticketStatus);
	List<BarChartDataItem> fetchTicketCountStatusAndMonthWise();
}
