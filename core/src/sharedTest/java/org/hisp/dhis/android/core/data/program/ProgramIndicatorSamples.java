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

import org.hisp.dhis.android.core.common.AggregationType;
import org.hisp.dhis.android.core.common.AnalyticsType;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.program.ProgramIndicator;

import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillNameableProperties;
import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.parseDate;

public class ProgramIndicatorSamples {

    public static ProgramIndicator getProgramIndicator() {
        ProgramIndicator.Builder builder = ProgramIndicator.builder();


        fillNameableProperties(builder);
        builder
                .id(1L)
                .uid("test_program_indicator")
                .displayInForm(true)
                .expression("test_expression")
                .dimensionItem("test_dimension_item")
                .filter("test_filter")
                .decimals(3)
                .aggregationType(AggregationType.AVERAGE)
                .program(ObjectWithUid.create("test_program"));
        return builder.build();
    }

    public static ProgramIndicator getAgeAtVisit() {
        return ProgramIndicator.builder()
                .id(1L)
                .uid("GSae40Fyppf")
                .created(parseDate("2015-09-21T23:35:50.945"))
                .lastUpdated(parseDate("2015-09-21T23:47:57.820"))
                .name("Age at visit")
                .displayName("Age at visit")
                .shortName("Age")
                .displayShortName("Age")
                .aggregationType(AggregationType.AVERAGE)
                .displayInForm(true)
                .description("Age at visit")
                .displayDescription("Age at visit")
                .dimensionItem("GSae40Fyppf")
                .expression("d2:yearsBetween(A{iESIqZ0R0R0},V{event_date})")
                .program(ObjectWithUid.create("lxAQ7Zs9VYR"))
                .analyticsType(AnalyticsType.EVENT)
                .build();
    }
}