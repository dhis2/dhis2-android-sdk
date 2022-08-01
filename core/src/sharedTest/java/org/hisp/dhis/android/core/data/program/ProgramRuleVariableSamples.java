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
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramRuleVariableSourceType;

import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillIdentifiableProperties;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.parseDate;

public class ProgramRuleVariableSamples {

    public static ProgramRuleVariable getProgramRuleVariable() {
        ProgramRuleVariable.Builder builder = ProgramRuleVariable.builder();

        fillIdentifiableProperties(builder);
        builder
                .id(1L)
                .useCodeForOptionSet(true)
                .program(ObjectWithUid.create("program"))
                .programStage(ObjectWithUid.create("program_stage"))
                .dataElement(ObjectWithUid.create("data_element"))
                .trackedEntityAttribute(ObjectWithUid.create("tracked_entity_attribute"))
                .programRuleVariableSourceType(ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM);
        return builder.build();
    }

    public static ProgramRuleVariable getHemoglobin() {
        return ProgramRuleVariable.builder()
                .id(1L)
                .uid("omrL0gtPpDL")
                .created(parseDate("2016-04-12T15:57:18.645"))
                .lastUpdated(parseDate("2017-05-23T00:29:24.356"))
                .name("hemoglobin")
                .displayName("hemoglobin")
                .programRuleVariableSourceType(ProgramRuleVariableSourceType.DATAELEMENT_NEWEST_EVENT_PROGRAM)
                .program(ObjectWithUid.create("lxAQ7Zs9VYR"))
                .dataElement(ObjectWithUid.create("vANAXwtLwcT"))
                .build();
    }
}