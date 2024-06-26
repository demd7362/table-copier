package org.table.tools.validator;

import org.table.tools.executor.QueryExecutor;
import org.table.tools.executor.SimpleQueryExecutor;
import org.table.tools.strategy.Converter;

import java.sql.Connection;
import java.util.List;

public class MySQLValidator implements DataSourceValidator {
    protected final Connection connection;
    protected final QueryExecutor executor;


    public MySQLValidator(Connection connection) {
        this.connection = connection;
        this.executor = new SimpleQueryExecutor(connection, Converter.CAMEL_CASE);
    }

    @Override
    public boolean isRootUser() throws Exception {
        String query = "SELECT CURRENT_USER()";
        List<String> results = executor.doExecute(query, String.class);
        if (results == null || results.isEmpty()) {
            return false;
        }
        String currentUser = results.get(0).split("@")[0];
        return currentUser.equalsIgnoreCase("root");
    }
}
