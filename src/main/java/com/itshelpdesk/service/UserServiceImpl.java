package com.itshelpdesk.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itshelpdesk.dao.UserDao;
import com.pc.model.User;

@Service(value = "userServiceImpl")
public class UserServiceImpl implements UserService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier(value = "userDaoImpl")
	UserDao userDao;

	@Override
	@Transactional(readOnly = true)
	public User getUserByUserName(String userName) {
		LOGGER.debug("Fetching User data for the given userName: {}", userName);
		User user = userDao.fetchUserByUserName(userName);
		LOGGER.debug("Fetched User: {}", user);
		return user;
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getUsersByRole(String roleName, String requestorUserName) {
		LOGGER.debug("Fetching user list with roleName: {}, requested by user with username: {}", roleName,
				requestorUserName);

		List<User> users = userDao.fetchUsersByRole(roleName);
		LOGGER.debug("Fetched users: {}", users.toString());

		return users;
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserById(int id) {
		LOGGER.debug("Fetching user with id: {}", id);
		User user = userDao.fetchUserById(id);
		LOGGER.debug("Fetched user: {}", user);
		return user;
	}

}
