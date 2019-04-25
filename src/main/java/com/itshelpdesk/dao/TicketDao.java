package com.itshelpdesk.dao;

import java.util.List;

import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;

public interface TicketDao {

	Ticket getTicket(int id, int userId);

	List<Ticket> getTickets(int userId, String status, String priority);

	boolean updateTicket(Ticket ticket, int userId);

	boolean updateMultipleTickets(List<Ticket> tickets, int userId);

	boolean deleteTicket(int ticketId, int userId);

	int createTicketHistory(TicketHistory ticketHistory, int ticketId, int userId);

	int createTicket(Ticket ticket, int userId);

	boolean createTicketAssignments(List<Ticket> tickets);

}
