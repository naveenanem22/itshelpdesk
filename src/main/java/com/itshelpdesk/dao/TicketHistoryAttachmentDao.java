package com.itshelpdesk.dao;

import java.util.List;

public interface TicketHistoryAttachmentDao {

	boolean createTicketHistoryAttachment(List<Integer> attachmentIds, int ticketHistoryId);

}
