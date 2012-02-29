package com.ceres.cldoc;

import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;

public interface IUserService {
	public static final long NO_USER = 3;
	public static final long NOT_DEFINED = 2;
	public static final long NOT_EQUAL = 1;
	public static final long SUCCESS = 0;

	Session login(Session session, String userName, String password);
	void register(Session session, Person person, String userName, String password);
	long setPassword(Session session, User user, String password1, String password2);
}
