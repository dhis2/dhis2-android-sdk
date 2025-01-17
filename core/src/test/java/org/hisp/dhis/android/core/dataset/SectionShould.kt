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

package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class SectionShould extends BaseObjectShould implements ObjectShould {

    public SectionShould() {
        super("dataset/section.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        Section section = objectMapper.readValue(jsonStream, Section.class);

        assertThat(section.uid()).isEqualTo("Y2rk0vzgvAx");
        assertThat(section.code()).isEqualTo("Code123");
        assertThat(section.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.parseDate("2016-10-12T13:22:42.731"));
        assertThat(section.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2012-04-26T19:26:02.933"));
        assertThat(section.name()).isEqualTo("Immunization");
        assertThat(section.displayName()).isEqualTo("Immunization");

        assertThat(section.description()).isEqualTo("Immunization dose administration");
        assertThat(section.dataSet().uid()).isEqualTo("BfMAe6Itzgt");
        assertThat(section.sortOrder()).isEqualTo(2);
        assertThat(section.showRowTotals()).isFalse();
        assertThat(section.showColumnTotals()).isFalse();
        assertThat(section.dataElements().size()).isEqualTo(15);
        assertThat(section.dataElements().get(0).uid()).isEqualTo("s46m5MS0hxu");
        assertThat(section.greyedFields().size()).isEqualTo(1);
        assertThat(section.greyedFields().get(0).uid()).isEqualTo("ca8lfO062zg.Prlt0C1RF0s");
    }
}