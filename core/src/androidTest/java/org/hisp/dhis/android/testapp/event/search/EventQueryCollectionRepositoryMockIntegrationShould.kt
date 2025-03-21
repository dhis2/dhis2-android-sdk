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
package org.hisp.dhis.android.testapp.event.search

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.util.toJavaSimpleDate
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class EventQueryCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_by_program() {
        val events = d2.eventModule().eventQuery()
            .byProgram().eq("lxAQ7Zs9VYR")
            .blockingGet()

        assertThat(events.size).isEqualTo(2)
    }

    @Test
    fun find_uids_by_program() {
        val eventUids = d2.eventModule().eventQuery()
            .byProgram().eq("lxAQ7Zs9VYR")
            .blockingGetUids()

        assertThat(eventUids.size).isEqualTo(2)
    }

    @Test
    fun get_scope() {
        val scope = d2.eventModule().eventQuery().scope

        assertThat(scope.mode()).isNotNull()
    }

    @Test
    fun filter_by_event_filter() {
        val event = d2.eventModule().eventQuery()
            .byEventFilter().eq("atoQ7Zs9Ijo")
            .blockingGet()

        assertThat(event.size).isEqualTo(1)
    }

    @Test
    fun filter_by_data_value() {
        val events1 = d2.eventModule().eventQuery()
            .byDataValue("hB9F8vKFmlk").eq("3842")
            .blockingGet()

        assertThat(events1.size).isEqualTo(1)

        val events2 = d2.eventModule().eventQuery()
            .byDataValue("hB9F8vKFmlk").ge("3842")
            .blockingGet()

        assertThat(events2.size).isEqualTo(2)
    }

    @Test
    fun filter_by_date_data_value() {
        val events1 = d2.eventModule().eventQuery()
            .byDataValue("uFAQYm3UgBL").after("2019-02-01".toJavaSimpleDate()!!)
            .byDataValue("uFAQYm3UgBL").before("2019-02-10".toJavaSimpleDate()!!)
            .blockingGet()

        assertThat(events1.size).isEqualTo(1)
    }
}
