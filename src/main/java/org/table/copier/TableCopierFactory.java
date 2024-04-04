package org.table.copier;

import org.table.exception.InvalidUserException;
import org.table.tools.validator.DataSourceValidator;
import org.table.tools.validator.MariaDBValidator;
import org.table.tools.validator.MySQLValidator;
import org.table.tools.validator.OracleValidator;

import javax.sql.DataSource;
import java.sql.Connection;

public class TableCopierFactory {
    private static final String MYSQL_PREFIX = "jdbc:mysql:";
    private static final String MARIADB_PREFIX = "jdbc:mariadb:";
    private static final String ORACLE_PREFIX = "jdbc:oracle:";

    public static TableCopier create(DataSource dataSource) throws Exception {
        TableCopier tableCopier;
        DataSourceValidator validator;
        Connection connection = dataSource.getConnection();
        String jdbcURL = connection.getMetaData().getURL();
        if (jdbcURL.startsWith(MYSQL_PREFIX)) {
            tableCopier = new MySQLTableCopier(connection);
            validator = new MySQLValidator(connection);
        } else if (jdbcURL.startsWith(MARIADB_PREFIX)) {
            tableCopier = new MariaDBTableCopier(connection);
            validator = new MariaDBValidator(connection);
        } else if (jdbcURL.startsWith(ORACLE_PREFIX)) {
            tableCopier = new OracleTableCopier(connection);
            validator = new OracleValidator(connection);
        } else {
            throw new IllegalArgumentException("Unsupported database type: " + jdbcURL);
        }
        if (!validator.isRootUser()) {
            throw new InvalidUserException("Invalid data source has founded. only root user allowed.");
        }

        return tableCopier;
    }

}
