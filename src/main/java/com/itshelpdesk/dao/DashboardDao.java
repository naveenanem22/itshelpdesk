package com.itshelpdesk.dao;

import java.util.List;

import com.itshelpdesk.model.BarChartDataItem;
import com.itshelpdesk.model.PieChartDataItem;

public interface DashboardDao {
	Integer fetchCountOfTicketsInLastHourByStatus(String ticketStatus);

	List<BarChartDataItem> fetchTicketCountStatusAndMonthWise();

	List<PieChartDataItem> fetchDepartmentWisePayload();
	
	Integer fetchTotalTicketCountFromStart();

}
