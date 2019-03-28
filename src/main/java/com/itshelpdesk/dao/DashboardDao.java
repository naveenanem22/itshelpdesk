package com.itshelpdesk.dao;

import java.util.List;
import java.util.Map;

public interface DashboardDao {
	Integer fetchCountOfTicketsInLastHourByStatus(String ticketStatus);
	List<Map<Integer, List<Map<String, Integer>>>> fetchTicketCountStatusAndMonthWise();
	

}
