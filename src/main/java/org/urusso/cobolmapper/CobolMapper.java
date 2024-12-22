package org.urusso.cobolmapper;

import lombok.SneakyThrows;
import org.urusso.cobolmapper.annotation.CobolSegment;
import org.urusso.cobolmapper.exception.CobolExceptionEnum;
import org.urusso.cobolmapper.exception.CobolMapperException;

import java.lang.reflect.*;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CobolMapper {
    private int delimiterSize = ";".length();
    private DateTimeFormatter customDateTimeFormatter;
    private DateTimeFormatter customDateFormatter;

    /**
     * The main method that maps the Cobol {@link String}.
     * <p>
     * The output {@code DTO} can be any DTO as long as it contains {@link CobolSegment} for some of its class variables.
     * </p>
     *
     * @param cobolInput The Cobol {@link String} that is going to be mapped
     * @param clazz The class type of the output {@code DTO}
     * @return DTO The {@code DTO} mapped starting from the cobolInput {@link String}
     * @param <T> The output {@code DTO}
     */
    @SneakyThrows
    public <T> T map(String cobolInput, Class<T> clazz) {
        T rootObject = getInstance(clazz);

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            var isAnnotated = field.isAnnotationPresent(CobolSegment.class);

            if (isAnnotated) {
                Object value = getValue(cobolInput, field, 0);
                field.set(rootObject, value);
            }
        }

        return rootObject;
    }

    /**
     * <p>Sets a custom {@link DateTimeFormatter} for date and time formatting.</p>
     * <p>
     * This formatter will be used to customize how date and time values are
     * formatted and parsed within this {@link CobolMapper} instance. </p>
     * <p>
     * If not set, or a {@code null} value is passed, the default {@code DateTimeFormatter.ISO_DATE_TIME} will be used instead.
     * </p>
     *
     * @param pattern The custom {@link String} pattern to use for the date and time values formatting
     * @return the current {@code CobolMapper} instance with the custom formatter
     */
    public CobolMapper withDateTimeFormatter(String pattern) {
        this.customDateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return this;
    }

    /**
     * <p>Sets a custom {@link DateTimeFormatter} for date formatting.</p>
     * <p>
     * This formatter will be used to customize how date values are
     * formatted and parsed within this {@link CobolMapper} instance. </p>
     * <p>
     * If not set, or a {@code null} value is passed, the default {@code DateTimeFormatter.ISO_DATE} will be used instead.
     * </p>
     *
     * @param pattern The custom {@link String} pattern to use for the date values formatting
     * @return the current {@code CobolMapper} instance with the custom formatter
     */
    public CobolMapper withDateFormatter(String pattern) {
        this.customDateFormatter = DateTimeFormatter.ofPattern(pattern);
        return this;
    }

    /**
     * <p>Sets a custom {@link Integer} size for delimiters.</p>
     * <p>
     * This size will be used to determine the offset while cycling through the elements.
     * <p>
     * If not set, or a {@code null} value is passed, the default {@code 1} will be used instead.
     * </p>
     *
     * @param delimiterSize The custom {@link Integer} size of delimiters present between elements
     * @return the current {@code CobolMapper} instance with the custom delimiterSize
     */
    public CobolMapper withDelimiterSize(Integer delimiterSize) {
        if (delimiterSize != null)
            this.delimiterSize = delimiterSize;
        return this;
    }

    private Object getValue(String cobolInput, Field field, int offset) {
        var annotation = field.getAnnotation(CobolSegment.class);

        //LIST
        if(List.class.isAssignableFrom(field.getType())) {
            checkBasicParamsAnnotation(annotation.startPos(), annotation.length(), field);
            checkListAnnotation(annotation.listLength(), field);
            return getList(cobolInput, annotation, field, offset);
        //OBJECT
        } else if(!isPrimitive(field.getGenericType())) {
            return getObject(cobolInput, field.getType(), offset);
        }

        //PRIMITIVE
        checkBasicParamsAnnotation(annotation.startPos(), annotation.length(), field);
        int start = offset > 0 ? annotation.startPos() + offset : annotation.startPos();
        int end = start + annotation.length();
        return getSegment(cobolInput, start, end, field.getGenericType());
    }

    private Object getSegment(String cobolInput, int start, int end, Type type) {
        var segment = cobolInput.substring(start, end).strip();

        return parseValue(segment, type);
    }

    @SneakyThrows
    private Object getObject(String cobolInput, Class<?> clazz, int offset) {
        Object valueObject = getInstance(clazz);
        for(Field childField : clazz.getDeclaredFields()) {
            childField.setAccessible(true);

            Object value = getValue(cobolInput, childField, offset);
            childField.set(valueObject, value);
        }

        return valueObject;
    }

    @SuppressWarnings("unchecked")
    private List<Object> getList(String cobolInput, CobolSegment annotation, Field field, int offset) {
        List<Object> list = (List<Object>) getInstance(ArrayList.class);

        Type listType = getListType(field);
        if(isPrimitive(listType)) {
            addSegmentToList(cobolInput, annotation, list, listType, offset);
        } else {
            addObjectToList(cobolInput, annotation, list, listType);
        }

        return list;
    }

    private void addObjectToList(String cobolInput, CobolSegment annotation, List<Object> list, Type listType) {
        int start = annotation.startPos();
        int length = annotation.length();
        int listLength = annotation.listLength();
        int listEnd = start + listLength;
        var classType = (Class<?>) listType;

        int offset = 0;
        while(start < listEnd) {
            Object segmentObject = getObjectForList(cobolInput, offset, classType);
            list.add(segmentObject);

            start += length + delimiterSize;
            offset += length + delimiterSize;
        }
    }

    @SneakyThrows
    private Object getObjectForList(String cobolInput, int offset, Class<?> clazz) {
        Object valueObject = getInstance(clazz);
        for(Field childField : clazz.getDeclaredFields()) {
            childField.setAccessible(true);

            Object value = getValue(cobolInput, childField, offset);
            childField.set(valueObject, value);
        }

        return valueObject;
    }

    private void addSegmentToList(String cobolInput, CobolSegment annotation, List<Object> list, Type listType, int offset) {
        int start = offset > 0 ? annotation.startPos() + offset : annotation.startPos();
        int length = annotation.length();
        int listEnd = start + annotation.listLength();

        while(start < listEnd) {
            int segmentEnd = start + length;
            Object segment = getSegment(cobolInput, start, segmentEnd, listType);
            list.add(segment);

            start += length + delimiterSize;
        }
    }

    private Object parseValue(String subString, Type valueType) {
        DateTimeFormatter dateTimeFormatter = customDateTimeFormatter != null ? customDateTimeFormatter : null;
        DateTimeFormatter dateFormatter = customDateFormatter != null ? customDateFormatter : null;

        var parser = TypeParser.getParser(valueType, dateTimeFormatter, dateFormatter);
        return parser.parse(subString);
    }

    private static <T> T getInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new CobolMapperException(CobolExceptionEnum.MISSING_CONSTRUCTOR.format(clazz.getName()));
        }
    }

    private static Type getListType(Field field) {
        return ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    private static boolean isPrimitive(Type type) {
        return type.getTypeName().startsWith("java.");
    }

    private static void checkListAnnotation(int listLength, Field field) {
        if(listLength == -1) {
            String completeFieldName = getCompleteFieldName(field);
            throw new CobolMapperException(MessageFormat.format(CobolExceptionEnum.LIST_LENGTH_REQUIRED.message(), completeFieldName));
        }
    }

    private static void checkBasicParamsAnnotation(int startPos, int length, Field field) {
        if(startPos == -1 || length == -1) {
            String completeFieldName = getCompleteFieldName(field);
            throw new CobolMapperException(MessageFormat.format(CobolExceptionEnum.BACIS_PARAMS_REQUIRED.message(), completeFieldName));
        }
    }

    private static String getCompleteFieldName(Field field) {
        return MessageFormat.format("{0}.{1}", field.getDeclaringClass().toString(), field.getName());
    }
}
