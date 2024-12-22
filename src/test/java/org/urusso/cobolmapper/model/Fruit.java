package org.urusso.cobolmapper.model;

import lombok.Data;
import org.urusso.cobolmapper.annotation.CobolSegment;

import java.util.List;

@Data
public class Fruit {
    @CobolSegment(startPos = 22, length = 10)
    private String name;
    @CobolSegment(startPos = 33, length = 10, listLength = 21)
    private List<String> possibleColors;
}
