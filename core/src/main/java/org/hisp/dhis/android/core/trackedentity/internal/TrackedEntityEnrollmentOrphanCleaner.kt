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
package org.hisp.dhis.android.core.trackedentity.internal

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterBreakTheGlassHelper
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackedEntityEnrollmentOrphanCleaner(
    private val enrollmentStore: EnrollmentStore,
    private val breakTheGlassHelper: TrackerImporterBreakTheGlassHelper,
) {

    suspend fun deleteOrphan(
        parent: TrackedEntityInstance?,
        children: Collection<Enrollment>?,
        program: String?,
    ): Boolean {
        return if (parent != null && children != null) {
            val orphanEnrollmentsClause = WhereClauseBuilder().run {
                appendKeyStringValue(EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE, parent.uid())
                appendNotInKeyStringValues(EnrollmentTableInfo.Columns.UID, children.map { it.uid() })
                appendInKeyEnumValues(
                    EnrollmentTableInfo.Columns.SYNC_STATE,
                    listOf(State.SYNCED, State.SYNCED_VIA_SMS),
                )
                if (program != null) {
                    appendKeyStringValue(EnrollmentTableInfo.Columns.PROGRAM, program)
                }
                build()
            }

            val orphanEnrollments = enrollmentStore.selectWhere(orphanEnrollmentsClause)

            val deletedEnrollments = orphanEnrollments.filter { e -> isAccessibleByGlass(e, program) }

            if (deletedEnrollments.isNotEmpty()) {
                enrollmentStore.deleteByUid(deletedEnrollments)
            }

            deletedEnrollments.isNotEmpty()
        } else {
            false
        }
    }

    private suspend fun isAccessibleByGlass(enrollment: Enrollment, program: String?): Boolean {
        val isProtected = breakTheGlassHelper.isProtectedInSearchScope(
            program = enrollment.program(),
            organisationUnit = enrollment.organisationUnit(),
        )

        return !isProtected || enrollment.program() == program
    }
}
