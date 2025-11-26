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

package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.Query
import org.hisp.dhis.android.persistence.common.daos.ObjectDao
import org.hisp.dhis.android.persistence.event.EventTableInfo
import org.hisp.dhis.android.persistence.program.ProgramStageDataElementTableInfo
import org.hisp.dhis.android.processor.GenerateDaoQueries

@GenerateDaoQueries(tableName = "TrackedEntityDataValueTableInfo.TABLE_NAME")
internal interface TrackedEntityDataValueDaoAux : ObjectDao<TrackedEntityDataValueDB> {

    @Query(
        """UPDATE TrackedEntityDataValue
        SET ${TrackedEntityDataValueTableInfo.Columns.SYNC_STATE} = :state 
        WHERE ${TrackedEntityDataValueTableInfo.Columns.EVENT} = :uid;""",
    )
    fun setSyncStateByEvent(uid: String, state: String)

    @Query(
        """
        DELETE FROM ${TrackedEntityDataValueTableInfo.TABLE_NAME}
        WHERE ${TrackedEntityDataValueTableInfo.Columns.EVENT} = :eventUid
          AND ${TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT} NOT IN (:dataElementUids)
    """,
    )
    fun deleteByEventAndNotInDataElements(
        eventUid: String,
        dataElementUids: List<String>,
    ): Int

    @Query(
        """
        DELETE FROM ${TrackedEntityDataValueTableInfo.TABLE_NAME}
        WHERE ${TrackedEntityDataValueTableInfo.Columns.EVENT} = :eventUid
          AND ${TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT} = :dataElementUid
    """,
    )
    fun deleteByEventAndDataElement(
        eventUid: String,
        dataElementUid: String,
    ): Int

    @Query(
        """
        DELETE FROM ${TrackedEntityDataValueTableInfo.TABLE_NAME}
        WHERE ${TrackedEntityDataValueTableInfo.Columns.EVENT} = :eventUid
    """,
    )
    fun deleteByEvent(eventUid: String): Int

    @Query(
        """
        DELETE FROM ${TrackedEntityDataValueTableInfo.TABLE_NAME}
        WHERE ${TrackedEntityDataValueTableInfo.Columns.EVENT} = :eventUid
          AND ${TrackedEntityDataValueTableInfo.Columns.VALUE} IS NULL 
    """,
    )
    fun removeDeletedDataValuesByEvent(
        eventUid: String,
    ): Int

    @Query(
        """
        DELETE FROM ${TrackedEntityDataValueTableInfo.TABLE_NAME}
        WHERE ${TrackedEntityDataValueTableInfo.Columns.EVENT} = :eventUid
          AND ${TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT} NOT IN (
              SELECT psde.${ProgramStageDataElementTableInfo.Columns.DATA_ELEMENT}
              FROM ${ProgramStageDataElementTableInfo.TABLE_NAME} AS psde
              INNER JOIN ${EventTableInfo.TABLE_NAME} AS e
                ON psde.${ProgramStageDataElementTableInfo.Columns.PROGRAM_STAGE} = 
                e.${EventTableInfo.Columns.PROGRAM_STAGE}
              WHERE e.${EventTableInfo.Columns.UID} = :eventUid
          )
    """,
    )
    fun removeUnassignedDataValuesByEvent(eventUid: String): Int
}
