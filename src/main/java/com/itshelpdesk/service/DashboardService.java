package com.itshelpdesk.service;

import java.util.List;
import java.util.Map;

import com.itshelpdesk.model.BarChartDataItem;
import com.itshelpdesk.model.PieChartDataItem;

public interface DashboardService {
	Integer fetchCountOfTicketsInLastHourByStatus(String ticketStatus);
	Integer fetchTotalTicketCountFromStart();
	List<BarChartDataItem> fetchTicketCountStatusAndMonthWise();
	List<PieChartDataItem> fetchDepartmentWisePayload();
	
}
