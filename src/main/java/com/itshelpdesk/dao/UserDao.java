package com.itshelpdesk.dao;

import java.util.List;

import com.pc.model.User;

public interface UserDao {

	User fetchUserByUserName(String userName);

	List<User> fetchUsersByRole(String roleName);

}
