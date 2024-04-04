package org.table.tools.executor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.table.tools.strategy.Converter;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SimpleQueryExecutor implements QueryExecutor {
    protected final Connection connection;

    protected final Converter converter;


    @Override
    public <T> List<T> doExecute(@NonNull String query, Class<T> clazz) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (clazz == null) {
                return null;
            } else if (clazz == String.class) {
                return (List<T>) bindString(resultSet);
            } else {
                return bindObject(resultSet, clazz);
            }
        }
    }

    protected List<String> bindString(ResultSet resultSet) throws Exception {
        List<String> list = new ArrayList<>();
        while (resultSet.next()) {
            Object value = resultSet.getObject(1);
            list.add(value.toString());
        }
        return list;
    }

    protected <T> List<T> bindObject(ResultSet resultSet, Class<T> clazz) throws Exception {
        List<T> list = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()) {
            T instance = createInstance(clazz);
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(i);
                bind(instance, columnName, value);
            }
            list.add(instance);
        }
        return list;
    }

    public <T> List<T> doExecute(@NonNull String query, Class<T> clazz, String... args) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                statement.setString(i + 1, args[i]);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                if (clazz == null) {
                    return null;
                } else {
                    return bindObject(resultSet, clazz);
                }
            }
        }
    }

    protected <T> T createInstance(Class<T> targetClass) throws Exception {
        return targetClass.getDeclaredConstructor().newInstance();
    }

    protected void bind(Object instance, String columnName, Object value) throws Exception {
        try {
            String camelCaseColumnName = converter.convertStrategy().convert(columnName);
            Field targetField = instance.getClass().getDeclaredField(camelCaseColumnName);
            targetField.setAccessible(true);
            if (targetField.getType().isAssignableFrom(value.getClass())) {
                targetField.set(instance, value);
            }
        } catch (NoSuchFieldException ignored) {

        }
    }


}
