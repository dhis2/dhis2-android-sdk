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

package org.hisp.dhis.android.core.arch.repositories.filters.internal;

import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class BooleanFilterConnectorShould {

    @Mock
    private BaseRepositoryFactory<BaseRepository> baseRepositoryFactory;

    private ArgumentCaptor<RepositoryScope> updatedRepositoryScope = ArgumentCaptor.forClass(RepositoryScope.class);

    private String key = "key";

    private BooleanFilterConnector<BaseRepository> filterConnector;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        filterConnector = new BooleanFilterConnector<>(baseRepositoryFactory, RepositoryScope.empty(), key);
    }

    @Test
    public void should_build_filter_item_on_false() {
        filterConnector.eq(false);
        RepositoryScopeFilterItem expectedItem =
                RepositoryScopeFilterItem.builder().key(key).operator(FilterItemOperator.EQ).value("0").build();

        verify(baseRepositoryFactory).updated(updatedRepositoryScope.capture());
        RepositoryScopeFilterItem item = updatedRepositoryScope.getValue().filters().get(0);

        assertThat(item).isEqualTo(expectedItem);
    }

    @Test
    public void should_build_filter_item_on_true() {
        filterConnector.eq(true);
        RepositoryScopeFilterItem expectedItem =
                RepositoryScopeFilterItem.builder().key(key).operator(FilterItemOperator.EQ).value("1").build();

        verify(baseRepositoryFactory).updated(updatedRepositoryScope.capture());
        RepositoryScopeFilterItem item = updatedRepositoryScope.getValue().filters().get(0);

        assertThat(item).isEqualTo(expectedItem);
    }
}
