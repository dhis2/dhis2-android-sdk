/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
