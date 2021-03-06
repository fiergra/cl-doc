package com.ceres.cldoc.client;

import com.ceres.cldoc.client.ClDoc;
import com.ceres.cldoc.model.Patient;
import com.ceres.cldoc.model.Person;
import com.ceres.dynamicforms.client.Interactor;
import com.ceres.dynamicforms.client.WidgetCreator;
import com.google.gwt.user.client.ui.Widget;

public class PersonDetails {
	public static Widget create(ClDoc clDoc, Person person, Interactor ia) {
		Widget content = WidgetCreator.createWidget(
				"<Form>" +
				((person instanceof Patient) ? "<FormItem label=\"ID\"><long fieldName=\"perId\" required=\"true\"/></FormItem>" : "") +
				"<FormItem label=\"Name\"><HBox><ItemFieldTextInput required=\"true\" fieldName=\"lastName\"/><Label text=\"Vorname\"/><ItemFieldTextInput required=\"true\" fieldName=\"firstName\"/><Label text=\"Geburtsdatum\"/><ItemFieldDateField required=\"true\" fieldName=\"dateOfBirth\"/><Label text=\"Geschlecht\"/><option parent=\"MASTERDATA.GENDER\" fieldName=\"gender\"/></HBox></FormItem><FormItem label=\"Strasse\"><HBox><ItemFieldTextInput fieldName=\"primaryAddress.street\"/><Label text=\"Nr.\"/><ItemFieldTextInput fieldName=\"primaryAddress.number\"/></HBox></FormItem><FormItem label=\"PLZ\"><HBox><ItemFieldTextInput fieldName=\"primaryAddress.postCode\"/><Label text=\"Ort\"/><ItemFieldTextInput width=\"100%\" fieldName=\"primaryAddress.city\"/></HBox></FormItem><FormItem label=\"Telefon\"><HBox><ItemFieldTextInput fieldName=\"primaryAddress.phone\"/><Label text=\"c/o\"/><ItemFieldTextInput width=\"100%\" fieldName=\"primaryAddress.co\"/></HBox></FormItem><FormItem label=\"Bemerkung\"><ItemFieldTextArea width=\"100%\" fieldName=\"primaryAddress.note\"/></FormItem></Form>", ia);
		
		return content;
	}

}
