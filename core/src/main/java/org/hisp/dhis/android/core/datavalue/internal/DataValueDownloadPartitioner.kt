/*
 *  Copyright (c) 2004-2026, University of Oslo
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
package org.hisp.dhis.android.core.datavalue.internal

import org.hisp.dhis.android.core.arch.helpers.internal.UrlLengthHelper

/**
 * Splits the `/api/dataValueSets` download of a data set until every request stays within a bound
 * of potential values, so the server is never asked for an unbounded space.
 *
 * Dimensions are split in order: periods first, then attribute option combos, and organisation
 * units last, since descending the hierarchy is the most involved. The cost is multiplicative, so
 * the total number of requests is roughly the same whichever dimension gives way first; spending
 * the cheap ones first just leaves a smaller residual factor for the hierarchy to cover.
 *
 * Organisation units are not flattened into a list of leaves. A selection sent with `children=true`
 * covers a unit and all its descendants, so replacing a parent by its children would drop the
 * values of the parent itself. Descending one level therefore yields the parents no longer
 * expanded plus their children still expanded, which covers exactly the same space:
 * `subtree(R) = R plus the subtrees of its children`.
 */
internal object DataValueDownloadPartitioner {

    const val DEFAULT_MAX_POTENTIAL_VALUES = 100_000L

    private const val QUERY_WITHOUT_UIDS_LENGTH = (
        "dataValueSets?fields=dataElement,period,orgUnit,categoryOptionCombo,attributeOptionCombo,value," +
            "storedBy,created,lastUpdated,comment,followup,deleted&lastUpdated=0000-00-00T00:00:00.000" +
            "&dataSet=&period=&orgUnit=&attributeOptionCombo=&children=true&includeDeleted=true"
        ).length

    private const val DATA_SET_UIDS = 1

    private val maxUidsInUrl = UrlLengthHelper.getHowManyUidsFitInURL(QUERY_WITHOUT_UIDS_LENGTH)

    fun partition(estimate: DataValueDownloadEstimate): List<DataValuePartition> {
        return split(
            periodIds = estimate.periodIds,
            attributeOptionComboUids = estimate.attributeOptionComboUids,
            orgUnits = OrgUnitSelection(estimate.rootOrgUnitUids, includeDescendants = true),
            context = Context(estimate),
        )
    }

    fun requiresOrgUnitSplit(
        categoryOptionCombosPerCell: Int,
        assignedOrgUnitCount: Int,
        maxPotentialValues: Long = DEFAULT_MAX_POTENTIAL_VALUES,
    ): Boolean {
        return categoryOptionCombosPerCell.toLong() * assignedOrgUnitCount > maxPotentialValues
    }

    @Suppress("ReturnCount")
    private fun split(
        periodIds: List<String>,
        attributeOptionComboUids: List<String>,
        orgUnits: OrgUnitSelection,
        context: Context,
    ): List<DataValuePartition> {
        if (context.volume(periodIds, attributeOptionComboUids, orgUnits) == 0L) {
            return emptyList()
        }
        if (context.fits(periodIds, attributeOptionComboUids, orgUnits)) {
            return listOf(partitionOf(periodIds, attributeOptionComboUids, orgUnits))
        }
        if (periodIds.size > 1) {
            return halve(periodIds).flatMap { split(it, attributeOptionComboUids, orgUnits, context) }
        }
        if (attributeOptionComboUids.size > 1) {
            return halve(attributeOptionComboUids).flatMap { split(periodIds, it, orgUnits, context) }
        }
        if (orgUnits.uids.size > 1) {
            return halve(orgUnits.uids).flatMap {
                split(periodIds, attributeOptionComboUids, orgUnits.copy(uids = it), context)
            }
        }
        // A selection that cannot be descended either has no dimension left to split, so that means a single period, a
        // single attribute option combo and a single leaf organisation unit, and the url is never the constraint.
        return descend(orgUnits, context)
            ?.flatMap { split(periodIds, attributeOptionComboUids, it, context) }
            ?: listOf(partitionOf(periodIds, attributeOptionComboUids, orgUnits))
    }

    /**
     * Replaces a selection covering whole subtrees by the same space one level down: the units
     * themselves, no longer expanded, plus their children, still expanded. Returns null when the
     * selection is not expanded or has no children left, that is, when it cannot be descended.
     */
    @Suppress("ReturnCount")
    private fun descend(orgUnits: OrgUnitSelection, context: Context): List<OrgUnitSelection>? {
        if (!orgUnits.includeDescendants) {
            return null
        }
        val children = context.orgUnits.children(orgUnits.uids)
        if (children.isEmpty()) {
            return null
        }
        return listOf(
            orgUnits.copy(includeDescendants = false),
            OrgUnitSelection(children, includeDescendants = true),
        )
    }

    private fun partitionOf(
        periodIds: List<String>,
        attributeOptionComboUids: List<String>,
        orgUnits: OrgUnitSelection,
    ): DataValuePartition {
        return DataValuePartition(
            periodIds = periodIds,
            orgUnitUids = orgUnits.uids,
            includeDescendants = orgUnits.includeDescendants,
            attributeOptionComboUids = attributeOptionComboUids.takeIf { it.isNotEmpty() },
        )
    }

    private fun <T> halve(values: List<T>): List<List<T>> {
        val half = values.size / 2
        return listOf(values.subList(0, half), values.subList(half, values.size))
    }

    private data class OrgUnitSelection(val uids: List<String>, val includeDescendants: Boolean)

    private class Context(private val estimate: DataValueDownloadEstimate) {
        private val maxPotentialValues: Long get() = estimate.maxPotentialValues

        val orgUnits: DataValueOrgUnits get() = estimate.orgUnits

        fun fits(
            periodIds: List<String>,
            attributeOptionComboUids: List<String>,
            orgUnits: OrgUnitSelection,
        ): Boolean {
            return volume(periodIds, attributeOptionComboUids, orgUnits) <= maxPotentialValues &&
                urlFits(periodIds, attributeOptionComboUids, orgUnits)
        }

        fun volume(
            periodIds: List<String>,
            attributeOptionComboUids: List<String>,
            orgUnits: OrgUnitSelection,
        ): Long {
            return periodIds.size.toLong() *
                this.orgUnits.assignedCount(orgUnits.uids, orgUnits.includeDescendants) *
                estimate.categoryOptionCombosPerCell *
                attributeOptionComboUids.size.coerceAtLeast(1)
        }

        private fun urlFits(
            periodIds: List<String>,
            attributeOptionComboUids: List<String>,
            orgUnits: OrgUnitSelection,
        ): Boolean {
            val uids = DATA_SET_UIDS + periodIds.size + orgUnits.uids.size + attributeOptionComboUids.size
            return uids <= maxUidsInUrl
        }
    }
}
