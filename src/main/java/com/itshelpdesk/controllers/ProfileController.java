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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itshelpdesk.service.ProfileService;
import com.pmt.model.Employee;
import com.pmt.service.EmployeeService;

@RestController(value = "profileController")
@RequestMapping("/v0/profile-management")
@Validated
public class ProfileController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	@Qualifier("profileServiceImpl")
	ProfileService profileService;

	@GetMapping(path = "/employees/{id}")
	public ResponseEntity<Employee> getEmployeeById(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable("id") int employeeId) {
		LOGGER.debug("Fetching Employee details by employeeId: {}", employeeId);
		return new ResponseEntity<Employee>(profileService.getEmployeeById(employeeId, userDetails.getUsername()),
				HttpStatus.OK);
	}

}
