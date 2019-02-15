package com.itshelpdesk.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;
import com.pc.custom.exceptions.InternalServerException;
import com.pc.custom.exceptions.RecordNotFoundException;
import com.pc.model.Department;

@Repository(value = "ticketDaoImpl")
public class TicketDaoImpl implements TicketDao {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Ticket getTicket(int id, int userId) {
		List<Ticket> tickets;
		try {
			LOGGER.debug("Fetching ticket with id:{} for the userId: {}", id, userId);

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ticket.*, department.dept_name, ");
			sql.append("priority.pty_name, status.sts_name, svctype_name, tkttype_name FROM ticket ");
			sql.append("INNER JOIN priority ON tkt_pty_id = pty_id ");
			sql.append("INNER JOIN status ON tkt_sts_id = sts_id ");
			sql.append("INNER JOIN user ON tkt_created_by = u_id ");
			sql.append("INNER JOIN department ON tkt_dept_id = dept_id ");
			sql.append("INNER JOIN servicetype ON tkt_svctype_id = svctype_id ");
			sql.append("INNER JOIN tickettype ON tkt_tkttype_id = tkttype_id ");

			sql.append("WHERE tkt_id = :tkt_id && u_id =:u_id");

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tkt_id", id);
			paramMap.put("u_id", userId);

			tickets = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new TicketDetailsRowMapper());

		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new InternalServerException("Unexpected error occurred while fetching the Interview details.");
		}

		if (tickets.size() == 0) {
			System.out.println("ticket not found exception on the way...");
			throw new RecordNotFoundException("No Ticket with the id: " + id + " found.");
		} else if (tickets.size() == 1) {
			// Fetching the ticket-history
			List<TicketHistory> ticketHistoryList = getTicketHistory(id);

			// Attaching ticket-history to ticket
			tickets.get(0).setTicketHistoryList(ticketHistoryList);

			// Returning the fetched ticket
			return tickets.get(0);
		}

