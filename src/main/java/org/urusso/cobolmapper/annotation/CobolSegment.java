package org.urusso.cobolmapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CobolSegment {
    int startPos() default -1;
    int length() default -1;
    int listLength() default -1;
}
