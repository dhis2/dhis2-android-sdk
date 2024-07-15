/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.arch.repositories.collection

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagedList
import androidx.paging.testing.asSnapshot
import com.jraska.livedata.TestObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.OrderByClauseBuilder
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.category.internal.CategoryOptionStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class PagingMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var store: CategoryOptionStore
    private lateinit var allValues: List<CategoryOption>

    private val empty = RepositoryScope.empty()
    private val orderByClause = OrderByClauseBuilder.orderByFromItems(empty.orderBy(), empty.pagingKey())

    @Before
    fun setUp() {
        store = objects.d2DIComponent.categoryOptionStore
        allValues = store.selectWhere("1", orderByClause, 8)
    }

    @Test
    fun get_initial_objects_with_default_order_considering_prefetch_distance() {
        val liveData = d2.categoryModule().categoryOptions().getPaged(2)

        TestObserver.test(liveData)
            .awaitValue()
            .assertHasValue()
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList.size == 6 }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[0] == allValues[0] }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[1] == allValues[1] }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[2] == allValues[2] }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[3] == allValues[3] }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[4] == allValues[4] }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[5] == allValues[5] }
    }

    @Test
    fun get_initial_objects_ordered_by_display_name_asc() {
        val liveData = d2.categoryModule().categoryOptions()
            .orderByDisplayName(RepositoryScope.OrderByDirection.ASC)
            .getPaged(2)

        TestObserver.test(liveData)
            .awaitValue()
            .assertHasValue()
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList.size == 6 }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[0]!!.displayName() == "At PHU" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[1]!!.displayName() == "Female" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[2]!!.displayName() == "In Community" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[3]!!.displayName() == "MCH Aides" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[4]!!.displayName() == "Male" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[5]!!.displayName() == "SECHN" }
    }

    @Test
    fun get_initial_objects_ordered_by_display_name_desc() {
        val liveData = d2.categoryModule().categoryOptions()
            .orderByDisplayName(RepositoryScope.OrderByDirection.DESC)
            .getPaged(2)

        TestObserver.test(liveData)
            .awaitValue()
            .assertHasValue()
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList.size == 6 }
            .assertValue { pagedList: PagedList<CategoryOption> ->
                pagedList[0]!!.displayName() == "default display name"
            }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[1]!!.displayName() == "Trained TBA" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[2]!!.displayName() == "SECHN" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[3]!!.displayName() == "Male" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[4]!!.displayName() == "MCH Aides" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[5]!!.displayName() == "In Community" }
    }

    @Test
    fun get_initial_objects_ordered_by_description_desc() {
        val liveData = d2.categoryModule().categoryOptions()
            .orderByDescription(RepositoryScope.OrderByDirection.DESC)
            .getPaged(2)

        TestObserver.test(liveData)
            .awaitValue()
            .assertHasValue()
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList.size == 6 }
            .assertValue { pagedList: PagedList<CategoryOption> ->
                pagedList[0]!!.displayName() == "default display name"
            }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[1]!!.displayName() == "Female" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[2]!!.displayName() == "Male" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[3]!!.displayName() == "In Community" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[4]!!.displayName() == "At PHU" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[5]!!.displayName() == "MCH Aides" }
    }

    @Test
    fun get_initial_objects_ordered_by_description_and_display_name_desc() = runTest {
        val liveData = d2.categoryModule().categoryOptions()
            .orderByDescription(RepositoryScope.OrderByDirection.DESC)
            .orderByDisplayName(RepositoryScope.OrderByDirection.ASC)
            .getPaged(2)

        TestObserver.test(liveData)
            .awaitValue()
            .assertHasValue()
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList.size == 6 }
            .assertValue { pagedList: PagedList<CategoryOption> ->
                pagedList[0]!!.displayName() == "default display name"
            }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[1]!!.displayName() == "At PHU" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[2]!!.displayName() == "Female" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[3]!!.displayName() == "In Community" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[4]!!.displayName() == "Male" }
            .assertValue { pagedList: PagedList<CategoryOption> -> pagedList[5]!!.displayName() == "MCH Aides" }
    }

    @Test
    fun get_pagingData_with_initial_objects_with_default_order_considering_prefetch_distance() = runTest {
        val items = d2.categoryModule().categoryOptions().getPagingData(2)

        val itemsSnapshot: List<CategoryOption> = items.asSnapshot()

        assertEquals(itemsSnapshot.size, 6)
        assertEquals(itemsSnapshot[0], allValues[0])
        assertEquals(itemsSnapshot[1], allValues[1])
        assertEquals(itemsSnapshot[2], allValues[2])
        assertEquals(itemsSnapshot[3], allValues[3])
        assertEquals(itemsSnapshot[4], allValues[4])
        assertEquals(itemsSnapshot[5], allValues[5])
    }

    @Test
    fun get_pagingData_with_initial_objects_ordered_by_display_name_asc() = runTest {
        val items = d2.categoryModule().categoryOptions()
            .orderByDisplayName(RepositoryScope.OrderByDirection.ASC)
            .getPagingData(2)

        val snapshot = items.asSnapshot()

        assertEquals(snapshot.size, 6)
        assertEquals(snapshot[0].displayName(), "At PHU")
        assertEquals(snapshot[1].displayName(), "Female")
        assertEquals(snapshot[2].displayName(), "In Community")
        assertEquals(snapshot[3].displayName(), "MCH Aides")
        assertEquals(snapshot[4].displayName(), "Male")
        assertEquals(snapshot[5].displayName(), "SECHN")
    }

    @Test
    fun get_pagingData_with_initial_objects_ordered_by_display_name_desc() = runTest {
        val items = d2.categoryModule().categoryOptions()
            .orderByDisplayName(RepositoryScope.OrderByDirection.DESC)
            .getPagingData(2)

        val snapshot = items.asSnapshot()

        assertEquals(snapshot.size, 6)
        assertEquals(snapshot[0].displayName(), "default display name")
        assertEquals(snapshot[1].displayName(), "Trained TBA")
        assertEquals(snapshot[2].displayName(), "SECHN")
        assertEquals(snapshot[3].displayName(), "Male")
        assertEquals(snapshot[4].displayName(), "MCH Aides")
        assertEquals(snapshot[5].displayName(), "In Community")
    }

    @Test
    fun get_pagingData_with_initial_objects_ordered_by_description_desc() = runTest {
        val items = d2.categoryModule().categoryOptions()
            .orderByDescription(RepositoryScope.OrderByDirection.DESC)
            .getPagingData(2)

        val snapshot = items.asSnapshot()

        assertEquals(snapshot.size, 6)
        assertEquals(snapshot[0].displayName(), "default display name")
        assertEquals(snapshot[1].displayName(), "Female")
        assertEquals(snapshot[2].displayName(), "Male")
        assertEquals(snapshot[3].displayName(), "In Community")
        assertEquals(snapshot[4].displayName(), "At PHU")
        assertEquals(snapshot[5].displayName(), "MCH Aides")
    }

    @Test
    fun get_pagingData_with_initial_objects_ordered_by_description_and_display_name_desc() = runTest {
        val items = d2.categoryModule().categoryOptions()
            .orderByDescription(RepositoryScope.OrderByDirection.DESC)
            .orderByDisplayName(RepositoryScope.OrderByDirection.ASC)
            .getPagingData(2)

        val snapshot = items.asSnapshot()

        assertEquals(snapshot.size, 6)
        assertEquals(snapshot[0].displayName(), "default display name")
        assertEquals(snapshot[1].displayName(), "At PHU")
        assertEquals(snapshot[2].displayName(), "Female")
        assertEquals(snapshot[3].displayName(), "In Community")
        assertEquals(snapshot[4].displayName(), "Male")
        assertEquals(snapshot[5].displayName(), "MCH Aides")
    }
}
