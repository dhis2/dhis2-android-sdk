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

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class UnwrappedEqInFilterConnectorShould {

    @Mock
    private BaseRepositoryFactory<BaseRepository> baseRepositoryFactory;

    private ArgumentCaptor<RepositoryScope> updatedRepositoryScope = ArgumentCaptor.forClass(RepositoryScope.class);

    private String key = "key";

    private UnwrappedEqInFilterConnector<BaseRepository> filterConnector;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        filterConnector = new UnwrappedEqInFilterConnector<>(baseRepositoryFactory, RepositoryScope.empty(), key);
    }

    @Test
    public void should_build_in_filter() {
        filterConnector.in("uid1", "uid2", "uid3");
        RepositoryScopeFilterItem expectedItem =
                RepositoryScopeFilterItem.builder().key(key).operator(FilterItemOperator.IN).value("(uid1, uid2, uid3)").build();

        verify(baseRepositoryFactory).updated(updatedRepositoryScope.capture());
        RepositoryScopeFilterItem item = updatedRepositoryScope.getValue().filters().get(0);

        assertThat(item).isEqualTo(expectedItem);
    }

    @Test
    public void get_value_should_parse_single_value() {
        String value = "uid";

        List<String> parsedList = UnwrappedEqInFilterConnector.getValueList(value);

        assertThat(parsedList.size()).isEqualTo(1);
        assertThat(parsedList.get(0)).isEqualTo(value);
    }

    @Test
    public void get_value_should_parse_list_value() {
        String value = "(uid1, uid2, uid3)";

        List<String> parsedList = UnwrappedEqInFilterConnector.getValueList(value);

        assertThat(parsedList.size()).isEqualTo(3);
        assertThat(parsedList.get(0)).isEqualTo("uid1");
        assertThat(parsedList.get(1)).isEqualTo("uid2");
        assertThat(parsedList.get(2)).isEqualTo("uid3");
    }

    @Test
    public void get_value_should_parse_empty_list() {
        String value = "()";

        List<String> parsedList = UnwrappedEqInFilterConnector.getValueList(value);

        assertThat(parsedList.size()).isEqualTo(0);
    }

}
