package com.ceres.cldoc;

import java.io.FileNotFoundException;

import com.ceres.cldoc.model.Act;

public interface IDocService {
	byte[] print(Session session, Act act) throws FileNotFoundException;
}
