package com.itshelpdesk.dao;

import java.util.List;

import com.pc.model.Attachment;

public interface ItsHelpDeskAttachmentDao {

	List<Integer> createAttachments(List<Attachment> attachments);

}
