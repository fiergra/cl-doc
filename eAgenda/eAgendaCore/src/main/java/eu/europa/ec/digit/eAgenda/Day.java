package eu.europa.ec.digit.eAgenda;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Day implements Serializable {
	private static final long serialVersionUID = -2367081828485697944L;
	public List<Slot> slots;
	private int index;

	protected Day() {}
	
	public Day(int index) {
		this.index = index;
	}
	
	public void addSlot(Slot slot) {
		if (slots == null) {
			slots = new ArrayList<>();
		}
		slots.add(slot);
		slots.sort((s1, s2) -> {
			Integer i1 = s1.h * 100 + s1.m;
			Integer i2 = s2.h * 100 + s2.m;
			return i1.compareTo(i2);
		});
		slot.day = this;
	}
}
