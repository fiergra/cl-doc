package com.ceres.dynamicforms.client;

import java.util.HashMap;

public interface ILinkFactory {

	InteractorLink createLink(Interactor interactor, String fieldName, HashMap<String, String> attributes);

}
