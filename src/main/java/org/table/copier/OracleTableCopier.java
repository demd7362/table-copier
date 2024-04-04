package org.table.copier;

import org.apache.commons.dbcp2.BasicDataSource;
import org.table.exception.CreateTableException;
import org.table.model.ShowCreateTable;
import org.table.tools.executor.QueryExecutor;
import org.table.tools.executor.SimpleQueryExecutor;
import org.table.tools.strategy.Converter;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class OracleTableCopier implements TableCopier {
    protected final Connection connection;
    protected final QueryExecutor executor;

    OracleTableCopier(Connection connection) {
        this.connection = connection;
        this.executor = new SimpleQueryExecutor(connection, Converter.CAMEL_CASE);
    }

    public static void main(String[] args) throws Exception {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:orcl");
        dataSource.setUsername("system");
        dataSource.setPassword("password");
        Connection connection = dataSource.getConnection();
        TableCopier tableCopier = new OracleTableCopier(connection);
        tableCopier.copy("SOURCE_SCHEMA", "TARGET_SCHEMA");
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
        copyTables(sourceSchema, targetSchema);
    }

    private void createSchema(String schema) throws Exception {
        String query = String.format("CREATE USER %s IDENTIFIED BY password DEFAULT TABLESPACE users QUOTA UNLIMITED ON users", schema);
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
        for (String createTableSQL : createTableQueries) {
            executor.doExecute(createTableSQL, null);
        }
    }

    private List<String> getTableNames(String schema) throws Exception {
        String query = "SELECT table_name FROM all_tables WHERE owner = ?";
        return executor.doExecute(query, String.class, schema);
    }

    private String getCreateTableSQL(String schema, String tableName) throws Exception {
        String query = String.format("SELECT dbms_metadata.get_ddl('TABLE','%s','%s') FROM dual",
                tableName,
                schema
        );
        List<ShowCreateTable> showCreateTables = executor.doExecute(query, ShowCreateTable.class);
        if (showCreateTables == null || showCreateTables.isEmpty()) {
            String message = String.format("Create table query execution failed. check your query `%s`", query);
            throw new CreateTableException(message);
        }
        String result = showCreateTables.get(0).getCreateTable();
        System.out.println(result);
        return result;
    }
}
