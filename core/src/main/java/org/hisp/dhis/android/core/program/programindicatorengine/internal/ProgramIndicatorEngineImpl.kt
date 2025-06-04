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
package org.hisp.dhis.android.core.program.programindicatorengine.internal

import org.hisp.dhis.android.core.arch.helpers.UidsHelper.mapByUid
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.constant.internal.ConstantStore
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventCollectionRepository
import org.hisp.dhis.android.core.program.ProgramStageCollectionRepository
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorStore
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.program.programindicatorengine.ProgramIndicatorEngine
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.koin.core.annotation.Singleton

@Singleton
internal class ProgramIndicatorEngineImpl(
    private val programIndicatorStore: ProgramIndicatorStore,
    private val dataElementStore: DataElementStore,
    private val trackedEntityAttributeStore: TrackedEntityAttributeStore,
    private val enrollmentStore: EnrollmentStore,
    private val eventRepository: EventCollectionRepository,
    private val programRepository: ProgramStageCollectionRepository,
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore,
    private val constantStore: ConstantStore,
    private val programStageStore: ProgramStageStore,
) : ProgramIndicatorEngine {

    @Deprecated("Deprecated in Java")
    override suspend fun getProgramIndicatorValue(
        enrollmentUid: String?,
        eventUid: String?,
        programIndicatorUid: String,
    ): String? {
        return when {
            eventUid != null -> getEventProgramIndicatorValue(eventUid, programIndicatorUid)
            enrollmentUid != null -> getEnrollmentProgramIndicatorValue(enrollmentUid, programIndicatorUid)
            else -> return null
        }
    }

    override suspend fun getEnrollmentProgramIndicatorValue(
        enrollmentUid: String,
        programIndicatorUid: String,
    ): String? {
        val programIndicator = programIndicatorStore.selectByUid(programIndicatorUid) ?: return null

        val enrollment = enrollmentStore.selectByUid(enrollmentUid)
            ?: throw NoSuchElementException("Enrollment $enrollmentUid does not exist.")

        val programIndicatorContext = ProgramIndicatorContext(
            programIndicator = programIndicator,
            attributeValues = getAttributeValues(enrollment),
            enrollment = enrollment,
            events = getEnrollmentEvents(enrollment),
        )

        return evaluateProgramIndicatorContext(programIndicatorContext)
    }

    override suspend fun getEventProgramIndicatorValue(eventUid: String, programIndicatorUid: String): String? {
        val programIndicator = programIndicatorStore.selectByUid(programIndicatorUid) ?: return null

        val event = eventRepository
            .withTrackedEntityDataValues()
            .byDeleted().isFalse
            .uid(eventUid)
            .blockingGet() ?: throw NoSuchElementException("Event $eventUid does not exist or is deleted.")

        val enrollment = event.enrollment()?.let {
            enrollmentStore.selectByUid(it)
        }

        val programIndicatorContext = ProgramIndicatorContext(
            programIndicator = programIndicator,
            attributeValues = getAttributeValues(enrollment),
            enrollment = enrollment,
            events = mapOf(event.programStage()!! to listOf(event)),
        )

        return evaluateProgramIndicatorContext(programIndicatorContext)
    }

    private suspend fun evaluateProgramIndicatorContext(context: ProgramIndicatorContext): String? {
        val executor = ProgramIndicatorExecutor(
            constantMap(),
            context,
            dataElementStore,
            trackedEntityAttributeStore,
            programStageStore,
        )

        return executor.getProgramIndicatorValue(context.programIndicator)
    }

    private suspend fun constantMap(): Map<String, Constant> {
        val constants = constantStore.selectAll()
        return mapByUid(constants)
    }

    private suspend fun getAttributeValues(enrollment: Enrollment?): Map<String, TrackedEntityAttributeValue> {
        val trackedEntityAttributeValues = enrollment?.trackedEntityInstance()?.let { teiUid ->
            trackedEntityAttributeValueStore.queryByTrackedEntityInstance(teiUid)
        } ?: return mapOf()

        return trackedEntityAttributeValues
            .filter { it.trackedEntityAttribute() != null }
            .associateBy { it.trackedEntityAttribute()!! }
    }

    private fun getEnrollmentEvents(enrollment: Enrollment): Map<String, List<Event>> {
        val programStageUids = programRepository.byProgramUid().eq(enrollment.program()).blockingGetUids()

        return programStageUids.associateWith { programStageUid ->
            val programStageEvents = eventRepository
                .byProgramStageUid().eq(programStageUid)
                .byEnrollmentUid().eq(enrollment.uid())
                .byDeleted().isFalse
                .orderByEventDate(RepositoryScope.OrderByDirection.ASC)
                .orderByLastUpdated(RepositoryScope.OrderByDirection.ASC)
                .withTrackedEntityDataValues()
                .blockingGet()

            programStageEvents
        }
    }
}
