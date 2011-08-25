package com.ceres.cldoc.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ConfigurationServiceAsync {
	 void listChildren(String parent, AsyncCallback<List<String>> callback);
}
