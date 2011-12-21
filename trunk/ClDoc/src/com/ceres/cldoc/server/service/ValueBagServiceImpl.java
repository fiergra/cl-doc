package com.ceres.cldoc.server.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ceres.cldoc.client.service.ValueBagService;
import com.ceres.cldoc.shared.domain.GenericItem;
import com.ceres.cldoc.shared.domain.IGenericItem;
import com.ceres.cldoc.shared.domain.Participation;
import com.ceres.cldoc.shared.domain.RealWorldEntity;
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
		ObjectifyService.register(GenericItem.class);
		ObjectifyService.register(Participation.class);
	}

	@Override
	public GenericItem findById(Number id) {
		Objectify ofy = ObjectifyService.begin();
		GenericItem valueBag = ofy.get(new Key<GenericItem>(GenericItem.class, id.longValue()));
		valueBag.setParticipations(ofy.query(Participation.class).filter("valueBag", this).list());

		for (Participation p: valueBag.getParticipations()) {
			p.entity = ofy.get(p.pkEntity);
			p.valueBag = valueBag;
		}
		
		return valueBag;
	}


	@Override
	public GenericItem save(GenericItem valueBag) {
		Objectify ofy = ObjectifyService.begin();
		ofy.put(valueBag);
		
		if (valueBag.getParticipations() != null) {
		
			for (Participation p : valueBag.getParticipations()) {
				if (p.id == null) {
					p.pkEntity = new Key<RealWorldEntity>(RealWorldEntity.class, p.entity.id);
					p.pkValueBag = new Key<GenericItem>(GenericItem.class, valueBag.getId());
					ofy.put(p);
				}
			}
		}		
		return valueBag;
	}

	@Override
	public void delete(GenericItem item) {
		Objectify ofy = ObjectifyService.begin();
		ofy.delete(item);
	}


	@Override
	public List<GenericItem> findByEntity(RealWorldEntity entity) {
		Objectify ofy = ObjectifyService.begin();
		List<Participation> participations = ofy.query(Participation.class).filter("pkEntity", new Key<RealWorldEntity>(RealWorldEntity.class, entity.id)).list();
		List<Key<GenericItem>> keys = new ArrayList<Key<GenericItem>>(participations.size());
		for (Participation participation : participations) {
			keys.add(participation.pkValueBag);
		}

		ArrayList <GenericItem> result = new ArrayList<GenericItem>(ofy.get(keys).values());
		
		Collections.sort(result, new Comparator<GenericItem>() {

			@Override
			public int compare(GenericItem o1, GenericItem o2) {
				return o2.getCreated().compareTo(o1.getCreated());
			}
		});
		
		return result;
	}
}
