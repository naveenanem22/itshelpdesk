package com.itshelpdesk.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pc.deserializers.JsonDateDeserializer;
import com.pc.serializers.JsonDateSerializer;

public class TicketHistory {

	@JsonProperty("id")
	private int id;

	@JsonProperty("authorName")
	private String authorName;

	@JsonProperty(value = "commentedOn")
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private LocalDateTime commentedDate;

	@JsonProperty("comment")
	private String comment;

	public TicketHistory() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public LocalDateTime getCommentedDate() {
		return commentedDate;
	}

	public void setCommentedDate(LocalDateTime commentedDate) {
		this.commentedDate = commentedDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "TicketHistory [id=" + id + ", authorName=" + authorName + ", commentedDate=" + commentedDate
				+ ", comment=" + comment + "]";
	}

}
