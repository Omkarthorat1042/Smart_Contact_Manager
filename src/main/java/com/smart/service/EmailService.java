package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String subject, String message, String to)
	{
		
	boolean f = false;
	
	//rest of the code
	String from = "omkarthorat1042@gmail.com";
	
	
	//variable for gmail
			String host = "smtp.gmail.com";
			
			//get the sys properties
			Properties properties = System.getProperties();
			System.out.println("PROPERIES: " + properties);
			
			
			//setting important information to properties objects...
			
			
			//host set
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", "465");
			properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.auth", "true");
			
			//step 1..to get the session object...
			Session session = Session.getInstance(properties, new Authenticator() {
				
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					
					
					
					return new PasswordAuthentication("omkarthorat1042@gmail.com", "bgua hyoi insr jwdd");
				}
				
				
				 
			});
			session.setDebug(true);
				
			//step 2.. compse the message [text, multimedia]
			
			MimeMessage m = new MimeMessage(session);
			
			try {
			
				//step 3..from email
			m.setFrom(from);
			
			//adding recipent to mssg
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			//adding subject to message
			m.setSubject(subject);
			
			
			//adding text to msg
//			m.setText(message);
			m.setContent(message, "text/html");
			
			
			//step 3.. send the msg using transport class
			Transport.send(m);
			
			System.out.println("Sent success................!");
			f=true;
			
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			return f;
		}
	
	
	
	
	
	
	
}
