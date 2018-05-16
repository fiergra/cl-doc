package eu.europa.ec.digit.client;

import com.ceres.dynamicforms.client.command.ICommand;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.PushButton;

import eu.europa.ec.digit.eAgenda.Campaign;
import eu.europa.ec.digit.eAgenda.Slot;

public class SlotRenderer extends FocusPanel {

	private Slot slot;
	private PushButton pbDelete;
	private HorizontalPanel hpControls = new HorizontalPanel();
	private WorkPatternEditor workPatternEditor;
	private IntegerBox intBox = new IntegerBox();
	

	public SlotRenderer(WorkPatternEditor workPatternEditor, Slot slot) {
		this.workPatternEditor = workPatternEditor;
		this.slot = slot;
		
		setStyleName("slotRenderer");
		addMouseOutHandler(e -> showControls(false));
		addMouseOverHandler(e -> showControls(true));
		
		hpControls.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		hpControls.setWidth("100%");
		
		Image imgDelete = new Image("assets/images/delete.png");
		imgDelete.setPixelSize(12, 12);
		pbDelete = new PushButton(imgDelete);
		pbDelete.setPixelSize(14, 14);
//		pbDelete.setStyleName("deleteSlotButton");
		
		intBox.setPixelSize(14, 14);
		if (slot.capacity != null) {
			intBox.setValue(slot.capacity);
		}
		
		intBox.addChangeHandler(e -> setSlotCapacity(intBox.getValue()));
		
		HorizontalPanel hpControlWrapper = new HorizontalPanel();
		hpControlWrapper.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpControlWrapper.add(intBox);
		hpControlWrapper.add(pbDelete);
		hpControls.add(hpControlWrapper);
	
		hpControls.setVisible(false);
		add(hpControls);
		
		pbDelete.addClickHandler(e -> deleteSlot());
		
	}

	class SetCapacityCommand extends CampaignCommand {
		
		private Slot slot;
		private Integer initialValue;
		private Integer newValue;

		SetCapacityCommand(Campaign c, Slot slot, Integer value) {
			super(c, "set capacity of " + slot.toString() + " to " + value);
			this.slot = slot;
			this.initialValue = slot.capacity;
			this.newValue = value;
		}
		
		@Override
		public void undo() {
			slot.capacity = initialValue;
			intBox.setValue(initialValue);
			saveCampaign();
		}
		
		@Override
		public void exec() {
			slot.capacity = newValue;
			intBox.setValue(newValue);
			saveCampaign();
		}
	};

	
	private void setSlotCapacity(Integer value) {
		ICommand c = new SetCapacityCommand(workPatternEditor.campaign, slot, value);
		eAgendaUI.commando.execute(c);
	}

	private void deleteSlot() {
		ICommand c = new CampaignCommand(workPatternEditor.campaign, "delete slot " + slot.toString()) {
			
			@Override
			public void undo() {
				slot.day.addSlot(slot);
				saveCampaign();
				workPatternEditor.updateDisplay();
			}
			
			@Override
			public void exec() {
				slot.day.slots.remove(slot);
				saveCampaign();
				workPatternEditor.updateDisplay();
			}
		};
		
		eAgendaUI.commando.execute(c);
	}

	private void showControls(boolean b) {
		hpControls.setVisible(b);
	}
	

}
