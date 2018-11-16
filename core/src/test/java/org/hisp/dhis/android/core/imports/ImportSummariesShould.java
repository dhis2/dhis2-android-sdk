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
public class ImportSummariesShould extends BaseObjectShould implements ObjectShould {

    public ImportSummariesShould() {
        super("imports/import_summaries.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ImportSummaries importSummaries = objectMapper.readValue(jsonStream, ImportSummaries.class);

        assertThat(importSummaries.responseType()).isEqualTo("ImportSummaries");
        assertThat(importSummaries.importStatus()).isEqualTo(ImportStatus.SUCCESS);
        assertThat(importSummaries.imported()).isEqualTo(1);
        assertThat(importSummaries.updated()).isEqualTo(2);
        assertThat(importSummaries.deleted()).isEqualTo(3);
        assertThat(importSummaries.ignored()).isEqualTo(4);
    }
}
