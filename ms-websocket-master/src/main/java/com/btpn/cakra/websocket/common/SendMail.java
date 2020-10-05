package com.btpn.cakra.websocket.common;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.bson.Document;

import com.btpn.cakra.websocket.config.EnvironmentVariable;
import com.btpn.cakra.websocket.config.StackTraceMessage;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class SendMail {

	MongoClient mongoClient;
	EnvironmentVariable envVar;
	MongoCollection<Document> projectVariableCollection;
	private static Logger logger = Logger.getLogger("InfoLogging");

	public SendMail(MongoClient mongoClient, EnvironmentVariable envVar) {
		this.envVar = envVar;
		this.mongoClient = mongoClient;
		try {
			projectVariableCollection = this.mongoClient.getDatabase(envVar.getMongodbDatabase())
					.getCollection("project_variable");
		} catch (Exception e) {
		}
	}

	public void sendMailFailed(Map<String, Object> payload) {
		FindIterable<Document> prjVarIterable = projectVariableCollection
				.find(and(eq("key", "email"), eq("projectId", payload.get("groupId").toString())));
		Iterator<Document> prjVarIterator = prjVarIterable.iterator();
		while (prjVarIterator.hasNext()) {
			try {
				Document prjVarDoc = prjVarIterator.next();
				Map<String, Object> projectVariable = new HashMap<>(prjVarDoc);
				Properties props = new Properties();
				props.put("mail.smtp.auth", envVar.getMailSmtpAuth());
				props.put("mail.smtp.host", envVar.getMailSmtpHost());
				props.put("mail.smtp.port", envVar.getMailSmtpPort());
				props.put("mail.transport.protocol", envVar.getMailTransportProtocol());

				Authenticator auth = new SMTPAuthenticator(envVar);

				Session session = Session.getDefaultInstance(props, auth);

				Transport transport = session.getTransport();

				MimeMessage message = new MimeMessage(session);

				message.setFrom(new InternetAddress(envVar.getMailSmtpAuth()));

				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse((String) projectVariable.get("address")));

				StringWriter subjectSw = new StringWriter();
				Velocity.evaluate(new VelocityContext(payload), subjectSw, "", (String) projectVariable.get("subject"));
				message.setSubject(subjectSw.toString());

				StringWriter bodySw = new StringWriter();
				Velocity.evaluate(new VelocityContext(payload), bodySw, "", (String) projectVariable.get("body"));
				message.setText(bodySw.toString());

				transport.connect();
				transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
				transport.close();
			} catch (Exception e) {
				logger.info("Error send mail on failed job cause : " + StackTraceMessage.getStackTrace(e));
			}
		}
	}

	private static class SMTPAuthenticator extends Authenticator {
		EnvironmentVariable envVar;

		public SMTPAuthenticator(EnvironmentVariable envVar) {
			this.envVar = envVar;
			getPasswordAuthentication();
		}

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			String username = envVar.getSmtpAuthUser();
			String password = envVar.getSmtpAuthPassword();
			return new PasswordAuthentication(username, password);
		}
	}

}
