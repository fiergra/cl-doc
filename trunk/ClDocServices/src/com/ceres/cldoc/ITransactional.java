package com.ceres.cldoc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ITransactional {

	<T> T execute(Connection con) throws SQLException;

}
