package org.table.copier;

import org.table.copier.TableCopier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class MySQLTableCopier implements TableCopier {
    protected final Connection connection;


    MySQLTableCopier(Connection connection) throws SQLException {
        this.connection = connection;
    }

    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("connection closed"); // change to logger
        }
    }


    @Override
    public void copy(String sourceSchema, String targetSchema) throws SQLException {
        createSchema(targetSchema);
        copyTables(sourceSchema, targetSchema);
    }



//    private void grantPermissions(String schema) throws SQLException {
//        try (PreparedStatement statement = connection.prepareStatement(
//                "GRANT ALL PRIVILEGES ON " + schema + ".* TO ?@'%'")) {
//            statement.setString(1, username);
//            statement.execute();
//        }
//    }

    private void createSchema(String schema) throws SQLException {
        try(PreparedStatement statement = connection.prepareStatement("CREATE SCHEMA IF NOT EXISTS " + schema)){
            statement.execute();
        }
    }

    private void copyTables(String sourceSchema, String targetSchema) throws SQLException {
        List<String> tableNames = getTableNames(sourceSchema);
        for (String tableName : tableNames) {
            copyTable(sourceSchema, targetSchema, tableName);
        }
    }

    private List<String> getTableNames(String schema) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = ?")){
            statement.setString(1, schema);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tableNames.add(resultSet.getString("table_name"));
                }
            }
        }
        return tableNames;
    }

    private void copyTable(String sourceSchema, String targetSchema, String tableName) throws SQLException {
        String createTableSql = getCreateTableSql(sourceSchema, tableName);
        String newCreateTableSql = createTableSql.replace(sourceSchema, targetSchema);
        try(PreparedStatement statement = connection.prepareStatement(newCreateTableSql)){
            statement.execute();
        }

    }


    private String getCreateTableSql(String schema, String tableName) throws SQLException {
        String createTableSql = null;
        try(PreparedStatement statement = connection.prepareStatement("SHOW CREATE TABLE " + schema + "." + tableName)){
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    createTableSql = resultSet.getString(2);
                }
            }
        }
        return createTableSql;
    }


}
