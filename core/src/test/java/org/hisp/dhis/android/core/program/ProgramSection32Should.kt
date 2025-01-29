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

package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class ProgramSection32Should extends BaseObjectShould implements ObjectShould {

    public ProgramSection32Should() {
        super("program/program_section_32_and_previous.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ProgramSection programSection = objectMapper.readValue(jsonStream, ProgramSection.class);

        assertThat(programSection.uid()).isEqualTo("Nc8OxbNuVH3");
        assertThat(programSection.code()).isEqualTo("Code");
        assertThat(programSection.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.parseDate("2018-05-15T08:14:06.767"));
        assertThat(programSection.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2018-05-15T08:14:06.767"));
        assertThat(programSection.name()).isEqualTo("My Program Section");
        assertThat(programSection.displayName()).isEqualTo("My Program Section");

        assertThat(programSection.description()).isEqualTo("Description");
        assertThat(UidsHelper.getUidOrNull(programSection.program())).isEqualTo("IpHINAT79UW");
        assertThat(programSection.attributes().get(0).uid()).isEqualTo("zDhUuAYrxNC");
        assertThat(programSection.sortOrder()).isEqualTo(1);
        assertThat(programSection.formName()).isEqualTo("formName");
        assertThat(programSection.style()).isEqualTo(ObjectStyle.builder().color("#fff").icon("my-icon").build());
    }
}