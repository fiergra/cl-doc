package eu.europa.ec.digit.eAgenda;



import java.io.Serializable;
import java.util.Date;

public class Slot implements Serializable {
	private static final long serialVersionUID = 5314229455699834104L;
	
	public int h;
	public int m;
	public int durationInMinutes;
	public Integer capacity;

	public transient Day day;

	protected Slot() {}

	public Slot(int h, int m, int d) {
		this(h, m, d, null);
	}

	public Slot(Slot s) {
		this(s.h, s.m, s.durationInMinutes, s.capacity);
	}
	
	public Slot(int h, int m, int d, Integer capacity) {
		this.h = h;
		this.m = m;
		this.durationInMinutes = d;
		this.capacity = capacity;
	}

	@Override
	public String toString() {
		return h + ":" + m + "(" + durationInMinutes + "min, capacity=" + capacity + ")";
	}

	@SuppressWarnings("deprecation")
	public Date getFrom(Date d) {
		d.setHours(h);
		d.setMinutes(m);
		d.setSeconds(0);
		return d;
	}

	public Date getUntil(Date d) {
		d = getFrom(d);
		return new Date(d.getTime() + durationInMinutes * 60 * 1000);
	}

	public boolean isAdjacent(Slot slot) {
		Date d = new Date();
		return getUntil(d).getTime() == slot.getFrom(d).getTime();
	}

	public void combineWith(Slot slot) {
		durationInMinutes += slot.durationInMinutes;
		if ((capacity == null && slot.capacity != null) || (capacity != null && slot.capacity != null && slot.capacity < capacity)) {
			capacity = slot.capacity;
		}
	}

	
}
