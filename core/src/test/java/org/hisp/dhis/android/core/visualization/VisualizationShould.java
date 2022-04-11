/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.visualization;

import static com.google.common.truth.Truth.assertThat;

import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.visualization.VisualizationSamples;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

public class VisualizationShould extends BaseObjectShould implements ObjectShould {

    public VisualizationShould() {
        super("visualization/visualization.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        Visualization visualization = objectMapper.readValue(jsonStream, Visualization.class);

        assertThat(visualization.uid()).isEqualTo("PYBH8ZaAQnC");
        assertThat(visualization.type()).isEqualTo(VisualizationType.PIVOT_TABLE);
        assertThat(visualization.digitGroupSeparator()).isEqualTo(DigitGroupSeparator.COMMA);

        assertThat(visualization.dataDimensionItems().get(0).indicator().uid()).isEqualTo("Uvn6LCg7dVU");
        assertThat(visualization.dataDimensionItems().get(1).dataElement().uid()).isEqualTo("cYeuwXTCPkU");
        assertThat(visualization.dataDimensionItems().get(2).dataElementOperand().uid())
                .isEqualTo("Jtf34kNZhzP.pq2XI5kz2BY");
        assertThat(visualization.dataDimensionItems().get(3).programIndicator().uid()).isEqualTo("p2Zxg0wcPQ3");
        assertThat(visualization.dataDimensionItems().get(4).programDataElement().uid())
                .isEqualTo("lxAQ7Zs9VYR.sWoqcoByYmD");
        assertThat(visualization.dataDimensionItems().get(4).programDataElement().program().uid())
                .isEqualTo("lxAQ7Zs9VYR");
        assertThat(visualization.dataDimensionItems().get(4).programDataElement().dataElement().uid())
                .isEqualTo("sWoqcoByYmD");
        assertThat(visualization.dataDimensionItems().get(5).programAttribute().uid())
                .isEqualTo("U5KybNCtA3E.iggSfNDnsCw");
        assertThat(visualization.dataDimensionItems().get(5).programAttribute().program().uid())
                .isEqualTo("U5KybNCtA3E");
        assertThat(visualization.dataDimensionItems().get(5).programAttribute().attribute().uid())
                .isEqualTo("iggSfNDnsCw");
    }
}