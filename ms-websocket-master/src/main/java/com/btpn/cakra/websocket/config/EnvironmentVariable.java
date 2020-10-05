package com.btpn.cakra.websocket.config;

public class EnvironmentVariable {

	private String mongodbUsername;
	private String mongodbPassword;
	private String mongodbDatabase;
	private String mongodbHost;
	private String mongodbPort;
	private String mongodbTimeout;
	private String smtpAuthUser;
	private String smtpAuthPassword;
	private String mailSmtpAuth;
	private String mailSmtpHost;
	private String mailSmtpPort;
	private String mailTransportProtocol;
	private String minutes;

	public String getMongodbUsername() {
		return mongodbUsername;
	}

	public void setMongodbUsername(String mongodbUsername) {
		this.mongodbUsername = mongodbUsername;
	}

	public String getMongodbPassword() {
		return mongodbPassword;
	}

	public void setMongodbPassword(String mongodbPassword) {
		this.mongodbPassword = mongodbPassword;
	}

	public String getMongodbDatabase() {
		return mongodbDatabase;
	}

	public void setMongodbDatabase(String mongodbDatabase) {
		this.mongodbDatabase = mongodbDatabase;
	}

	public String getMongodbHost() {
		return mongodbHost;
	}

	public void setMongodbHost(String mongodbHost) {
		this.mongodbHost = mongodbHost;
	}

	public String getMongodbPort() {
		return mongodbPort;
	}

	public void setMongodbPort(String mongodbPort) {
		this.mongodbPort = mongodbPort;
	}

	public String getMongodbTimeout() {
		return mongodbTimeout;
	}

	public void setMongodbTimeout(String mongodbTimeout) {
		this.mongodbTimeout = mongodbTimeout;
	}

	public String getSmtpAuthUser() {
		return smtpAuthUser;
	}

	public void setSmtpAuthUser(String smtpAuthUser) {
		this.smtpAuthUser = smtpAuthUser;
	}

	public String getSmtpAuthPassword() {
		return smtpAuthPassword;
	}

	public void setSmtpAuthPassword(String smtpAuthPassword) {
		this.smtpAuthPassword = smtpAuthPassword;
	}

	public String getMailSmtpAuth() {
		return mailSmtpAuth;
	}

	public void setMailSmtpAuth(String mailSmtpAuth) {
		this.mailSmtpAuth = mailSmtpAuth;
	}

	public String getMailSmtpHost() {
		return mailSmtpHost;
	}

	public void setMailSmtpHost(String mailSmtpHost) {
		this.mailSmtpHost = mailSmtpHost;
	}

	public String getMailSmtpPort() {
		return mailSmtpPort;
	}

	public void setMailSmtpPort(String mailSmtpPort) {
		this.mailSmtpPort = mailSmtpPort;
	}

	public String getMailTransportProtocol() {
		return mailTransportProtocol;
	}

	public void setMailTransportProtocol(String mailTransportProtocol) {
		this.mailTransportProtocol = mailTransportProtocol;
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	@Override
	public String toString() {
		return "EnvironmentVariable [mongodbUsername=" + mongodbUsername + ", mongodbPassword=" + mongodbPassword
				+ ", mongodbDatabase=" + mongodbDatabase + ", mongodbHost=" + mongodbHost + ", mongodbPort="
				+ mongodbPort + ", mongodbTimeout=" + mongodbTimeout + ", smtpAuthUser=" + smtpAuthUser
				+ ", smtpAuthPassword=" + smtpAuthPassword + ", mailSmtpAuth=" + mailSmtpAuth + ", mailSmtpHost="
				+ mailSmtpHost + ", mailSmtpPort=" + mailSmtpPort + ", mailTransportProtocol=" + mailTransportProtocol
				+ ", minutes=" + minutes + "]";
	}

}
