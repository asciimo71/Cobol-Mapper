package org.urusso.cobolmapper;

import org.junit.jupiter.api.Test;
import org.urusso.cobolmapper.model.SampleDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CobolMapperTest extends BaseMockitoTest {
    @Test
    public void test() {
        var copyCobol = "niccolò        ;barella        ;1;2;3;madrid    ;1;2;paris     ;3;4";

        var response = new CobolMapper().map(copyCobol, SampleDto.class);
        assertNotNull(response);
    }
}
