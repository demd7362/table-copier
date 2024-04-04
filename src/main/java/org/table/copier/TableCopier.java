package org.table.copier;

public interface TableCopier extends AutoCloseable {
    void copy(String sourceSchema, String targetSchema) throws Exception;
}
