package org.urusso.cobolmapper.exception;

import java.util.Arrays;

public enum CobolExceptionEnum {
    LIST_LENGTH_REQUIRED("@CobolSegment: listLength param required for {0}"),
    BACIS_PARAMS_REQUIRED("@CobolSegment: startPos and length params are required for {0}"),
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
