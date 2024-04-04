package org.table.tools.validator;

import java.sql.SQLException;

public interface DataSourceValidator {
    boolean isRootUser() throws SQLException;
}
