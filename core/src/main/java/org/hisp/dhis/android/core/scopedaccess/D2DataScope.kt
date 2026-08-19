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
 * The subset of the local database a [ScopedD2] may reach.
 *
 * A scope is a *grant*: it is authored by whoever owns the trust decision — a server administrator,
 * a host application — and handed to the code being restricted, which cannot widen it. Every
 * dimension defaults to the closed value, so an empty `D2DataScope()` grants nothing.
 *
 * Read and write access are separate. [writable] never widens the read scope; a UID must be in both
 * to be writable, so the common "read this program, write nothing" case is the default.
 *
 * ```kotlin
 * val scope = D2DataScope(
 *     programs = UidScope.of("IpHINAT79UW"),
 *     orgUnits = OrgUnitScope.of(listOf("O6uvpzGd5pu")),
 *     capabilities = setOf(D2Capability.READ_METADATA, D2Capability.READ_TRACKED_ENTITY),
 * )
 * val sdk = d2.scopedTo(scope)
 * ```
 */
data class D2DataScope(
    /** Programs whose tracked entities, enrollments and events are readable. */
    val programs: UidScope = UidScope.None,
    /** Data sets whose data values are readable. */
    val dataSets: UidScope = UidScope.None,
    /** Tracked entity types that are readable. Defaults to [UidScope.All]: [programs] already bounds
     *  the tracker data, and narrowing types further is rarely needed. */
    val trackedEntityTypes: UidScope = UidScope.All,
    /** Data elements that are readable, within the bound already set by [programs] and [dataSets]. */
    val dataElements: UidScope = UidScope.All,
    /** Organisation units whose data is readable. */
    val orgUnits: OrgUnitScope = OrgUnitScope.None,
    /** The subset of the above that may also be written. Always intersected with the read scope. */
    val writable: WritableScope = WritableScope.NONE,
    /** Feature areas this scope unlocks. Empty means nothing is exposed. */
    val capabilities: Set<D2Capability> = emptySet(),
) {

    /** True if [capability] is granted. */
    fun has(capability: D2Capability): Boolean = capability in capabilities

    /** True if any write capability is granted and something is actually writable. */
    fun hasAnyWrite(): Boolean =
        capabilities.any { it.isWrite() } && !(writable.programs.isEmpty() && writable.dataSets.isEmpty())

    /** Programs that may be written: the read grant intersected with [WritableScope.programs]. */
    fun writablePrograms(): UidScope = programs.intersect(writable.programs)

    /** Data sets that may be written: the read grant intersected with [WritableScope.dataSets]. */
    fun writableDataSets(): UidScope = dataSets.intersect(writable.dataSets)

    companion object {
        /** A scope that grants nothing. */
        @JvmField
        val NONE: D2DataScope = D2DataScope()
    }
}

/**
 * The writable subset of a [D2DataScope].
 *
 * Every field is intersected with the corresponding read field, so this can only ever restrict.
 * Listing a UID here that the read scope does not grant has no effect.
 */
data class WritableScope(
    val programs: UidScope = UidScope.None,
    val dataSets: UidScope = UidScope.None,
    val orgUnits: OrgUnitScope = OrgUnitScope.None,
) {
    companion object {
        /** Read-only: nothing may be written. */
        @JvmField
        val NONE: WritableScope = WritableScope()
    }
}
