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

package org.hisp.dhis.android.core.data.program;

import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;

import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillNameableProperties;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.parseDate;

public class ProgramTrackedEntityAttributeSamples {

    public static ProgramTrackedEntityAttribute getProgramTrackedEntityAttribute() {
        ProgramTrackedEntityAttribute.Builder builder = ProgramTrackedEntityAttribute.builder();
        fillNameableProperties(builder);
        return builder
                .id(1L)
                .mandatory(Boolean.TRUE)
                .trackedEntityAttribute(ObjectWithUid.create("tracked_entity_attribute"))
                .allowFutureDate(Boolean.FALSE)
                .displayInList(Boolean.FALSE)
                .program(ObjectWithUid.create("program"))
                .sortOrder(1)
                .searchable(Boolean.TRUE).build();
    }

    public static ProgramTrackedEntityAttribute getChildProgrammeGender() {
        return ProgramTrackedEntityAttribute.builder()
                .id(1L)
                .created(parseDate("2016-10-11T10:41:40.401"))
                .lastUpdated(parseDate("2016-10-11T10:41:40.401"))
                .uid("YhqgQ6Iy4c4")
                .name("Child Programme Gender")
                .shortName("Child Programme Gender")
                .displayName("Child Programme Gender")
                .mandatory(true)
                .displayShortName("Child Programme Gender")
                .allowFutureDate(true)
                .displayInList(true)
                .sortOrder(1)
                .searchable(false)
                .program(ObjectWithUid.create("lxAQ7Zs9VYR"))
                .trackedEntityAttribute(ObjectWithUid.create("cejWyOfXge6"))
                .build();
    }
}