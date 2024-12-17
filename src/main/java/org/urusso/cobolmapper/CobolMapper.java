package org.urusso.cobolmapper;

import lombok.SneakyThrows;
import org.urusso.cobolmapper.annotation.CobolSegment;
import org.urusso.cobolmapper.exception.CobolExceptionEnum;
import org.urusso.cobolmapper.exception.CobolMapperException;

import java.lang.reflect.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CobolMapper {
    private DateTimeFormatter customDateTimeFormatter;
    private DateTimeFormatter customDateFormatter;

    @SneakyThrows
    public <T> T map(String cobolString, Class<T> clazz) {
        T rootObject = getInstance(clazz);

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            var isAnnotated = field.isAnnotationPresent(CobolSegment.class);

            if (isAnnotated) {
                Object value = getValue(cobolString, field, 0);
                field.set(rootObject, value);
            }
        }

        return rootObject;
    }

    public CobolMapper withDateTimeFormatter(DateTimeFormatter formatter) {
        this.customDateTimeFormatter = formatter;
        return this;
    }

    public CobolMapper withDateFormatter(DateTimeFormatter formatter) {
        this.customDateFormatter = formatter;
        return this;
    }

    private Object getValue(String cobolString, Field field, int offset) {
        var annotation = field.getAnnotation(CobolSegment.class);

        //LIST
        if(List.class.isAssignableFrom(field.getType())) {
            return getList(cobolString, annotation, field, offset);
        //OBJECT
        } else if(!isPrimitive(field.getGenericType())) {
            return getObject(cobolString, field.getType(), offset);
        }

        //PRIMITIVE
        int start = offset > 0 ? annotation.start() + offset :annotation.start();
        int end = offset > 0 ? annotation.end() + offset :annotation.end();
        return getSegment(cobolString, start, end, field.getGenericType());
    }

    private Object getSegment(String cobolString, int start, int end, Type type) {
        var segment = cobolString.substring(start, end).strip();
        return parseValue(segment, type);
    }

    @SneakyThrows
    private Object getObject(String cobolString, Class<?> clazz, int offset) {
        Object valueObject = getInstance(clazz);
        for(Field childField : clazz.getDeclaredFields()) {
            childField.setAccessible(true);

            Object value = getValue(cobolString, childField, offset);
            childField.set(valueObject, value);
        }

        return valueObject;
    }

    @SuppressWarnings("unchecked")
    private List<Object> getList(String cobolString, CobolSegment annotation, Field field, int offset) {
        List<Object> list = (List<Object>) getInstance(ArrayList.class);

        Type listType = getListType(field);
        if(isPrimitive(listType)) {
            addSegmentToList(cobolString, annotation, list, listType, offset);
        } else {
            addObjectToList(cobolString, annotation, list, listType);
        }

        return list;
    }

    private void addObjectToList(String cobolString, CobolSegment annotation, List<Object> list, Type listType) {
        int start = annotation.start();
        int end = annotation.end();
        int size = annotation.listElementSize();
        var classType = (Class<?>) listType;

        if(size == -1)
            throw new CobolMapperException(CobolExceptionEnum.SIZE_REQUIRED);

        int offset = 0;
        while(start < end) {
            Object segmentObject = getObjectForList(cobolString, offset, classType);
            list.add(segmentObject);

            //+1 for delimiters like ";"
            start += size + 1;
            offset += size + 1;
        }
    }

    @SneakyThrows
    private Object getObjectForList(String cobolString, int offset, Class<?> clazz) {
        Object valueObject = getInstance(clazz);
        for(Field childField : clazz.getDeclaredFields()) {
            childField.setAccessible(true);

            Object value = getValue(cobolString, childField, offset);
            childField.set(valueObject, value);
        }

        return valueObject;
    }

    private void addSegmentToList(String cobolString, CobolSegment annotation, List<Object> list, Type listType, int offset) {
        int start = offset > 0 ? annotation.start() + offset : annotation.start();
        int end = offset > 0 ? annotation.end() + offset : annotation.end();
        int size = annotation.listElementSize();

        if(size == -1)
            throw new CobolMapperException(CobolExceptionEnum.SIZE_REQUIRED);

        while(start < end) {
            int segmentEnd = start + size;
            Object segment = getSegment(cobolString, start, segmentEnd, listType);
            list.add(segment);

            start += size + 1; //+1 for delimiters like ";"
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
}
