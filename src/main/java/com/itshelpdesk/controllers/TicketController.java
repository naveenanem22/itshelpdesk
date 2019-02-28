package com.itshelpdesk.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itshelpdesk.model.Ticket;
import com.itshelpdesk.model.TicketHistory;
import com.itshelpdesk.service.TicketService;
import com.pc.model.Department;
import com.pc.services.FileStorageService;

@RestController(value = "ticketController")
@RequestMapping("/v0/ticket-management/tickets")
public class TicketController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("ticketServiceImpl")
	private TicketService ticketService;

	@Autowired
	private FileStorageService fileStorageService;

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		// Load file as Resource
		LOGGER.debug("Download request received...");
		Resource resource = fileStorageService.loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			LOGGER.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		LOGGER.debug("contentType: {}", contentType);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> updateTicket(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable(value = "id", required = true) int ticketId,
			@RequestPart(value = "comment", required = true) String comment,
			@RequestPart(value = "commentedOn", required = true) String commentedOn,
			@RequestPart(value = "file1", required = false) MultipartFile file1,
			@RequestPart(value = "file2", required = false) MultipartFile file2,
			@RequestPart(value = "file3", required = false) MultipartFile file3,
			@RequestPart(value = "status", required = false) String status) {
		List<MultipartFile> files = new ArrayList<MultipartFile>();
		if (file1 != null)
			files.add(file1);
		if (file2 != null)
			files.add(file2);
		if (file3 != null)
			files.add(file3);

		TicketHistory ticketHistory = new TicketHistory();
		ticketHistory.setAuthorName(userDetails.getUsername());
		ticketHistory.setComment(comment);
		ticketHistory.setCommentedDate(LocalDateTime.now());
		ticketHistory.setFiles(files);

		List<TicketHistory> ticketHistoryList = new ArrayList<TicketHistory>();
		ticketHistoryList.add(ticketHistory);

		Ticket ticket = new Ticket();
		ticket.setId(ticketId);
		ticket.setStatus(status);
		ticket.setTicketHistoryList(ticketHistoryList);
		LOGGER.debug("Updating ticket: {} by the given user: {}", ticket, userDetails.getUsername());

		ticketService.updateTicket(ticket, 1);
		return ResponseEntity.noContent().build();
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> createTicket(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam("ticketTitle") String title, @RequestParam("ticketDescription") String description,
			@RequestParam("department") String departmentName, @RequestParam("priority") String priority,
			@RequestParam("serviceCategory") String serviceCategory, @RequestParam("status") String status,
			@RequestParam("additionalInfo") String additionalInfo, @RequestParam("deskNumber") String deskNumber,
			@RequestParam("officeLocation") String officeLocation, @RequestParam("serviceType") String serviceType,
			@RequestParam(value = "file1", required = false) MultipartFile file1,
			@RequestParam(value = "file2", required = false) MultipartFile file2,
			@RequestParam(value = "file3", required = false) MultipartFile file3) {
		Ticket ticket = new Ticket();
		Department department = new Department();
		department.setName(departmentName);
		ticket.setDescription(description);
		ticket.setDeskNumber(deskNumber);
		ticket.setOfficeLocation(officeLocation);
		ticket.setPriority(priority);
		ticket.setServiceCategory(serviceCategory);
		ticket.setTitle(title);
		ticket.setType(serviceType);
		ticket.setDepartment(department);
		ticket.setStatus(status);

		int ticketId = ticketService.createTicket(ticket, 1);
		LOGGER.debug("Ticket created with id: {}", ticketId);

		return ResponseEntity.created(null).build();
	}

	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Ticket> getTicket(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable("id") int ticketId) {
		LOGGER.debug("Fetching ticket details with id:{} for the user with userName:{}", ticketId,
				userDetails.getUsername());
		Ticket ticket = ticketService.getTicket(ticketId, 1);
		LOGGER.debug("Fetched ticket details: {}", ticket);
		return new ResponseEntity<Ticket>(ticket, HttpStatus.OK);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Ticket>> getTickets(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(value = "status", required = false) String statusName,
			@RequestParam(value = "priority", required = false) String priority) {
		LOGGER.debug("Fetching tickets for the user with username: " + userDetails.getUsername());

		// Setting empty fields for searching if they are not present in the request
		if (statusName == null || statusName.equalsIgnoreCase("all"))
			statusName = "";
		else
			LOGGER.debug("Search Criteria - status: {}", statusName);
		if (priority == null)
			priority = "";
		else
			LOGGER.debug("Search Criteria - priority: {}", priority);

		return new ResponseEntity<List<Ticket>>(
				ticketService.getTicketsByUserName(userDetails.getUsername(), statusName, priority), HttpStatus.OK);
	}

}
