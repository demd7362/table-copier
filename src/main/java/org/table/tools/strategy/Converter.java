package org.table.tools.strategy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Converter {
    CAMEL_CASE(new CamelCaseStrategy());
    private final ConvertStrategy strategy;
}
