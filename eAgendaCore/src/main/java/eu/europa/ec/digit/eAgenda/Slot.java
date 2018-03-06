package eu.europa.ec.digit.eAgenda;



import java.io.Serializable;

public class Slot implements Serializable {
	private static final long serialVersionUID = 5314229455699834104L;
	
	public int h;
	public int m;
	public int d;
	public Integer capacity;

	public transient Day day;

	protected Slot() {}

	public Slot(int h, int m, int d) {
		this(h, m, d, null);
	}

	public Slot(int h, int m, int d, Integer capacity) {
		this.h = h;
		this.m = m;
		this.d = d;
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return h + ":" + m + "(" + d + "min, capacity=" + capacity + ")";
	}
	

	
}
