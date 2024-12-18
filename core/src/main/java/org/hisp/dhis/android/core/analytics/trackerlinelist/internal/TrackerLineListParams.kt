/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.analytics.trackerlinelist.internal

import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.arch.repositories.paging.PageConfig
import org.hisp.dhis.android.core.util.replaceOrPush

internal data class TrackerLineListParams(
    val trackerVisualization: String?,
    val outputType: TrackerLineListOutputType?,
    val programId: String?,
    val programStageId: String?,
    val trackedEntityTypeId: String?,
    val columns: List<TrackerLineListItem>,
    val filters: List<TrackerLineListItem>,
    val pageConfig: PageConfig = DefaultPaging,
) {
    val allItems = columns + filters

    operator fun plus(other: TrackerLineListParams): TrackerLineListParams {
        return copy(
            outputType = other.outputType ?: outputType,
            programId = other.programId ?: programId,
            programStageId = other.programStageId ?: programStageId,
        ).run {
            other.columns.fold(this) { params, item -> params.updateInColumns(item) }
        }.run {
            other.filters.fold(this) { params, item -> params.updateInFilters(item) }
        }
    }

    fun updateInColumns(item: TrackerLineListItem): TrackerLineListParams {
        return copy(
            columns = columns.replaceOrPush(item) { it.id == item.id },
            filters = filters.filterNot { it.id == item.id },
        )
    }

    fun updateInFilters(item: TrackerLineListItem): TrackerLineListParams {
        return copy(
            columns = columns.filterNot { it.id == item.id },
            filters = filters.replaceOrPush(item) { it.id == item.id },
        )
    }

    fun hasOrgunit(): Boolean {
        return (columns + filters).any { it is TrackerLineListItem.OrganisationUnitItem }
    }

    fun flattenRepeatedDataElements(): TrackerLineListParams {
        return this.copy(
            columns = flattenRepeatedDataElements(this.columns),
            filters = flattenRepeatedDataElements(this.filters),
        )
    }

    private fun flattenRepeatedDataElements(items: List<TrackerLineListItem>): List<TrackerLineListItem> {
        return items.map { item ->
            when (item) {
                is TrackerLineListItem.ProgramDataElement -> flattenDataElement(item)
                else -> listOf(item)
            }
        }.flatten()
    }

    private fun flattenDataElement(item: TrackerLineListItem.ProgramDataElement): List<TrackerLineListItem> {
        return if (item.repetitionIndexes.isNullOrEmpty()) {
            listOf(item)
        } else {
            sortIndexes(item.repetitionIndexes).map { idx -> item.copy(repetitionIndexes = listOf(idx)) }
        }
    }

    private fun sortIndexes(indexes: List<Int>): List<Int> {
        val (positive, negativeOrZero) = indexes.sorted().partition { it > 0 }
        return positive + negativeOrZero
    }

    companion object {
        val DefaultPaging = PageConfig.Paging(1, 500)
    }
}
