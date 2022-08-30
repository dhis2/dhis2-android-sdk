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

package org.hisp.dhis.android.core.data.legendset;

import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.legendset.Legend;

import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillIdentifiableProperties;

public class LegendSamples {

    public static Legend getLegend() {
        Legend.Builder legendBuilder = Legend.builder();

        fillIdentifiableProperties(legendBuilder);
        legendBuilder
                .id(1L)
                .startValue(30.5)
                .endValue(40.0)
                .color("#d9f0a3")
                .legendSet(ObjectWithUid.create("legend_set_uid"));
        return legendBuilder.build();
    }

    public static Legend get45To60() {
        return Legend.builder()
                .id(1L)
                .uid("BzQkRWHS7lu")
                .name("45 - 60")
                .startValue(45.0)
                .endValue(60.0)
                .color("#F38026")
                .legendSet(ObjectWithUid.create("TiOkbpGEud4"))
                .build();
    }
}