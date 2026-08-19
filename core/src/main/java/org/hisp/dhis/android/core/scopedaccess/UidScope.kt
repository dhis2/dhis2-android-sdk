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
package org.hisp.dhis.android.core.scopedaccess

/**
 * A restriction over a set of metadata UIDs, used to build a [D2DataScope].
 *
 * The three cases are deliberately distinct: [None] and `Only(emptySet())` both permit nothing,
 * while [All] applies no restriction at all. [uidsOrNull] returns `null` precisely for [All], which
 * is how callers know that no filter needs to be applied.
 */
sealed interface UidScope {

    /** No restriction — every UID of the relevant type is in scope. */
    data object All : UidScope

    /** Nothing is in scope. This is the default for every dimension of a [D2DataScope]. */
    data object None : UidScope

    /** Only the listed [uids] are in scope. */
    data class Only(val uids: Set<String>) : UidScope

    /** True if [uid] falls inside this scope. A null [uid] is never in scope. */
    fun allows(uid: String?): Boolean = when (this) {
        is All -> uid != null
        is None -> false
        is Only -> uid != null && uid in uids
    }

    /** True if this scope permits nothing, either because it is [None] or an empty [Only]. */
    fun isEmpty(): Boolean = when (this) {
        is All -> false
        is None -> true
        is Only -> uids.isEmpty()
    }

    /**
     * The explicit UID set this scope narrows to, or null when it does not narrow ([All]) and no
     * filter is therefore required.
     */
    fun uidsOrNull(): Set<String>? = when (this) {
        is All -> null
        is None -> emptySet()
        is Only -> uids
    }

    /**
     * The intersection of this scope with [other]. Intersection is how a grant is combined with a
     * caller-supplied narrowing: it can only ever shrink.
     */
    fun intersect(other: UidScope): UidScope = when {
        this is All -> other
        other is All -> this
        this is None || other is None -> None
        else -> Only((this as Only).uids intersect (other as Only).uids)
    }

    companion object {
        /** [Only] over [uids], or [None] when empty. */
        @JvmStatic
        fun of(uids: Collection<String>): UidScope = if (uids.isEmpty()) None else Only(uids.toSet())

        /** [Only] over [uids], or [None] when empty. */
        @JvmStatic
        fun of(vararg uids: String): UidScope = of(uids.toList())
    }
}
