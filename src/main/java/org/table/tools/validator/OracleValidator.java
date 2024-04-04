package org.table.tools.validator;

import org.table.tools.executor.QueryExecutor;
import org.table.tools.executor.SimpleQueryExecutor;
import org.table.tools.strategy.Converter;

import java.sql.Connection;
import java.util.List;

public class OracleValidator implements DataSourceValidator {
    protected final Connection connection;
    protected final QueryExecutor executor;

    public OracleValidator(Connection connection) {
        this.connection = connection;
        this.executor = new SimpleQueryExecutor(connection, Converter.CAMEL_CASE);
    }

    @Override
    public boolean isRootUser() throws Exception {
        String query = "SELECT USER FROM DUAL";
        List<String> results = executor.doExecute(query, String.class);
        if (results == null || results.isEmpty()) {
            return false;
        }
        String currentUser = results.get(0).toUpperCase();
        List<String> adminable = List.of("SYS", "SYSTEM");
        return adminable.contains(currentUser);
    }
}
