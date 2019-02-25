package com.itshelpdesk.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.itshelpdesk.dao.ItsHelpDeskAttachmentDao;
import com.itshelpdesk.dao.TicketDao;
import com.itshelpdesk.dao.TicketHistoryAttachmentDao;
import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;
import com.pc.custom.exceptions.InternalServerException;
import com.pc.model.Attachment;
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

	@Override
	public Ticket getTicket(int ticketId, int userId) {
		return ticketDao.getTicket(ticketId, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Ticket> getTicketsByUserName(String userName) {
		LOGGER.debug("Fetching ticktets for the user with username: " + userName);
		return ticketDao.getTickets(userName);
	}

	@Override
	@Transactional
	public boolean updateTicket(Ticket ticket, int userId) {
		/*
		 * Update ticket table only when there is an update to atleast one of -sts_name,
		 * dept_id, pty_name, tkttype_name or svctype_name
		 */
		List<Integer> attachmentIds = null;
		int ticketHistoryId = 0;

		if (!(ticket.getStatus() == null && ticket.getDepartment() == null && ticket.getPriority() == null
				&& ticket.getServiceCategory() == null && ticket.getType() == null)) {

			LOGGER.debug("Updating ticket-details: {} for the given userId: {}", ticket, userId);
			ticketDao.updateTicket(ticket, userId);

		}

		// Create tickethistory item in table if tickethistory is present
		if (ticket.getTicketHistoryList() != null)
			ticketHistoryId = ticketDao.createTicketHistory(ticket.getTicketHistoryList().get(0), ticket.getId(),
					userId);

		// Upload attachments only if it has attachment(s) to be uploaded
		List<MultipartFile> files = new ArrayList<MultipartFile>();
		List<String> fileNames = new ArrayList<String>();
		files = ticket.getTicketHistoryList().get(0).getFiles();
		if (files != null && !files.isEmpty()) {
			fileNames = fileStorageService.storeMultipleFile(files);

			// Create List<Attachment> from fileNames
			List<Attachment> attachments = new ArrayList<Attachment>();
			Attachment attachment = new Attachment();
			fileNames.forEach(fileName -> {
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
	public boolean deleteTicket(int ticketId, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int createTicketHistory(TicketHistory ticketHistory, int ticketId, int userId) {
		return ticketDao.createTicketHistory(ticketHistory, ticketId, userId);
	}

}
