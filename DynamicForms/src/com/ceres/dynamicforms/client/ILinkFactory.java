package com.ceres.dynamicforms.client;

import java.util.HashMap;

import com.ceres.core.IApplication;

public interface ILinkFactory {

	InteractorLink createLink(IApplication application, Interactor interactor, String fieldName, HashMap<String, String> attributes);

}
