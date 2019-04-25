package com.itshelpdesk.dao;

import com.pc.model.User;

public interface UserDao {

	User fetchUserByUserName(String userName);

}
