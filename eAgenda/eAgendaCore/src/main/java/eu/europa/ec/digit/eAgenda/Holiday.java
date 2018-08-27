package eu.europa.ec.digit.eAgenda;

import java.io.Serializable;
import java.util.Date;

public class Holiday implements Serializable {
	private static final long serialVersionUID = -3851500636483091770L;

	public Date date;
	public String cityCode;
	
	protected Holiday() {}

	public Holiday(Date date, String cityCode) {
		this.date = date;
		this.cityCode = cityCode;
	}

	@Override
	public String toString() {
		return "Holiday [date=" + date + ", cityCode=" + cityCode + "]";
	}
	
	
}
