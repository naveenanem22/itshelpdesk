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

	/* CRUD Ticket START *******************************************************/

	/* Fetching operations START */
	List<Ticket> getTickets(String status, String priority);

	List<Ticket> getTickets(int userId, String status, String priority);

	List<Ticket> getTicketsByAssignee(int userId, String status);

	List<Ticket> getTicketsByCreator(int userId, String status);

	Page<Ticket> getPaginatedTicketsByCreator(int userId, String sortBy, String sortOrder, String status,
			Pageable pageable);

	Page<Ticket> getPaginatedTicketsByAssignee(int userId, String sortBy, String sortOrder, String status,
			Pageable pageable);

	Page<Ticket> getPaginatedTickets(int createdBy, boolean createdByMe, String sortBy, String sortOrder, String status,
			Pageable pageable, String priority, boolean isSearch, String searchText,
			List<String> searchFieldsList);

	/* Fetching operations END */

	/* Fetching single ticket START */
	Ticket getTicketByAssignee(int ticketId, int userId);

	Ticket getTicketByCreator(int ticketId, int userId);

	Ticket getTicket(int id);
	/* Fetching single ticket END */

	/* Update Ticket START */
	boolean updateTicket(Ticket ticket, boolean createdByMe, boolean assignedToMe, boolean managedByMe);

	boolean updateTickets(List<Ticket> tickets);

	boolean updateTicketByCreator(Ticket ticket, int userId);

	boolean updateTicketByAssignee(Ticket ticket, User assignee);

	/* Update Ticket END */

	/* Delete ticket START */
	boolean deleteTicket(int ticketId, int userId);
	/* Delete ticket END */

	/* Creating Ticket START */

	int createTicket(Ticket ticket, int userId);

	/* Creating Ticket END */

	/* CRUD Ticket END *******************************************************/

	/* CRUD TicketHistory - conversation START */
	/* Creating TicketHistory START */
	int createTicketHistory(TicketHistory ticketHistory, int ticketId, int userId);
	/* Creating TicketHistory END */
	/* CRUD TicketHistory - conversation END */

	/* CRUD TicketAssignment START */
	boolean createTicketAssignments(List<Ticket> tickets);

	boolean createTicketAssignment(Ticket ticket);
	/* CRUD TicketAssignment END */

}
