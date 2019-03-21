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

package org.hisp.dhis.android.testapp.trackedentity;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.SyncedDatabaseMockIntegrationShould;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityAttributeCollectionRepositoryMockIntegrationShould extends SyncedDatabaseMockIntegrationShould {

    @Test
    public void find_all() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .get();

        assertThat(trackedEntityAttributes.size(), is(2));
    }

    @Test
    public void filter_by_pattern() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .byPattern().eq("RANDOM(XXX######)")
                        .get();

        assertThat(trackedEntityAttributes.size(), is(1));
    }

    @Test
    public void filter_by_sort_order_in_list_no_programs() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .bySortOrderInListNoProgram().eq(0)
                        .get();

        assertThat(trackedEntityAttributes.size(), is(1));
    }

    @Test
    public void filter_by_option_set() {
        List<TrackedEntityAttribute> dataElements =
                d2.trackedEntityModule().trackedEntityAttributes
                        .byOptionSetUid().eq("VQ2lai3OfVG")
                        .get();
        assertThat(dataElements.size(), is(1));
    }

    @Test
    public void by_value_type() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .byValueType().eq(ValueType.NUMBER)
                        .get();

        assertThat(trackedEntityAttributes.size(), is(1));
    }

    @Test
    public void by_expression() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .byExpression().eq("expression")
                        .get();

        assertThat(trackedEntityAttributes.size(), is(1));
    }

    @Test
    public void by_program_scope() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .byProgramScope().isTrue()
                        .get();

        assertThat(trackedEntityAttributes.size(), is(1));
    }

    @Test
    public void by_display_in_list_no_program() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .byDisplayInListNoProgram().isTrue()
                        .get();

        assertThat(trackedEntityAttributes.size(), is(1));
    }

    @Test
    public void by_generated() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .byGenerated().isTrue()
                        .get();

        assertThat(trackedEntityAttributes.size(), is(1));
    }

    @Test
    public void by_display_on_visit_schedule() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .byDisplayOnVisitSchedule().isTrue()
                        .get();

        assertThat(trackedEntityAttributes.size(), is(1));
    }

    @Test
    public void by_orgunit_scope() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .byOrgUnitScope().isTrue()
                        .get();

        assertThat(trackedEntityAttributes.size(), is(1));
    }

    @Test
    public void by_unique() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .byUnique().isTrue()
                        .get();

        assertThat(trackedEntityAttributes.size(), is(1));
    }

    @Test
    public void by_inherit() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .byInherit().isTrue()
                        .get();

        assertThat(trackedEntityAttributes.size(), is(1));
    }

    @Test
    public void by_form_name() {
        List<TrackedEntityAttribute> trackedEntityAttributes =
                d2.trackedEntityModule().trackedEntityAttributes
                        .byFormName().eq("formname")
                        .get();

        assertThat(trackedEntityAttributes.size(), is(1));
    }

}
