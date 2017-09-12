package com.qlik_websocket_client.email;

import java.util.Date;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;




public class EmailSender {
	
	public static final String SMTP_HOST_NAME = "smtp.sendgrid.net";
	public static final int SMTP_HOST_PORT = 2525;
	public static final String SMTP_AUTH_USER = "lalit1viewtech";
	public static final String SMTP_AUTH_PWD  = "Phs2ERh@&!*Ghsla2kd7";
	
	
	public static void sendMail(MailMessage mailMessage) throws Exception {
		Properties mailProps = new Properties();
		mailProps.put("mail.smtp.starttls.enable", "true");
		mailProps.put("mail.smtp.host", SMTP_HOST_NAME); 
		mailProps.put("mail.smtp.auth", "true");
		mailProps.put("mail.smtp.port", SMTP_HOST_PORT);
		mailProps.put("mail.transport.protocol", "smtp");
		mailProps.put("mail.smtp.auth", "true");

		Session mailSession = Session.getInstance(mailProps, new SMTPAuthenticator(SMTP_AUTH_USER, SMTP_AUTH_PWD));

		Multipart multipart = new MimeMultipart();
		MimeBodyPart mbpText = new MimeBodyPart();
		mbpText.setText(mailMessage.getTextMessage());
		mbpText.setContent(mailMessage.getTextMessage(), "text/html");
		multipart.addBodyPart(mbpText);
		
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
		mc.addMailcap("message/rfc822;; x-java-content-  handler=com.sun.mail.handlers.message_rfc822"); 
		CommandMap.setDefaultCommandMap(mc);

		Message msg = new MimeMessage(mailSession);		
		InternetAddress intAdd = new InternetAddress(mailMessage.getInternetAddress());
		intAdd.setPersonal(mailMessage.getSender());
		msg.setFrom(intAdd);
//		msg.setReplyTo(new InternetAddress[]{new InternetAddress(mailMessage.getSender())});
		msg.setSubject(mailMessage.getSubject());
		msg.setContent(multipart);
		msg.setSentDate(new Date());
		String[] arrTo = null;
		InternetAddress[] addressTo = null;
		if(mailMessage.getRecipientTo() != null && !mailMessage.getRecipientTo().isEmpty()) {
			if(mailMessage.getRecipientTo().contains(";")) {
				arrTo = mailMessage.getRecipientTo().split(";");
				addressTo = new InternetAddress[arrTo.length];
				for(int i=0; i < arrTo.length; i++) {
					addressTo[i] = new InternetAddress(arrTo[i].trim());
				}
				msg.addRecipients(Message.RecipientType.TO, addressTo);
			} else {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mailMessage.getRecipientTo().trim()));
			}
		}
		if(mailMessage.getRecipientCC()!=null && !mailMessage.getRecipientCC().isEmpty() ) {
			if(mailMessage.getRecipientCC().contains(";")) {
				arrTo = mailMessage.getRecipientCC().split(";");
				addressTo = new InternetAddress[arrTo.length];
				for(int i=0; i < arrTo.length; i++) {
					addressTo[i] = new InternetAddress(arrTo[i].trim());
				}
				msg.addRecipients(Message.RecipientType.CC, addressTo);
			} else {
				msg.addRecipient(Message.RecipientType.CC, new InternetAddress(mailMessage.getRecipientCC().trim()));
			}
		}
		if(mailMessage.getRecipientBcc() != null && !mailMessage.getRecipientBcc().isEmpty()) {
			if(mailMessage.getRecipientBcc().contains(";")) {
				arrTo = mailMessage.getRecipientBcc().split(";");
				addressTo = new InternetAddress[arrTo.length];
				for(int i=0; i < arrTo.length; i++) {
					addressTo[i] = new InternetAddress(arrTo[i].trim());
				}
				msg.addRecipients(Message.RecipientType.BCC, addressTo);
			} else {
				msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(mailMessage.getRecipientBcc().trim()));
			}
		}

		if(mailMessage.getFileAttachment()!=null) {
			for(int i = 0; i < mailMessage.getFileAttachment().length ; i++) {
				MimeBodyPart mbpAttachment = new MimeBodyPart();
				DataSource source = new FileDataSource(mailMessage.getFileAttachment()[i]);
				mbpAttachment.setHeader("Content-Type", mailMessage.getFileAttachmentContentType()[i]);
				mbpAttachment.setDataHandler(new DataHandler(source));
				mbpAttachment.setFileName(mailMessage.getFileAttachmentFileName()[i]);
				multipart.addBodyPart(mbpAttachment);
			}
		}
		try{
			//Modified by Lalit (For SendGrid) - Start
			Transport t = mailSession.getTransport("smtp");
			t.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
			t.sendMessage(msg, msg.getAllRecipients());
			t.close();
			//Transport.send(msg);
			//Modified by Lalit (For SendGrid) - End
		}catch(Exception e){e.printStackTrace();}
	}
	
	
	public static int sendMailUpdated(MailMessage mailMessage) {
		try {
							Properties mailProps = new Properties();
							mailProps.put("mail.smtp.starttls.enable", "true");
							mailProps.put("mail.smtp.host", SMTP_HOST_NAME); 
							mailProps.put("mail.smtp.auth", "true");
							mailProps.put("mail.smtp.port", SMTP_HOST_PORT);
							mailProps.put("mail.transport.protocol", "smtp");
							mailProps.put("mail.smtp.auth", "true");
					
							Session mailSession = Session.getInstance(mailProps, new SMTPAuthenticator(SMTP_AUTH_USER, SMTP_AUTH_PWD));
					
							Multipart multipart = new MimeMultipart();
							MimeBodyPart mbpText = new MimeBodyPart();
							mbpText.setText(mailMessage.getTextMessage());
							mbpText.setContent(mailMessage.getTextMessage(), "text/html");
							multipart.addBodyPart(mbpText);
							
							MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
							mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
							mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
							mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
							mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
							mc.addMailcap("message/rfc822;; x-java-content-  handler=com.sun.mail.handlers.message_rfc822"); 
							CommandMap.setDefaultCommandMap(mc);
					
							Message msg = new MimeMessage(mailSession);		
							InternetAddress intAdd = new InternetAddress(mailMessage.getInternetAddress());
							intAdd.setPersonal(mailMessage.getSender());
							msg.setFrom(intAdd);
					//		msg.setReplyTo(new InternetAddress[]{new InternetAddress(mailMessage.getSender())});
							msg.setSubject(mailMessage.getSubject());
							msg.setContent(multipart);
							msg.setSentDate(new Date());
							String[] arrTo = null;
							InternetAddress[] addressTo = null;
							if(mailMessage.getRecipientTo() != null && !mailMessage.getRecipientTo().isEmpty()) {
								if(mailMessage.getRecipientTo().contains(";")) {
									arrTo = mailMessage.getRecipientTo().split(";");
									addressTo = new InternetAddress[arrTo.length];
									for(int i=0; i < arrTo.length; i++) {
										addressTo[i] = new InternetAddress(arrTo[i].trim());
									}
									msg.addRecipients(Message.RecipientType.TO, addressTo);
								} else {
									msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mailMessage.getRecipientTo().trim()));
								}
							}
							if(mailMessage.getRecipientCC()!=null && !mailMessage.getRecipientCC().isEmpty() ) {
								if(mailMessage.getRecipientCC().contains(";")) {
									arrTo = mailMessage.getRecipientCC().split(";");
									addressTo = new InternetAddress[arrTo.length];
									for(int i=0; i < arrTo.length; i++) {
										addressTo[i] = new InternetAddress(arrTo[i].trim());
									}
									msg.addRecipients(Message.RecipientType.CC, addressTo);
								} else {
									msg.addRecipient(Message.RecipientType.CC, new InternetAddress(mailMessage.getRecipientCC().trim()));
								}
							}
							if(mailMessage.getRecipientBcc() != null && !mailMessage.getRecipientBcc().isEmpty()) {
								if(mailMessage.getRecipientBcc().contains(";")) {
									arrTo = mailMessage.getRecipientBcc().split(";");
									addressTo = new InternetAddress[arrTo.length];
									for(int i=0; i < arrTo.length; i++) {
										addressTo[i] = new InternetAddress(arrTo[i].trim());
									}
									msg.addRecipients(Message.RecipientType.BCC, addressTo);
								} else {
									msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(mailMessage.getRecipientBcc().trim()));
								}
							}
					
							if(mailMessage.getFileAttachment()!=null) {
								for(int i = 0; i < mailMessage.getFileAttachment().length ; i++) {
									MimeBodyPart mbpAttachment = new MimeBodyPart();
									DataSource source = new FileDataSource(mailMessage.getFileAttachment()[i]);
									mbpAttachment.setHeader("Content-Type", mailMessage.getFileAttachmentContentType()[i]);
									mbpAttachment.setDataHandler(new DataHandler(source));
									mbpAttachment.setFileName(mailMessage.getFileAttachmentFileName()[i]);
									multipart.addBodyPart(mbpAttachment);
								}
							}
								//Modified by Lalit (For SendGrid) - Start
								Transport t = mailSession.getTransport("smtp");
								t.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER, SMTP_AUTH_PWD);
								t.sendMessage(msg, msg.getAllRecipients());
								t.close();
								return 1;
								//Transport.send(msg);
								//Modified by Lalit (For SendGrid) - End
		} catch(Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	public static class SMTPAuthenticator extends Authenticator {
		private String userName;
		private String password;
		
		public SMTPAuthenticator(String userName, String password)
		{
			this.userName = userName;
			this.password = password;
		}

		public PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication(userName, password);
		}

	}
	
	
}