		else
			throw new InternalServerException("Unexpected error occurred while fetching the Interview details.");
	}

	public List<TicketHistory> getTicketHistory(int ticketId) {
		LOGGER.debug("Fetching tickethistory for ticket with id:{}", ticketId);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ticketconversation.*, user.u_username FROM ticketconversation ");
		sql.append("INNER JOIN ticket ON tktconv_tkt_id = tkt_id ");
		sql.append("INNER JOIN user ON tktconv_author = u_id ");
		sql.append("WHERE tkt_id =:tkt_id");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tkt_id", ticketId);

		List<TicketHistory> ticketHistoryList = namedParameterJdbcTemplate.query(sql.toString(), paramMap,
				new TicketHistoryRowMapper());
		return ticketHistoryList;
	}

	@Override
	public List<Ticket> getTickets(String userName) {
		LOGGER.debug("Fetching tickets for the user with username: " + userName);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT tkt_id, tkt_title, tkt_updated_date, sts_name FROM ticket ");
		sql.append("INNER JOIN user ON tkt_created_by = u_id ");
		sql.append("INNER JOIN status ON tkt_sts_id = sts_id ");
		sql.append("WHERE user.u_username =:u_username");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("u_username", userName);
		List<Ticket> tickets = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new TicketsRowMapper());
		LOGGER.debug("Tickets fetched: " + tickets.toString());
		return tickets;
	}

	@Override
	public boolean updateTicket(Ticket ticket, int userId) {
		int numberOfRowsAffected;
		LOGGER.debug("Updating ticket with id: {} for the user with id: {}", ticket.getId(), userId);

		StringBuilder sql = new StringBuilder();
		// Update the ticket status in ticket table
		sql.append("UPDATE ticket SET tkt_updated_date =:tkt_updated_date");

		if (ticket.getStatus() != null)
			sql.append(",tkt_sts_id = (SELECT sts_id FROM status WHERE sts_name =:sts_name)");
		if (ticket.getDepartment() != null)
			sql.append("tkt_dept_id =:dept_id), ");
		if (ticket.getPriority() != null)
			sql.append(",tkt_pty_id = (SELECT pty_id FROM priority WHERE pty_name =:pty_name)");
		if (ticket.getType() != null)
			sql.append(",tkt_tkttype_id = (SELECT tkt_id FROM tickettype WHERE tkttype_name =:tkttype_name)");
		if (ticket.getServiceCategory() != null)
			sql.append(",tkt_svctype_id = (SELECT svctype_id FROM servicetype WHERE svctype_name =:svctype_name)");

		sql.append(" WHERE tkt_id =:tkt_id && tkt_created_by =:tkt_created_by");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tkt_updated_date", ticket.getUpdatedDate());
		if (ticket.getStatus() != null)
			paramMap.put("sts_name", "Closed");
		if (ticket.getDepartment() != null)
			paramMap.put("dept_id", ticket.getDepartment().getId());
		if (ticket.getPriority() != null)
			paramMap.put("pty_name", ticket.getPriority());
		if (ticket.getType() != null)
			paramMap.put("tkttype_name", ticket.getType());
		if (ticket.getServiceCategory() != null)
			paramMap.put("svctype_name", ticket.getServiceCategory());
		paramMap.put("tkt_id", ticket.getId());
		paramMap.put("tkt_created_by", userId);
		numberOfRowsAffected = namedParameterJdbcTemplate.update(sql.toString(), paramMap);

		if (numberOfRowsAffected == 1)
			return true;

		return false;
	}

	public boolean createTicketHistory(TicketHistory ticketHistory, int ticketId, int userId) {
		LOGGER.debug("Creating ticket-history record: {} for the given ticket with id: {} by user with id: {}",
				ticketHistory, ticketId, userId);
		int numberOfRowsAffected;
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ticketconversation ");
		sql.append("(");
		sql.append("tktconv_tkt_id, tktconv_author, tktconv_message, tktconv_commented_on");
		sql.append(")");
		sql.append("VALUES ");
		sql.append("(");
		sql.append(":tktconv_tkt_id, :tktconv_author, :tktconv_message, :tktconv_commented_on");
		sql.append(")");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tktconv_tkt_id", ticketId);
		paramMap.put("tktconv_author", userId);
		paramMap.put("tktconv_message", ticketHistory.getComment());
		paramMap.put("tktconv_commented_on", ticketHistory.getCommentedDate());

		numberOfRowsAffected = namedParameterJdbcTemplate.update(sql.toString(), paramMap);

		if (numberOfRowsAffected == 1)
			return true;

		return false;
	}

	@Override
	public boolean deleteTicket(int ticketId, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

	private static class TicketDetailsRowMapper implements RowMapper<Ticket> {

		@Override
		public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
			Ticket ticket = new Ticket();
			Department department = new Department();
			department.setId(rs.getInt("tkt_dept_id"));
			ticket.setCreatedDate(rs.getTimestamp("tkt_created_date").toLocalDateTime());
			ticket.setDepartment(department);
			ticket.setDescription(rs.getString("tkt_description"));
			ticket.setId(rs.getInt("tkt_id"));
			ticket.setPriority(rs.getString("pty_name"));
			ticket.setServiceCategory(rs.getString("svctype_name"));
			ticket.setStatus(rs.getString("sts_name"));
			ticket.setTitle(rs.getString("tkt_title"));
			ticket.setType(rs.getString("tkttype_name"));
			ticket.setUpdatedDate(rs.getTimestamp("tkt_updated_date").toLocalDateTime());

			return ticket;
		}

	}

	private static class TicketsRowMapper implements RowMapper<Ticket> {

		@Override
		public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
			Ticket ticket = new Ticket();
			ticket.setId(rs.getInt("tkt_id"));
			ticket.setStatus(rs.getString("sts_name"));
			ticket.setTitle(rs.getString("tkt_title"));
			ticket.setUpdatedDate(rs.getTimestamp("tkt_updated_date").toLocalDateTime());
			return ticket;
		}

	}

	private static class TicketHistoryRowMapper implements RowMapper<TicketHistory> {

		@Override
		public TicketHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
			TicketHistory ticketHistoryList = new TicketHistory();
			ticketHistoryList.setAuthorName(rs.getString("u_username"));
			ticketHistoryList.setComment(rs.getString("tktconv_message"));
			ticketHistoryList.setCommentedDate(rs.getTimestamp("tktconv_commented_on").toLocalDateTime());
			ticketHistoryList.setId(rs.getInt("tktconv_id"));

			return ticketHistoryList;
		}

	}

}
