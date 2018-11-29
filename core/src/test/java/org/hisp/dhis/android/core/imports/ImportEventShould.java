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
public class ImportEventShould extends BaseObjectShould implements ObjectShould {

    public ImportEventShould() {
        super("imports/import_event.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ImportEvent importEvent = objectMapper.readValue(jsonStream, ImportEvent.class);

        assertThat(importEvent.imported()).isEqualTo(1);
        assertThat(importEvent.updated()).isEqualTo(2);
        assertThat(importEvent.deleted()).isEqualTo(3);
        assertThat(importEvent.ignored()).isEqualTo(4);

        assertThat(importEvent.importStatus()).isEqualTo(ImportStatus.SUCCESS);
        assertThat(importEvent.responseType()).isEqualTo("ImportSummaries");
        assertThat(importEvent.importSummaries()).isNotNull();
        assertThat(importEvent.importSummaries().size()).isEqualTo(2);

        ImportSummary importSummary = importEvent.importSummaries().get(0);
        assertThat(importSummary).isNotNull();
        assertThat(importSummary.reference()).isEqualTo("xqpUvfxT4PZ");
        assertThat(importSummary.responseType()).isEqualTo("ImportSummary");
        assertThat(importSummary.importStatus()).isEqualTo(ImportStatus.SUCCESS);
        assertThat(importSummary.importCount()).isNotNull();

    }
}
