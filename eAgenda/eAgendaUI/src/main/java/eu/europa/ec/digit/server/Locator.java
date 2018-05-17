package eu.europa.ec.digit.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public class Locator {
	
	private static Logger log = Logger.getLogger(Locator.class.getCanonicalName());
	private Locator() {}
	
	@SuppressWarnings("rawtypes")
	private static HashMap<Class, Collection> serviceImplementations = new HashMap<Class, Collection>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Collection<T> getImplementations(Class interfaceClazz) {
		Collection <T> result = serviceImplementations.get(interfaceClazz);
		if (result == null) {
			result = new ArrayList<T>();
			serviceImplementations.put(interfaceClazz, result);
			
			log.info("loading implementation of '" + interfaceClazz.getCanonicalName() + "'");
			ServiceLoader<T> loader = ServiceLoader.load(interfaceClazz);
			Iterator<T> implementations = loader.iterator();
			while (implementations.hasNext()) {
				result.add((T) implementations.next());
			}

		}
		return result;
	}

	
	@SuppressWarnings({ "rawtypes" })
	public static synchronized <T> T getImplementation(Class interfaceClazz) throws ClassNotFoundException {
		Collection<T> result = getImplementations(interfaceClazz);
		
		if (result.isEmpty()) {
			log.severe("NO implementation found for: " + interfaceClazz.getCanonicalName());
			throw new ClassNotFoundException(interfaceClazz.getCanonicalName());
		} 
		
		if (result.size() > 1) {
			log.warning("multiple implementations of " + interfaceClazz.getCanonicalName() + ". First one is chosen: " + result.getClass().getCanonicalName());
		}
		return result.iterator().next();
	}
	
}
