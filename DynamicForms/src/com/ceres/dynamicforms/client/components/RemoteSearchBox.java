package com.ceres.dynamicforms.client.components;

import com.ceres.dynamicforms.client.ITranslator;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

public class RemoteSearchBox<T> extends SearchBox<T> {

	public RemoteSearchBox(final ITranslator application, final RunSearch<T> runSearch, final LabelFunc<T> replacement, final LabelFunc<T> display) {
		this(application, runSearch, replacement, display, 1);
	}

	
	public RemoteSearchBox(final ITranslator application, final RunSearch<T> runSearch, final LabelFunc<T> replacement, final LabelFunc<T> display, final int minlength) {
		super(new MultiWordSuggestOracle(){
			
			class SearchTimer extends Timer {
				private Request request;
				private Callback callback;
				
				@Override
				public void run() {
					runSearch.run(request, callback, replacement, display);
				}
				
				public void schedule(final Request request, final Callback callback) {
					cancel();
					if (request.getQuery().length() >= minlength) {
						this.request = request;
						this.callback = callback;
						schedule(500);
					}
				}
				
			}
			
			SearchTimer st = new SearchTimer();
			@Override
			public void requestSuggestions(final Request request, final Callback callback) {
				st.schedule(request, callback);
			}
		}, replacement);
		
	}
	
}
