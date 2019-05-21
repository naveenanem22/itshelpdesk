package com.itshelpdesk.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itshelpdesk.service.UserService;
import com.pc.model.User;

@RestController("userController")
@RequestMapping("/v0")
@Validated
/**
 * 
 * @author Naveen Anem
 * @version 1.0
 * @since 2019-21-05
 */
public class UserController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("userServiceImpl")
	private UserService userService;

	/**
	 * To fetch users based on various search criteria: - role etc. 
	 */
	@GetMapping(path = "/user-management/users")
	public ResponseEntity<List<User>> getUsersByRole(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(required = false, name = "roleName") String roleName) {
		LOGGER.debug("Fetching users with the role name: {}, for the user with username: {}", roleName,
				userDetails.getUsername());

		List<User> users = userService.getUsersByRole(roleName, userDetails.getUsername());
		LOGGER.debug("Fetched users: {}", users.toString());

		return new ResponseEntity<List<User>>(users, HttpStatus.OK);

	}

}
