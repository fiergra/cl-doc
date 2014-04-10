package com.ceres.cldoc;

import java.io.FileNotFoundException;

import com.ceres.cldoc.model.Act;
import com.ceres.cldoc.model.ISession;

public interface IDocService {
	byte[] print(ISession session, Act act) throws FileNotFoundException;
}
