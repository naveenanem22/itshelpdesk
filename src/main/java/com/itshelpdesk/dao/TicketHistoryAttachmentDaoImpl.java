package com.itshelpdesk.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pc.custom.exceptions.InternalServerException;

@Repository(value = "ticketHistoryAttachmentDaoImpl")
public class TicketHistoryAttachmentDaoImpl implements TicketHistoryAttachmentDao {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public boolean createTicketHistoryAttachment(List<Integer> attachmentIds, int ticketHistoryid) {
		StringBuilder sql = new StringBuilder();
		List<Map<String, Object>> batchValues;
		try {

			sql.append("INSERT INTO tktconvattachment ");
			sql.append("(tca_tktconv_id, tca_ihda_id) ");
			sql.append("VALUES ");
			sql.append("(:tca_tktconv_id, :tca_ihda_id)");

			batchValues = new ArrayList<>(attachmentIds.size());
			attachmentIds.forEach(attachmentId -> {
				batchValues.add(new MapSqlParameterSource("tca_ihda_id", attachmentId.intValue())
						.addValue("tca_tktconv_id", ticketHistoryid).getValues());
				
			});
			namedParameterJdbcTemplate.batchUpdate(sql.toString(),
					batchValues.toArray(new Map[attachmentIds.size()]));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalServerException("An unexepected error occured while updating ticket.");
		}

	}

}
