package org.urusso.cobolmapper.exception;

import java.util.Arrays;

public enum CobolExceptionEnum {
    SIZE_REQUIRED("@CobolSegment: listElementSize param required for lists"),
    MISSING_CONSTRUCTOR("%s needs a default constructor");

    private final String message;

    CobolExceptionEnum(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public String format(String ... args) {
        return String.format(this.message, Arrays.stream(args).toArray());
    }
}
