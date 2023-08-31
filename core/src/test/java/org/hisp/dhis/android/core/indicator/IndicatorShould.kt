/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core.indicator

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.BaseObjectShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.junit.Test
import java.io.IOException
import java.text.ParseException

class IndicatorShould : BaseObjectShould("indicators/indicator.json"), ObjectShould {
    @Test
    @Throws(IOException::class, ParseException::class)
    override fun map_from_json_string() {
        val indicator = objectMapper.readValue(jsonStream, Indicator::class.java)
        Truth.assertThat(indicator.code()).isEqualTo("IN_52462")
        Truth.assertThat(indicator.lastUpdated()).isEqualTo(
            BaseIdentifiableObject.DATE_FORMAT.parse("2013-03-21T11:17:44.926"),
        )
        Truth.assertThat(indicator.uid()).isEqualTo("ReUHfIn0pTQ")
        Truth.assertThat(indicator.created()).isEqualTo(
            BaseIdentifiableObject.DATE_FORMAT.parse("2012-11-05T09:16:29.054"),
        )
        Truth.assertThat(indicator.name()).isEqualTo("ANC 1-3 Dropout Rate")
        Truth.assertThat(indicator.shortName()).isEqualTo("ANC 1-3 Dropout Rate")
        Truth.assertThat(indicator.description()).isEqualTo(
            "Indicates the percentage of clients dropping" +
                " out between the 1st and the 3rd ANC visit. Calculated as the difference between" +
                " ANC1 and ANC3 by the ANC 1 visits.",
        )
        Truth.assertThat(indicator.deleted()).isNull()
        Truth.assertThat(indicator.annualized()).isFalse()
        Truth.assertThat(indicator.numerator()).isEqualTo(
            "#{fbfJHSPpUQD.pq2XI5kz2BY}+#" +
                "{fbfJHSPpUQD.PT59n8BQbqM}-#{Jtf34kNZhzP.pq2XI5kz2BY}-#{Jtf34kNZhzP.PT59n8BQbqM}",
        )
        Truth.assertThat(indicator.numeratorDescription()).isEqualTo("ANC1-ANC3")
        Truth.assertThat(indicator.denominator())
            .isEqualTo("#{fbfJHSPpUQD.pq2XI5kz2BY}+#{fbfJHSPpUQD.PT59n8BQbqM}")
        Truth.assertThat(indicator.denominatorDescription()).isEqualTo("Total 1st ANC visits")
        Truth.assertThat(indicator.url()).isEqualTo("")
        Truth.assertThat(indicator.indicatorType()).isEqualTo(ObjectWithUid.create("bWuNrMHEoZ0"))
        Truth.assertThat(indicator.decimals()).isEqualTo(3)
        Truth.assertThat(indicator.style()).isEqualTo(ObjectStyle.builder().color("#FF0000").icon("circle").build())
    }
}
