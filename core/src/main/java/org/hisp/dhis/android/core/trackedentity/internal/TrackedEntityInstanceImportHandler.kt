/*
 *  Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import java.util.*
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreUtils.getSyncState
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentImportHandler
import org.hisp.dhis.android.core.imports.TrackerImportConflict
import org.hisp.dhis.android.core.imports.internal.TEIImportSummary
import org.hisp.dhis.android.core.imports.internal.TEIWebResponseHandlerSummary
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictParser
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo

@Reusable
internal class TrackedEntityInstanceImportHandler @Inject internal constructor(
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore,
    private val enrollmentImportHandler: EnrollmentImportHandler,
    private val trackerImportConflictStore: TrackerImportConflictStore,
    private val trackerImportConflictParser: TrackerImportConflictParser,
    private val relationshipStore: RelationshipStore,
    private val dataStatePropagator: DataStatePropagator,
    private val relationshipDHISVersionManager: RelationshipDHISVersionManager,
    private val relationshipRepository: RelationshipCollectionRepository,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore
) {

    private val alreadyDeletedInServerRegex =
        Regex("Tracked entity instance (\\w{11}) cannot be deleted as it is not present in the system")

    @Suppress("NestedBlockDepth")
    fun handleTrackedEntityInstanceImportSummaries(
        teiImportSummaries: List<TEIImportSummary?>?,
        instances: List<TrackedEntityInstance>
    ): TEIWebResponseHandlerSummary {
        val summary = TEIWebResponseHandlerSummary()
        val processedTeis = mutableListOf<String>()

        teiImportSummaries?.filterNotNull()?.forEach { teiImportSummary ->
            val teiUid = teiImportSummary.reference() ?: checkAlreadyDeletedInServer(teiImportSummary)

            if (teiUid != null) {
                processedTeis.add(teiUid)

                val instance = instances.find { it.uid() == teiUid }
                val state = getSyncState(teiImportSummary.status())
                trackerImportConflictStore.deleteTrackedEntityConflicts(teiUid)

                val handleAction = trackedEntityInstanceStore.setSyncStateOrDelete(teiUid, state)

                if (state == State.ERROR || state == State.WARNING) {
                    resetNestedDataStates(instance)
                    instance?.let { summary.teis.error.add(it) }
                } else {
                    setRelationshipsState(teiUid, State.SYNCED)
                    instance?.let { summary.teis.success.add(it) }
                }

                if (handleAction !== HandleAction.Delete) {
                    storeTEIImportConflicts(teiImportSummary)
                    val enSummary = handleEnrollmentImportSummaries(teiImportSummary, instances, state)
                    summary.add(enSummary)
                    dataStatePropagator.refreshTrackedEntityInstanceAggregatedSyncState(teiUid)
                }

                if (state == State.SYNCED &&
                    (handleAction == HandleAction.Update || handleAction == HandleAction.Insert)
                ) {
                    trackedEntityAttributeValueStore.removeDeletedAttributeValuesByInstance(teiUid)
                }
            }
        }

        val ignoredTeis = processIgnoredTEIs(processedTeis, instances)

        summary.teis.ignored.addAll(ignoredTeis)

        return summary
    }

    private fun handleEnrollmentImportSummaries(
        teiImportSummary: TEIImportSummary,
        instances: List<TrackedEntityInstance>,
        teiState: State
    ): TEIWebResponseHandlerSummary {
        return teiImportSummary.enrollments()?.importSummaries()?.let { importSummaries ->
            val teiUid = teiImportSummary.reference()
            enrollmentImportHandler.handleEnrollmentImportSummary(
                importSummaries,
                getEnrollments(teiUid, instances),
                teiState
            )
        } ?: TEIWebResponseHandlerSummary()
    }

    private fun storeTEIImportConflicts(teiImportSummary: TEIImportSummary) {
        val trackerImportConflicts: MutableList<TrackerImportConflict> = ArrayList()
        if (teiImportSummary.description() != null) {
            trackerImportConflicts.add(
                getConflictBuilder(teiImportSummary)
                    .conflict(teiImportSummary.description())
                    .displayDescription(teiImportSummary.description())
                    .value(teiImportSummary.reference())
                    .build()
            )
        }
        teiImportSummary.conflicts()?.forEach { importConflict ->
            trackerImportConflicts.add(
                trackerImportConflictParser
                    .getTrackedEntityInstanceConflict(importConflict, getConflictBuilder(teiImportSummary))
            )
        }

        trackerImportConflicts.forEach { trackerImportConflictStore.insert(it) }
    }

    // Legacy code for <= 2.29
    private fun setRelationshipsState(trackedEntityInstanceUid: String?, state: State) {
        val dbRelationships =
            relationshipRepository.getByItem(RelationshipHelper.teiItem(trackedEntityInstanceUid), true, false)
        val ownedRelationships = relationshipDHISVersionManager
            .getOwnedRelationships(dbRelationships, trackedEntityInstanceUid)
        for (relationship in ownedRelationships) {
            relationshipStore.setSyncStateOrDelete(relationship.uid()!!, state)
        }
    }

    private fun processIgnoredTEIs(
        processedTEIs: List<String>,
        instances: List<TrackedEntityInstance>
    ): List<TrackedEntityInstance> {
        return instances.filterNot { processedTEIs.contains(it.uid()) }.onEach { instance ->
            trackerImportConflictStore.deleteTrackedEntityConflicts(instance.uid())
            trackedEntityInstanceStore.setSyncStateOrDelete(instance.uid(), State.TO_UPDATE)
            resetNestedDataStates(instance)
        }
    }

    private fun resetNestedDataStates(instance: TrackedEntityInstance?) {
        instance?.let {
            dataStatePropagator.resetUploadingEnrollmentAndEventStates(instance.uid())
            setRelationshipsState(instance.uid(), State.TO_UPDATE)
        }
    }

    private fun getEnrollments(
        trackedEntityInstanceUid: String?,
        instances: List<TrackedEntityInstance>
    ): List<Enrollment> {
        return instances.find { it.uid() == trackedEntityInstanceUid }?.let {
            TrackedEntityInstanceInternalAccessor.accessEnrollments(it)
        } ?: listOf()
    }

    private fun checkAlreadyDeletedInServer(summary: TEIImportSummary): String? {
        val teiUid = summary.description()?.let {
            alreadyDeletedInServerRegex.find(it)?.groupValues?.get(1)
        }

        return if (teiUid != null && trackedEntityInstanceStore.selectByUid(teiUid)?.deleted() == true) {
            teiUid
        } else {
            null
        }
    }

    private fun getConflictBuilder(teiImportSummary: TEIImportSummary): TrackerImportConflict.Builder {
        return TrackerImportConflict.builder()
            .trackedEntityInstance(teiImportSummary.reference())
            .tableReference(TrackedEntityInstanceTableInfo.TABLE_INFO.name())
            .status(teiImportSummary.status())
            .created(Date())
    }
}
