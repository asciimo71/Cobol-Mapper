package org.urusso.cobolmapper.mapper;

import lombok.SneakyThrows;
import org.urusso.cobolmapper.annotation.CobolSegment;
import org.urusso.cobolmapper.exception.CobolExceptionEnum;
import org.urusso.cobolmapper.exception.CobolMapperException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mapper<T> {
    private final Map<Field, ValueDescriptor> descriptors;

    public Mapper(Class<T> clazz) {
        descriptors = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            var isAnnotated = field.isAnnotationPresent(CobolSegment.class);

            if (isAnnotated) {
                ValueDescriptor valueDescriptor = ValueDescriptor.of(field);
                this.descriptors.put(field, valueDescriptor);
            }
        }
    }

    public T map(String input, T output) throws CobolMapperException {
        throw new RuntimeException("Method Not Implemented");
    }

    private static class ValueDescriptor {
        private final Field field;
        private final CobolSegment annotation;
        private final String completeFieldName;

        ValueDescriptor(CobolSegment annotation, Field field) {
            this.annotation = annotation;
            this.field = field;
            this.completeFieldName = MessageFormat.format("{0}.{1}", field.getDeclaringClass().toString(), field.getName());
        }

        protected Field getField() { return field; }
        protected CobolSegment getAnnotation() { return annotation; }

        public static ValueDescriptor of(Field field) {
            // add cobol annotation properties to field
            // create a field parser according to properties
            // return the VD object

            // inspect cobol annotations
            var annotation = field.getAnnotation(CobolSegment.class);

            //LIST
            if(List.class.isAssignableFrom(field.getType())) {
                return new ListValueDescriptor(annotation, field);
                //OBJECT
            } else if(!isPrimitive(field.getGenericType())) {
                return new ObjectValueDescriptor(annotation, field);
            }

            //PRIMITIVE
            checkBasicParamsAnnotation(annotation.startPos(), annotation.length(), field);
            int start = offset > 0 ? annotation.startPos() + offset : annotation.startPos();
            int end = start + annotation.length();
            return getSegment(cobolInput, start, end, field.getGenericType());

            return null;
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
        
        private static boolean isPrimitive(Type type) {
            return type.getTypeName().startsWith("java.");
        }

        protected void checkBasicParamsAnnotation() {
            var startPos = annotation.startPos();
            var length = annotation.length();

            if(startPos == -1 || length == -1) {
                throw new CobolMapperException(MessageFormat.format(CobolExceptionEnum.BACIS_PARAMS_REQUIRED.message(), getCompleteFieldName()));
            }
        }

        protected String getCompleteFieldName() {
            return completeFieldName;
        }

        private static class ListValueDescriptor extends ValueDescriptor {
            private final Type listType;
            private final int start;
            private final int length;
            private final int listLength;
            private final int listEnd =;
            private final Class<?> classType;

            public ListValueDescriptor(CobolSegment annotation, Field field) {
                super(annotation, field);
                checkBasicParamsAnnotation();
                checkListAnnotation();

                this.start = annotation.startPos();
                this.length = annotation.length();
                this.listLength = annotation.listLength();
                this.listEnd = start + listLength;
                this.listType = getListType();
                this.classType = (Class<?>)listType;
            }

            private void checkListAnnotation() {
                if(getAnnotation().listLength() == -1) {
                    throw new CobolMapperException(MessageFormat.format(CobolExceptionEnum.LIST_LENGTH_REQUIRED.message(), getCompleteFieldName()));
                }
            }

            private Type getListType() {
                return switch(getField().getGenericType()) {
                    case ParameterizedType p -> p.getActualTypeArguments()[0];
                    default -> throw new IllegalStateException("Unexpected value: " + getField().getGenericType());
                };
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

        }

        private static class ObjectValueDescriptor extends ValueDescriptor {
            public ObjectValueDescriptor(CobolSegment annotation, Field field) {
                super(annotation, field);
                checkBasicParamsAnnotation();
            }
        }
    }
}
