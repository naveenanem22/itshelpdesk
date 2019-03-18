package com.itshelpdesk.service;

import java.util.List;

import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;

public interface TicketService {

	int createTicket(Ticket ticket, int userId);

	Ticket getTicket(int ticketId, int userId);

	List<Ticket> getTicketsByUserName(String userName, String status, String priority);

	boolean updateTicket(Ticket ticket, int userId);

	boolean deleteTicket(int ticketId, int userId);

	int createTicketHistory(TicketHistory ticketHistory, int ticketId, int userId);
	
	boolean assignAndUpdateNewTickets(List<Ticket> tickets, int userId);

}
