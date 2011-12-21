package com.ceres.cldoc.client.controls;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ListRetrievalService <T> {
	void retrieve(String filter, AsyncCallback<List<T>> callback);
}
