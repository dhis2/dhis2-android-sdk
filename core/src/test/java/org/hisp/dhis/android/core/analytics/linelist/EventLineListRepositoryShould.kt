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
package org.hisp.dhis.android.core.analytics.linelist

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EventLineListRepositoryShould {

    private val eventLineListService: EventLineListService = mock()

    private val initialParams = EventLineListParams()

    private val paramsCaptor = argumentCaptor<EventLineListParams>()

    private val repository = EventLineListRepositoryImpl(eventLineListService, initialParams)

    @Test
    fun `Call service with dataElement list`() {
        repository
            .withDataElement("uid1")
            .withDataElement("uid2")
            .blockingEvaluate()

        verify(eventLineListService).evaluate(paramsCaptor.capture())
        assertThat(paramsCaptor.firstValue.dataElements.size).isEqualTo(2)
        assertThat(paramsCaptor.firstValue.dataElements).contains(LineListItem("uid1"))
        assertThat(paramsCaptor.firstValue.dataElements).contains(LineListItem("uid2"))
    }

    @Test
    fun `Call service with programIndicator list`() {
        repository
            .withProgramIndicator("uid1")
            .withProgramIndicator("uid2")
            .blockingEvaluate()

        verify(eventLineListService).evaluate(paramsCaptor.capture())
        assertThat(paramsCaptor.firstValue.programIndicators.size).isEqualTo(2)
        assertThat(paramsCaptor.firstValue.programIndicators).contains(LineListItem("uid1"))
        assertThat(paramsCaptor.firstValue.programIndicators).contains(LineListItem("uid2"))
    }

    @Test
    fun `Call service with program stage`() {
        repository
            .byProgramStage().eq("program_stage_uid")
            .blockingEvaluate()

        verify(eventLineListService).evaluate(paramsCaptor.capture())
        assertThat(paramsCaptor.firstValue.programStage).isEqualTo("program_stage_uid")
    }

    @Test
    fun `Call service with tracked entity instance uid`() {
        repository
            .byTrackedEntityInstance().eq("tracked_entity_instance_uid")
            .blockingEvaluate()

        verify(eventLineListService).evaluate(paramsCaptor.capture())
        assertThat(paramsCaptor.firstValue.trackedEntityInstance).isEqualTo("tracked_entity_instance_uid")
    }
}
