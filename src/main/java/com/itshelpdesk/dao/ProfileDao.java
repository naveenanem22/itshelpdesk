package com.itshelpdesk.dao;

import com.pmt.model.Employee;

public interface ProfileDao {
	
	Employee getEmployeeById(int employeeId, int userId);

}
