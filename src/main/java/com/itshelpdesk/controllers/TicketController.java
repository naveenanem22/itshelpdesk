package com.itshelpdesk.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping("/v0")
@Validated
public class TicketController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("ticketServiceImpl")
	private TicketService ticketService;

	@Autowired
	private FileStorageService fileStorageService;

	/********************** root URI START ***********************/
	@GetMapping("/tickets/downloadFile/{fileName:.+}")
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

	/********************** root URI END ***********************/

	/********************** ticket-management URI START ***********************/
	@PutMapping(path = "/ticket-management/tickets", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateTickets(@AuthenticationPrincipal UserDetails userDetails,
			@RequestBody List<Ticket> tickets) {
		LOGGER.debug("Updating tickets: {} by the given user: {}", tickets.toString(), userDetails.getUsername());

		ticketService.assignAndUpdateNewTickets(tickets, userDetails.getUsername());

		return ResponseEntity.noContent().build();
	}

	@PutMapping(path = "/ticket-management/tickets/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> updateTicket(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable(value = "id", required = true) int ticketId,
			@RequestPart(value = "comment", required = true) String comment,
			@RequestPart(value = "commentedOn", required = true) String commentedOn,
			@RequestPart(value = "file1", required = false) MultipartFile file1,
			@RequestPart(value = "file2", required = false) MultipartFile file2,
			@RequestPart(value = "file3", required = false) MultipartFile file3,
			@RequestPart(value = "status", required = false) String status,			
			@RequestParam(required = false, name = "createdByMe") boolean createdByMe,
			@RequestParam(required = false, name = "assignedToMe") boolean assignedToMe,
			@RequestParam(required = false, name = "managedByMe") boolean managedByMe) {
		// Setting ticketId
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
		if (status != null)
			ticket.setStatus(status);
		ticket.setTicketHistoryList(ticketHistoryList);
		LOGGER.debug("Updating ticket: {} by the creator: {}", ticket, userDetails.getUsername());

		LOGGER.debug("createdByMe: {}", createdByMe);
		LOGGER.debug("assignedToMe: {}", assignedToMe);
		LOGGER.debug("managedByMe: {}", managedByMe);

		ticketService.updateTicket(ticket, userDetails.getUsername(), createdByMe, assignedToMe, managedByMe);

		return ResponseEntity.noContent().build();
	}

	@GetMapping(path = "/ticket-management/tickets/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Ticket> getTicket(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable("id") int ticketId) {
		LOGGER.debug("Fetching ticket details with id:{} for the user with userName:{}", ticketId,
				userDetails.getUsername());
		Ticket ticket = ticketService.getTicket(ticketId);
		LOGGER.debug("Fetched ticket details: {}", ticket);
		return new ResponseEntity<Ticket>(ticket, HttpStatus.OK);
	}

	@GetMapping(path = "/ticket-management/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<Ticket>> getTickets(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(value = "status", required = false) String statusName,
			@RequestParam(value = "priority", required = false) String priority,
			@RequestParam(required = false, name = "sortBy") String sortBy,
			@RequestParam(required = false, name = "sortOrder") String sortOrder,
			@RequestParam(required = false, name = "pageNumber") int pageNumber,
			@RequestParam(required = false, name = "pageSize") int pageSize,
			@RequestParam(required = false, name = "createdByMe") boolean createdByMe) {
		LOGGER.debug("Fetching tickets for the user with username: " + userDetails.getUsername());

		// Setting empty fields for searching if they are not present in the request
		if (statusName == null || statusName.equalsIgnoreCase("all") || statusName.equalsIgnoreCase("null"))
			statusName = "";
		else
			LOGGER.debug("Search Criteria - status: {}", statusName);
		if (priority == null || priority.equalsIgnoreCase("null"))
			priority = "";
		else
			LOGGER.debug("Search Criteria - priority: {}", priority);

		return new ResponseEntity<Page<Ticket>>(ticketService.getPaginatedTickets(userDetails.getUsername(),
				createdByMe, sortBy, sortOrder, statusName, pageNumber, pageSize, priority), HttpStatus.OK);
	}

	/********************** ticket-management URI END ***********************/
	/********************** ticket-support URI START ************************/
	@PutMapping(path = "/ticket-support/tickets/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> updateAssignedTicket(@AuthenticationPrincipal UserDetails userDetails,
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
		if (status != null)
			ticket.setStatus(status);
		ticket.setTicketHistoryList(ticketHistoryList);
		LOGGER.debug("Updating ticket: {} by the given user: {}", ticket, userDetails.getUsername());

		// ticketService.updateTicket(ticket, userDetails.getUsername());
		return ResponseEntity.noContent().build();
	}

	@GetMapping(path = "/ticket-support/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Ticket>> getTicketsByAssignee(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(required = false, name = "status") String status,
			@RequestParam(required = false, name = "sortBy") String sortBy) {
		LOGGER.debug("Fetching tickets assigned to the user with userName: {}, status: {} and sortBy: {}",
				userDetails.getUsername(), status, sortBy);

		return new ResponseEntity<List<Ticket>>(ticketService.getTicketsByAssignee(userDetails.getUsername(), status),
				HttpStatus.OK);
	}

	@GetMapping(path = "/ticket-support/tickets/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Ticket> getTicketByAssignee(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable("id") int ticketId) {
		LOGGER.debug("Fetching ticket details with id:{} assigned to the user with the userName:{}", ticketId,
				userDetails.getUsername());
		Ticket ticket = ticketService.getTicketByAssignee(userDetails.getUsername(), ticketId);
		LOGGER.debug("Fetched ticket details: {}", ticket.toString());
		return new ResponseEntity<Ticket>(ticket, HttpStatus.OK);
	}

	/********************** ticket-support URI END **************************/
	/********************** ticketing URI START *****************************/
	@RequestMapping(path = "/ticketing/tickets", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Ticket> createTicket(@AuthenticationPrincipal UserDetails userDetails,
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
		ticket.setAdditionalInfo(additionalInfo);

		// creating ticketHistory using the additional-info and attachments
		List<MultipartFile> files = new ArrayList<MultipartFile>();
		if (file1 != null)
			files.add(file1);
		if (file2 != null)
			files.add(file2);
		if (file3 != null)
			files.add(file3);

		TicketHistory ticketHistory = new TicketHistory();
		ticketHistory.setAuthorName(userDetails.getUsername());
		ticketHistory.setComment(additionalInfo);
		ticketHistory.setCommentedDate(LocalDateTime.now());
		ticketHistory.setFiles(files);

		List<TicketHistory> ticketHistoryList = new ArrayList<TicketHistory>();
		ticketHistoryList.add(ticketHistory);
		ticket.setTicketHistoryList(ticketHistoryList);

		int ticketId = ticketService.createTicket(ticket, userDetails.getUsername());
		LOGGER.debug("Ticket created with id: {}", ticketId);
		Ticket createdTicket = new Ticket();
		createdTicket.setId(ticketId);
		return new ResponseEntity<Ticket>(createdTicket, HttpStatus.CREATED);

	}

	@PutMapping(path = "/ticketing/tickets/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> updateTicketByCreator(@AuthenticationPrincipal UserDetails userDetails,
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
		if (status != null)
			ticket.setStatus(status);
		ticket.setTicketHistoryList(ticketHistoryList);
		LOGGER.debug("Updating ticket: {} by the creator: {}", ticket, userDetails.getUsername());

		ticketService.updateTicketByCreator(ticket, userDetails.getUsername());
		return ResponseEntity.noContent().build();
	}

	/*
	 * @PutMapping(path = "/ticketing/tickets/{id}", consumes =
	 * MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<Object>
	 * updateOwnTicket(@AuthenticationPrincipal UserDetails userDetails,
	 * 
	 * @PathVariable(value = "id", required = true) int ticketId,
	 * 
	 * @RequestPart(value = "comment", required = true) String comment,
	 * 
	 * @RequestPart(value = "commentedOn", required = true) String commentedOn,
	 * 
	 * @RequestPart(value = "file1", required = false) MultipartFile file1,
	 * 
	 * @RequestPart(value = "file2", required = false) MultipartFile file2,
	 * 
	 * @RequestPart(value = "file3", required = false) MultipartFile file3,
	 * 
	 * @RequestPart(value = "status", required = false) String status) {
	 * List<MultipartFile> files = new ArrayList<MultipartFile>(); if (file1 !=
	 * null) files.add(file1); if (file2 != null) files.add(file2); if (file3 !=
	 * null) files.add(file3);
	 * 
	 * TicketHistory ticketHistory = new TicketHistory();
	 * ticketHistory.setAuthorName(userDetails.getUsername());
	 * ticketHistory.setComment(comment);
	 * ticketHistory.setCommentedDate(LocalDateTime.now());
	 * ticketHistory.setFiles(files);
	 * 
	 * List<TicketHistory> ticketHistoryList = new ArrayList<TicketHistory>();
	 * ticketHistoryList.add(ticketHistory);
	 * 
	 * Ticket ticket = new Ticket(); ticket.setId(ticketId); if (status != null)
	 * ticket.setStatus(status); ticket.setTicketHistoryList(ticketHistoryList);
	 * LOGGER.debug("Updating ticket: {} by the given user: {}", ticket,
	 * userDetails.getUsername());
	 * 
	 * ticketService.updateTicket(ticket, userDetails.getUsername()); return
	 * ResponseEntity.noContent().build(); }
	 */

	@GetMapping(path = "/ticketing/tickets", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Page<Ticket>> getTicketsByCreator(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(required = false, name = "status") String status,
			@RequestParam(required = false, name = "sortBy") String sortBy,
			@RequestParam(required = false, name = "sortOrder") String sortOrder,
			@RequestParam(required = false, name = "pageNumber") int pageNumber,
			@RequestParam(required = false, name = "pageSize") int pageSize) {
		LOGGER.debug("Fetching tickets created by the user with userName: {}, status: {} and sortBy: {}",
				userDetails.getUsername(), status, sortBy);

		return new ResponseEntity<Page<Ticket>>(ticketService.getPaginatedTicketsByCreator(userDetails.getUsername(),
				sortBy, sortOrder, status, pageNumber, pageSize), HttpStatus.OK);
	}

	@GetMapping(path = "/ticketing/tickets/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Ticket> getTicketByCreator(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable("id") int ticketId) {
		LOGGER.debug("Fetching ticket details with id:{} created by the user with the userName:{}", ticketId,
				userDetails.getUsername());
		Ticket ticket = ticketService.getTicketByCreator(userDetails.getUsername(), ticketId);
		LOGGER.debug("Fetched ticket details: {}", ticket.toString());
		return new ResponseEntity<Ticket>(ticket, HttpStatus.OK);
	}

	/********************** ticketing URI END ***********************/

}
