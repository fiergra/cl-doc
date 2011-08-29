package com.ceres.cldoc.client.service;

import java.util.List;

import com.ceres.cldoc.shared.layout.FormDesc;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("configuration")
public interface ConfigurationService extends RemoteService {
	FormDesc parse(String xml);
	List<String> listChildren(String parent);
	String getUploadUrl();
}
