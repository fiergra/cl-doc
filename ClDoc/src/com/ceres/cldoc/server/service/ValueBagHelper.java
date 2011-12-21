package com.ceres.cldoc.server.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.env.IGenericField;
import org.mortbay.log.Log;

import com.ceres.cldoc.shared.domain.GenericItem;
import com.ceres.cldoc.shared.domain.GenericItem;
import com.ceres.cldoc.shared.domain.IGenericItemField;
import com.googlecode.objectify.Key;


public class ValueBagHelper {

	public static GenericItem convert(Object object) {
		GenericItem vb = new GenericItem(object.getClass().getCanonicalName(), null);

		Field[] dfs = object.getClass().getFields();

		for (Field field : dfs) {
			try {
				String fieldName = field.getName();

				if (String.class.isAssignableFrom(field.getType())) {
					String value = (String) field.get(object);
					vb.set(fieldName, value);
				} else if (Number.class.isAssignableFrom(field.getType())) {
					Number value = (Number) field.get(object);
					vb.set(fieldName, value);
				} else if (Date.class.isAssignableFrom(field.getType())) {
					Date value = (Date) field.get(object);
					vb.set(fieldName, value);
				} else {
					Object value = field.get(object);
					if (value != null && !(value instanceof Key)) {
						vb.set(fieldName, convert(value));
//					} else {
//						vb.set(fieldName, (ValueBag)null);
					}
				}
			} catch (Exception x) {
				Log.debug(x.getMessage());
			}
		}

		return vb;
	}
	
	public static <T> T reconvert(GenericItem valueBag) {
		if (valueBag == null || valueBag.getClassName() == null) { 
			return null; 
		} 
		try {
			Class<T> clazz = (Class<T>) Class.forName(valueBag.getClassName());
			T result = clazz.newInstance();
			
			Set<Entry<String, IGenericItemField>> fields = valueBag.getFields().entrySet();
			for (Entry<String, IGenericItemField> entry : fields) {
				Field cf = clazz.getField(entry.getKey());
				if (Modifier.isPublic(cf.getModifiers())) {
					if (entry.getValue() instanceof GenericItem) {
						cf.set(result, reconvert((GenericItem)entry.getValue()));
					} else {
						cf.set(result, entry.getValue());
					}
				}
			}

//
//			
//			Set<Entry<String, String>> strings = valueBag.getStrings().entrySet();
//			for (Entry<String, String> entry : strings) {
//				Field cf = clazz.getField(entry.getKey());
//				if (Modifier.isPublic(cf.getModifiers())) {
//					cf.set(result, entry.getValue());
//				}
//			}
//
//			Set<Entry<String, Number>> numbers = valueBag.getNumbers().entrySet();
//			for (Entry<String, Number> entry : numbers) {
//				Field cf = clazz.getField(entry.getKey());
//				if (Modifier.isPublic(cf.getModifiers())) {
//					cf.set(result, entry.getValue());
//				}
//			}
//
//			Set<Entry<String, Date>> dates = valueBag.getDates().entrySet();
//			for (Entry<String, Date> entry : dates) {
//				Field cf = clazz.getField(entry.getKey());
//				if (Modifier.isPublic(cf.getModifiers())) {
//					cf.set(result, entry.getValue());
//				}
//			}
//			
//			Set<Entry<String, ValueBag>> children = valueBag.getChildren().entrySet();
//			for (Entry<String, ValueBag> entry : children) {
//				Field cf = clazz.getField(entry.getKey());
//				if (Modifier.isPublic(cf.getModifiers())) {
//					cf.set(result, reconvert((ValueBag) entry.getValue()));
//				}
//			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
