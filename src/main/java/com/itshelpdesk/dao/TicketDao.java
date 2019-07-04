package com.itshelpdesk.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;
import com.pc.model.User;

public interface TicketDao {

	@Deprecated
	Ticket getTicket(int id, int userId);

	List<Ticket> getTicketsByAssignee(int userId, String status);

	List<Ticket> getTicketsByCreator(int userId, String status);

	Page<Ticket> getPaginatedTicketsByCreator(int userId, String sortBy, String sortOrder, String status,
			Pageable pageable);

	Ticket getTicketByAssignee(int ticketId, int userId);

	Ticket getTicketByCreator(int ticketId, int userId);

	List<Ticket> getTickets(int userId, String status, String priority);

	boolean updateTicket(Ticket ticket, int userId);

	boolean updateTickets(List<Ticket> tickets);

	boolean updateTicketByManager(Ticket ticket);

	boolean updateTicketByCreator(Ticket ticket, User createdBy);

	boolean updateTicketByAssignee(Ticket ticket, User assignee);

	boolean deleteTicket(int ticketId, int userId);

	int createTicketHistory(TicketHistory ticketHistory, int ticketId, int userId);

	int createTicket(Ticket ticket, int userId);

	boolean createTicketAssignments(List<Ticket> tickets);

	boolean createTicketAssignment(Ticket ticket);

	List<Ticket> getTickets(String status, String priority);

	Page<Ticket> getPaginatedTickets(String sortBy, String sortOrder, String status, Pageable pageable,
			String priority);

	Ticket getTicket(int id);

}
