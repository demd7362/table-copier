package org.table.tools.validator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLValidator implements DataSourceValidator {
    private final Connection connection;

    public MySQLValidator(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean isRootUser() throws SQLException {
        String query = "SELECT CURRENT_USER()";
        try(Statement statement = connection.createStatement()){
            try(ResultSet resultSet = statement.executeQuery(query)){
                if(resultSet.next()){
                    String currentUser = resultSet.getString(1).split("@")[0];
                    return currentUser.equalsIgnoreCase("root");
                }
            }
        }
        return false;
    }
}
