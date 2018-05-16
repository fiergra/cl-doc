package eu.europa.ec.digit.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ceres.dynamicforms.client.SimpleTranslator;
import com.ceres.dynamicforms.client.components.LabelFunc;
import com.ceres.dynamicforms.client.components.RemoteSearchBox;
import com.ceres.dynamicforms.client.components.RunSearch;
import com.ceres.dynamicforms.client.components.SearchSuggestion;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

import eu.europa.ec.digit.eAgenda.IResource;
import eu.europa.ec.digit.eAgenda.User;

public class PersonSearchBox extends RemoteSearchBox<User> {

	private static RunSearch<User> runSearch = new RunSearch<User>() {

		@Override
		public void run(Request request, Callback callback, LabelFunc<User> replacement, LabelFunc<User> display) {
			eAgendaUI.service.findPersons(request.getQuery(), new RPCCallback<List<User>>() {

				@Override
				protected void onResult(List<User> resources) {
					Collection<Suggestion> suggestions = new ArrayList<SuggestOracle.Suggestion>();
					for (User p : resources) {
						MultiWordSuggestion suggestion = new SearchSuggestion<IResource>(p, replacement.label(p), display.label(p));
						suggestions.add(suggestion);
					}
					Response response = new Response(suggestions);
					callback.onSuggestionsReady(request, response);

				}
			});
		}
	};

	
	public PersonSearchBox() {
		super(new SimpleTranslator(), runSearch, r -> r.getDisplayName(), r -> r.getDisplayName());
	}
	
	

}
