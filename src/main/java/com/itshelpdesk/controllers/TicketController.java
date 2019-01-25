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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;



@RestController(value = "ticketController")
@RequestMapping("/v0/ticket-management/tickets")
public class TicketController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	

	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateTicket() {
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createTicket() {
		return ResponseEntity.created(null).build();
	}
	
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Ticket> getTicket(@AuthenticationPrincipal UserDetails userDetails){
		LOGGER.debug("Username: "+userDetails.getUsername());
		LOGGER.debug("Password: "+userDetails.getPassword());
		TicketHistory t1h1 = new TicketHistory();
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
		t1.setDepartment("ITS-Helpdesk");
		t1.setDeskNumber("DA-46");
		t1.setId(12345);
		t1.setOfficeLocation("Kony-Hyderabad");
		t1.setPriority("Normal");
		t1.setServiceCategory("Network");
		t1.setStatus("Open");
		t1.setTicketHistoryList(ticketHistoryList);
		t1.setTitle("Internet not working on my personal device.");
		t1.setType("Task");
		t1.setUpdatedDate(LocalDateTime.now());
		
		return new ResponseEntity<Ticket>(t1,HttpStatus.OK);
	}
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Ticket>> getTickets(){		
		
		Ticket t1 = new Ticket();
		t1.setCreatedDate(LocalDateTime.now());
		t1.setDepartment("ITS-Helpdesk");
		t1.setDeskNumber("DA-46");
		t1.setId(12345);
		t1.setOfficeLocation("Kony-Hyderabad");
		t1.setPriority("Normal");
		t1.setServiceCategory("Network");
		t1.setStatus("Open");
		t1.setTitle("Internet not working on my personal device.");
		t1.setType("Task");
		t1.setUpdatedDate(LocalDateTime.now());
		
		Ticket t2 = new Ticket();
		t2.setCreatedDate(LocalDateTime.now());
		t2.setDepartment("ITS-Helpdesk");
		t2.setDeskNumber("DA-46");
		t2.setId(12346);
		t2.setOfficeLocation("Kony-Hyderabad");
		t2.setPriority("Normal");
		t2.setServiceCategory("Network");
		t2.setStatus("Open");
		t2.setTitle("Printer not working.");
		t2.setType("Task");
		t2.setUpdatedDate(LocalDateTime.now());
		
		Ticket t3 = new Ticket();
		t3.setCreatedDate(LocalDateTime.now());
		t3.setDepartment("ITS-Helpdesk");
		t3.setDeskNumber("DA-46");
		t3.setId(12347);
		t3.setOfficeLocation("Kony-Hyderabad");
		t3.setPriority("Normal");
		t3.setServiceCategory("Network");
		t3.setStatus("Open");
		t3.setTitle("Printer not working.");
		t3.setType("Task");
		t3.setUpdatedDate(LocalDateTime.now());
		
		Ticket t4 = new Ticket();
		t4.setCreatedDate(LocalDateTime.now());
		t4.setDepartment("ITS-Helpdesk");
		t4.setDeskNumber("DA-46");
		t4.setId(12348);
		t4.setOfficeLocation("Kony-Hyderabad");
		t4.setPriority("Normal");
		t4.setServiceCategory("Network");
		t4.setStatus("Open");
		t4.setTitle("Printer not working.");
		t4.setType("Task");
		t4.setUpdatedDate(LocalDateTime.now());
		
		Ticket t5 = new Ticket();
		t5.setCreatedDate(LocalDateTime.now());
		t5.setDepartment("ITS-Helpdesk");
		t5.setDeskNumber("DA-46");
		t5.setId(12349);
		t5.setOfficeLocation("Kony-Hyderabad");
		t5.setPriority("Normal");
		t5.setServiceCategory("Network");
		t5.setStatus("Open");
		t5.setTitle("Printer not working.");
		t5.setType("Task");
		t5.setUpdatedDate(LocalDateTime.now());
		
		List<Ticket> tickets = new ArrayList<Ticket>();
		tickets.add(t1);
		tickets.add(t2);
		tickets.add(t3);
		tickets.add(t4);
		tickets.add(t5);
		return new ResponseEntity<List<Ticket>>(tickets, HttpStatus.OK);
	}

}
