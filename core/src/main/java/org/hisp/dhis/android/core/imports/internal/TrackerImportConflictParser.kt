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
package org.hisp.dhis.android.core.imports.internal

import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.imports.TrackerImportConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.BadAttributePatternConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.EnrollmentHasInvalidProgramConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.EnrollmentNotFoundConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.EventHasInvalidProgramConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.EventHasInvalidProgramStageConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.EventNotFoundConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.FileResourceAlreadyAssignedConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.FileResourceReferenceNotFoundConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.InvalidAttributeValueTypeConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.InvalidDataValueConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.InvalidTrackedEntityTypeConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.LackingEnrollmentCascadeDeleteAuthorityConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.LackingTEICascadeDeleteAuthorityConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.MissingAttributeConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.MissingDataElementConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.NonUniqueAttributeConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.TrackedEntityInstanceNotFoundConflict
import org.hisp.dhis.android.core.imports.internal.conflicts.TrackerImportConflictItem
import org.hisp.dhis.android.core.imports.internal.conflicts.TrackerImportConflictItemContext
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueCollectionRepository
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueCollectionRepository
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackerImportConflictParser(
    attributeStore: TrackedEntityAttributeStore,
    dataElementStore: DataElementStore,
    private val trackedEntityAttributeValueRepository: TrackedEntityAttributeValueCollectionRepository,
    private val trackedEntityInstanceDataValueRepository: TrackedEntityDataValueCollectionRepository,
) {

    private val context = TrackerImportConflictItemContext(attributeStore, dataElementStore)

    private val commonConflicts = listOf(
        LackingEnrollmentCascadeDeleteAuthorityConflict,
        LackingTEICascadeDeleteAuthorityConflict,
        TrackedEntityInstanceNotFoundConflict,
        EventNotFoundConflict,
        EventHasInvalidProgramConflict,
        EventHasInvalidProgramStageConflict,
        EnrollmentNotFoundConflict,
        EnrollmentHasInvalidProgramConflict,
        FileResourceAlreadyAssignedConflict,
        FileResourceReferenceNotFoundConflict,
    )

    private val trackedEntityInstanceConflicts: List<TrackerImportConflictItem> = commonConflicts + listOf(
        InvalidAttributeValueTypeConflict,
        MissingAttributeConflict,
        BadAttributePatternConflict,
        NonUniqueAttributeConflict,
        InvalidTrackedEntityTypeConflict,
    )

    private val enrollmentConflicts: List<TrackerImportConflictItem> = commonConflicts + listOf(
        InvalidAttributeValueTypeConflict,
        MissingAttributeConflict,
        BadAttributePatternConflict,
        NonUniqueAttributeConflict,
    )

    private val eventConflicts: List<TrackerImportConflictItem> = commonConflicts + listOf(
        InvalidDataValueConflict,
        MissingDataElementConflict,
    )

    suspend fun getTrackedEntityInstanceConflict(
        conflict: ImportConflict,
        conflictBuilder: TrackerImportConflict.Builder,
    ): TrackerImportConflict {
        return evaluateConflicts(conflict, conflictBuilder, trackedEntityInstanceConflicts)
    }

    suspend fun getEnrollmentConflict(
        conflict: ImportConflict,
        conflictBuilder: TrackerImportConflict.Builder,
    ): TrackerImportConflict {
        return evaluateConflicts(conflict, conflictBuilder, enrollmentConflicts)
    }

    suspend fun getEventConflict(
        conflict: ImportConflict,
        conflictBuilder: TrackerImportConflict.Builder,
    ): TrackerImportConflict {
        return evaluateConflicts(conflict, conflictBuilder, eventConflicts)
    }

    private suspend fun evaluateConflicts(
        conflict: ImportConflict,
        conflictBuilder: TrackerImportConflict.Builder,
        conflictTypes: List<TrackerImportConflictItem>,
    ): TrackerImportConflict {
        val conflictType = conflictTypes.find { it.matches(conflict) }

        if (conflictType != null) {
            conflictBuilder
                .errorCode(conflictType.errorCode)
                .displayDescription(conflictType.getDisplayDescription(conflict, context))
                .trackedEntityAttribute(conflictType.getTrackedEntityAttribute(conflict))
                .dataElement(conflictType.getDataElement(conflict))
        } else {
            conflictBuilder
                .displayDescription(conflict.value())
        }

        return conflictBuilder
            .conflict(conflict.value())
            .value(getConflictValue(conflictBuilder))
            .build()
    }

    private suspend fun getConflictValue(conflictBuilder: TrackerImportConflict.Builder): String? {
        val auxConflict = conflictBuilder.build()

        return if (auxConflict.dataElement() != null && auxConflict.event() != null) {
            trackedEntityInstanceDataValueRepository
                .value(auxConflict.event()!!, auxConflict.dataElement()!!)
                .getInternal()?.value()
        } else if (auxConflict.trackedEntityAttribute() != null && auxConflict.trackedEntityInstance() != null) {
            trackedEntityAttributeValueRepository
                .value(auxConflict.trackedEntityAttribute()!!, auxConflict.trackedEntityInstance()!!)
                .getInternal()?.value()
        } else {
            null
        }
    }
}
