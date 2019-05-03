package com.itshelpdesk.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;
import com.pc.custom.exceptions.InternalServerException;
import com.pc.custom.exceptions.RecordNotFoundException;
import com.pc.model.Attachment;
import com.pc.model.Department;

@Repository(value = "ticketDaoImpl")
public class TicketDaoImpl implements TicketDao {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("itsHelpDeskAttachmentDaoImpl")
	private ItsHelpDeskAttachmentDao itsHelpDeskAttachmentDao;

	@Override
	public List<Ticket> getTickets(String status, String priority) {
		LOGGER.debug("Fetching tickets");

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT tkt_id, tkt_title, tkt_updated_date, sts_name, pty_name FROM ticket ");
		sql.append("INNER JOIN status ON tkt_sts_id = sts_id ");
		sql.append("INNER JOIN priority ON tkt_pty_id = pty_id ");
		if (status != null && !(status.isEmpty()))
			sql.append(" && sts_name = :sts_name");
		if (priority != null && !(priority.isEmpty()))
			sql.append(" && pty_name = :pty_name");

		LOGGER.debug("Fetching the tickets using query: {}", sql.toString());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sts_name", status);
		paramMap.put("pty_name", priority);
		List<Ticket> tickets = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new TicketsRowMapper());
		LOGGER.debug("Tickets fetched: " + tickets.toString());
		return tickets;
	}

	@Override
	public Ticket getTicket(int id) {
		List<Ticket> tickets;
		try {
			LOGGER.debug("Fetching ticket with id:{}", id);

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ticket.*, department.dept_name, ");
			sql.append("priority.pty_name, status.sts_name, svctype_name, tkttype_name FROM ticket ");
			sql.append("INNER JOIN priority ON tkt_pty_id = pty_id ");
			sql.append("INNER JOIN status ON tkt_sts_id = sts_id ");
			sql.append("INNER JOIN department ON tkt_dept_id = dept_id ");
			sql.append("INNER JOIN servicetype ON tkt_svctype_id = svctype_id ");
			sql.append("INNER JOIN tickettype ON tkt_tkttype_id = tkttype_id ");

			sql.append("WHERE tkt_id = :tkt_id");

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tkt_id", id);

			tickets = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new TicketDetailsRowMapper());

		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalServerException("Unexpected error occurred while fetching the Interview details.");
		}

		if (tickets.size() == 0) {
			throw new RecordNotFoundException("No Ticket with the id: " + id + " found.");
		} else if (tickets.size() == 1) {
			// Printing ticket details:
			LOGGER.debug("Fetched ticket details: {}", tickets.get(0).toString());
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
			// Printing ticket details:
			LOGGER.debug("Fetched ticket details: {}", tickets.get(0).toString());
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

	public int createTicket(Ticket ticket, int userId) {
		int numberOfRowsAffected;
		// Setting audit field data
		ticket.setCreatedDate(LocalDateTime.now(ZoneOffset.UTC));
		ticket.setUpdatedDate(LocalDateTime.now(ZoneOffset.UTC));
		StringBuilder sql = new StringBuilder();

		sql.append("INSERT INTO ticket ");
		sql.append("(");
		sql.append("tkt_updated_date, tkt_created_date");
		sql.append(", tkt_title, tkt_description, tkt_dept_id, ");
		sql.append("tkt_pty_id, tkt_tkttype_id, tkt_svctype_id,");
		sql.append("tkt_created_by,");
		sql.append("tkt_sts_id");
		sql.append(")");
		sql.append("VALUES ");
		sql.append("(");
		sql.append(":tkt_updated_date, :tkt_created_date, :tkt_title, :tkt_description");
		sql.append(",(SELECT dept_id FROM department WHERE dept_name =:dept_name)");
		sql.append(",(SELECT pty_id FROM priority WHERE pty_name =:pty_name)");
		sql.append(",(SELECT tkttype_id FROM tickettype WHERE tkttype_name =:tkttype_name)");
		sql.append(",(SELECT svctype_id FROM servicetype WHERE svctype_name =:svctype_name)");
		sql.append(",:tkt_created_by");
		sql.append(",(SELECT sts_id FROM status WHERE sts_name =:sts_name)");
		sql.append(")");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tkt_updated_date", ticket.getUpdatedDate());
		paramMap.put("tkt_created_date", ticket.getCreatedDate());
		paramMap.put("tkt_title", ticket.getTitle());
		paramMap.put("tkt_description", ticket.getDescription());
		paramMap.put("dept_name", ticket.getDepartment().getName());
		paramMap.put("pty_name", ticket.getPriority());
		paramMap.put("sts_name", ticket.getStatus());
		paramMap.put("svctype_name", ticket.getServiceCategory());
		paramMap.put("tkttype_name", ticket.getType());
		paramMap.put("tkt_created_by", userId);

		numberOfRowsAffected = namedParameterJdbcTemplate.update(sql.toString(), paramMap);

		if (numberOfRowsAffected == 1)
			return 1;
		else
			throw new InternalServerException("Unexpected error occured while creating a ticket.");

	}

	@Override
	public List<Ticket> getTickets(int userId, String status, String priority) {
		LOGGER.debug("Fetching tickets for the user with userId: " + userId);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT tkt_id, tkt_title, tkt_updated_date, sts_name, pty_name FROM ticket ");
		sql.append("INNER JOIN user ON tkt_created_by = u_id ");
		sql.append("INNER JOIN status ON tkt_sts_id = sts_id ");
		sql.append("INNER JOIN priority ON tkt_pty_id = pty_id ");
		sql.append("WHERE user.u_id =:u_id");
		if (status != null && !(status.isEmpty()))
			sql.append(" && sts_name = :sts_name");
		if (priority != null && !(priority.isEmpty()))
			sql.append(" && pty_name = :pty_name");

		LOGGER.debug("Fetching the tickets using query: {}", sql.toString());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("u_id", userId);
		paramMap.put("sts_name", status);
		paramMap.put("pty_name", priority);
		List<Ticket> tickets = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new TicketsRowMapper());
		LOGGER.debug("Tickets fetched: " + tickets.toString());
		return tickets;
	}

	@Override
	public boolean updateTicket(Ticket ticket, int userId) {
		int numberOfRowsAffected;

		// Set auditlogging fields data
		ticket.setUpdatedDate(LocalDateTime.now(ZoneOffset.UTC));

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

	@Override
	public boolean deleteTicket(int ticketId, int userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateTickets(List<Ticket> tickets) {
		// Updating multiple tickets in ticket table
		LOGGER.debug("Updating status of tickets: {}", tickets.toString());
		StringBuilder sql = new StringBuilder();
		sql.append(
				"UPDATE ticket SET tkt_sts_id = (SELECT sts_id FROM status WHERE sts_name like :sts_name) WHERE tkt_id =:tkt_id");

		List<Map<String, Object>> batchValues = new ArrayList<>(tickets.size());
		tickets.forEach(ticket -> {
			batchValues.add(new MapSqlParameterSource("tkt_id", ticket.getId()).addValue("sts_name", ticket.getStatus())
					.getValues());

		});

		namedParameterJdbcTemplate.batchUpdate(sql.toString(), batchValues.toArray(new Map[tickets.size()]));
		return true;
	}

	@Override
	public List<Ticket> getTicketsByAssignee(int assigneeId, String status) {
		LOGGER.debug("Fetching tickets assigned to the user with userId: {}, status: {}", assigneeId, status);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT tkt_id, tkt_title, tkt_updated_date, sts_name, pty_name FROM ticket ");
		sql.append("INNER JOIN status ON tkt_sts_id = sts_id ");
		sql.append("INNER JOIN priority ON tkt_pty_id = pty_id ");
		sql.append("INNER JOIN viewticketsassignedtouser ON tkt_id = tatu_tkt_id ");
		sql.append("WHERE tatu_assigned_to =:tatu_assigned_to");
		if (status != null && !(status.isEmpty()) && !(status.equalsIgnoreCase("all")))
			sql.append(" && sts_name = :sts_name");

		LOGGER.debug("Fetching the tickets using query: {}", sql.toString());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tatu_assigned_to", assigneeId);
		paramMap.put("sts_name", status);
		List<Ticket> tickets = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new TicketsRowMapper());
		LOGGER.debug("Tickets fetched: " + tickets.toString());
		return tickets;
	}
	
	@Override
	public List<Ticket> getTicketsByCreator(int createdBy, String status) {
		LOGGER.debug("Fetching tickets created by the user with userId: {}, status: {}", createdBy, status);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT tkt_id, tkt_title, tkt_updated_date, sts_name, pty_name FROM ticket ");
		sql.append("INNER JOIN status ON tkt_sts_id = sts_id ");
		sql.append("INNER JOIN priority ON tkt_pty_id = pty_id ");
		sql.append("WHERE tkt_created_by =:tkt_created_by");
		if (status != null && !(status.isEmpty()) && !(status.equalsIgnoreCase("all")))
			sql.append(" && sts_name = :sts_name");

		LOGGER.debug("Fetching the tickets using query: {}", sql.toString());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tkt_created_by", createdBy);
		paramMap.put("sts_name", status);
		List<Ticket> tickets = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new TicketsRowMapper());
		LOGGER.debug("Tickets fetched: " + tickets.toString());
		return tickets;
	}

	@Override
	public Ticket getTicketByCreator(int ticketId, int createdBy) {
		List<Ticket> tickets;
		try {
			LOGGER.debug("Fetching ticket with id:{} created by the user with userId: {}", ticketId, createdBy);

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ticket.*, department.dept_name, ");
			sql.append("priority.pty_name, status.sts_name, svctype_name, tkttype_name FROM ticket ");
			sql.append("INNER JOIN priority ON tkt_pty_id = pty_id ");
			sql.append("INNER JOIN status ON tkt_sts_id = sts_id ");
			sql.append("INNER JOIN department ON tkt_dept_id = dept_id ");
			sql.append("INNER JOIN servicetype ON tkt_svctype_id = svctype_id ");
			sql.append("INNER JOIN tickettype ON tkt_tkttype_id = tkttype_id ");
			sql.append("WHERE tkt_id = :tkt_id && tkt_created_by = :tkt_created_by");

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tkt_id", ticketId);
			paramMap.put("tkt_created_by", createdBy);

			tickets = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new TicketDetailsRowMapper());

		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalServerException("Unexpected error occurred while fetching the ticket details.");
		}

		if (tickets.size() == 0) {
			throw new RecordNotFoundException("No Ticket with the id: " + ticketId + " found.");
		} else if (tickets.size() == 1) {
			// Printing ticket details:
			LOGGER.debug("Fetched ticket details: {}", tickets.get(0).toString());
			// Fetching the ticket-history
			List<TicketHistory> ticketHistoryList = getTicketHistory(ticketId);

			// Attaching ticket-history to ticket
			tickets.get(0).setTicketHistoryList(ticketHistoryList);

			// Returning the fetched ticket
			return tickets.get(0);
		}

		else
			throw new InternalServerException("Unexpected error occurred while fetching the Interview details.");

	}

	@Override
	public Ticket getTicketByAssignee(int ticketId, int assignedTo) {
		List<Ticket> tickets;
		try {
			LOGGER.debug("Fetching ticket with id:{} assigned to: {}", ticketId, assignedTo);

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ticket.*, department.dept_name, ");
			sql.append("priority.pty_name, status.sts_name, svctype_name, tkttype_name FROM ticket ");
			sql.append("INNER JOIN priority ON tkt_pty_id = pty_id ");
			sql.append("INNER JOIN status ON tkt_sts_id = sts_id ");
			sql.append("INNER JOIN department ON tkt_dept_id = dept_id ");
			sql.append("INNER JOIN servicetype ON tkt_svctype_id = svctype_id ");
			sql.append("INNER JOIN tickettype ON tkt_tkttype_id = tkttype_id ");
			sql.append("INNER JOIN viewticketsassignedtouser ON tkt_id = tatu_tkt_id ");
			sql.append("WHERE tkt_id = :tkt_id && tatu_assigned_to = :tatu_assigned_to");

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("tkt_id", ticketId);
			paramMap.put("tatu_assigned_to", assignedTo);

			tickets = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new TicketDetailsRowMapper());

		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalServerException("Unexpected error occurred while fetching ticket details.");
		}

		if (tickets.size() == 0) {
			throw new RecordNotFoundException("No Ticket with the id: " + ticketId + " found.");
		} else if (tickets.size() == 1) {
			// Printing ticket details:
			LOGGER.debug("Fetched ticket details: {}", tickets.get(0).toString());
			// Fetching the ticket-history
			List<TicketHistory> ticketHistoryList = getTicketHistory(ticketId);

			// Attaching ticket-history to ticket
			tickets.get(0).setTicketHistoryList(ticketHistoryList);

			// Returning the fetched ticket
			return tickets.get(0);
		}

		else
			throw new InternalServerException("Unexpected error occurred while fetching ticket details.");
	}

	/*********** CRUD operations for ticketassignment table *******/

	public boolean createTicketAssignments(List<Ticket> tickets) {
		// Insert ticketassignment records as a batch
		LOGGER.debug("Inserting ticketassignment records");
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ticketassignment ");
		sql.append("(");
		sql.append("ta_tkt_id, ta_assigned_to, ta_created_date");
		sql.append(")");
		sql.append("VALUES ");
		sql.append("(");
		sql.append(":ta_tkt_id, :ta_assigned_to, :ta_created_date");
		sql.append(")");

		List<Map<String, Object>> batchValues = new ArrayList<>(tickets.size());
		tickets.forEach(ticket -> {
			batchValues.add(new MapSqlParameterSource("ta_tkt_id", ticket.getId())
					.addValue("ta_assigned_to", ticket.getAssignedTo().getId())
					.addValue("ta_created_date", ticket.getCreatedDate()).getValues());

		});

		namedParameterJdbcTemplate.batchUpdate(sql.toString(), batchValues.toArray(new Map[tickets.size()]));
		return true;
	}

	public boolean createTicketAssignment() {
		return false;
	}

	/*********** CRUD operations for tickethistory table *******/
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

		// Fetching & Attachments ticket-hitory attachments
		ticketHistoryList.forEach(ticketHistoryItem -> {
			LOGGER.debug("Fetching attachment(s) for ticket-history with id: {}", ticketHistoryItem.getId());
			List<Attachment> attachments = new ArrayList<Attachment>();
			attachments = itsHelpDeskAttachmentDao.getAttachmentsByTicketHistory(ticketHistoryItem.getId());
			LOGGER.debug("Attachment Count: {}", attachments.size());

			/*
			 * Attachment attachment1 = new Attachment(); attachment1.setId(1);
			 * attachment1.setFileType("txt");
			 * attachment1.setName("123198_getAlertPackageResponse");
			 * attachment1.setSize(123000); attachment1.setDownloadUri(
			 * "http://localhost:8080/filestorage/123198_getAlertPackageResponse.txt");
			 * 
			 * Attachment attachment2 = new Attachment(); attachment2.setId(2);
			 * attachment2.setFileType("doc"); attachment2.setName("NetworkteamConsent");
			 * attachment2.setSize(456000); attachment2.setDownloadUri(
			 * "http://localhost:8080/filestorage/123198_getAlertPackageResponse.txt");
			 * 
			 * attachments.add(attachment1); attachments.add(attachment2);
			 */
			ticketHistoryItem.setAttachments(attachments);
		});

		return ticketHistoryList;
	}

	public int createTicketHistory(TicketHistory ticketHistory, int ticketId, int userId) {
		LOGGER.debug("Creating ticket-history record: {} for the given ticket with id: {} by user with id: {}",
				ticketHistory, ticketId, userId);
		int numberOfRowsAffected;
		KeyHolder ticketHistoryKey = new GeneratedKeyHolder();
		String[] keyColumnNames = new String[1];
		keyColumnNames[0] = "tktconv_id";
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ticketconversation ");
		sql.append("(");
		sql.append("tktconv_tkt_id, tktconv_author, tktconv_message, tktconv_commented_on");
		sql.append(")");
		sql.append("VALUES ");
		sql.append("(");
		sql.append(":tktconv_tkt_id, :tktconv_author, :tktconv_message, :tktconv_commented_on");
		sql.append(")");

		SqlParameterSource paramSource = new MapSqlParameterSource().addValue("tktconv_tkt_id", ticketId)
				.addValue("tktconv_author", userId).addValue("tktconv_message", ticketHistory.getComment())
				.addValue("tktconv_commented_on", ticketHistory.getCommentedDate());

		numberOfRowsAffected = namedParameterJdbcTemplate.update(sql.toString(), paramSource, ticketHistoryKey,
				keyColumnNames);

		if (numberOfRowsAffected == 1)
			return ticketHistoryKey.getKey().intValue();

		else
			throw new InternalServerException("Unexpected error occured while creating tickethistory record");
	}

	/* Row Mappers */

	private static class TicketDetailsRowMapper implements RowMapper<Ticket> {

		@Override
		public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
			Ticket ticket = new Ticket();
			Department department = new Department();
			department.setId(rs.getInt("tkt_dept_id"));
			department.setName(rs.getString("dept_name"));
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
			ticket.setPriority(rs.getString("pty_name"));
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
