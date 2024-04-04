package org.table.copier;

import java.sql.Connection;
import java.sql.SQLException;

public class MariaDBTableCopier extends MySQLTableCopier {

    protected MariaDBTableCopier(Connection connection) throws SQLException {
        super(connection);
    }
}
