package org.table;

import javax.sql.DataSource;
import java.sql.SQLException;

public interface TableCopier extends AutoCloseable {
    DataSource generate(String sourceSchema, String targetSchema) throws SQLException;
}
