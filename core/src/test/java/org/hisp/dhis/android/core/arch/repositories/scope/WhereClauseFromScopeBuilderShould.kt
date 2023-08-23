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
package org.hisp.dhis.android.core.arch.repositories.scope

import com.google.common.collect.Lists
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.arch.repositories.scope.internal.WhereClauseFromScopeBuilder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WhereClauseFromScopeBuilderShould {
    private val builder: WhereClauseBuilder = mock()

    private val eqItem =
        RepositoryScopeFilterItem.builder().key("k1").operator(FilterItemOperator.EQ).value("v1").build()
    private val likeItem =
        RepositoryScopeFilterItem.builder().key("k2").operator(FilterItemOperator.LIKE).value("v2").build()

    @Test
    fun build_where_statement_for_equals_key_value() {
        val scopeBuilder = WhereClauseFromScopeBuilder(builder)
        val filterItems = listOf(eqItem)
        whenever(builder.build()).doReturn("1")

        scopeBuilder.getWhereClause(scopeForItems(filterItems))
        verify(builder).appendKeyOperatorValue(eqItem.key(), eqItem.operator().sqlOperator, eqItem.value())
        verify(builder).build()
        verifyNoMoreInteractions(builder)
    }

    @Test
    fun build_where_statement_for_like_key_value() {
        val scopeBuilder = WhereClauseFromScopeBuilder(builder)
        val filterItems = listOf(likeItem)
        whenever(builder.build()).doReturn("1")

        scopeBuilder.getWhereClause(scopeForItems(filterItems))
        verify(builder)
            .appendKeyOperatorValue(likeItem.key(), likeItem.operator().sqlOperator, likeItem.value())
        verify(builder).build()
        verifyNoMoreInteractions(builder)
    }

    @Test
    fun build_where_statement_for_eq_and_like_key_value() {
        val scopeBuilder = WhereClauseFromScopeBuilder(builder)
        val filterItems: List<RepositoryScopeFilterItem> = Lists.newArrayList(eqItem, likeItem)
        whenever(builder.build()).doReturn("1")

        scopeBuilder.getWhereClause(scopeForItems(filterItems))
        verify(builder).appendKeyOperatorValue(eqItem.key(), eqItem.operator().sqlOperator, eqItem.value())
        verify(builder)
            .appendKeyOperatorValue(likeItem.key(), likeItem.operator().sqlOperator, likeItem.value())
        verify(builder).build()
        verifyNoMoreInteractions(builder)
    }

    @Test
    fun build_where_statement_when_no_filters_on_empty_builder() {
        val scopeBuilder = WhereClauseFromScopeBuilder(builder)
        whenever(builder.isEmpty).doReturn(true)
        whenever(builder.build()).doReturn("1")

        val result = scopeBuilder.getWhereClause(scopeForItems(emptyList()))
        assertThat(result).isEqualTo("1")
        verify(builder).isEmpty
        verifyNoMoreInteractions(builder)
    }

    @Test
    fun build_where_statement_when_no_filters_on_non_empty_builder() {
        val scopeBuilder = WhereClauseFromScopeBuilder(builder)
        whenever(builder.isEmpty).doReturn(false)
        whenever(builder.build()).doReturn("1")

        scopeBuilder.getWhereClause(scopeForItems(emptyList()))
        verify(builder).isEmpty
        verify(builder).build()
        verifyNoMoreInteractions(builder)
    }

    private fun scopeForItems(items: List<RepositoryScopeFilterItem>): RepositoryScope {
        return RepositoryScope.empty().toBuilder().filters(items).build()
    }
}
