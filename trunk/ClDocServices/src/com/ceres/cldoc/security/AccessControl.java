package com.ceres.cldoc.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.ceres.cldoc.ITransactional;
import com.ceres.cldoc.Locator;
import com.ceres.cldoc.model.Catalog;
import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.ISession;
import com.ceres.cldoc.model.User;
import com.ceres.cldoc.util.Jdbc;

public class AccessControl {
	public static Policies get(final ISession session, final User user, final Entity entity) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Policies execute(Connection con) throws SQLException {
				HashMap<String, Collection<String>> actions = new HashMap<String, Collection<String>>();
				getRoles(session, con, user, entity);
				
				if (!user.roles.isEmpty()) {
					String sql = "select " +
							"role.id role_id, role.code role_code, " +
							"action.id action_id, action.code action_code, " +
							"objectType.id objectType_id, objectType.code objectType_code " +
							"from Policy P " +
							"inner join Catalog role on role.id = P.role " +
							"inner join Catalog objectType on objectType.id = P.objectType " +
							"inner join Catalog action on action.id = P.action where role.id in(" + getRoleIds(user) + ") ";
					PreparedStatement s = con.prepareStatement(sql);
					ResultSet rs = s.executeQuery();
					while (rs.next()) {
//						Catalog role = new Catalog(rs.getLong("role_id"), rs.getString("role_code"));
//						Catalog objectType = new Catalog(rs.getLong("objectType_id"), rs.getString("objectType_code"));
//						Catalog action = new Catalog(rs.getLong("action_id"), rs.getString("action_code"));
						String role = rs.getString("role_code");
						String objectType = rs.getString("objectType_code");
						String action = rs.getString("action_code");
						
						Collection<String> typeActions = actions.get(objectType);
						if (typeActions == null) {
							typeActions = new HashSet<String>();
							actions.put(objectType, typeActions);
						}
						
						typeActions.add(action);
					}
					rs.close();
					s.close();
				}					
				return new Policies(user, actions);
			}

			private String getRoleIds(User user) {
				if (user.roles != null && user.roles.size() > 0) {
					Iterator<Catalog> iter = user.roles.iterator();
					StringBuffer ids = new StringBuffer(String.valueOf(iter.next().id));
					while (iter.hasNext()) {
						ids.append("," + iter.next().id);
					}
					return ids.toString();
				} else {
					return "";
				}
			}

			private void getRoles(final ISession session, Connection con,
					final User user, final Entity entity) throws SQLException {
				String sql = "select * from Assignment where startdate <= CURRENT_DATE AND (enddate is null OR enddate > CURRENT_DATE) ";

				if (user != null) {
					sql += " AND userid = ?";
				}
				if (entity != null) {
					sql += " AND entityid = ?";
				}

				PreparedStatement s = con.prepareStatement(sql);
				int i = 1;
				if (user != null) {
					s.setLong(i++, user.id);
				}
				if (entity != null) {
					s.setLong(i++, entity.getId());
				}
				ResultSet rs = s.executeQuery();
				user.roles = new HashSet<Catalog>();
				while (rs.next()) {
					Catalog role = Locator.getCatalogService().load(session, rs.getLong("role"));
					role.children = Locator.getCatalogService().loadList(session, role);
					addRole(user, role, false);
				}
				rs.close();
				s.close();
			}

			private void addRole(User user, Catalog role, boolean isImplied) {
				if (!isImplied) {
					role.parent = null;
				}
				user.roles.add(role);
				if (role.hasChildren()) {
					for (Catalog child:role.children) {
						addRole(user, child, true);
					}
				}
			}
		});
	}
}
