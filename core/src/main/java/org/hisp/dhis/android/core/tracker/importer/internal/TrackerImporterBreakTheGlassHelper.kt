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
package org.hisp.dhis.android.core.tracker.importer.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.imports.TrackerImportConflictTableInfo
import org.hisp.dhis.android.core.imports.internal.TEIWebResponseHandlerSummary
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.program.AccessLevel
import org.hisp.dhis.android.core.program.internal.ProgramStoreInterface
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterPayload
import org.hisp.dhis.android.core.trackedentity.ownership.OwnershipManagerImpl
import org.hisp.dhis.android.core.user.internal.UserOrganisationUnitLinkStore

@Reusable
internal class TrackerImporterBreakTheGlassHelper @Inject constructor(
    private val conflictStore: TrackerImportConflictStore,
    private val userOrganisationUnitLinkStore: UserOrganisationUnitLinkStore,
    private val enrollmentStore: EnrollmentStore,
    private val programStore: ProgramStoreInterface,
    private val ownershipManagerImpl: OwnershipManagerImpl
) {

    /**
     * Get glass errors for importer V1
     */
    fun getGlassErrors(
        summary: TEIWebResponseHandlerSummary,
        instances: List<TrackedEntityInstance>
    ): List<TrackedEntityInstance> {
        return summary.enrollments.ignored.filter { enrollment ->
            isProtectedInSearchScope(enrollment.program(), enrollment.organisationUnit())
        }.mapNotNull { enrollment ->
            instances.mapNotNull { tei ->
                val teiEnrollment =
                    TrackedEntityInstanceInternalAccessor.accessEnrollments(tei).find { it.uid() == enrollment.uid() }

                if (teiEnrollment != null) {
                    TrackedEntityInstanceInternalAccessor
                        .insertEnrollments(tei.toBuilder(), listOf(teiEnrollment))
                        .build()
                } else {
                    null
                }
            }.firstOrNull()
        }
    }

    /**
     * Get glass errors for importer V2
     */
    fun getGlassErrors(payload: NewTrackerImporterPayload): NewTrackerImporterPayload {
        val importedEnrollments = payload.enrollments.map { it.uid() }
        val enrollmentWhereClause = WhereClauseBuilder()
            .appendKeyStringValue(TrackerImportConflictTableInfo.Columns.ERROR_CODE, ImporterError.E1102.name)
            .appendInKeyStringValues(TrackerImportConflictTableInfo.Columns.ENROLLMENT, importedEnrollments)
            .build()

        val importerEvents = payload.events.map { it.uid() }
        val eventWhereClause = WhereClauseBuilder()
            .appendKeyStringValue(TrackerImportConflictTableInfo.Columns.ERROR_CODE, ImporterError.E1000.name)
            .appendInKeyStringValues(TrackerImportConflictTableInfo.Columns.EVENT, importerEvents)
            .build()

        val candidateEnrollments = conflictStore.selectWhere(enrollmentWhereClause)
        val candidateEvents = conflictStore.selectWhere(eventWhereClause)

        val glassErrors = NewTrackerImporterPayload()
        candidateEnrollments
            .mapNotNull { error -> error.enrollment()?.let { id -> payload.enrollments.find { it.uid() == id } } }
            .filter { enrollment ->
                isProtectedInSearchScope(enrollment.program(), enrollment.organisationUnit())
            }
            .map { enrollment ->
                glassErrors.enrollments.add(enrollment)
                glassErrors.trackedEntities.addAll(
                    payload.trackedEntities.filter {
                        it.uid() == enrollment.trackedEntity()
                    }
                )
                glassErrors.events.addAll(
                    payload.events.filter {
                        it.enrollment() == enrollment.uid()
                    }
                )
            }

        candidateEvents
            .filter { error -> glassErrors.events.none { it.uid() == error.event() } }
            .mapNotNull { error -> error.event()?.let { id -> payload.events.find { it.uid() == id } } }
            .filter { event ->
                event.enrollment()?.let { enrollmentStore.selectByUid(it) }?.let {
                    isProtectedInSearchScope(it.program(), it.organisationUnit())
                } ?: false
            }
            .map { event ->
                glassErrors.events.add(event)
            }

        return glassErrors
    }

    /**
     * Fake break the glass for importer V1
     */
    fun fakeBreakGlass(instances: List<TrackedEntityInstance>) {
        instances.forEach { instance ->
            TrackedEntityInstanceInternalAccessor.accessEnrollments(instance).forEach { enrollment ->
                if (instance.uid() != null && enrollment.program() != null)
                    ownershipManagerImpl.fakeBreakGlass(instance.uid()!!, enrollment.program()!!)
            }
        }
    }

    /**
     * Fake break the glass for importer V2
     */
    fun fakeBreakGlass(payload: NewTrackerImporterPayload) {
        payload.enrollments.forEach {
            if (it.trackedEntity() != null && it.program() != null) {
                ownershipManagerImpl.fakeBreakGlass(it.trackedEntity()!!, it.program()!!)
            }
        }

        payload.events
            .filter { event -> payload.enrollments.none { it.uid() == event.enrollment() } }
            .forEach { event ->
                event.enrollment()?.let { enrollmentStore.selectByUid(it) }?.let {
                    if (it.trackedEntityInstance() != null && it.program() != null) {
                        ownershipManagerImpl.fakeBreakGlass(it.trackedEntityInstance()!!, it.program()!!)
                    }
                }
            }
    }

    fun isProtectedInSearchScope(program: String?, organisationUnit: String?): Boolean {
        return if (program != null && organisationUnit != null) {
            isProtectedProgram(program) && isNotCaptureScope(organisationUnit)
        } else {
            false
        }
    }

    private fun isProtectedProgram(program: String?): Boolean {
        return program?.let { programStore.selectByUid(it)?.accessLevel() == AccessLevel.PROTECTED } ?: false
    }

    private fun isNotCaptureScope(organisationUnit: String?): Boolean {
        return organisationUnit?.let { !userOrganisationUnitLinkStore.isCaptureScope(it) } ?: false
    }
}
