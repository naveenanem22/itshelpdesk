package com.itshelpdesk.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.itshelpdesk.dao.ItsHelpDeskAttachmentDao;
import com.itshelpdesk.dao.TicketDao;
import com.itshelpdesk.dao.TicketHistoryAttachmentDao;
import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;
import com.pc.model.Attachment;
import com.pc.model.User;
import com.pc.services.FileStorageService;

@Service(value = "ticketServiceImpl")
public class TicketServiceImpl implements TicketService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("ticketDaoImpl")
	TicketDao ticketDao;

	@Autowired
	FileStorageService fileStorageService;

	@Autowired
	@Qualifier("ticketHistoryAttachmentDaoImpl")
	TicketHistoryAttachmentDao ticketHistoryDao;

	@Autowired
	@Qualifier("itsHelpDeskAttachmentDaoImpl")
	ItsHelpDeskAttachmentDao itsHelpDeskAttachmentDao;

	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;

	@Transactional
	public int createTicket(Ticket ticket, String userName) {
		LOGGER.debug("Fetching user for the given userName: {}", userName);
		int createdTicketId;
		int ticketHistoryId = 0;
		List<Integer> attachmentIds = null;
		
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		LOGGER.debug("Creating ticket with the details: {} by the user with id: {}", ticket, user.getId());
		createdTicketId = ticketDao.createTicket(ticket, user.getId());
		
		LOGGER.debug("Created ticketId: {}", createdTicketId);

		/*
		 * Creating conversation history with additional info and attachments once we
		 * the ticket is created successfully
		 */

		LOGGER.debug("Creating TicketHistory record with additional-info and attachments");
		
		//Setting createdTicketId as id of the ticket
		ticket.setId(createdTicketId);
		
		
		// Create tickethistory item in table if tickethistory is present
		if (ticket.getTicketHistoryList() != null)
			ticketHistoryId = ticketDao.createTicketHistory(ticket.getTicketHistoryList().get(0), ticket.getId(),
					user.getId());

		// Upload attachments only if it has attachment(s) to be uploaded
		List<MultipartFile> files = new ArrayList<MultipartFile>();
		List<String> fileNames = new ArrayList<String>();
		files = ticket.getTicketHistoryList().get(0).getFiles();
		if (files != null && !files.isEmpty()) {
			fileNames = fileStorageService.storeMultipleFile(files);

			// Create List<Attachment> from fileNames
			List<Attachment> attachments = new ArrayList<Attachment>();

			fileNames.forEach(fileName -> {
				Attachment attachment = new Attachment();
				attachment.setName(fileName);
				attachments.add(attachment);
			});

			// Code to persist the fileNames data to db
			attachmentIds = itsHelpDeskAttachmentDao.createAttachments(attachments);
			LOGGER.debug("Ids of the inserted attachment records: {}", attachmentIds.toString());

		}

		// Create records in ticketconv-attachment table if ticket has attachments
		if (attachmentIds != null && !attachmentIds.isEmpty() && ticketHistoryId != 0)
			ticketHistoryDao.createTicketHistoryAttachment(attachmentIds, ticketHistoryId);
		
		//return the createdTicketId
		return createdTicketId;

	}

	@Override
	@Transactional(readOnly = true)
	public Ticket getTicket(int ticketId, String userName) {

		LOGGER.debug("Fetching user for the given userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		return ticketDao.getTicket(ticketId, user.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public List<Ticket> getTicketsByUserName(String userName, String status, String priority) {
		LOGGER.debug("Fetching user for the given userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		LOGGER.debug("Fetching ticktets for the user with userId: " + user.getId());
		return ticketDao.getTickets(user.getId(), status, priority);
	}

	@Override
	@Transactional
	public boolean updateTicket(Ticket ticket, String userName) {
		// Fetch user by given userName
		LOGGER.debug("Fetching user by userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		List<Integer> attachmentIds = null;
		int ticketHistoryId = 0;

		LOGGER.debug("Updating ticket-details: {} for the given userId: {}", ticket, user.getId());
		ticketDao.updateTicket(ticket, user.getId());

		// Create tickethistory item in table if tickethistory is present
		if (ticket.getTicketHistoryList() != null)
			ticketHistoryId = ticketDao.createTicketHistory(ticket.getTicketHistoryList().get(0), ticket.getId(),
					user.getId());

		// Upload attachments only if it has attachment(s) to be uploaded
		List<MultipartFile> files = new ArrayList<MultipartFile>();
		List<String> fileNames = new ArrayList<String>();
		files = ticket.getTicketHistoryList().get(0).getFiles();
		if (files != null && !files.isEmpty()) {
			fileNames = fileStorageService.storeMultipleFile(files);

			// Create List<Attachment> from fileNames
			List<Attachment> attachments = new ArrayList<Attachment>();

			fileNames.forEach(fileName -> {
				Attachment attachment = new Attachment();
				attachment.setName(fileName);
				attachments.add(attachment);
			});

			// Code to persist the fileNames data to db
			attachmentIds = itsHelpDeskAttachmentDao.createAttachments(attachments);
			LOGGER.debug("Ids of the inserted attachment records: {}", attachmentIds.toString());

		}

		// Create records in ticketconv-attachment table if ticket has attachments
		if (attachmentIds != null && !attachmentIds.isEmpty() && ticketHistoryId != 0)
			ticketHistoryDao.createTicketHistoryAttachment(attachmentIds, ticketHistoryId);

		/*
		 * if (files.size() > 0) files.forEach(file -> {
		 * fileStorageService.storeFile(file); });
		 */
		return true;

	}

	@Override
	public boolean deleteTicket(int ticketId, String userName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int createTicketHistory(TicketHistory ticketHistory, int ticketId, String userName) {

		LOGGER.debug("Fetching user for the given userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		return ticketDao.createTicketHistory(ticketHistory, ticketId, user.getId());
	}

	@Override
	@Transactional
	public boolean assignTicketByManager(Ticket ticket, String userName) {

		LOGGER.debug("Fetching user for the given userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		// Set audit-logging for the ticket
		LOGGER.debug("Setting audit-logging for updatedDate");
		ticket.setUpdatedDate(LocalDateTime.now(ZoneOffset.UTC));

		// Fetching assigedTo user based on username
		LOGGER.debug("Fetching assignedTo user data");
		User assignedTo = userService.getUserByUserName(ticket.getAssignedTo().getUserName());
		LOGGER.debug("Fetched assignedTo user: {}", user);

		// Updating the assignedTo field of the ticket
		ticket.setAssignedTo(assignedTo);

		// Creating assignment record
		ticketDao.createTicketAssignment(ticket);
		LOGGER.debug("ticket-assignment successful.");

		// Updating ticket's status
		LOGGER.debug("Updating ticket: {} with the status: {} by the user: {}", ticket, ticket.getStatus(), user);
		ticketDao.updateTicketByManager(ticket);
		LOGGER.debug("ticket-status update successful.");

		return true;
	}

	@Override
	@Transactional
	public boolean assignAndUpdateNewTickets(List<Ticket> tickets, String userName) {
		LOGGER.debug("Fetching user for the given userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		// Assign new tickets to engineers and update status
		LOGGER.debug("Update tickets: {} by the user with userId: {}", tickets.toString(), user.getId());

		// TODO Fetch userId based on userName or user's firstName or lastName
		// Update each ticket with fetched userId under assignedTo field
		tickets.forEach(ticket -> {
			User assignedTo = new User();
			assignedTo.setId(2);
			ticket.setAssignedTo(assignedTo);
		});

		// Assign tickets
		if (ticketDao.createTicketAssignments(tickets))
			// Update ticket status
			ticketDao.updateTickets(tickets);

		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Ticket> getTickets(String status, String priority) {
		LOGGER.debug("Fetching tickets");
		List<Ticket> tickets = ticketDao.getTickets(status, priority);
		LOGGER.debug("Fetched tickets: {}", tickets);
		return tickets;
	}

	@Override
	public Page<Ticket> getPaginatedTickets(String userName, boolean createdByMe, String sortBy, String sortOrder,
			String status, int pageNumber, int pageSize, String priority) {

		LOGGER.debug("Fetching user for the given userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		LOGGER.debug("Fetching tickets");

		LOGGER.debug("Creating pageable object with pageNumber: {} and size: {}", pageNumber, pageSize);
		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		Page<Ticket> paginatedTickets = ticketDao.getPaginatedTickets(user.getId(), createdByMe, sortBy, sortOrder,
				status, pageable, priority);

		LOGGER.debug("Tickets fetched: {}", paginatedTickets.toString());
		return paginatedTickets;
	}

	@Override
	public Ticket getTicket(int ticketId) {
		LOGGER.debug("Fetching ticket details with id: {}", ticketId);
		Ticket ticket = ticketDao.getTicket(ticketId);
		LOGGER.debug("Fetched ticket details: {}", ticket);
		return ticket;
	}

	@Override
	public List<Ticket> getTicketsByAssignee(String userName, String status) {
		LOGGER.debug("Fetching user for the given userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		LOGGER.debug("Fetching tickets assigned to the user with id: {} with the status: {}", user.getId(), status);
		List<Ticket> tickets = ticketDao.getTicketsByAssignee(user.getId(), status);

		return tickets;
	}

	@Override
	public Ticket getTicketByAssignee(String userName, int ticketId) {

		LOGGER.debug("Fetching user for the given userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		LOGGER.debug("Fetching ticket details assigned to the user with id: {}", user.getId());
		Ticket ticket = ticketDao.getTicketByAssignee(ticketId, user.getId());
		LOGGER.debug("Fetched ticket details: {}", ticket.toString());
		return ticket;
	}

	@Override
	public List<Ticket> getTicketsByCreator(String userName, String status) {
		LOGGER.debug("Fetching user for the given userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		LOGGER.debug("Fetching tickets created by the user with userId: {}", user.getId());
		List<Ticket> tickets = ticketDao.getTicketsByCreator(user.getId(), status);
		LOGGER.debug("Tickets fetched: {}", tickets.toString());
		return tickets;
	}

	@Override
	public Page<Ticket> getPaginatedTicketsByCreator(String userName, String sortBy, String sortOrder, String status,
			int pageNumber, int pageSize) {
		LOGGER.debug("Fetching user for the given userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		LOGGER.debug("Creating pageable object with pageNumber: {} and size: {}", pageNumber, pageSize);
		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		LOGGER.debug("Fetching tickets created by the user with userId: {}", user.getId());
		Page<Ticket> paginatedTickets = ticketDao.getPaginatedTicketsByCreator(user.getId(), sortBy, sortOrder, status,
				pageable);
		LOGGER.debug("Tickets fetched: {}", paginatedTickets.toString());
		return paginatedTickets;
	}

	@Override
	public Ticket getTicketByCreator(String userName, int ticketId) {
		LOGGER.debug("Fetching user for the given userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		LOGGER.debug("Fetched user: {}", user);

		LOGGER.debug("Fetching ticket details created by the user with id: {}", user.getId());
		Ticket ticket = ticketDao.getTicketByCreator(ticketId, user.getId());
		return ticket;
	}

}
