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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.SyncedDatabaseMockIntegrationShould;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityDataValueCollectionRepositoryMockIntegrationShould extends SyncedDatabaseMockIntegrationShould {

    @Test
    public void allow_access_to_all_tracked_entity_data_values() {
        List<TrackedEntityDataValue> trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues.get();
        assertThat(trackedEntityDataValues.size(), is(12));
    }

    @Test
    public void filter_by_event() {
        List<TrackedEntityDataValue> trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues
                .byEvent().eq("single1")
                .get();
        assertThat(trackedEntityDataValues.size(), is(6));
    }

    @Test
    public void filter_by_created() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse("2015-02-28T12:05:00.333");
        List<TrackedEntityDataValue> trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues
                .byCreated().eq(date)
                .get();
        assertThat(trackedEntityDataValues.size(), is(1));
    }

    @Test
    public void filter_by_last_updated() throws ParseException {
        Date date = BaseIdentifiableObject.DATE_FORMAT.parse("2015-02-28T12:05:00.222");
        List<TrackedEntityDataValue> trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues
                .byLastUpdated().eq(date)
                .get();
        assertThat(trackedEntityDataValues.size(), is(1));
    }

    @Test
    public void filter_by_data_element() {
        List<TrackedEntityDataValue> trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues
                .byDataElement().eq("ebaJjqltK5N")
                .get();
        assertThat(trackedEntityDataValues.size(), is(2));
    }

    @Test
    public void filter_by_stored_by() {
        List<TrackedEntityDataValue> trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues
                .byStoredBy().eq("storer")
                .get();
        assertThat(trackedEntityDataValues.size(), is(1));
    }

    @Test
    public void filter_by_value() {
        List<TrackedEntityDataValue> trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues
                .byValue().eq("11")
                .get();
        assertThat(trackedEntityDataValues.size(), is(2));
    }

    @Test
    public void filter_by_provided_elsewhere() {
        List<TrackedEntityDataValue> trackedEntityDataValues = d2.trackedEntityModule().trackedEntityDataValues
                .byProvidedElsewhere().eq(true)
                .get();
        assertThat(trackedEntityDataValues.size(), is(1));
    }
}