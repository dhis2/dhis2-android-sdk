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
package org.hisp.dhis.android.core.analytics.trackerlinelist

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.TrackerLineListParams
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.TrackerLineListRepositoryImpl
import org.hisp.dhis.android.core.analytics.trackerlinelist.internal.TrackerLineListService
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TrackerLineListRepositoryShould {

    private val service: TrackerLineListService = mock()

    private val initialParams = TrackerLineListParams(null, null, null, null, null, listOf(), listOf(), listOf())

    private val paramsCaptor = argumentCaptor<TrackerLineListParams>()

    private val repository = TrackerLineListRepositoryImpl(initialParams, service)

    @Test
    fun `Call service with overridden columns respecting initial order`() {
        val de1_1 = TrackerLineListItem.ProgramDataElement("dataElement1", "programStage", listOf(), null)
        val de2_1 = TrackerLineListItem.ProgramDataElement("dataElement2", "programStage", listOf(), null)
        val de1_2 = de1_1.copy(filters = listOf(DataFilter.EqualTo("value")))

        repository
            .withColumn(de1_1)
            .withColumn(de2_1)
            .withColumn(de1_2)
            .blockingEvaluate()

        verify(service).evaluate(paramsCaptor.capture())
        val columns = paramsCaptor.firstValue.columns

        assertThat(columns.size).isEqualTo(2)

        val dataElementColumns = columns.filterIsInstance<TrackerLineListItem.ProgramDataElement>()

        dataElementColumns.forEachIndexed { index, item ->
            when (index) {
                0 -> {
                    assertThat(item.dataElement).isEqualTo("dataElement1")
                    assertThat(item.filters.size).isEqualTo(1)
                }
                1 -> {
                    assertThat(item.dataElement).isEqualTo("dataElement2")
                    assertThat(item.filters.size).isEqualTo(0)
                }
            }
        }
    }
}
