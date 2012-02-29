package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class TxManager {
	private static Logger log = Logger.getLogger("TxManager");
    private static DataSource dataSource;
    
	private static DataSource getDataSource() throws SQLException {
		if (dataSource == null) {
			try {
				log.info("look up datasource...");
				Context initCtx = new InitialContext();
				Context envCtx = (Context) initCtx.lookup("java:comp/env");
				dataSource = (DataSource)envCtx.lookup("jdbc/ClDoc");
				log.info("datasource resource found!");
			} catch (Exception x) {
				log.info("datasource resource NOT found: " + x.getLocalizedMessage());
				MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
				ds.setDatabaseName("test");
				ds.setProfileSQL(false);
				ds.setDumpMetadataOnColumnNotFound(true);
				ds.setDumpQueriesOnException(true);
	//			ds.setUser("ralph4");
	//			ds.setPassword("sql4");
				dataSource = ds;
			}
		} 
        return dataSource;
		
	}
	
	private static Connection getConnection() {
		Connection con = null;
		try {
			con = getDataSource().getConnection();
			con.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	private static ConcurrentHashMap<Session, Transaction> transactions = new ConcurrentHashMap<Session, Transaction>();
	
	public static Connection start(Session session) {
		Transaction tx = transactions.get(session);
		if (tx == null) {
			log.info("starting transaction (" + session.getId() + ")");
			tx = new Transaction();
			transactions.put(session, tx);
		} else {
			log.info("using transaction (" + session.getId() + "/" + tx.txCount + ")");
		}
		if (tx.txCount++ == 0) {
			log.info("get connection");
			tx.con = getConnection();
		}
		return tx.con;
	}

	public static void end(Session session) {
		Transaction tx = transactions.get(session);

		if (--tx.txCount == 0) {
			try {
				log.info("committing transaction (" + session.getId() + ")");
				tx.con.commit();
				tx.con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			tx.con = null;
		} else {
			log.info("ending transaction (" + session.getId() + "/" + tx.txCount + ")");
		}
	}

	public static void cancel(Session session) {
		Transaction tx = transactions.get(session);

		if (tx.txCount > 0) {
			log.info("cancel transaction (" + session.getId() + ")");
			tx.txCount = 0;
			if (tx.con != null) {
				try {
					tx.con.rollback();
					tx.con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				tx.con = null;
			}
		}
	}
	
	
}