package com.ceres.dynamicforms.client.components;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

public class SearchSuggestOracle<T> extends MultiWordSuggestOracle {
		private T selected;
		private RunSearch<T> runSearch;
		private LabelFunc<T> replacement;
		private LabelFunc<T> display;
		private SearchTimer st = new SearchTimer();
		
		public SearchSuggestOracle(final RunSearch<T> runSearch, final LabelFunc<T> replacement, final LabelFunc<T> display){
			this.runSearch = runSearch;
			this.replacement = replacement;
			this.display = display;
		}
		
		@Override
		public void requestSuggestions(final Request request, final Callback callback) {
			st.schedule(request, callback);
		}

		public T getSelected() {
			return selected;
		}

		public void setSelected(T selected) {
			this.selected = selected;
		}

		class SearchTimer extends Timer {
			private Request request;
			private Callback callback;
			
			@Override
			public void run() {
				selected = null;
				runSearch.run(request, callback, replacement, display);
			}
			
			public void schedule(final Request request, final Callback callback) {
				cancel();
				this.request = request;
				this.callback = callback;
				schedule(250);
			}
			
		}
		
		
}