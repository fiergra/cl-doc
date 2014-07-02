package com.ceres.cldoc;

import java.util.HashMap;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.Participation;
import com.ceres.cldoc.Session;

public interface IParticipationService {
	void save(Session session, Participation participation);
	HashMap<String, Participation> load(Session session, Act act);
	void delete(Session session, long actId, String role);
}
