package com.ceres.cldoc.server.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import com.ceres.cldoc.client.service.HumanBeingService;
import com.ceres.cldoc.client.service.PersonService;
import com.ceres.cldoc.shared.domain.HumanBeing;
import com.ceres.cldoc.shared.domain.Person;
import com.ceres.cldoc.shared.domain.GenericItem;
import com.ceres.cldoc.shared.util.Strings;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class HumanBeingServiceImpl extends RemoteServiceServlet implements
		HumanBeingService {

	static {
		ObjectifyService.register(HumanBeing.class);
	}

	public List<HumanBeing> search(String filter) {
		HashSet<HumanBeing> result = new HashSet<HumanBeing>();
		Objectify ofy = ObjectifyService.begin();
		String transcript = Strings.transcribe(filter);
		
		result.addAll(ofy.query(HumanBeing.class).filter("transcriptLastName >=", transcript).filter("transcriptLastName <", transcript + "\ufffd").list());
		result.addAll(ofy.query(HumanBeing.class).filter("transcriptFirstName >=", transcript).filter("transcriptFirstName <", transcript + "\ufffd").list());

		return new ArrayList<HumanBeing>(result);
	}

	@Override
	public HumanBeing findById(long id) {
		Objectify ofy = ObjectifyService.begin();
		HumanBeing humanBeing = ofy.get(new Key<HumanBeing>(HumanBeing.class, id));
		return humanBeing;
	}

	private static final PersonService personService = new PersonServiceImpl();

	@Override
	public HumanBeing save(HumanBeing humanBeing) {
		Objectify ofy = ObjectifyService.begin();
		ofy.put(humanBeing);
		return humanBeing;
	}

	@Override
	public void delete(HumanBeing humanBeing) {
		Objectify ofy = ObjectifyService.begin();
		ofy.delete(humanBeing);
	}
}
