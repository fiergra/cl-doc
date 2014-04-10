package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import com.ceres.cldoc.model.Entity;
import com.ceres.cldoc.model.FileSystemNode;
import com.ceres.cldoc.util.Files;
import com.ceres.cldoc.util.Jdbc;
import com.ceres.cldoc.model.ISession;

public class SettingsServiceImpl implements ISettingsService {

	@Override
	public List<FileSystemNode> listFiles(String directory) {
		return Files.list(directory);
	}

	@Override
	public void set(ISession session, final String name, final String value, final Entity entity) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws Exception {
				int i = 1;
				PreparedStatement s = con.prepareStatement("insert into Setting (name, value, entityId) values (?,?,?)");
				s.setString(i++, name);
				s.setString(i++, value);
				if (entity != null) {
					s.setLong(i++, entity.getId());
				} else {
					s.setNull(i++, Types.INTEGER);
				}
				
				try {
					s.executeUpdate();
				} catch (SQLException x) {
					String sql = "update Setting set value = ? where name = ?";
					if (entity != null) {
						sql += " and entityId = ?";
					} else {
						sql += " and entityId is null";
					}
					s = con.prepareStatement(sql);
					int rows = s.executeUpdate();
					s.close();
				}
				return null;
			}
		});
	}

	@Override
	public String get(ISession session, final String name, final Entity entity) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public String execute(Connection con) throws Exception {
				String value = null;
				
				String sql = "select * from Setting where name = ? and ";
				if (entity != null) {
					sql += "(entityId is null OR entityId = ?)";
				} else {
					sql += "entityId is null";
				}
				sql += " order by EntityId";
				
				PreparedStatement s = con.prepareStatement(sql);
				int i = 1;
				s.setString(i++, name);
				if (entity != null) {
					s.setLong(i++, entity.getId());
				}
				ResultSet rs = s.executeQuery();
				if (rs.next()) {
					value = rs.getString("value");
					Long entId = rs.getLong("entityId");
					if (entity != null && rs.wasNull() && rs.next()) {
						value = rs.getString("value");
					}
				}
				return value;
			}
		});
	}

	
}
