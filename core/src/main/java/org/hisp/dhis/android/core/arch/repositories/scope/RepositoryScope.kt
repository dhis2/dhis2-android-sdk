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

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenSelection
import org.hisp.dhis.android.core.arch.repositories.scope.internal.AccessGuard
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeComplexFilterItem
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeOrderByItem

@ConsistentCopyVisibility
@ModelBuilder
data class RepositoryScope internal constructor(
    val filters: List<RepositoryScopeFilterItem>,
    val complexFilters: List<RepositoryScopeComplexFilterItem>,
    internal val orderBy: List<RepositoryScopeOrderByItem>,
    internal val children: ChildrenSelection,
    /**
     * Vetoes out-of-scope writes, or null for an unrestricted scope (the default, and the case for
     * every repository obtained straight from [D2][org.hisp.dhis.android.core.D2]).
     *
     * Set only by [ScopedD2][org.hisp.dhis.android.core.scopedaccess.ScopedD2]. The generated
     * builder setter is `internal` and copy-on-write carries the guard through every filter call,
     * so restricted code can neither install nor remove one.
     */
    internal val accessGuard: AccessGuard? = null,
) {
    enum class OrderByDirection(val api: String) {
        ASC("asc"),
        DESC("desc"),
    }

    fun filters(): List<RepositoryScopeFilterItem> = filters
    fun complexFilters(): List<RepositoryScopeComplexFilterItem> = complexFilters
    internal fun orderBy(): List<RepositoryScopeOrderByItem> = orderBy
    internal fun children(): ChildrenSelection = children
    internal fun accessGuard(): AccessGuard? = accessGuard

    fun hasFilters(): Boolean = filters.isNotEmpty() || complexFilters.isNotEmpty()

    fun toBuilder(): Builder = RepositoryScopeBuilder.from(this)

    class Builder : RepositoryScopeBuilder()

    companion object {
        @JvmStatic
        fun empty(): RepositoryScope = builder()
            .children(ChildrenSelection.empty())
            .filters(emptyList())
            .complexFilters(emptyList())
            .orderBy(emptyList())
            .build()

        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
