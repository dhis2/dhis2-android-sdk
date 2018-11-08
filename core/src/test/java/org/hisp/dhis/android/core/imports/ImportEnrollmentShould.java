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
public class ImportEnrollmentShould extends BaseObjectShould implements ObjectShould {

    public ImportEnrollmentShould() {
        super("imports/import_enrollment.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ImportEnrollment importEnrollment = objectMapper.readValue(jsonStream, ImportEnrollment.class);

        assertThat(importEnrollment.imported()).isEqualTo(0);
        assertThat(importEnrollment.updated()).isEqualTo(1);
        assertThat(importEnrollment.ignored()).isEqualTo(0);
        assertThat(importEnrollment.deleted()).isEqualTo(0);

        assertThat(importEnrollment.responseType()).isEqualTo("ImportSummaries");
        assertThat(importEnrollment.importStatus()).isEqualTo(ImportStatus.SUCCESS);
        assertThat(importEnrollment.importSummaries()).isNotNull();

        assertThat(importEnrollment.importSummaries()).size().isEqualTo(1);

        ImportSummary importSummary = importEnrollment.importSummaries().get(0);

        assertThat(importSummary).isNotNull();
        assertThat(importSummary.importEvent()).isNotNull();
        assertThat(importSummary.reference()).isEqualTo("XaBZwKbHVxS");
    }
}
