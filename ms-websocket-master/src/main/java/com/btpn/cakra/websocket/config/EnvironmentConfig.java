package com.btpn.cakra.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvironmentConfig {

	@Bean
	public EnvironmentVariable environmentModel() {
		EnvironmentVariable env = new EnvironmentVariable();
		env.setMongodbUsername(System.getenv("MONGODB_USERNAME"));
		env.setMongodbPassword(System.getenv("MONGODB_PASSWORD"));
		env.setMongodbDatabase(System.getenv("MONGODB_DATABASE"));
		env.setMongodbHost(System.getenv("MONGODB_HOST"));
		env.setMongodbPort(System.getenv("MONGODB_PORT"));
		env.setMongodbTimeout(System.getenv("MONGO_CONNECTION_TIMEOUT"));
		env.setSmtpAuthUser(System.getenv("SMTP_AUTH_USER"));
		env.setSmtpAuthPassword(System.getenv("SMTP_AUTH_PWD"));
		env.setMailSmtpAuth(System.getenv("MAIL_SMTP_AUTH"));
		env.setMailSmtpHost(System.getenv("MAIL_SMTP_HOST"));
		env.setMailSmtpPort(System.getenv("MAIL_SMTP_PORT"));
		env.setMailTransportProtocol(System.getenv("MAIL_TRANSPORT_PROTOCOL"));
		env.setMinutes(System.getenv("MINUTES"));

		return env;
	}
}
