package com.ceres.dynamicforms.client.components;

import java.util.Collection;
import java.util.HashMap;

import com.ceres.dynamicforms.client.ITranslator;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;

public class SearchBox<T> extends SuggestBox {

	private T selected;
	private LabelFunc<T> replacement;
	private HashMap<String, T> entriesByLabel;
	
	public SearchBox(final ITranslator translator, final Collection<T> list, final LabelFunc<T> replacement) {
		this(new MultiWordSuggestOracle(), replacement);
		entriesByLabel = new HashMap<String, T>();
		setEntries(translator, list);
	}
	
	public SearchBox(MultiWordSuggestOracle multiWordSuggestOracle, final LabelFunc<T> replacement) {
		super(multiWordSuggestOracle);
		this.replacement = replacement;
		
//		getValueBox().addChangeHandler(new ChangeHandler() {
//			
//			@Override
//			public void onChange(ChangeEvent event) {
//				setSelected(null);
//				SuggestOracle.Suggestion suggestion = new SearchSuggestion<T>(null, "", "");
//			    SelectionEvent.fire(SearchBox.this, suggestion);
//			}
//		});
//		
		addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
				if (event.getSelectedItem() instanceof SearchSuggestion) {
					SearchSuggestion<T>si = (SearchSuggestion<T>) event.getSelectedItem();
					selected = si != null ? si.person : null;
				} else {
					selected = entriesByLabel.get(event.getSelectedItem().getReplacementString());
				}
			}
		});
	}

	public void setEntries(ITranslator application, final Collection<T> list) {
		entriesByLabel.clear();
		if (list != null) {
			MultiWordSuggestOracle oracle = (MultiWordSuggestOracle) getSuggestOracle();
			for (T r:list) {
				addEntry(application, oracle, r);
			}
		}
	}
	

	
	public T getSelected() {
		return selected;
	}

	public void setSelected(T selected) {
		this.selected = selected;
		setText(selected != null ? replacement.label(selected) : null);
	}

	public boolean isEmpty() {
		return getText() == null || getText().length() == 0 || getSelected() == null;
	}

	private void addEntry(ITranslator application, MultiWordSuggestOracle oracle, T r) {
		String label = replacement.label(r);
		entriesByLabel.put(label,  r);
		oracle.add(label);
	}
	
	
}
