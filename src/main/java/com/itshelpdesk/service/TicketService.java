package com.itshelpdesk.service;

import java.util.List;

import com.itshelpdesk.model.Ticket;

public interface TicketService {

	Ticket getTicket(int ticketId, int userId);

	List<Ticket> getTicketsByUserName(String userName);

	boolean updateTicket(Ticket ticket, int userId);

	boolean deleteTicket(int ticketId, int userId);

}
