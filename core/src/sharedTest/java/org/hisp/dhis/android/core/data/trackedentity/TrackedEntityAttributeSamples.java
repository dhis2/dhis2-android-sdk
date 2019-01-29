/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.data.trackedentity;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeSearchScope;

import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillNameableProperties;

public class TrackedEntityAttributeSamples {

    public static TrackedEntityAttribute get() {

        TrackedEntityAttribute.Builder builder = TrackedEntityAttribute.builder();
        fillNameableProperties(builder);

        return builder
                .pattern("pattern")
                .sortOrderInListNoProgram(1)
                .optionSet(OptionSet.builder().uid("option_set_uid").build())
                .valueType(ValueType.BOOLEAN)
                .expression("expression")
                .searchScope(TrackedEntityAttributeSearchScope.SEARCH_ORG_UNITS)
                .programScope(Boolean.TRUE)
                .displayInListNoProgram(Boolean.TRUE)
                .generated(Boolean.TRUE)
                .displayOnVisitSchedule(Boolean.TRUE)
                .orgUnitScope(Boolean.TRUE)
                .unique(Boolean.TRUE)
                .inherit(Boolean.TRUE)
                .build();
    }
}