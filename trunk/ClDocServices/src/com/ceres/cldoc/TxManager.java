package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.ceres.core.ISession;
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
				dataSource = (DataSource) envCtx.lookup("jdbc/ClDoc");
				dataSource.getConnection().close();
//				
//				if (dataSource instanceof MysqlConnectionPoolDataSource) {
//					MysqlConnectionPoolDataSource ds = (MysqlConnectionPoolDataSource) dataSource;
//					ds.setDatabaseName("ClDoc");
//					ds.setUser("ralph4");
//					ds.setPassword("sql4");
//					ds.setProfileSQL(false);
//					ds.setDumpMetadataOnColumnNotFound(true);
//					ds.setDumpQueriesOnException(true);
//					try {
//						ds.getConnection().close();
//					} catch (SQLException x) {
//						ds.setUser("root");
//						ds.setPassword("sql4");
//						ds.getConnection().close();
//					}
//					dataSource = ds;
//				}
				
				log.info("datasource resource found!");
			} catch (Exception x) {
				try {
					log.info("datasource resource NOT found: "
							+ x.getLocalizedMessage());
					MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
					ds.setDatabaseName("ClDoc");
					ds.setUser("root");
					ds.setPassword("sql4");
					ds.setProfileSQL(false);
					ds.setDumpMetadataOnColumnNotFound(true);
					ds.setDumpQueriesOnException(true);
					ds.getConnection().close();
					dataSource = ds;
				} catch (SQLException e) {
					log.info("datasource resource NOT found: "
							+ e.getLocalizedMessage());
					MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
					ds.setDatabaseName("ClDoc");
					ds.setServerName("krebsgesellschaft.dyndns.org");
					ds.setPort(2007);
					ds.setUser("ralph4");
					ds.setPassword("sql4");
					ds.getConnection().close();
					dataSource = ds;
				}
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

	private static ConcurrentHashMap<ISession, Transaction> transactions = new ConcurrentHashMap<ISession, Transaction>();

	public static synchronized Connection start(ISession session) {
		Transaction tx = transactions.get(session);
		if (tx == null) {
			log.finest("starting transaction (" + session.getId() + ")");
			tx = new Transaction();
			transactions.put(session, tx);
		} else {
			log.finest("using transaction (" + session.getId() + "/" + tx.txCount
					+ ")");
		}
		if (tx.txCount++ == 0) {
			log.finest("get connection");
			tx.con = getConnection();
		}
		return tx.con;
	}

	public static synchronized void end(ISession session) {
		Transaction tx = transactions.get(session);

		if (--tx.txCount == 0) {
			try {
				log.finest("committing transaction (" + session.getId() + ")");
				tx.con.commit();
				tx.con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			transactions.remove(session);
			tx.con = null;
		} else {
			log.finest("ending transaction (" + session.getId() + "/"
					+ tx.txCount + ")");
		}
	}

	public static synchronized void cancel(ISession session) {
		Transaction tx = transactions.get(session);

		if (tx.txCount > 0) {
			log.finest("cancel transaction (" + session.getId() + ")");
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
