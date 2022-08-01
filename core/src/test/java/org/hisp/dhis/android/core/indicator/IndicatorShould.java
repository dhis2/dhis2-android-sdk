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

package org.hisp.dhis.android.core.indicator;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class IndicatorShould extends BaseObjectShould implements ObjectShould {

    public IndicatorShould() {
        super("indicators/indicator.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        Indicator indicator = objectMapper.readValue(jsonStream, Indicator.class);

        assertThat(indicator.code()).isEqualTo("IN_52462");
        assertThat(indicator.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2013-03-21T11:17:44.926"));
        assertThat(indicator.uid()).isEqualTo("ReUHfIn0pTQ");
        assertThat(indicator.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2012-11-05T09:16:29.054"));
        assertThat(indicator.name()).isEqualTo("ANC 1-3 Dropout Rate");
        assertThat(indicator.shortName()).isEqualTo("ANC 1-3 Dropout Rate");
        assertThat(indicator.description()).isEqualTo("Indicates the percentage of clients dropping" +
                " out between the 1st and the 3rd ANC visit. Calculated as the difference between" +
                " ANC1 and ANC3 by the ANC 1 visits.");
        assertThat(indicator.deleted()).isNull();

        assertThat(indicator.annualized()).isFalse();
        assertThat(indicator.numerator()).isEqualTo("#{fbfJHSPpUQD.pq2XI5kz2BY}+#" +
                "{fbfJHSPpUQD.PT59n8BQbqM}-#{Jtf34kNZhzP.pq2XI5kz2BY}-#{Jtf34kNZhzP.PT59n8BQbqM}");
        assertThat(indicator.numeratorDescription()).isEqualTo("ANC1-ANC3");
        assertThat(indicator.denominator())
                .isEqualTo("#{fbfJHSPpUQD.pq2XI5kz2BY}+#{fbfJHSPpUQD.PT59n8BQbqM}");
        assertThat(indicator.denominatorDescription()).isEqualTo("Total 1st ANC visits");
        assertThat(indicator.url()).isEqualTo("");
        assertThat(indicator.indicatorType()).isEqualTo(ObjectWithUid.create("bWuNrMHEoZ0"));
        assertThat(indicator.decimals()).isEqualTo(3);
    }
}