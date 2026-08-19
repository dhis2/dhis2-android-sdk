/*
 *  Copyright (c) 2004-2025, University of Oslo
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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.repositories.scope.internal.AccessGuard
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeComplexFilterItem
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeOrderByItem
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock

/**
 * The invariants that make [org.hisp.dhis.android.core.scopedaccess.ScopedD2] safe.
 *
 * A pre-narrowed repository is only unforgeable because a scope cannot be widened once built. These
 * tests pin that property down directly, rather than through a repository, so a regression shows up
 * here as the cause rather than somewhere downstream as a symptom.
 */
@RunWith(JUnit4::class)
class AccessGuardPropagationShould {

    private val guard: AccessGuard = mock()

    private val mandatoryFilter = RepositoryScopeFilterItem.builder()
        .key("program").operator(FilterItemOperator.IN).value("('granted')").build()

    private val callerFilter = RepositoryScopeFilterItem.builder()
        .key("program").operator(FilterItemOperator.EQ).value("'other'").build()

    private val guardedScope = RepositoryScope.empty().toBuilder().accessGuard(guard).build()

    @Test
    fun keep_the_guard_when_a_filter_is_added() {
        val narrowed = RepositoryScopeHelper.withFilterItem(guardedScope, callerFilter)

        assertThat(narrowed.accessGuard()).isSameInstanceAs(guard)
    }

    @Test
    fun keep_the_guard_through_a_long_chain_of_filters() {
        val narrowed = (1..10).fold(guardedScope) { scope, index ->
            RepositoryScopeHelper.withFilterItem(
                scope,
                RepositoryScopeFilterItem.builder()
                    .key("k$index").operator(FilterItemOperator.EQ).value("'v$index'").build(),
            )
        }

        assertThat(narrowed.accessGuard()).isSameInstanceAs(guard)
        assertThat(narrowed.filters()).hasSize(10)
    }

    @Test
    fun keep_the_guard_when_a_complex_filter_is_added() {
        val narrowed = RepositoryScopeHelper.withComplexFilterItem(
            guardedScope,
            RepositoryScopeComplexFilterItem("uid IN (SELECT uid FROM Whatever)"),
        )

        assertThat(narrowed.accessGuard()).isSameInstanceAs(guard)
    }

    @Test
    fun keep_the_guard_when_ordering_changes() {
        val narrowed = RepositoryScopeHelper.withOrderBy(
            guardedScope,
            RepositoryScopeOrderByItem.builder()
                .column("name").direction(RepositoryScope.OrderByDirection.ASC).build(),
        )

        assertThat(narrowed.accessGuard()).isSameInstanceAs(guard)
    }

    @Test
    fun accumulate_filters_rather_than_replace_them() {
        // The property the whole design rests on: a caller filtering the same column the grant
        // already filtered gets the intersection, never a replacement.
        val granted = RepositoryScopeHelper.withFilterItem(guardedScope, mandatoryFilter)
        val narrowed = RepositoryScopeHelper.withFilterItem(granted, callerFilter)

        assertThat(narrowed.filters()).hasSize(2)
        assertThat(narrowed.filters()).containsExactly(mandatoryFilter, callerFilter).inOrder()
    }

    @Test
    fun have_no_guard_on_an_ordinary_scope() {
        // Repositories obtained straight from D2 must be unaffected by any of this.
        assertThat(RepositoryScope.empty().accessGuard()).isNull()
    }
}
