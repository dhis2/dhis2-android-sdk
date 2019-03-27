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

import org.hisp.dhis.android.core.arch.repositories.paging.RepositoryDataSource;
import org.hisp.dhis.android.core.arch.repositories.paging.RepositoryPagingConfig;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeHelper;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeOrderByItem;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import androidx.paging.ItemKeyedDataSource;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RepositoryPagingShould  {

    private RepositoryScope emptyScope = RepositoryScope.empty();

    @Mock
    private IdentifiableObjectStore<CategoryOption> store;

    @Mock
    private List<CategoryOption> objects;

    @Mock
    private ItemKeyedDataSource.LoadInitialCallback<CategoryOption> initialCallback;

    @Mock
    private ItemKeyedDataSource.LoadCallback<CategoryOption> pageCallback;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(store.selectWhere(anyString(), anyString(), anyInt())).thenReturn(objects);
    }

    @Test
    public void get_initial_page_objects_without_order_by() {
        RepositoryDataSource<CategoryOption> dataSource = new RepositoryDataSource<>(store, emptyScope);
        dataSource.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, 3, false), initialCallback);
        verify(store).selectWhere("1", "_id ASC", 3);
        verify(initialCallback).onResult(objects);
    }

    @Test
    public void get_initial_page_objects_with_order_by() {
        RepositoryScope updatedScope = RepositoryScopeHelper.withOrderBy(emptyScope,
                RepositoryScopeOrderByItem.builder().column("name").direction(RepositoryScope.OrderByDirection.DESC).build());
        RepositoryDataSource<CategoryOption> dataSource = new RepositoryDataSource<>(store, updatedScope);
        dataSource.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, 3, false), initialCallback);
        verify(store).selectWhere("1", "name DESC, _id ASC", 3);
        verify(initialCallback).onResult(objects);
    }

    @Test
    public void get_initial_page_objects_with_forced_oder_by_paging_key_asc() {
        RepositoryScope updatedScope = RepositoryScopeHelper.withOrderBy(emptyScope,
                RepositoryScopeOrderByItem.builder().column(RepositoryPagingConfig.PAGING_KEY).direction(RepositoryScope.OrderByDirection.ASC).build());
        RepositoryDataSource<CategoryOption> dataSource = new RepositoryDataSource<>(store, updatedScope);
        dataSource.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, 3, false), initialCallback);
        verify(store).selectWhere("1", "_id ASC", 3);
        verify(initialCallback).onResult(objects);
    }

    @Test
    public void get_initial_page_objects_with_forced_oder_by_paging_key_desc() {
        RepositoryScope updatedScope = RepositoryScopeHelper.withOrderBy(emptyScope,
                RepositoryScopeOrderByItem.builder().column(RepositoryPagingConfig.PAGING_KEY).direction(RepositoryScope.OrderByDirection.DESC).build());
        RepositoryDataSource<CategoryOption> dataSource = new RepositoryDataSource<>(store, updatedScope);
        dataSource.loadInitial(new ItemKeyedDataSource.LoadInitialParams<>(null, 3, false), initialCallback);
        verify(store).selectWhere("1", "_id DESC", 3);
        verify(initialCallback).onResult(objects);
    }

    /*@Test
    public void lalala() {
        RepositoryScopeHelper.withOrderBy(emptyScope, RepositoryScopeOrderByItem.builder().column("name").direction(RepositoryScope.OrderByDirection.DESC).build());
        RepositoryDataSource<CategoryOption> dataSource = new RepositoryDataSource<>(store, emptyScope);
        dataSource.loadInitial(new ItemKeyedDataSource.LoadParams<>(null, 3, false), initialCallback);
        verify(store).selectWhere("1", "_id ASC", 3);
        verify(initialCallback).onResult(objects);
    }

    @Test
    public void get_after_page_objects_from_repository() {
        RepositoryScopeHelper.withOrderBy(emptyScope, RepositoryScopeOrderByItem.builder().column("name").direction(RepositoryScope.OrderByDirection.DESC).build());
        RepositoryDataSource<CategoryOption> dataSource = new RepositoryDataSource<>(store, emptyScope);
        dataSource.loadInitial(new ItemKeyedDataSource.LoadParams<>(null, 3, false), initialCallback);
        verify(store).selectWhere("1", "_id ASC", 3);
        verify(initialCallback).onResult(objects);
    }

    /*@Test
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
    }*/
}