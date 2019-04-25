package com.itshelpdesk.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pc.model.User;

@Repository(value = "userDaoImpl")
public class UserDaoImpl implements UserDao {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public User fetchUserByUserName(String userName) {
		LOGGER.debug("Fetching user for the given UserName: {}", userName);
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT * FROM user WHERE u_username = ?");

		User user = jdbcTemplate.queryForObject(sql.toString(), new Object[] { userName }, new UserRowMapper());

		return user;
	}

	private class UserRowMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getInt("u_id"));
			user.setUserName(rs.getString("u_username"));
			return user;
		}

	}

}
