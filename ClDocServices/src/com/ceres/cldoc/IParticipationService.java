package com.ceres.cldoc;

import java.util.HashMap;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;
import com.ceres.core.ISession;

public interface IParticipationService {
	void save(ISession session, Participation participation);
	HashMap<Long, Participation> load(ISession session, Act act);
	void delete(ISession session, long actId, long roleId);
}
