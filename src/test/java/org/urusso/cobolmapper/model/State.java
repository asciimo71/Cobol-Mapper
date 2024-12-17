package org.urusso.cobolmapper.model;

import lombok.Data;
import org.urusso.cobolmapper.annotation.CobolSegment;

import java.util.List;

@Data
public class State {
    @CobolSegment(start = 38, end = 48)
    private String name;
    @CobolSegment(start = 49, end = 52, listElementSize = 1)
    private List<Integer> stateNumber;
}
