package org.urusso.cobolmapper.model;

import lombok.Data;
import org.urusso.cobolmapper.annotation.CobolSegment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SampleDto {
    @CobolSegment
    private Person person;

    @CobolSegment(start = 22, end = 87, listElementSize = 32)
    private List<Fruit> fruitList;

    @CobolSegment(start = 88, end = 89)
    private Integer integerValue;
    @CobolSegment(start = 88, end = 89)
    private Long longValue;
    @CobolSegment(start = 88, end = 89)
    private Double doubleValue;
    @CobolSegment(start = 88, end = 89)
    private Float floatValue;
    @CobolSegment(start = 88, end = 89)
    private Boolean booleanNumberValue;
    @CobolSegment(start = 90, end = 91)
    private Character characterValue;
    @CobolSegment(start = 92, end = 97)
    private Boolean booleanValue;
    @CobolSegment(start = 98, end = 117)
    private LocalDateTime dateTimeValue;
    @CobolSegment(start = 118, end = 128)
    private LocalDate dateValue;
}
