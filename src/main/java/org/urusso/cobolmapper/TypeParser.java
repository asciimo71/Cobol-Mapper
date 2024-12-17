package org.urusso.cobolmapper;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum TypeParser {
    INTEGER(Integer.class, int.class) {
        @Override
        public Object parse(String subString) {
            return StringUtils.isBlank(subString) ? null : Integer.parseInt(subString);
        }
    },
    LONG(Long.class, long.class) {
        @Override
        public Object parse(String subString) {
            return StringUtils.isBlank(subString) ? null : Long.parseLong(subString);
        }
    },
    DOUBLE(Double.class, double.class) {
        @Override
        public Object parse(String subString) {
            return StringUtils.isBlank(subString) ? null : Double.parseDouble(subString);
        }
    },
    BOOLEAN(Boolean.class, boolean.class) {
        @Override
        public Object parse(String subString) {
            if (subString.length() == 1)  // handling 0/1 booleans
                subString = "1".equals(subString) ? "true" : "false";
            return StringUtils.isBlank(subString) ? null : Boolean.parseBoolean(subString);
        }
    },
    CHARACTER(Character.class, char.class) {
        @Override
        public Object parse(String subString) {
            return StringUtils.isBlank(subString) ? null : subString.charAt(0);
        }
    },
    LOCAL_DATE_TIME(LocalDateTime.class) {
        @Override
        public Object parse(String subString) {
            DateTimeFormatter formatter = customDateTimeFormatter != null ? customDateTimeFormatter : DEFAULT_DATE_TIME_FORMATTER;
            return StringUtils.isBlank(subString) ? null : LocalDateTime.parse(subString, formatter);
        }
    },
    LOCAL_DATE(LocalDate.class) {
        @Override
        public Object parse(String subString) {
            DateTimeFormatter formatter = customDateFormatter != null ? customDateFormatter : DEFAULT_DATE_FORMATTER;
            return StringUtils.isBlank(subString) ? null : LocalDate.parse(subString, formatter);
        }
    },
    STRING(String.class) {
        @Override
        public Object parse(String subString) {
            return StringUtils.isBlank(subString) ? null : subString;
        }
    };

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    private static DateTimeFormatter customDateTimeFormatter;
    private static DateTimeFormatter customDateFormatter;

    private final Class<?>[] types;

    TypeParser(Class<?>... types) {
        this.types = types;
    }

    public static TypeParser getParser(Type valueType, DateTimeFormatter dateTimeFormatter, DateTimeFormatter dateFormatter) {
        customDateTimeFormatter = dateTimeFormatter;
        customDateFormatter = dateFormatter;
        return TypeParser.getParser(valueType);
    }

    public static TypeParser getParser(Type valueType) {
        for (TypeParser parser : values()) {
            for (Class<?> type : parser.types) {
                if (type.equals(valueType)) {
                    return parser;
                }
            }
        }

        return TypeParser.STRING;
    }

    public abstract Object parse(String subString);
}
