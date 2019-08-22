package com.itshelpdesk.service;

import java.util.List;

import com.pc.model.User;

public interface UserService {

	User getUserByUserName(String userName);

	List<User> getUsersByRole(String roleName, String requestorUserName);
	
	User getUserById(int id);

}
