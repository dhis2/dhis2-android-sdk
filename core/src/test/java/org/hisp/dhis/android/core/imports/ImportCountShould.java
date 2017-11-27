package org.hisp.dhis.android.core.imports;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class ImportCountShould {

    @Test
    public void map_from_json_string() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();
        ImportCount importCount = objectMapper.readValue("{\n" +
                "      \"imported\": 0,\n" +
                "      \"updated\": 0,\n" +
                "      \"ignored\": 1,\n" +
                "      \"deleted\": 0\n" +
                "    }", ImportCount.class);

        assertThat(importCount.imported()).isEqualTo(0);
        assertThat(importCount.updated()).isEqualTo(0);
        assertThat(importCount.ignored()).isEqualTo(1);
        assertThat(importCount.deleted()).isEqualTo(0);
    }
}
