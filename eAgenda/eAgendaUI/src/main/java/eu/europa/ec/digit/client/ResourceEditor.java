package eu.europa.ec.digit.client;

import com.ceres.dynamicforms.client.ITranslator;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.SimpleForm;
import com.ceres.dynamicforms.client.SimpleTranslator;
import com.ceres.dynamicforms.client.TextBoxLink;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;

import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.Person;
import eu.europa.ec.digit.eAgenda.Room;

public class ResourceEditor<T extends IResource> extends SimpleForm {
	
	private static final String TYPEOFRESOURCE = "TYPEOFRESOURCE";
	private T resource;
	private Interactor<T> interactor = new Interactor<>();
	private ITranslator<T> translator = new SimpleTranslator<>();
	
	private RadioButton rbPerson = new RadioButton(TYPEOFRESOURCE, "Person");
	private RadioButton rbUser = new RadioButton(TYPEOFRESOURCE, "User");
	private RadioButton rbRoom = new RadioButton(TYPEOFRESOURCE, "Room");
	
	public ResourceEditor(T resource) {
		this.resource = resource;
		setup();
		interactor.toDialog(translator, resource);
	}

	private void setup() {
		
		HorizontalPanel hpTypes = new HorizontalPanel();
		hpTypes.add(rbPerson);
		hpTypes.add(rbUser);
		hpTypes.add(rbRoom);
		
		addLine("Type", hpTypes);
		
		if (resource instanceof Person) {
			addLine("", new HTML("<b>PERSON</b>"));
		} else if (resource instanceof Room) {
			addLine("", new HTML("<b>Room</b>"));
		}
		TextBox txtDisplayName = new TextBox();
		interactor.addLink(new TextBoxLink<>(interactor, txtDisplayName, v -> v.getDisplayName(), (v,s) -> v.setDisplayName(s), true));
		addLine("Display name", txtDisplayName);
		TextBox txtEMail = new TextBox();
		interactor.addLink(new TextBoxLink<>(interactor, txtEMail, v -> v.getEMailAddress(), (v,s) -> v.setEMailAddress(s), true));
		addLine("Display name", txtDisplayName);
	}
}
