package com.ceres.cldoc.timemanagement;

import com.ceres.cldoc.model.Entity;
import com.ceres.core.ISession;

public interface ITimeManagementService {
	WorkPattern getWorkPattern(ISession session, Entity person);
}
