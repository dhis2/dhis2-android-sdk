package org.hisp.dhis.android.core.arch.repositories.scope;

import org.assertj.core.util.Lists;
import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
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

    private RepositoryScopeItem eqItem = RepositoryScopeItem.builder().key("k1").operator("eq").value("v1").build();
    private RepositoryScopeItem likeItem = RepositoryScopeItem.builder().key("k2").operator("like").value("v2").build();
    private RepositoryScopeItem unknownOperatorItem = RepositoryScopeItem.builder().key("k2").operator("unknown").value("v2").build();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void build_where_statement_for_equals_key_value() {
        WhereClauseFromScopeBuilder scopeBuilder = new WhereClauseFromScopeBuilder(builder);
        List<RepositoryScopeItem> scope = Collections.singletonList(eqItem);
        scopeBuilder.getWhereClause(scope);
        verify(builder).appendKeyStringValue(eqItem.key(), eqItem.value());
        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }

    @Test
    public void build_where_statement_for_like_key_value() {
        WhereClauseFromScopeBuilder scopeBuilder = new WhereClauseFromScopeBuilder(builder);
        List<RepositoryScopeItem> scope = Collections.singletonList(likeItem);
        scopeBuilder.getWhereClause(scope);
        verify(builder).appendKeyLikeStringValue(likeItem.key(), likeItem.value());
        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }

    @Test
    public void build_where_statement_for_eq_and_like_key_value() {
        WhereClauseFromScopeBuilder scopeBuilder = new WhereClauseFromScopeBuilder(builder);
        List<RepositoryScopeItem> scope = Lists.newArrayList(eqItem, likeItem);
        scopeBuilder.getWhereClause(scope);
        verify(builder).appendKeyStringValue(eqItem.key(), eqItem.value());
        verify(builder).appendKeyLikeStringValue(likeItem.key(), likeItem.value());
        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }

    @Test
    public void ignore_unknown_operators() {
        WhereClauseFromScopeBuilder scopeBuilder = new WhereClauseFromScopeBuilder(builder);
        List<RepositoryScopeItem> scope = Lists.newArrayList(unknownOperatorItem);
        scopeBuilder.getWhereClause(scope);
        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }
}