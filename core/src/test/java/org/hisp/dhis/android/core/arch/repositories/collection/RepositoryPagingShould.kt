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
package org.his.dhis.android.core.arch.repositories.collection

import android.content.ContentValues
import androidx.paging.ItemKeyedDataSource
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.paging.internal.RepositoryDataSource
import org.hisp.dhis.android.core.arch.repositories.paging.internal.RepositoryPagingConfig
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper.withFilterItem
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper.withOrderBy
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeOrderByItem
import org.hisp.dhis.android.core.category.CategoryOption
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class RepositoryPagingShould {
    private val emptyScope = RepositoryScope.empty()

    private val store: IdentifiableObjectStore<CategoryOption> = mock()
    private val databaseAdapter: DatabaseAdapter = mock()
    private val `object`: CategoryOption = mock()
    private val key: CategoryOption = mock()
    private val keyContentValues: ContentValues = mock()

    private val objects = listOf(`object`)
    private val childrenAppenders: ChildrenAppenderGetter<CategoryOption> = emptyMap()

    private val initialCallback: ItemKeyedDataSource.LoadInitialCallback<CategoryOption> = mock()

    @Before
    fun setUp() {
        whenever(store.selectWhere(any(), any(), any())).doReturn(objects)
    }

    @Test
    fun get_initial_page_objects_without_order_by() {
        val dataSource: RepositoryDataSource<CategoryOption> =
            RepositoryDataSource(store, databaseAdapter, emptyScope, childrenAppenders)
        dataSource.loadInitial(ItemKeyedDataSource.LoadInitialParams(null, 3, false), initialCallback)
        Mockito.verify(store).selectWhere("1", "_id ASC", 3)
        Mockito.verify(initialCallback).onResult(objects)
    }

    @Test
    fun get_initial_page_objects_with_order_by() {
        val updatedScope = withOrderBy(
            emptyScope,
            RepositoryScopeOrderByItem.builder().column("name").direction(RepositoryScope.OrderByDirection.DESC).build()
        )
        val dataSource: RepositoryDataSource<CategoryOption?> =
            RepositoryDataSource<M?>(store, updatedScope, childrenAppenders)
        dataSource.loadInitial(ItemKeyedDataSource.LoadInitialParams(null, 3, false), initialCallback!!)
        Mockito.verify(store).selectWhere("1", "name DESC, _id ASC", 3)
        Mockito.verify(initialCallback).onResult(objects)
    }

    @Test
    fun get_initial_page_objects_with_forced_order_by_paging_key_asc() {
        val updatedScope = withOrderBy(
            emptyScope,
            RepositoryScopeOrderByItem.builder().column(RepositoryPagingConfig.PAGING_KEY)
                .direction(RepositoryScope.OrderByDirection.ASC).build()
        )
        val dataSource: RepositoryDataSource<CategoryOption?> =
            RepositoryDataSource<M?>(store, updatedScope, childrenAppenders)
        dataSource.loadInitial(ItemKeyedDataSource.LoadInitialParams(null, 3, false), initialCallback!!)
        Mockito.verify(store).selectWhere("1", "_id ASC", 3)
        Mockito.verify(initialCallback).onResult(objects)
    }

    @Test
    fun get_initial_page_objects_with_forced_order_by_paging_key_desc() {
        val updatedScope = withOrderBy(
            emptyScope,
            RepositoryScopeOrderByItem.builder().column(RepositoryPagingConfig.PAGING_KEY)
                .direction(RepositoryScope.OrderByDirection.DESC).build()
        )
        val dataSource: RepositoryDataSource<CategoryOption?> =
            RepositoryDataSource<M?>(store, updatedScope, childrenAppenders)
        dataSource.loadInitial(ItemKeyedDataSource.LoadInitialParams(null, 3, false), initialCallback!!)
        Mockito.verify(store).selectWhere("1", "_id DESC", 3)
        Mockito.verify(initialCallback).onResult(objects)
    }

    @Test
    fun get_initial_page_objects_with_two_order_by() {
        val updatedScope = withOrderBy(
            emptyScope,
            RepositoryScopeOrderByItem.builder().column("c1").direction(RepositoryScope.OrderByDirection.DESC).build()
        )
        val updatedScope2 = withOrderBy(
            updatedScope,
            RepositoryScopeOrderByItem.builder().column("c2").direction(RepositoryScope.OrderByDirection.ASC).build()
        )
        val dataSource: RepositoryDataSource<CategoryOption?> =
            RepositoryDataSource<M?>(store, updatedScope2, childrenAppenders)
        dataSource.loadInitial(ItemKeyedDataSource.LoadInitialParams(null, 3, false), initialCallback!!)
        Mockito.verify(store).selectWhere("1", "c1 DESC, c2 ASC, _id ASC", 3)
        Mockito.verify(initialCallback).onResult(objects)
    }

    @Test
    fun get_after_page_objects_with_order_by_and_filter() {
        Mockito.`when`(key!!.toContentValues()).thenReturn(keyContentValues)
        Mockito.`when`(keyContentValues!!.getAsString("_id")).thenReturn("5")
        Mockito.`when`(keyContentValues.getAsString("code")).thenReturn("key-code")
        Mockito.`when`(keyContentValues.getAsString("name")).thenReturn("key-name")
        val filterScope = withFilterItem(
            emptyScope,
            RepositoryScopeFilterItem.builder().key("program").operator(FilterItemOperator.EQ).value("'uid'").build()
        )
        val updatedScope = withOrderBy(
            filterScope,
            RepositoryScopeOrderByItem.builder().column("code").direction(RepositoryScope.OrderByDirection.DESC).build()
        )
        val updatedScope2 = withOrderBy(
            updatedScope,
            RepositoryScopeOrderByItem.builder().column("name").direction(RepositoryScope.OrderByDirection.ASC).build()
        )
        val dataSource: RepositoryDataSource<CategoryOption?> =
            RepositoryDataSource<M?>(store, updatedScope2, childrenAppenders)
        dataSource.loadAfter(ItemKeyedDataSource.LoadParams(key, 3), initialCallback!!)
        Mockito.verify(store).selectWhere(
            "((code = 'key-code' AND name = 'key-name' AND _id > '5') OR " +
                "(code = 'key-code' AND name > 'key-name') OR " +
                "(code < 'key-code')) " +
                "AND program = 'uid'",
            "code DESC, name ASC, _id ASC",
            3
        )
        Mockito.verify(initialCallback).onResult(objects)
    }
}