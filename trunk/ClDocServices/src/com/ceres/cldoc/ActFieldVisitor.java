package com.ceres.cldoc;

import java.io.Serializable;
import java.lang.reflect.Field;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ISession;
import com.ceres.cldoc.model.Participation;

public class ActFieldVisitor {
	
	
	public static String replaceVars(ISession session, Act act, String text) {
		int index = text.indexOf('{');

		while (index != -1) {
			String varName = getVarName(text, index);
			String value = getValue(session, act, varName);
			text = text.replace("{" + varName + "}", value);
			index = text.indexOf('{');
		}
		return text;
	}

	private static String getValue(ISession session, Act act, String varName) {
		Serializable value = null;
		int index = varName.indexOf('.');
		
		if (index != -1) {
			if (varName.startsWith("PATIENT.")) {
				Participation participation = act
						.getParticipation(Participation.PROTAGONIST);
				if (participation != null) {
					value = getEntityProperty(participation.entity,
							varName.substring("PATIENT.".length()));
				}
			} else {
				Object object = act.getValue(varName.substring(0, index));
				value = object != null ? getEntityProperty(object,
						varName.substring(index + 1)) : null;
			}
		} else {
			value = act.getValue(varName);
		}
		return value != null ? value.toString() : "";
	}

	private static Serializable getEntityProperty(Object entity, String propertyName) {
		Serializable value = null;
//		try {
//			PropertyDescriptor pd = new PropertyDescriptor(propertyName, entity.getClass());
//			Method read = pd.getReadMethod();
//			value = (Serializable)read.invoke(entity, null);
//		} catch (IntrospectionException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//		return null;
		
//		if (entity instanceof Person) {
//			Person p = (Person)entity;
//			if (propertyName.equals("firstName")) { return p.firstName; }
//			if (propertyName.equals("lastName")) { return p.lastName; }
//			if (propertyName.equals("dateOfBirth")) { return p.dateOfBirth; }
//		}
//		return null;
//		
		try {
			Class clazz = entity.getClass();
			Field field = clazz.getDeclaredField(propertyName);
			return (Serializable) (field != null ? field.get(entity) : null);
		} catch (Exception e) {
			e.printStackTrace();
			return "class: " + entity.getClass().getCanonicalName() + "[" + propertyName + "] ==> " + e;
		}
	}

	private static String getVarName(String text, int beginIndex) {
		int endIndex = text.indexOf('}');
		String varName = text.substring(beginIndex + 1, endIndex);
		return varName;
	}

}
