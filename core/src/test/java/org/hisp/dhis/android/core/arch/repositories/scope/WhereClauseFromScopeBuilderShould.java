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

package org.hisp.dhis.android.core.arch.repositories.scope;

import org.assertj.core.util.Lists;
import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.repositories.children.ChildrenSelection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(JUnit4.class)
public class WhereClauseFromScopeBuilderShould {

    @Mock
    private WhereClauseBuilder builder;

    private RepositoryScopeFilterItem eqItem = RepositoryScopeFilterItem.builder().key("k1").operator("=").value("v1").build();
    private RepositoryScopeFilterItem likeItem = RepositoryScopeFilterItem.builder().key("k2").operator("LIKE").value("v2").build();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void build_where_statement_for_equals_key_value() {
        WhereClauseFromScopeBuilder scopeBuilder = new WhereClauseFromScopeBuilder(builder);
        List<RepositoryScopeFilterItem> filterItems = Collections.singletonList(eqItem);
        scopeBuilder.getWhereClause(scopeForItems(filterItems));
        verify(builder).appendKeyOperatorValue(eqItem.key(), eqItem.operator(), eqItem.value());
        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }

    @Test
    public void build_where_statement_for_like_key_value() {
        WhereClauseFromScopeBuilder scopeBuilder = new WhereClauseFromScopeBuilder(builder);
        List<RepositoryScopeFilterItem> filterItems = Collections.singletonList(likeItem);
        scopeBuilder.getWhereClause(scopeForItems(filterItems));
        verify(builder).appendKeyOperatorValue(likeItem.key(), likeItem.operator(), likeItem.value());
        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }

    @Test
    public void build_where_statement_for_eq_and_like_key_value() {
        WhereClauseFromScopeBuilder scopeBuilder = new WhereClauseFromScopeBuilder(builder);
        List<RepositoryScopeFilterItem> filterItems = Lists.newArrayList(eqItem, likeItem);
        scopeBuilder.getWhereClause(scopeForItems(filterItems));
        verify(builder).appendKeyOperatorValue(eqItem.key(), eqItem.operator(), eqItem.value());
        verify(builder).appendKeyOperatorValue(likeItem.key(), likeItem.operator(), likeItem.value());
        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }

    private RepositoryScope scopeForItems(List<RepositoryScopeFilterItem> items) {
        return RepositoryScope.builder().filters(items).children(ChildrenSelection.empty()).build();
    }
}