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

package org.hisp.dhis.android.testapp.trackedentity;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class TrackedEntityAttributeCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_pattern() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byPattern().eq("RANDOM(XXX######)")
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_sort_order_in_list_no_programs() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .bySortOrderInListNoProgram().eq(0)
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_option_set() {
        List<TrackedEntityAttribute> dataElements =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byOptionSetUid().eq("VQ2lai3OfVG")
                        .blockingGet();
        assertThat(dataElements.size()).isEqualTo(1);
    }

    @Test
    public void by_value_type() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byValueType().eq(ValueType.NUMBER)
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void by_expression() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byExpression().eq("expression")
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void by_program_scope() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byProgramScope().isTrue()
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void by_display_in_list_no_program() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byDisplayInListNoProgram().isTrue()
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void by_generated() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byGenerated().isTrue()
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void by_display_on_visit_schedule() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byDisplayOnVisitSchedule().isTrue()
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void by_orgunit_scope() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byOrgUnitScope().isTrue()
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void by_unique() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byUnique().isTrue()
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void by_inherit() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byInherit().isTrue()
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void by_field_mask() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byFieldMask().eq("XXXXX")
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void by_form_name() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byFormName().eq("formname")
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void by_display_form_name() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byDisplayFormName().eq("displayformname")
                        .blockingGet();

        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void withLegendSets() {
        TrackedEntityAttribute trackedEntityAttribute =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .withLegendSets()
                        .one()
                        .blockingGet();
        assertThat(trackedEntityAttribute.legendSets().size()).isEqualTo(2);
    }

    @Test
    public void filter_by_field_color() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byColor().eq("#556")
                        .blockingGet();
        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_field_icon() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes()
                        .byIcon().eq("attribute-icon")
                        .blockingGet();
        assertThat(trackedEntityAttributes.size()).isEqualTo(1);
    }
}
