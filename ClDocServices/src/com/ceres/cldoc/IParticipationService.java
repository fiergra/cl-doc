package com.ceres.cldoc;

import java.util.HashMap;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;

public interface IParticipationService {
	void save(Session session, Participation participation);
	HashMap<Long, Participation> load(Session session, Act act);
	void delete(Session session, long actId, long roleId);
}
