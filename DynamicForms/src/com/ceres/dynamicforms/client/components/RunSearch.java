package com.ceres.dynamicforms.client.components;

import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;

public interface RunSearch<T> {

	void run(Request request, Callback callback, LabelFunc<T> replacement, LabelFunc<T> display);

}
