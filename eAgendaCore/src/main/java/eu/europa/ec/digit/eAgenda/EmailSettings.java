package eu.europa.ec.digit.eAgenda;

import java.io.Serializable;

public class EmailSettings implements Serializable {

	private static final long serialVersionUID = -7846088387873533162L;
	
	public String subject = "subject";
	public String body = "body";
	public boolean includeHost;
	
	protected EmailSettings() {}

	public EmailSettings(Campaign c) {
		if (c.emailSettings != null) {
			this.subject = c.emailSettings.subject;
			this.body = c.emailSettings.body;
			this.includeHost = c.emailSettings.includeHost;
		} 
	}

	public EmailSettings(String subject, String body, boolean includeHost) {
		this.subject = subject;
		this.body = body;
		this.includeHost = includeHost;
	}

}
