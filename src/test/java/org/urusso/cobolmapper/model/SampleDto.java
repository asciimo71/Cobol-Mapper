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

    @CobolSegment(startPos = 22, length = 32, listLength = 65)
    private List<Fruit> fruitList;

    @CobolSegment(startPos = 88, length = 1)
    private Integer integerValue;
    @CobolSegment(startPos = 88, length = 1)
    private Long longValue;
    @CobolSegment(startPos = 88, length = 1)
    private Double doubleValue;
    @CobolSegment(startPos = 88, length = 1)
    private Float floatValue;
    @CobolSegment(startPos = 88, length = 1)
    private Boolean booleanNumberValue;
    @CobolSegment(startPos = 90, length = 1)
    private Character characterValue;
    @CobolSegment(startPos = 92, length = 5)
    private Boolean booleanValue;
    @CobolSegment(startPos = 98, length = 19)
    private LocalDateTime dateTimeValue;
    @CobolSegment(startPos = 118, length = 10)
    private LocalDate dateValue;
}
