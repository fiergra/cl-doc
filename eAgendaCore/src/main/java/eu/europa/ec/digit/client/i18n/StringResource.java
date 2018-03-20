package eu.europa.ec.digit.client.i18n;

import java.io.Serializable;

public class StringResource implements Serializable {

	private static final long serialVersionUID = 4888792469037142963L;
	
	public String key;
	public String en;
	public String fr;
	public String de;
	
	protected StringResource() {}

	public StringResource(String key, String en, String fr, String de) {
		this.key = key;
		this.en = en;
		this.fr = fr;
		this.de = de;
	}

	

}
