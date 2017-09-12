package com.qlik_websocket_client.email;

import java.io.File;
import java.io.Serializable;

public class MailMessage implements Serializable {

	
	private static final long serialVersionUID = -5816798771288286268L;
	
	private File[] fileAttachment; 
	private String[] fileAttachmentFileName; 
	private String[] fileAttachmentContentType;
	private String sender = "";
	private String subject = "";
	private String textMessage = "";
    private String recipientTo = "";
    private String recipientCC = "";
    private String recipientBcc ;
    private String templateField = "";
    private String notes="";
    private File[] copyAttachments = new File[3]; 
    public String winPath="C:\\opt\\uploadedFiles\\";
    public String linPath="/opt/uploadedFiles/";
    private String recipientType;
    private String dateIssueNotesCorresHitroy;
    private int issueCorresHitoryId;
    private int msid;
    private String internetAddress;

    
	public int getMsid() {
		return msid;
	}
	public void setMsid(int msid) {
		this.msid = msid;
	}
	public int getIssueCorresHitoryId() {
		return issueCorresHitoryId;
	}
	public void setIssueCorresHitoryId(int issueCorresHitoryId) {
		this.issueCorresHitoryId = issueCorresHitoryId;
	}
	public String getDateIssueNotesCorresHitroy() {
		return dateIssueNotesCorresHitroy;
	}
	public void setDateIssueNotesCorresHitroy(String dateIssueNotesCorresHitroy) {
		this.dateIssueNotesCorresHitroy = dateIssueNotesCorresHitroy;
	}
	public String getRecipientType() {
		return recipientType;
	}
	public void setRecipientType(String recipientType) {
		this.recipientType = recipientType;
	}
	/**
	 * @return the copyAttachments
	 */
	public File[] getCopyAttachments() {
		return copyAttachments;
	}
	/**
	 * @param copyAttachments the copyAttachments to set
	 */
	public void setCopyAttachments(File[] copyAttachments) {
		this.copyAttachments = copyAttachments;
	}
	/**
	 * @return the templateField
	 */
	public String getTemplateField() {
		return templateField;
	}
	/**
	 * @param templateField the templateField to set
	 */
	public void setTemplateField(String templateField) {
		this.templateField = templateField;
	}
	/**
	 * @return the fileAttachmentFile
	 */
	public File[] getFileAttachment() {
		return fileAttachment;
	}
	/**
	 * @param fileAttachmentFile the fileAttachmentFile to set
	 */
	public void setFileAttachment(File[] fileAttachmentFile) {
		this.fileAttachment = fileAttachmentFile;
	}
	/**
	 * @return the fileAttachmentFileName
	 */
	public String[] getFileAttachmentFileName() {
		return fileAttachmentFileName;
	}
	/**
	 * @param fileAttachmentFileName the fileAttachmentFileName to set
	 */
	public void setFileAttachmentFileName(String[] fileAttachmentFileName) {
		this.fileAttachmentFileName = fileAttachmentFileName;
	}
	/**
	 * @return the fileAttachmentContentType
	 */
	public String[] getFileAttachmentContentType() {
		return fileAttachmentContentType;
	}
	/**
	 * @param fileAttachmentContentType the fileAttachmentContentType to set
	 */
	public void setFileAttachmentContentType(String[] fileAttachmentContentType) {
		this.fileAttachmentContentType = fileAttachmentContentType;
	}
	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}
	/**
	 * @param sender the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the textMessage
	 */
	public String getTextMessage() {
		return textMessage;
	}
	/**
	 * @param textMessage the textMessage to set
	 */
	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}
	/**
	 * @return the recipientTo
	 */
	public String getRecipientTo() {
		return recipientTo;
	}
	/**
	 * @param recipientTo the recipientTo to set
	 */
	public void setRecipientTo(String recipientTo) {
		this.recipientTo = recipientTo;
	}
	/**
	 * @return the recipientCC
	 */
	public String getRecipientCC() {
		return recipientCC;
	}
	/**
	 * @param recipientCC the recipientCC to set
	 */
	public void setRecipientCC(String recipientCC) {
		this.recipientCC = recipientCC;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getRecipientBcc() {
		return recipientBcc;
	}
	public void setRecipientBcc(String recipientBcc) {
		this.recipientBcc = recipientBcc;
	}
	public String getInternetAddress() {
		return internetAddress;
	}
	public void setInternetAddress(String internetAddress) {
		this.internetAddress = internetAddress;
	}
   


}
