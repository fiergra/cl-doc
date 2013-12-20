package com.ceres.dynamicforms.client;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;

public interface IPreProcessor {
	Element process(Document document, Element element);
}
