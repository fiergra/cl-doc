package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Organisation;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;
import com.ceres.core.ISession;

public interface IUserService {
	public static final long NO_USER = 3;
	public static final long NOT_DEFINED = 2;
	public static final long NOT_EQUAL = 1;
	public static final long SUCCESS = 0;

	ISession login(ISession session, String userName, String password);
	User register(ISession session, Person person, Organisation organisation, String userName, String password);
	long setPassword(ISession session, User user, String password1, String password2);
	List<User> listUsers(ISession session, String filter);
	void addRole(ISession session, User user, Catalog role);
	void removeRole(ISession session, User user, Catalog role);
}
