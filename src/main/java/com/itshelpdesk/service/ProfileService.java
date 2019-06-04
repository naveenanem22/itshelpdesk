package com.itshelpdesk.service;

import com.pmt.model.Employee;

public interface ProfileService {

	Employee getEmployeeById(int employeeId, String userName);

}
