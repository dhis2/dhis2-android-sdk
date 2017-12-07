package org.hisp.dhis.android.core.imports;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class ImportConflictShould {

    @Test
    public void map_from_json_string() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        ImportConflict importConflict = objectMapper.readValue("{\n" +
                "        \"object\": \"Attribute.attribute\",\n" +
                "        \"value\": \"Invalid attribute zDhUuAYrxNCx1\"\n" +
                "      }", ImportConflict.class);

        assertThat(importConflict.object()).isEqualTo("Attribute.attribute");
        assertThat(importConflict.value()).isEqualTo("Invalid attribute zDhUuAYrxNCx1");
    }
}
