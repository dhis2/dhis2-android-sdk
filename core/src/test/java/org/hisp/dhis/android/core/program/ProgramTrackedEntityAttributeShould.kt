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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.common.ValueTypeDeviceRendering;
import org.hisp.dhis.android.core.common.ValueTypeRenderingType;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class ProgramTrackedEntityAttributeShould extends BaseObjectShould implements ObjectShould {

    private static ValueTypeDeviceRendering desktopRendering = ValueTypeDeviceRendering.builder()
            .type(ValueTypeRenderingType.VERTICAL_RADIOBUTTONS).min(0).max(10).step(1).decimalPoints(0).build();

    private static ValueTypeDeviceRendering mobileRendering = ValueTypeDeviceRendering.builder()
            .type(ValueTypeRenderingType.SHARED_HEADER_RADIOBUTTONS).min(3).max(15).step(2).decimalPoints(1).build();

    public ProgramTrackedEntityAttributeShould() {
        super("program/program_tracked_entity_attribute.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ProgramTrackedEntityAttribute programTrackedEntityAttribute = objectMapper.readValue(jsonStream,
                ProgramTrackedEntityAttribute.class);

        assertThat(programTrackedEntityAttribute.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-10-11T10:41:40.401"));
        assertThat(programTrackedEntityAttribute.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-10-11T10:41:40.401"));
        assertThat(programTrackedEntityAttribute.uid()).isEqualTo("YhqgQ6Iy4c4");

        assertThat(programTrackedEntityAttribute.name()).isEqualTo("Child Programme Gender");
        assertThat(programTrackedEntityAttribute.displayName()).isEqualTo("Child Programme Gender");
        assertThat(programTrackedEntityAttribute.shortName()).isEqualTo("Child Programme Gender");
        assertThat(programTrackedEntityAttribute.displayShortName()).isEqualTo("Child Programme Gender");

        assertThat(programTrackedEntityAttribute.mandatory()).isFalse();
        assertThat(programTrackedEntityAttribute.trackedEntityAttribute().uid()).isEqualTo("cejWyOfXge6");
        assertThat(programTrackedEntityAttribute.program().uid()).isEqualTo("IpHINAT79UW");
        assertThat(programTrackedEntityAttribute.allowFutureDate()).isFalse();
        assertThat(programTrackedEntityAttribute.displayInList()).isFalse();
        assertThat(programTrackedEntityAttribute.sortOrder()).isEqualTo(1);
        assertThat(programTrackedEntityAttribute.renderType().desktop()).isEqualTo(desktopRendering);
        assertThat(programTrackedEntityAttribute.renderType().mobile()).isEqualTo(mobileRendering);
    }
}