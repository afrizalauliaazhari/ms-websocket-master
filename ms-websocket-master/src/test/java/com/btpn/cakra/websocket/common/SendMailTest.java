package com.btpn.cakra.websocket.common;

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.btpn.cakra.websocket.config.EnvironmentVariable;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

@RunWith(SpringJUnit4ClassRunner.class)
public class SendMailTest {

	@InjectMocks
	SendMail sm;

	@Mock
	MongoClient mongoClient;

	@Mock
	EnvironmentVariable envVar;

	@Mock
	MongoCollection<Document> projectVariableCollection;

	private static final String FROM = "notification@btpn.com";
	private static final String TO = "v-Afrizal.Azhari@btpn.com";

	private static final String BODY = "Test email body.";
	private static final String SUBJECT = "Test";

	private static final String HOST = "email-smtp.us-west-2.amazonaws.com";

	private static final int PORT = 25;

	@SuppressWarnings({ "unchecked", "unused" })
	@Test
	public void SendTest() {
		MongoCollection<Document> collGroup = mock(MongoCollection.class);
	}

	@SuppressWarnings("unused")
	@Test
	public void SendFailedTest() throws MessagingException {
		Map<String, Object> payload = new HashMap<String, Object>();
		Properties props = System.getProperties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", PORT);
		props.put("mail.smtp.host", HOST);
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtp.starttls.required", "true");

		Session session = Session.getInstance(props);
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(FROM));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));
		msg.setSubject(SUBJECT);
		msg.setContent(BODY, "text/plain");

		Transport transport = session.getTransport();

		transport.close();

	}
}
