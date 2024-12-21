package org.urusso.cobolmapper.model;

import lombok.Data;
import org.urusso.cobolmapper.annotation.CobolSegment;

import java.util.List;

@Data
public class Fruit {
    @CobolSegment(start = 22, end = 32)
    private String name;
    @CobolSegment(start = 33, end = 54, listElementSize = 10)
    private List<String> possibleColors;
}
