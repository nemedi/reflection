package demo.mailer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

public class Mailer {
	
	public interface MailListener {
		void onSent(int index, String feedback);
	}
	
	private String id;
	private String host;
	private String login;
	private String password;
	private MailData data;
	private MailListener listener;

	public Mailer(String id,
			String host,
			String login,
			String password,
			String csv,
			MailListener listener) throws FileNotFoundException, IOException {
		this.id = id;
		this.host = host;
		this.login = login;
		this.password = password;
		this.data = new MailData(csv);
		this.listener = listener;
	}
	
	public void send(String from, String subject, String body)
			throws AddressException, MessagingException, FileNotFoundException, IOException {
		if (data.isEmpty()) {
			return;
		}
		Properties proterties = System.getProperties();
        proterties.put("mail.smtps.host", host);
    	proterties.put("mail.smtps.auth","true");
        Session session = Session.getInstance(proterties, null);
        SMTPTransport transport = (SMTPTransport) session.getTransport("smtps");
        transport.connect(host, login, new String(Base64.getDecoder().decode(password)));
        for (int i = 0; i < data.getLength(); i++) {
        	String to = null;
        	for (int j = 0; j < data.getFields().length; j++) {
        		String field = data.getFields()[j];
        		String value = data.getValue(i, j);
        		subject = subject.replace("{{" + field + "}}", value);
        		body = body.replace("{{" + field + "}}", value);
        		if (MailData.EMAIL.equalsIgnoreCase(field)) {
        			to = value;
        		}
        	}
        	if (to == null) {
        		continue;
        	}
        	Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO,
            		InternetAddress.parse(to, false));
            message.setSubject(subject);
            message.setText(body);
            message.setHeader("X-Mailer", id);
            message.setSentDate(new Date());
            transport.sendMessage(message, message.getAllRecipients());
            listener.onSent(i, transport.getLastServerResponse());
        }
        transport.close();
	}
	
	public MailData getData() {
		return data;
	}
	
}
