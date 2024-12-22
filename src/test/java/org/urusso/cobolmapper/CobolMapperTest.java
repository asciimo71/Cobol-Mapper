package org.urusso.cobolmapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.urusso.cobolmapper.exception.CobolMapperException;
import org.urusso.cobolmapper.model.NoConstructorDto;
import org.urusso.cobolmapper.model.SampleDto;

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

        assertNotNull(response.getPerson());
        assertNotNull(response.getPerson().getName());
        assertNotNull(response.getPerson().getSurname());

        assertNotNull(response.getFruitList());
        assertFalse(response.getFruitList().isEmpty());
        assertEquals(2, response.getFruitList().size());

        assertNotNull(response.getIntegerValue());
        assertNotNull(response.getLongValue());
        assertNotNull(response.getDoubleValue());
        assertNotNull(response.getFloatValue());
        assertNotNull(response.getBooleanNumberValue());
        assertNotNull(response.getCharacterValue());
        assertNotNull(response.getBooleanValue());
        assertNotNull(response.getDateTimeValue());
        assertNotNull(response.getDateValue());
    }

    @Test
    void noDefaultConstructorExceptionTest() {
        var cobolInput = "mario     ";

        var mapper = new CobolMapper();
        assertThrows(CobolMapperException.class, () -> mapper.map(cobolInput, NoConstructorDto.class));
    }
}
