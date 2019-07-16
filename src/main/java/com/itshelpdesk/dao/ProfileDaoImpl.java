package com.itshelpdesk.dao;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pc.custom.exceptions.InternalServerException;
import com.pc.model.Badge;
import com.pmt.model.ContactInfo;
import com.pmt.model.Employee;
import com.pmt.model.IndividualAddress;

@Repository(value = "profileDaoImpl")
public class ProfileDaoImpl implements ProfileDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileDaoImpl.class);

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public Employee getEmployeeById(int employeeId, int userId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM employee ");
		sql.append("INNER JOIN useremployee ON ue_emp_id = emp_id ");
		sql.append("INNER JOIN user ON ue_u_id = u_id ");
		sql.append("LEFT JOIN employeeaddress ON empaddr_emp_id = emp_id ");
		sql.append("LEFT JOIN employeecontact ON ec_emp_id = emp_id ");
		sql.append("LEFT JOIN aboutme ON emp_id = abm_emp_id ");
		sql.append("LEFT JOIN individualaddress ON empaddr_ia_id = ia_id ");
		sql.append("LEFT JOIN employeecredits ON ecr_emp_id = emp_id ");
		sql.append("LEFT JOIN employeeprofilepic ON eprp_emp_id = emp_id ");
		sql.append("WHERE u_id=:u_id && emp_id=:emp_id");

		LOGGER.debug("Fetching employee details with the query: {}", sql.toString());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("u_id", userId);
		paramMap.put("emp_id", employeeId);

		LOGGER.debug("Parammap: {}", paramMap);

		List<Employee> employees = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new EmployeeRowMapper());

		if (employees.size() == 1) {
			Employee employee = employees.get(0);
			LOGGER.debug("Fetching badges for the employee");
			employee.setBadges(getEmployeeBadges(employeeId, userId));
			return employee;
		}

		else
			throw new InternalServerException(
					"Unexpected exception occured while fetching employee details with id: " + employeeId);
	}

	@Override
	public List<Badge> getEmployeeBadges(int employeeId, int userId) {
		LOGGER.debug("Fetching badges for the employeeId: {}", employeeId);
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM employeebadge ");
		sql.append("INNER JOIN employee ON emp_id = eb_emp_id ");
		sql.append("INNER JOIN badge ON eb_bdg_id  = bdg_id ");
		sql.append("WHERE emp_id=:emp_id");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("emp_id", employeeId);

		LOGGER.debug("paramMap: {}", paramMap);
		LOGGER.debug("Fetching Employee-Badges list with the query: {}", sql);

		List<Badge> badges = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BadgeRowMapper());

		return badges;
	}

	private static class EmployeeRowMapper implements RowMapper<Employee> {

		@Override
		public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
			Employee employee = new Employee();
			employee.setFirstName(rs.getString("emp_firstname"));
			employee.setLastName(rs.getString("emp_lastname"));
			employee.setAboutMe(rs.getString("abm_aboutme_text"));
			employee.setDesignation(rs.getString("emp_designation"));
			employee.setCredits(rs.getInt("ecr_credits"));
			Blob blob = rs.getBlob("eprp_img_file");
			employee.setBase64ProfilePic(Base64.getEncoder().encodeToString(blob.getBytes(1, (int) blob.length())));

			ContactInfo contactInfo = new ContactInfo();
			contactInfo.setHomePhone(rs.getString("ec_home_phone"));
			contactInfo.setHomePhoneCountryCode(rs.getString("ec_home_phone_country_code"));
			contactInfo.setPrimaryMobile(rs.getString("ec_primary_mobile_phone"));
			contactInfo.setPrimaryMobileCountryCode(rs.getString("ec_primary_mobile_phone_country_code"));
			contactInfo.setSecondaryMobile(rs.getString("ec_secondary_mobile_phone"));
			contactInfo.setSecondaryMobileCountryCode(rs.getString("ec_secondary_mobile_phone_country_code"));
			contactInfo.setOfficePhone(rs.getString("ec_office_phone"));
			contactInfo.setOfficePhoneCountryCode(rs.getString("ec_office_phone_country_code"));

			employee.setContactInfo(contactInfo);

			IndividualAddress individualAddress = new IndividualAddress();
			individualAddress.setAddressLine1(rs.getString("ia_addr_line_1"));
			employee.setIndividualAddress(individualAddress);

			return employee;
		}
	}

	private static class BadgeRowMapper implements RowMapper<Badge> {

		@Override
		public Badge mapRow(ResultSet rs, int rowNum) throws SQLException {
			Badge badge = new Badge();
			badge.setId(rs.getInt("bdg_id"));
			badge.setDescription(rs.getString("bdg_desc"));
			badge.setTitle(rs.getString("bdg_title"));
			Blob blob = rs.getBlob("bdg_img");
			badge.setBase64Image(Base64.getEncoder().encodeToString(blob.getBytes(1, (int) blob.length())));
			
			return badge;
		}

	}

}
