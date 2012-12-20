package com.ceres.cldoc;

import java.util.List;

import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.Person;
import com.ceres.cldoc.model.User;

public interface IUserService {
	public static final long NO_USER = 3;
	public static final long NOT_DEFINED = 2;
	public static final long NOT_EQUAL = 1;
	public static final long SUCCESS = 0;

	Session login(Session session, String userName, String password);
	User register(Session session, Person person, Entity organisation, String userName, String password);
	long setPassword(Session session, User user, String password1, String password2);
	List<User> listUsers(Session session, String filter);
	void addRole(Session session, User user, Catalog role);
	void removeRole(Session session, User user, Catalog role);
}
