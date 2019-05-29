/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.testapp.indicator;

import org.hisp.dhis.android.core.indicator.Indicator;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(D2JunitRunner.class)
public class IndicatorCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<Indicator> indicators = d2.indicatorModule().indicators
                .get();
        assertThat(indicators.size(), is(1));
    }

    @Test
    public void filter_by_annualized() {
        List<Indicator> indicators = d2.indicatorModule().indicators
                .byAnnualized().isFalse()
                .get();
        assertThat(indicators.size(), is(1));
    }

    @Test
    public void filter_by_indicator_type_url() {
        List<Indicator> indicators = d2.indicatorModule().indicators
                .byIndicatorTypeUid().eq("bWuNrMHEoZ0")
                .get();
        assertThat(indicators.size(), is(1));
    }

    @Test
    public void filter_by_numerator() {
        List<Indicator> indicators = d2.indicatorModule().indicators
                .byNumerator().eq("#{fbfJHSPpUQD.pq2XI5kz2BY}+#{fbfJHSPpUQD.PT59n8BQbqM}-#{Jtf34kNZhzP.pq2XI5kz2BY}-#{Jtf34kNZhzP.PT59n8BQbqM}")
                .get();
        assertThat(indicators.size(), is(1));
    }

    @Test
    public void filter_by_numerator_description() {
        List<Indicator> indicators = d2.indicatorModule().indicators
                .byNumeratorDescription().eq("ANC1-ANC3")
                .get();
        assertThat(indicators.size(), is(1));
    }

    @Test
    public void filter_by_denominator() {
        List<Indicator> indicators = d2.indicatorModule().indicators
                .byDenominator().eq("#{fbfJHSPpUQD.pq2XI5kz2BY}+#{fbfJHSPpUQD.PT59n8BQbqM}")
                .get();
        assertThat(indicators.size(), is(1));
    }

    @Test
    public void filter_by_denominator_description() {
        List<Indicator> indicators = d2.indicatorModule().indicators
                .byDenominatorDescription().eq("Total 1st ANC visits")
                .get();
        assertThat(indicators.size(), is(1));
    }

    @Test
    public void filter_by_url() {
        List<Indicator> indicators = d2.indicatorModule().indicators
                .byUrl().eq("url")
                .get();
        assertThat(indicators.size(), is(1));
    }
}