package org.table.tools.validator;

import java.sql.Connection;

public class MariaDBValidator extends MySQLValidator {
    public MariaDBValidator(Connection connection) {
        super(connection);
    }
}
