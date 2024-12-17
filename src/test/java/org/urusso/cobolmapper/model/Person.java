package org.urusso.cobolmapper.model;

import lombok.Data;
import org.urusso.cobolmapper.annotation.CobolSegment;

import java.util.List;

@Data
public class Person {
    @CobolSegment(start = 0, end = 14)
    private String name;
    @CobolSegment(start = 16, end = 30)
    private String surname;
    @CobolSegment(start = 38, end = 58, listElementSize = 14)
    private List<State> state;
}
