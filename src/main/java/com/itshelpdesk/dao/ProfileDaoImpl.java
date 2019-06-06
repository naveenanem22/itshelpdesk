package com.itshelpdesk.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.pmt.model.ContactInfo;
import com.pmt.model.Employee;
import com.pmt.model.IndividualAddress;

@Repository(value="profileDaoImpl")
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
		sql.append("INNER JOIN employeeaddress ON empaddr_emp_id = emp_id ");
		sql.append("INNER JOIN employeecontact ON ec_emp_id = emp_id ");
		sql.append("INNER JOIN individualaddress ON empaddr_ia_id = ia_id ");
		sql.append("WHERE u_id=:u_id && emp_id=:emp_id");

		LOGGER.debug("Fetching employee details with the query: {}", sql.toString());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("u_id", userId);
		paramMap.put("emp_id", employeeId);

		List<Employee> employees = namedParameterJdbcTemplate.query(sql.toString(), paramMap, new EmployeeRowMapper());

		if (employees.size() == 1)
			return employees.get(0);
		else
			throw new InternalServerException(
					"Unexpected exception occured while fetching employee details with id: " + employeeId);
	}

	private static class EmployeeRowMapper implements RowMapper<Employee> {

		@Override
		public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
			Employee employee = new Employee();
			employee.setFirstName(rs.getString("emp_firstname"));
			employee.setLastName(rs.getString("emp_lastname"));

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

}
