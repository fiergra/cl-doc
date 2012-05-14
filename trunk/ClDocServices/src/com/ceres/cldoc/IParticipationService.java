package com.ceres.cldoc;

import java.util.Collection;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;

public interface IParticipationService {
	void save(Session session, Participation participation);
	Collection<Participation> load(Session session, Act act);
}
