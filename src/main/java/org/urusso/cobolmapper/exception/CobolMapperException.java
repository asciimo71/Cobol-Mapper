package org.urusso.cobolmapper.exception;

public class CobolMapperException extends RuntimeException {
    public CobolMapperException(CobolExceptionEnum cobolExceptionEnum) {
        super(cobolExceptionEnum.message());
    }

    public CobolMapperException(String message) {
        super(message);
    }
}
