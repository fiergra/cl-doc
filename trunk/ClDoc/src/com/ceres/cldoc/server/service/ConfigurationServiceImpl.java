package com.ceres.cldoc.server.service;

import java.util.ArrayList;
import java.util.List;

import com.ceres.cldoc.client.service.ConfigurationService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ConfigurationServiceImpl extends RemoteServiceServlet implements
		ConfigurationService {

	@Override
	public List<String> listChildren(String parent) {
		ArrayList<String> result = new ArrayList<String>();
		if (parent.equals("MAIN")) {
			result.add("HOME");
			result.add("CONFIG");
			result.add("DEBUG");
		} else if (parent.equals("PERSONALFILE")) {
			result.add("HISTORY");
			result.add("DETAILS");
		}
		return result;
	}

}
