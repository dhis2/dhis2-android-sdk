package org.hisp.dhis.android.core.imports;

import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class ImportCountShould extends BaseObjectShould implements ObjectShould {

    public ImportCountShould() {
        super("imports/import_count.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ImportCount importCount = objectMapper.readValue(jsonStream, ImportCount.class);

        assertThat(importCount.imported()).isEqualTo(0);
        assertThat(importCount.updated()).isEqualTo(0);
        assertThat(importCount.ignored()).isEqualTo(1);
        assertThat(importCount.deleted()).isEqualTo(0);
    }
}
