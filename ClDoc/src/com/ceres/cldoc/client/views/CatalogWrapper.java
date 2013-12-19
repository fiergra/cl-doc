package com.ceres.cldoc.client.views;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import com.ceres.cldoc.model.Catalog;

public class CatalogWrapper extends HashMap<String, Serializable> {

	private static final long serialVersionUID = 1L;
	private final Catalog catalog;

	public CatalogWrapper(Catalog catalog) {
		this.catalog = catalog;
		put("code", catalog.code);
		put("date", catalog.date);
		put("text", catalog.text);
		put("shortText", catalog.shortText);
		put("number1", catalog.number1);
		put("number2", catalog.number2);
		put("logicalOrder", catalog.logicalOrder);
	}

	public Catalog unwrap() {
		catalog.code = (String) get("code");
		catalog.date = (Date) get("date");
		catalog.shortText = (String) get("shortText");
		catalog.text = (String) get("text");
		catalog.number1 = (Long) get("number1");
		catalog.number2 = (Long) get("number2");
		catalog.logicalOrder = (Long) get("logicalOrder");
		return catalog;
	}

}
