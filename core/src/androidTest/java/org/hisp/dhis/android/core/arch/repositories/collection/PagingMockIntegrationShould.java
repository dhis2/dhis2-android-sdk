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

package org.hisp.dhis.android.core.arch.repositories.collection;

import com.jraska.livedata.TestObserver;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.OrderByClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

@RunWith(D2JunitRunner.class)
public class PagingMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private IdentifiableObjectStore<CategoryOption> store;
    private List<CategoryOption> allValues;
    private RepositoryScope empty = RepositoryScope.empty();
    private String orderByClause = OrderByClauseBuilder.orderByFromItems(empty.orderBy(), empty.pagingKey());

    @Before
    public void setUp() {
        store = objects.d2DIComponent.categoryOptionStore();
        allValues = store.selectWhere("1", orderByClause, 8);
    }

    @Test
    public void get_initial_objects_with_default_order_considering_prefetch_distance() throws InterruptedException {
        LiveData<PagedList<CategoryOption>> liveData = d2.categoryModule().categoryOptions().getPaged(2);
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
    public void get_initial_objects_ordered_by_display_name_asc() throws InterruptedException {
        LiveData<PagedList<CategoryOption>> liveData = d2.categoryModule().categoryOptions()
                .orderByDisplayName(RepositoryScope.OrderByDirection.ASC)
                .getPaged(2);
        TestObserver.test(liveData)
                .awaitValue()
                .assertHasValue()
                .assertValue(pagedList -> pagedList.size() == 6)
                .assertValue(pagedList -> pagedList.get(0).displayName().equals("At PHU"))
                .assertValue(pagedList -> pagedList.get(1).displayName().equals("Female"))
                .assertValue(pagedList -> pagedList.get(2).displayName().equals("In Community"))
                .assertValue(pagedList -> pagedList.get(3).displayName().equals("MCH Aides"))
                .assertValue(pagedList -> pagedList.get(4).displayName().equals("Male"))
                .assertValue(pagedList -> pagedList.get(5).displayName().equals("SECHN"));
    }

    @Test
    public void get_initial_objects_ordered_by_display_name_desc() throws InterruptedException {
        LiveData<PagedList<CategoryOption>> liveData = d2.categoryModule().categoryOptions()
                .orderByDisplayName(RepositoryScope.OrderByDirection.DESC)
                .getPaged(2);
        TestObserver.test(liveData)
                .awaitValue()
                .assertHasValue()
                .assertValue(pagedList -> pagedList.size() == 6)
                .assertValue(pagedList -> pagedList.get(0).displayName().equals("default display name"))
                .assertValue(pagedList -> pagedList.get(1).displayName().equals("Trained TBA"))
                .assertValue(pagedList -> pagedList.get(2).displayName().equals("SECHN"))
                .assertValue(pagedList -> pagedList.get(3).displayName().equals("Male"))
                .assertValue(pagedList -> pagedList.get(4).displayName().equals("MCH Aides"))
                .assertValue(pagedList -> pagedList.get(5).displayName().equals("In Community"));
    }

    @Test
    public void get_initial_objects_ordered_by_description_desc() throws InterruptedException {
        LiveData<PagedList<CategoryOption>> liveData = d2.categoryModule().categoryOptions()
                .orderByDescription(RepositoryScope.OrderByDirection.DESC)
                .getPaged(2);
        TestObserver.test(liveData)
                .awaitValue()
                .assertHasValue()
                .assertValue(pagedList -> pagedList.size() == 6)
                .assertValue(pagedList -> pagedList.get(0).displayName().equals("default display name"))
                .assertValue(pagedList -> pagedList.get(1).displayName().equals("Female"))
                .assertValue(pagedList -> pagedList.get(2).displayName().equals("Male"))
                .assertValue(pagedList -> pagedList.get(3).displayName().equals("In Community"))
                .assertValue(pagedList -> pagedList.get(4).displayName().equals("At PHU"))
                .assertValue(pagedList -> pagedList.get(5).displayName().equals("MCH Aides"));
    }

    @Test
    public void get_initial_objects_ordered_by_description_and_display_name_desc() throws InterruptedException {
        LiveData<PagedList<CategoryOption>> liveData = d2.categoryModule().categoryOptions()
                .orderByDescription(RepositoryScope.OrderByDirection.DESC)
                .orderByDisplayName(RepositoryScope.OrderByDirection.ASC)
                .getPaged(2);
        TestObserver.test(liveData)
                .awaitValue()
                .assertHasValue()
                .assertValue(pagedList -> pagedList.size() == 6)
                .assertValue(pagedList -> pagedList.get(0).displayName().equals("default display name"))
                .assertValue(pagedList -> pagedList.get(1).displayName().equals("At PHU"))
                .assertValue(pagedList -> pagedList.get(2).displayName().equals("Female"))
                .assertValue(pagedList -> pagedList.get(3).displayName().equals("In Community"))
                .assertValue(pagedList -> pagedList.get(4).displayName().equals("Male"))
                .assertValue(pagedList -> pagedList.get(5).displayName().equals("MCH Aides"));
    }
}