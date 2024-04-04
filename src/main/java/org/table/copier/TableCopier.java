package org.table.copier;

import java.sql.SQLException;

public interface TableCopier extends AutoCloseable {
    void copy(String sourceSchema, String targetSchema) throws SQLException;
}
