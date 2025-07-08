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

package org.hisp.dhis.android.core.user.internal

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.common.DataColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.persistence.dataset.DataSetCompleteRegistrationTableInfo
import org.hisp.dhis.android.persistence.datastore.DataStoreEntryTableInfo
import org.hisp.dhis.android.persistence.datavalue.DataValueTableInfo
import org.hisp.dhis.android.persistence.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.persistence.fileresource.FileResourceTableInfo
import org.hisp.dhis.android.persistence.note.NoteTableInfo
import org.hisp.dhis.android.persistence.relationship.RelationshipTableInfo
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityInstanceTableInfo
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerTableInfo

internal object AccountManagerHelper {

    private val tablesWithSyncState = listOf(
        DataValueTableInfo.TABLE_INFO,
        DataSetCompleteRegistrationTableInfo.TABLE_INFO,
        TrackedEntityInstanceTableInfo.TABLE_INFO,
        EnrollmentTableInfo.TABLE_INFO,
        EventTableInfo.TABLE_INFO,
        FileResourceTableInfo.TABLE_INFO,
        NoteTableInfo.TABLE_INFO,
        ProgramOwnerTableInfo.TABLE_INFO,
        RelationshipTableInfo.TABLE_INFO,
        DataStoreEntryTableInfo.TABLE_INFO,
    )

    private val syncStateQuery =
        tablesWithSyncState.joinToString(" UNION ") { "SELECT ${DataColumns.SYNC_STATE} FROM ${it.name()}" }

    @Suppress("NestedBlockDepth")
    fun getSyncState(adapter: DatabaseAdapter): State {
        val states = mutableSetOf<State>()

        adapter.rawQuery(syncStateQuery).use { cursor ->
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    State.values()
                        .find { it.name == cursor.getString(0) }
                        ?.let { states.add(it) }
                } while (cursor.moveToNext())
            }
        }

        return reduceSyncState(states)
    }

    private fun reduceSyncState(states: Collection<State>): State {
        return when {
            states.contains(State.ERROR) -> State.ERROR
            states.contains(State.WARNING) -> State.WARNING
            states.contains(State.UPLOADING) ||
                states.contains(State.TO_POST) ||
                states.contains(State.TO_UPDATE) -> State.TO_UPDATE
            states.contains(State.SENT_VIA_SMS) -> State.SENT_VIA_SMS
            states.contains(State.SYNCED_VIA_SMS) -> State.SYNCED_VIA_SMS
            else -> State.SYNCED
        }
    }
}
