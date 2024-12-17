package org.urusso.cobolmapper.model;

import lombok.Data;
import org.urusso.cobolmapper.annotation.CobolSegment;

import java.util.List;

@Data
public class SampleDto {
    @CobolSegment
    private Person person;
    @CobolSegment(start = 0, end = 14)
    private String name;
    @CobolSegment(start = 32, end = 37, listElementSize = 1)
    private List<Integer> numbers;
    @CobolSegment(start = 38, end = 58, listElementSize = 14)
    private List<State> state;
    @CobolSegment(start = 32, end = 33)
    private Character digit;
}
