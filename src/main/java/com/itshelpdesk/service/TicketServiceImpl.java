package com.itshelpdesk.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.itshelpdesk.dao.TicketDao;
import com.itshelpdesk.model.Ticket;

@Service(value = "ticketServiceImpl")
public class TicketServiceImpl implements TicketService {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("ticketDaoImpl")
	TicketDao ticketDao;

	@Override
	public Ticket getTicket(int ticketId, int userId) {
		return ticketDao.getTicket(ticketId, userId);
	}

	@Override
	public List<Ticket> getTicketsByUserName(String userName) {
		LOGGER.debug("Fetching ticktets for the user with username: "+userName);		
		return ticketDao.getTickets(userName);
	}

	@Override
	public boolean updateTicket(Ticket ticket, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteTicket(int ticketId, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

}
