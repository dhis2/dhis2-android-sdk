/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.testapp.indicator

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class IndicatorCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val indicators = d2.indicatorModule().indicators().blockingGet()
        assertThat(indicators.size).isEqualTo(1)
    }

    @Test
    fun include_legend_sets_as_children() {
        val indicator = d2.indicatorModule().indicators()
            .withLegendSets()
            .one()
            .blockingGet()!!

        assertThat(indicator.legendSets()!!.size).isEqualTo(1)
        assertThat(indicator.legendSets()!![0].uid()).isEqualTo("rtOkbpGEud4")
    }

    @Test
    fun filter_by_annualized() {
        val indicators = d2.indicatorModule().indicators()
            .byAnnualized().isFalse
            .blockingGet()

        assertThat(indicators.size).isEqualTo(1)
    }

    @Test
    fun filter_by_indicator_type_url() {
        val indicators = d2.indicatorModule().indicators()
            .byIndicatorTypeUid().eq("bWuNrMHEoZ0")
            .blockingGet()

        assertThat(indicators.size).isEqualTo(1)
    }

    @Test
    fun filter_by_numerator() {
        val indicators = d2.indicatorModule().indicators()
            .byNumerator()
            .eq(
                "#{fbfJHSPpUQD.pq2XI5kz2BY}+#{fbfJHSPpUQD.PT59n8BQbqM}" +
                    "-#{Jtf34kNZhzP.pq2XI5kz2BY}-#{Jtf34kNZhzP.PT59n8BQbqM}",
            )
            .blockingGet()

        assertThat(indicators.size).isEqualTo(1)
    }

    @Test
    fun filter_by_numerator_description() {
        val indicators = d2.indicatorModule().indicators()
            .byNumeratorDescription().eq("ANC1-ANC3")
            .blockingGet()

        assertThat(indicators.size).isEqualTo(1)
    }

    @Test
    fun filter_by_denominator() {
        val indicators = d2.indicatorModule().indicators()
            .byDenominator().eq("#{fbfJHSPpUQD.pq2XI5kz2BY}+#{fbfJHSPpUQD.PT59n8BQbqM}")
            .blockingGet()

        assertThat(indicators.size).isEqualTo(1)
    }

    @Test
    fun filter_by_denominator_description() {
        val indicators = d2.indicatorModule().indicators()
            .byDenominatorDescription().eq("Total 1st ANC visits")
            .blockingGet()

        assertThat(indicators.size).isEqualTo(1)
    }

    @Test
    fun filter_by_url() {
        val indicators = d2.indicatorModule().indicators()
            .byUrl().eq("url")
            .blockingGet()

        assertThat(indicators.size).isEqualTo(1)
    }

    @Test
    fun filter_by_color() {
        val indicators = d2.indicatorModule().indicators()
            .byColor().eq("#FF0000")
            .blockingGet()

        assertThat(indicators.size).isEqualTo(1)
    }

    @Test
    fun filter_by_icon() {
        val indicators = d2.indicatorModule().indicators()
            .byIcon().eq("circle")
            .blockingGet()

        assertThat(indicators.size).isEqualTo(1)
    }

    @Test
    fun filter_by_dataSetUid() {
        val indicators = d2.indicatorModule().indicators()
            .byDataSetUid("lyLU2wR22tC")
            .blockingGet()

        assertThat(indicators.size).isEqualTo(1)
    }

    @Test
    fun filter_by_sectionUid() {
        val indicators = d2.indicatorModule().indicators()
            .bySectionUid("Y2rk0vzgvAx")
            .blockingGet()

        assertThat(indicators.size).isEqualTo(1)
    }
}
