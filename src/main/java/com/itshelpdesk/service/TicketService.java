package com.itshelpdesk.service;

import java.util.List;

import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;

public interface TicketService {
	
	int createTicket(Ticket ticket);

	Ticket getTicket(int ticketId, int userId);

	List<Ticket> getTicketsByUserName(String userName);

	boolean updateTicket(Ticket ticket, int userId);

	boolean deleteTicket(int ticketId, int userId);

	int createTicketHistory(TicketHistory ticketHistory, int ticketId, int userId);

}
