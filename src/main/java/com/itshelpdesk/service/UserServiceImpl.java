package com.itshelpdesk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.itshelpdesk.dao.UserDao;
import com.pc.model.User;

@Service(value = "userServiceImpl")
public class UserServiceImpl implements UserService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier(value = "userDaoImpl")
	UserDao userDao;

	@Override
	public User getUserByUserName(String userName) {
		LOGGER.debug("Fetching User data for the given userName: {}", userName);
		User user = userDao.fetchUserByUserName(userName);
		LOGGER.debug("Fetched User: {}", user);
		return user;
	}

}
