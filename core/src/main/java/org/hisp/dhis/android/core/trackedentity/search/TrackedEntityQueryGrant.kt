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
package org.hisp.dhis.android.core.trackedentity.search

import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryMode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode

/**
 * A mandatory restriction carried by a [TrackedEntityInstanceQueryRepositoryScope].
 *
 * Tracker search uses a different scope mechanism from the rest of the SDK: its scope is a record of
 * *fields*, and `by*()` calls **replace** them rather than appending, so the append-only guarantee
 * that protects
 * [RepositoryScope][org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope] does not
 * hold here. A grant closes that gap by being re-applied on every repository construction — and
 * every `by*()` builds a new repository — so a caller's own narrowing can never outlive it.
 *
 * Instances come only from [ScopedD2][org.hisp.dhis.android.core.scopedaccess.ScopedD2].
 */
class TrackedEntityQueryGrant internal constructor(
    internal val programs: Set<String>?,
    internal val orgUnits: Set<String>?,
    internal val trackedEntityTypes: Set<String>?,
) {
    internal companion object {
        /**
         * A UID that cannot match any row, used to reduce an out-of-grant equality filter to an
         * empty result. DHIS2 UIDs are 11 alphanumeric characters starting with a letter, so this
         * is not a valid UID and cannot collide.
         */
        const val NO_MATCH = "__scope_denied__"
    }
}

/**
 * Re-applies the mandatory restriction to this scope, returning the scope that may actually be run.
 *
 * Called from the [TrackedEntitySearchOperators] constructor, so it runs on every repository the
 * fluent API produces. Program is handled separately, in
 * [TrackedEntityInstanceLocalQueryHelper]: the scope holds a single program value and cannot express
 * "one of these", so a multi-program grant becomes a sub-select at query time instead.
 */
internal fun TrackedEntityInstanceQueryRepositoryScope.applyGrant(): TrackedEntityInstanceQueryRepositoryScope {
    val grant = mandatory ?: return this

    val builder = toBuilder()
        // A scoped search is answered from the local database. The online modes query the server
        // directly with the user's credentials, which no local filter can bound.
        .mode(RepositoryMode.OFFLINE_ONLY)

    grant.programs?.let { granted ->
        if (program != null && program !in granted) {
            builder.program(TrackedEntityQueryGrant.NO_MATCH)
        }
    }

    grant.trackedEntityTypes?.let { granted ->
        if (trackedEntityType != null && trackedEntityType !in granted) {
            builder.trackedEntityType(TrackedEntityQueryGrant.NO_MATCH)
        }
    }

    grant.orgUnits?.let { granted ->
        val requested = orgUnits
        val effective = if (requested.isEmpty()) granted.toList() else requested.filter { it in granted }
        builder
            // `granted` is already expanded to leaves by the caller's scope resolver, so SELECTED is
            // exact. Leaving the caller's mode would let DESCENDANTS or ACCESSIBLE widen it again.
            .orgUnitMode(OrganisationUnitMode.SELECTED)
            .orgUnits(effective.ifEmpty { listOf(TrackedEntityQueryGrant.NO_MATCH) })
    }

    return builder.build()
}
