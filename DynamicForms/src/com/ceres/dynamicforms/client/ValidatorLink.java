package com.ceres.dynamicforms.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ceres.dynamicforms.client.Interactor.LinkChangeHandler;
import com.ceres.dynamicforms.client.components.AbstractFactory;

public class ValidatorLink extends InteractorLink {
	
	public enum Test {GT, GTE, LT, LTE};
	
	public static final String TEST = "test";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	
	public static class Factory extends AbstractFactory {

		public Factory(ITranslator translator) {
			super(translator);
		}

		@Override
		public InteractorLink createLink(final Interactor interactor, String fieldName, HashMap<String, String> attributes) {
			String sTest = attributes.get(TEST);
			Test test = Test.valueOf(sTest);
			final ValidatorLink vLink = new ValidatorLink(interactor, test, attributes.get(LEFT), attributes.get(RIGHT));
			
			interactor.addChangeHandler(new LinkChangeHandler() {
				
				@Override
				protected void onChange(InteractorLink link) {
					vLink.hilite(vLink.isValid());
				}
			});
			return vLink;
		}
		
	}

	private static int instanceCounter = 0;
	private String leftName;
	private String rightName;
	private Test test;
	
	public ValidatorLink(Interactor interactor, Test test, String leftName, String rightName) {
		super(interactor, "validator#" + instanceCounter++);
		this.test = test; 
		this.leftName = leftName; 
		this.rightName = rightName; 
	}

	@Override
	public void toDialog(Map<String, Serializable> item) {
	}

	@Override
	public void fromDialog(Map<String, Serializable> item) {
	}

	@Override
	public void enable(boolean enabled) {
	}

	@Override
	protected void hilite(boolean isValid) {
		InteractorLink left = interactor.getLink(leftName);
		InteractorLink right = interactor.getLink(rightName);
		
		left.hilite(isValid);
		right.hilite(isValid);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@SuppressWarnings("rawtypes")
	private Comparable getComparable(InteractorLink link) {
		Map<String, Serializable> item = new HashMap<>();
		link.fromDialog(item);
		Serializable value = item.get(link.name);
		return value instanceof Comparable ? (Comparable)value : null;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean isValid() {
		InteractorLink left = interactor.getLink(leftName);
		InteractorLink right = interactor.getLink(rightName);
		boolean isValid = left.isValid() && right.isValid();
		
		if (isValid && (!left.isEmpty() || right.isEmpty())) {
			Comparable cLeft = getComparable(left);
			Comparable cRight = getComparable(right);
			
			@SuppressWarnings("unchecked")
			int result = cLeft.compareTo(cRight);
			
			switch (test) {
			case GT: isValid = result < 0; break; 
			case GTE: isValid = result <= 0; break; 
			case LT: isValid = result > 0; break; 
			case LTE: isValid = result >= 0; break; 
			}
			
		}
				
		return isValid;
	}
	
	

}
