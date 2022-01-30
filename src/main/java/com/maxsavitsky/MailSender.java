package com.maxsavitsky;

import com.sun.mail.smtp.SMTPMessage;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MailSender {

	private static MailSender instance;

	public static MailSender getInstance() {
		if(instance == null)
			instance = new MailSender();
		return instance;
	}

	private final String mail, pass;

	private final Address adminAddress;

	private final Properties props;

	private MailSender(){
		try(FileInputStream fis = new FileInputStream("mail.properties")) {
			Properties properties = new Properties();
			properties.load(fis);
			mail = properties.getProperty("mail");
			pass = properties.getProperty("pass");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		try {
			adminAddress = InternetAddress.parse("admin@maxsavteam.com")[0];
		} catch (AddressException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.maxsavteam.com");
		props.put("mail.smtp.ssl.trust", "*");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.ssl.protocols", "TLSv1.3");
		props.put("mail.smtp.user", mail);
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.transport.protocol", "smtp");
	}

	public void sendToAdmin(String title, String msg) throws MessagingException {
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mail, pass);
			}
		});

		Message message = new SMTPMessage(session);
		message.setFrom(new InternetAddress("malinka-term-status@maxsavteam.com"));

		message.setRecipient(Message.RecipientType.TO, adminAddress);
		message.setSubject(title);
		message.setContent(msg, "text/plain");

		Transport.send(message);

	}

}
