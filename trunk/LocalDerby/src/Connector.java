import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;


public class Connector  {

    private String protocol = "jdbc:derby:";

	/**
	 * @param args
	 * @throws SQLException 
	 */

    private DataSource dataSource;
    
	private DataSource getDataSource() throws SQLException, NamingException {
		if (dataSource == null) {
			EmbeddedConnectionPoolDataSource ds = new EmbeddedConnectionPoolDataSource();
			ds.setDatabaseName("d:/javadb/databases/cldoc");
			ds.setCreateDatabase("create");
			dataSource = ds;
		}
		
		
		
        return dataSource;
		
	}
	
	private Connection getConnection() {
		try {
			return getDataSource().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void releaseConnection(Connection c) {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws SQLException {
		Connector c = new Connector();
		Connection con = c.getConnection();
		
		PreparedStatement s = con.prepareStatement("select * from person where per_id = ?");
		s.setLong(1, 36762l);
		ResultSet rs = s.executeQuery();
		if (rs.next()) {
			System.out.println(rs.getString("firstname") + " " + rs.getString("lastname") + " *" + rs.getDate("birthdate"));
		}
		rs.close();
		s.close();
		c.releaseConnection(con);
	}

}
