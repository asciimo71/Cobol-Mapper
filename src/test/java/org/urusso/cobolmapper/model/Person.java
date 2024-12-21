package org.urusso.cobolmapper.model;

import lombok.Data;
import org.urusso.cobolmapper.annotation.CobolSegment;

@Data
public class Person {
    @CobolSegment(start = 0, end = 10)
    private String name;
    @CobolSegment(start = 11, end = 21)
    private String surname;
}
