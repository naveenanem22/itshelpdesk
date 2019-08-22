package com.itshelpdesk.dao;

import java.util.List;

import com.pc.model.User;

public interface UserDao {

	User fetchUserByUserName(String userName);
	
	User fetchUserById(int id);

	List<User> fetchUsersByRole(String roleName);

}
