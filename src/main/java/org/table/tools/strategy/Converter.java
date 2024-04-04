package org.table.tools.strategy;

public enum Converter {
    CAMEL_CASE {
        private ConvertStrategy strategy;

        public ConvertStrategy convertStrategy() {
            if (strategy == null) {
                strategy = new CamelCaseStrategy();
            }
            return strategy;
        }
    };

    public abstract ConvertStrategy convertStrategy();
}
