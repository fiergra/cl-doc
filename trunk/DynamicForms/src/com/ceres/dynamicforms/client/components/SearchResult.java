package com.ceres.dynamicforms.client.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchResult<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 7606474468788291869L;

	public long callId;
	public List<T> resultList;

	public SearchResult() {}

	public SearchResult(long callId) {
		this.callId = callId;
		resultList = new ArrayList<T>();
	}

	public void add(T person) {
		resultList.add(person);
	}
}
