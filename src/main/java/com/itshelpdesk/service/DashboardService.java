package com.itshelpdesk.service;

import java.util.List;
import java.util.Map;

import com.itshelpdesk.model.BarChart;

public interface DashboardService {
	Integer fetchCountOfTicketsInLastHourByStatus(String ticketStatus);
	BarChart fetchTicketCountStatusAndMonthWise();
}
