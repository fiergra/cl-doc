package eu.europa.ec.digit.eAgenda;

import java.io.Serializable;

public class City implements Serializable {
	private static final long serialVersionUID = -6072036581333183639L;
	public String code;
	
	protected City() {}
	
	public City(String code) {
		this.code = code;
	}
}
