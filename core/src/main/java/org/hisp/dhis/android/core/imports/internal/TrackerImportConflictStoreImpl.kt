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

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStoreImpl
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.event.EventTableInfo
import org.hisp.dhis.android.core.imports.TrackerImportConflict
import org.hisp.dhis.android.core.imports.TrackerImportConflictTableInfo
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackerImportConflictStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : TrackerImportConflictStore,
    ObjectStoreImpl<TrackerImportConflict>(
        databaseAdapter,
        TrackerImportConflictTableInfo.TABLE_INFO,
        BINDER,
        { cursor: Cursor -> TrackerImportConflict.create(cursor) },
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

    companion object {
        private val BINDER = StatementBinder { o: TrackerImportConflict, w: StatementWrapper ->
            w.bind(1, o.conflict())
            w.bind(2, o.value())
            w.bind(3, o.trackedEntityInstance())
            w.bind(4, o.enrollment())
            w.bind(5, o.event())
            w.bind(6, o.trackedEntityAttribute())
            w.bind(7, o.dataElement())
            w.bind(8, o.tableReference())
            w.bind(9, o.errorCode())
            w.bind(10, o.status())
            w.bind(11, o.created())
            w.bind(12, o.displayDescription())
        }
    }
}
