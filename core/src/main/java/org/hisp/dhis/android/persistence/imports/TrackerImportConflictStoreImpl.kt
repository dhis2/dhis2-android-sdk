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

package org.hisp.dhis.android.persistence.imports

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.imports.TrackerImportConflict
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.stores.ObjectStoreImpl
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.persistence.event.EventTableInfo

internal class TrackerImportConflictStoreImpl(
    val dao: TrackerImportConflictDao,
) : TrackerImportConflictStore, ObjectStoreImpl<TrackerImportConflict, TrackerImportConflictDB>(
    dao,
    TrackerImportConflict::toDB,
    SQLStatementBuilderImpl(TrackerImportConflictTableInfo.TABLE_INFO),
) {
    override suspend fun deleteEventConflicts(eventUid: String) {
        deleteTypeConflicts(
            TrackerImportConflictTableInfo.Columns.EVENT,
            EventTableInfo.TABLE_INFO,
            eventUid,
        )
    }

    override suspend fun deleteEnrollmentConflicts(enrollmentUid: String) {
        deleteTypeConflicts(
            TrackerImportConflictTableInfo.Columns.ENROLLMENT,
            EnrollmentTableInfo.TABLE_INFO,
            enrollmentUid,
        )
    }

    override suspend fun deleteTrackedEntityConflicts(tackedEntityUid: String) {
        deleteTypeConflicts(
            TrackerImportConflictTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
            TrackedEntityInstanceTableInfo.TABLE_INFO,
            tackedEntityUid,
        )
    }

    private suspend fun deleteTypeConflicts(column: String, tableInfo: TableInfo, uid: String) {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(column, uid)
            .appendKeyStringValue(
                TrackerImportConflictTableInfo.Columns.TABLE_REFERENCE,
                tableInfo.name(),
            )
            .build()
        deleteWhereIfExists(whereClause)
    }
}
