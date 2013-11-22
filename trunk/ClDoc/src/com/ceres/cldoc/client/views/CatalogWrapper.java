package com.ceres.cldoc.client.views;

import java.io.Serializable;
import java.util.Date;

import com.ceres.cldoc.model.Catalog;
import com.ceres.dynamicforms.client.INamedValues;

public class CatalogWrapper implements INamedValues {

	private final Catalog catalog;

	public CatalogWrapper(Catalog catalog) {
		this.catalog = catalog;
	}

	@Override
	public Date getDate() {
		return null;
	}

	@Override
	public void setDate(Date date) {
	}

	@Override
	public Serializable getValue(String name) {
		if (name.equals("code")) {
			return catalog.code;
		} else if (name.equals("date")){
			return catalog.date;
		} else if (name.equals("shortText")){
			return catalog.shortText;
		} else if (name.equals("text")){
			return catalog.text;
		} else if (name.equals("number1")){
			return catalog.number1;
		} else if (name.equals("number2")){
			return catalog.number2;
		} else if (name.equals("logicalOrder")){
			return catalog.logicalOrder;
		}
		return null;
	}

	@Override
	public Serializable setValue(String name, Serializable value) {
		if (name.equals("code")) {
			catalog.code = (String) value;
		} else if (name.equals("date")){
			catalog.date = (Date) value;
		} else if (name.equals("shortText")){
			catalog.shortText = (String) value;
		} else if (name.equals("text")){
			catalog.text = (String) value;
		} else if (name.equals("number1")){
			catalog.number1 = (Long) value;
		} else if (name.equals("number2")){
			catalog.number2 = (Long) value;
		} else if (name.equals("logicalOrder")){
			catalog.logicalOrder = (Long) value;
		}
		return value;
	}

	public Catalog unwrap() {
		return catalog;
	}

}
