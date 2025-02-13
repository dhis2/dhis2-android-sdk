/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity.search;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class SearchGridMapperShould extends BaseObjectShould {

    public SearchGridMapperShould() {
        super("trackedentity/search_grid.json");
    }

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        SearchGrid searchGrid = objectMapper.readValue(jsonStream, SearchGrid.class);
        SearchGridMapper mapper = new SearchGridMapper();

        List<TrackedEntityInstance> teis = mapper.transform(searchGrid);

        assertThat(teis.size()).isEqualTo(2);

        TrackedEntityInstance tei1 = teis.get(0);
        assertThat(tei1.uid()).isEqualTo("PmslDkqLqeG");
        assertThat(tei1.created()).isEqualTo(BaseIdentifiableObject.parseSpaceDate("2018-04-27 17:34:17.005"));
        assertThat(tei1.lastUpdated()).isEqualTo(BaseIdentifiableObject.parseSpaceDate("2018-04-27 17:34:28.442"));
        assertThat(tei1.organisationUnit()).isEqualTo("DiszpKrYNg8");
        assertThat(tei1.trackedEntityType()).isEqualTo("nEenWmSyUEp");

        TrackedEntityAttributeValue attValue1A = tei1.trackedEntityAttributeValues().get(0);
        TrackedEntityAttributeValue attValue1B = tei1.trackedEntityAttributeValues().get(1);
        assertThat(attValue1A.value()).isEqualTo("Firsty");
        assertThat(attValue1B.value()).isEqualTo("Namey");

        TrackedEntityInstance tei2 = teis.get(1);
        assertThat(tei2.uid()).isEqualTo("Lf9FhXshRnd");
        assertThat(tei2.created()).isEqualTo(BaseIdentifiableObject.parseSpaceDate("2018-04-26 06:10:51.634"));
        assertThat(tei2.lastUpdated()).isEqualTo(BaseIdentifiableObject.parseSpaceDate("2018-04-26 06:10:52.944"));
        assertThat(tei2.organisationUnit()).isEqualTo("DiszpKrYNg8");
        assertThat(tei2.trackedEntityType()).isEqualTo("nEenWmSyUEp");

        TrackedEntityAttributeValue attValue2A = tei2.trackedEntityAttributeValues().get(0);
        TrackedEntityAttributeValue attValue2B = tei2.trackedEntityAttributeValues().get(1);
        assertThat(attValue2A.value()).isEqualTo("Jorge");
        assertThat(attValue2B.value()).isEqualTo("Fernandez");
    }
}