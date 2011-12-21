package com.ceres.cldoc.server.service;

import com.ceres.cldoc.client.service.PersonService;
import com.ceres.cldoc.shared.domain.Address;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.IGenericItem;
import com.ceres.cldoc.shared.domain.Person;
import com.ceres.cldoc.shared.domain.RealWorldEntity;
import com.ceres.cldoc.shared.domain.GenericItem;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PersonServiceImpl extends RemoteServiceServlet implements
		PersonService {

	static {
		ObjectifyService.register(RealWorldEntity.class);
		ObjectifyService.register(Person.class);
		ObjectifyService.register(HumanBeing.class);
		ObjectifyService.register(Address.class);
	}

	@Override
	public Person save(Person person) {
		Objectify ofy = ObjectifyService.begin();
		ofy.put(person);
		return person;
	}

	@Override
	public void delete(Person person) {
		Objectify ofy = ObjectifyService.begin();
		ofy.delete(person);
	}
}
