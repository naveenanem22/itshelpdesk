package com.itshelpdesk.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pc.model.Attachment;

@Repository(value = "itsHelpDeskAttachmentDaoImpl")
public class ItsHelpDeskAttachmentDaoImpl implements ItsHelpDeskAttachmentDao {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Integer> createAttachments(List<Attachment> attachments) {
		StringBuilder sql = new StringBuilder();

		sql.append("INSERT INTO itshelpdeskattachment ");
		sql.append("(");
		sql.append("ihda_name");
		sql.append(")");
		sql.append("VALUES ");
		sql.append("(");
		sql.append(":ihda_name");
		sql.append(")");

		List<Map<String, Object>> batchValues = new ArrayList<>(attachments.size());
		attachments.forEach(attachment -> {
			LOGGER.debug("FileName to be logged: {}", attachment.getName());
			batchValues.add(new MapSqlParameterSource("ihda_name", attachment.getName()).getValues());
		});

		namedParameterJdbcTemplate.batchUpdate(sql.toString(), batchValues.toArray(new Map[attachments.size()]));

		// List of attachment-names to fetch ids
		List<String> fileNames = attachments.stream().map(Attachment::getName).collect(Collectors.toList());

		// Retrieve the inserted row ids
		sql = new StringBuilder();
		sql.append("SELECT ihda_id FROM itshelpdeskattachment WHERE ");
		sql.append("ihda_name IN(:ihda_name)");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("ihda_name", fileNames);

		List<Integer> attachmentIds = namedParameterJdbcTemplate.query(sql.toString(), paramMap,
				new AttachmentIdRowMapper());

		return attachmentIds;
		
	}

	private static class AttachmentIdRowMapper implements RowMapper<Integer> {

		@Override
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			Integer attachmentId = rs.getInt("ihda_id");
			return attachmentId;
		}

	}

}
