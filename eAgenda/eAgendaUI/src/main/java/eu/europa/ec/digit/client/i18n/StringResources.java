package eu.europa.ec.digit.client.i18n;

import java.util.HashMap;

import com.ceres.dynamicforms.client.SimpleForm;
import com.ceres.dynamicforms.client.command.AbstractCommand;
import com.ceres.dynamicforms.client.command.Commando;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

import eu.europa.ec.digit.client.RPCCallback;
import eu.europa.ec.digit.client.eAgendaUI;

public class StringResources {

	public static final String TITLE = "___$";

	
	private static Commando commando;
	private static HashMap<String, StringResource> stringResources;

	public static void init(Commando c, Runnable callback) {
		commando = c;
		eAgendaUI.service.getStringResources(new RPCCallback<HashMap<String,StringResource>>() {

			@Override
			protected void onResult(HashMap<String, StringResource> result) {
				stringResources = result;
				callback.run();
			}

		});
	}

	private static native String getNavigatorLanguage()  /*-{
		return navigator.language || navigator.userLanguage;
	}-*/;
	
	
	public static String language = map2supported(getNavigatorLanguage().substring(0, 2)); 
	
	public static String getLabel(String key) {
		StringResource sr = stringResources.get(key);
		return sr != null ? getLabel(sr) : key;
	}

	private static String map2supported(String lng) {
		return ("en".equals(lng) || "fr".equals(lng) ||"de".equals(lng)) ? lng : "en";
	}

	private static String getLabel(StringResource sr) {
		switch (language) {
		case "en": return sr.en;
		case "fr": return sr.fr;
		case "de": return sr.de;
		}
		return sr.key;
	}

	public static boolean canEdit() {
		return eAgendaUI.userContext.isAdmin();
	}
	
	
	static class SaveStringResources extends AbstractCommand {

		private StringResource original;
		private StringResource modified;

		public SaveStringResources(StringResource original, StringResource modified) {
			super("save string resource", "save string resource");
			this.original = original;
			this.modified = modified;
		}

		@Override
		public void exec() {
			save(modified);
		}

		@Override
		public void undo() {
			save(original);
		}

		private void save(StringResource sr) {
			eAgendaUI.service.saveStringResource(sr, new RPCCallback<Void>() {

				@Override
				protected void onResult(Void result) {
				}
			});
			stringResources.put(sr.key, sr);
		}


	}

	public static void edit(String key) {
		if (stringResources != null) {
			StringResource sr = stringResources.containsKey(key) ? stringResources.get(key) : new StringResource(key, null, null, null);
			StringResource srTitle = stringResources.containsKey(StringResources.TITLE + key) ? stringResources.get(StringResources.TITLE + key) : new StringResource(key, null, null, null);
			
			SimpleForm sf = new SimpleForm();
			sf.addLine("locale", new Label(getNavigatorLanguage()));
			sf.addLine("key", new Label(key));
			
			TextBox txtEn = new TextBox();
			TextArea titleEn = new TextArea();
			TextBox txtFr = new TextBox();
			TextArea titleFr = new TextArea();
			TextBox txtDe = new TextBox();
			TextArea titleDe = new TextArea();

			txtEn.setWidth("100%");
			txtFr.setWidth("100%");
			txtDe.setWidth("100%");
			titleEn.setWidth("100%");
			titleFr.setWidth("100%");
			titleDe.setWidth("100%");

			txtEn.setText(sr.en);
			txtFr.setText(sr.fr);
			txtDe.setText(sr.de);
			titleEn.setText(srTitle.en);
			titleFr.setText(srTitle.fr);
			titleDe.setText(srTitle.de);
			
			
			ChangeHandler chText = e -> updateSr(sr, new StringResource(key, txtEn.getText(), txtFr.getText(), txtDe.getText()));
			txtEn.addChangeHandler(chText);
			txtFr.addChangeHandler(chText);
			txtDe.addChangeHandler(chText);

			ChangeHandler chTitle = e -> updateSr(sr, new StringResource(StringResources.TITLE + key, titleEn.getText(), titleFr.getText(), titleDe.getText()));
			titleEn.addChangeHandler(chTitle);
			titleFr.addChangeHandler(chTitle);
			titleDe.addChangeHandler(chTitle);

			sf.addLine("en", txtEn);
			sf.addLine("", titleEn);
			sf.addLine("fr", txtFr);
			sf.addLine("", titleFr);
			sf.addLine("de", txtDe);
			sf.addLine("", titleDe);
			
			PopupPanel popUp = new PopupPanel(true);
			popUp.add(sf);
			popUp.center();
		}		
	}

	private static void updateSr(StringResource original, StringResource modified) {
		commando.execute(new SaveStringResources(original, modified));
	}

}
