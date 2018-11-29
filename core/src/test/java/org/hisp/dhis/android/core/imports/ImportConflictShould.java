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
public class ImportConflictShould extends BaseObjectShould implements ObjectShould {

    public ImportConflictShould() {
        super("imports/import_conflict.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ImportConflict importConflict = objectMapper.readValue(jsonStream, ImportConflict.class);

        assertThat(importConflict.object()).isEqualTo("Attribute.attribute");
        assertThat(importConflict.value()).isEqualTo("Invalid attribute zDhUuAYrxNCx1");
    }
}
