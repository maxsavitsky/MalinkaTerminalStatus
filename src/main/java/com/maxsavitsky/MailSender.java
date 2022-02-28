package com.maxsavitsky;

import com.sun.mail.smtp.SMTPMessage;

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
import java.util.Objects;
import java.util.Properties;

public class MailSender {

	private static MailSender instance;

	public static MailSender getInstance() {
		return instance;
	}

	public static void init(String mailPropertiesFile){
		instance = new MailSender(mailPropertiesFile);
	}

	private final String mail;
	private final String pass;

	private final InternetAddress[] internetAddresses;

	private final Properties props;

	private MailSender(String mailPropertiesFile){
		String addressList;
		String smtpHost;
		try(FileInputStream fis = new FileInputStream(mailPropertiesFile)) {
			Properties properties = new Properties();
			properties.load(fis);
			mail = Objects.requireNonNull(properties.getProperty("mail"));
			pass = Objects.requireNonNull(properties.getProperty("pass"));
			addressList = Objects.requireNonNull(properties.getProperty("recipients-list"));
			smtpHost = Objects.requireNonNull(properties.getProperty("smtp-host"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		try {
			internetAddresses = InternetAddress.parse(addressList);
		} catch (AddressException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", smtpHost);
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
		message.setFrom(new InternetAddress(mail));

		for(var i : internetAddresses)
			message.setRecipient(Message.RecipientType.TO, i);
		message.setSubject(title);
		message.setContent(msg, "text/plain");

		Transport.send(message);

	}

}
