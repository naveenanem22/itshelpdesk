package com.itshelpdesk.dao;

import java.util.List;

import com.pc.model.Badge;
import com.pmt.model.Employee;

public interface ProfileDao {
	
	Employee getEmployeeById(int employeeId, int userId);
	
	List<Badge> getEmployeeBadges(int employeeId, int userId);
	

}
