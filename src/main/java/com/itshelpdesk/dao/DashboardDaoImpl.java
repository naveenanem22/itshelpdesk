package com.itshelpdesk.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class DashboardDaoImpl implements DashboardDao {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int fetchCountOfTicketsInLastHourByStatus(String ticketStatus) {

		LOGGER.debug("Fetching {} ticket-count in the last one hour", ticketStatus);
		StringBuilder sql = new StringBuilder();
		switch (ticketStatus) {
		case "New":
			sql.append("SELECT * FROM viewnewticketsinlasthour");
			break;
		case "Closed":
			sql.append("SELECT * FROM viewclosedticketsinlasthour");
			break;
		default:
			break;
		}

		return 0;
	}

}
