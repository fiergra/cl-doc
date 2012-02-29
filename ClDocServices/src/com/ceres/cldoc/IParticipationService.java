package com.ceres.cldoc;

import java.util.Collection;

import com.ceres.cldoc.model.GenericItem;
import com.ceres.cldoc.model.Participation;

public interface IParticipationService {
	void save(Session session, Participation participation);
	Collection<Participation> load(Session session, GenericItem item);
}
