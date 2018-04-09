package eu.europa.ec.digit.eAgenda;

import java.util.ArrayList;
import java.util.List;

public class BookedSlot {
	public final Slot slot;
	public final List<Appointment> appointments = new ArrayList<>();
	
	public BookedSlot(Slot slot) {
		this.slot = slot;
	}
}
