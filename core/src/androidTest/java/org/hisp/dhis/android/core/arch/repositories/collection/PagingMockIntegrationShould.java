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

package org.hisp.dhis.android.core.arch.repositories.collection;

import com.jraska.livedata.TestObserver;

import org.hisp.dhis.android.core.data.database.SyncedDatabaseMockIntegrationShould;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;
import androidx.test.runner.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class PagingMockIntegrationShould extends SyncedDatabaseMockIntegrationShould {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private TrackedEntityDataValueStore store;
    private List<TrackedEntityDataValue> allValues;

    @Before
    public void setUp() {
        store = getD2DIComponent().trackedEntityDataValueStore();
        allValues = store.selectInitialPaging("1", 12);
    }

    @Test
    public void get_initial_objects_considering_prefetch_distance() throws InterruptedException {
        LiveData<PagedList<TrackedEntityDataValue>> liveData =
                d2.trackedEntityModule().trackedEntityDataValues.getPaged(2);
        TestObserver.test(liveData)
                .awaitValue()
                .assertHasValue()
                .assertValue(pagedList -> pagedList.size() == 6)
                .assertValue(pagedList -> pagedList.get(0).equals(allValues.get(0)))
                .assertValue(pagedList -> pagedList.get(1).equals(allValues.get(1)))
                .assertValue(pagedList -> pagedList.get(2).equals(allValues.get(2)))
                .assertValue(pagedList -> pagedList.get(3).equals(allValues.get(3)))
                .assertValue(pagedList -> pagedList.get(4).equals(allValues.get(4)))
                .assertValue(pagedList -> pagedList.get(5).equals(allValues.get(5)));
    }

    @Test
    public void get_initial_page_objects_from_repository() {
        List<TrackedEntityDataValue> firstPage = store.selectInitialPaging("1", 2);
        assertThat(firstPage).hasSize(2);
        assertThat(firstPage.get(0)).isEqualTo(allValues.get(0));
        assertThat(firstPage.get(1)).isEqualTo(allValues.get(1));
    }

    @Test
    public void get_next_page_objects_from_repository() {
        List<TrackedEntityDataValue> afterPage = store.selectAfterPaging("1",
                allValues.get(1).id(), 2);
        assertThat(afterPage).hasSize(2);
        assertThat(afterPage.get(0)).isEqualTo(allValues.get(2));
        assertThat(afterPage.get(1)).isEqualTo(allValues.get(3));
    }

    @Test
    public void get_previous_page_objects_from_repository() {
        List<TrackedEntityDataValue> beforePage = store.selectBeforePaging("1",
                allValues.get(3).id(), 2);
        assertThat(beforePage).hasSize(2);
        assertThat(beforePage.get(0)).isEqualTo(allValues.get(2));
        assertThat(beforePage.get(1)).isEqualTo(allValues.get(1));
    }
}