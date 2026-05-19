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

package org.hisp.dhis.android.core.program.internal

import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType

internal object ProgramParentUidsHelper {

    fun getAssignedOptionSetUids(
        attributes: List<TrackedEntityAttribute>?,
        programStages: List<ProgramStage>?,
    ): Set<String> {
        val uids = mutableSetOf<String>()
        attributes?.let { getOptionSetUidsForAttributes(uids, it) }
        programStages?.let { getOptionSetUidsForDataElements(uids, it) }
        return uids
    }

    private fun getOptionSetUidsForDataElements(uids: MutableSet<String>, programStages: List<ProgramStage>) {
        for (programStage in programStages) {
            val programStageDataElements = programStage.programStageDataElements() ?: continue
            for (programStageDataElement in programStageDataElements) {
                val dataElement = programStageDataElement.fullDataElement()
                dataElement?.optionSet()?.let { uids.add(it.uid()) }
            }
        }
    }

    private fun getOptionSetUidsForAttributes(uids: MutableSet<String>, attributes: List<TrackedEntityAttribute>) {
        for (attribute in attributes) {
            attribute.optionSet()?.let { uids.add(it.uid()) }
        }
    }

    fun getAssignedTrackedEntityUids(programs: List<Program>?): Set<String> {
        if (programs == null) return emptySet()
        val uids = mutableSetOf<String>()
        for (program in programs) {
            program.trackedEntityType()?.let { uids.add(it.uid()) }
        }
        return uids
    }

    fun getAssignedTrackedEntityAttributeUids(
        programs: List<Program>,
        types: List<TrackedEntityType>,
    ): Set<String> {
        val attributeUids = mutableSetOf<String>()
        for (program in programs) {
            program.programTrackedEntityAttributes()?.forEach { programAttribute ->
                programAttribute.trackedEntityAttribute()?.uid()?.let { attributeUids.add(it) }
            }
        }
        for (type in types) {
            type.trackedEntityTypeAttributes()?.forEach { attribute ->
                attribute.trackedEntityAttribute()?.uid()?.let { attributeUids.add(it) }
            }
        }
        return attributeUids
    }
}
