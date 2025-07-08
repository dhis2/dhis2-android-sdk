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
package org.hisp.dhis.android.testapp.program

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.ValueTypeRenderingType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class ProgramTrackedEntityAttributeCollectionRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val programTrackedEntityAttributes = d2.programModule().programTrackedEntityAttributes().blockingGet()

        assertThat(programTrackedEntityAttributes.size).isEqualTo(2)
    }

    @Test
    fun filter_by_mandatory() {
        val programTrackedEntityAttributes = d2.programModule().programTrackedEntityAttributes()
            .byMandatory()
            .isFalse
            .blockingGet()

        assertThat(programTrackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun filter_by_tracked_entity_attribute() {
        val programTrackedEntityAttributes = d2.programModule().programTrackedEntityAttributes()
            .byTrackedEntityAttribute()
            .eq("cejWyOfXge6")
            .blockingGet()

        assertThat(programTrackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun filter_by_allow_future_date() {
        val programTrackedEntityAttributes = d2.programModule().programTrackedEntityAttributes()
            .byAllowFutureDate()
            .isTrue
            .blockingGet()

        assertThat(programTrackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun filter_by_display_in_list() {
        val programTrackedEntityAttributes = d2.programModule().programTrackedEntityAttributes()
            .byDisplayInList()
            .isTrue
            .blockingGet()

        assertThat(programTrackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun filter_by_program() {
        val programTrackedEntityAttributes = d2.programModule().programTrackedEntityAttributes()
            .byProgram()
            .eq("IpHINAT79UW")
            .blockingGet()

        assertThat(programTrackedEntityAttributes.size).isEqualTo(2)
    }

    @Test
    fun filter_by_sort_order() {
        val programTrackedEntityAttributes = d2.programModule().programTrackedEntityAttributes()
            .bySortOrder()
            .biggerThan(1)
            .blockingGet()

        assertThat(programTrackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun filter_by_searchable() {
        val programTrackedEntityAttributes = d2.programModule().programTrackedEntityAttributes()
            .bySearchable()
            .isTrue
            .blockingGet()

        assertThat(programTrackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun include_render_type_as_children() {
        val programTrackedEntityAttribute = d2.programModule().programTrackedEntityAttributes()
            .byUid().eq("YhqgQ6Iy4c4")
            .withRenderType()
            .one().blockingGet()

        assertThat(programTrackedEntityAttribute!!.renderType()!!.mobile()!!.type())
            .isEqualTo(ValueTypeRenderingType.SHARED_HEADER_RADIOBUTTONS)
        assertThat(programTrackedEntityAttribute.renderType()!!.desktop()!!.type())
            .isEqualTo(ValueTypeRenderingType.VERTICAL_RADIOBUTTONS)
    }

    @Test
    fun order_by_sort_order() {
        val programTrackedEntityAttributes = d2.programModule().programTrackedEntityAttributes()
            .orderBySortOrder(RepositoryScope.OrderByDirection.DESC)
            .blockingGet()

        assertThat(programTrackedEntityAttributes[0].uid()).isEqualTo("QhqgQ6Iy4c4")
        assertThat(programTrackedEntityAttributes[0].sortOrder()).isEqualTo(2)
        assertThat(programTrackedEntityAttributes[1].uid()).isEqualTo("YhqgQ6Iy4c4")
        assertThat(programTrackedEntityAttributes[1].sortOrder()).isEqualTo(1)
    }
}
