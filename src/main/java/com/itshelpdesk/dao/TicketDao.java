package com.itshelpdesk.dao;

import java.util.List;

import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;

public interface TicketDao {
	
	@Deprecated
	Ticket getTicket(int id, int userId);

	List<Ticket> getTicketsByAssignee(int userId, String status);
	
	List<Ticket> getTicketsByCreator(int userId, String status);

	Ticket getTicketByAssignee(int ticketId, int userId);

	Ticket getTicketByCreator(int ticketId, int userId);

	List<Ticket> getTickets(int userId, String status, String priority);

	boolean updateTicket(Ticket ticket, int userId);

	boolean updateTickets(List<Ticket> tickets);

	boolean deleteTicket(int ticketId, int userId);

	int createTicketHistory(TicketHistory ticketHistory, int ticketId, int userId);

	int createTicket(Ticket ticket, int userId);

	boolean createTicketAssignments(List<Ticket> tickets);

	List<Ticket> getTickets(String status, String priority);

	Ticket getTicket(int id);

}
