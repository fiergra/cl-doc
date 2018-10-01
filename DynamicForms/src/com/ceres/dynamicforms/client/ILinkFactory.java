package com.ceres.dynamicforms.client;

import java.util.HashMap;

public interface ILinkFactory<T> {

	InteractorLink<T> createLink(Interactor<T> interactor, String fieldName, HashMap<String, String> attributes);

}
