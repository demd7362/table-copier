package org.table;

import lombok.SneakyThrows;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TableCopierFactory {


    public static TableCopier create(DataSource dataSource) throws SQLException {
        String jdbcUrl;
        try (Connection connection = dataSource.getConnection()) {
            jdbcUrl = connection.getMetaData().getURL();
        }

        if (jdbcUrl.startsWith("jdbc:mysql:")) {
            return new MySQLTableCopier(dataSource);
        } else if (jdbcUrl.startsWith("jdbc:oracle:")) {
//            return new OracleTableCopier(dataSource);
        } else if (jdbcUrl.startsWith("jdbc:mariadb:")) {
//            return new MariaDBTableCopier(dataSource);
        }
        throw new IllegalArgumentException("Unsupported database type: " + jdbcUrl);
    }


}
