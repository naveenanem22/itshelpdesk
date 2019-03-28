package com.itshelpdesk.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itshelpdesk.model.BarChartDataItem;
import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.service.DashboardService;

@RestController(value = "dashboardController")
@RequestMapping("/v0/dashboard")
@Validated
public class DashboardController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("dashboardServiceImpl")
	private DashboardService dashboardService;

	@GetMapping(path = "/barChart", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BarChartDataItem>> getTicket(@AuthenticationPrincipal UserDetails userDetails) {
		LOGGER.debug("Fetching BarChart details");
		return new ResponseEntity<List<BarChartDataItem>>(dashboardService.fetchTicketCountStatusAndMonthWise(),
				HttpStatus.OK);
	}

}
