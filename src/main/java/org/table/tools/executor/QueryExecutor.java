package org.table.tools.executor;

import java.util.List;

public interface QueryExecutor {
    <T> List<T> doExecute(String query, Class<T> clazz) throws Exception;

    <T> List<T> doExecute(String query, Class<T> clazz, String... args) throws Exception;
}
