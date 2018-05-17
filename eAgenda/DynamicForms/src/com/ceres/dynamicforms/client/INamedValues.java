package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.Date;

public interface INamedValues extends Serializable {
	Date getDate();
	void setDate(Date date);
	
	Serializable getValue(String name);
	Serializable setValue(String name, Serializable value);
}
