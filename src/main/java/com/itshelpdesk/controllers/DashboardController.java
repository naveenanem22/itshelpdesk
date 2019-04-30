package com.itshelpdesk.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.itshelpdesk.model.PieChartDataItem;
import com.itshelpdesk.service.DashboardService;

@RestController(value = "dashboardController")
@RequestMapping("/v0/ticket-management/dashboard")
@Validated
public class DashboardController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("dashboardServiceImpl")
	private DashboardService dashboardService;

	@GetMapping(path = "/barChart", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BarChartDataItem>> getBarChart(@AuthenticationPrincipal UserDetails userDetails) {
		LOGGER.debug("Fetching BarChart details");
		return new ResponseEntity<List<BarChartDataItem>>(dashboardService.fetchTicketCountStatusAndMonthWise(),
				HttpStatus.OK);
	}

	@GetMapping(path = "/pieChart", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PieChartDataItem>> getPieChart(@AuthenticationPrincipal UserDetails userDetails) {
		LOGGER.debug("Fetching PieChart data");
		return new ResponseEntity<List<PieChartDataItem>>(dashboardService.fetchDepartmentWisePayload(), HttpStatus.OK);
	}

	@GetMapping(path = "/lasthour/{status}")
	public ResponseEntity<Map<String, Integer>> getTicketCountInLastOneHourByStatus(
			@AuthenticationPrincipal UserDetails userDetails, @PathVariable("status") String ticketStatus) {
		LOGGER.debug("Fetching {} TicketCount in last one hour", ticketStatus);
		Integer count = dashboardService.fetchCountOfTicketsInLastHourByStatus(ticketStatus);
		LOGGER.debug("Fetched ticketcount: {}", count);
		Map<String, Integer> lastHourDataByStatus = new HashMap<String, Integer>();
		lastHourDataByStatus.put("ticketCount", count);
		return new ResponseEntity<Map<String, Integer>>(lastHourDataByStatus, HttpStatus.OK);
	}

}
