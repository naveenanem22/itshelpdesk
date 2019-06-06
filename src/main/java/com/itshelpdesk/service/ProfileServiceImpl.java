package com.itshelpdesk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itshelpdesk.dao.ProfileDao;
import com.pc.model.User;
import com.pmt.model.Employee;
import com.pmt.service.EmployeeService;

@Service("profileServiceImpl")
public class ProfileServiceImpl implements ProfileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceImpl.class);

	@Autowired
	@Qualifier("userServiceImpl")
	UserService userService;
	
	@Autowired
	@Qualifier("profileDaoImpl")
	private ProfileDao profileDao;

	@Override
	@Transactional(readOnly = true)
	public Employee getEmployeeById(int employeeId, String userName) {
		LOGGER.debug("Fetching user for the given userName: {}", userName);
		User user = userService.getUserByUserName(userName);
		
		Employee employee =  profileDao.getEmployeeById(employeeId, user.getId());
		LOGGER.debug("Fetched employee details: {}", employee);

		return employee;
	}

}
