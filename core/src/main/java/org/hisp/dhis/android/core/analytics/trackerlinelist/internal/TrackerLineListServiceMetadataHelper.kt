/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.trackerlinelist.TrackerLineListItem
import org.hisp.dhis.android.core.category.internal.CategoryStore
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorStore
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackerLineListServiceMetadataHelper(
    private val trackedEntityAttributeStore: TrackedEntityAttributeStore,
    private val dataElementStore: DataElementStore,
    private val programIndicatorStore: ProgramIndicatorStore,
    private val programStore: ProgramStore,
    private val programStageStore: ProgramStageStore,
    private val categoryStore: CategoryStore,
) {

    fun getMetadata(params: TrackerLineListParams): Map<String, MetadataItem> {
        val metadata: MutableMap<String, MetadataItem> = mutableMapOf()

        (params.columns + params.filters).forEach { item ->
            metadata += getMetadata(item)
        }

        return metadata
    }

    private fun getMetadata(item: TrackerLineListItem): Map<String, MetadataItem> {
        val metadata: MutableMap<String, MetadataItem> = mutableMapOf()

        if (!metadata.containsKey(item.id)) {
            val metadataItems = when (item) {
                is TrackerLineListItem.ProgramAttribute -> getProgramAttributeItems(item)
                is TrackerLineListItem.ProgramDataElement -> getProgramDataElement(item)
                is TrackerLineListItem.ProgramIndicator -> getProgramIndicator(item)
                is TrackerLineListItem.Category -> getCategory(item)
                else -> emptyList()
            }
            val metadataItemsMap = metadataItems.associateBy { it.id }

            metadata += metadataItemsMap
        }

        return metadata
    }

    private fun getProgramAttributeItems(item: TrackerLineListItem.ProgramAttribute): List<MetadataItem> {
        val attribute = trackedEntityAttributeStore.selectByUid(item.uid)
            ?: throw AnalyticsException.InvalidTrackedEntityAttribute(item.uid)

        return listOf(
            MetadataItem.TrackedEntityAttributeItem(attribute),
        )
    }

    private fun getProgramIndicator(item: TrackerLineListItem.ProgramIndicator): List<MetadataItem> {
        val programIndicator = programIndicatorStore.selectByUid(item.uid)
            ?: throw AnalyticsException.InvalidProgramIndicator(item.uid)

        return listOf(
            MetadataItem.ProgramIndicatorItem(programIndicator),
        )
    }

    private fun getProgramDataElement(item: TrackerLineListItem.ProgramDataElement): List<MetadataItem> {
        val dataElement = dataElementStore.selectByUid(item.dataElement)
            ?.let { MetadataItem.DataElementItem(it) }
            ?: throw AnalyticsException.InvalidDataElement(item.dataElement)

        val programStage = getProgramStage(item.programStage)
            .let { MetadataItem.ProgramStageItem(it) }

        val program = programStage.item.program()?.uid()?.let { getProgram(it) }
            ?.let { MetadataItem.ProgramItem(it) }
            ?: throw AnalyticsException.InvalidArguments("ProgramStage ${programStage.item.uid()} has no program")

        return listOf(dataElement, program, programStage)
    }

    private fun getProgram(programId: String): Program {
        return programStore.selectByUid(programId)
            ?: throw AnalyticsException.InvalidProgram(programId)
    }

    private fun getProgramStage(programStageId: String): ProgramStage {
        return programStageStore.selectByUid(programStageId)
            ?: throw AnalyticsException.InvalidProgramStage(programStageId)
    }

    private fun getCategory(item: TrackerLineListItem.Category): List<MetadataItem> {
        val category = categoryStore.selectByUid(item.uid) ?: throw AnalyticsException.InvalidCategory(item.uid)
        return listOf(MetadataItem.CategoryItem(category))
    }
}
