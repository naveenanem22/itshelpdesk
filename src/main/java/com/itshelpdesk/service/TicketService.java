package com.itshelpdesk.service;

import java.util.List;

import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;

public interface TicketService {

	int createTicket(Ticket ticket, String userName);

	Ticket getTicket(int ticketId, String userName);

	List<Ticket> getTicketsByUserName(String userName, String status, String priority);

	boolean updateTicket(Ticket ticket, String userName);

	boolean deleteTicket(int ticketId, String userName);

	int createTicketHistory(TicketHistory ticketHistory, int ticketId, String userName);

	boolean assignAndUpdateNewTickets(List<Ticket> tickets, String userName);
	
	boolean assignTicketByManager(Ticket ticket, String userName);

	List<Ticket> getTickets(String status, String priority);

	List<Ticket> getTicketsByAssignee(String userName, String status);
	
	List<Ticket> getTicketsByCreator(String userName, String status);

	Ticket getTicket(int ticketId);

	Ticket getTicketByAssignee(String userName, int ticketId);
	
	Ticket getTicketByCreator(String userName, int ticketId);

}
