package org.urusso.cobolmapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.urusso.cobolmapper.exception.CobolMapperException;
import org.urusso.cobolmapper.model.NoConstructorDto;
import org.urusso.cobolmapper.model.SampleDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CobolMapperTest {
    @Test
    void mapTest() {
        var cobolInput = "mario     ;rossi     ;apple     ;red       ;green     ;banana    ;yellow    ;green     ;1;F;false;2024-12-21T14:50:05;2024-12-21;";

        var response = new CobolMapper()
                .withDateTimeFormatter("yyyy-MM-dd'T'HH:mm:ss").withDateFormatter("yyyy-MM-dd")
                .withDelimiterSize(1)
                .map(cobolInput, SampleDto.class);

        assertNotNull(response);

        assertNotNull(response.getPerson(), "person should not be null");
        assertEquals("mario", response.getPerson().getName(), "name should be mario");
        assertEquals("rossi", response.getPerson().getSurname(), "surname should be rossi");

        assertNotNull(response.getFruitList(), "fruitList should not be null");
        assertFalse(response.getFruitList().isEmpty(), "fruitList should not be empty");

        assertEquals(2, response.getFruitList().size(), "fruitList should contain 2 fruits");

        assertEquals("apple", response.getFruitList().get(0).getName(), "name should be apple");
        assertEquals(2, response.getFruitList().get(0).getPossibleColors().size(), "apple.possible colors should be size(2)");
        assertEquals("red", response.getFruitList().get(0).getPossibleColors().get(0), "apple.color(0) should be red");
        assertEquals("green", response.getFruitList().get(0).getPossibleColors().get(1), "apple.color(1) should be green");

        assertEquals("banana", response.getFruitList().get(1).getName(), "name should be banana");
        assertEquals(2, response.getFruitList().get(1).getPossibleColors().size(), "banana.possible colors should be size(2)");
        assertEquals("yellow", response.getFruitList().get(1).getPossibleColors().get(0), "banana.color(0) should be yellow");
        assertEquals("green", response.getFruitList().get(1).getPossibleColors().get(1), "banana.color(1) should be green");

        assertNotNull(response.getIntegerValue(), "integerValue should not be null");
        assertEquals(1, response.getIntegerValue(), "integer value should be 1");

        assertNotNull(response.getLongValue(), " longValue should not be null");
        assertEquals(1L, response.getLongValue(), "long value should be 1L");

        assertNotNull(response.getDoubleValue(), "doubleValue should not be null");
        assertEquals(1.0, response.getDoubleValue(), "double value should be 1.0");

        assertNotNull(response.getFloatValue(), "floatValue should not be null");
        assertEquals(1.0f, response.getFloatValue(), "float value should be 1.0f");

        assertNotNull(response.getBooleanNumberValue(), " booleanNumberValue should not be null");
        assertEquals(Boolean.TRUE, response.getBooleanNumberValue(), "booleanNumber value should be Boolean.TRUE");

        assertNotNull(response.getCharacterValue(), "characterValue should not be null");
        assertEquals('F', response.getCharacterValue(), "character value should be 'F'");

        assertNotNull(response.getBooleanValue(), "booleanValue should not be null");
        assertEquals(false, response.getBooleanValue(), "boolean value should be false");

        assertNotNull(response.getDateTimeValue(), "dateTimeValue should not be null");
        assertEquals(LocalDateTime.of(2024, Month.DECEMBER, 21, 14,50, 5), response.getDateTimeValue(), "dateTime value should be 2024-12-21T14:50:05");

        assertNotNull(response.getDateValue(), "dateValue should not be null");
        assertEquals(LocalDate.of(2024, Month.DECEMBER, 21), response.getDateValue(), "date value should be 2024-12-21");
    }

    @Test
    void noDefaultConstructorExceptionTest() {
        var cobolInput = "mario     ";

        var mapper = new CobolMapper();
        assertThrows(CobolMapperException.class, () -> mapper.map(cobolInput, NoConstructorDto.class));
    }
}
