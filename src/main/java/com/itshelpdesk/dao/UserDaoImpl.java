package com.itshelpdesk.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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

		// sql.append("SELECT * FROM user WHERE u_username = ?");
		sql.append("SELECT * FROM user ");
		sql.append("INNER JOIN useremployee ON ue_u_id = u_id ");
		sql.append("INNER JOIN employee ON emp_id = ue_emp_id ");
		sql.append("WHERE u_username =?");

		User user = jdbcTemplate.queryForObject(sql.toString(), new Object[] { userName }, new UserRowMapper());

		return user;
	}

	@Override
	public User fetchUserById(int id) {
		LOGGER.debug("Fetching user by the given id: {}", id);
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT * FROM user ");
		sql.append("INNER JOIN useremployee ON ue_u_id = u_id ");
		sql.append("INNER JOIN employee ON ue_emp_id = emp_id ");
		sql.append("WHERE u_id =:u_id");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("u_id", id);

		User user = namedParameterJdbcTemplate.queryForObject(sql.toString(), paramMap, new UserWithDetailsRowMapper());

		// User user = jdbcTemplate.queryForObject(sql.toString(), new Object[] { new
		// Integer(id) }, new UserWithDetailsRowMapper());
		LOGGER.debug("Fetched user: {}", user);

		return user;
	}

	@Override
	public List<User> fetchUsersByRole(String roleName) {
		LOGGER.debug("Fetching users with the role: {}", roleName);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM user ");
		sql.append("INNER JOIN userrole ON ur_u_id = u_id ");
		sql.append("INNER JOIN role ON ur_role_id = role_id ");
		sql.append("INNER JOIN useremployee ON ue_u_id = u_id ");
		sql.append("INNER JOIN employee ON ue_emp_id = emp_id ");
		sql.append("WHERE role_name = :role_name");

		LOGGER.debug("Fetching data with the sql: {}", sql.toString());
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("role_name", roleName);

		List<User> users = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new UserWithDetailsRowMapper());
		LOGGER.debug("Fetched users: {}", users.toString());
		return users;
	}

	private class UserRowMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getInt("u_id"));
			user.setUserName(rs.getString("u_username"));
			user.setEmployeeId(rs.getInt("emp_id"));
			return user;
		}

	}

	private class UserWithDetailsRowMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getInt("u_id"));
			user.setUserName(rs.getString("u_username"));
			user.setFirstName(rs.getString("emp_firstname"));
			user.setLastName(rs.getString("emp_lastname"));
			return user;
		}

	}

}
