package eu.europa.ec.digit.eAgenda;

import java.io.Serializable;

public class AppointmentType implements Serializable {
	private static final long serialVersionUID = -4590412316109200992L;
	public String name;
	public int duration = 30;
	public String color = "#FAFAFA";
	
	protected AppointmentType() {}

	public AppointmentType(String name, int duration, String color) {
		this.name = name;
		this.duration = duration;
		this.color = color;
	}
	
	
}
