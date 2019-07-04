package com.itshelpdesk.service;

import java.util.List;

import org.springframework.data.domain.Page;

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
	Page<Ticket> getPaginatedTickets(String sortBy, String sortOrder, String status, int pageNumber,
			int pageSize, String priority);

	List<Ticket> getTicketsByAssignee(String userName, String status);

	List<Ticket> getTicketsByCreator(String userName, String status);

	Page<Ticket> getPaginatedTicketsByCreator(String userName, String sortBy, String sortOrder, String status, int pageNumber,
			int pageSize);

	Ticket getTicket(int ticketId);

	Ticket getTicketByAssignee(String userName, int ticketId);

	Ticket getTicketByCreator(String userName, int ticketId);

}
