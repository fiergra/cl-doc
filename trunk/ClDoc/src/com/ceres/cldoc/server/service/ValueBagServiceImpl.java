package com.ceres.cldoc.server.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.bcel.generic.ARRAYLENGTH;

import com.ceres.cldoc.client.service.ValueBagService;
import com.ceres.cldoc.shared.domain.Participation;
import com.ceres.cldoc.shared.domain.RealWorldEntity;
import com.ceres.cldoc.shared.domain.ValueBag;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ValueBagServiceImpl extends RemoteServiceServlet implements
		ValueBagService {

	static {
		ObjectifyService.register(ValueBag.class);
		ObjectifyService.register(Participation.class);
	}

	@Override
	public ValueBag findById(Number id) {
		Objectify ofy = ObjectifyService.begin();
		ValueBag valueBag = ofy.get(new Key<ValueBag>(ValueBag.class, id.longValue()));
		valueBag.setParticipations(ofy.query(Participation.class).filter("valueBag", this).list());

		for (Participation p: valueBag.getParticipations()) {
			p.entity = ofy.get(p.pkEntity);
			p.valueBag = valueBag;
		}
		
		return valueBag;
	}


	@Override
	public ValueBag save(ValueBag valueBag) {
		Objectify ofy = ObjectifyService.begin();
		ofy.put(valueBag);
		
		for (Participation p : valueBag.getParticipations()) {
			if (p.id == null) {
				p.pkEntity = new Key<RealWorldEntity>(RealWorldEntity.class, p.entity.id);
				p.pkValueBag = new Key<ValueBag>(ValueBag.class, valueBag.getId());
				ofy.put(p);
			}
		}
		
		return valueBag;
	}

	@Override
	public void delete(ValueBag valueBag) {
		Objectify ofy = ObjectifyService.begin();
		ofy.delete(valueBag);
	}


	@Override
	public Collection<ValueBag> findByEntity(RealWorldEntity entity) {
		Objectify ofy = ObjectifyService.begin();
		List<Participation> participations = ofy.query(Participation.class).filter("pkEntity", new Key<RealWorldEntity>(RealWorldEntity.class, entity.id)).list();
		List<Key<ValueBag>> keys = new ArrayList<Key<ValueBag>>(participations.size());
		for (Participation participation : participations) {
			keys.add(participation.pkValueBag);
		}

		ArrayList <ValueBag> result = new ArrayList<ValueBag>(ofy.get(keys).values());
		return result;
	}
}
