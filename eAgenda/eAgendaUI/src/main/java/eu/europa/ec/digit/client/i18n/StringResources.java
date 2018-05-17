package eu.europa.ec.digit.client.i18n;

import java.util.HashMap;

import com.ceres.dynamicforms.client.SimpleForm;
import com.ceres.dynamicforms.client.command.AbstractCommand;
import com.ceres.dynamicforms.client.command.Commando;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

import eu.europa.ec.digit.client.RPCCallback;
import eu.europa.ec.digit.client.eAgendaUI;

public class StringResources {

	
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
		return true;
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
			
			SimpleForm sf = new SimpleForm();
			sf.addLine("locale", new Label(getNavigatorLanguage()));
			sf.addLine("key", new Label(key));
			
			TextBox txtEn = new TextBox();
			TextBox txtFr = new TextBox();
			TextBox txtDe = new TextBox();

			txtEn.setWidth("100%");
			txtFr.setWidth("100%");
			txtDe.setWidth("100%");

			txtEn.setText(sr.en);
			txtFr.setText(sr.fr);
			txtDe.setText(sr.de);
			
			
			ChangeHandler ch = e -> updateSr(sr, new StringResource(key, txtEn.getText(), txtFr.getText(), txtDe.getText()));
			txtEn.addChangeHandler(ch);
			txtFr.addChangeHandler(ch);
			txtDe.addChangeHandler(ch);
			
			sf.addLine("en", txtEn);
			sf.addLine("fr", txtFr);
			sf.addLine("de", txtDe);
			
			PopupPanel popUp = new PopupPanel(true);
			popUp.add(sf);
			popUp.center();
		}		
	}

	private static void updateSr(StringResource original, StringResource modified) {
		commando.execute(new SaveStringResources(original, modified));
	}

}
