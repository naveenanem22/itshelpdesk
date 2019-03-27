package com.itshelpdesk.dao;

public interface DashboardDao {
	int fetchCountOfTicketsInLastHourByStatus(String ticketStatus);
	

}
