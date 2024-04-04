package org.table.copier;

import org.table.exception.CreateTableException;
import org.table.model.ShowCreateTable;
import org.table.tools.executor.QueryExecutor;
import org.table.tools.executor.SimpleQueryExecutor;
import org.table.tools.strategy.Converter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MySQLTableCopier implements TableCopier {
    protected final Connection connection;
    protected final QueryExecutor executor;


    MySQLTableCopier(Connection connection) {
        this.connection = connection;
        this.executor = new SimpleQueryExecutor(connection, Converter.CAMEL_CASE);
    }

    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("connection closed"); // change to logger
        }
    }


    @Override
    public void copy(String sourceSchema, String targetSchema) throws Exception {
        createSchema(targetSchema);
        String ip = getIp();
        grantPermissions(targetSchema, "TESTER", ip);
        copyTables(sourceSchema, targetSchema);
    }

    private String getIp() throws SQLException {
        String jdbcURL = connection.getMetaData().getURL();
        return null;
    }

    private void grantPermissions(String schema, String username, String ip) throws Exception {
        String query = String.format("GRANT CREATE, ALTER, SELECT, INSERT, UPDATE, DELETE, REFERENCES, INDEX ON %s.* TO '%s'@'%s'",
                schema,
                username,
                ip
        );
        executor.doExecute(query, null);
    }


    private void createSchema(String schema) throws Exception {
        String query = String.format("CREATE SCHEMA IF NOT EXISTS %s", schema);
        executor.doExecute(query, null);
    }

    private void copyTables(String sourceSchema, String targetSchema) throws Exception {
        List<String> tableNames = getTableNames(sourceSchema);
        List<String> createTableQueries = new ArrayList<>();
        for (String tableName : tableNames) {
            String createTableSQL = getCreateTableSQL(sourceSchema, tableName);
            String newCreateTableSQL = createTableSQL.replace(sourceSchema, targetSchema);
            createTableQueries.add(newCreateTableSQL);
        }
        useSchema(targetSchema);
        for (String createTableSQL : createTableQueries) {
            executor.doExecute(createTableSQL, null);
        }
    }

    private List<String> getTableNames(String schema) throws Exception {
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";
        return executor.doExecute(query, String.class, schema);
    }


    private void useSchema(String schema) throws Exception {
        executor.doExecute("USE " + schema, null);
    }


    private String getCreateTableSQL(String schema, String tableName) throws Exception {
        String query = String.format("SHOW CREATE TABLE %s.%s",
                schema,
                tableName
        );
        List<ShowCreateTable> showCreateTables = executor.doExecute(query, ShowCreateTable.class);
        if (showCreateTables.isEmpty()) {
            throw new CreateTableException("Create table query execution failed. try check source schema");
        }
        return showCreateTables.get(0).getCreateTable();
    }


}
