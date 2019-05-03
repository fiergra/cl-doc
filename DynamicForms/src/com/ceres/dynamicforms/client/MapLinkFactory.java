package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ceres.dynamicforms.client.components.MapListRenderer;

public class MapLinkFactory implements ILinkFactory<Map<String, Serializable>> {

	protected final  ITranslator<Map<String, Serializable>> translator;

	public MapLinkFactory(ITranslator<Map<String, Serializable>> translator) {
		this.translator = translator;
	}
	
	protected MapList createWidget(String[] labels, HashMap<String, String> attributes, Interactor<Map<String, Serializable>> interactor) {
		return new MapList(translator, labels, interactor);
	}
	
	@Override
	public InteractorWidgetLink<Map<String, Serializable>> createLink(final Interactor<Map<String, Serializable>> interactor, final String fieldName, HashMap<String, String> attributes) {
		String sLabels = attributes.get(MapListRenderer.LABELS);
		String[] labels = sLabels.split(";");
//		final String className = fieldName;//attributes.get(CLASSNAME);
		final MapList ml = createWidget(labels, attributes, interactor);
		setColDefs(ml, attributes);
		final InteractorWidgetLink<Map<String, Serializable>> mlLink = new InteractorWidgetLink<Map<String, Serializable>>(interactor, fieldName, ml, attributes) {
			
			@Override
			public void toDialog(Map<String, Serializable> item) {
				@SuppressWarnings("unchecked")
				List<Map<String, Serializable>> acts = (List<Map<String, Serializable>>) item.get(fieldName);
				ml.toDialog(acts );
			}
			
			@Override
			public boolean isEmpty() {
				return false;
			}
			
			@Override
			public void fromDialog(Map<String, Serializable> item) {
				List<Map<String, Serializable>> acts = new ArrayList<>();
				ml.fromDialog(acts );
				item.put(fieldName, (Serializable) acts);
			}
		};
		
		ml.setChangeHandler(() -> interactor.onChange(mlLink));

		return mlLink;
	}
	
	private void setColDefs(MapList ml,
			HashMap<String, String> attributes) {
		int count = Integer.valueOf(attributes.get("columns")); 
		for (int col = 0; col < count; col++) {
			String colDef = attributes.get("colDef" + col);
			ml.addColDef(colDef);
		}
	}



}
