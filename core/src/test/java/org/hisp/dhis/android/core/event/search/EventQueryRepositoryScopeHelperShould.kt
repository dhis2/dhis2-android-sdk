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
package org.hisp.dhis.android.core.event.search

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.event.EventFilter
import org.hisp.dhis.android.core.event.EventQueryCriteria
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EventQueryRepositoryScopeHelperShould {

    private val filterUid = "filterUid"

    @Test
    fun `Should parse order clause`() {
        val scope = EventQueryRepositoryScope.empty()

        val criteria = EventQueryCriteria.builder()
            .order("dueDate:asc,eventDate:desc,Xve32rmxfpT:asc")
            .build()
        val filter = EventFilter.builder().uid(filterUid).eventQueryCriteria(criteria).build()

        val updatedScope = EventQueryRepositoryScopeHelper.addEventFilter(scope, filter)

        assertThat(updatedScope.order().size).isEqualTo(3)
        updatedScope.order().first().let {
            assertThat(it.column()).isEqualTo(EventQueryScopeOrderColumn.DUE_DATE)
            assertThat(it.direction()).isEqualTo(RepositoryScope.OrderByDirection.ASC)
        }
        updatedScope.order()[1].let {
            assertThat(it.column()).isEqualTo(EventQueryScopeOrderColumn.EVENT_DATE)
            assertThat(it.direction()).isEqualTo(RepositoryScope.OrderByDirection.DESC)
        }
        updatedScope.order().last().let {
            assertThat(it.column().type()).isEqualTo(EventQueryScopeOrderColumn.Type.DATA_ELEMENT)
            assertThat(it.column().value()).isEqualTo("Xve32rmxfpT")
            assertThat(it.direction()).isEqualTo(RepositoryScope.OrderByDirection.ASC)
        }
    }

    @Test
    fun `Should ignore unknown order clauses`() {
        val scope = EventQueryRepositoryScope.empty()

        val criteria = EventQueryCriteria.builder()
            .order("invalidOrderClause:asc,eventDate:desc")
            .build()
        val filter = EventFilter.builder().uid(filterUid).eventQueryCriteria(criteria).build()

        val updatedScope = EventQueryRepositoryScopeHelper.addEventFilter(scope, filter)

        assertThat(updatedScope.order().size).isEqualTo(1)
        updatedScope.order().first().let {
            assertThat(it.column()).isEqualTo(EventQueryScopeOrderColumn.EVENT_DATE)
            assertThat(it.direction()).isEqualTo(RepositoryScope.OrderByDirection.DESC)
        }
    }

    @Test
    fun `Should overwrite existing properties`() {
        val scope = EventQueryRepositoryScope.builder().program("initial_program").build()
        val filter = EventFilter.builder().uid(filterUid).program("filter_program").build()

        val updatedScope = EventQueryRepositoryScopeHelper.addEventFilter(scope, filter)

        assertThat(updatedScope.program()).isEqualTo("filter_program")
    }
}
