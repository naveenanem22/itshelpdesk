package com.itshelpdesk.dao;

import java.util.List;

import com.itshelpdesk.model.Ticket;

public interface TicketDao {

	Ticket getTicket(int id, int userId);

	List<Ticket> getTickets(String userName);

	boolean updateTicket(Ticket ticket, int userId);

	boolean deleteTicket(int ticketId, int userId);

}
