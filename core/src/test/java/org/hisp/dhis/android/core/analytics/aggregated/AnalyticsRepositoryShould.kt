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
package org.hisp.dhis.android.core.analytics.aggregated

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hisp.dhis.android.core.analytics.AnalyticsLegendStrategy
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsRepositoryImpl
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsRepositoryParams
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsService
import org.hisp.dhis.android.core.common.AggregationType
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AnalyticsRepositoryShould {

    private val analyticsService: AnalyticsService = mock()

    private val initialParams = AnalyticsRepositoryParams(listOf(), listOf())

    private val paramsCaptor = argumentCaptor<AnalyticsRepositoryParams>()

    private val repository = AnalyticsRepositoryImpl(initialParams, analyticsService)

    @Test
    fun `Call service with fixed legend strategy`() {
        repository
            .withLegendStrategy(AnalyticsLegendStrategy.Fixed("uid1"))
            .blockingEvaluate()

        verify(analyticsService).evaluate(paramsCaptor.capture())
        val legendStrategy = paramsCaptor.firstValue.analyticsLegendStrategy

        assertThat(legendStrategy).isInstanceOf(AnalyticsLegendStrategy.Fixed::class.java)
        assertThat((legendStrategy as AnalyticsLegendStrategy.Fixed).legendSetUid).isEqualTo("uid1")
    }

    @Test
    fun `Call service with none legend strategy`() {
        repository
            .withLegendStrategy(AnalyticsLegendStrategy.None)
            .blockingEvaluate()

        verify(analyticsService).evaluate(paramsCaptor.capture())
        val legendStrategy = paramsCaptor.firstValue.analyticsLegendStrategy

        assertThat(legendStrategy).isInstanceOf(AnalyticsLegendStrategy.None::class.java)
    }

    @Test
    fun `Call service with byDataItem legend strategy`() {
        repository
            .withLegendStrategy(AnalyticsLegendStrategy.ByDataItem)
            .blockingEvaluate()

        verify(analyticsService).evaluate(paramsCaptor.capture())
        val legendStrategy = paramsCaptor.firstValue.analyticsLegendStrategy

        assertThat(legendStrategy).isInstanceOf(AnalyticsLegendStrategy.ByDataItem::class.java)
    }

    @Test
    fun `Call service with overriden aggregation type`() {
        repository
            .withAggregationType(AggregationType.LAST)
            .blockingEvaluate()

        verify(analyticsService).evaluate(paramsCaptor.capture())
        val aggregationType = paramsCaptor.firstValue.aggregationType

        assertThat(aggregationType).isEqualTo(AggregationType.LAST)
    }
}
