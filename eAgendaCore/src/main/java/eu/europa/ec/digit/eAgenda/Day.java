package eu.europa.ec.digit.eAgenda;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Day implements Serializable {
	private static final long serialVersionUID = -2367081828485697944L;
	public List<Slot> slots;
	
	public Day() {}
	
	public void addSlot(Slot slot) {
		if (slots == null) {
			slots = new ArrayList<>();
		}
		slots.add(slot);
		slot.day = this;
	}
}
