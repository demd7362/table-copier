package org.table.tools.validator;

import org.table.tools.executor.QueryExecutor;
import org.table.tools.executor.SimpleQueryExecutor;
import org.table.tools.strategy.Converter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLValidator implements DataSourceValidator {
    protected final Connection connection;
    protected final QueryExecutor executor;


    public MySQLValidator(Connection connection) {
        this.connection = connection;
        this.executor = new SimpleQueryExecutor(connection, Converter.CAMEL_CASE);
    }

    @Override
    public boolean isRootUser() throws SQLException {
        String query = "SELECT CURRENT_USER()";
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    String currentUser = resultSet.getString(1).split("@")[0];
                    return currentUser.equalsIgnoreCase("root");
                }
            }
        }
        return false;
    }
}
