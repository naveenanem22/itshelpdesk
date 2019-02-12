package com.itshelpdesk.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;
import com.itshelpdesk.service.TicketService;

@RestController(value = "ticketController")
@RequestMapping("/v0/ticket-management/tickets")
public class TicketController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("ticketServiceImpl")
	private TicketService ticketService;

	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateTicket() {
		return ResponseEntity.noContent().build();
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createTicket(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("ticketTitle") String title,
			@RequestParam(value = "file1", required = false) MultipartFile file1,
			@RequestParam(value = "file2", required = false) MultipartFile file2,
			@RequestParam(value = "file3", required = false) MultipartFile file3) {
		LOGGER.debug(title);
		if (file1 != null)
			LOGGER.debug(file1.getOriginalFilename());
		if (file2 != null)
			LOGGER.debug(file2.getOriginalFilename());
		if (file3 != null)
			LOGGER.debug(file3.getOriginalFilename());

		return ResponseEntity.created(null).build();
	}

	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Ticket> getTicket(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") int ticketId) {
		LOGGER.debug("Fetching ticket details with id:{} for the user with userName:{}", ticketId, userDetails.getUsername());
		
/*		TicketHistory t1h1 = new TicketHistory();
		t1h1.setAuthorName("Naveen Kumar Anem");
		t1h1.setComment("Internet not working.");
		t1h1.setCommentedDate(LocalDateTime.now());
		t1h1.setId(1);

		TicketHistory t1h2 = new TicketHistory();
		t1h2.setAuthorName("Praveen Kumar");
		t1h2.setComment("Working on it. Pls give sometime.");
		t1h2.setCommentedDate(LocalDateTime.now());
		t1h2.setId(2);

		TicketHistory t1h3 = new TicketHistory();
		t1h3.setAuthorName("Naveen Kumar Anem");
		t1h3.setComment("Pls update once completed and see if this can be done at the earliset.");
		t1h3.setCommentedDate(LocalDateTime.now());
		t1h3.setId(4);

		List<TicketHistory> ticketHistoryList = new ArrayList<TicketHistory>();
		ticketHistoryList.add(t1h1);
		ticketHistoryList.add(t1h2);
		ticketHistoryList.add(t1h3);

		Ticket t1 = new Ticket();
		t1.setCreatedDate(LocalDateTime.now());
		t1.setDeskNumber("DA-46");
		t1.setId(12345);
		t1.setOfficeLocation("Kony-Hyderabad");
		t1.setPriority("Normal");
		t1.setServiceCategory("Network");
		t1.setStatus("Open");
		t1.setTicketHistoryList(ticketHistoryList);
		t1.setTitle("Internet not working on my personal device.");
		t1.setType("Task");
		t1.setUpdatedDate(LocalDateTime.now());*/
		Ticket ticket = ticketService.getTicket(ticketId, 1);
		LOGGER.debug("Fetched ticket details: {}", ticket);

		return new ResponseEntity<Ticket>(ticket, HttpStatus.OK);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Ticket>> getTickets(@AuthenticationPrincipal UserDetails userDetails) {
		LOGGER.debug("Fetching tickets for the user with username: "+ userDetails.getUsername());
		return new ResponseEntity<List<Ticket>>(ticketService.getTicketsByUserName(userDetails.getUsername()),
				HttpStatus.OK);
	}

}
