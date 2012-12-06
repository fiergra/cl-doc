package com.ceres.cldoc;

import java.sql.Connection;

public interface ITransactional {

	<T> T execute(Connection con) throws Exception;

}
