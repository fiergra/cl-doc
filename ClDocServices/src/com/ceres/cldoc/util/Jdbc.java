package com.ceres.cldoc.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.ceres.cldoc.ITransactional;
import com.ceres.cldoc.Session;
import com.ceres.cldoc.TxManager;

public class Jdbc {

	private static Logger log = Logger.getLogger("Jdbc");

	public static Long exec(PreparedStatement s) throws SQLException {
		int rows = s.executeUpdate();
		log.finer(rows + " affected.");
		ResultSet keys = s.getGeneratedKeys();
		Long id = null;
		if (keys.next()) {
			id = keys.getLong(1);
		}
		keys.close();
		return id;
	}

	public static <T> T doTransactional(Session session, ITransactional itransactional) {
		Connection con = TxManager.start(session);
		try {
			T result = itransactional.execute(con);
			TxManager.end(session);
			return result;
		} catch (RuntimeException rx) {
			TxManager.cancel(session);
			throw rx;
		} catch (SQLException x) {
			log.severe(x.getLocalizedMessage());
			TxManager.cancel(session);
			throw new RuntimeException(x);
		}
		
	}

}
