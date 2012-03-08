package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ceres.cldoc.model.LayoutDefinition;
import com.ceres.cldoc.util.Jdbc;

public class LayoutDefinitionServiceImpl implements ILayoutDefinitionService {

	private static Logger log = Logger.getLogger("LayoutDefinitionService");

	@Override
	public void save(Session session, final LayoutDefinition ld) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
				insert(con, ld, true);
				return null;
			}

			private void insert(Connection con, final LayoutDefinition ld, boolean register)
					throws SQLException {
				try {
					PreparedStatement u = con.prepareStatement(
							"update LayoutDefinition set valid_to = CURRENT_TIMESTAMP where itemclassid = (select ID from ItemClass where name = ?) and typeid = 1 and valid_to is null");
					u.setString(1, ld.name);
					int rows = u.executeUpdate();
					log.info("closed " + rows + " layoutdef(s)");
					u.close();
					
					PreparedStatement s = con.prepareStatement(
							"insert into LayoutDefinition (itemclassid, typeid, xml, valid_to) " +
							"values ((select ID from ItemClass where name = ?), 1, ?, null)", new String[]{"ID"});
					s.setString(1, ld.name);
					s.setString(2, ld.xmlLayout);
					ld.id = Jdbc.exec(s);
					log.info("inserted new layoutdef #" + ld.id);
					s.close();
				} catch (SQLException x) {
					if (register) {
						Locator.getGenericItemService().registerItemClass(con, ld.name);
						insert(con, ld, false);
					} else {
						throw x;
					}
				}
			}
		});
	}

	@Override
	public LayoutDefinition load(Session session, String className) {
		List<LayoutDefinition> list = listLayoutDefinitions(session, className);
		return list != null && !list.isEmpty() ? list.get(0) : null;
	}

	@Override
	public List<LayoutDefinition> listLayoutDefinitions(Session session,
			final String filter) {
		return Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public List<LayoutDefinition> execute(Connection con) throws SQLException {
				PreparedStatement s = con.prepareStatement(
						"select ld.id, icl.name classname, valid_To, xml  from LayoutDefinition ld " +
						"inner join ItemClass icl on icl.id = ld.itemclassid " +
						"where upper(icl.name) like ? and (valid_To >= CURRENT_TIMESTAMP or valid_to is null)");
				s.setString(1, filter != null ? filter.toUpperCase() + "%" : "%");
				ResultSet rs = s.executeQuery();
				List<LayoutDefinition> result = fecthLayoutDefinitions(rs);
				rs.close();
				s.close();
				return result;
			}
		});
	}

	protected List<LayoutDefinition> fecthLayoutDefinitions(ResultSet rs) throws SQLException {
		List<LayoutDefinition> result = new ArrayList<LayoutDefinition>();
		while (rs.next()) {
			LayoutDefinition ld = new LayoutDefinition();
			ld.id = rs.getLong("id");
			ld.name = rs.getString("classname");
//			ld.validTo = new Date(rs.getTimestamp("valid_to").getTime());
			ld.xmlLayout = rs.getString("xml");
			result.add(ld);
		}
		return result;
	}

	@Override
	public void delete(Session session, String className) {
		Jdbc.doTransactional(session, new ITransactional() {
			
			@Override
			public Void execute(Connection con) throws SQLException {
//				PreparedStatement s = con.prepareStatement("delete from LayoutDefinition where classname = ?");
//				s.setString(1, )
				return null;
			}
		});
	}
}
