package org.urusso.cobolmapper.model;

import lombok.Data;
import org.urusso.cobolmapper.annotation.CobolSegment;

@Data
public class Person {
    @CobolSegment(startPos = 0, length = 10)
    private String name;
    @CobolSegment(startPos = 11, length = 10)
    private String surname;
}
