package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.shared.layout.FormDesc;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ConfigurationServiceAsync {
	 void parse(String xml, AsyncCallback<FormDesc> callback);
	 void listChildren(String parent, AsyncCallback<List<String>> callback);
	 void getUploadUrl(AsyncCallback<String> callback);
}
