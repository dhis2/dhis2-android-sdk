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
package org.hisp.dhis.android.testapp.trackedentity

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class TrackedEntityAttributeCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes().blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(3)
    }

    @Test
    fun filter_by_pattern() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byPattern().eq("RANDOM(XXX######)")
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun filter_by_sort_order_in_list_no_programs() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .bySortOrderInListNoProgram().eq(0)
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun filter_by_option_set() {
        val dataElements = d2.trackedEntityModule().trackedEntityAttributes()
            .byOptionSetUid().eq("VQ2lai3OfVG")
            .blockingGet()

        assertThat(dataElements.size).isEqualTo(1)
    }

    @Test
    fun by_value_type() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byValueType().eq(ValueType.NUMBER)
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun by_expression() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byExpression().eq("expression")
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun by_program_scope() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byProgramScope().isTrue
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun by_display_in_list_no_program() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byDisplayInListNoProgram().isTrue
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(2)
    }

    @Test
    fun by_generated() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byGenerated().isTrue
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun by_display_on_visit_schedule() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byDisplayOnVisitSchedule().isTrue
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun by_confidential() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byConfidential().isTrue
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun by_orgunit_scope() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byOrgUnitScope().isTrue
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun by_unique() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byUnique().isTrue
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun by_inherit() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byInherit().isTrue
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(2)
    }

    @Test
    fun by_field_mask() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byFieldMask().eq("XXXXX")
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun by_form_name() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byFormName().eq("formname")
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun by_display_form_name() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byDisplayFormName().eq("displayformname")
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun withLegendSets() {
        val trackedEntityAttribute = d2.trackedEntityModule().trackedEntityAttributes()
            .withLegendSets()
            .one().blockingGet()

        assertThat(trackedEntityAttribute!!.legendSets()!!.size).isEqualTo(2)
    }

    @Test
    fun filter_by_field_color() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byColor().eq("#556")
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }

    @Test
    fun filter_by_field_icon() {
        val trackedEntityAttributes = d2.trackedEntityModule().trackedEntityAttributes()
            .byIcon().eq("attribute-icon")
            .blockingGet()

        assertThat(trackedEntityAttributes.size).isEqualTo(1)
    }
}
