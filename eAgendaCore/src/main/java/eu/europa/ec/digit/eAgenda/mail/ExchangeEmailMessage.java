package eu.europa.ec.digit.eAgenda.mail;

import java.io.Serializable;
import java.util.Date;

public class ExchangeEmailMessage implements Serializable {
	private static final long serialVersionUID = 7425782531792370947L;
	public boolean isRead;
	public String from;
	public String body;
	public Date received;
	public String uniqueId;
	public String subject;

}
