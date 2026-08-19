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
package org.hisp.dhis.android.core.scopedaccess.internal

import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.scopedaccess.D2DataScope
import org.hisp.dhis.android.core.scopedaccess.OrgUnitScope
import org.hisp.dhis.android.core.scopedaccess.UidScope

/**
 * Turns the declarative parts of a [D2DataScope] into the concrete UID sets the repository filters
 * need.
 *
 * Three of the grant's dimensions cannot be expressed as a filter directly:
 *
 *  - [OrgUnitScope.Only] with `DESCENDANTS`/`CHILDREN` names roots, but `byOrganisationUnitUid()`
 *    wants leaves;
 *  - [OrgUnitScope.Capture] names no UIDs at all;
 *  - a data set grant has to become the set of data elements belonging to those data sets, because
 *    `DataValue` has no data-set column.
 *
 * Each answer is computed once and cached for the life of the resolver, which is the life of the
 * [ScopedD2][org.hisp.dhis.android.core.scopedaccess.ScopedD2] that owns it. Callers that need to
 * pick up a metadata sync should obtain a fresh `ScopedD2`.
 */
@Suppress("TooManyFunctions")
internal class ScopeResolver(
    private val d2: D2,
    private val scope: D2DataScope,
) {

    private var readOrgUnitsResolved = false
    private var readOrgUnits: Set<String>? = null

    private var writeOrgUnitsResolved = false
    private var writeOrgUnits: Set<String>? = null

    private var readDataElementsResolved = false
    private var readDataElements: Set<String>? = null

    private var writeDataElementsResolved = false
    private var writeDataElements: Set<String>? = null

    /**
     * The organisation units readable under this grant, or null when unrestricted and no filter is
     * needed.
     */
    fun readableOrgUnits(): Set<String>? {
        if (!readOrgUnitsResolved) {
            readOrgUnits = resolveOrgUnits(scope.orgUnits)
            readOrgUnitsResolved = true
        }
        return readOrgUnits
    }

    /** The organisation units writable under this grant, or null when unrestricted. */
    fun writableOrgUnits(): Set<String>? {
        if (!writeOrgUnitsResolved) {
            val read = readableOrgUnits()
            val write = resolveOrgUnits(scope.writable.orgUnits)
            writeOrgUnits = intersect(read, write)
            writeOrgUnitsResolved = true
        }
        return writeOrgUnits
    }

    /**
     * The data elements readable under this grant, or null when unrestricted.
     *
     * Derived from [D2DataScope.dataSets] and then narrowed by an explicit
     * [D2DataScope.dataElements] if one was given.
     */
    fun readableDataElements(): Set<String>? {
        if (!readDataElementsResolved) {
            readDataElements = intersect(
                dataElementsOf(scope.dataSets),
                scope.dataElements.uidsOrNull(),
            )
            readDataElementsResolved = true
        }
        return readDataElements
    }

    /** The data elements writable under this grant, or null when unrestricted. */
    fun writableDataElements(): Set<String>? {
        if (!writeDataElementsResolved) {
            writeDataElements = intersect(
                readableDataElements(),
                dataElementsOf(scope.writableDataSets()),
            )
            writeDataElementsResolved = true
        }
        return writeDataElements
    }

    /** The programs an enrollment of [teiUid] belongs to. Used to place a TEI inside the grant. */
    fun programsOfTrackedEntity(teiUid: String): Set<String> =
        d2.enrollmentModule().enrollments()
            .byTrackedEntityInstance().eq(teiUid)
            .blockingGet()
            .mapNotNull { it.program() }
            .toSet()

    /** The program an event belongs to, or null if the event is unknown locally. */
    fun programOfEvent(eventUid: String): String? =
        d2.eventModule().events().uid(eventUid).blockingGet()?.program()

    /** The organisation unit an event belongs to, or null if the event is unknown locally. */
    fun orgUnitOfEvent(eventUid: String): String? =
        d2.eventModule().events().uid(eventUid).blockingGet()?.organisationUnit()

    private fun resolveOrgUnits(orgUnitScope: OrgUnitScope): Set<String>? = when (orgUnitScope) {
        is OrgUnitScope.All -> null
        is OrgUnitScope.None -> emptySet()
        is OrgUnitScope.Capture -> d2.organisationUnitModule().organisationUnits()
            .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
            .blockingGetUids()
            .toSet()

        is OrgUnitScope.Only -> when (orgUnitScope.mode) {
            OrganisationUnitMode.SELECTED -> orgUnitScope.uids
            else -> expandHierarchy(orgUnitScope.uids, orgUnitScope.mode)
        }
    }

    /**
     * Expands org unit roots to the units beneath them.
     *
     * `path` on an organisation unit is the slash-separated chain of ancestor UIDs ending in its
     * own, so a `LIKE %uid%` over `path` finds the whole sub-tree in one query. For
     * [OrganisationUnitMode.CHILDREN] the result is trimmed back to the roots and their immediate
     * children.
     */
    private fun expandHierarchy(roots: Set<String>, mode: OrganisationUnitMode): Set<String> {
        if (roots.isEmpty()) return emptySet()

        val descendants = roots.flatMapTo(mutableSetOf()) { root ->
            d2.organisationUnitModule().organisationUnits()
                .byPath().like(root)
                .blockingGetUids()
        }

        return if (mode == OrganisationUnitMode.CHILDREN) {
            val children = d2.organisationUnitModule().organisationUnits()
                .byParentUid().`in`(roots.toList())
                .blockingGetUids()
            roots + children
        } else {
            descendants + roots
        }
    }

    private fun dataElementsOf(dataSets: UidScope): Set<String>? {
        val uids = dataSets.uidsOrNull() ?: return null

        return if (uids.isEmpty()) {
            emptySet()
        } else {
            d2.dataSetModule().dataSets()
                .withDataSetElements()
                .byUid().`in`(uids.toList())
                .blockingGet()
                .flatMap { dataSet -> dataSet.dataSetElements().orEmpty() }
                .map { it.dataElement().uid() }
                .toSet()
        }
    }

    /** Null means "no restriction", so it is the identity of this intersection, not the zero. */
    private fun intersect(a: Set<String>?, b: Set<String>?): Set<String>? = when {
        a == null -> b
        b == null -> a
        else -> a intersect b
    }
}
