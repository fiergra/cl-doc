package com.ceres.dynamicforms.client.components;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;

public class SearchSuggestion<T> extends MultiWordSuggestion {
	
	public T person;
	
	public SearchSuggestion(){}
	
	public SearchSuggestion(T person, String replacement, String display) {
		super(replacement, display);
		this.person = person;
	}
}
