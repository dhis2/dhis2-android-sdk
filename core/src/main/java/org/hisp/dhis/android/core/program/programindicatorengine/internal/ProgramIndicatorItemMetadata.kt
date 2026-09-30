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

package org.hisp.dhis.android.core.program.programindicatorengine.internal

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.mapByUid
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute

/**
 * Metadata read by the program indicator expression items, loaded before the expression is visited.
 * The ANTLR visitor is not suspending, so without it every item reads the store from a nested
 * blocking call.
 */
internal data class ProgramIndicatorItemMetadata(
    val dataElements: Map<String, DataElement> = emptyMap(),
    val trackedEntityAttributes: Map<String, TrackedEntityAttribute> = emptyMap(),
    val programStages: Map<String, ProgramStage> = emptyMap(),
) {
    companion object {
        private val stageDataElementPattern = Regex("""#\{\s*\w+\s*\.\s*(\w+)""")
        private val attributePattern = Regex("""A\{\s*(\w+)""")
        private const val PROGRAM_STAGE_NAME = "program_stage_name"

        suspend fun load(
            expressions: List<String?>,
            dataElementStore: IdentifiableObjectStore<DataElement>,
            trackedEntityAttributeStore: IdentifiableObjectStore<TrackedEntityAttribute>,
            programStageStore: IdentifiableObjectStore<ProgramStage>? = null,
            programStageUids: Collection<String> = emptyList(),
        ): ProgramIndicatorItemMetadata {
            val text = expressions.filterNotNull().joinToString(" ")
            val dataElementUids = uidsMatching(stageDataElementPattern, text)
            val attributeUids = uidsMatching(attributePattern, text)
            val stageUids = programStageUids.takeIf { text.contains(PROGRAM_STAGE_NAME) }.orEmpty().toList()

            return ProgramIndicatorItemMetadata(
                dataElements = selectByUids(dataElementStore, dataElementUids),
                trackedEntityAttributes = selectByUids(trackedEntityAttributeStore, attributeUids),
                programStages = programStageStore?.let { selectByUids(it, stageUids) }.orEmpty(),
            )
        }

        fun expressionsOf(programIndicator: ProgramIndicator): List<String?> {
            return listOf(programIndicator.expression(), programIndicator.filter()) +
                programIndicator.analyticsPeriodBoundaries().orEmpty().map { it.boundaryTarget() }
        }

        private fun uidsMatching(pattern: Regex, text: String): List<String> {
            return pattern.findAll(text).map { it.groupValues[1] }.distinct().toList()
        }

        private suspend fun <O : ObjectWithUidInterface> selectByUids(
            store: IdentifiableObjectStore<O>,
            uids: List<String>,
        ): Map<String, O> {
            return if (uids.isEmpty()) emptyMap() else mapByUid(store.selectByUids(uids))
        }
    }
}
