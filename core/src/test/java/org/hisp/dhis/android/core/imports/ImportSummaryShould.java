package org.hisp.dhis.android.core.imports;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class ImportSummaryShould {

    @Test
    public void map_from_json_string_with_tei_conflicts() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        String importSummaryStr = new ResourcesFileReader().getStringFromFile(
                "imports/import_summary_with_tei_conflicts.json");
        ImportSummary importSummary = objectMapper.readValue(importSummaryStr, ImportSummary.class);

        assertThat(importSummary.importStatus()).isEqualTo(ImportStatus.ERROR);
        assertThat(importSummary.responseType()).isEqualTo("ImportSummary");
        assertThat(importSummary.importCount()).isNotNull();
        assertThat(importSummary.importCount().imported()).isEqualTo(0);
        assertThat(importSummary.importCount().updated()).isEqualTo(0);
        assertThat(importSummary.importCount().ignored()).isEqualTo(1);
        assertThat(importSummary.importCount().deleted()).isEqualTo(0);

        assertThat(importSummary.importConflicts()).isNotNull();
        assertThat(importSummary.importConflicts().size()).isEqualTo(1);

        ImportConflict importConflict = importSummary.importConflicts().get(0);

        assertThat(importConflict).isNotNull();
        assertThat(importConflict.value()).isEqualTo("Value '201921212' is not a valid date for attribute iESIqZ0R0R0");
        assertThat(importConflict.object()).isEqualTo("Attribute.value");
    }

    @Test
    public void importSummary_shouldParseImportSummaryWithEnrollmentConflictsFromJson() throws Exception {
        //TODO Test
    }

    @Test
    public void map_from_json_string_with_event_conflicts() throws Exception {
        ObjectMapper objectMapper = Inject.objectMapper();

        String importSummaryStr = new ResourcesFileReader().getStringFromFile("imports/import_summary_with_event_conflicts.json");
        ImportSummary importSummary = objectMapper.readValue(importSummaryStr, ImportSummary.class);

        assertThat(importSummary.responseType()).isEqualTo("ImportSummary");
        assertThat(importSummary.importStatus()).isEqualTo(ImportStatus.SUCCESS);
        assertThat(importSummary.importCount()).isNotNull();

        assertThat(importSummary.importCount().imported()).isEqualTo(0);
        assertThat(importSummary.importCount().updated()).isEqualTo(1);
        assertThat(importSummary.importCount().ignored()).isEqualTo(0);
        assertThat(importSummary.importCount().deleted()).isEqualTo(0);

        assertThat(importSummary.reference()).isEqualTo("Rmp5T1vmZ74");

        assertThat(importSummary.importEvent()).isNull();
        assertThat(importSummary.importEnrollment()).isNotNull();


    }
}
